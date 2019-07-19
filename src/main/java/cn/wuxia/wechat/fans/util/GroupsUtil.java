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

import org.springframework.util.Assert;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * 分组相关功能
 * @author guwen
 */
public class GroupsUtil extends BaseUtil {

    /**
     * 创建分组
     * @author guwen
     * @param name 分组名
     * @return
     * @throws WeChatException 
     */
    public static Map<String, Object> create(BasicAccount account, String name) throws WeChatException{
        Assert.hasText(name, "name 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("name", name);
        map.put("group", group);

        return post(url, map);

    }

    /**
     * 修改分组名
     * @author guwen
     * @param id 分组id
     * @param name 新的组名
     * @return
     * @throws WeChatException 
     */
    public static Map<String, Object> update(BasicAccount account, int id, String name) throws WeChatException {
        Assert.hasText(name, "name 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/groups/update?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("name", name);
        group.put("id", id);
        map.put("group", group);

        return post(url, map);

    }

    /**
     * 移动粉丝到分组
     * @author guwen
     * @param openid 粉丝openid 
     * @param toGroupid 所到组id
     * @return
     * @throws WeChatException 
     */
    public static Map<String, Object> membersUpdate(BasicAccount account, String openid, Integer toGroupid) throws WeChatException {
        Assert.hasText(openid, "openid 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openid", openid);
        map.put("to_groupid", toGroupid);

        return post(url, map);

    }

    /**
     * 批量移动用户分组
     * @author guwen
     * @param openidList 粉丝列表
     * @param toGroupid 所到组id
     * @return
     * @throws WeChatException 
     */
    public static Map<String, Object> membersBatchupdate(BasicAccount account, List<String> openidList, Integer toGroupid) throws WeChatException {
        Assert.notEmpty(openidList, "openidList 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/groups/members/batchupdate?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openid_list", openidList);
        map.put("to_groupid", toGroupid);

        return post(url, map);

    }

    /**
     * 删除分组
     * @author guwen
     * @param groupid  分组id
     * @return
     * @throws WeChatException 
     */
    public static Map<String, Object> delete(BasicAccount account, int groupid) throws WeChatException {
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/groups/delete?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> group = new HashMap<String, Object>();
        group.put("id", groupid);
        map.put("group", group);

        return post(url, map);

    }

    /**
     * 获取所有分组
     * @author songlin.li
     * @param name 分组名
     * @return
     * @throws WeChatException 
     */
    public static Map<String, Object> get(BasicAccount account) throws WeChatException {
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=" + access_token;
        return get(url);

    }
}
