/*
* Created on :2016年6月30日
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.media.bean;

import cn.wuxia.wechat.media.enums.TempMediaTypeEnum;

public class TempMediaResult {
	// 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb，主要用于视频与音乐格式的缩略图）
	TempMediaTypeEnum type;

	// 媒体文件上传后，获取时的唯一标识
	String media_id;

	String created_at;

	public TempMediaTypeEnum getType() {
		return type;
	}

	public void setType(TempMediaTypeEnum type) {
		this.type = type;
	}

	public void setType(String type) {
		this.type = TempMediaTypeEnum.valueOf(type);
	}

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

}
