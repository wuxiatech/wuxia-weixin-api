/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.fans.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import cn.wuxia.common.web.httpclient.HttpClientRequest;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * 分组相关功能
 * @author guwen
 */
public class TagsUtil extends BaseUtil {

    /**
     * 创建标签
     * @author songlin.li
     * @param name 分组名
     * @return
     */
    public static Map<String, Object> create(BasicAccount account, String name) {
        Assert.hasText(name, "name 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=" + access_token;
        HttpClientRequest param = new HttpClientRequest();
        param.setUrl(url);
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("name", name);
        map.put("tag", group);

        return post(param, map);

    }

    /**
     * 修改标签名
     * @author songlin.li
     * @param id 分组id
     * @param name 新的组名
     * @return
     */
    public static Map<String, Object> update(BasicAccount account, int id, String name) {
        Assert.hasText(name, "name 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/update?access_token=" + access_token;
        HttpClientRequest param = new HttpClientRequest();
        param.setUrl(url);
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("name", name);
        group.put("id", id);
        map.put("tag", group);

        return post(param, map);

    }

    /**
     * 获取用户身上的标签列表
     * @author guwen
     * @param openid 粉丝openid 
     * @param toGroupid 所到组id
     * @return
     */
    public static Map<String, Object> getUsertags(BasicAccount account, String openid) {
        Assert.hasText(openid, "openid 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/getidlist?access_token=" + access_token;
        HttpClientRequest param = new HttpClientRequest();
        param.setUrl(url);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openid", openid);

        return post(param, map);

    }

    /**
     * 批量为用户打标签
     * @author songlin.li
     * @param openidList 粉丝列表
     * @param tagid 所到组id
     * @return
     */
    public static Map<String, Object> membersBatchtagging(BasicAccount account, List<String> openidList, Integer tagid) {
        Assert.notEmpty(openidList, "openidList 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=" + access_token;
        HttpClientRequest param = new HttpClientRequest();
        param.setUrl(url);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openid_list", openidList);
        map.put("tagid", tagid);

        return post(param, map);

    }

    /**
     * 批量为用户取消标签
     * @author guwen
     * @param openidList 粉丝列表
     * @param tagid 所到组id
     * @return
     */
    public static Map<String, Object> membersBatchuntaging(BasicAccount account, List<String> openidList, Integer tagid) {
        Assert.notEmpty(openidList, "openidList 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=" + access_token;
        HttpClientRequest param = new HttpClientRequest();
        param.setUrl(url);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openid_list", openidList);
        map.put("tagid", tagid);

        return post(param, map);

    }

    /**
     * 删除标签
     * @author songlin.li
     * @param tagid  标签id
     * @return
     */
    public static Map<String, Object> delete(BasicAccount account, int tagid) {
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=" + access_token;
        HttpClientRequest param = new HttpClientRequest();
        param.setUrl(url);
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("id", tagid);
        map.put("tag", group);

        return post(param, map);

    }

    /**
     * 获取公众号已创建的标签
     * @author songlin.li
     * @param name 分组名
     * @return
     */
    public static Map<String, Object> get(BasicAccount account) {
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=" + access_token;
        HttpClientRequest param = new HttpClientRequest(url);
        return get(param);

    }

    /**
     * 获取标签下粉丝列表
     * @author songlin.li
     * @param tagid
     * @return
     */
    public static Map<String, Object> get(BasicAccount account, int tagid) {
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=" + access_token;
        HttpClientRequest param = new HttpClientRequest(url);
        Map<String, Object> map = Maps.newHashMap();
        map.put("tagid", tagid);
        return post(param, map);

    }
}
