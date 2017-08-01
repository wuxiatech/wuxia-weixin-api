/*
* Created on :2015-7-2
* Author :金
* Change History
* Version"), Date Author"), Reason
* <Ver.No"), <date"),<who modify <reason"),
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.card.emuns;

import cn.wuxia.common.util.StringUtil;

/**
 * 卡券颜色枚举
 */

public enum ColorEnum {

    Color010("#55BD47"),

    Color020("#10AD61"),

    Color030("#35A4DE"),

    Color040("#3D78DA"),

    Color050("#9058CB"),

    Color060("#DE9C33"),

    Color070("#EBAC16"),

    Color080("#F9861F"),

    Color090("#E75735"),

    Color100("#D54036");

    private String value;

    private ColorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ColorEnum getColorEnumByValue(String value) {
        for (ColorEnum colorEnum : ColorEnum.values()) {
            if (StringUtil.equals(value, colorEnum.getValue())) {
                return colorEnum;
            }
        }
        return null;
    }

    public static ColorEnum getColorEnumByName(String name) {
        for (ColorEnum colorEnum : ColorEnum.values()) {
            if (StringUtil.equals(name, colorEnum.name())) {
                return colorEnum;
            }
        }
        return null;
    }
}
