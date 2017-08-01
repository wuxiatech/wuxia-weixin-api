/*
* Created on :2015年3月3日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.oauth.bean;

import java.io.Serializable;

import cn.wuxia.wechat.BasicAccount;

/**
 * 
 * [ticket id]
 * 微信 token类
 * @author wuwenhao
 * @ Version : V<Ver.No> <2015年3月3日>
 */
public class OAuthTokeVo implements Serializable {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private String code; // 授权CODE

    private String accessToken;

    private String expiresIn;

    private String refreshToken;

    private String openId;

    private String scope;

    private String unionId;

    private BasicAccount wxAccount;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public BasicAccount getWxAccount() {
        return wxAccount;
    }

    public void setWxAccount(BasicAccount wxAccount) {
        this.wxAccount = wxAccount;
    }

}
