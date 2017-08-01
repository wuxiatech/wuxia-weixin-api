/*
* Created on :2017年4月7日
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 wuxia.gd.cn All right reserved.
*/
package cn.wuxia.wechat.pay.bean;

import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;

import cn.wuxia.common.entity.ValidationEntity;

public class PayParamBean extends ValidationEntity {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    @NotBlank
    String out_trade_no;

    @NotBlank
    String body;

    /**
     * 订单总金额，单位为分
     */
    @NotBlank
    String total_fee;

    /**
     * APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
     */
    @NotBlank
    String spbill_create_ip;

    /**
     * 交易起始时间
     */
    Date time_start;

    /**
     * 交易结束时间
     */
    Date time_expire;

    @NotBlank
    TradeType trade_type;

    /**
     * trade_type=NATIVE时（即扫码支付），此参数必传
     */
    String product_id;

    /**
     * trade_type=JSAPI时（即公众号支付），此参数必传
     */
    String openid;

    String fee_type = "CNY";

    String device_info = "WEB";

    @NotBlank
    String notify_url;

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        if (null != spbill_create_ip) {
            String[] ips = spbill_create_ip.split(",");
            spbill_create_ip = ips[0];
        }
        this.spbill_create_ip = spbill_create_ip;
    }

    public Date getTime_start() {
        return time_start;
    }

    public void setTime_start(Date time_start) {
        this.time_start = time_start;
    }

    public Date getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(Date time_expire) {
        this.time_expire = time_expire;
    }

    public TradeType getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(TradeType trade_type) {
        this.trade_type = trade_type;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public enum TradeType {
        JSAPI, NATIVE, APP;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

}
