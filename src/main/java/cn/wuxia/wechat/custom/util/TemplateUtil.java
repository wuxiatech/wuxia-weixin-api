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

import cn.wuxia.wechat.WeChatException;
import org.nutz.json.Json;
import org.springframework.util.Assert;

import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.custom.bean.TemplateDataBean;
import cn.wuxia.wechat.miniprogram.bean.ProgramAccount;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 模版信息功能
 * @author guwen
 *
 */
public class TemplateUtil extends BaseUtil {

    private final static String sendUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send";

    /**
     * 发送模版信息
     * @author guwen
     * @param touser 接收者的openid
     * @param templateId 模版id
     * @param url 点击进入的url
     * @param dataList 数据列表
     * @return
     */
    public static Map<String, Object> send(BasicAccount account, String touser, String templateId, String url, List<TemplateDataBean> dataList)
            throws WeChatException {
        return send(account, null, touser, templateId, url, null, dataList);
    }

    /**
     * 发送模版信息
     * @author guwen
     * @param touser 接收者的openid
     * @param templateId 模版id
     * @param url 点击进入的url
     * @param color 模板内容字体颜色，不填默认为黑色
     * @param dataList 数据列表
     *                 "miniprogram":{
    "appid":"xiaochengxuappid12345",
    "pagepath":"index?foo=bar"
    },
     * @return
     */
    public static Map<String, Object> send(BasicAccount account, ProgramAccount programAccount, String touser, String templateId, String url,
            String color, List<TemplateDataBean> dataList) throws WeChatException {
        Assert.hasText(touser, "参数错误 - touser");
        Assert.hasText(templateId, "参数错误 - templateId");
        Assert.notEmpty(dataList, "参数错误 - dataList不能为空");

        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("touser", touser);
        map.put("template_id", templateId);
        if (StringUtil.isNotBlank(url)) {
            map.put("url", url);

        }
        if (StringUtil.isNotBlank(color)) {
            map.put("color", color);
        }

        if (programAccount != null) {
            map.put("miniprogram", BeanUtil.beanToMap(programAccount));
        }
        Map<String, Map<String, String>> data = new HashMap<>();
        for (TemplateDataBean item : dataList) {
            data.put(item.getName(), item.getValueData());
        }
        map.put("data", data);
        System.out.println("data=" + data);
        System.out.println("json data=" + Json.toJson(data));
        return post(sendUrl + "?access_token=" + access_token, map);
    }

}
