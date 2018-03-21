/*
* Created on :2015年6月23日
* Author     :Administrator
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.open.util;

import java.util.HashMap;
import java.util.Map;

import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.WeChatException;

/**
 * 第三方平台代获取access_token
 * [ticket id]
 * Description of the class 
 * @author songlin.li
 * @ Version : V<Ver.No> <2015年6月25日>
 */
public class ProxyAuthorizerTokenUtil extends ThirdBaseUtil {

    /**
     * 获取（刷新）授权公众号的令牌
     * @author guwen
     * @author modified songlin
     * @param authorizerAppid 授权方appid 
     * @param authorizerRefreshToken 授权方的刷新令牌
     * @return
     * @throws WeChatException 
     */
    public static Map<String, Object> getAuthorizerAccessToken(String authorizerAppid, String authorizerRefreshToken) throws WeChatException {
        String accessToken = getComponentAccessToken();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("component_appid", OPEN_APPID);
        map.put("authorizer_appid", authorizerAppid);
        map.put("authorizer_refresh_token", authorizerRefreshToken);

        Map<String, Object> result = post("https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=" + accessToken,
                map);
        return result;
    }

}
