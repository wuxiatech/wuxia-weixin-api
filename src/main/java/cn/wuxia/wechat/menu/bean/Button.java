/*
* Created on :2015年7月18日
* Author     :Administrator
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.menu.bean;

import java.util.ArrayList;
import java.util.List;

import cn.wuxia.wechat.menu.enums.ButtonType;

/**
 * 微信 菜单按钮
 * @author guwen
 */
public class Button {
    private ButtonType type;

    private String name;

    private String key;

    private String url;

    private String media_id;

    private List<Button> sub_button = new ArrayList<>();

    public Button(String name, List<Button> sub_button) {
        super();
        this.name = name;
        this.sub_button = sub_button;
    }

    public Button(String name, Button... buttons) {
        super();
        this.name = name;
        for (Button b : buttons) {
            this.sub_button.add(b);
        }
    }

    public Button(ButtonType type, String name, String key, String url) {
        super();
        this.type = type;
        this.name = name;
        this.key = key;
        this.url = url;
    }

    public Button() {
        super();
    }

    public ButtonType getType() {
        return type;
    }

    public void setType(ButtonType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Button> getSub_button() {
        return sub_button;
    }

    public void setSub_button(List<Button> sub_button) {
        this.sub_button = sub_button;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

}
