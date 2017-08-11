/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.menu.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.menu.bean.Button;
import cn.wuxia.wechat.menu.bean.Matchrule;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * 菜单相关功能
 * @author guwen
 */
public class MenuUtil extends BaseUtil {

    /**
     * 创建菜单
     * @author guwen
     */
    public static Map<String, Object> create(BasicAccount account, List<Button> buttonList) {
        Assert.notEmpty(buttonList, "buttonList 参数有误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token;
        Map<String, List<Button>> map = new HashMap<>();
        map.put("button", buttonList);

        return post(url, map);

    }

    /**
     * 创建自定义菜单
     * 出于安全考虑，一个公众号的所有个性化菜单，最多只能设置为跳转到3个域名下的链接
     * @author songlin.li
     * @param account
     * @param buttonList
     * @param rule
     */
    public static Map<String, Object> createConditionMenu(BasicAccount account, List<Button> buttonList, Matchrule rule) {
        Assert.notEmpty(buttonList, "buttonList 参数有误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=" + access_token;
        Map<String, Object> map = new HashMap<>();
        map.put("button", buttonList);
        map.put("matchrule", rule);
        return post(url, map);
    }

    /**
    * 创建自定义菜单
    * @author songlin.li
    * @param account
    * @param openid
    */
    public static Map<String, Object> testMatchrule(BasicAccount account, String openid) {
        Assert.notNull(openid, "openid 参数有误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/menu/trymatch?access_token=" + access_token;
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", openid);
        return post(url, map);
    }

    /**
     * 删除自定义菜单
     * @author songlin.li
     * @param account
     */
    public static Map<String, Object> delete(BasicAccount account) {
        Assert.notNull(account, "openid 参数有误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=" + access_token;
        return get(url);
    }

    /**
     * 查看自定义菜单
     * @author songlin.li
     * @param account
     */
    public static Map<String, Object> get(BasicAccount account) {
        Assert.notNull(account, "openid 参数有误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=" + access_token;
        return get(url);
    }

}
