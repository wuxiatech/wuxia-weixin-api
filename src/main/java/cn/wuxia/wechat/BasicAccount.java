package cn.wuxia.wechat;

import cn.wuxia.common.util.StringUtil;

/**
 * 微信账号  开发者配置
 * [ticket id]
 * Description of the class 
 * @author songlin.li
 * @ Version : V<Ver.No> <2016年3月31日>
 */
public class BasicAccount {

    private String appid;

    private String appSecret;

    private String authorizerRefreshToken;

    private boolean isAuthorizedToThird;

    public BasicAccount(String appid, String secretOrRefreshToken) {
        this.appid = appid;
        if (StringUtil.startsWith(secretOrRefreshToken, "refreshtoken@")) {
            this.authorizerRefreshToken = secretOrRefreshToken;
            isAuthorizedToThird = true;
        } else {
            this.appSecret = secretOrRefreshToken;
            isAuthorizedToThird = false;
        }
    }

    public String getAppid() {
        return appid;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getAuthorizerRefreshToken() {
        return authorizerRefreshToken;
    }

    public boolean isAuthorizedToThird() {
        return isAuthorizedToThird;
    }

}
