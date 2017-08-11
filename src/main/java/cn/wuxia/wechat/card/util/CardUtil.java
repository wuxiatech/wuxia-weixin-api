/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.card.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;

import cn.wuxia.common.util.DateUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.card.bean.CardBaseInfoBean;
import cn.wuxia.wechat.card.bean.CardBaseInfoBean.DateInfo;
import cn.wuxia.wechat.card.emuns.CardType;
import cn.wuxia.wechat.sign.util.SignUtil;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * [ticket id]
 * 卡券功能 
 * @author songlin
 * @ Version : V<Ver.No> <7 Apr, 2015>
 */
public class CardUtil extends BaseUtil {

    private final static String resumeUrl = "https://api.weixin.qq.com/card/code/consume?access_token=";

    private final static String detailUrl = "https://api.weixin.qq.com/card/get?access_token=";

    private final static String createQrcodeUrl = "https://api.weixin.qq.com/card/qrcode/create?access_token=";

    private final static String createUrl = properties.getProperty("card.create");

    private final static String deleteUrl = properties.getProperty("card.delete");

    private final static String codeGetUrl = properties.getProperty("card.code.get");

    private final static String batchgetUrl = properties.getProperty("card.batchget");

    private final static String codeUpdateUrl = properties.getProperty("card.code.update");

    private final static String codeUnavailableUrl = properties.getProperty("card.code.unavailable");

    private final static String updateUrl = properties.getProperty("card.update");

    private final static String modifystockUrl = properties.getProperty("card.modifystock");

    private final static String getcolorsUrl = properties.getProperty("card.getcolors");

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
     * @ 
     */
    public static Map<String, Object> createGeneralCoupon(BasicAccount account, CardBaseInfoBean baseInfo, String defaultDetail) {
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
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);

    }

    /**
     * 创建团购券
     * @param baseInfo 基本的卡券数据
     * @param dealDetail 团购详情
     * @return
     * @ 
     */
    public static Map<String, Object> createGroupon(BasicAccount account, CardBaseInfoBean baseInfo, String dealDetail) {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);
        groupon.put("deal_detail", dealDetail);

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "GROUPON");
        card.put("groupon", groupon);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);
    }

    /**
     * 创建礼品券
     * @param baseInfo 基本的卡券数据
     * @param gift 礼品券专用，表示礼品名字
     * @return
     * @ 
     */
    public static Map<String, Object> createGift(BasicAccount account, CardBaseInfoBean baseInfo, String gift) {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);
        groupon.put("gift", gift); //

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "GIFT"); //
        card.put("gift", groupon); //

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);
    }

    /**
     * 创建代金券
     * @param baseInfo  基本的卡券数据
     * @param leastCost 代金券专用，表示起用金额（单位为分）
     * @param reduceCost 代金券专用，表示减免金额（单位为分）
     * @return
     * @ 
     */
    public static Map<String, Object> createCash(BasicAccount account, CardBaseInfoBean baseInfo, Integer leastCost, Integer reduceCost) {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);
        if (leastCost != null) {
            groupon.put("least_cost", leastCost); //
        }
        groupon.put("reduce_cost", reduceCost);

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "CASH"); //
        card.put("cash", groupon); //

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);
    }

    /**
     * 创建折扣券
     * @param baseInfo  基本的卡券数据
     * @param discount 折扣券专用，表示打折额度（百分比）。填30 就是七折。
     * @return 
     * @ 
     */
    public static Map<String, Object> createDiscount(BasicAccount account, CardBaseInfoBean baseInfo, Integer discount) {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);
        groupon.put("discount", discount); //

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "DISCOUNT"); //
        card.put("discount", groupon); //

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);
    }

    /**
     * 创建会员卡 
     * @param baseInfo   基本的卡券数据
     * @param supplyBonus 是否支持积分
     * @param supplyBalance 是否支持储值
     * @param bonusCleared 积分清零规则
     * @param bonusRules 积分规则
     * @param balanceRules 储值说明
     * @param prerogative 特权说明
     * @param bindOldCardUrl 绑定旧卡的url，与“activate_url”字段二选一必填。
     * @param activateUrl 激活会员卡的url，与“bind_old_card_url”字段二选一必填。
     * @param needPushOnView true 为用户点击进入会员卡时是否推送事件。
     * @return
     * @ 
     */
    public static Map<String, Object> createMemberCard(BasicAccount account, CardBaseInfoBean baseInfo, Boolean supplyBonus, Boolean supplyBalance,
            String bonusCleared, String bonusRules, String balanceRules, String prerogative, String bindOldCardUrl, String activateUrl,
            Boolean needPushOnView) {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);
        groupon.put("supply_bonus", supplyBonus); //
        groupon.put("supply_balance", supplyBalance); //
        if (supplyBonus == true) {
            groupon.put("bonus_cleared", bonusCleared);
            groupon.put("bonus_rules", bonusRules);
        }
        if (supplyBalance == true) {
            groupon.put("balance_rules", balanceRules);
        }
        groupon.put("prerogative", prerogative);
        if (!StringUtils.isBlank(bindOldCardUrl)) {
            groupon.put("bind_old_card_url", bindOldCardUrl);
        } else {
            groupon.put("activate_url", activateUrl);
        }
        if (needPushOnView != null) {
            groupon.put("need_push_on_view", needPushOnView);
        }

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "MEMBER_CARD"); //
        card.put("member_card", groupon); //

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
      //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);
    }

    /**
     *  创建景点门票 
     * @param baseInfo 基本的卡券数据
     * @param ticketClass 票类型，例如平日全票，套票等
     * @param guideUrl  导览图url
     * @return
     * @ 
     */
    public static Map<String, Object> createScenicTicket(BasicAccount account, CardBaseInfoBean baseInfo, String ticketClass, String guideUrl) {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);
        if (!StringUtils.isBlank(ticketClass)) {
            groupon.put("ticket_class", ticketClass); //
        }
        if (!StringUtils.isBlank(guideUrl)) {
            groupon.put("guide_url", guideUrl);
        }

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "SCENIC_TICKET"); //
        card.put("scenic_ticket", groupon); //

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
      //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);
    }

    /**
     * 创建电影票
     * @param baseInfo  基本的卡券数据
     * @param detail 电影票详情
     * @return
     * @ 
     */
    public static Map<String, Object> createMovieTicket(BasicAccount account, CardBaseInfoBean baseInfo, String detail) {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);
        if (!StringUtils.isBlank(detail)) {
            groupon.put("detail", detail); //
        }

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "MOVIE_TICKET"); //
        card.put("movie_ticket", groupon); //

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
      //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);
    }

    /**
     * 创建飞机票
     * @param baseInfo 基本的卡券数据 
     * @param from 起点，上限为18 个汉字
     * @param to 终点，上限为18 个汉字
     * @param flight 航班
     * @param departureTime 起飞时间。Unix 时间戳格式
     * @param landingTime 降落时间。Unix 时间戳格式
     * @param checkInUrl 在线值机的链接
     * @param gate 登机口。
     * @param boardingTime 登机时间，只显示“时分”不显示日期，按时间戳格式填写。如发生登机时间变更，建议商家实时调用该接口变更。
     * @param airModel 机型，上限为8 个汉字
     * @return
     * @ 
     */
    public static Map<String, Object> createBoardingPass(BasicAccount account, CardBaseInfoBean baseInfo, String from, String to, String flight,
            Long departureTime, Long landingTime, String checkInUrl, String gate, Long boardingTime, String airModel) {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);
        groupon.put("from", from); //
        groupon.put("to", to);
        groupon.put("flight", flight);
        if (departureTime != null) {
            groupon.put("departure_time", departureTime);
        }
        if (landingTime != null) {
            groupon.put("landing_time", landingTime);
        }
        if (!StringUtils.isBlank(checkInUrl)) {
            groupon.put("check_in_url", checkInUrl);
        }
        if (!StringUtils.isBlank(gate)) {
            groupon.put("gate", gate);
        }
        if (boardingTime != null) {
            groupon.put("boarding_time", boardingTime);
        }
        if (airModel != null) {
            groupon.put("air_model", airModel);
        }

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "BOARDING_PASS"); //
        card.put("boarding_pass", groupon); //

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
      //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);
    }

    /**
     * 创建红包
     * @param baseInfo 基本的卡券数据
     * @return
     * @ 
     */
    public static Map<String, Object> createLuckyMoney(BasicAccount account, CardBaseInfoBean baseInfo) {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "LUCKY_MONEY"); //
        card.put("lucky_money", groupon); //

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
        //发送
      //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);
    }

    /**
     * 创建会议门票
     * @param baseInfo 基本的卡券数据
     * @param meetingDetail 会议详情
     * @param mapUrl 会场导览图
     * @return
     * @ 
     */
    public static Map<String, Object> createMeetingTicket(BasicAccount account, CardBaseInfoBean baseInfo, String meetingDetail, String mapUrl) {
        Map<String, Object> base_info = CardUtil.createBaseInfo(baseInfo);

        Map<String, Object> groupon = new HashMap<String, Object>();
        groupon.put("base_info", base_info);
        groupon.put("meeting_detail", meetingDetail); //
        if (!StringUtils.isBlank(mapUrl)) {
            groupon.put("map_url", mapUrl);
        }

        Map<String, Object> card = new HashMap<String, Object>();
        card.put("card_type", "MEETING_TICKET"); //
        card.put("meeting_ticket", groupon); //

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card", card);
      //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(createUrl + "?access_token=" + access_token, map);
    }

    /**
     * 卡券核销  - 消耗code
     * @param cardId 卡券ID。创建卡券时use_custom_code 填写true时必填。非自定义code 不必填写。
     * @param code 要消耗序列号 必填
     * @return
     * @ 
     * @throws Exception
     */
    public static Map<String, Object> consume(BasicAccount account, String cardId, String code) {
        String access_token = TokenUtil.getAccessToken(account);
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(cardId)) {
            map.put("card_id", cardId);
        }
        map.put("code", code);

        return post(resumeUrl + "?access_token=" + access_token, map);
    }

    /**
     * 查看卡券信息
     * @author songlin
     * @param code
     * @return
     * @throws Exception
     */
    public static Map<String, Object> detail(BasicAccount account, String cardId) throws Exception {
        String access_token = TokenUtil.getAccessToken(account);
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("card_id", cardId);
        return post(detailUrl + "?access_token=" + access_token, paramMap);
    }

    private static String API_TICKET;

    private static long PREV_TIME;

    /**
      * Description of the method
      * @author songlin
      * @return
      */
    public static String getApiTicket(BasicAccount account) throws WeChatException {
        Long nowTime = DateUtil.newInstanceDate().getTime();
        Long between = (nowTime - PREV_TIME) / 1000;
        if (between < 2 * 60 * 60 && StringUtil.isNotBlank(API_TICKET)) {
            return API_TICKET;
        }
        PREV_TIME = nowTime;
        String ticketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
        HttpClientRequest param = new HttpClientRequest();
        // 微信授权access_token 
        param.addParam("access_token", TokenUtil.getAccessToken(account));
        // 授权类型
        param.addParam("type", "wx_card");
        param.setUrl(ticketUrl);
        //获取微信api_token认证
        Map<String, Object> apiToken = post(param);
        logger.debug("" + apiToken);
        if (!StringUtil.equals("ok", (String) apiToken.get("errmsg"))) {
            throw new WeChatException("获取api_ticket有误:" + apiToken.get("errmsg"));
        }
        if (MapUtils.isNotEmpty(apiToken) && StringUtil.isNotBlank(apiToken.get("ticket"))) {
            API_TICKET = (String) apiToken.get("ticket");
        }
        return API_TICKET;
    }

    /**
     * 卡券签名
     * @author songlin
     * @param cardId
     * @return
     */
    public static Map<String, String> sign(BasicAccount account, String cardId, String openId) throws WeChatException {
        String api_ticket = getApiTicket(account);
        String timestamp = SignUtil.create_timestamp();
        String card_id = cardId;
        String code = "";
        String openid = StringUtil.isBlank(openId) ? "" : openId;
        String balance = "";
        String[] sorts = new String[] { api_ticket, timestamp, card_id, code, openid, balance };
        //注意这里值且必须有序
        Arrays.sort(sorts);
        String signature = StringUtil.join(sorts, "");
        System.out.println("排序结果：" + signature);

        logger.debug("before signature{api_ticket:{},timestamp:{},card_id:{}}", api_ticket, timestamp, card_id);
        System.out.println("api_ticket:" + api_ticket);
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
     * @ 
     */
    public static Map<String, Object> createQrcode(BasicAccount account, String cardId, String code, String openid, Integer expireSeconds,
            Boolean isUniqueCode, Integer balance, Integer outerId) {
        String access_token = TokenUtil.getAccessToken(account);
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
        return post(createQrcodeUrl + "?access_token=" + access_token, postData);
    }

    /**
     * 增加测试白名单
     * @author songlin
     * @param openId
     * @param username
     * @return
     * @throws Exception
     */
    public static Map<String, Object> testwhitelist(BasicAccount account, List<String> openid, List<String> username) throws Exception {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        if (openid != null && openid.size() > 0) {
            map.put("openid", openid);
        }
        if (username != null && username.size() > 0) {
            map.put("username", username);
        }
        return post("https://api.weixin.qq.com/card/testwhitelist/set?access_token=" + access_token, map);
    }

    /**
     * 删除卡券
     * @param cardId 卡券ID
     * @return
     * @
     */
    public static Map<String, Object> delete(BasicAccount account, String cardId) {
        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("card_id", cardId);

        return post(deleteUrl + "?access_token=" + access_token, postData);
    }

    /**
     * 查询code
     * @param cardId 要消耗序列号所述的card_id， 生成券时use_custom_code 填写true 时必填。非自定义code 不必填写。
     * @param code 要查询的序列号
     * @return
     * @
     */
    public static Map<String, Object> codeGet(BasicAccount account, String cardId, String code) {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("code", code);
        if (StringUtils.isBlank(cardId)) {
            postData.put("card_id", cardId);
        }

        return post(codeGetUrl + "?access_token=" + access_token, postData);
    }

    /**
     * 批量查询卡列表
     * @param offset 查询卡列表的起始偏移量
     * @param count 需要查询的卡片的数量（数量最大50）
     * @return
     * @
     */
    public static Map<String, Object> batchget(BasicAccount account, Integer offset, Integer count) {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("offset", offset);
        postData.put("count", count);

        return post(batchgetUrl + "?access_token=" + access_token, postData);
    }

    /**
     * 更改code
     * @param code 卡券的code 编码
     * @param cardId 卡券ID
     * @param newCode 新的卡券code 编码
     * @return
     * @
     */
    public static Map<String, Object> codeUpdate(BasicAccount account, String code, String cardId, String newCode) {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("code", code);
        postData.put("card_id", cardId);
        postData.put("new_code", newCode);

        return post(codeUpdateUrl + "?access_token=" + access_token, postData);
    }

    /**
     * 设置卡券失效
     * @param code 需要设置为失效的code
     * @param cardId 自定义code 的卡券必填。非自定义code的卡券不填。
     * @return
     * @
     */
    public static Map<String, Object> codeUnavailable(BasicAccount account, String code, String cardId) {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("code", code);
        if (!StringUtils.isBlank(cardId)) {
            postData.put("card_id", cardId);
        }

        return post(codeUnavailableUrl + "?access_token=" + access_token, postData);
    }

    /**
     * 解析 基本的卡券数据 返回对应的map  更新用
     * @param baseInfo 基本的卡券数据
     * @return
     */
    private static Map<String, Object> updateBaseInfo(CardBaseInfoBean baseInfo) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (!StringUtils.isBlank(baseInfo.getLogoUrl())) {
            map.put("logo_url", baseInfo.getLogoUrl());
        }
        if (!StringUtils.isBlank(baseInfo.getNotice())) {
            map.put("notice", baseInfo.getNotice());
        }
        if (!StringUtils.isBlank(baseInfo.getDescription())) {
            map.put("description", baseInfo.getDescription());
        }
        if (!StringUtils.isBlank(baseInfo.getServicePhone())) {
            map.put("service_phone", baseInfo.getServicePhone());
        }
        if (!StringUtils.isBlank(baseInfo.getColor())) {
            map.put("color", baseInfo.getColor());
        }
        if (baseInfo.getLocationIdList() != null && baseInfo.getLocationIdList().size() > 0) {
            map.put("location_id_list", baseInfo.getLocationIdList());
        }
        if (!StringUtils.isBlank(baseInfo.getCustomUrlName())) {
            map.put("custom_url_name", baseInfo.getCustomUrlName());
        }
        if (!StringUtils.isBlank(baseInfo.getCustomUrl())) {
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
        if (baseInfo.getCodeType() != null) {
            map.put("code_type", baseInfo.getCodeType().toString());
        }
        if (baseInfo.getGetLimit() != null) {
            map.put("get_limit", baseInfo.getGetLimit());
        }
        if (baseInfo.getCanGiveFriend() != null) {
            map.put("can_give_friend", baseInfo.getCanGiveFriend());
        }
        if (baseInfo.getCanShare() != null) {
            map.put("can_share", baseInfo.getCanShare());
        }

        //使用日期，有效期的信息
        DateInfo dateInfo = baseInfo.getDateInfo();
        if (dateInfo != null) {
            Map<String, Object> dateMap = new HashMap<String, Object>();
            if (dateInfo.getType() != null && 1 == dateInfo.getType()) { //固定日期区间
                dateMap.put("type", dateInfo.getType());
                dateMap.put("begin_timestamp", dateInfo.getBeginTimestamp());
                dateMap.put("end_timestamp", dateInfo.getEndTimestamp());
                map.put("date_info", dateMap);
            }
        }

        return map;
    }

    /**
     * 更改卡券信息  -- 非特殊卡券
     * @param baseInfo 基本的卡券数据
     * @param cardType 卡券类型
     * @return
     * @  
     */
    public static Map<String, Object> update(BasicAccount account, String cardId, CardType cardType, CardBaseInfoBean baseInfo) {
        System.out.println(cardType);
        Map<String, Object> base_info = CardUtil.updateBaseInfo(baseInfo);

        Map<String, Object> generalCoupon = new HashMap<String, Object>();
        generalCoupon.put("base_info", base_info);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(cardType.toString(), generalCoupon);
        map.put("card_id", cardId);
        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(updateUrl + "?access_token=" + access_token, map);
    }

    /**
     * 更改卡券信息  -- 会员卡
     * @param baseInfo 基本的卡券数据
     * @param bonusCleared 积分清零规则
     * @param bonusRules 积分规则
     * @param balanceRules 储值说明
     * @param prerogative 特权说明
     * @return
     * @
     */
    public static Map<String, Object> updateMemberCard(BasicAccount account, String cardId, CardBaseInfoBean baseInfo, String bonusCleared,
            String bonusRules, String balanceRules, String prerogative) {
        Map<String, Object> base_info = CardUtil.updateBaseInfo(baseInfo);

        Map<String, Object> generalCoupon = new HashMap<String, Object>();
        generalCoupon.put("base_info", base_info);
        if (!StringUtils.isBlank(bonusCleared)) {
            generalCoupon.put("bonus_cleared", bonusCleared);
        }
        if (!StringUtils.isBlank(bonusRules)) {
            generalCoupon.put("bonus_rules", bonusRules);
        }
        if (!StringUtils.isBlank(balanceRules)) {
            generalCoupon.put("balance_rules", balanceRules);
        }
        if (!StringUtils.isBlank(prerogative)) {
            generalCoupon.put("prerogative", prerogative);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("member_card", generalCoupon); //
        map.put("card_id", cardId);
        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(updateUrl + "?access_token=" + access_token, map);
    }

    /**
     * 更改卡券信息  -- 机票
     * @param baseInfo 基本的卡券数据
     * @param gate 登机口。
     * @param boardingTime 登机时间，只显示“时分”不显示日期，按时间戳格式填写。
     * @return
     * @
     */
    public static Map<String, Object> updateBoardingPass(BasicAccount account, String cardId, CardBaseInfoBean baseInfo, String gate,
            Long boardingTime) {
        Map<String, Object> base_info = CardUtil.updateBaseInfo(baseInfo);

        Map<String, Object> generalCoupon = new HashMap<String, Object>();
        generalCoupon.put("base_info", base_info);
        if (!StringUtils.isBlank(gate)) {
            generalCoupon.put("gate", gate);
        }
        if (boardingTime != null) {
            generalCoupon.put("boarding_time", boardingTime);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("boarding_pass", generalCoupon); //
        map.put("card_id", cardId);
        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(updateUrl + "?access_token=" + access_token, map);
    }

    /**
     * 更改卡券信息  -- 门票
     * @param baseInfo 基本的卡券数据
     * @param guideUrl 导览图url
     * @return
     * @
     */
    public static Map<String, Object> updateScenicTicket(BasicAccount account, String cardId, CardBaseInfoBean baseInfo, String guideUrl) {
        Map<String, Object> base_info = CardUtil.updateBaseInfo(baseInfo);

        Map<String, Object> generalCoupon = new HashMap<String, Object>();
        generalCoupon.put("base_info", base_info);
        if (!StringUtils.isBlank(guideUrl)) {
            generalCoupon.put("guide_url", guideUrl);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("scenic_ticket", generalCoupon); //
        map.put("card_id", cardId);
        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(updateUrl + "?access_token=" + access_token, map);
    }

    /**
     * 更改卡券信息  -- 电影票
     * @param baseInfo 基本的卡券数据
     * @param detail 电影票详情
     * @return
     * @
     */
    public static Map<String, Object> updateMovieTicket(BasicAccount account, String cardId, CardBaseInfoBean baseInfo, String detail) {
        Map<String, Object> base_info = CardUtil.updateBaseInfo(baseInfo);

        Map<String, Object> generalCoupon = new HashMap<String, Object>();
        generalCoupon.put("base_info", base_info);
        if (!StringUtils.isBlank(detail)) {
            generalCoupon.put("detail", detail);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("movie_ticket", generalCoupon); //
        map.put("card_id", cardId);
        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(updateUrl + "?access_token=" + access_token, map);
    }

    /**
     * 更改卡券信息  -- 会议门票
     * @param baseInfo 基本的卡券数据
     * @param mapUrl 会场导览图
     * @return
     * @
     */
    public static Map<String, Object> updateMeetingTicket(BasicAccount account, String cardId, CardBaseInfoBean baseInfo, String mapUrl) {
        Map<String, Object> base_info = CardUtil.updateBaseInfo(baseInfo);

        Map<String, Object> generalCoupon = new HashMap<String, Object>();
        generalCoupon.put("base_info", base_info);
        if (!StringUtils.isBlank(mapUrl)) {
            generalCoupon.put("map_url", mapUrl);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("meeting_ticket", generalCoupon); //
        map.put("card_id", cardId);
        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(updateUrl + "?access_token=" + access_token, map);
    }

    /**
     * 库存修改
     * @param cardId 卡券ID
     * @param increaseStockValue 增加多少库存，可以不填或填0
     * @param reduceStockValue 减少多少库存，可以不填或填0
     * @return
     * @
     */
    public static Map<String, Object> modifystock(BasicAccount account, String cardId, Integer increaseStockValue, Integer reduceStockValue) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("card_id", cardId);
        if (increaseStockValue != null) {
            map.put("increase_stock_value", increaseStockValue);
        }
        if (reduceStockValue != null) {
            map.put("reduce_stock_value", reduceStockValue);
        }

        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(modifystockUrl + "?access_token=" + access_token, map);
    }

    public static Map<String, Object> getcolors(BasicAccount account) {
        Map<String, Object> map = new HashMap<String, Object>();

        //发送
        String access_token = TokenUtil.getAccessToken(account);
        return post(getcolorsUrl + "?access_token=" + access_token, map);
    }

    public static String getCardLogoUrl() {
        return cardLogoUrl;
    }

}
