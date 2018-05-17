package cn.wuxia.wechat.pay.bean;

public class TransferToUserResult {
    //    商户订单号
    String partner_trade_no;//	是	1217752501201407033233368018	String(32)	商户订单号，需保持历史全局唯一性(只能是字母或者数字，不能包含有符号)
    //    微信订单号

    String payment_no;//	是	1007752501201407033233368018	String	企业付款成功，返回的微信订单号
    //    微信支付成功时间

    String payment_time;//	是	2015-05-19 15：26：59	String	企业付款成功时间

    public String getPartner_trade_no() {
        return partner_trade_no;
    }

    public void setPartner_trade_no(String partner_trade_no) {
        this.partner_trade_no = partner_trade_no;
    }

    public String getPayment_no() {
        return payment_no;
    }

    public void setPayment_no(String payment_no) {
        this.payment_no = payment_no;
    }

    public String getPayment_time() {
        return payment_time;
    }

    public void setPayment_time(String payment_time) {
        this.payment_time = payment_time;
    }
}
