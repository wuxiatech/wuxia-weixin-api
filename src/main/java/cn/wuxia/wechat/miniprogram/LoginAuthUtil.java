package cn.wuxia.wechat.miniprogram;

import cn.wuxia.common.util.MapUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.web.httpclient.HttpAction;
import cn.wuxia.common.web.httpclient.HttpClientMethod;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.miniprogram.bean.AppLoginSession;

import java.util.Map;

public class LoginAuthUtil extends BaseUtil {
    final static HttpAction jscode2session = new HttpAction("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code", HttpClientMethod.GET);
    BasicAccount account;

    public LoginAuthUtil build() {
        String appid = properties.getProperty("miniapp.APP_ID");
        String appsecret = properties.getProperty("miniapp.APP_SECRET");
        this.account = new BasicAccount(appid, appsecret);
        return this;
    }

    public AppLoginSession getSession(String jscode) throws WeChatException {
        return LoginAuthUtil.getSession(account, jscode);
    }

    public static AppLoginSession getSession(BasicAccount account, String jscode) throws WeChatException {
        String url = jscode2session.getUrl();
        url = String.format(url, account.getAppid(), account.getAppSecret(), jscode);
        Map<String, Object> returnMap = get(url);
        if (MapUtil.isNotEmpty(returnMap)) {
            return BeanUtil.mapToBean(returnMap, AppLoginSession.class);
        } else {
            throw new WeChatException("返回数据有误！");
        }
    }
}
