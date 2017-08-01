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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.shakearound.bean.DeviceBean;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * 置备Util 主要用于微信摇一摇
 * @author guwen
 */
public class DeviceUtil extends BaseUtil {

    private final static String applyUrl = properties.getProperty("shakearound.device.apply");

    private final static String updateUrl = properties.getProperty("shakearound.device.update");

    private final static String bindlocationUrl = properties.getProperty("shakearound.device.bindlocation");

    private final static String searchUrl = properties.getProperty("shakearound.device.search");

    private final static String bindpageUrl = properties.getProperty("shakearound.device.bindpage");

    /**
     * 申请设备ID
     * @param quantity 申请的设备ID的数量，单次新增设备超过500个，需走人工审核流程
     * @param applyReason 申请理由，不超过100个字
     * @param comment 备注，不超过15个汉字或30个英文字母 可空
     * @param poiId 设备关联的门店ID 可空
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static Map<String, Object> apply(BasicAccount account, int quantity, String applyReason, String comment, String poiId)
            throws UnsupportedEncodingException {
        Assert.isTrue(quantity > 0 && quantity <= 500, "quantity必须大于0并且小于等于500");
        Assert.hasText(applyReason, "applyReason为必填");
        Assert.isTrue(applyReason.length() <= 100, "applyReason 超过100个字");

        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(applyUrl + "?access_token=" + access_token);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("quantity", quantity);
        map.put("apply_reason", applyReason);
        if (!StringUtils.isBlank(comment)) {
            Assert.isTrue(comment.length() <= 30, "comment 长度超出");
            map.put("comment", comment);
        }
        if (StringUtils.isNotEmpty(poiId)) {
            map.put("poi_id", poiId);
        }
        return post(param, map);
    }

    /**
     * 编辑设备信息
     * @param deviceId 设备编号，若填了UUID、major、minor，则可不填设备编号，若二者都填，则以设备编号为优先
     * @param uuid UUID、major、minor，三个信息需填写完整，若填了设备编号，则可不填此信息。
     * @param major UUID、major、minor，三个信息需填写完整，若填了设备编号，则可不填此信息。
     * @param minor UUID、major、minor，三个信息需填写完整，若填了设备编号，则可不填此信息。
     * @param comment 设备的备注信息，不超过15个汉字或30个英文字母。
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> udpate(BasicAccount account, Integer deviceId, String uuid, Integer major, Integer minor, String comment)
            throws UnsupportedEncodingException {
        Assert.hasText(comment, "comment 为必填");
        if (deviceId == null) {
            Assert.notNull(uuid, "uuid 不能为空或填写deviceId");
            Assert.notNull(major, "major 不能为空或填写deviceId");
            Assert.notNull(minor, "minor 不能为空或填写deviceId");
        }

        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(updateUrl + "?access_token=" + access_token);

        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> device_identifier = new HashMap<String, Object>();
        device_identifier.put("device_id", deviceId);
        device_identifier.put("uuid", uuid);
        device_identifier.put("major", major);
        device_identifier.put("minor", minor);
        map.put("device_identifier", device_identifier);
        map.put("comment", comment);

        return post(param, map);
    }

    /**
     * 配置设备与门店的关联关系
     * @param deviceId 设备编号，若填了UUID、major、minor，则可不填设备编号，若二者都填，则以设备编号为优先
     * @param uuid UUID、major、minor，三个信息需填写完整，若填了设备编号，则可不填此信息
     * @param major UUID、major、minor，三个信息需填写完整，若填了设备编号，则可不填此信息
     * @param minor UUID、major、minor，三个信息需填写完整，若填了设备编号，则可不填此信息
     * @param poiId 设备关联的门店ID
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> bindlocation(BasicAccount account, Integer deviceId, String uuid, Integer major, Integer minor, Integer poiId)
            throws UnsupportedEncodingException {
        if (deviceId == null) {
            Assert.notNull(uuid, "uuid 不能为空或填写deviceId");
            Assert.notNull(major, "major 不能为空或填写deviceId");
            Assert.notNull(minor, "minor 不能为空或填写deviceId");
        }
        Assert.notNull(poiId, "poiId 不能为空");

        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(bindlocationUrl + "?access_token=" + access_token);

        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> device_identifier = new HashMap<String, Object>();
        device_identifier.put("device_id", deviceId);
        device_identifier.put("uuid", uuid);
        device_identifier.put("major", major);
        device_identifier.put("minor", minor);
        map.put("device_identifier", device_identifier);
        map.put("poi_id", poiId);
        return post(param, map);
    }

    /**
     * 查询设备列表  -- 查询指定设备时
     * @param deviceList 要查询的设备列表，可同时查询多个
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> search(BasicAccount account, List<DeviceBean> deviceList) throws UnsupportedEncodingException {
        Assert.notEmpty(deviceList, "deviceList 不能为空");

        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(searchUrl + "?access_token=" + access_token);

        Map<String, Object> map = new HashMap<String, Object>();
        List<Object> device_identifiers = new ArrayList<Object>();
        for (DeviceBean item : deviceList) {
            Map<String, Object> deviceJo = new HashMap<String, Object>();
            deviceJo.put("device_id", item.getDeviceId());
            deviceJo.put("uuid", item.getUuid());
            deviceJo.put("major", item.getMajor());
            deviceJo.put("minor", item.getMinor());
            device_identifiers.add(deviceJo);
        }

        map.put("type", 1);
        map.put("device_identifiers", device_identifiers);
        return post(param, map);
    }

    /**
     * 查询设备列表  -- 需要分页查询或者指定范围内的设备时
     * @param begin 设备列表的起始索引值
     * @param count 待查询的设备个数
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> search(BasicAccount account, int begin, int count) throws UnsupportedEncodingException {
        Assert.isTrue(begin >= 0, "begin必须大于等于0");
        Assert.isTrue(count > 0 && count <= 50, "count必须大于0,小于等于50");

        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(searchUrl + "?access_token=" + access_token);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", 2);
        map.put("begin", begin);
        map.put("count", count);
        return post(param, map);
    }

    /**
     * 查询设备列表  -- 当需要根据批次ID查询时
     * @param applyId 批次ID，申请设备ID时所返回的批次ID
     * @param begin 设备列表的起始索引值
     * @param count 待查询的设备个数
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> search(BasicAccount account, int applyId, int begin, int count) throws UnsupportedEncodingException {
        Assert.isTrue(begin >= 0, "begin必须大于等于0");
        Assert.isTrue(count > 0 && count <= 50, "count必须大于0,小于等于50");

        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(searchUrl + "?access_token=" + access_token);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", 3);
        map.put("begin", begin);
        map.put("count", count);
        map.put("apply_id", applyId);
        return post(param, map);
    }

    /**
     * 绑定页面
     * @author guwen
     * @param device 指定页面的设备
     * @param pageIds 待关联的页面列表
     * @param bind 关联操作标志位， 0为解除关联关系，1为建立关联关系
     * @param append 新增操作标志位， 0为覆盖，1为新增
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Map<String, Object> bindpage(BasicAccount account, DeviceBean device, List<Integer> pageIds, Integer bind, Integer append) {
        Assert.notNull(device, "device 不能为空");
        Assert.notEmpty(pageIds, "pageIds 不能为空");
        Assert.isTrue(bind == 0 || bind == 1, "bind 只能为0或1");
        Assert.isTrue(append == 0 || append == 1, "append 只能为0或1");

        HttpClientRequest param = new HttpClientRequest();
        String access_token = TokenUtil.getAccessToken(account);
        param.setUrl(bindpageUrl + "?access_token=" + access_token);

        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> device_identifier = new HashMap<String, Object>();
        device_identifier.put("device_id", device.getDeviceId());
        device_identifier.put("uuid", device.getUuid());
        device_identifier.put("major", device.getMajor());
        device_identifier.put("minor", device.getMinor());
        map.put("device_identifier", device_identifier);

        List<Object> page_ids = new ArrayList<Object>();
        for (Integer item : pageIds) {
            page_ids.add(item);
        }
        map.put("page_ids", page_ids);

        map.put("bind", bind);
        map.put("append", append);

        return post(param, map);
    }

}
