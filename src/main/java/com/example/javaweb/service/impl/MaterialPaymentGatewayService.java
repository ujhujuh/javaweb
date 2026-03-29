package com.example.javaweb.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.javaweb.common.exception.BusinessException;
import com.example.javaweb.config.PaymentProperties;
import com.example.javaweb.entity.UpMaterialOrder;
import com.example.javaweb.vo.material.MaterialPayInitVO;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class MaterialPaymentGatewayService {

    private static final String PAY_WECHAT = "WECHAT";
    private static final String PAY_ALIPAY = "ALIPAY";

    @Autowired
    private PaymentProperties paymentProperties;

    public MaterialPayInitVO initiatePay(UpMaterialOrder order, String payType) {
        if (PAY_ALIPAY.equals(payType)) {
            return initiateAlipay(order);
        }
        if (PAY_WECHAT.equals(payType)) {
            return initiateWechat(order);
        }
        throw new BusinessException("不支持的支付方式");
    }

    public AlipayNotifyResult parseAlipayNotify(Map<String, String> params) {
        try {
            PaymentProperties.Alipay config = paymentProperties.getAlipay();
            if (!Boolean.TRUE.equals(config.getEnabled())) {
                throw new BusinessException("支付宝支付未启用");
            }
            boolean verified = AlipaySignature.rsaCheckV1(
                    params,
                    config.getAlipayPublicKey(),
                    "UTF-8",
                    "RSA2"
            );
            if (!verified) {
                throw new BusinessException("支付宝回调验签失败");
            }
            String outTradeNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String status = params.get("trade_status");
            String totalAmount = params.get("total_amount");
            String appId = params.get("app_id");
            if (config.getAppId() != null && !config.getAppId().equals(appId)) {
                throw new BusinessException("支付宝回调应用ID不匹配");
            }
            boolean success = "TRADE_SUCCESS".equals(status) || "TRADE_FINISHED".equals(status);
            return new AlipayNotifyResult(outTradeNo, tradeNo, success, totalAmount);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("支付宝回调处理失败: " + e.getMessage());
        }
    }

    public WechatNotifyResult parseWechatNotify(String timestamp,
                                                String nonce,
                                                String signature,
                                                String serial,
                                                String requestBody) {
        try {
            PaymentProperties.Wechat config = paymentProperties.getWechat();
            if (!Boolean.TRUE.equals(config.getEnabled())) {
                throw new BusinessException("微信支付未启用");
            }
            RSAAutoCertificateConfig wxConfig = buildWechatConfig(config);
            NotificationParser parser = new NotificationParser(wxConfig);
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(serial)
                    .nonce(nonce)
                    .signature(signature)
                    .timestamp(timestamp)
                    .body(requestBody)
                    .build();
            Transaction transaction = parser.parse(requestParam, Transaction.class);
            String outTradeNo = transaction.getOutTradeNo();
            String transactionId = transaction.getTransactionId();
            String tradeState = transaction.getTradeState() == null ? null : transaction.getTradeState().toString();
            boolean success = "SUCCESS".equalsIgnoreCase(tradeState);
            return new WechatNotifyResult(outTradeNo, transactionId, success);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("微信回调处理失败: " + e.getMessage());
        }
    }

    private MaterialPayInitVO initiateAlipay(UpMaterialOrder order) {
        try {
            PaymentProperties.Alipay config = paymentProperties.getAlipay();
            if (!Boolean.TRUE.equals(config.getEnabled())) {
                throw new BusinessException("支付宝支付未启用");
            }
            if (isBlank(config.getAppId()) || isBlank(config.getAppPrivateKey()) || isBlank(config.getAlipayPublicKey())) {
                throw new BusinessException("支付宝支付参数未配置完整");
            }

            AlipayClient alipayClient = new DefaultAlipayClient(
                    config.getGateway(),
                    config.getAppId(),
                    config.getAppPrivateKey(),
                    "json",
                    "UTF-8",
                    config.getAlipayPublicKey(),
                    "RSA2"
            );

            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl(config.getNotifyUrl());
            request.setReturnUrl(config.getReturnUrl());

            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(order.getOrderNo());
            model.setTotalAmount(order.getPayAmount().setScale(2, RoundingMode.HALF_UP).toPlainString());
            model.setSubject(order.getMaterialTitle());
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            model.setTimeoutExpress("30m");
            request.setBizModel(model);

            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (!response.isSuccess()) {
                throw new BusinessException("支付宝下单失败: " + response.getSubMsg());
            }

            MaterialPayInitVO vo = new MaterialPayInitVO();
            vo.setOrderNo(order.getOrderNo());
            vo.setPayType(PAY_ALIPAY);
            vo.setFormHtml(response.getBody());
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("支付宝下单失败: " + e.getMessage());
        }
    }

    private MaterialPayInitVO initiateWechat(UpMaterialOrder order) {
        try {
            PaymentProperties.Wechat config = paymentProperties.getWechat();
            if (!Boolean.TRUE.equals(config.getEnabled())) {
                throw new BusinessException("微信支付未启用");
            }
            RSAAutoCertificateConfig wxConfig = buildWechatConfig(config);
            NativePayService service = new NativePayService.Builder().config(wxConfig).build();

            PrepayRequest request = new PrepayRequest();
            request.setAppid(config.getAppId());
            request.setMchid(config.getMchId());
            request.setDescription(order.getMaterialTitle());
            request.setOutTradeNo(order.getOrderNo());
            request.setNotifyUrl(config.getNotifyUrl());

            Amount amount = new Amount();
            amount.setTotal(toFen(order.getPayAmount()));
            request.setAmount(amount);

            PrepayResponse response = service.prepay(request);
            MaterialPayInitVO vo = new MaterialPayInitVO();
            vo.setOrderNo(order.getOrderNo());
            vo.setPayType(PAY_WECHAT);
            vo.setCodeUrl(response.getCodeUrl());
            vo.setPayUrl(response.getCodeUrl());
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("微信下单失败: " + e.getMessage());
        }
    }

    private RSAAutoCertificateConfig buildWechatConfig(PaymentProperties.Wechat config) {
        if (isBlank(config.getAppId())
                || isBlank(config.getMchId())
                || isBlank(config.getMchSerialNo())
                || isBlank(config.getApiV3Key())
                || isBlank(config.getNotifyUrl())) {
            throw new BusinessException("微信支付参数未配置完整");
        }
        if (isBlank(config.getPrivateKeyPath())) {
            throw new BusinessException("微信支付私钥文件路径未配置");
        }
        return new RSAAutoCertificateConfig.Builder()
                .merchantId(config.getMchId())
                .privateKeyFromPath(config.getPrivateKeyPath())
                .merchantSerialNumber(config.getMchSerialNo())
                .apiV3Key(config.getApiV3Key())
                .build();
    }

    private int toFen(BigDecimal yuan) {
        BigDecimal amount = yuan == null ? BigDecimal.ZERO : yuan;
        return amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static class AlipayNotifyResult {
        private final String outTradeNo;
        private final String tradeNo;
        private final boolean success;
        private final String totalAmount;

        public AlipayNotifyResult(String outTradeNo, String tradeNo, boolean success, String totalAmount) {
            this.outTradeNo = outTradeNo;
            this.tradeNo = tradeNo;
            this.success = success;
            this.totalAmount = totalAmount;
        }

        public String getOutTradeNo() {
            return outTradeNo;
        }

        public String getTradeNo() {
            return tradeNo;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getTotalAmount() {
            return totalAmount;
        }
    }

    public static class WechatNotifyResult {
        private final String outTradeNo;
        private final String transactionId;
        private final boolean success;

        public WechatNotifyResult(String outTradeNo, String transactionId, boolean success) {
            this.outTradeNo = outTradeNo;
            this.transactionId = transactionId;
            this.success = success;
        }

        public String getOutTradeNo() {
            return outTradeNo;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
