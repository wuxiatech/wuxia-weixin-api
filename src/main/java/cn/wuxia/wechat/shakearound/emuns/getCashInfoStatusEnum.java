/*
* Created on :2015年10月29日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.shakearound.emuns;

import cn.wuxia.common.util.NumberUtil;
import cn.wuxia.common.util.StringUtil;

public enum getCashInfoStatusEnum {
    /** 发放中 **/
    SENDING((short) 0, "发放中"),
    /** 已发放待领取 **/
    SENT((short) 1, "已发放待领取"),
    /** 发放失败 **/
    FAILED((short) 2, "发放失败"),
    /** 已领取 **/
    RECEIVED((short) 3, "已领取"),
    /** 已退款 **/
    REFUND((short) 4, "已退款");

    private Short type;

    private String value;

    private getCashInfoStatusEnum(Short type, String value) {
        this.type = type;
        this.value = value;
    }

    public Short getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static getCashInfoStatusEnum getEnumName(String enumName) {
        for (getCashInfoStatusEnum enumStatusName : getCashInfoStatusEnum.values()) {
            if (StringUtil.equals(enumName, enumStatusName.name())) {
                return enumStatusName;
            }
        }
        return null;
    }

    public static getCashInfoStatusEnum getShort(Short type) {
        for (getCashInfoStatusEnum enumStatusName : getCashInfoStatusEnum.values()) {
            if (NumberUtil.equals(type, enumStatusName.getType())) {
                return enumStatusName;
            }
        }
        return null;
    }

}
