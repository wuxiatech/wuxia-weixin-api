/*
* Created on :2015年7月18日
* Author     :Administrator
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.custom.bean;

/**
 * 微信 图文 news bean
 * [ticket id]
 * Description of the class 
 * @author guwen
 * @ Version : V<Ver.No> <2015年7月18日>
 */
public class Article {
    private String thumbMediaId;

    private String author;

    private String title;

    private String contentSourceUrl;

    private String content;

    private String digest;

    private String showCoverPic;

    private String description;

    private String url;

    private String picurl;

    public Article() {
        super();
    }

    /**
     * 构造一个群发用的图文
     */
    public Article(String thumbMediaId, String author, String title, String contentSourceUrl, String content, String digest, String showCoverPic) {
        super();
        this.thumbMediaId = thumbMediaId;
        this.author = author;
        this.title = title;
        this.contentSourceUrl = contentSourceUrl;
        this.content = content;
        this.digest = digest;
        this.showCoverPic = showCoverPic;
    }

    /**
     * 构造一个客服接口用的图文
     */
    public Article(String title, String description, String url, String picurl) {
        super();
        this.title = title;
        this.description = description;
        this.url = url;
        this.picurl = picurl;
    }

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentSourceUrl() {
        return contentSourceUrl;
    }

    public void setContentSourceUrl(String contentSourceUrl) {
        this.contentSourceUrl = contentSourceUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getShowCoverPic() {
        return showCoverPic;
    }

    public void setShowCoverPic(String showCoverPic) {
        this.showCoverPic = showCoverPic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

}
