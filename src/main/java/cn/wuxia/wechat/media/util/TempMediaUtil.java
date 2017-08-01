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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;

import cn.wuxia.common.util.FileUtil;
import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.custom.bean.Article;
import cn.wuxia.wechat.media.bean.TempMediaResult;
import cn.wuxia.wechat.media.enums.TempMediaTypeEnum;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * [ticket id]
 * 新增临时素材
 * @author songlin
 * @ Version : V<Ver.No> <7 Apr, 2015>
 */
public class TempMediaUtil extends BaseUtil {

    private final static String MEDIA_DOWN_URL = properties.getProperty("MEDIA.DOWN.URL");

    private final static String MEDIA_UPLOAD_URL = properties.getProperty("MEDIA.UPLOAD.URL");

    /**
     * 上传多媒体资源
     * @author songlin
     * @param media
     * @param type
     */
    public static TempMediaResult upload(BasicAccount account, File media, TempMediaTypeEnum type) throws WeChatException {
        /**
         * 校验上传文件大小
         */
        long filesize = FileUtil.sizeOf(media);
        int sizekb = (int) ((filesize / 1024) + 1);
        if (sizekb > type.getSize()) {
            throw new WeChatException(type.getDesc());
        }

        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(MEDIA_UPLOAD_URL);
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
            return null;
        }
        return BeanUtil.mapToBean(m, TempMediaResult.class);
    }

    /**
     * 上传图片1M(PNG\JPEG\JPG\GIF)
     * @author songlin
     * @param mediaId
     */
    public static TempMediaResult uploadImg(BasicAccount account, File media) throws WeChatException {
        return upload(account, media, TempMediaTypeEnum.image);

    }

    /**
     * 上传语音2M
     * @author songlin
     * @param mediaId
     */
    public static TempMediaResult uploadVoice(BasicAccount account, File media) throws WeChatException {
        return upload(account, media, TempMediaTypeEnum.voice);
    }

    /**
     * 上传视频10M
     * @author songlin
     * @param mediaId
     */
    public static TempMediaResult uploadVideo(BasicAccount account, File media) throws WeChatException {
        return upload(account, media, TempMediaTypeEnum.video);

    }

    /**
    * 上传缩略图64K
    * @author songlin
    * @param mediaId
    */
    public static TempMediaResult uploadThumb(BasicAccount account, File media) throws WeChatException {
        return upload(account, media, TempMediaTypeEnum.thumb);

    }

    /**
     * 获取多媒体资源
     * @author songlin
     * @param mediaId
     * @throws IOException 
     * @throws WeChatException 
     */
    public static void download(BasicAccount account, String mediaId, String filePath) throws IOException, WeChatException {
        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(MEDIA_DOWN_URL);
        param.addParam("access_token", access_token);
        param.addParam("media_id", mediaId);
        HttpClientResponse respone;
        try {
            respone = HttpClientUtil.get(param);
        } catch (HttpClientException e) {
            throw new WeChatException(e);
        }
        File file = new File(filePath);
        FileOutputStream output = FileUtils.openOutputStream(file);
        try {
            IOUtils.copy(respone.getContent(), output);
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(respone.getContent());
        }
    }

    /**
     * 上传LOGO 
     * 1.上传的图片限制文件大小限制1MB，像素为300*300，支持JPG 格式。    2.调用接口获取的logo_url 进支持在微信相关业务下使用，否则会做相应处理
     * @param file 图片文件
     * @return
     * @throws IOException
     */
    /* public static Map<String, Object> uploadimg(File file) throws IOException {
        HttpRequest param = new HttpRequest();
        String access_token = AuthenUtil.getAccessToken();
        param.setUrl(uploadimgUrl + "?access_token=" + access_token);
        HttpResponse respone = HttpClientUtil.upload(param, file);
        Map<String, Object> m = JsonUtil.fromJson(respone.getStringResult());
        return m;
    
    }*/

    /**
     * 上传图文
     * @author guwen
     * @param articles 图文列表
     * @return
     */
    public static Map<String, Object> uploadnews(BasicAccount account, List<Article> articles) {
        Assert.notEmpty(articles, "articles 参数有误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=" + access_token;
        HttpClientRequest param = new HttpClientRequest();
        param.setUrl(url);
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

        return post(param, map);

    }

}