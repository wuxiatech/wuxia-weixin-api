/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.media.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import cn.wuxia.common.util.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.wuxia.common.hibernate.query.Pages;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.web.httpclient.*;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.WechatHttpRequest;
import cn.wuxia.wechat.custom.bean.Article;
import cn.wuxia.wechat.media.bean.MaterialMediaResult;
import cn.wuxia.wechat.media.bean.MediaContent;
import cn.wuxia.wechat.media.enums.MaterialMediaTypeEnum;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * [ticket id]
 * 永久临时素材
 * @author songlin
 * @ Version : V<Ver.No> <7 Apr, 2015>
 */
public class MaterialMediaUtil extends BaseUtil {

    private static HttpAction MEDIA_UPLOAD = HttpAction.Action("https://api.weixin.qq.com/cgi-bin/material/add_material", HttpClientMethod.POST);

    private static HttpAction MEDIA_GET = HttpAction.Action("https://api.weixin.qq.com/cgi-bin/material/get_material", HttpClientMethod.POST);

    private static HttpAction MEDIA_BATCHGET = HttpAction.Action("https://api.weixin.qq.com/cgi-bin/material/batchget_material",
            HttpClientMethod.POST);

    private static HttpAction NEWS_ADD = HttpAction.Action("https://api.weixin.qq.com/cgi-bin/material/add_news", HttpClientMethod.POST);

    /**
     * 上传多媒体资源
     * @author songlin
     * @param media
     * @param type
     */
    public static MaterialMediaResult upload(BasicAccount account, File media, MaterialMediaTypeEnum type) throws WeChatException {
        /**
         * 校验上传文件大小
         */
        long filesize = FileUtil.sizeOf(media);
        int sizekb = (int) ((filesize / 1024) + 1);
        if (sizekb > type.getSize()) {
            throw new WeChatException(type.getDesc());
        }

        HttpClientRequest param = HttpClientRequest.create(MEDIA_UPLOAD);
        String access_token = TokenUtil.getAccessToken(account);
        param.addParam("access_token", access_token);
        param.addParam("type", type.name());
        param.addParam("media", media);
        HttpClientResponse respone;
        try {
            respone = HttpClientUtil.upload(param);
        } catch (HttpClientException e) {
            throw new WeChatException(e);
        }
        Map<String, Object> m = JsonUtil.fromJson(respone.getStringResult());
        if (StringUtil.isNotBlank(m.get("errcode"))) {
            logger.error(m.get("errmsg") + "");
        }
        return BeanUtil.mapToBean(m, MaterialMediaResult.class);
    }

    /**
     * 上传图片1M
     * @author songlin
     * @param mediaId
     */
    public static MaterialMediaResult uploadImg(BasicAccount account, File media) throws WeChatException {
        return upload(account, media, MaterialMediaTypeEnum.image);
    }

    /**
     * 上传语音2M
     * @author songlin
     * @param mediaId
     */
    public static MaterialMediaResult uploadVoice(BasicAccount account, File media) throws WeChatException {
        return upload(account, media, MaterialMediaTypeEnum.voice);
    }

    /**
     * 上传视频10M
     * @author songlin
     * @param mediaId
     */
    public static MaterialMediaResult uploadVideo(BasicAccount account, File media) throws WeChatException {
        return upload(account, media, MaterialMediaTypeEnum.video);
    }

    /**
     * 上传图文
     * @author songlin
     * @param articles 图文列表
     * @return
     * @throws WeChatException
     */
    public static MaterialMediaResult uploadNews(@NotNull BasicAccount account, List<Article> articles) throws WeChatException {
        Assert.notEmpty(articles, "articles 参数有误");
        Map<String, Object> map = new HashMap<String, Object>();

        List<Map<String, Object>> list = new ArrayList<>();
        for (Article item : articles) {
            Map<String, Object> m = new HashMap<>();
            m.put("thumb_media_id", item.getThumbMediaId());
            if (StringUtil.isNotBlank(item.getAuthor())) {
                m.put("author", item.getAuthor());
            }
            m.put("title", item.getTitle());
            if (StringUtil.isNotBlank(item.getContentSourceUrl())) {
                m.put("content_source_url", item.getContentSourceUrl());
            }
            m.put("content", item.getContent());
            if (StringUtil.isNotBlank(item.getDigest())) {
                m.put("digest", item.getDigest());
            }
            if (StringUtil.isNotBlank(item.getShowCoverPic())) {
                m.put("show_cover_pic", item.getShowCoverPic());
            }

            list.add(m);
        }
        map.put("articles", list);
        Map m = WechatHttpRequest.build(account, NEWS_ADD).json(map).execute();
        return BeanUtil.mapToBean(m, MaterialMediaResult.class);
    }

    /**
     * 上传图文
     * @author songlin
     * @param articles 图文列表
     * @return
     * @throws WeChatException
     */
    public static void updateNews(@NotNull BasicAccount account, @NotNull String mediaId, Article article, int index) throws WeChatException {
        final HttpAction UPDATE_NEWS = HttpAction.Action("https://api.weixin.qq.com/cgi-bin/material/update_news", HttpClientMethod.POST);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("media_id", mediaId);
        map.put("index", index);
        Map<String, Object> m = new HashMap<>();
        m.put("thumb_media_id", article.getThumbMediaId());
        if (StringUtil.isNotBlank(article.getAuthor())) {
            m.put("author", article.getAuthor());
        }
        m.put("title", article.getTitle());
        if (StringUtil.isNotBlank(article.getContentSourceUrl())) {
            m.put("content_source_url", article.getContentSourceUrl());
        }
        m.put("content", article.getContent());
        if (StringUtil.isNotBlank(article.getDigest())) {
            m.put("digest", article.getDigest());
        }
        if (StringUtil.isNotBlank(article.getShowCoverPic())) {
            m.put("show_cover_pic", article.getShowCoverPic());
        }

        map.put("articles", m);
        WechatHttpRequest.build(account, UPDATE_NEWS).json(map).execute();
    }

    /**
    * 上传缩略图64K
    * @author songlin
    * @param mediaId
    */
    public static MaterialMediaResult uploadThumb(BasicAccount account, File media) throws WeChatException {
        return upload(account, media, MaterialMediaTypeEnum.thumb);
    }

    /**
     * 获取多媒体资源
     * @author songlin
     * @param media
     * @param type
     */
    public static Map get(BasicAccount account, String mediaid, MaterialMediaTypeEnum type) throws WeChatException {
        Map<String, Object> m = WechatHttpRequest.build(account).json("{\"media_id\": \"" + mediaid + "\"}").execute(MEDIA_GET);
        return m;
    }

    public static List<Article> getNews(BasicAccount account, String mediaid) throws WeChatException {
        AssertUtil.isNull(mediaid, "mediaid不能为空");
        Map content = get(account, mediaid, MaterialMediaTypeEnum.news);
        List<Map> newsItem = (List) MapUtils.getObject(content, "news_item");
        List<Article> articles = Lists.newArrayList();
        for (Map news : newsItem) {
            Article article = new Article();
            article.setTitle(MapUtils.getString(news, "title"));
            article.setDigest(MapUtils.getString(news, "digest"));
            article.setThumbMediaId(MapUtils.getString(news, "thumb_media_id"));
            article.setShowCoverPic(MapUtils.getString(news, "show_cover_pic"));
            article.setAuthor(MapUtils.getString(news, "author"));
            article.setContent(MapUtils.getString(news, "content"));
            article.setUrl(MapUtils.getString(news, "url"));
            article.setContentSourceUrl(MapUtils.getString(news, "content_source_url"));
            articles.add(article);
        }
        return articles;
    }

    /**
     * 获取多媒体资源
     * @author songlin
     * @param mediaId
     * @throws IOException
     * @throws WeChatException
     */
    public static void download(BasicAccount account, String mediaId, String filePath) throws IOException, WeChatException {
        String access_token = TokenUtil.getAccessToken(account);
        HttpClientResponse respone;
        try {
            respone = HttpClientUtil.postJson(MEDIA_GET.getUrl() + "?access_token=" + access_token, "{\"media_id\":\"" + mediaId + "\"}");
        } catch (HttpClientException e) {
            throw new WeChatException(e);
        }
        logger.info("HEADERS:{}", respone.getResponseHeaders());
        String contenttype = respone.getHeader("contenttype");
        if (StringUtil.indexOf(contenttype, "application/json") >= 0 || StringUtil.indexOf(contenttype, "text/plain") >= 0) {
            Map map = JsonUtil.fromJson(respone.getStringResult());
            throw new WeChatException(MapUtil.getString(map, "errmsg"));
        }
        File file = new File(filePath);
        FileOutputStream output = FileUtils.openOutputStream(file);
        try {
            IOUtils.copy(respone.getContent(), output);
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(respone.getContent());
        }
        logger.info("成功保存文件到{}", filePath);
    }

    /**
     * 批量多媒体资源列表
     * @author songlin
     * @param media
     * @param type
     */
    public static List<MediaContent> batchget(BasicAccount account, MaterialMediaTypeEnum type, Pages pages) throws WeChatException {
        boolean queryall = false;
        if (pages.getPageSize() < 0) {
            /**
             * 接口限制20条
             */
            pages.setPageSize(20);
            queryall = true;
        }
        Map<String, Object> param = Maps.newHashMap();
        param.put("type", type.name());
        param.put("offset", pages.getFirst());
        param.put("count", pages.getPageSize());

        Map m = WechatHttpRequest.build(account, MEDIA_BATCHGET).json(param).execute();

        int total_count = MapUtils.getInteger(m, "total_count");
        pages.setTotalCount(total_count);

        List<Map> items = (List) MapUtils.getObject(m, "item");
        List<MediaContent> mediaContents = Lists.newArrayList();
        for (Map map : items) {
            MediaContent mediaContent = new MediaContent();
            mediaContent.setMedia_id(MapUtils.getString(map, "media_id"));
            mediaContent.setUpdate_time(MapUtils.getString(map, "update_time"));
            switch (type) {

                case image:
                case voice:
                case video:
                case thumb:
                    mediaContent.setUrl(MapUtils.getString(map, "url"));
                    mediaContent.setName(MapUtils.getString(map, "name"));
                    break;
                case news:
                    Map content = MapUtils.getMap(map, "content");
                    List<Map> newsItem = (List) MapUtils.getObject(content, "news_item");
                    List<Article> articles = Lists.newArrayList();
                    for (Map news : newsItem) {
                        Article article = new Article();
                        article.setTitle(MapUtils.getString(news, "title"));
                        article.setDigest(MapUtils.getString(news, "digest"));
                        article.setThumbMediaId(MapUtils.getString(news, "thumb_media_id"));
                        article.setShowCoverPic(MapUtils.getString(news, "show_cover_pic"));
                        article.setAuthor(MapUtils.getString(news, "author"));
                        article.setContent(MapUtils.getString(news, "content"));
                        article.setUrl(MapUtils.getString(news, "url"));
                        article.setContentSourceUrl(MapUtils.getString(news, "content_source_url"));
                        articles.add(article);
                    }
                    mediaContent.setContent(articles);
                    break;
            }
            mediaContents.add(mediaContent);
        }

        if (queryall && pages.isHasNext()) {
            mediaContents.addAll(batchget(account, type, new Pages(pages.getNextPage(), pages.getPageSize())));
        }
        return mediaContents;
    }

}
