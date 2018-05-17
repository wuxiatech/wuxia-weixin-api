package cn.wuxia.wechat.pay.bean;

public class SendRedPackResult {
    //    商户订单号
    String mch_billno;//	是	10000098201411111234567890	String(28)
    //    商户订单号（每个订单号必须唯一）
    //
    //    组成： mch_id+yyyymmdd+10位一天内不能重复的数字
    //
    //    商户号

    String mch_id;//	是	10000098	String(32)	微信支付分配的商户号
    //    公众账号appid

    String wxappid;//	是	wx8888888888888888	String(32)	微信分配的公众账号ID（企业号corpid即为此appId）
    //    用户openid

    String re_openid;//	是	oxTWIuGaIt6gTKsQRLau2M0yL16E	String(32)
    //    接受收红包的用户
    //
    //            用户在wxappid下的openid
    //
    //    总金额

    int total_amount;//	是	1000	int	付款总金额，单位分
    //    微信单号

    String send_listid;//	是	100000000020150520314766074200	String(32)	微信红包订单号

    public String getMch_billno() {
        return mch_billno;
    }

    public void setMch_billno(String mch_billno) {
        this.mch_billno = mch_billno;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getWxappid() {
        return wxappid;
    }

    public void setWxappid(String wxappid) {
        this.wxappid = wxappid;
    }

    public String getRe_openid() {
        return re_openid;
    }

    public void setRe_openid(String re_openid) {
        this.re_openid = re_openid;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public String getSend_listid() {
        return send_listid;
    }

    public void setSend_listid(String send_listid) {
        this.send_listid = send_listid;
    }
}
