/*
* Created on :12 Oct, 2015
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jdom2.JDOMException;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tencent.common.HttpsAsyncRequest;
import com.tencent.common.HttpsRequest;

import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.util.MD5Util;
import cn.wuxia.common.util.NumberUtil;
import cn.wuxia.common.util.ServletUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.XMLUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.PayAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.pay.util.PayUtil;
import cn.wuxia.wechat.shakearound.bean.SeachCashCouponBean;
import cn.wuxia.wechat.shakearound.bean.ShakePackBean;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 *
 * [ticket id]
 *  摇一摇红包
 * @author songlin
 * @ Version : V<Ver.No> <12 Oct, 2015>
 */
public class ShakeCashCoupon extends PayUtil {

    /**
     * 普通红包
     */
    private final static String CREATE_PACKAGE = "https://api.mch.weixin.qq.com/mmpaymkttransfers/hbpreorder";

    /**
     * 红包查询接口
     */
    private final static String GETHB_INFO = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo";

    /**
     * 
     */
    private final static String CREATE_ACTIVITY = "https://api.weixin.qq.com/shakearound/lottery/addlotteryinfo?access_token=%s&use_template=2&logo_url=%s";

    /**
     * 录入红包信息
     */
    private final static String ENTER_PACKAGE = "https://api.weixin.qq.com/shakearound/lottery/setprizebucket?access_token=%s";

    /**
     * 活动开关
     */
    private final static String ONOFF_ACTIVITY = "https://api.weixin.qq.com/shakearound/lottery/setlotteryswitch?access_token=%s&lottery_id=%s&onoff=%s";

    private final static String logo = properties.getProperty("CARD_LOGO_URL");

    private static String key = properties.getProperty("APP_KEY");

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
     * @return [ticket, 订单号，红包号]
     */
    public static String[] createShakePackage(PayAccount account, String sendName, int amount, int totalPackage, String wish, String action,
            String remark) throws WeChatException {
        return addShakePackage(account, sendName, amount, totalPackage, wish, action, remark, "NORMAL");
    }

    /**
     * 裂变红包
     * ex. CashCoupon.createOnePackage("oqJ0Ft94bzT2Or6L7wjDGGQu3zzg", 1200, 3, "测试裂变红包", "分豆豆红包", "我就不告诉你怎么玩，快快关注分豆豆领更多红包");
     * @author songlin
     * @param openId
     * @param amount 单位分
     * @param totalPackage 红包发放总人数
     * @param wish 红包祝福语 
     * @param action 活动名称
     * @param remark
     * @return [ticket, 订单号，红包号]
     */
    public static String[] createShakeGroupPackage(PayAccount account, String sendName, int amount, int totalPackage, String wish, String action,
            String remark) throws WeChatException {
        return addShakePackage(account, sendName, amount, totalPackage, wish, action, remark, "GROUP");
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
     * @return [ticket, 订单号，红包号]
     */
    private static String[] addShakePackage(PayAccount account, String sendName, int amount, int totalPackage, String wish, String action,
            String remark, String packType) throws WeChatException {
        Map<String, Object> packageParams = Maps.newLinkedHashMap();
        //商户订单号（每个订单号必须唯一）组成： mch_id+yyyymmdd+10位一天内不能重复的数字
        String billno = account.getPartner() + DateUtil.dateToString(new Date(), DateUtil.DateFormatter.FORMAT_YYYYMMDDHHMMSS)
                + RandomStringUtils.randomNumeric(4);
        packageParams.put("mch_billno", billno);
        packageParams.put("mch_id", account.getPartner());
        packageParams.put("wxappid", account.getAppid());
        //发送红包名称
        Assert.isTrue(sendName.length() < 33);
        packageParams.put("send_name", sendName);
        //红包发放总金额，即一组红包金额总和，包括分享者的红包和裂变的红包，单位分
        packageParams.put("total_amount", amount);
        //红包发放总人数，即总共有多少人可以领到该组红包（包括分享者
        packageParams.put("total_num", totalPackage);
        //红包祝福语 
        Assert.isTrue(wish.length() < 128);
        packageParams.put("wishing", wish);
        packageParams.put("auth_mchid", "1000052601");
        packageParams.put("auth_appid", "wxbf42bd79c4391863");
        packageParams.put("risk_cntl", "NORMAL");
        //普通红包 NORMAL-普通红包；GROUP-裂变红包(可分享红包给好友，无关注公众号能力)
        packageParams.put("hb_type", packType);
        //红包金额设置方式，只对裂变红包生效。ALL_RAND—全部随机
        packageParams.put("amt_type", "ALL_RAND");
        //活动名称 
        Assert.isTrue(action.length() < 33);
        packageParams.put("act_name", action);
        Assert.isTrue(remark.length() < 256);
        packageParams.put("remark", remark);//随机字符串，不长于32位 
        packageParams.put("nonce_str", RandomStringUtils.randomAlphanumeric(32));

        TreeMap<String, Object> orderParms = Maps.newTreeMap();
        orderParms.putAll(packageParams);
        String parm = ServletUtils.getUrlParamsByMap(orderParms);
        parm += "&key=" + key;
        System.out.println(parm);
        String sign = MD5Util.MD5HexEncode(parm, "UTF-8").toUpperCase();
        Map<String, Object> m = Maps.newLinkedHashMap();

        m.put("sign", sign);
        m.putAll(packageParams);
        ShakePackBean postXml = (ShakePackBean) BeanUtil.mapToBean(m, ShakePackBean.class);
        String result = "";
        try {
            HttpsAsyncRequest param1 = new HttpsAsyncRequest();
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
            return new String[] { rs.get("sp_ticket"), rs.get("mch_billno"), rs.get("detail_id") };
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
     * @return [ticket, 订单号，红包号]
     */
    public static List<String[]> createBatchShakePackages(PayAccount account, String sendName, Integer[] amount, String wish, String action,
            String remark, String packType) throws WeChatException {
        List<ShakePackBean> list = Lists.newArrayList();
        Integer[] a = { 1, 2 };
        for (int amou : amount) {
            Map<String, Object> packageParams = Maps.newLinkedHashMap();

            //商户订单号（每个订单号必须唯一）组成： mch_id+yyyymmdd+10位一天内不能重复的数字
            String billno = account.getPartner() + DateUtil.dateToString(new Date(), DateUtil.DateFormatter.FORMAT_YYYYMMDDHHMMSS)
                    + RandomStringUtils.randomNumeric(4);
            packageParams.put("mch_billno", billno);
            packageParams.put("mch_id", account.getPartner());
            packageParams.put("wxappid", account.getAppid());
            //发送红包名称
            Assert.isTrue(sendName.length() < 33);
            packageParams.put("send_name", sendName);
            //红包发放总金额，即一组红包金额总和，包括分享者的红包和裂变的红包，单位分
            packageParams.put("total_amount", amou);
            //红包发放总人数，即总共有多少人可以领到该组红包（包括分享者
            packageParams.put("total_num", 1);
            //红包祝福语 
            Assert.isTrue(wish.length() < 128);
            packageParams.put("wishing", wish);
            packageParams.put("auth_mchid", "1000052601");
            packageParams.put("auth_appid", "wxbf42bd79c4391863");
            packageParams.put("risk_cntl", "NORMAL");
            //普通红包 NORMAL-普通红包；GROUP-裂变红包(可分享红包给好友，无关注公众号能力)
            packageParams.put("hb_type", packType);
            //红包金额设置方式，只对裂变红包生效。ALL_RAND—全部随机
            packageParams.put("amt_type", "ALL_RAND");
            //活动名称 
            Assert.isTrue(action.length() < 33);
            packageParams.put("act_name", action);
            Assert.isTrue(remark.length() < 256);
            packageParams.put("remark", remark);//随机字符串，不长于32位 
            packageParams.put("nonce_str", RandomStringUtils.randomAlphanumeric(32));

            TreeMap<String, Object> orderParms = Maps.newTreeMap();
            orderParms.putAll(packageParams);
            String parm = ServletUtils.getUrlParamsByMap(orderParms);
            parm += "&key=" + key;
            System.out.println(parm);
            String sign = MD5Util.MD5HexEncode(parm, "UTF-8").toUpperCase();
            Map<String, Object> m = Maps.newLinkedHashMap();

            m.put("sign", sign);
            m.putAll(packageParams);
            ShakePackBean postXml = (ShakePackBean) BeanUtil.mapToBean(m, ShakePackBean.class);
            list.add(postXml);
        }
        List<String> result = Lists.newArrayList();
        try {
            HttpsAsyncRequest param1 = new HttpsAsyncRequest();
            Object[] b = list.toArray();
            result = param1.sendPost(CREATE_PACKAGE, b);
        } catch (UnrecoverableKeyException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException
                | HttpClientException e) {
            throw new WeChatException("", e);
        }
        List<String[]> rtn = Lists.newArrayList();
        for (String res : result) {
            try {
                Map<String, String> rs = XMLUtil.doXMLParse(res);
                logger.info("红包返回：" + rs.get("err_code") + "  " + rs.get("return_msg"));
                if (StringUtil.equalsIgnoreCase("SUCCESS", rs.get("return_code"))) {
                    rtn.add(new String[] { rs.get("sp_ticket"), rs.get("mch_billno"), rs.get("detail_id"), rs.get("total_amount"),
                            rs.get("return_msg") });
                } else {
                    logger.warn(rs.get("err_code_des"));
                    rtn.add(new String[] { null, rs.get("mch_billno"), null, rs.get("total_amount"), rs.get("return_msg") });
                }
            } catch (JDOMException | IOException e) {
                logger.error("", e);
            }
        }
        return rtn;
    }

    /**
     * 
     * @author songlin
     * @param title 抽奖活动名称
     * @param desc 抽奖活动描述
     * @param beginTime 活动开始时间
     * @param endTime 活动结束时间
     * @param total 总共红包
     * @param jumpUrl 关注后调转链接
     * @return [抽奖活动ID，摇一摇页面ID]
     */
    public static Object[] createActivity(BasicAccount account, String title, String desc, Date beginTime, Date endTime, int total, String jumpUrl)
            throws WeChatException {
        Map<String, Object> prams = Maps.newHashMap();
        Assert.isTrue(title.length() <= 6, "抽奖活动名称小于6个汉字");
        prams.put("title", title);
        Assert.isTrue(desc.length() <= 7, "抽奖活动描述小于7个汉字");
        prams.put("desc", desc);
        prams.put("onoff", 1);
        prams.put("begin_time", beginTime.getTime() / 1000);
        prams.put("expire_time", endTime.getTime() / 1000);

        prams.put("sponsor_appid", account.getAppid());
        prams.put("total", total);
        prams.put("jump_url", jumpUrl);
        prams.put("key", key);
        String url = String.format(CREATE_ACTIVITY, TokenUtil.getAccessToken(account), logo);
        Map<String, Object> result = post(url, prams);
        if (NumberUtil.equals(0, NumberUtil.toInteger(result.get("errcode")))) {
            return new Object[] { result.get("lottery_id"), result.get("page_id") };
        } else {
            throw new WeChatException(result.get("errmsg") + "");
        }
    }

    /**
     * 在调用"创建红包活动"接口之后，调用此接口录入红包信息。注意，此接口每次调用，都会向某个活动新增一批红包信息，如果红包数少于100个，请通过一次调用添加所有红包信息。如果红包数大于100，可以多次调用接口添加。请注意确保多次录入的红包ticket总的数目不大于创建该红包活动时设置的total值。
     * @author songlin
     * @param lotteryId
     * @param tickets 红包ticket列表，如果红包数较多，可以一次传入多个红包，批量调用该接口设置红包信息。每次请求传入的红包个数上限为100 
     */
    public static void enterPackInfo(PayAccount account, String lotteryId, List<String> tickets) throws WeChatException {
        String url = String.format(ENTER_PACKAGE, TokenUtil.getAccessToken(account));

        Map<String, Object> prams = Maps.newHashMap();
        //  红包抽奖id，来自addlotteryinfo返回的lottery_id 
        prams.put("lottery_id", lotteryId);
        prams.put("mchid", account.getPartner());
        prams.put("sponsor_appid", account.getAppid());
        //红包ticket列表，如果红包数较多，可以一次传入多个红包，批量调用该接口设置红包信息。每次请求传入的红包个数上限为100
        List<Map<String, String>> tick = Lists.newArrayList();
        for (String ticket : tickets) {
            Map<String, String> m = Maps.newHashMap();
            m.put("ticket", ticket);
            tick.add(m);
        }
        prams.put("prize_info_list", tick);

        Map<String, Object> result = post(url, prams);
        logger.debug("{}", result);
        if (NumberUtil.equals(0, NumberUtil.toInteger(result.get("errcode")))) {

        } else {
            throw new WeChatException(result.get("errmsg") + "");
        }
    }

    /**
     * 设置红包活动抽奖开关
     * @author songlin
     * @param lotteryId
     * @param onoff true->on; false -> off
     * @return
     */
    public static boolean onoffActivity(BasicAccount account, String lotteryId, boolean onoff) throws WeChatException {
        String url = String.format(ONOFF_ACTIVITY, TokenUtil.getAccessToken(account), lotteryId, onoff ? 1 : 0);
        String resp;
        try {
            resp = HttpClientUtil.get(url);
        } catch (HttpClientException e) {
            throw new WeChatException(e);
        }
        Map<String, Object> result = JsonUtil.fromJson(resp);
        logger.debug(resp);
        if (NumberUtil.equals(0, NumberUtil.toInteger(result.get("errcode")))) {
            return true;
        } else {
            throw new WeChatException(result.get("errmsg") + "");
        }
    }

    // 红包专用生成签名
    public static Map<String, String> sign(String openid, String lotterid) {
        Map<String, String> ret = Maps.newHashMap();
        String nonce_str = RandomStringUtils.randomAlphanumeric(32);
        logger.info("传入openid：" + openid);
        logger.info("传入活动id：" + lotterid);
        //注意这里参数名必须全部小写，且必须有序
        String string1 = "lottery_id=" + lotterid + "&noncestr=" + nonce_str + "&openid=" + openid + "&key=" + key;
        logger.info("得到的字符串准备进行签名的值：" + string1);
        String signature = DigestUtils.md5Hex(StringUtils.getBytesUtf8(string1));
        ret.put("pack_nonceStr", nonce_str);
        ret.put("pack_signature", signature.toUpperCase());
        logger.debug("生成摇一摇红包签名及返回其它信息:{}", ret);
        return ret;
    }

    /**
     * 红包查询接口，查找当前红包当前状态
     * @author wuwenhao
     * @param mchBillno
     * @param billType
     * @return
     */
    public static String[] gethbinfo(PayAccount account, String mchBillno, String billType) throws WeChatException {
        Map<String, Object> packageParams = Maps.newHashMap();
        packageParams.put("mch_billno", mchBillno);
        packageParams.put("mch_id", account.getPartner());
        packageParams.put("appid", account.getAppid());
        packageParams.put("bill_type", billType);
        packageParams.put("nonce_str", RandomStringUtils.randomAlphanumeric(32));
        TreeMap<String, Object> orderParms = Maps.newTreeMap();
        orderParms.putAll(packageParams);
        String parm = ServletUtils.getUrlParamsByMap(orderParms);
        parm += "&key=" + key;
        System.out.println(parm);
        String sign = MD5Util.MD5HexEncode(parm, "UTF-8").toUpperCase();
        Map<String, Object> m = Maps.newLinkedHashMap();

        m.put("sign", sign);
        m.putAll(packageParams);
        SeachCashCouponBean postXml = (SeachCashCouponBean) BeanUtil.mapToBean(m, SeachCashCouponBean.class);
        String result = "";
        try {
            HttpsRequest param1 = new HttpsRequest();
            result = param1.sendPost(GETHB_INFO, postXml);
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
        if (StringUtil.equalsIgnoreCase("SUCCESS", rs.get("return_code")) && StringUtil.equalsIgnoreCase("SUCCESS", rs.get("return_code"))) {
            return new String[] { rs.get("status"), rs.get("detail_id"), rs.get("Send_time") };
        } else {
            throw new WeChatException(rs.get("return_msg"));
        }
    }

}
