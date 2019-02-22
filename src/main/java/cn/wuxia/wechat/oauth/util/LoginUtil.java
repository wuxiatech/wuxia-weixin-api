package cn.wuxia.wechat.oauth.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.google.common.collect.Maps;

import cn.wuxia.common.util.MapUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.oauth.bean.AuthUserInfoBean;
import cn.wuxia.wechat.oauth.bean.OAuthTokeVo;
import cn.wuxia.wechat.oauth.enums.SexEnum;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * [ticket id]
 * 微信授权登录工具类
 * @author songlin.li
 * @ Version : V<Ver.No> <2015年4月1日>
 */
public class LoginUtil extends BaseUtil {

    /**
     *  获取用户信息
     * @author Wind.Zhao
     * @param code
     * @param appId
     * @param appSecret
     * @param request
     * @return
     * @throws WeChatException 
     */
    public static OAuthTokeVo authUser(BasicAccount account, String code) throws WeChatException {
        // 第一个URL是用于获取code后，获取access_token 
        logger.info("进入请求地址：");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
        // 获取toke
        OAuthTokeVo authToke = new OAuthTokeVo();
        authToke.setAppid(account.getAppid());
        /**
         * songlin.li
         *  code 在auth2授权后redirect url中返回，要获取授权信息要先调用oauth2(url)
         */

        authToke.setCode(code);
        // 接收返回json
        Map<String, Object> jsonMap = Maps.newHashMap();
        HttpClientRequest wxparam = new HttpClientRequest();
        // 公众号的唯一标识
        wxparam.addParam("appid", account.getAppid());
        // 公众号的appsecret
        wxparam.addParam("secret", account.getAppSecret());
        // 填写第一步获取的code参数
        wxparam.addParam("code", code);
        // 填写为authorization_code
        wxparam.addParam("grant_type", properties.getProperty("GRANT_TYPE"));
        // 把json转换成MAP对象
        wxparam.setUrl(url);
        jsonMap = TokenUtil.post(wxparam);
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
        return authToke;
    }

    /**
     * 获取用户基础信息,仅在用户LoginUtil.oauth2(String url, true) scope 为true时有效
     * @see LoginUtil.oauth2()
     * @author songlin
     * @param oauthToken
     * @return
     * @throws WeChatException 
     */
    public static AuthUserInfoBean getAuthUserInfo(OAuthTokeVo oauthToken) throws WeChatException {
        // 用于抽取微信用户信息
        String url = "https://api.weixin.qq.com/sns/userinfo";
        HttpClientRequest wxparam = new HttpClientRequest();
        wxparam.setUrl(url);
        // 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
        wxparam.addParam("access_token", oauthToken.getAccessToken());
        // 用户的唯一标识
        wxparam.addParam("openid", oauthToken.getOpenId());
        // 语言
        wxparam.addParam("lang", "zh_CN");
        // end 
        // 把json转换成MAP对象
        Map<String, Object> json = post(wxparam);
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
     * 刷新access_token（如果需要）
     * 由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，refresh_token拥有较长的有效期（7天、30天、60天、90天），当refresh_token失效的后，需要用户重新授权
     * @author songlin
     * @param oauthToken
     * @return
     * @throws WeChatException 
     */
    private static OAuthTokeVo refreshAccessToken(OAuthTokeVo oauthToken) throws WeChatException {
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
        HttpClientRequest wxparam = new HttpClientRequest();
        wxparam.setUrl(url);
        wxparam.addParam("appid", oauthToken.getAppid());
        wxparam.addParam("grant_type", "refresh_token");
        wxparam.addParam("refresh_token", oauthToken.getRefreshToken());
        // 把json转换成MAP对象
        Map<String, Object> json = post(wxparam);
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
        return oauthToken;
    }

    /**
     * 检验授权凭证（access_token）是否有效
     * @author songlin
     * @param oauthToken
     * @return
     * @throws WeChatException 
     */
    private static boolean rightAccessToken(OAuthTokeVo oauthToken) throws WeChatException {
        String url = "https://api.weixin.qq.com/sns/auth";
        HttpClientRequest wxparam = new HttpClientRequest();
        wxparam.setUrl(url);
        // 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
        wxparam.addParam("access_token", oauthToken.getAccessToken());
        // 用户的唯一标识
        wxparam.addParam("openid", oauthToken.getOpenId());
        // 把json转换成MAP对象
        Map<String, Object> json = post(wxparam);
        logger.info("返回JSON数据{}", json);
        return StringUtil.equals("0", "" + json.get("errcode"));
    }

    /**
     * 微信授权登录返回
     * @author songlin.li
     * @param url
     * @param scope true为非静默授权，需要用户确认；false为静默授权
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String oauth2(String appid, String url, boolean scope) throws UnsupportedEncodingException {
        url = URLEncoder.encode(url, "UTF-8");
        String oauthurl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=1#wechat_redirect";
        oauthurl = String.format(oauthurl, appid, url, (scope ? "snsapi_userinfo" : "snsapi_base"));
        logger.info("开始微信{}授权url:{}", (scope ? "非静默" : "静默"), oauthurl);
        return oauthurl;
    }
}
