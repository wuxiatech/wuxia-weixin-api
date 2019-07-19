package cn.wuxia.wechat.pay.enums;

public enum PayTradeType {
    /**
     * 微信内公众号或小程序支付
     */
    JSAPI,
    /**
     *
     */
    APP,
    /**
     * 生成付款二维码，对方扫码支付
     */
    NATIVE,
    /**
     * H5, 网页拉起支付
     */
    MWEB;
}
