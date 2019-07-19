/*
* Created on :Jan 10, 2015
* Author     :songlin
* 支付平台
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.card.emuns;

/**
 * 卡券类型
 * @author guwen
 *
 */
public enum CardType {

    /** 通用券 */
    general_coupon,
    /** 团购券 */
    groupon,
    /** 礼品券 */
    gift,
    /** 代金券 */
    cash,
    /** 折扣券 */
    discount,
    /** 会员卡 */
    member_card,
    /** 景点门票 */
    scenic_ticket,
    /** 电影票 */
    movie_ticket,
    /** 飞机票 */
    boarding_pass,
    /** 红包 */
    lucky_money,
    /** 会议门票 */
    meeting_ticket,
    /** 分豆豆自制券优惠券*/
    fdd_general_coupon,
    /** 分豆豆自制代金券 **/
    fdd_cash
}
