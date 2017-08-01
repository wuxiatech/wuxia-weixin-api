/*
* Created on :2015年10月20日
* Author     :Administrator
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.message.bean;

import java.util.Date;

import cn.wuxia.wechat.BasicAccount;

/**
 * 
 * 微信请求报文 
 * Description of the class 
 * @author guwen
 * @ Version : V<Ver.No> <2015年10月20日>
 */
public abstract class Receive {

    //接收时间
    private Date receiveTime;

    //来源
    private String receiveOrigin;

    //来源IP
    private String fromIp;

    /**
     * 开发配置信息
     */
    private BasicAccount account;

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getReceiveOrigin() {
        return receiveOrigin;
    }

    public void setReceiveOrigin(String receiveOrigin) {
        this.receiveOrigin = receiveOrigin;
    }

    public String getFromIp() {
        return fromIp;
    }

    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }

    public BasicAccount getAccount() {
        return account;
    }

    public void setAccount(BasicAccount account) {
        this.account = account;
    }

}
