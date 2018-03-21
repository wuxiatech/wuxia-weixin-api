/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.shakearound.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * 页面Util 主要用于微信摇一摇
 * @author guwen
 */
public class PageUtil extends BaseUtil {

    private final static String addUrl = properties.getProperty("shakearound.page.add");

    private final static String updateUrl = properties.getProperty("shakearound.page.update");

    private final static String searchUrl = properties.getProperty("shakearound.page.search");

    private final static String deleteUrl = properties.getProperty("shakearound.page.delete");

    /**
     * 新增页面
     * @param title 在摇一摇页面展示的主标题，不超过6个字
     * @param description 在摇一摇页面展示的副标题，不超过7个字
     * @param pageUrl 跳转链接
     * @param comment 页面的备注信息，不超过15个字 可空
     * @param iconUrl 在摇一摇页面展示的图片。图片需先上传至微信侧服务器，用“素材管理-上传图片素材”接口上传图片，返回的图片URL再配置在此处
     * @return
     * @throws UnsupportedEncodingException
     * @throws WeChatException 
     */
    public static Map<String, Object> add(BasicAccount account, String title, String description, String pageUrl, String comment, String iconUrl)
            throws UnsupportedEncodingException, WeChatException {
        Assert.hasText(title, "title 为必填 !");
        Assert.isTrue(title.length() <= 6, "title 长度必须小于等于6");
        Assert.hasText(description, "description 为必填 !");
        Assert.isTrue(description.length() <= 7, "description 长度必须小于等于7");
        Assert.isTrue(pageUrl.startsWith("http"), "pageUrl 格式不正确");
        Assert.isTrue(iconUrl.startsWith("http"), "iconUrl 格式不正确");

        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", title);
        map.put("description", description);
        map.put("icon_url", iconUrl);
        map.put("page_url", pageUrl);
        if (StringUtils.isNotEmpty(comment)) {
            Assert.isTrue(comment.length() <= 15, "comment 长度必须小于等于15");
            map.put("comment", comment);
        }

        return post(addUrl + "?access_token=" + access_token, map);
    }

    /**
     * 编辑页面信息
     * @param pageId 摇周边页面唯一ID
     * @param title 在摇一摇页面展示的主标题，不超过6个字
     * @param description 在摇一摇页面展示的副标题，不超过7个字
     * @param pageUrl 在摇一摇页面展示的图片。图片需先上传至微信侧服务器，用“素材管理-上传图片素材”接口上传图片，返回的图片URL再配置在此处
     * @param comment 跳转链接
     * @param iconUrl 页面的备注信息，不超过15个字
     * @return
     * @throws UnsupportedEncodingException
     * @throws WeChatException 
     */
    public static Map<String, Object> update(BasicAccount account, Integer pageId, String title, String description, String pageUrl, String comment,
            String iconUrl) throws UnsupportedEncodingException, WeChatException {
        Assert.notNull(pageId, "pageId 不能为空");
        Assert.hasText(title, "title 为必填 !");
        Assert.isTrue(title.length() <= 6, "title 长度必须小于等于6");
        Assert.hasText(description, "description 为必填 !");
        Assert.isTrue(description.length() <= 7, "description 长度必须小于等于7");
        Assert.isTrue(pageUrl.startsWith("http"), "pageUrl 格式不正确");
        Assert.isTrue(iconUrl.startsWith("http"), "iconUrl 格式不正确");

        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page_id", pageId);
        map.put("title", title);
        map.put("description", description);
        map.put("icon_url", iconUrl);
        map.put("page_url", pageUrl);
        if (StringUtils.isNotEmpty(comment)) {
            Assert.isTrue(comment.length() <= 15, "comment 长度必须小于等于15");
            map.put("comment", comment);
        }

        return post(updateUrl + "?access_token=" + access_token, map);
    }

    /**
     * 查询页面列表  -- 需要查询指定页面时
     * @param pageIds 指定页面的id列表
     * @return
     * @throws UnsupportedEncodingException
     * @throws WeChatException 
     */
    public static Map<String, Object> search(BasicAccount account, List<Integer> pageIds) throws UnsupportedEncodingException, WeChatException {
        Assert.notEmpty(pageIds, "pageIds 不能为空");

        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        List<Object> page_ids = new ArrayList<Object>();
        for (Integer item : pageIds) {
            page_ids.add(item);
        }
        map.put("page_ids", page_ids);

        return post(searchUrl + "?access_token=" + access_token, map);
    }

    /**
     * 查询页面列表 -- 需要分页查询或者指定范围内的页面时
     * @param begin 页面列表的起始索引值
     * @param count 待查询的页面个数
     * @return
     * @throws UnsupportedEncodingException
     * @throws WeChatException 
     */
    public static Map<String, Object> search(BasicAccount account, int begin, int count) throws UnsupportedEncodingException, WeChatException {
        Assert.isTrue(begin >= 0, "begin必须大于等于0");
        Assert.isTrue(count > 0 && count <= 50, "count必须大于0,小于等于50");

        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("begin", begin);
        map.put("count", count);

        return post(searchUrl + "?access_token=" + access_token, map);
    }

    /**
     * 删除页面
     * @author guwen
     * @param pageId
     * @return
     * @throws WeChatException 
     */
    public static Map<String, Object> delete(BasicAccount account, Integer pageId) throws WeChatException {
        Assert.notNull(pageId, "pageIds 不能为空");

        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page_id", pageId);

        return post(deleteUrl + "?access_token=" + access_token, map);
    }

}
