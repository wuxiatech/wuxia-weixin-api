/*
* Created on :23 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.pay.bean;

/**
 * 
 * [ticket id]
 * 开发者支付配置数据
 * @author songlin
 * @ Version : V<Ver.No> <23 Apr, 2015>
 */
public class PayConfigBean {

    String partner;

    String appKey;

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

}
