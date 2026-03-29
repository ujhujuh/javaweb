package com.example.javaweb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private Alipay alipay = new Alipay();
    private Wechat wechat = new Wechat();

    @Data
    public static class Alipay {
        private Boolean enabled = false;
        private String gateway = "https://openapi.alipay.com/gateway.do";
        private String appId;
        private String appPrivateKey;
        private String alipayPublicKey;
        private String notifyUrl;
        private String returnUrl;
    }

    @Data
    public static class Wechat {
        private Boolean enabled = false;
        private String appId;
        private String mchId;
        private String mchSerialNo;
        private String privateKeyPath;
        private String privateKeyPem;
        private String apiV3Key;
        private String notifyUrl;
    }
}
