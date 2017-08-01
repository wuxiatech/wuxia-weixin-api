/*
* Created on :2015年3月3日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.shakearound.bean;

/**
 * 
 * 微信 设备类
 * @author guwen
 */
public class DeviceBean {

    private Integer deviceId;

    private String uuid;

    private Integer major;

    private Integer minor;

    public DeviceBean() {
    }

    public DeviceBean(Integer deviceId, String uuid, Integer major, Integer minor) {
        super();
        this.deviceId = deviceId;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

}
