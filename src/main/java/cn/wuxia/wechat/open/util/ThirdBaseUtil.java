package cn.wuxia.wechat.open.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.NumberUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.WeChatException;

/**
 * 
 * [ticket id]
 * 微信 开放第三方平台工具类
 * @author guwen
 * @ Version : V<Ver.No> <2015年6月19日>
 */
public class ThirdBaseUtil extends cn.wuxia.wechat.BaseUtil {

    protected static Logger logger = LoggerFactory.getLogger(ThirdBaseUtil.class);

    public static String OPEN_APPID;

    protected static String OPEN_APP_SECRET;

    public static String OPEN_TOKEN;

    public static String OPEN_ENCODING_AES_KEY; //消息加_密 密钥

    static {
        init();
    }

    private static void init() {
        OPEN_APPID = properties.getProperty("OPEN.APPID");
        OPEN_APP_SECRET = properties.getProperty("OPEN.APPSECRET");
        Assert.notNull(OPEN_APPID, "OPEN.APPID不能为空");
        Assert.notNull(OPEN_APP_SECRET, "OPEN_APP_SECRET不能为空");
        OPEN_TOKEN = properties.getProperty("OPEN.TOKEN");
        OPEN_ENCODING_AES_KEY = properties.getProperty("OPEN.ENCODINGAESKEY");
    }

    /**
     * 拿第三方平台票据
     * @author guwen
     * @return
     */
    protected static String getComponentVerifyTicket() {
        String componentVerifyTicket = (String) getOutCache(OPEN_APPID, "component_verify_ticket");
        logger.info("get componentVerifyTicket:{}", componentVerifyTicket);
        return componentVerifyTicket;
    }

    /**
     * 存第三方平台票据
     * @author guwen
     * @return
     */
    public static void setComponentVerifyTicket(String componentVerifyTicket) {
        logger.info("set componentVerifyTicket:{}", componentVerifyTicket);
        putInCache(OPEN_APPID, "component_verify_ticket", componentVerifyTicket);
    }

    /**
     * 第一步：获取开放平台第三方平台的access token 调用时先更新ticketValue
     * @author guwen
     * @author modifiedBy songlin
     * @param componentVerifyTicket 
     * @return
     * @throws WeChatException 
     */
    protected static String getComponentAccessToken() throws WeChatException {

        String verifyTicket = ThirdBaseUtil.getComponentVerifyTicket();
        Assert.hasText(verifyTicket, "ticketValue 参数错误");
        Long nowTime = System.currentTimeMillis();
        /**
         * 读取缓存信息
         */
        long componentAccessTokenPrevTime = NumberUtil.toLong(getOutCache(OPEN_APPID, "component_access_token_prev_time"), 0L);
        String componentAccessToken = (String) getOutCache(OPEN_APPID, "component_access_token");
        Long between = (nowTime - componentAccessTokenPrevTime);
        if (between < 1 * 60 * 60 * 1000 && StringUtil.isNotBlank(componentAccessToken)) { //一小时更新一次
            return componentAccessToken;
        }

        HttpClientRequest param = new HttpClientRequest();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("component_appid", OPEN_APPID);
        map.put("component_appsecret", OPEN_APP_SECRET);
        map.put("component_verify_ticket", verifyTicket);

        Map<String, Object> result = post("https://api.weixin.qq.com/cgi-bin/component/api_component_token", map);
        if (result.get("errcode") != null && (Integer) result.get("errcode") != 0) {
            logger.error("获取access_token有误:" + result);
            throw new WeChatException("获取access_token有误:" + result.get("errmsg"));
        }
        /**
         * 获取新的消息后存进缓存
         */
        putInCache(OPEN_APPID, "component_access_token_prev_time", System.currentTimeMillis());
        componentAccessToken = (String) result.get("component_access_token");
        putInCache(OPEN_APPID, "component_access_token", componentAccessToken);

        logger.info("新的第三方平台access token: {}" + componentAccessToken);
        return componentAccessToken;
    }

    /**
     * 第二步：获取开放平台第三方平台的预授权码
     * @author guwen
     * @author modifiedBy songlin
     * @param componentVerifyTicket 
     * @return
     * @throws IOException 
     * @throws WeChatException 
     */
    protected static String getPreAuthCode() throws IOException, WeChatException {
        String verifyTicket = getComponentVerifyTicket();
        Assert.hasText(verifyTicket, "ticketValue 参数错误");

        Long nowTime = DateUtil.newInstanceDate().getTime();
        /**
         * 读取缓存信息
         */
        Long between = (nowTime - NumberUtil.toLong(getOutCache(OPEN_APPID, "pre_auth_code_prev_time"), 0L));
        String preAuthCode = (String) getOutCache(OPEN_APPID, "pre_auth_code");
        if (between < 10 * 60 * 1000 && StringUtil.isNotBlank(preAuthCode)) { //十分钟更新一次
            return preAuthCode;
        }

        String accessToken = getComponentAccessToken();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("component_appid", OPEN_APPID);

        Map<String, Object> result = post("https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=" + accessToken, map);
        if (result.get("errcode") != null && (Integer) result.get("errcode") != 0) {
            logger.error("获取pre_auth_code有误:" + result);
            throw new WeChatException("获取pre_auth_code有误:" + result.get("errmsg"));
        }
        /**
         * 更新缓存信息
         */
        putInCache(OPEN_APPID, "pre_auth_code_prev_time", System.currentTimeMillis());
        preAuthCode = (String) result.get("pre_auth_code");
        putInCache(OPEN_APPID, "pre_auth_code", preAuthCode);

        logger.info("新的预授权码：{}", preAuthCode);
        return preAuthCode;

    }
}
