package cn.wuxia.wechat;

import org.springframework.util.Assert;

/**
 * 微信账号  开发者配置
 * [ticket id]
 * Description of the class 
 * @author guwen
 * @ Version : V<Ver.No> <2016年3月31日>
 */
public class PayAccount extends Account {

    private String partner;

    private String appKey;

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

}
