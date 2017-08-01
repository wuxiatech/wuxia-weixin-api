package cn.wuxia.wechat.message.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import cn.wuxia.wechat.message.enums.ReplyMsgType;

/**
 * 回复给微信的报文
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class ReplyMessage extends Reply {

    @XmlElement(name = "ToUserName")
    private String toUserName;

    @XmlElement(name = "FromUserName")
    private String fromUserName;

    @XmlElement(name = "CreateTime")
    private Long createTime;

    @XmlElement(name = "MsgType")
    private ReplyMsgType msgType;

    @XmlElement(name = "Content")
    private String content;

    @XmlElement(name = "Image")
    private Image image;

    @XmlElement(name = "Voice")
    private Voice voice;

    @XmlElement(name = "Video")
    private Video video;

    @XmlElement(name = "ArticleCount")
    private Integer articleCount;

    @XmlElement(name = "Music")
    private Music music;

    @XmlElement(name = "Articles")
    private Articles articles;

    public Articles getArticles() {
        return articles;
    }

    public void setArticles(Articles articles) {
        this.articles = articles;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public ReplyMsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(ReplyMsgType msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public Integer getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Integer articleCount) {
        this.articleCount = articleCount;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Image {

        public Image() {
            super();
        }

        public Image(String mediaId) {
            super();
            this.mediaId = mediaId;
        }

        @XmlElement(name = "MediaId")
        private String mediaId;

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Voice {

        @XmlElement(name = "MediaId")
        private String mediaId;

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Video {

        @XmlElement(name = "MediaId")
        private String mediaId;

        @XmlElement(name = "Title")
        private String title;

        @XmlElement(name = "Description")
        private String description;

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Music {

        @XmlElement(name = "Title")
        private String title;

        @XmlElement(name = "Description")
        private String description;

        @XmlElement(name = "MusicUrl")
        private String musicUrl;

        @XmlElement(name = "HQMusicUrl")
        private String hqMusicUrl;

        @XmlElement(name = "ThumbMediaId")
        private String thumbMediaId;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMusicUrl() {
            return musicUrl;
        }

        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
        }

        public String getHqMusicUrl() {
            return hqMusicUrl;
        }

        public void setHqMusicUrl(String hqMusicUrl) {
            this.hqMusicUrl = hqMusicUrl;
        }

        public String getThumbMediaId() {
            return thumbMediaId;
        }

        public void setThumbMediaId(String thumbMediaId) {
            this.thumbMediaId = thumbMediaId;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Articles {

        @XmlElement(name = "item")
        private List<Article> articles;

        public Articles() {
        }

        public Articles(List<Article> articles) {
            this.articles = articles;
        }

        public Articles(Article... articles) {
            this.articles = new ArrayList<ReplyMessage.Articles.Article>();
            for (Article item : articles) {
                this.articles.add(item);
            }
        }

        public List<Article> getArticles() {
            return articles;
        }

        public void setArticles(List<Article> articles) {
            this.articles = articles;
        }

        @XmlAccessorType(XmlAccessType.FIELD)
        public static class Article {

            public Article() {
            }

            public Article(String title, String description, String picUrl, String url) {
                super();
                this.title = title;
                this.description = description;
                this.picUrl = picUrl;
                this.url = url;
            }

            @XmlElement(name = "Title")
            private String title;

            @XmlElement(name = "Description")
            private String description;

            @XmlElement(name = "PicUrl")
            private String picUrl;

            @XmlElement(name = "Url")
            private String url;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

        }

    }

}
