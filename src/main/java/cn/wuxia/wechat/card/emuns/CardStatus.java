/*
* Created on :Jan 10, 2015
* Author     :songlin
* 支付平台
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.card.emuns;

import cn.wuxia.common.util.StringUtil;

/**
 * 卡券类型
 * @author guwen
 *
 */
public enum CardStatus {

    /** 待审核 */
    CARD_STATUS_NOT_VERIFY("审核中"),

    /** 审核失败 */
    CARD_STATUS_VERIFY_FAIL("未通过"),

    /** 通过审核 */
    CARD_STATUS_VERIFY_OK("已通过");

    private String message;

    private CardStatus(String message) {
        this.message = message;
    }

    public static CardStatus getByMessage(String name) {
        for (CardStatus e : CardStatus.values()) {
            if (StringUtil.equals(name, e.name())) {
                return e;
            }
        }
        return null;

    }

    public String getMessage() {
        return message;
    }

}
