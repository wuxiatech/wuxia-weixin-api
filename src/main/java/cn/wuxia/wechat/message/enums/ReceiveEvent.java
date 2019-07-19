/*
* Created on :2015年7月15日
* Author     :Administrator
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.message.enums;

import cn.wuxia.common.util.StringUtil;

/**
 * 微信消息类型
 * [ticket id]
 * Description of the class 
 * @author guwen
 * @ Version : V<Ver.No> <2015年7月15日>
 */
public enum ReceiveEvent {

    subscribe("关注"),
    /**
     * 
     */
    unsubscribe("取消关注"),
    /**
     * 
     */
    SCAN("扫描二维码事件"),
    /**
     * 
     */
    LOCATION("定位"),
    /**
     * 
     */
    CLICK("点击菜单时事件操作"),
    /**
     * 
     */
    VIEW("点击查看自定义菜单"),
    
    /**
     * 
     */
    PAY("支付成功事件"),
    /**
     * 
     */
    SUBSCRIPTION("订阅事件"),
    /**
     * 
     */
    ShakearoundUserShake("摇一摇"),
    /** 卡券过审 */
    card_pass_check("卡券过审"),
    /** 卡券未过审 */
    card_not_pass_check("卡券未过审"),
    /** 用户领取卡券 */
    user_get_card("用户领取卡券"),
    /** 用户删除卡券 */
    user_del_card("用户删除卡券"),
    /** 进入会员卡 */
    user_view_card("进入会员卡"),
    /** 卡券核销 */
    user_consume_card("卡券核销"),
    /** 红包 **/
    ShakearoundLotteryBind("摇一摇红包"),

    TEMPLATESENDJOBFINISH("模板消息发送完成"),

    merchant_order("微店小程序订单付款");
    private String desc;

    private ReceiveEvent(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
