/*
* Created on :Jan 10, 2015
* Author     :songlin
* 支付平台
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.oauth.enums;

public enum SexEnum {
    MAN, WOMEN, UNKNOW;

    public static SexEnum get(Integer sex) {
        switch (sex) {
            case 0:
                return UNKNOW;
            case 1:
                return MAN;
            case 2:
                return WOMEN;
        }
        return UNKNOW;
    }
}
