package cn.wuxia.wechat.pay.bean;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

public class RedPackBean {

    @Length(max = 28)
    String mch_billno; //是	10000098201411111234567890	String(28)

    @Length(max = 32)
    @NotBlank
    String send_name;//	是	天虹百货	String(32)	红包发送者名称
    //    用户openid

    @NotBlank
    String re_openid;//	是	oxTWIuGaIt6gTKsQRLau2M0yL16E	String(32)
    //    接受红包的用户
    //
    //            用户在wxappid下的openid
    //
    //    付款金额

    @Min(value = 0)
    int total_num;//	是	1	int
    //            红包发放总人数

    @NotNull
    @Digits(integer = 3, fraction = 2)
    double amount;//	是	10	int	付款金额，单位元
    //    红包发放总人数

    //    红包祝福语

    @NotBlank
    @Length(max = 128)
    String wishing;//是	感谢您参加猜灯谜活动，祝您元宵节快乐！	String(128)	红包祝福语

    //    活动名称
    @NotBlank
    @Length(max = 32)
    String act_name;//	是	猜灯谜抢红包活动	String(32)	活动名称
    //    备注

    @NotBlank
    @Length(max = 256)
    String remark;//	是	猜越多得越多，快来抢！	String(256)	备注信息
    //    场景id

    public String getMch_billno() {
        return mch_billno;
    }

    public void setMch_billno(String mch_billno) {
        this.mch_billno = mch_billno;
    }

    public String getSend_name() {
        return send_name;
    }

    public void setSend_name(String send_name) {
        this.send_name = send_name;
    }

    public String getRe_openid() {
        return re_openid;
    }

    public void setRe_openid(String re_openid) {
        this.re_openid = re_openid;
    }

    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getWishing() {
        return wishing;
    }

    public void setWishing(String wishing) {
        this.wishing = wishing;
    }

    public String getAct_name() {
        return act_name;
    }

    public void setAct_name(String act_name) {
        this.act_name = act_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
