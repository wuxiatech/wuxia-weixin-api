package cn.wuxia.wechat.pay.util;

import cn.wuxia.common.exception.ValidateException;
import cn.wuxia.common.util.*;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.web.httpclient.HttpAction;
import cn.wuxia.common.web.httpclient.HttpClientMethod;
import cn.wuxia.common.xml.Dom4jXmlUtil;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.PayAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.pay.bean.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.dom4j.DocumentException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public class RedPackUtil extends BaseUtil {
    private final static HttpAction sendRedpackUrl = HttpAction.Action("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack",
            HttpClientMethod.POST);

    private final static HttpAction sendGroupRedpackUrl = HttpAction.Action("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendgroupredpack",
            HttpClientMethod.POST);

    private final static HttpAction getRedpackInfoUrl = HttpAction.Action("https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo",
            HttpClientMethod.POST);

    public static SendRedPackResult send(PayAccount payAccount, RedPackBean redPack) throws WeChatException {

        return send(payAccount, sendRedpackUrl, redPack);
    }

    public static SendRedPackResult sendGroup(PayAccount payAccount, RedPackBean redPack) throws WeChatException {

        return send(payAccount, sendGroupRedpackUrl, redPack);
    }

    private static SendRedPackResult send(PayAccount payAccount, HttpAction action, RedPackBean redPack) throws WeChatException {
        SendRedPackBean sendRedPackBean = action.equals(sendGroupRedpackUrl) ? new SendGroupRedPackBean() : new SendRedPackBean();
        BeanUtil.copyProperties(sendRedPackBean, redPack);
        if (StringUtil.isBlank(sendRedPackBean.getMch_billno())) {
            sendRedPackBean.setMch_billno(NoGenerateUtil.generateNo(payAccount.getPartner(), 18));
        }
        sendRedPackBean.setClient_ip(SystemUtil.getOSIpAddr());
        sendRedPackBean.setNonce_str(PaySignUtil.getNonceStr());
        sendRedPackBean.setMch_id(payAccount.getPartner());
        sendRedPackBean.setWxappid(payAccount.getAppid());
        if (sendRedPackBean.getTotal_num() < 1) {
            sendRedPackBean.setTotal_num(1);
        }
        DecimalFormat df = new DecimalFormat("######.##");
        String financingNum = df.format(redPack.getAmount());
        logger.info("amount:{}", financingNum);
        sendRedPackBean.setTotal_amount(NumberUtil.toInt(PayUtil.changeY2F(financingNum)));
        if (sendRedPackBean.getTotal_amount() / sendRedPackBean.getTotal_num() < 100) {
            throw new WeChatException("每个红包的平均金额必须在1.00元到200.00元之间");
        }
        if (sendRedPackBean.getTotal_amount() > 20000) {
            sendRedPackBean.setScene_id("PRODUCT_5");
        }
        String sign = PaySignUtil.createSign(payAccount, new TreeMap<>(BeanUtil.beanToMap(sendRedPackBean)));
        sendRedPackBean.setSign(sign);
        String result = "";
        try {
            logger.info(ToStringBuilder.reflectionToString(sendRedPackBean));
            ValidatorUtil.validate(sendRedPackBean);
            result = PayHttpsRequest.init(payAccount).send(action, sendRedPackBean);
        } catch (IOException | KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("", e);
            throw new WeChatException("初始化证书出错。", e);
        } catch (ValidateException e1) {
            logger.error("", e1);
            throw new WeChatException("参数信息有误。", e1);
        }
        try {
            Map<String, Object> resultMap = Dom4jXmlUtil.xml2map(result, false);
            if (StringUtil.equalsIgnoreCase("FAIL", MapUtil.getString(resultMap, "return_code"))
                    || StringUtil.equalsIgnoreCase("FAIL", MapUtil.getString(resultMap, "result_code"))) {
                throw new WeChatException(MapUtil.getString(resultMap, "return_msg") + "" + MapUtil.getString(resultMap, "err_code_des"));
            }
            return MapUtil.mapToBean(resultMap, SendRedPackResult.class);
        } catch (DocumentException e) {
            logger.warn("xml解析有误", e);
            throw new WeChatException("xml解析有误。");
        }

    }


    /**
     * 根据商户订单号查找红包记录
     *
     * @param payAccount
     * @param mchBillno
     * @return
     * @throws WeChatException
     */
    public static GetRedPackInfoResult getInfo(PayAccount payAccount, String mchBillno) throws WeChatException {
        GetRedPackInfoBean getRedPackBean = new GetRedPackInfoBean();
        getRedPackBean.setMch_billno(mchBillno);
        getRedPackBean.setNonce_str(PaySignUtil.getNonceStr());
        getRedPackBean.setMch_id(payAccount.getPartner());
        getRedPackBean.setAppid(payAccount.getAppid());
        getRedPackBean.setBill_type("MCHT");

        String sign = PaySignUtil.createSign(payAccount, new TreeMap<>(BeanUtil.beanToMap(getRedPackBean)));
        getRedPackBean.setSign(sign);
        String result = "";
        try {
            logger.info(ToStringBuilder.reflectionToString(getRedPackBean));
            ValidatorUtil.validate(getRedPackBean);
            result = PayHttpsRequest.init(payAccount).send(getRedpackInfoUrl, getRedPackBean);
        } catch (IOException | KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("", e);
            throw new WeChatException("初始化证书出错。", e);
        } catch (ValidateException e1) {
            logger.error("", e1);
            throw new WeChatException("参数信息有误。", e1);
        }
        try {
            GetRedPackInfoResult redPackInfoResult = XMLUtil.converyToJavaBean(result, GetRedPackInfoResult.class);
            if (redPackInfoResult.isok()) {
                return redPackInfoResult;
            } else {
                throw new WeChatException(redPackInfoResult.errorMsg());
            }
        } catch (JAXBException e) {
            logger.warn("xml解析有误", e);
            throw new WeChatException("xml解析有误。");
        }
    }
}
