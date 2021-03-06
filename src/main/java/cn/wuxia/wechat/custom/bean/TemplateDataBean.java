/*
* Created on :2015年3月3日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.custom.bean;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.collect.Maps;

/**
 * 
 * 微信 模版信息Data 详见
 * https://mp.weixin.qq.com/advanced/tmplmsg?action=faq&token=569006858&lang=zh_CN
 * 
 * name 对应 data的键值
 * 
 * @author guwen
 */
public class TemplateDataBean {

    private String name;

    private String value;

    private String color;

    public TemplateDataBean() {
    }

    public TemplateDataBean(String name, String value, String color) {
        super();
        this.name = name;
        this.value = value;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Map<String, String> getValueData() {
        Map<String, String> valueMap = Maps.newHashMap();
        valueMap.put("value", getValue());
        valueMap.put("color", getColor() == null ? "" : getColor());
        return valueMap;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
