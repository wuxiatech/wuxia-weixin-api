package cn.wuxia.wechat.token.util;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.springframework.util.Assert;

import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.util.NumberUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.open.util.ProxyAuthorizerTokenUtil;

/**
 * [ticket id] 微信工具类
 *
 * @author wuwenhao @ Version : V<Ver.No> <2015年4月1日>
 */
public class TokenUtil extends BaseUtil {

    private final static String CACHE_ACCESS_TOKEN_KEY = "cache_access_token";

    private final static String CACHE_PREV_TOKEN_TIME_KEY = "cache_prev_token_time";

    /**
     * 获取access_token
     *
     * @return
     * @throws IOException
     * @author songlin
     */
    public static String getAccessToken(BasicAccount account) {

        if (StringUtil.isNotBlank(account.getAppSecret())) {
            return getAccessToken(account.getAppid(), account.getAppSecret());
        } else if (StringUtil.isNotBlank(account.getAuthorizerRefreshToken())) {
            return getAuthorizerAccessToken(account.getAppid(), account.getAuthorizerRefreshToken());
        } else {
            throw new RuntimeException("app_secret或authorizer_refresh_token不能为空");
        }

    }

    /**
     * 获取access_token
     *
     * @return
     * @throws IOException
     * @author songlin
     */
    public static String getAccessToken(String appid, String appSecret) {
        logger.info("请求时间1：" + System.currentTimeMillis());
        String accessToken = (String) getOutCache(appid, CACHE_ACCESS_TOKEN_KEY);
        System.out.println("access_token:" + accessToken);
        Long prevTime = NumberUtil.toLong(getOutCache(appid, CACHE_PREV_TOKEN_TIME_KEY), 0L);
        System.out.println("prev_time:" + DateUtil.defaultFormatTimeStamp(new Date(prevTime)));
        Long nowTime = DateUtil.newInstanceDate().getTime();
        System.out.println("now_time:" + DateUtil.defaultFormatTimeStamp(new Date(nowTime)));
        Long between = (nowTime - prevTime) / 1000;
        if (between < 1 * 60 * 60 && StringUtil.isNotBlank(accessToken)) { // 一小时更新一次
            logger.info("ASSCESS_TOKEN在有效期内{} < 3600 return {}", between, accessToken);
            return accessToken;
        }
        synchronized (appid) {
            logger.info("请求时间2：" + System.currentTimeMillis());
            logger.info("ASSCESS_TOKEN重新获取{} > 3600 或 access_token , {}", between, accessToken);
            HttpClientRequest wxparam = new HttpClientRequest();
            // 获取access_token填写client_credential
            wxparam.addParam("grant_type", "client_credential");
            // 第三方用户唯一凭证
            wxparam.addParam("appid", appid);
            // 第三方用户唯一凭证密钥，即appsecret
            wxparam.addParam("secret", appSecret);
            // 获取微信授权access_token
            String wxurl = "https://api.weixin.qq.com/cgi-bin/token";
            wxparam.setUrl(wxurl);
            // 获取微信access_token认证
            HttpClientResponse resp;
            try {
                resp = HttpClientUtil.get(wxparam);
            } catch (HttpClientException e) {
                logger.error("", e.getMessage());
                throw new RuntimeException(e);
            }
            Map<String, Object> map = JsonUtil.fromJson(resp.getStringResult());
            if (StringUtil.isBlank(map.get("access_token"))) {
                logger.error("{}", map);
                throw new RuntimeException("获取access_token有误:" + map);
            }
            putInCache(appid, CACHE_PREV_TOKEN_TIME_KEY, DateUtil.newInstanceDate().getTime());
            putInCache(appid, CACHE_ACCESS_TOKEN_KEY, (String) map.get("access_token"));
            Assert.isTrue(StringUtil.equals((String) map.get("access_token"), (String) getOutCache(appid, CACHE_ACCESS_TOKEN_KEY)), "access_token值不相同");
            logger.info("请求时间3：" + System.currentTimeMillis());
            return (String) map.get("access_token");
        }
    }

    /**
     * 获取authorizer_access_token
     *
     * @return
     * @throws IOException
     * @author songlin
     */
    public static String getAuthorizerAccessToken(String authorizerAppid, String authorizerRefreshToken) {
        logger.info("请求时间1：" + System.currentTimeMillis());
        String accessToken = (String) getOutCache(authorizerAppid, CACHE_ACCESS_TOKEN_KEY);
        System.out.println("authorizer_access_token:" + accessToken);
        Long prevTime = NumberUtil.toLong(getOutCache(authorizerAppid, CACHE_PREV_TOKEN_TIME_KEY), 0L);
        System.out.println("prev_time:" + DateUtil.defaultFormatTimeStamp(new Date(prevTime)));
        Long nowTime = DateUtil.newInstanceDate().getTime();
        System.out.println("now_time:" + DateUtil.defaultFormatTimeStamp(new Date(nowTime)));
        Long between = (nowTime - prevTime) / 1000;
        if (between < 1 * 60 * 60 && StringUtil.isNotBlank(accessToken)) { // 一小时更新一次
            logger.info("ASSCESS_TOKEN在有效期内{} < 3600 return {}", between, accessToken);
            return accessToken;
        }
        synchronized (authorizerAppid) {
            logger.info("请求时间2：" + System.currentTimeMillis());
            logger.info("ASSCESS_TOKEN重新获取{} > 3600 或 authorizer_access_token , {}", between, accessToken);

            Map<String, Object> map;
            try {
                map = ProxyAuthorizerTokenUtil.getAuthorizerAccessToken(authorizerAppid, authorizerRefreshToken);
            } catch (WeChatException e) {
                logger.error("", e.getMessage());
                throw new RuntimeException(e);
            }
            if (StringUtil.isBlank(map.get("authorizer_access_token"))) {
                logger.error("{}", map);
                throw new RuntimeException("获取authorizer_access_token有误:" + map);
            }
            putInCache(authorizerAppid, CACHE_PREV_TOKEN_TIME_KEY, DateUtil.newInstanceDate().getTime());
            putInCache(authorizerAppid, CACHE_ACCESS_TOKEN_KEY, (String) map.get("authorizer_access_token"));
            Assert.isTrue(StringUtil.equals((String) map.get("authorizer_access_token"), (String) getOutCache(authorizerAppid, CACHE_ACCESS_TOKEN_KEY)),
                    "authorizer_access_token值不相同");
            logger.info("请求时间3：" + System.currentTimeMillis());
            return (String) map.get("authorizer_access_token");
        }
    }
}
