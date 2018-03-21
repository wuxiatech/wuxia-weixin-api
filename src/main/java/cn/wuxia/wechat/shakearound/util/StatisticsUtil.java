/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.shakearound.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.wuxia.wechat.WeChatException;
import org.springframework.util.Assert;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.shakearound.bean.DeviceBean;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * 统计Util 主要用于微信摇一摇
 * @author guwen
 */
public class StatisticsUtil extends BaseUtil {

    private final static String deviceUrl = properties.getProperty("shakearound.statistics.device");

    private final static String pageUrl = properties.getProperty("shakearound.statistics.page");

    /**
     * 以设备为维度的数据统计接口
     * @param device 指定页面的设备
     * @param beginDate 起始日期时间戳，最长时间跨度为30天  10位时间戳
     * @param endDate 结束日期时间戳，最长时间跨度为30天  10位时间戳
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> device(BasicAccount account, DeviceBean device, long beginDate, long endDate)
            throws  WeChatException {
        Assert.notNull(device, "device 不能为空");
        if (device.getDeviceId() == null) {
            Assert.notNull(device.getUuid(), "uuid 不能为空或填写deviceId");
            Assert.notNull(device.getMajor(), "major 不能为空或填写deviceId");
            Assert.notNull(device.getMinor(), "minor 不能为空或填写deviceId");
        }

        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> device_identifier = new HashMap<String, Object>();
        device_identifier.put("device_id", device.getDeviceId());
        device_identifier.put("uuid", device.getUuid());
        device_identifier.put("major", device.getMajor());
        device_identifier.put("minor", device.getMinor());
        map.put("device_identifier", device_identifier);

        map.put("begin_date", beginDate);
        map.put("end_date", endDate);

        return post(deviceUrl + "?access_token=" + access_token, map);
    }

    /**
     * 以页面为维度的数据统计接口
     * @param pageId 指定页面的设备ID
     * @param beginDate 起始日期时间戳，最长时间跨度为30天  10位时间戳
     * @param endDate 结束日期时间戳，最长时间跨度为30天  10位时间戳
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> page(BasicAccount account, Integer pageId, long beginDate, long endDate) throws WeChatException {
        Assert.notNull(pageId, "pageId 不能为空");

        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page_id", pageId);
        map.put("begin_date", beginDate);
        map.put("end_date", endDate);

        return post(pageUrl + "?access_token=" + access_token, map);
    }

}
