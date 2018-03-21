/*
* Created on :2017年7月31日
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 wuxia.gd.cn All right reserved.
*/
package cn.wuxia.wechat.open.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import cn.wuxia.common.util.MapUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.oauth.bean.AuthUserInfoBean;
import cn.wuxia.wechat.oauth.bean.OAuthTokeVo;
import cn.wuxia.wechat.oauth.enums.SexEnum;

/**
 * 
 * [ticket id]
 * 代授权相关
 * @author songlin
 * @ Version : V<Ver.No> <2017年7月31日>
 */
public class ProxyLoginUtil extends ThirdBaseUtil {
    /**
     *  第二步：获取用户信息
     * @author Wind.Zhao
     * @param code
     * @param OPEN_APPID
     * @param appSecret
     * @param request
     * @return
     * @throws WeChatException 
     */
    public static OAuthTokeVo authUser(BasicAccount account, String code) throws WeChatException {
        // 第一个URL是用于获取code后，获取access_token 
        logger.info("进入请求地址：");
        String url = "https://api.weixin.qq.com/sns/oauth2/component/access_token?appid=%s&code=%s&grant_type=authorization_code&component_appid=%s&component_access_token=%s";
        // 获取toke
        OAuthTokeVo authToke = new OAuthTokeVo();
        authToke.setWxAccount(account);
        /**
         * songlin.li
         *  code 在auth2授权后redirect url中返回，要获取授权信息要先调用oauth2(url)
         */

        authToke.setCode(code);
        // 接收返回json
        url = String.format(url, account.getAppid(), code, OPEN_APPID, getComponentAccessToken());
        // 把json转换成MAP对象
        Map<String, Object> jsonMap = post(url);
        // 如果请求正确则赋值
        logger.info("第一个JSON数据：" + jsonMap);
        if (StringUtil.isNotBlank(jsonMap.get("errcode"))) {
            throw new WeChatException("无效:" + code + "[" + jsonMap.get("errmsg") + "]");
        }
        authToke.setAccessToken(MapUtil.getString(jsonMap, "access_token"));
        authToke.setExpiresIn(MapUtil.getString(jsonMap, "expires_in"));
        authToke.setOpenId(MapUtil.getString(jsonMap, "openid"));
        authToke.setRefreshToken(MapUtil.getString(jsonMap, "refresh_token"));
        authToke.setScope(MapUtil.getString(jsonMap, "scope"));
        authToke.setUnionId(MapUtil.getString(jsonMap, "unionid"));
        return authToke;
    }

    /**
     * 第四步：获取用户基础信息,仅在用户LoginUtil.oauth2(String url, true) scope 为true时有效
     * @see LoginUtil.oauth2()
     * @author songlin
     * @param oauthToken
     * @return
     * @throws WeChatException 
     */
    public static AuthUserInfoBean getAuthUserInfo(OAuthTokeVo oauthToken) throws WeChatException {
        // 用于抽取微信用户信息
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
        url = String.format(url, oauthToken.getAccessToken(), oauthToken.getOpenId());
        // 把json转换成MAP对象
        Map<String, Object> json = post(url);
        logger.info("第二个JSON数据：" + json);
        if (StringUtil.isNotBlank(json.get("errcode"))) {
            if (!rightAccessToken(oauthToken)) {
                logger.error("无效AccountToken:[" + oauthToken.getAccessToken() + "]");
                /**
                 * 尝试刷新AccessToken来获取有效的AccessToken
                 */
                oauthToken = refreshAccessToken(oauthToken);
                return getAuthUserInfo(oauthToken);
            } else {
                logger.error("无效:[" + json.get("errmsg") + "]");
                throw new WeChatException("无效: [" + json.get("errmsg") + "]");
            }
        }
        AuthUserInfoBean authUser = (AuthUserInfoBean) BeanUtil.mapToBean(json, AuthUserInfoBean.class);
        authUser.setSex(SexEnum.get((Integer) json.get("sex")));
        return authUser;
    }

    /**
     * 第三步：刷新access_token（如果需要）
     * 由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，refresh_token拥有较长的有效期（7天、30天、60天、90天），当refresh_token失效的后，需要用户重新授权
     * @author songlin
     * @param oauthToken
     * @return
     * @throws WeChatException 
     */
    private static OAuthTokeVo refreshAccessToken(OAuthTokeVo oauthToken) throws WeChatException {
        String url = "https://api.weixin.qq.com/sns/oauth2/component/refresh_token?appid=%s&grant_type=refresh_token&component_appid=%s&component_access_token=%s&refresh_token=%s";
        url = String.format(url, oauthToken.getWxAccount().getAppid(), OPEN_APPID, getComponentAccessToken(), oauthToken.getRefreshToken());

        // 把json转换成MAP对象
        Map<String, Object> json = post(url);
        logger.info("返回JSON数据{}", json);
        if (StringUtil.isNotBlank(json.get("errcode"))) {
            logger.error("无效:[" + json.get("errmsg") + "]");
            return null;
        }
        oauthToken.setAccessToken("" + json.get("access_token"));
        oauthToken.setExpiresIn("" + json.get("expires_in"));
        oauthToken.setOpenId((String) json.get("openid"));
        oauthToken.setRefreshToken("" + json.get("refresh_token"));
        oauthToken.setScope("" + json.get("scope"));
        oauthToken.setUnionId("" + json.get("unionid"));
        return oauthToken;
    }

    /**
     * 检验授权凭证（access_token）是否有效
     * @author songlin
     * @param oauthToken
     * @return
     */
    private static boolean rightAccessToken(OAuthTokeVo oauthToken) {
        String url = "https://api.weixin.qq.com/sns/auth";
        HttpClientRequest wxparam = new HttpClientRequest();
        wxparam.setUrl(url);
        // 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
        wxparam.addParam("access_token", oauthToken.getAccessToken());
        // 用户的唯一标识
        wxparam.addParam("openid", oauthToken.getOpenId());
        // 把json转换成MAP对象
        try {
            Map<String, Object> json = post(wxparam);
            return true;
        }catch (WeChatException e){
            return false;
        }
    }

    /**
     * 第一步：微信第三方平台代授权登录返回
     * @author songlin.li
     * @param url
     * @param scope true为非静默授权，需要用户确认；false为静默授权
     * @throws UnsupportedEncodingException 
     */
    public static String oauth2(String appid, String openThirdAppid, String url, boolean scope) throws UnsupportedEncodingException {
        url = URLEncoder.encode(url, "UTF-8");
        openThirdAppid = StringUtil.isBlank(openThirdAppid) ? ProxyOAuthUtil.OPEN_APPID : openThirdAppid;
        String oauthurl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&component_appid=%s#wechat_redirect";
        oauthurl = String.format(oauthurl, appid, url, (scope ? "snsapi_userinfo" : "snsapi_base"), openThirdAppid);
        logger.info("开始微信{}第三方{}代授权url:{}", (scope ? "非静默" : "静默"), openThirdAppid, oauthurl);
        return oauthurl;
    }

}
