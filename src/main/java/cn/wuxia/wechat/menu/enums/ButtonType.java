/*
* Created on :Jan 10, 2015
* Author     :songlin
* 支付平台
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.menu.enums;

/**
 * 按钮类型
 * @author guwen
 *
 */
public enum ButtonType {
    /** 点击推事件 */
    click,
    /** 跳转URL */
    view,
    /** 扫码推事件 */
    scancode_push,
    /** 扫码推事件且弹出“消息接收中”提示框 */
    scancode_waitmsg,
    /** 弹出系统拍照发图 */
    pic_sysphoto,
    /** 弹出拍照或者相册发图 */
    pic_photo_or_album,
    /** 弹出微信相册发图器 */
    pic_weixin,
    /** 弹出地理位置选择器 */
    location_select,
    /** 下发消息（除文本消息）*/
    media_id,
    /** 跳转图文消息URL */
    view_limited;

}
