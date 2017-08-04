package cn.wuxia.wechat.js.util;

import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;

import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.NumberUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.sign.util.SignUtil;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * [ticket id]
 * 微信JS认证工具类 
 * 
 * @author wuwenhao
 * @fixed by songlin.li
 * @ Version : V<Ver.No> <2015年4月1日>
 */
public class AuthUtil extends BaseUtil {

    /**
     * 返回认证信息
     * @author wuwenhao
     * @modify songlin.li
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
        // 微信授权access_token 
        param.addParam("access_token", TokenUtil.getAccessToken(account));
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

    // 生成签名
    public static Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = Maps.newHashMap();

        String nonce_str = SignUtil.create_nonce_str();
        String timestamp = SignUtil.create_timestamp();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
        //            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        //            crypt.reset();
        //            crypt.update(string1.getBytes("UTF-8"));
        //            signature = byteToHex(crypt.digest());
        signature = DigestUtils.sha1Hex(StringUtils.getBytesUtf8(string1));

        ret.put("url", url);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        logger.debug("生成微信签名及返回其它信息:{}", ret);
        return ret;
    }

}
