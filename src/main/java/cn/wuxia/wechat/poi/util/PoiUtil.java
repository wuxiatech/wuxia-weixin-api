/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.poi.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.poi.bean.PoiBaseInfoBean;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 门店管理
 * @author guwen
 *
 */
public class PoiUtil extends BaseUtil {

    private final static String addpoiUrl = properties.getProperty("poi.addpoi");

    private final static String getpoiUrl = properties.getProperty("poi.getpoi");

    private final static String getpoilistUrl = properties.getProperty("poi.getpoilist");

    private final static String delpoiUrl = properties.getProperty("poi.delpoi");

    private final static String updatepoiUrl = properties.getProperty("poi.updatepoi");

    /**
     * 创建门店
     * @param baseInfo 基本的门店信息
     * @return
     * @throws UnsupportedEncodingException
     * @throws WeChatException 
     */
    public static Map<String, Object> addpoi(BasicAccount account, PoiBaseInfoBean baseInfo) throws UnsupportedEncodingException, WeChatException {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> base_info = new HashMap<String, Object>();
        if (!StringUtils.isBlank(baseInfo.getSid())) {
            base_info.put("sid", baseInfo.getSid());
        }
        base_info.put("business_name", baseInfo.getBusinessName());
        if (!StringUtils.isBlank(baseInfo.getBranchName())) {
            base_info.put("branch_name", baseInfo.getBranchName());
        }
        base_info.put("province", baseInfo.getProvince());
        base_info.put("city", baseInfo.getCity());
        if (!StringUtils.isBlank(baseInfo.getDistrict())) {
            base_info.put("district", baseInfo.getDistrict());
        }
        base_info.put("address", baseInfo.getAddress());
        base_info.put("telephone", baseInfo.getTelephone());
        base_info.put("categories", baseInfo.getCategories());
        base_info.put("offset_type", baseInfo.getOffsetType());
        base_info.put("longitude", baseInfo.getLongitude());
        base_info.put("latitude", baseInfo.getLatitude());
        //图片列表
        List<String> photoList = baseInfo.getPhotoList();
        List<Map<String, String>> photo_list = new ArrayList<Map<String, String>>();
        for (String item : photoList) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("photo_url", item);
            photo_list.add(m);
        }
        base_info.put("photo_list", photo_list);

        if (!StringUtils.isBlank(baseInfo.getRecommend())) {
            base_info.put("recommend", baseInfo.getRecommend());
        }
        base_info.put("special", baseInfo.getSpecial());
        if (!StringUtils.isBlank(baseInfo.getIntroduction())) {
            base_info.put("introduction", baseInfo.getIntroduction());
        }
        base_info.put("openTime", baseInfo.getOpenTime());
        if (baseInfo.getAvgPrice() != null) {
            base_info.put("avg_price", baseInfo.getAvgPrice());
        }

        Map<String, Object> business = new HashMap<String, Object>();
        business.put("base_info", base_info);

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("business", business);

        return post(addpoiUrl + "?access_token=" + access_token, postData);
    }

    /**
     * 查询门店信息
     * @param poiId  poi_id
     * @return
     * @throws UnsupportedEncodingException
     * @throws WeChatException 
     */
    public static Map<String, Object> getpoi(BasicAccount account, String poiId) throws UnsupportedEncodingException, WeChatException {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("poi_id", poiId);

        return post(getpoiUrl + "?access_token=" + access_token, postData);
    }

    /**
     * 查询门店列表
     * @param begin 开始位置，0 即为从第一条开始查询
     * @param limit 返回数据条数，最大允许50，默认为20
     * @return
     * @throws UnsupportedEncodingException
     * @throws WeChatException 
     */
    public static Map<String, Object> getpoilist(BasicAccount account, Integer begin, Integer limit) throws UnsupportedEncodingException, WeChatException {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("begin", begin);
        postData.put("limit", limit);

        return post(getpoilistUrl + "?access_token=" + access_token, postData);
    }

    /**
     * 删除门店
     * @param poiId 门店ID
     * @return
     * @throws UnsupportedEncodingException
     * @throws WeChatException 
     */
    public static Map<String, Object> delpoi(BasicAccount account, String poiId) throws UnsupportedEncodingException, WeChatException {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("poi_id", poiId);

        return post(delpoiUrl + "?access_token=" + access_token, postData);
    }

    /**
     * 修改门店服务信息
     * @param baseInfo   基本的门店信息
     * @return
     * @throws UnsupportedEncodingException
     * @throws WeChatException 
     */
    public static Map<String, Object> updatepoi(BasicAccount account, PoiBaseInfoBean baseInfo) throws UnsupportedEncodingException, WeChatException {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> base_info = new HashMap<String, Object>();
        base_info.put("poi_id", baseInfo.getPoiId());
        if (!StringUtils.isBlank(baseInfo.getTelephone())) {
            base_info.put("telephone", baseInfo.getTelephone());
        }
        if (baseInfo.getPhotoList() != null && baseInfo.getPhotoList().size() > 0) {
            List<String> photoList = baseInfo.getPhotoList();
            List<Map<String, String>> photo_list = new ArrayList<Map<String, String>>();
            for (String item : photoList) {
                Map<String, String> m = new HashMap<String, String>();
                m.put("photo_url", item);
                photo_list.add(m);
            }
            base_info.put("photo_list", photo_list);
        }
        if (!StringUtils.isBlank(baseInfo.getRecommend())) {
            base_info.put("recommend", baseInfo.getRecommend());
        }
        if (!StringUtils.isBlank(baseInfo.getSpecial())) {
            base_info.put("special", baseInfo.getSpecial());
        }
        if (!StringUtils.isBlank(baseInfo.getIntroduction())) {
            base_info.put("introduction", baseInfo.getIntroduction());
        }
        if (!StringUtils.isBlank(baseInfo.getOpenTime())) {
            base_info.put("open_time", baseInfo.getOpenTime());
        }
        if (baseInfo.getAvgPrice() != null) {
            base_info.put("avg_price", baseInfo.getAvgPrice());
        }

        Map<String, Object> business = new HashMap<String, Object>();
        business.put("base_info", base_info);

        Map<String, Object> postData = Maps.newHashMap();
        postData.put("business", business);

        return post(updatepoiUrl + "?access_token=" + access_token, postData);
    }

}
