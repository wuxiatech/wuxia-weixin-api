/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.qrcode.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.img.ImageUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 二维码功能
 * @author guwen
 *
 */
public class QrcodeUtil extends BaseUtil {

    private final static String showqrcodeUrl = properties.getProperty("QRCODE.SHOWQRCODE");

    /**
     * 通过ticket换取二维码
     * @param ticket ticket
     * @param scentId 场景值ID
     * @param filePath 存储图片的地址
     * @param waterPath 如果有水印则填写水印地址支持（http及本地）
     * @return 返回二维码图片地址
     * @throws UnsupportedEncodingException 
     */
    public static File showqrcode(String ticket, String scentId, String waterPath) throws Exception {
        ticket = URLEncoder.encode(ticket, "UTF-8");
        String downUrl = showqrcodeUrl + "?ticket=" + ticket;
        File qrcodeFile = HttpClientUtil.download(new HttpClientRequest(downUrl),
                System.getProperty("java.io.tmpdir") + File.separator + StringUtil.random(5) + scentId + ".jpg");
        if (StringUtil.isNotBlank(waterPath)) {
            File warterFile = null;
            if (StringUtil.startsWith(waterPath, "http")) {
                warterFile = HttpClientUtil.download(new HttpClientRequest(waterPath));
            } else {
                warterFile = new File(waterPath);
            }
            //缩小图片
            ImageUtil.scale3(warterFile.getPath(), warterFile.getPath(), 120, false);
            //添加水印
            ImageUtil.pressImage(warterFile.getPath(), qrcodeFile.getPath(), qrcodeFile.getPath(), 0, 0, 1);
        }
        return qrcodeFile;
    }

    /**
     * 生成临时二维码
     * @author songlin.li
     * @param sceneId 1-100000的数字
     * @return
     */
    public static Map<String, Object> createTemp(BasicAccount account, int expireSeconds, int sceneId) {
        Assert.isTrue(sceneId >= 1 && sceneId <= 100000, "sceneId 必须为1-100000的数字");
        Assert.isNull(expireSeconds >= 30 && expireSeconds <= 2592000, "expireSeconds必须在30秒到30天之间");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("action_name", "QR_SCENE");
        map.put("expire_seconds", expireSeconds);
        Map<String, Object> scene = new HashMap<String, Object>();
        scene.put("scene_id", sceneId);
        Map<String, Object> action_info = new HashMap<String, Object>();
        action_info.put("scene", scene);
        map.put("action_info", action_info);
        //发送
        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + access_token);
        return post(param, map);
    }

    /**
     * 生成永久二维码
     * @author guwen
     * @param sceneId 1-100000的数字
     * @return
     */
    public static Map<String, Object> createLimit(BasicAccount account, int sceneId) {
        Assert.isTrue(sceneId >= 1 && sceneId <= 100000, "sceneId 必须为1-100000的数字");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("action_name", "QR_LIMIT_SCENE");

        Map<String, Object> scene = new HashMap<String, Object>();
        scene.put("scene_id", sceneId);
        Map<String, Object> action_info = new HashMap<String, Object>();
        action_info.put("scene", scene);
        map.put("action_info", action_info);
        //发送
        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + access_token);
        return post(param, map);
    }

}
