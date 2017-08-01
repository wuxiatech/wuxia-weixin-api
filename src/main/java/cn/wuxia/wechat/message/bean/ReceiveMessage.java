package cn.wuxia.wechat.message.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import cn.wuxia.wechat.message.enums.ReceiveEvent;
import cn.wuxia.wechat.message.enums.ReceiveMsgType;

/**
 * 接收到的微信消息
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class ReceiveMessage extends Receive {

    @XmlElement(name = "ToUserName")
    private String toUserName;

    @XmlElement(name = "FromUserName")
    private String fromUserName;

    @XmlElement(name = "CreateTime")
    private Long createTime;

    @XmlElement(name = "MsgType")
    private ReceiveMsgType msgType;

    @XmlElement(name = "Content")
    private String content;

    @XmlElement(name = "MsgId")
    private String msgId;

    @XmlElement(name = "PicUrl")
    private String picUrl;

    @XmlElement(name = "MediaId")
    private String mediaId;

    @XmlElement(name = "Format")
    private String format;

    @XmlElement(name = "ThumbMediaId")
    private String thumbMediaId;

    @XmlElement(name = "Location_X")
    private String locationX;

    @XmlElement(name = "Location_Y")
    private String locationY;

    @XmlElement(name = "Scale")
    private String scale;

    @XmlElement(name = "Label")
    private String label;

    @XmlElement(name = "Title")
    private String title;

    @XmlElement(name = "Description")
    private String description;

    @XmlElement(name = "Url")
    private String url;

    @XmlElement(name = "Event")
    private ReceiveEvent event;

    @XmlElement(name = "EventKey")
    private String eventKey;

    @XmlElement(name = "Ticket")
    private String ticket;

    @XmlElement(name = "Latitude")
    private String latitude;

    @XmlElement(name = "Longitude")
    private String longitude;

    @XmlElement(name = "Precision")
    private String precision;

    @XmlElement(name = "CardId")
    private String cardId;

    @XmlElement(name = "FriendUserName")
    private String friendUserName;

    @XmlElement(name = "IsGiveByFriend")
    private Integer isGiveByFriend;

    @XmlElement(name = "UserCardCode")
    private String userCardCode;

    @XmlElement(name = "OldUserCardCode")
    private String oldUserCardCode;

    @XmlElement(name = "OuterId")
    private String outerId;

    @XmlElement(name = "BindTime")
    private Long bindTime;

    @XmlElement(name = "LotteryId")
    private String lotteryid;

    public String getFriendUserName() {
        return friendUserName;
    }

    public void setFriendUserName(String friendUserName) {
        this.friendUserName = friendUserName;
    }

    public Integer getIsGiveByFriend() {
        return isGiveByFriend;
    }

    public void setIsGiveByFriend(Integer isGiveByFriend) {
        this.isGiveByFriend = isGiveByFriend;
    }

    public String getUserCardCode() {
        return userCardCode;
    }

    public void setUserCardCode(String userCardCode) {
        this.userCardCode = userCardCode;
    }

    public String getOldUserCardCode() {
        return oldUserCardCode;
    }

    public void setOldUserCardCode(String oldUserCardCode) {
        this.oldUserCardCode = oldUserCardCode;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public ReceiveMsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(ReceiveMsgType msgType) {
        this.msgType = msgType;
    }

    public ReceiveEvent getEvent() {
        return event;
    }

    public void setEvent(ReceiveEvent event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "Message [toUserName=" + toUserName + ", fromUserName=" + fromUserName + ", createTime=" + createTime + ", msgType=" + msgType
                + ", content=" + content + ", msgId=" + msgId + ", picUrl=" + picUrl + ", mediaId=" + mediaId + ", format=" + format
                + ", thumbMediaId=" + thumbMediaId + ", locationX=" + locationX + ", locationY=" + locationY + ", scale=" + scale + ", label=" + label
                + ", title=" + title + ", description=" + description + ", url=" + url + ", event=" + event + ", eventKey=" + eventKey + ", ticket="
                + ticket + ", latitude=" + latitude + ", longitude=" + longitude + ", precision=" + precision + "]";
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Long getBindTime() {
        return bindTime;
    }

    public void setBindTime(Long bindTime) {
        this.bindTime = bindTime;
    }

    public String getLotteryid() {
        return lotteryid;
    }

    public void setLotteryid(String lotteryid) {
        this.lotteryid = lotteryid;
    }

}
