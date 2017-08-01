/*
* Created on :2016年6月30日
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.media.bean;

import cn.wuxia.wechat.media.enums.MaterialMediaTypeEnum;

public class MaterialMediaResult {
	// 媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb，主要用于视频与音乐格式的缩略图）
	MaterialMediaTypeEnum type;

	// 媒体文件上传后，获取时的唯一标识
	String media_id;

	String url;

	public MaterialMediaTypeEnum getType() {
		return type;
	}

	public void setType(MaterialMediaTypeEnum type) {
		this.type = type;
	}

	public void setType(String type) {
		this.type = MaterialMediaTypeEnum.valueOf(type);
	}

	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
