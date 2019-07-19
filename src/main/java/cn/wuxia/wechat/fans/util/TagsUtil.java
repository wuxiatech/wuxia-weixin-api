/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.fans.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wuxia.wechat.WeChatException;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;

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
    public static Map<String, Object> create(BasicAccount account, String name) throws WeChatException {
        Assert.hasText(name, "name 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("name", name);
        map.put("tag", group);

        return post(url, map);

    }

    /**
     * 修改标签名
     * @author songlin.li
     * @param id 分组id
     * @param name 新的组名
     * @return
     */
    public static Map<String, Object> update(BasicAccount account, int id, String name) throws WeChatException {
        Assert.hasText(name, "name 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/update?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("name", name);
        group.put("id", id);
        map.put("tag", group);

        return post(url, map);

    }

    /**
     * 获取用户身上的标签列表
     * @author guwen
     * @param openid 粉丝openid 
     * @param toGroupid 所到组id
     * @return
     */
    public static Map<String, Object> getUsertags(BasicAccount account, String openid) throws WeChatException {
        Assert.hasText(openid, "openid 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/getidlist?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openid", openid);

        return post(url, map);

    }

    /**
     * 批量为用户打标签
     * @author songlin.li
     * @param openidList 粉丝列表
     * @param tagid 所到组id
     * @return
     */
    public static Map<String, Object> membersBatchtagging(BasicAccount account, List<String> openidList, Integer tagid) throws WeChatException {
        Assert.notEmpty(openidList, "openidList 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openid_list", openidList);
        map.put("tagid", tagid);

        return post(url, map);

    }

    /**
     * 批量为用户取消标签
     * @author guwen
     * @param openidList 粉丝列表
     * @param tagid 所到组id
     * @return
     */
    public static Map<String, Object> membersBatchuntaging(BasicAccount account, List<String> openidList, Integer tagid) throws WeChatException {
        Assert.notEmpty(openidList, "openidList 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openid_list", openidList);
        map.put("tagid", tagid);

        return post(url, map);

    }

    /**
     * 删除标签
     * @author songlin.li
     * @param tagid  标签id
     * @return
     */
    public static Map<String, Object> delete(BasicAccount account, int tagid) throws WeChatException {
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("id", tagid);
        map.put("tag", group);

        return post(url, map);

    }

    /**
     * 获取公众号已创建的标签
     * @author songlin.li
     * @param name 分组名
     * @return
     */
    public static Map<String, Object> get(BasicAccount account) throws WeChatException {
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=" + access_token;
        return get(url);

    }

    /**
     * 获取标签下粉丝列表
     * @author songlin.li
     * @param tagid
     * @return
     */
    public static Map<String, Object> get(BasicAccount account, int tagid) throws WeChatException {
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=" + access_token;
        Map<String, Object> map = Maps.newHashMap();
        map.put("tagid", tagid);
        return post(url, map);

    }
}
