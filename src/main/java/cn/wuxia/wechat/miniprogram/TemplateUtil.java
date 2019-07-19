/*
 * Created on :7 Apr, 2015
 * Author     :songlin
 * Change History
 * Version       Date         Author           Reason
 * <Ver.No>     <date>        <who modify>       <reason>
 * Copyright 2014-2020 songlin.li All right reserved.
 */
package cn.wuxia.wechat.miniprogram;

import cn.wuxia.common.util.StringUtil;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.custom.bean.TemplateDataBean;
import cn.wuxia.wechat.token.util.TokenUtil;
import org.nutz.json.Json;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模版信息功能
 *
 * @author guwen
 */
public class TemplateUtil extends BaseUtil {

    private final static String sendUrl = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send";

    /**
     * 发送模版信息
     *
     * @param touser     接收者的openid
     * @param templateId 模版id
     * @param page       点击进入的页面路径
     * @param formid     表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id
     * @param dataList   数据列表
     *                   "miniprogram":{
     *                   "appid":"xiaochengxuappid12345",
     *                   "pagepath":"index?foo=bar"
     *                   },
     * @return
     * @author guwen
     */
    public static Map<String, Object> send(BasicAccount account, String touser, String templateId, String page,
                                           String formid, List<TemplateDataBean> dataList) throws WeChatException {
        Assert.hasText(touser, "参数错误 - touser");
        Assert.hasText(templateId, "参数错误 - templateId");
        Assert.hasText(formid, "参数错误 - formid");
        Assert.notEmpty(dataList, "参数错误 - dataList不能为空");

        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("touser", touser);
        map.put("template_id", templateId);
        map.put("form_id", formid);
        if (StringUtil.isNotBlank(page)) {
            map.put("page", page);

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
