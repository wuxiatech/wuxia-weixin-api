package cn.wuxia.wechat.message.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 接收到的微信消息 --支付结果请求
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class PayReceiveMessage extends Receive {

    @XmlElement(name = "appid")
    private String appid;

    @XmlElement(name = "bank_type")
    private String bankType;

    @XmlElement(name = "cash_fee")
    private Integer cashFee;

    @XmlElement(name = "fee_type")
    private String feeType;

    @XmlElement(name = "is_subscribe")
    private String isSubscribe;

    @XmlElement(name = "mch_id")
    private String mchId;

    @XmlElement(name = "nonce_str")
    private String nonceStr;

    @XmlElement(name = "openid")
    private String openid;

    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    @XmlElement(name = "result_code")
    private String resultCode;

    @XmlElement(name = "return_code")
    private String returnCode;

    @XmlElement(name = "sign")
    private String sign;

    @XmlElement(name = "time_end")
    private String timeEnd;

    @XmlElement(name = "total_fee")
    private Integer totalFee;

    @XmlElement(name = "trade_type")
    private String tradeType;

    @XmlElement(name = "transaction_id")
    private String transactionId;

    @XmlElement(name = "return_msg")
    private String returnMsg;

    @XmlElement(name = "err_code")
    private String errCode;

    @XmlElement(name = "err_code_des")
    private String errCodeDes;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public Integer getCashFee() {
        return cashFee;
    }

    public void setCashFee(Integer cashFee) {
        this.cashFee = cashFee;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrCodeDes() {
        return errCodeDes;
    }

    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes;
    }

}
