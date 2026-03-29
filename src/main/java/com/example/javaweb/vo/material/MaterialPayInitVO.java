package com.example.javaweb.vo.material;

import lombok.Data;

@Data
public class MaterialPayInitVO {
    private String orderNo;
    private String payType;
    /**
     * 支付宝网页支付表单（自动提交）
     */
    private String formHtml;
    /**
     * 跳转支付链接（若第三方返回链接）
     */
    private String payUrl;
    /**
     * 微信 native 支付二维码原始内容
     */
    private String codeUrl;
}
