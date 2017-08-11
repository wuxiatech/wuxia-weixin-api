/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.custom.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.custom.bean.Article;
import cn.wuxia.wechat.custom.bean.KefuAccount;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * 发送消息相关功能
 * 
 * @author guwen
 */
public class MessageUtil extends BaseUtil {

    /**
     * 发送客服信息 文本
     * 
     * @author guwen
     * @param touser
     *            接收者openid
     * @param content
     *            文本内容
     * @return
     */
    public static Map<String, Object> customSendText(BasicAccount account, String touser, String content) {

        return customSendText(account, null, touser, content);
    }

    /**
     * 发送客服信息 图片
     * 
     * @author guwen
     * @param touser
     *            接收者openid
     * @param mediaId
     *            发送的图片/语音/视频的媒体ID
     * @return
     */
    public static Map<String, Object> customSendImage(BasicAccount account, String touser, String mediaId) {
        return customSendImage(account, null, touser, mediaId);

    }

    /**
     * 发送客服信息 图文
     * 
     * @author guwen
     * @param touser
     *            接收者openid
     * @param articles
     *            发送的图文列表
     * @return
     */
    public static Map<String, Object> customSendNews(BasicAccount account, String touser, List<Article> articles) {
        return customSendNews(account, null, touser, articles);
    }

    /**
     * 发送客服信息 文本
     * 
     * @author songlin.li
     * @param kefuAccount
     *            客服账号
     * @param touser
     *            接收者openid
     * @param content
     *            文本内容
     * @return
     */
    public static Map<String, Object> customSendText(BasicAccount account, KefuAccount kefuAccount, String touser, String content) {
        Assert.hasText(touser, "touser 参数错误");
        Assert.hasText(content, "content 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, Object> text = new HashMap<String, Object>();
        text.put("content", content);

        map.put("touser", touser);
        map.put("text", text);
        map.put("msgtype", "text");

        if (kefuAccount != null) {
            Map<String, Object> customservice = new HashMap<String, Object>();
            customservice.put("kf_account", kefuAccount.getKf_account());
            map.put("customservice", customservice);
        }
        return post(url, map);

    }

    /**
     * 发送客服信息 图片
     * 
     * @author songlin.li
     * @param kefuAccount
     *            客服账号
     * @param touser
     *            接收者openid
     * @param mediaId
     *            发送的图片/语音/视频的媒体ID
     * @return
     */
    public static Map<String, Object> customSendImage(BasicAccount account, KefuAccount kefuAccount, String touser, String mediaId) {
        Assert.hasText(touser, "touser 参数错误");
        Assert.hasText(mediaId, "mediaId 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, Object> image = new HashMap<String, Object>();
        image.put("media_id", mediaId);

        map.put("touser", touser);
        map.put("image", image);
        map.put("msgtype", "image");
        if (kefuAccount != null) {
            Map<String, Object> customservice = new HashMap<String, Object>();
            customservice.put("kf_account", kefuAccount.getKf_account());
            map.put("customservice", customservice);
        }
        return post(url, map);

    }

    /**
     * 发送客服信息 图文
     * 
     * @author songlin.li
     * @param kefuAccount
     *            客服账号
     * @param touser
     *            接收者openid
     * @param articles
     *            发送的图文列表
     * @return
     */
    public static Map<String, Object> customSendNews(BasicAccount account, KefuAccount kefuAccount, String touser, List<Article> articles) {
        Assert.hasText(touser, "touser 参数错误");
        Assert.notEmpty(articles, "articles 参数错误");
        if (articles.size() > 8) {
            throw new IllegalArgumentException("articles的个数不能大于8");
        }
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("touser", touser);
        map.put("msgtype", "news");

        Map<String, List<Article>> news = new HashMap<String, List<Article>>();
        news.put("articles", articles);
        map.put("news", news);
        if (kefuAccount != null) {
            Map<String, Object> customservice = new HashMap<String, Object>();
            customservice.put("kf_account", kefuAccount.getKf_account());
            map.put("customservice", customservice);
        }
        return post(url, map);

    }

    /**
     * 发送客服信息 音频
     * 
     * @author songlin.li
     * 
     * @param touser
     *            接收者openid
     * @param mediaId
     *            发送的图片/语音/视频的媒体ID
     * @return
     */
    public static Map<String, Object> customSendVoice(BasicAccount account, String touser, String mediaId) {

        return customSendVoice(account, null, touser, mediaId);
    }

    /**
     * 发送客服信息 音频
     * 
     * @author songlin.li
     * @param kefuAccount
     *            客服账号
     * @param touser
     *            接收者openid
     * @param mediaId
     *            发送的图片/语音/视频的媒体ID
     * @return
     */
    public static Map<String, Object> customSendVoice(BasicAccount account, KefuAccount kefuAccount, String touser, String mediaId) {
        Assert.hasText(touser, "touser 参数错误");
        Assert.hasText(mediaId, "mediaId 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, Object> voice = new HashMap<String, Object>();
        voice.put("media_id", mediaId);

        map.put("touser", touser);
        map.put("voice", voice);
        map.put("msgtype", "voice");
        if (kefuAccount != null) {
            Map<String, Object> customservice = new HashMap<String, Object>();
            customservice.put("kf_account", kefuAccount.getKf_account());
            map.put("customservice", customservice);
        }
        return post(url, map);

    }

    /**
     * 发送客服信息 视频
     * 
     * @author songlin.li
     * 
     * @param touser
     *            接收者openid
     * @param mediaId
     *            发送的图片/语音/视频的媒体ID
     * @return
     */
    public static Map<String, Object> customSendVideo(BasicAccount account, String touser, String mediaId, String thumbMediaId, String title,
            String description) {
        return customSendVideo(account, null, touser, mediaId, thumbMediaId, title, description);
    }

    /**
     * 发送客服信息 视频
     * 
     * @author songlin.li
     * @param kefuAccount
     *            客服账号
     * @param touser
     *            接收者openid
     * @param mediaId
     *            发送的图片/语音/视频的媒体ID
     * @return
     */
    public static Map<String, Object> customSendVideo(BasicAccount account, KefuAccount kefuAccount, String touser, String mediaId,
            String thumbMediaId, String title, String description) {
        Assert.hasText(touser, "touser 参数错误");
        Assert.hasText(mediaId, "mediaId 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + access_token;
        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, Object> video = new HashMap<String, Object>();
        video.put("media_id", mediaId);
        // 缩略图
        video.put("thumb_media_id", thumbMediaId);
        video.put("title", title);
        video.put("description", description);
        map.put("touser", touser);
        map.put("video", video);
        map.put("msgtype", "video");
        if (kefuAccount != null) {
            Map<String, Object> customservice = new HashMap<String, Object>();
            customservice.put("kf_account", kefuAccount.getKf_account());
            map.put("customservice", customservice);
        }
        return post(url, map);

    }
}
