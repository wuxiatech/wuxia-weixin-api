package cn.wuxia.wechat.message.bean;

import cn.wuxia.wechat.message.enums.ReplyMsgType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 回复给微信的报文
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class TransferCustomerReplyMessage extends Reply {

    @XmlElement(name = "ToUserName")
    private String toUserName;

    @XmlElement(name = "FromUserName")
    private String fromUserName;

    @XmlElement(name = "CreateTime")
    private Long createTime;

    @XmlElement(name = "MsgType")
    private ReplyMsgType msgType;


    @XmlElement(name = "TransInfo")
    private TransInfo transInfo;


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

    public TransInfo getTransInfo() {
        return transInfo;
    }

    public void setTransInfo(TransInfo transInfo) {
        this.transInfo = transInfo;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TransInfo {

        public TransInfo() {
            super();
        }

        public TransInfo(String kefuAccount) {
            super();
            this.kefuAccount = kefuAccount;
        }

        @XmlElement(name = "KfAccount")
        private String kefuAccount;

        public void setKefuAccount(String kefuAccount) {
            this.kefuAccount = kefuAccount;
        }

        public String getKefuAccount() {
            return kefuAccount;
        }
    }


}
