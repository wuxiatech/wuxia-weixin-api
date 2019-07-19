/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.media.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FilenameUtils;

import cn.wuxia.common.web.httpclient.HttpAction;
import cn.wuxia.common.web.httpclient.HttpClientMethod;
import cn.wuxia.common.web.httpclient.HttpClientUtil;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.WechatHttpRequest;

/**
 * 
 * [ticket id]
 * 新增临时素材
 * @author songlin
 * @ Version : V<Ver.No> <7 Apr, 2015>
 */
public class MediaUtil extends BaseUtil {

    private final static HttpAction IMAGE_UPLOAD = HttpAction.Action("https://api.weixin.qq.com/cgi-bin/media/uploadimg", HttpClientMethod.POST);

    /**
     * 获取多媒体资源
     * @author songlin
     * @param mediaId
     * @throws IOException 
     */
    @Deprecated
    public static void download(BasicAccount account, String mediaId, String filePath) throws IOException, WeChatException {
        TempMediaUtil.download(account, mediaId, filePath);
    }

    /**
     * 上传微信使用的其它图片，不受数量限制
     * 1.上传的图片限制文件大小限制1MB，像素为300*300，支持jpg/png 格式。
     * 2.调用接口获取的logo_url 进支持在微信相关业务下使用，否则会做相应处理
     * @param file 图片文件
     * @return
     * @throws IOException
     */
    public static String uploadimg(BasicAccount account, File file) throws IOException, WeChatException {
        Map m = WechatHttpRequest.build(account, IMAGE_UPLOAD).addParam("media", file).execute();
        String url = MapUtils.getString(m, "url");
        logger.info("target url:{}", url);
        return url;
    }

    /**
     * 上传微信使用的其它图片，不受数量限制
     * 1.上传的图片限制文件大小限制1MB，像素为300*300，支持jpg/png 格式。
     * 2.调用接口获取的logo_url 进支持在微信相关业务下使用，否则会做相应处理
     * @param fileurl 图片地址
     * @return
     * @throws IOException
     */
    public static String uploadimg(BasicAccount account, String fileurl) throws IOException, WeChatException {
        logger.info("source url:{}", fileurl);
        String tempfile = System.getProperty("java.io.tmpdir") + File.separator + FilenameUtils.getName(fileurl);
        File file = HttpClientUtil.download(fileurl, tempfile);
        return uploadimg(account, file);
    }
}
