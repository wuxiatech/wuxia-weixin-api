package cn.wuxia.wechat;

import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * 微信账号  开发者配置
 * [ticket id]
 * Description of the class 
 * @author guwen
 * @ Version : V<Ver.No> <2016年3月31日>
 */
public class PayAccount extends Account implements Serializable{

    private static final long serialVersionUID = -2770311997608823845L;
    private String partner;

    private String appKey;

    /**
     * 商户名字
     */
    private String name;

    public PayAccount(Account account, String partner, String appKey) {
        this(account.getAppid(), account.getAppSecret(), account.getToken(), account.getPrimitiveid(), partner, appKey);
    }

    public PayAccount(String appid, String appSecret, String token, String primitiveid, String partner, String appKey) {
        super(appid, appSecret, token, primitiveid);
        Assert.notNull(partner, "找不到公众号配置[*.PARNER]信息，请检查wechat.config.properties配置文件");
        Assert.notNull(appKey, "找不到公众号配置[*.APP_KEY]信息，请检查wechat.config.properties配置文件");
        this.partner = partner;
        this.appKey = appKey;
    }

    public String getPartner() {
        return partner;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
