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

import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.custom.bean.TemplateDataBean;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 模版信息功能
 * @author guwen
 *
 */
public class TemplateUtil extends BaseUtil {

    private final static String sendUrl = properties.getProperty("template.send");

    /**
     * 发送模版信息
     * @author guwen
     * @param touser 接收者的openid
     * @param templateId 模版id
     * @param url 点击进入的url
     * @param topcolor 顶部颜色
     * @param dataList 数据列表
     * @return
     */
    public static Map<String, Object> send(BasicAccount account, String touser, String templateId, String url, String topcolor,
            List<TemplateDataBean> dataList) {
        Assert.hasText(touser, "参数错误 - touser");
        Assert.hasText(templateId, "参数错误 - templateId");
        Assert.hasText(topcolor, "参数错误 - topcolor");
        Assert.notEmpty(dataList, "参数错误 - dataList不能为空");

        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(sendUrl + "?access_token=" + access_token);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("touser", touser);
        map.put("template_id", templateId);
        if (StringUtil.isNotBlank(url)) {
            map.put("url", url);

        }
        map.put("topcolor", topcolor);

        Map<String, Map<String, String>> data = new HashMap<>();
        for (TemplateDataBean item : dataList) {
            Map<String, String> m = new HashMap<>();
            m.put("value", item.getValue());
            m.put("color", item.getColor());
            data.put(item.getName(), m);
        }
        map.put("data", data);

        return post(param, map);
    }

}
