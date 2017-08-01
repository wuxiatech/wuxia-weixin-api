/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.open.util;

import java.io.IOException;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.google.common.collect.Maps;

import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.message.aes.AesException;
import cn.wuxia.wechat.message.aes.WXBizMsgCrypt;
import cn.wuxia.wechat.message.bean.ReceiveMessage;
import cn.wuxia.wechat.message.bean.ReplyMessage;
import cn.wuxia.wechat.message.enums.ReceiveMsgType;
import cn.wuxia.wechat.message.enums.ReplyMsgType;
import cn.wuxia.wechat.message.util.DTSUtil;

/**
 * 
 * 用于全网发布工具
 * @author guwen
 * @ Version : V<Ver.No> <7 Apr, 2015>
 */
public class FullWebUtil extends ThirdBaseUtil {

    private final static String fullWebAppid = properties.getProperty("FULL_WEB_APPID");

    private final static String fullWebUsername = properties.getProperty("FULL_WEB_USERNAME");

    /**
     * 判断公众号是否全网发布测试号
     * @author guwen
     * @param fullWebAppid 
     * @param fullWebUsername
     * @return 是反回true
     */
    public static boolean checkAccount(String appid, String username) {
        if (appid != null) {
            return appid.equals(fullWebAppid);
        }
        if (username != null) {
            return username.equals(fullWebUsername);
        }
        return false;
    }

    /**
     * 处理全网发布审核请求
     * @author guwen 微信推送的xml
     * @param xml
     * @return
     * @throws AesException 
     * @throws JAXBException 
     * @throws IOException 
     * @throws WeChatException 
     */
    public static String dealWechat(ReceiveMessage receive, String timestamp, String nonce)
            throws AesException, JAXBException, IOException, WeChatException {
        String account = receive.getToUserName(); //开发者微信号
        String openid = receive.getFriendUserName(); //发送方帐号
        if ("event".equals(receive.getMsgType().toString())) {
            logger.info("处理事件信息");
            String event = receive.getEvent().toString();
            ReplyMessage reply = new ReplyMessage();
            reply.setToUserName(openid);
            reply.setFromUserName(account);
            reply.setCreateTime(System.currentTimeMillis());
            reply.setMsgType(ReplyMsgType.text);
            reply.setContent(event + "from_callback");

            String replyXml = DTSUtil.reply2xml(reply);
            logger.info("reply: " + replyXml);
            WXBizMsgCrypt pc = new WXBizMsgCrypt(ThirdBaseUtil.OPEN_TOKEN, ThirdBaseUtil.OPEN_ENCODING_AES_KEY, ThirdBaseUtil.OPEN_APPID);
            replyXml = pc.encryptMsg(replyXml, timestamp, nonce);
            logger.info("加密后: " + replyXml);
            return replyXml;

        } else if (ReceiveMsgType.text.equals(receive.getMsgType())) {
            logger.info("处理文本信息");
            String content = receive.getContent();
            if ("TESTCOMPONENT_MSG_TYPE_TEXT".equals(content)) {
                ReplyMessage reply = new ReplyMessage();
                reply.setToUserName(openid);
                reply.setFromUserName(account);
                reply.setCreateTime(System.currentTimeMillis());
                reply.setMsgType(ReplyMsgType.text);
                reply.setContent("TESTCOMPONENT_MSG_TYPE_TEXT_callback");

                String replyXml = DTSUtil.reply2xml(reply);
                logger.info("reply : " + replyXml);
                WXBizMsgCrypt pc = new WXBizMsgCrypt(ThirdBaseUtil.OPEN_TOKEN, ThirdBaseUtil.OPEN_ENCODING_AES_KEY, ThirdBaseUtil.OPEN_APPID);
                replyXml = pc.encryptMsg(replyXml, timestamp, nonce);
                logger.info("加密后: " + replyXml);
                return replyXml;

            } else if (content.indexOf("QUERY_AUTH_CODE:") >= 0) {
                String queryAuthCode = content.replace("QUERY_AUTH_CODE:", "");
                Map<String, Object> map = ProxyOAuthUtil.apiQueryAuth(queryAuthCode);
                logger.info("测试公众号的授权信息" + map);
                Map<String, Object> authorization_info = (Map<String, Object>) map.get("authorization_info");
                String authorizerAccessToken = (String) authorization_info.get("authorizer_access_token");

                HttpClientRequest param = new HttpClientRequest();
                param.setUrl("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + authorizerAccessToken);
                Map<String, Object> pmap = Maps.newHashMap();
                pmap.put("touser", openid);
                pmap.put("msgtype", "text");
                Map<String, String> contentm = Maps.newHashMap();
                contentm.put("content", queryAuthCode + "_from_api");
                pmap.put("text", contentm);
                Map<String, Object> result = post(param, pmap);
                logger.info("微信返回" + result);
                return "";
            }

        }

        return null;
    }

}
