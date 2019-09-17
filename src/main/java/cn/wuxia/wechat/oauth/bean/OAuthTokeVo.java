/*
* Created on :2015年3月3日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.oauth.bean;

import java.io.Serializable;

import cn.wuxia.wechat.BasicAccount;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * [ticket id]
 * 微信 token类
 * @author songlin.li
 * @ Version : V<Ver.No> <2015年3月3日>
 */
@Getter
@Setter
public class OAuthTokeVo implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private String accessToken;

    private String expiresIn;

    private String refreshToken;

    private String openId;

    private String scope;

    private String appid;
    /**
     * 可能为空，注意使用
     */
    private String unionId;
}
