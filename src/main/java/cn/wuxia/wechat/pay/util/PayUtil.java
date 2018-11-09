package cn.wuxia.wechat.pay.util;

import cn.wuxia.common.util.MapUtil;
import cn.wuxia.common.util.NumberUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;
import cn.wuxia.common.xml.Dom4jXmlUtil;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.PayAccount;
import cn.wuxia.wechat.WeChatException;
import com.google.common.collect.Maps;
import org.dom4j.DocumentException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * [ticket id]
 * 微信工具类
 *
 * @author wuwenhao
 * @ Version : V<Ver.No> <2015年4月1日>
 */
public class PayUtil extends BaseUtil {

    public final static String unifiedorderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static SortedMap<String, Object> buildPayment(PayAccount account, String orderNo, String body, String amount, String createIp,
                                                         String openId, String notifyUrl, String attach) throws WeChatException {
        Assert.notNull(account, "PayAccount不能为空");
        Assert.notNull(orderNo, "订单号为空");
        Assert.isTrue(orderNo.length() < 32, "订单号" + orderNo + "长度超过32");
        Assert.notNull(body, "标题不能为空");
        Assert.notNull(amount, "金额不能为空");
        Assert.notNull(openId, "openid不能为空");
        Assert.notNull(notifyUrl, "notifyUrl不能为空");
        if (StringUtil.isNotBlank(attach) && StringUtil.length(attach) > 127) {
            throw new IllegalArgumentException("attach长度不能超过127");
        }
        // 第二次签名参数
        SortedMap<String, Object> signParams = new TreeMap<String, Object>();
        logger.info("订单号：" + orderNo);
        //  保存第一次签名参数,用于申请预支付ID
        SortedMap<String, Object> packageParams = new TreeMap<String, Object>();
        packageParams.put("appid", account.getAppid()); // 微信APPID
        // 处理获取两个IP的情况
        if (null != createIp) {
            String[] ips = createIp.split(",");
            createIp = ips[0];
        } else {
            throw new WeChatException("您的IP不正确");
        }
        packageParams.put("spbill_create_ip", createIp); // 当前地址
        packageParams.put("mch_id", account.getPartner()); // 微信支付商户ID
        packageParams.put("body", body); // 
        String nonceStr = PaySignUtil.getNonceStr();
        packageParams.put("nonce_str", "" + nonceStr);// 随便字符
        packageParams.put("openid", openId); // 微信授权登录后唯一标识 
        packageParams.put("out_trade_no", orderNo); // 订单号
        packageParams.put("total_fee", changeY2F(amount)); // 支付总金额
        packageParams.put("notify_url", notifyUrl); // 申请成功后跳转页面
        packageParams.put("trade_type", "JSAPI"); // 微信支付类型
        packageParams.put("attach", attach); //自定义参数,127长度
        //生成支付签名，要采用URLENCODER的原始值进行MD5算法！
        String sign = null;
        // 第一次签名，用于换取预支ID(preapay_id)
        try {
            sign = PaySignUtil.createSign(account, packageParams);
            //增加非参与签名的额外参数
            logger.debug("第一次签名：" + sign);
        } catch (Exception e1) {
            logger.error("", e1);
            throw new WeChatException("第一次签名错误：" + e1.getMessage());
        }
        packageParams.put("sign", sign);
        // 获取预付付ID
        Map<String, Object> preapyaId = wxPay(account, packageParams);
        String orderId = "prepay_id=" + preapyaId.get("prepayId");
        String timestamp = PaySignUtil.getTimeStamp();
        String nonceStr2 = PaySignUtil.getNonceStr();
        signParams.put("appId", account.getAppid());
        signParams.put("timeStamp", timestamp);
        signParams.put("nonceStr", nonceStr2);
        signParams.put("package", orderId);
        signParams.put("signType", "MD5");
        // 第二次签名，用于换取新的签名
        String paysign = null;
        try {
            paysign = PaySignUtil.createSign(account, signParams);
            // 保存签名
            logger.debug("第二次签名：" + paysign);
            signParams.put("paySign", paysign);
            return signParams;
        } catch (Exception e1) {
            throw new WeChatException("第二次签名错误：" + e1.getMessage());
        }
    }

    /**
     * 微信JSAPI支付
     *
     * @param packageParams
     * @return
     * @author CaRson.Yan
     */
    private static Map<String, Object> wxPay(PayAccount account, SortedMap<String, Object> packageParams) throws WeChatException {
        Map<String, Object> prepayId = Maps.newHashMap();
        // 统一支付接口,用于换取prepay_id
        String wxurl = unifiedorderUrl;
        // 用于向微信支付申请预支付ID参数，prepay_id
        SortedMap<String, Object> wxparam = Maps.newTreeMap();
        // 公众账号ID
        wxparam.put("appid", account.getAppid());
        // 商户号
        wxparam.put("mch_id", account.getPartner());
        // 终端ip
        wxparam.put("spbill_create_ip", packageParams.get("spbill_create_ip"));
        // 随机字符串
        wxparam.put("nonce_str", packageParams.get("nonce_str"));
        // 签名
        wxparam.put("sign", "" + packageParams.get("sign"));
        // 统一支付接口中，trade_type为JSAPI时，openid为必填参数！
        wxparam.put("openid", "" + packageParams.get("openid"));
        // 商品描述
        wxparam.put("body", "" + packageParams.get("body"));
        // 商户订单号
        wxparam.put("out_trade_no", "" + packageParams.get("out_trade_no"));
        // 总金额
        wxparam.put("total_fee", "" + packageParams.get("total_fee"));
        // 通知地址
        wxparam.put("notify_url", "" + packageParams.get("notify_url")); //是
        // 交易类型
        wxparam.put("trade_type", "JSAPI");
        wxparam.put("attach", "" + packageParams.get("attach")); //自定义参数)
        // 将数据提交给统一支付接口
        String xml = parseXML(wxparam);
        logger.info(xml);
        HttpClientResponse httpUrl;
        try {
            httpUrl = HttpClientUtil.postXml(wxurl, xml);
        } catch (HttpClientException e) {
            logger.error("", e);
            throw new WeChatException("无法请求" + wxurl, e);
        }
        try {
            byte[] t = httpUrl.getByteResult();
            String c = new String(t, "utf-8");
            logger.info("返回数据：" + c);
            Map<String, Object> resultMap = Dom4jXmlUtil.xml2map(c, false);
            if (StringUtil.equalsIgnoreCase("FAIL", MapUtil.getString(resultMap, "return_code"))
                    || StringUtil.equalsIgnoreCase("FAIL", MapUtil.getString(resultMap, "result_code"))) {
                throw new WeChatException(MapUtil.getString(resultMap, "return_msg") + "" + MapUtil.getString(resultMap, "err_code_des"));
            }
            return resultMap;
        } catch (UnsupportedEncodingException | DocumentException e) {
            throw new WeChatException("解析xml有误", e);
        }
    }

    //输出XML
    public static String parseXML(SortedMap<String, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"appkey".equals(k)) {
                if (NumberUtil.isNumber("" + parameters.get(k))) {
                    sb.append("<" + k + ">" + parameters.get(k) + "</" + k + ">\n");
                } else {
                    sb.append("<" + k + "><![CDATA[" + parameters.get(k) + "]]></" + k + ">\n");
                }
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 微信native方式支付
     *
     * @param orderNo
     * @param body
     * @param amount
     * @param createIp
     * @param url
     * @param attach
     * @return
     * @throws Exception
     * @author CaRson.Yan
     */
    public static String buildNativePayment(PayAccount account, String orderNo, String body, String amount, String createIp,
                                            String url, String attach) throws WeChatException {
        Assert.notNull(orderNo, "订单号为空");
        Assert.isTrue(orderNo.length() < 32, "订单号" + orderNo + "长度超过32");
        Assert.notNull(body, "标题不能为空");
        Assert.notNull(amount, "金额不能为空");
        Assert.notNull(url, "notifyurl不能为空");
        if (StringUtil.isNotBlank(attach) && StringUtil.length(attach) > 127) {
            throw new IllegalArgumentException("attach长度不能超过127");
        }

        logger.info("订单号：" + orderNo);
        //  保存第一次签名参数,用于申请预支付ID
        SortedMap<String, Object> packageParams = new TreeMap<String, Object>();
        packageParams.put("appid", account.getAppid()); // 微信APPID
        // 处理获取两个IP的情况
        if (null != createIp) {
            String[] ips = createIp.split(",");
            createIp = ips[0];
            ips = createIp.split(" ");
            createIp = ips[0];
        } else {
            throw new WeChatException("您的IP不正确");
        }
        packageParams.put("appid", account.getAppid()); //公众号ID
        packageParams.put("spbill_create_ip", createIp); // 当前地址
        packageParams.put("mch_id", account.getPartner()); // 微信支付商户ID
        packageParams.put("body", body == null ? "标题空" : body); //付款时显示的产品名称
        String nonceStr = PaySignUtil.getNonceStr();
        packageParams.put("nonce_str", "" + nonceStr);// 随便字符
        packageParams.put("out_trade_no", orderNo); // 订单号
        packageParams.put("total_fee", changeY2F("" + amount)); // 支付总金额
        packageParams.put("product_id", orderNo); //商品id
        packageParams.put("trade_type", "NATIVE"); // 微信支付类型
        packageParams.put("attach", attach); //自定义参数
        packageParams.put("notify_url", url); //回调url，支付操作后微信返回支付信息到该地址
        logger.info("=======packageParams：" + packageParams + "================================");
        //生成支付签名，要采用URLENCODER的原始值进行MD5算法！
        String sign = null;
        // 第一次签名，用于换取预支ID(preapay_id)
        try {
            sign = PaySignUtil.createSign(account, packageParams);
            //增加非参与签名的额外参数
            logger.debug("第一次签名：" + sign);
        } catch (Exception e1) {
            logger.error("", e1);
            throw new WeChatException("第一次签名错误：" + e1.getMessage());
        }
        packageParams.put("sign", sign);

        // 获取预付付ID
        Map<String, Object> returnInfo = wxNativePay(account, packageParams);
        return MapUtil.getString(returnInfo, "code_url");
    }

    /**
     * 微信Native支付
     *
     * @param packageParams
     * @return
     * @author CaRson.Yan
     */
    private static Map<String, Object> wxNativePay(PayAccount account, SortedMap<String, Object> packageParams) throws WeChatException {

        // 统一支付接口,用于换取prepay_id
        String wxurl = unifiedorderUrl;
        // 用于向微信支付申请预支付ID参数，prepay_id
        SortedMap<String, Object> wxparam = Maps.newTreeMap();
        // 公众账号ID
        wxparam.put("appid", account.getAppid());
        // 商户号
        wxparam.put("mch_id", account.getPartner());
        // 终端ip      
        wxparam.put("spbill_create_ip", packageParams.get("spbill_create_ip"));
        // 随机字符串
        wxparam.put("nonce_str", packageParams.get("nonce_str"));
        // 签名
        wxparam.put("sign", "" + packageParams.get("sign"));
        // 商品描述
        wxparam.put("body", "" + packageParams.get("body"));
        // 商户订单号 
        wxparam.put("out_trade_no", "" + packageParams.get("out_trade_no"));
        // 总金额
        wxparam.put("total_fee", "" + packageParams.get("total_fee"));
        // 通知地址 
        wxparam.put("notify_url", "" + packageParams.get("notify_url")); //是
        //产品id
        wxparam.put("product_id", "" + packageParams.get("product_id"));
        // 交易类型
        wxparam.put("trade_type", "" + packageParams.get("trade_type")); //是

        wxparam.put("attach", "" + packageParams.get("attach")); //自定义参数)
        // 将数据提交给统一支付接口
        String xml = parseXML(wxparam);
        logger.info(xml);
        HttpClientResponse httpUrl;
        try {
            httpUrl = HttpClientUtil.postXml(wxurl, xml);
        } catch (HttpClientException e) {
            logger.error("", e);
            throw new WeChatException("无法请求" + wxurl, e);
        }
        try {
            byte[] t = httpUrl.getByteResult();
            String c = new String(t, "utf-8");
            logger.info("返回数据：" + c);
            Map<String, Object> resultMap = Dom4jXmlUtil.xml2map(c, false);
            if (StringUtil.equalsIgnoreCase("FAIL", MapUtil.getString(resultMap, "return_code"))
                    || StringUtil.equalsIgnoreCase("FAIL", MapUtil.getString(resultMap, "result_code"))) {
                throw new WeChatException(MapUtil.getString(resultMap, "return_msg") + "" + MapUtil.getString(resultMap, "err_code_des"));
            }
            return resultMap;
        } catch (UnsupportedEncodingException | DocumentException e) {
            throw new WeChatException("解析xml有误", e);
        }
    }

    /**
     * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额
     *
     * @param amount
     * @return
     */
    public static String changeY2F(String amount) {
        String currency = amount.replaceAll("\\$|\\￥|\\,", ""); //处理包含, ￥ 或者$的金额    
        int index = currency.indexOf(".");
        int length = currency.length();
        Long amLong = 0l;
        if (index == -1) {
            amLong = Long.valueOf(currency + "00");
        } else if (length - index >= 3) {
            amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
        } else if (length - index == 2) {
            amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + 0);
        } else {
            amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
        }
        return amLong.toString();
    }

    /**
     * 查询订单接口
     *
     * @throws IOException
     * @author guwen
     */
    public static Map<String, Object> orderquery(PayAccount account, String transactionId, String outTradeNo) throws WeChatException {
        SortedMap<String, Object> map = Maps.newTreeMap();
        map.put("appid", account.getAppid());
        map.put("mch_id", account.getPartner());
        if (StringUtil.isNotBlank(transactionId)) {
            map.put("transaction_id", transactionId);
        }
        if (StringUtil.isNotBlank(outTradeNo)) {
            map.put("out_trade_no", outTradeNo);
        }
        map.put("nonce_str", PaySignUtil.getNonceStr());
        //签名
        String sign = PaySignUtil.createSign(account, map);
        map.put("sign", sign);
        String xml = PayUtil.parseXML(map);
        //发送请求
        logger.info("请求报文:" + xml);
        HttpClientResponse httpUrl;
        try {
            httpUrl = HttpClientUtil.postXml("https://api.mch.weixin.qq.com/pay/orderquery", xml);
        } catch (HttpClientException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
        byte[] t = httpUrl.getByteResult();
        try {
            String resultXml = new String(t, "utf-8");
            logger.debug("结果报文:" + resultXml);
            Map<String, Object> resultMap = Dom4jXmlUtil.xml2map(resultXml, false);
            if (StringUtil.equalsIgnoreCase("FAIL", MapUtil.getString(resultMap, "return_code"))
                    || StringUtil.equalsIgnoreCase("FAIL", MapUtil.getString(resultMap, "result_code"))) {
                throw new WeChatException(MapUtil.getString(resultMap, "return_msg") + "" + MapUtil.getString(resultMap, "err_code_des"));
            }
            return resultMap;
        } catch (UnsupportedEncodingException | DocumentException e) {
            throw new WeChatException("解析结果失败", e);
        }
    }

}
