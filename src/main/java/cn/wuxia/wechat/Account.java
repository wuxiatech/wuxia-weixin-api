package cn.wuxia.wechat;

import org.apache.commons.lang3.builder.ToStringBuilder;

import cn.wuxia.common.util.StringUtil;

import java.io.Serializable;

/**
 * 微信账号  开发者配置
 * [ticket id]
 * Description of the class 
 * @author songlin.li
 * @ Version : V<Ver.No> <2016年3月31日>
 */
public class Account extends BasicAccount implements Serializable{
    private static final long serialVersionUID = 3708714346608087242L;
    private String token;

    /**
     * 公众号原始id, 非必填
     */
    private String primitiveid;

    private String type;

    public Account(BasicAccount basicAccount) {
        super(basicAccount.getAppid(),
                StringUtil.isBlank(basicAccount.getAppSecret()) ? basicAccount.getAuthorizerRefreshToken() : basicAccount.getAppSecret());
    }

    /**
     * 
     * @param basicAccount
     * @param primitiveid
     */
    public Account(BasicAccount basicAccount, String token, String primitiveid) {
        this(basicAccount.getAppid(), basicAccount.getAppSecret(), token, primitiveid);
    }

    /**
     * 
     * @param appid
     * @param appSecret
     * @param token
     * @param primitiveid
     */
    public Account(String appid, String appSecret, String token, String primitiveid) {
        super(appid, appSecret);
        this.token = token;
        this.primitiveid = primitiveid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPrimitiveid() {
        return primitiveid;
    }

    public String getType() {
        return type;
    }

    public void setPrimitiveid(String primitiveid) {
        this.primitiveid = primitiveid;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
