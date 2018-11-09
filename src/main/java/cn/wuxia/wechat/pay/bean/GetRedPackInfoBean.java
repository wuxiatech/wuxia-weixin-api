package cn.wuxia.wechat.pay.bean;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public class GetRedPackInfoBean {
    //    随机字符串
    @NotBlank
    String nonce_str; //	是	5K8264ILTKCH16CQ2502SI8ZNMTM67VS	String(32)	随机字符串，不长于32位
    //    签名

    @NotBlank
    String sign;//	是	C380BEC2BFD727A4B6845133519F3AD6	String(32)	详见签名生成算法
    //    商户订单号

    @NotBlank
    String mch_billno; //是	10000098201411111234567890	String(28)
    //    商户订单号（每个订单号必须唯一。取值范围：0~9，a~z，A~Z）
    //
    //    接口根据商户订单号支持重入，如出现超时可再调用。
    //
    //    商户号

    @NotBlank
    String mch_id;//	是	10000098	String(32)	微信支付分配的商户号
    //    公众账号appid

    @NotBlank
    String appid;//	是	wx8888888888888888	String(32)	微信分配的公众账号ID（企业号corpid即为此appId）。接口传入的所有appid应该为公众号的appid（在mp.weixin.qq.com申请的），不能为APP的appid（在open.weixin.qq.com申请的）。
    //    商户名称

    @NotBlank
    String bill_type;//	是	MCHT:通过商户订单号获取红包信息
    //    用户openid


    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

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


    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }
}
