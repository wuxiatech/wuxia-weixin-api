/*
* Created on :12 Oct, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.shakearound.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.Assert;

public class PackBean {

    private String mch_billno;

    private String mch_id;

    private String wxappid;

    private String send_name;

    private String re_openid;

    //红包发放总金额，即一组红包金额总和，包括分享者的红包和裂变的红包，单位分
    private Integer total_amount;

    //红包发放总人数，即总共有多少人可以领到该组红包（包括分享者

    private Integer total_num;

    //全部随机,商户指定总金额和红包发放总人数，由微信支付随机计算出各红包金额
    private String amt_type;

    //红包祝福语 

    private String wishing;

    //活动名称 

    private String act_name;

    private String remark;

    //随机字符串，不长于32位 
    private String nonce_str = RandomStringUtils.randomAlphanumeric(32);

    private String client_ip;

    private String sign;

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

    public String getSend_name() {
        return send_name;
    }

    public void setSend_name(String send_name) {
        //发送红包名称
        Assert.isTrue(send_name.length() < 32);
        this.send_name = send_name;
    }

    public String getRe_openid() {
        return re_openid;
    }

    public void setRe_openid(String re_openid) {
        this.re_openid = re_openid;
    }

    public Integer getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Integer total_amount) {
        this.total_amount = total_amount;
    }

    public Integer getTotal_num() {
        return total_num;
    }

    public void setTotal_num(Integer total_num) {
        this.total_num = total_num;
    }

    public String getAmt_type() {
        return amt_type;
    }

    public void setAmt_type(String amt_type) {
        this.amt_type = amt_type;
    }

    public String getWishing() {
        return wishing;
    }

    public void setWishing(String wishing) {
        Assert.isTrue(wishing.length() < 128);
        this.wishing = wishing;
    }

    public String getAct_name() {
        return act_name;
    }

    public void setAct_name(String act_name) {
        Assert.isTrue(act_name.length() < 32);
        this.act_name = act_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        Assert.isTrue(remark.length() < 256);
        this.remark = remark;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);
                if (obj != null) {
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
