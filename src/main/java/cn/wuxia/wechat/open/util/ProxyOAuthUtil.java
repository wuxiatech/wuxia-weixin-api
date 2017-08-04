/*
* Created on :2015年6月23日
* Author     :Administrator
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.open.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.NumberUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.WeChatException;

/**
 * 第三方平台网页授权
 * [ticket id]
 * Description of the class 
 * @author Administrator
 * @ Version : V<Ver.No> <2015年6月25日>
 */
public class ProxyOAuthUtil extends ThirdBaseUtil {

    /**
     * 使用授权码换取公众号的授权信息
     * @author guwen
     * @author modifiedBy songlin
     * @return
     * @throws WeChatException 
     */
    public static Map<String, Object> apiQueryAuth(String authorizationCode) throws WeChatException {
        String accessToken = getComponentAccessToken();
        HttpClientRequest param = new HttpClientRequest();
        param.setUrl("https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=" + accessToken);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("component_appid", OPEN_APPID);
        map.put("authorization_code", authorizationCode);

        Map<String, Object> result = post(param, map);
        return result;
    }

    /**
     * 获取授权方的账户信息
     * @author guwen
     * @author modifiedBy songlin
     * @param authorizerAppid 授权方appid
     * @return
     */
    public static Map<String, Object> apiGetAuthorizerInfo(String authorizerAppid) throws WeChatException {
        String accessToken = getComponentAccessToken();
        HttpClientRequest param = new HttpClientRequest();
        param.setUrl("https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=" + accessToken);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("component_appid", OPEN_APPID);
        map.put("authorizer_appid", authorizerAppid);

        Map<String, Object> result = post(param, map);
        return result;
    }

    /**
     * 生成公众帐号授权第三方平台URl
     * @throws IOException 
     * @throws WeChatException 
     */
    public static String genAccountOAuthUrl(String redirectUri) throws IOException, WeChatException {
        redirectUri = URLEncoder.encode(redirectUri, "UTF-8");
        String result = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid=" + ProxyOAuthUtil.OPEN_APPID + "&pre_auth_code="
                + ProxyOAuthUtil.getPreAuthCode() + "&redirect_uri=" + redirectUri;
        return result;
    }

}
