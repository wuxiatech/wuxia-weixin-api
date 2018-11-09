package cn.wuxia.wechat.pay.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.*;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class GetRedPackInfoResult extends ResultCodeBean{
    //    商户订单号
    String mch_billno;//	是	10000098201411111234567890	String(28)
    //    商户订单号（每个订单号必须唯一）
    //
    //    组成： mch_id+yyyymmdd+10位一天内不能重复的数字
    //
    //    商户号

    String mch_id;//	是	10000098	String(32)	微信支付分配的商户号

    //    红包单号
    String detail_id; // 是  1000000000201503283103439304	String(32)	使用API发放现金红包时返回的红包单号

    //红包状态
    Status status;//	是	RECEIVED	string(16)

    public enum Status {
        SENDING("发放中"),
        SENT("已发放待领取"),
        FAILED("发放失败"),
        RECEIVED("已领取"),
        RFUND_ING("退款中"),
        REFUND("已退款");
        private String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    //发放类型
    String send_type;//	是	API	String(32)	API:通过API接口发放,

    public enum SendType {
        UPLOAD("通过上传文件方式发放"),
        ACTIVITY("通过活动方式发放");
        private String displayName;

        SendType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    //红包类型
    HbType hb_type;//	是	GROUP	String(32)

    public enum HbType {

        GROUP("裂变红包"),
        NORMAL("普通红包");
        private String displayName;

        HbType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    //红包个数
    Integer total_num;//	是	1	int	红包个数


    //    红包金额

    Integer total_amount;//	是	1000	int	付款总金额，单位分


    //失败原因
    String reason;//	否	余额不足	String(32)	发送失败原因

    //红包发送时间
    String send_time;//	是	2015-04-21 20:00:00	String(32)


    //红包退款时间
    String refund_time;//	否	2015-04-21 23:03:00	String(32)	红包的退款时间（如果其未领取的退款）

    //红包退款金额
    Integer refund_amount;//	否	8000	Int	红包退款金额


    //祝福语
    String wishing;//	否	新年快乐	String(128)	祝福语

    //活动描述
    String remark;//	否	新年红包	String(256)	活动描述，低版本微信可见


    //活动名称
    String act_name;//	否	新年红包	String(32)	发红包的活动名称


    //裂变红包领取列表


    @XmlElementWrapper(name = "hblist")
    @XmlElement(name = "hbinfo")
    List<Hbinfo> hblist;//	否	内容如下表	 	裂变红包的领取列表


    //@XmlRootElement(name = "hbinfo")
    public static class Hbinfo {


        //    用户openid

        String openid;//	是	oxTWIuGaIt6gTKsQRLau2M0yL16E	String(32)
        //    接受收红包的用户
        //
        //            用户在wxappid下的openid

        //金额
        Integer amount;//	是	100	int	领取金额

        //接收时间
        String rcv_time;//	是	2015-04-21 20:00:00	String(32)	领取红包的时间


        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getRcv_time() {
            return rcv_time;
        }

        public void setRcv_time(String rcv_time) {
            this.rcv_time = rcv_time;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
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

    public String getDetail_id() {
        return detail_id;
    }

    public void setDetail_id(String detail_id) {
        this.detail_id = detail_id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSend_type() {
        return send_type;
    }

    public void setSend_type(String send_type) {
        this.send_type = send_type;
    }

    public HbType getHb_type() {
        return hb_type;
    }

    public void setHb_type(HbType hb_type) {
        this.hb_type = hb_type;
    }

    public Integer getTotal_num() {
        return total_num;
    }

    public void setTotal_num(Integer total_num) {
        this.total_num = total_num;
    }

    public Integer getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Integer total_amount) {
        this.total_amount = total_amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getRefund_time() {
        return refund_time;
    }

    public void setRefund_time(String refund_time) {
        this.refund_time = refund_time;
    }

    public Integer getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(Integer refund_amount) {
        this.refund_amount = refund_amount;
    }

    public String getWishing() {
        return wishing;
    }

    public void setWishing(String wishing) {
        this.wishing = wishing;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAct_name() {
        return act_name;
    }

    public void setAct_name(String act_name) {
        this.act_name = act_name;
    }

    public List<Hbinfo> getHblist() {
        return hblist;
    }

    public void setHblist(List<Hbinfo> hblist) {
        this.hblist = hblist;
    }
}
