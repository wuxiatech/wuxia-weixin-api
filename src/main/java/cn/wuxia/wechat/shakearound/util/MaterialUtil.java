/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.shakearound.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.util.Assert;

import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * 素材Util 主要用于微信摇一摇
 * @author guwen
 */
public class MaterialUtil extends BaseUtil {

    private final static String addUrl = properties.getProperty("shakearound.material.add");

    /**
     * 上传图片素材
     * @param imgPath 图片文件地址
     * @return
     * @throws IOException
     */
    public static Map<String, Object> add(BasicAccount account, File file) throws IOException {
        Assert.notNull(file, "file 不能为空!");
        Assert.isTrue(file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png")
                || file.getName().endsWith(".gif"), "文件格式不正确");

        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(addUrl + "?access_token=" + access_token);
        param.addParam("media", file);
        HttpClientResponse respone;
        try {
            respone = HttpClientUtil.upload(param);
        } catch (HttpClientException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
        Map<String, Object> m = JsonUtil.fromJson(respone.getStringResult());
        return m;
    }

}
