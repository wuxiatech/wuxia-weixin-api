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
import java.util.Map;

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
import cn.wuxia.wechat.media.bean.MaterialMediaResult;
import cn.wuxia.wechat.media.enums.MaterialMediaTypeEnum;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * [ticket id]
 * 新增临时素材
 * @author songlin
 * @ Version : V<Ver.No> <7 Apr, 2015>
 */
public class MaterialMediaUtil extends BaseUtil {

    static String MEDIA_UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/material/add_material";

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
    * 上传缩略图64K
    * @author songlin
    * @param mediaId
    */
    public static MaterialMediaResult uploadThumb(BasicAccount account, File media) throws WeChatException {
        return upload(account, media, MaterialMediaTypeEnum.thumb);
    }

}
