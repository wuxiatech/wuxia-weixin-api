/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.message.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.wuxia.wechat.WeChatException;
import org.springframework.util.Assert;

import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.custom.bean.Article;
import cn.wuxia.wechat.token.util.TokenUtil;

import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;

/**
 * 
 * 发送消息相关功能
 * 
 * @author guwen
 */
public class MessageUtil extends BaseUtil {

	/**
	 * 根据分组进行群发 文本消息
	 * 
	 * @author guwen
	 * @param groupId
	 *            分组id 为null 发送给所有人
	 * @param content
	 *            发送的内容
	 * @return
	 */
	public static Map<String, Object> msgSendAllText(BasicAccount account, Integer groupId, String content) throws WeChatException {
		Assert.hasText(content, "content 参数错误");
		String access_token = TokenUtil.getAccessToken(account);
		String url = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=" + access_token;
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> filter = new HashMap<String, Object>();
		if (groupId == null) {
			filter.put("is_to_all", true);
		} else {
			filter.put("is_to_all", false);
			filter.put("group_id", groupId);
		}

		Map<String, Object> text = new HashMap<String, Object>();
		text.put("content", content);

		map.put("filter", filter);
		map.put("text", text);
		map.put("msgtype", "text");

		return post(url, map);

	}

	/**
	 * 根据分组进行群发 图片消息
	 * 
	 * @author guwen
	 * @param groupId
	 *            分组id 为null 发送给所有人
	 * @param mediaId
	 *            media_id需通过基础支持中的上传下载多媒体文件来得到
	 * @return
	 */
	public static Map<String, Object> msgSendAllImage(BasicAccount account, Integer groupId, String mediaId) throws WeChatException {
		Assert.hasText(mediaId, "mediaId 参数错误");
		String access_token = TokenUtil.getAccessToken(account);
		String url = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=" + access_token;
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> filter = new HashMap<String, Object>();
		if (groupId == null) {
			filter.put("is_to_all", true);
		} else {
			filter.put("is_to_all", false);
			filter.put("group_id", groupId);
		}

		Map<String, Object> image = new HashMap<String, Object>();
		image.put("media_id", mediaId);

		map.put("filter", filter);
		map.put("image", image);
		map.put("msgtype", "image");

		return post(url, map);

	}

	/**
	 * 根据分组进行群发 图文消息
	 * 
	 * @author guwen
	 * @param groupId
	 *            分组id 为null 发送给所有人
	 * @param mediaId
	 *            media_id需通过基础支持中的uploadnews方法来得到
	 * @return
	 */
	public static Map<String, Object> msgSendAllNews(BasicAccount account, Integer groupId, String mediaId) throws WeChatException {
		Assert.hasText(mediaId, "mediaId 参数错误");
		String access_token = TokenUtil.getAccessToken(account);
		String url = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=" + access_token;
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> filter = new HashMap<String, Object>();
		if (groupId == null) {
			filter.put("is_to_all", true);
		} else {
			filter.put("is_to_all", false);
			filter.put("group_id", groupId);
		}

		Map<String, Object> mpnews = new HashMap<String, Object>();
		mpnews.put("media_id", mediaId);

		map.put("filter", filter);
		map.put("mpnews", mpnews);
		map.put("msgtype", "mpnews");

		return post(url, map);

	}

	/**
	 * 根据openid列表进行群发 文本消息
	 * 
	 * @author guwen
	 * @param touser
	 *            openid列表
	 * @param content
	 *            文本内容
	 * @return
	 */
	public static Map<String, Object> msgSendText(BasicAccount account, List<String> touser, String content) throws WeChatException {
		Assert.notEmpty(touser, "openid 参数错误");
		Assert.hasText(content, "content 参数错误");
		String access_token = TokenUtil.getAccessToken(account);
		String url = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=" + access_token;
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> text = new HashMap<String, Object>();
		text.put("content", content);

		map.put("touser", touser);
		map.put("text", text);
		map.put("msgtype", "text");

		return post(url, map);

	}

	/**
	 * 根据openid列表进行群发 图片消息
	 * 
	 * @author guwen
	 * @param touser
	 *            openid列表
	 * @param mediaId
	 *            media_id需通过基础支持中的上传下载多媒体文件来得到
	 * @return
	 */
	public static Map<String, Object> msgSendImage(BasicAccount account, List<String> touser, String mediaId) throws WeChatException {
		Assert.notEmpty(touser, "openid 参数错误");
		Assert.hasText(mediaId, "content 参数错误");
		String access_token = TokenUtil.getAccessToken(account);
		String url = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=" + access_token;
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> image = new HashMap<String, Object>();
		image.put("media_id", mediaId);

		map.put("touser", touser);
		map.put("image", image);
		map.put("msgtype", "image");

		return post(url, map);

	}

	/**
	 * 根据openid列表进行群发 图文消息
	 * 
	 * @author guwen
	 * @param touser
	 *            openid列表
	 * @param mediaId
	 *            media_id需通过基础支持中的uploadnews方法来得到
	 * @return
	 */
	public static Map<String, Object> msgSendNews(BasicAccount account, List<String> touser, String mediaId) throws WeChatException {
		Assert.notEmpty(touser, "openid 参数错误");
		Assert.hasText(mediaId, "content 参数错误");
		String access_token = TokenUtil.getAccessToken(account);
		String url = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=" + access_token;
		Map<String, Object> map = new HashMap<String, Object>();

		Map<String, Object> mpnews = new HashMap<String, Object>();
		mpnews.put("media_id", mediaId);

		map.put("touser", touser);
		map.put("mpnews", mpnews);
		map.put("msgtype", "mpnews");

		return post(url, map);

	}

}
