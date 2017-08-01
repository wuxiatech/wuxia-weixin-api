/*
* Created on :Jan 10, 2015
* Author     :songlin
* 支付平台
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.media.enums;

public enum TempMediaTypeEnum {

    //媒体文件类型，分别有图片
    //单位KB
    image(1024, "1M，支持JPG格式"),
    //语音
    voice(2048, "2M，播放长度不超过60s，支持AMR/MP3格式"),
    //视频
    video(10240, "10MB，支持MP4格式"),
    //缩略图
    thumb(64, "64KB，支持JPG格式");

    private int size;

    private String desc;

    private TempMediaTypeEnum(int size, String desc) {
        this.desc = desc;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String getDesc() {
        return desc;
    }

}
