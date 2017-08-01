/*
* Created on :9 Oct, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.shakearound.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.jdom2.JDOMException;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.tencent.common.HttpsRequest;

import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.MD5Util;
import cn.wuxia.common.util.NumberUtil;
import cn.wuxia.common.util.ServletUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.XMLUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.wechat.PayAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.pay.util.PayUtil;
import cn.wuxia.wechat.shakearound.bean.PackBean;

public class CashCoupon extends PayUtil {
    //发送红包名称
    private static String PACKAGE_MCH = properties.getProperty("PACKAGE_MCH");

    /**
     * 普通红包
     */
    private final static String CREATE_PACKAGE = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";

    /**
     * 裂变红包
     */
    private final static String CREATE_GROUP_PACKAGE = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendgroupredpack";

    /**
     * 裂变红包
     * ex. CashCoupon.createOneGroupPackage("oqJ0Ft94bzT2Or6L7wjDGGQu3zzg", 1200, 9, "测试红包", "分豆豆裂变红包", "我就不告诉你怎么玩，快快关注分豆豆领更多红包");
     * @author songlin
     * @param openId
     * @param amount 单位分
     * @param totalPackage 红包发放总人数
     * @param wish 红包祝福语 
     * @param action 活动名称
     * @param remark
     */
    public static boolean createOneGroupPackage(PayAccount account, String openId, int amount, int totalPackage, String wish, String action,
            String remark) throws WeChatException{
        Map<String, Object> packageParams = Maps.newLinkedHashMap();

        //商户订单号（每个订单号必须唯一）组成： mch_id+yyyymmdd+10位一天内不能重复的数字
        String billno = account.getPartner() + DateUtil.dateToString(new Date(), DateUtil.DateFormatter.FORMAT_YYYYMMDDHHMMSS)
                + RandomStringUtils.randomNumeric(4);
        packageParams.put("mch_billno", billno);
        packageParams.put("mch_id", account.getPartner());
        packageParams.put("wxappid", account.getAppid());
        //发送红包名称
        Assert.isTrue(PACKAGE_MCH.length() < 32);
        packageParams.put("send_name", PACKAGE_MCH);
        packageParams.put("re_openid", openId);
        //红包发放总金额，即一组红包金额总和，包括分享者的红包和裂变的红包，单位分
        packageParams.put("total_amount", amount);
        //红包发放总人数，即总共有多少人可以领到该组红包（包括分享者
        Assert.isTrue(totalPackage >= 3 && totalPackage <= 20, "红包发放总人数应在3-20人间");
        packageParams.put("total_num", totalPackage);
        Assert.isTrue(NumberUtil.compare(NumberUtil.divide(amount, totalPackage, 2), 100) > 0, "每人的红包应大于100分，即amount>=" + totalPackage * 100);
        //全部随机,商户指定总金额和红包发放总人数，由微信支付随机计算出各红包金额
        packageParams.put("amt_type", "ALL_RAND");
        //红包祝福语 
        Assert.isTrue(wish.length() < 128);
        packageParams.put("wishing", wish);
        //活动名称 
        Assert.isTrue(action.length() < 32);
        packageParams.put("act_name", action);
        Assert.isTrue(remark.length() < 256);
        packageParams.put("remark", remark);//随机字符串，不长于32位 
        packageParams.put("nonce_str", RandomStringUtils.randomAlphanumeric(32));
        TreeMap<String, Object> orderParms = Maps.newTreeMap();
        orderParms.putAll(packageParams);
        String parm = ServletUtils.getUrlParamsByMap(orderParms);
        parm += "&key=" + account.getAppKey();
        System.out.println(parm);
        String sign = MD5Util.MD5HexEncode(parm, "UTF-8").toUpperCase();
        Map<String, Object> m = Maps.newLinkedHashMap();

        m.put("sign", sign);
        m.putAll(packageParams);
        PackBean postXml = (PackBean) BeanUtil.mapToBean(m, PackBean.class);
        String result = "";
        try {
            HttpsRequest param1 = new HttpsRequest();
            result = param1.sendPost(CREATE_GROUP_PACKAGE, postXml);
        } catch (UnrecoverableKeyException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            throw new WeChatException("", e);
        }
        Map<String, String> rs = Maps.newHashMap();
        try {
            rs = XMLUtil.doXMLParse(result);
        } catch (JDOMException | IOException e) {
            logger.error("", e);
        }
        logger.info("" + rs.get("return_msg"));
        if (StringUtil.equalsIgnoreCase("SUCCESS", rs.get("return_code"))) {
            return true;
        } else {
            throw new WeChatException(rs.get("return_msg"));
        }
    }

    /**
     * 普通红包
     * ex. CashCoupon.createOnePackage("oqJ0Ft94bzT2Or6L7wjDGGQu3zzg", 1200, 1, "测试红包", "分豆豆红包", "我就不告诉你怎么玩，快快关注分豆豆领更多红包");
     * @author songlin
     * @param openId
     * @param amount 单位分
     * @param totalPackage 红包发放总人数
     * @param wish 红包祝福语 
     * @param action 活动名称
     * @param remark
     */
    public static boolean createOnePackage(PayAccount account, String openId, int amount, int totalPackage, String wish, String action,
            String remark) throws WeChatException{
        Map<String, Object> packageParams = Maps.newLinkedHashMap();
        //商户订单号（每个订单号必须唯一）组成： mch_id+yyyymmdd+10位一天内不能重复的数字
        String billno = account.getPartner() + DateUtil.dateToString(new Date(), DateUtil.DateFormatter.FORMAT_YYYYMMDDHHMMSS)
                + RandomStringUtils.randomNumeric(4);
        packageParams.put("mch_billno", billno);
        packageParams.put("mch_id", account.getPartner());
        packageParams.put("wxappid", account.getAppid());
        //发送红包名称
        Assert.isTrue(PACKAGE_MCH.length() < 32);
        packageParams.put("send_name", PACKAGE_MCH);
        packageParams.put("re_openid", openId);
        //红包发放总金额，即一组红包金额总和，包括分享者的红包和裂变的红包，单位分
        packageParams.put("total_amount", amount);
        //红包发放总人数，即总共有多少人可以领到该组红包（包括分享者
        packageParams.put("total_num", totalPackage);
        //红包祝福语 
        Assert.isTrue(wish.length() < 128);
        packageParams.put("wishing", wish);
        packageParams.put("client_ip", "183.6.128.162");
        //全部随机,商户指定总金额和红包发放总人数，由微信支付随机计算出各红包金额
        //packageParams.put("amt_type", "ALL_RAND");
        //活动名称 
        Assert.isTrue(action.length() < 32);
        packageParams.put("act_name", action);
        Assert.isTrue(remark.length() < 256);
        packageParams.put("remark", remark);//随机字符串，不长于32位 
        packageParams.put("nonce_str", RandomStringUtils.randomAlphanumeric(32));

        TreeMap<String, Object> orderParms = Maps.newTreeMap();
        orderParms.putAll(packageParams);
        String parm = ServletUtils.getUrlParamsByMap(orderParms);
        parm += "&key=" + account.getAppKey();
        System.out.println(parm);
        String sign = MD5Util.MD5HexEncode(parm, "UTF-8").toUpperCase();
        Map<String, Object> m = Maps.newLinkedHashMap();

        m.put("sign", sign);
        m.putAll(packageParams);
        PackBean postXml = (PackBean) BeanUtil.mapToBean(m, PackBean.class);
        String result = "";
        try {
            HttpsRequest param1 = new HttpsRequest();
            result = param1.sendPost(CREATE_PACKAGE, postXml);
            System.out.println("返回结果：" + result);
        } catch (UnrecoverableKeyException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e) {
            throw new WeChatException("", e);
        }
        Map<String, String> rs = Maps.newHashMap();
        try {
            rs = XMLUtil.doXMLParse(result);
        } catch (JDOMException | IOException e) {
            logger.error("", e);
        }
        logger.info("" + rs.get("return_msg"));
        if (StringUtil.equalsIgnoreCase("SUCCESS", rs.get("return_code"))) {
            return true;
        } else {
            throw new WeChatException(rs.get("return_msg"));
        }

    }

    //输出XML
    private static String parseXML(Map<String, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (StringUtil.isNotBlank(v)) {
                if (NumberUtil.isNumber(v.toString())) {
                    sb.append("<" + k + ">" + v + "</" + k + ">\n");
                } else {
                    sb.append("<" + k + "><![CDATA[" + v + "]]></" + k + ">\n");
                }
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

}
