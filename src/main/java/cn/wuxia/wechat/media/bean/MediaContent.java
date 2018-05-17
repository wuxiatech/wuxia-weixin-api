package cn.wuxia.wechat.media.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import cn.wuxia.common.util.ListUtil;
import cn.wuxia.wechat.custom.bean.Article;
import cn.wuxia.wechat.message.bean.Reply;
import cn.wuxia.wechat.message.enums.ReplyMsgType;


public class MediaContent {

    private String media_id;

    private List<Article> content;

    private String update_time;

    private String name;

    private String url;


    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public List<Article> getContent() {
        return content;
    }

    public void setContent(List<Article> content) {
        this.content = content;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
