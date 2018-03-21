/*
* Created on :2017年7月31日
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 wuxia.gd.cn All right reserved.
*/
package cn.wuxia.wechat.open.util;

import java.util.Map;

import org.springframework.util.Assert;

import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.NumberUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.js.util.AuthUtil;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * [ticket id]
 * 代公众号授权js
 * @author songlin
 * @ Version : V<Ver.No> <2017年7月31日>
 */
public class ProxyJsAuthUtil extends BaseUtil {
    /**
     * 返回认证信息
     * @author songlin.li
     * @param flag
     * @param url
     * @return
     */
    public static Map<String, String> authentication(BasicAccount account, String url) throws WeChatException {
        // 缓存微信 js 认证
        logger.debug("authentication {}", url);
        Assert.notNull(url, "authentication URL 参数不能为空");
        Map<String, String> authentication = (Map<String, String>) BaseUtil.getOutCache(account.getAppid(), url);
        if (null == authentication || authentication.isEmpty() || !StringUtil.equalsIgnoreCase(authentication.get("url") + "", url)) {
            authentication = AuthUtil.sign(getJsApiTicket(account), url);
            authentication.put("appId", account.getAppid());
            authentication.put("jsApiTicket", getJsApiTicket(account));
            logger.debug("authentication jsapi_ticket:{}", authentication);
            putInCache(account.getAppid(), url, authentication);
        } else {
            String jsApiTicket = authentication.get("jsApiTicket");

            /**
             * 如果jsapi_ticket(创建authentication的ticket)已经过期，则去掉缓存中的authentication
             */
            if (!StringUtil.equals(jsApiTicket, getJsApiTicket(account))) {
                TokenUtil.removeCache(account.getAppid(), url);
                return authentication(account, url);
            }
        }
        return authentication;
    }

    /**
     * 获取jsapi_ticket
     * @author songlin
     * @return
     */
    private static String getJsApiTicket(BasicAccount account) throws WeChatException {

        Long nowTime = DateUtil.newInstanceDate().getTime();
        Long between = (nowTime - NumberUtil.toLong((String) getOutCache(account.getAppid(), "jsapi_ticket_prev_time"), 0)) / 1000;
        if (between < 2 * 60 * 60 && StringUtil.isNotBlank(getOutCache("jsapi_ticket", account.getAppid()))) {
            return (String) getOutCache("jsapi_ticket", account.getAppid());
        }
        logger.debug("JSAPI_TICKET nowTime:" + nowTime);
        String ticketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
        HttpClientRequest param = new HttpClientRequest();
        // 第三方代微信授权access_token
        param.addParam("access_token", TokenUtil.getAuthorizerAccessToken(account.getAppid(), account.getAuthorizerRefreshToken()));
        // 授权类型
        param.addParam("type", "jsapi");
        param.setUrl(ticketUrl);
        //获取微信api_token认证
        Map<String, Object> apiToken = post(param);
        logger.debug("" + apiToken);
        if (!StringUtil.equals("ok", (String) apiToken.get("errmsg"))) {
            throw new WeChatException("获取jsapi_ticket有误:" + apiToken.get("errmsg"));
        }

        putInCache(account.getAppid(), "jsapi_ticket_prev_time", nowTime.toString());
        putInCache(account.getAppid(), "jsapi_ticket", (String) apiToken.get("ticket"));
        Assert.isTrue(StringUtil.equals((String) apiToken.get("ticket"), (String) getOutCache(account.getAppid(), "jsapi_ticket")),
                "jsapi_ticket值不相同");
        return (String) apiToken.get("ticket");
    }
}
