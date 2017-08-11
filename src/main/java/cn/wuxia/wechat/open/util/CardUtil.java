/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.open.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;

import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.card.bean.CardBaseInfoBean;
import cn.wuxia.wechat.card.bean.CardBaseInfoBean.DateInfo;
import cn.wuxia.wechat.pay.util.PayUtil;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * [ticket id]
 * 卡券功能 
 * @author guwen
 * @ Version : V<Ver.No> <7 Apr, 2015>
 */
public class CardUtil extends PayUtil {

    private final static String resumeUrl = "https://api.weixin.qq.com/card/code/consume?access_token=";

    private final static String detailUrl = "https://api.weixin.qq.com/card/get?access_token=";

    private final static String createQrcodeUrl = "https://api.weixin.qq.com/card/qrcode/create?access_token=";

    private final static String createUrl = properties.getProperty("card.create");

    private final static String deleteUrl = properties.getProperty("card.delete");

    private final static String batchgetUrl = properties.getProperty("card.batchget");

    private final static String modifystockUrl = properties.getProperty("card.modifystock");

    private final static String cardLogoUrl = properties.getProperty("CARD_LOGO_URL");

    /**
     * 解析 基本的卡券数据 返回对应的map  创建用
     * @param baseInfo 基本的卡券数据
     * @return
     */
    private static Map<String, Object> createBaseInfo(CardBaseInfoBean baseInfo) {
        Assert.notNull(baseInfo, "baseInfo 不能为空");
        Assert.isTrue(baseInfo.getLogoUrl().startsWith("http"), "logoUrl格式不正确");
        Assert.notNull(baseInfo.getCodeType(), "codeType 不能为空");
        Assert.isTrue(baseInfo.getBrandName().length() <= 12, "brandName 长度不能大于12");
        Assert.hasLength(baseInfo.getTitle(), "baseInfo.getTitle() 不能为空");
        Assert.isTrue(baseInfo.getTitle().length() <= 9, "title 长度不能大于9");
        Assert.hasText(baseInfo.getColor(), "color 不能为空");
        Assert.isTrue(baseInfo.getNotice().length() <= 12, "notice 必须长度小于12");
        Assert.isTrue(baseInfo.getDescription().length() <= 1000, "description 必须长度小于1000");
        Assert.notNull(baseInfo.getDateInfo().getType(), "dateinfo.type 不能为空");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("logo_url", baseInfo.getLogoUrl());
        map.put("code_type", baseInfo.getCodeType());
        map.put("brand_name", baseInfo.getBrandName());
        map.put("title", baseInfo.getTitle());
        if (!StringUtils.isBlank(baseInfo.getSubTitle())) {
            Assert.isTrue(baseInfo.getSubTitle().length() <= 18, "subTitle 长度不能大于18");
            map.put("sub_title", baseInfo.getSubTitle());
        }
        map.put("color", baseInfo.getColor());
        map.put("notice", baseInfo.getNotice());
        map.put("description", baseInfo.getDescription());
        //使用日期，有效期的信息
        DateInfo dateInfo = baseInfo.getDateInfo();
        Map<String, Object> dateMap = new HashMap<String, Object>();
        dateMap.put("type", dateInfo.getType());
        if (dateInfo.getType() == 1) { //固定日期区间
            dateMap.put("begin_timestamp", dateInfo.getBeginTimestamp());
            dateMap.put("end_timestamp", dateInfo.getEndTimestamp());
        } else if (dateInfo.getType() == 2) { //固定时长
            dateMap.put("fixed_term", dateInfo.getFixedTerm());
            dateMap.put("fixed_begin_term", dateInfo.getFixedBeginTerm());
        }
        map.put("date_info", dateMap);
        //商品信息
        Map<String, Object> skuMap = new HashMap<String, Object>();
        skuMap.put("quantity", baseInfo.getSku().getQuantity());
        map.put("sku", skuMap);

        if (baseInfo.getLocationIdList() != null) {
            map.put("location_id_list", baseInfo.getLocationIdList());
        }
        if (baseInfo.getUseCustomCode() != null) {
            map.put("use_custom_code", baseInfo.getUseCustomCode());
        }
        if (baseInfo.getBindOpenid() != null) {
            map.put("bind_openid", baseInfo.getBindOpenid());
        }
        if (baseInfo.getCanShare() != null) {
            map.put("can_share", baseInfo.getCanShare());
        }
        if (baseInfo.getCanGiveFriend() != null) {
            map.put("can_give_friend", baseInfo.getCanGiveFriend());
        }
        if (baseInfo.getGetLimit() != null) {
            map.put("get_limit", baseInfo.getGetLimit());
        }
        if (!StringUtils.isBlank(baseInfo.getServicePhone())) {
            map.put("service_phone", baseInfo.getServicePhone());
        }
        if (!StringUtils.isBlank(baseInfo.getSource())) {
            map.put("source", baseInfo.getSource());
        }
        if (!StringUtils.isBlank(baseInfo.getCustomUrlName())) {
            map.put("custom_url_name", baseInfo.getCustomUrlName());
            map.put("custom_url", baseInfo.getCustomUrl());
        }
        if (!StringUtils.isBlank(baseInfo.getCustomUrlSubTitle())) {
            map.put("custom_url_sub_title", baseInfo.getCustomUrlSubTitle());
        }
        if (!StringUtils.isBlank(baseInfo.getPromotionUrlName())) {
            map.put("promotion_url_name", baseInfo.getPromotionUrlName());
        }
        if (!StringUtils.isBlank(baseInfo.getPromotionUrl())) {
            map.put("promotion_url", baseInfo.getPromotionUrl());
        }
        if (!StringUtils.isBlank(baseInfo.getPromotionUrlSubTitle())) {
            map.put("promotion_url_sub_title", baseInfo.getPromotionUrlSubTitle());
        }

        return map;
    }

    /**
     * 创建通用券
     * @param baseInfo 基本的卡券数据
     * @param defaultDetail 描述文本
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static Map<String, Object> createGeneralCoupon(String authorizerAccessToken, CardBaseInfoBean baseInfo, String defaultDetail)
            throws UnsupportedEncodingException {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);
        groupon.put("default_detail", defaultDetail); //

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "GENERAL_COUPON"); //
        card.put("general_coupon", groupon); //

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
        
        return post(createUrl + "?access_token=" + authorizerAccessToken, map);

    }

    /**
     * 卡券核销  - 消耗code
     * @param cardId 卡券ID。创建卡券时use_custom_code 填写true时必填。非自定义code 不必填写。
     * @param code 要消耗序列号 必填
     * @return
     * @throws UnsupportedEncodingException 
     * @throws Exception
     */
    public static Map<String, Object> consume(String authorizerAccessToken, String cardId, String code) throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(cardId)) {
            map.put("card_id", cardId);
        }
        map.put("code", code);

        return post(resumeUrl + "?access_token=" + authorizerAccessToken, map);
    }

    /**
     * 查看卡券信息
     * @author songlin
     * @param code
     * @return
     * @throws Exception
     */
    public static Map<String, Object> detail(String authorizerAccessToken, String cardId) throws Exception {
        Map<String, String> map = Maps.newHashMap();
        map.put("card_id", cardId);
        return post(detailUrl + "?access_token=" + authorizerAccessToken, map);
    }

    /**
     * 创建二维码
     * @param cardId 卡券ID 必填 
     * @param code 指定卡券code 码，只能被领一次。use_custom_code 字段为true 的卡券必须填写，非自定义code 不必填写。
     * @param openid 指定领取者的openid，只有该用户能领取。bind_openid字段为true 的卡券必须填写，非自定义openid 不必填写。
     * @param expireSeconds 指定二维码的有效时间，范围是60 ~ 1800 秒。不填默认为永久有效。
     * @param isUniqueCode 指定下发二维码，生成的二维码随机分配一个code，领取后不可再次扫描。填写true 或false。默认false。
     * @param balance 红包余额，以分为单位。红包类型必填（LUCKY_MONEY），其他卡券类型不填。
     * @param outerId 领取场景值，用于领取渠道的数据统计，默认值为0，字段类型为整型。用户领取卡券后触发的事件推送中会带上此自定义场景值。
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static Map<String, Object> createQrcode(String authorizerAccessToken, String cardId, String code, String openid, Integer expireSeconds,
            Boolean isUniqueCode, Integer balance, Integer outerId) throws UnsupportedEncodingException {
        Map<String, Object> postData = Maps.newHashMap();
        postData.put("action_name", "QR_CARD");
        Map<String, Object> action_info = Maps.newHashMap();
        Map<String, Object> card = Maps.newHashMap();
        card.put("card_id", cardId);
        if (!StringUtils.isBlank(code)) {
            card.put("code", code);
        }
        if (!StringUtils.isBlank(openid)) {
            card.put("openid", openid);
        }
        if (expireSeconds != null) {
            card.put("expire_seconds", expireSeconds);
        }
        if (isUniqueCode != null) {
            card.put("is_unique_code", isUniqueCode);
        }
        if (balance != null) {
            card.put("balance", balance);
        }
        if (outerId != null) {
            card.put("outer_id", outerId);
        }

        action_info.put("card", card);
        postData.put("action_info", action_info);
        return post(createQrcodeUrl + "?access_token=" + authorizerAccessToken, postData);
    }

    /**
     * 增加测试白名单
     * @author songlin
     * @param openId
     * @param username
     * @return
     * @throws Exception
     */
    public static Map<String, Object> testwhitelist(String authorizerAccessToken, List<String> openid, List<String> username) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        if (openid != null && openid.size() > 0) {
            map.put("openid", openid);
        }
        if (username != null && username.size() > 0) {
            map.put("username", username);
        }

        return post("https://api.weixin.qq.com/card/testwhitelist/set?access_token=" + authorizerAccessToken, map);
    }

    /**
     * 删除卡券
     */
    public static Map<String, Object> delete(BasicAccount account, String cardId) throws UnsupportedEncodingException {

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("card_id", cardId);

        return post(deleteUrl + "?access_token=" + TokenUtil.getAccessToken(account), postData);
    }

    /**
     * 批量查询卡列表
     * @param offset 查询卡列表的起始偏移量
     * @param count 需要查询的卡片的数量（数量最大50）
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> batchget(String authorizerAccessToken, Integer offset, Integer count) throws UnsupportedEncodingException {

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("offset", offset);
        postData.put("count", count);

        return post(batchgetUrl + "?access_token=" + authorizerAccessToken, postData);
    }

    /**
     * 库存修改
     * @param cardId 卡券ID
     * @param increaseStockValue 增加多少库存，可以不填或填0
     * @param reduceStockValue 减少多少库存，可以不填或填0
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> modifystock(BasicAccount account, String cardId, Integer increaseStockValue, Integer reduceStockValue)
            throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card_id", cardId);
        if (increaseStockValue != null) {
            map.put("increase_stock_value", increaseStockValue);
        }
        if (reduceStockValue != null) {
            map.put("reduce_stock_value", reduceStockValue);
        }

        //发送
        return post(modifystockUrl + "?access_token=" + TokenUtil.getAccessToken(account), map);
    }

    /**
     * 得到api ticket
     * @author guwen
     * @return
     */
    public static String getApiTicket(String authorizerAccessToken) throws WeChatException {

        String ticketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
        HttpClientRequest param = new HttpClientRequest();
        // 微信授权access_token 
        param.addParam("access_token", authorizerAccessToken);
        // 授权类型
        param.addParam("type", "wx_card");
        param.setUrl(ticketUrl);
        //获取微信api_token认证
        Map<String, Object> apiToken = post(param);
        logger.info("新的card api token" + apiToken);
        if (!StringUtil.equals("ok", (String) apiToken.get("errmsg"))) {
            throw new WeChatException("获取api_ticket有误:" + apiToken.get("errmsg"));
        }

        return (String) apiToken.get("ticket");
    }

    /**
     * 卡券签名
     * @author guwen
     * @param cardId
     * @return
     */
    public static Map<String, String> sign(String apiTicket, String cardId, String openId) throws WeChatException {
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String card_id = cardId;
        String code = "";
        String openid = StringUtil.isBlank(openId) ? "" : openId;
        String balance = "";
        String[] sorts = new String[] { apiTicket, timestamp, card_id, code, openid, balance };
        //注意这里值且必须有序
        Arrays.sort(sorts);
        String signature = StringUtil.join(sorts, "");
        System.out.println("排序结果：" + signature);

        logger.debug("before signature{api_ticket:{},timestamp:{},card_id:{}}", apiTicket, timestamp, card_id);
        System.out.println("api_ticket:" + apiTicket);
        System.out.println("timestamp:" + timestamp);
        System.out.println("card_id:" + card_id);
        try {
            signature = DigestUtils.sha1Hex(signature.getBytes("UTF-8"));
        } catch (Exception e) {
            logger.error("签名有误！！！", e);
            throw new WeChatException("签名有误！！！", e);
        }
        System.out.println("签名结果：" + signature);
        Map<String, String> result = Maps.newHashMap();
        result.put("openid", openid);
        result.put("card_id", card_id);
        result.put("card_timestamp", timestamp);
        result.put("card_signature", signature);
        return result;
    }

    public static String getCardLogoUrl() {
        return cardLogoUrl;
    }

    /**
     * 创建代金券
     * @author wuwenhao
     * @param baseInfo
     * @param leastCost
     * @param reduceCost
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> createCash(BasicAccount account, CardBaseInfoBean baseInfo, Integer leastCost, Integer reduceCost) {
        String curl = createUrl + "?access_token=%s";
        String url = String.format(curl, TokenUtil.getAccessToken(account));
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);
        Map<String, Object> cash = new HashMap<String, Object>();
        cash.put("least_cost", leastCost);
        cash.put("base_info", base_info);
        cash.put("reduce_cost", reduceCost);
        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "CASH"); //
        card.put("cash", cash); //
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
        return post(url, map);
    }

}
