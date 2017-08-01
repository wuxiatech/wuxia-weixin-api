/*
* Created on :Jan 10, 2015
* Author     :songlin
* 支付平台
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.card.emuns;

/**
 * code类型
 * @author guwen
 *
 */
public enum CodeType {

    /** 文本 */
    CODE_TYPE_TEXT,

    /** 一维码 */
    CODE_TYPE_BARCODE,

    /** 二维码 */
    CODE_TYPE_QRCODE,

    /** 二维码无code 显示 */
    CODE_TYPE_ONLY_QRCODE,

    /** 一维码无code 显示 */
    CODE_TYPE_ONLY_BARCODE,
    /** 无code类型  **/
    CODE_TYPE_NONE,

}
