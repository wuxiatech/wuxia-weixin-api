package cn.wuxia.wechat.pay.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jdom2.JDOMException;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;

import cn.wuxia.common.util.NumberUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.XMLUtil;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.PayAccount;

/**
 * 
 * [ticket id]
 * 微信工具类
 * @author wuwenhao
 * @ Version : V<Ver.No> <2015年4月1日>
 */
public class PayUtil extends BaseUtil {

    public final static String PAY_NOTIFY_URL;

    public final static String unifiedorderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    static {
        PAY_NOTIFY_URL = properties.getProperty("PAY_NOTIFY_URL");
    }

    public static SortedMap<String, Object> buildPayment(PayAccount account, String orderNo, String body, String amount, String createIp,
            String openId, String callbackUrl, String attach) throws Exception {
        Assert.notNull(account, "PayAccount不能为空");
        Assert.notNull(orderNo, "订单号不能为空");
        Assert.notNull(body, "标题不能为空");
        Assert.notNull(amount, "金额不能为空");
        Assert.notNull(openId, "openid不能为空");

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
            signParams.put("errMsg", "您的IP不正确");
            return signParams;
        }
        packageParams.put("spbill_create_ip", createIp); // 当前地址
        packageParams.put("mch_id", account.getPartner()); // 微信支付商户ID
        packageParams.put("body", body); // 
        String nonceStr = PaySignUtil.getNonceStr();
        packageParams.put("nonce_str", "" + nonceStr);// 随便字符
        packageParams.put("openid", openId); // 微信授权登录后唯一标识 
        packageParams.put("out_trade_no", orderNo); // 订单号
        packageParams.put("total_fee", changeY2F(amount)); // 支付总金额
        packageParams.put("notify_url", callbackUrl); // 申请成功后跳转页面
        packageParams.put("trade_type", "JSAPI"); // 微信支付类型
        packageParams.put("attach", attach); //自定义参数
        //生成支付签名，要采用URLENCODER的原始值进行MD5算法！
        String sign = null;
        // 第一次签名，用于换取预支ID(preapay_id)
        try {
            sign = PaySignUtil.createSign(account, packageParams);
            //增加非参与签名的额外参数
            logger.debug("第一次签名：" + sign);
        } catch (Exception e1) {
            logger.error("", e1);
            signParams.put("errMsg", "第一次签名错误：" + e1.getMessage());
            return signParams;
        }
        packageParams.put("sign", sign);
        // 获取预付付ID
        Map<String, Object> preapyaId = wxPay(account, packageParams);
        if (null != preapyaId && StringUtil.isNotBlank(preapyaId.get("prepayId"))) {
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
            } catch (Exception e1) {
                signParams.put("errMsg", "第二次签名错误：" + e1.getMessage());
            }
            logger.info("随机：" + nonceStr2);
            logger.info("时间：" + timestamp);
            logger.info("预支付：" + orderId);
        }
        if (null != preapyaId && (StringUtil.isNotBlank(preapyaId.get("errMsg")) || StringUtil.isNotBlank(preapyaId.get("returnMsg")))) {
            signParams.put("errMsg", preapyaId.get("errMsg"));
            signParams.put("returnMsg", preapyaId.get("returnMsg"));
        }
        return signParams;
    }

    /**
     * 微信JSAPI支付
     * @author CaRson.Yan
     * @param packageParams
     * @return
     */
    private static Map<String, Object> wxPay(PayAccount account, SortedMap<String, Object> packageParams) {
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
            throw new RuntimeException(e);
        }
        try {
            byte[] t = httpUrl.getByteResult();
            String c = new String(t, "utf-8");
            logger.info("返回数据：" + c);
            if (c.indexOf("prepay_id") > 0) {
                prepayId.put("prepayId", XMLUtil.getChildTagText(c, "prepay_id"));
            } else if (c.indexOf("err_code_des") > 0) {
                prepayId.put("errCode", XMLUtil.getChildTagText(c, "err_code"));
                prepayId.put("errMsg", XMLUtil.getChildTagText(c, "err_code_des"));
            } else if (c.indexOf("return_msg") > 0) {
                prepayId.put("returnMsg", XMLUtil.getChildTagText(c, "return_msg"));
            }
            // 判断是否有数据返回
            if (null != prepayId) {
                return prepayId;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
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
     * @author CaRson.Yan
     * @param orderNo
     * @param body
     * @param amount
     * @param createIp
     * @param openId
     * @param callbackUrl
     * @return
     * @throws Exception
     */
    public static SortedMap<String, Object> buildNativePayment(PayAccount account, String orderNo, String body, String amount, String createIp,
            String url, String attach) throws Exception {
        Assert.notNull(orderNo, "订单号不能为空");
        Assert.notNull(body, "标题不能为空");
        Assert.notNull(amount, "金额不能为空");

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
            ips = createIp.split(" ");
            createIp = ips[0];
        } else {
            signParams.put("errMsg", "您的IP不正确");
            return signParams;
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
            signParams.put("errMsg", "第一次签名错误：" + e1.getMessage());
            return signParams;
        }
        packageParams.put("sign", sign);

        // 获取预付付ID
        Map<String, Object> returnInfo = wxNativePay(account, packageParams);
        if (null != returnInfo && StringUtil.isNotBlank(returnInfo.get("prepayId"))) {
            String orderId = "prepay_id=" + returnInfo.get("prepayId");
            String codeUrl = "code_url=" + returnInfo.get("codeUrl");
            logger.info("生成二维码的链接： " + codeUrl);
            logger.info("预支付：" + orderId);
            signParams.put("codeUrl", returnInfo.get("codeUrl").toString());
        }
        if (null != returnInfo && (StringUtil.isNotBlank(returnInfo.get("errMsg")) || StringUtil.isNotBlank(returnInfo.get("returnMsg")))) {
            signParams.put("errMsg", returnInfo.get("errMsg"));
            signParams.put("returnMsg", returnInfo.get("returnMsg"));
        }
        return signParams;
    }

    /**
     * 微信Native支付
     * @author CaRson.Yan
     * @param packageParams
     * @return
     */
    private static Map<String, Object> wxNativePay(PayAccount account, SortedMap<String, Object> packageParams) {

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
            throw new RuntimeException(e);
        }
        Map<String, Object> prepayId = Maps.newHashMap();
        try {
            byte[] t = httpUrl.getByteResult();
            String c = new String(t, "utf-8");
            logger.info("返回数据：" + c);
            if (c.indexOf("prepay_id") > 0) {
                prepayId.put("prepayId", XMLUtil.getChildTagText(c, "prepay_id"));
                if (c.indexOf("code_url") > 0) {
                    prepayId.put("codeUrl", XMLUtil.getChildTagText(c, "code_url"));
                }
            } else if (c.indexOf("err_code_des") > 0) {
                prepayId.put("errCode", XMLUtil.getChildTagText(c, "err_code"));
                prepayId.put("errMsg", XMLUtil.getChildTagText(c, "err_code_des"));
            } else if (c.indexOf("return_msg") > 0) {
                prepayId.put("returnMsg", XMLUtil.getChildTagText(c, "return_msg"));
            }
            // 判断是否有数据返回
            if (null != prepayId) {
                return prepayId;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
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
     * @author guwen
     * @throws IOException 
     * @throws JDOMException 
     */
    public static Map<String, String> orderquery(PayAccount account, String transactionId, String outTradeNo) throws JDOMException, IOException {
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
        String resultXml = new String(t, "utf-8");
        logger.info("结果报文:" + resultXml);
        Map<String, String> resultMap = XMLUtil.doXMLParse(resultXml);

        return resultMap;
    }

}
