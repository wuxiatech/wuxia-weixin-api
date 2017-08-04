/*
* Created on :2015年7月15日
* Author     :Administrator
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.message.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.google.common.collect.Lists;

import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.custom.util.MessageUtil;
import cn.wuxia.wechat.message.bean.Receive;
import cn.wuxia.wechat.message.bean.Reply;
import cn.wuxia.wechat.message.bean.ReplyMessage;
import cn.wuxia.wechat.message.bean.ReplyMessage.Articles.Article;
import cn.wuxia.wechat.message.util.jaxb.CDataXMLStreamWriter;

/**
 * 消息工具
 *
 */
public class DTSUtil {

    /**
     * xml转ReceiveMessage对象
     * 
     * @throws JAXBException
     */
    public static Receive xml2receive(String xml, Class<?> clazz) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader sr = new StringReader(xml);
        Receive message = (Receive) unmarshaller.unmarshal(sr);
        return message;
    }

    /**
     * 把响应给微信的对象转换为xml
     */
    public static String reply2xml(Reply reply) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(reply.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // 去掉生成xml的默认报文头  
//        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        XMLStreamWriter streamWriter = null;
        CDataXMLStreamWriter cdataStreamWriter = null;
        String xml = null;
        try {
            StringWriter writer = new StringWriter();
            streamWriter = xof.createXMLStreamWriter(writer);
            cdataStreamWriter = new CDataXMLStreamWriter(streamWriter);
            marshaller.marshal(reply, cdataStreamWriter);
            xml = writer.toString();
            cdataStreamWriter.flush();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
            try {
                cdataStreamWriter.close();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }

        }
        return xml.replace("standalone=\"yes\"", "");

    }

    /**
     * 把响应给微信的对象转换为xml
     */
    public static void reply2message(BasicAccount account, ReplyMessage reply) throws Exception {
        switch (reply.getMsgType()) {
            case image:
                MessageUtil.customSendImage(account, reply.getToUserName(), reply.getImage().getMediaId());
                break;
            case music:
                break;
            case news:
                List<cn.wuxia.wechat.custom.bean.Article> lists = Lists.newArrayList();
                for (Article article : reply.getArticles().getArticles()) {
                    cn.wuxia.wechat.custom.bean.Article art = new cn.wuxia.wechat.custom.bean.Article();
                    art.setTitle(article.getTitle());
                    art.setDescription(article.getDescription());
                    art.setUrl(article.getUrl());
                    art.setPicurl(article.getPicUrl());
                    lists.add(art);
                }
                MessageUtil.customSendNews(account, reply.getToUserName(), lists);
                break;
            case text:
                MessageUtil.customSendText(account, reply.getToUserName(), reply.getContent());
                break;
            case video:
                break;
            case voice:
                break;
            default:
                break;

        }
        System.out.println("hhh");
    }
}
