/*
* Created on :2015年3月3日
* Author     :wuwenhao
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.card.bean;

import java.util.List;

import cn.wuxia.wechat.card.emuns.CodeType;

/**
 * 
 * 微信 基本的卡券数据 所有卡券通用
 * @author guwen
 */
public class CardBaseInfoBean {

    private String logoUrl;

    private CodeType codeType;

    private String brandName;

    private String title;

    private String subTitle;

    private String color;

    private String notice;

    private String description;

    private DateInfo dateInfo = new DateInfo();

    private Sku sku = new Sku();

    private List<Integer> locationIdList;

    private Boolean useCustomCode;

    private Boolean bindOpenid;

    private Boolean canShare;

    private Boolean canGiveFriend;

    private Integer getLimit;

    private String servicePhone;

    private String source;

    private String customUrlName;

    private String customUrl;

    private String customUrlSubTitle;

    private String promotionUrlName;

    private String promotionUrl;

    private String promotionUrlSubTitle;

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateInfo getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(DateInfo dateInfo) {
        this.dateInfo = dateInfo;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public List<Integer> getLocationIdList() {
        return locationIdList;
    }

    public void setLocationIdList(List<Integer> locationIdList) {
        this.locationIdList = locationIdList;
    }

    public Boolean getUseCustomCode() {
        return useCustomCode;
    }

    public void setUseCustomCode(Boolean useCustomCode) {
        this.useCustomCode = useCustomCode;
    }

    public Boolean getBindOpenid() {
        return bindOpenid;
    }

    public void setBindOpenid(Boolean bindOpenid) {
        this.bindOpenid = bindOpenid;
    }

    public Boolean getCanShare() {
        return canShare;
    }

    public void setCanShare(Boolean canShare) {
        this.canShare = canShare;
    }

    public Boolean getCanGiveFriend() {
        return canGiveFriend;
    }

    public void setCanGiveFriend(Boolean canGiveFriend) {
        this.canGiveFriend = canGiveFriend;
    }

    public Integer getGetLimit() {
        return getLimit;
    }

    public void setGetLimit(Integer getLimit) {
        this.getLimit = getLimit;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCustomUrlName() {
        return customUrlName;
    }

    public void setCustomUrlName(String customUrlName) {
        this.customUrlName = customUrlName;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public String getCustomUrlSubTitle() {
        return customUrlSubTitle;
    }

    public void setCustomUrlSubTitle(String customUrlSubTitle) {
        this.customUrlSubTitle = customUrlSubTitle;
    }

    public String getPromotionUrlName() {
        return promotionUrlName;
    }

    public void setPromotionUrlName(String promotionUrlName) {
        this.promotionUrlName = promotionUrlName;
    }

    public String getPromotionUrl() {
        return promotionUrl;
    }

    public void setPromotionUrl(String promotionUrl) {
        this.promotionUrl = promotionUrl;
    }

    public String getPromotionUrlSubTitle() {
        return promotionUrlSubTitle;
    }

    public void setPromotionUrlSubTitle(String promotionUrlSubTitle) {
        this.promotionUrlSubTitle = promotionUrlSubTitle;
    }

    public CodeType getCodeType() {
        return codeType;
    }

    public void setCodeType(CodeType codeType) {
        this.codeType = codeType;
    }

    /**
     * 卡卷 使用日期，有效期的信息。
     * @author Administrator
     *
     */
    public class DateInfo {

        public DateInfo() {
        }

        public DateInfo(Integer type, Long beginTimestamp, Long endTimestamp, Integer fixedTerm, Integer fixedBeginTerm) {
            super();
            this.type = type;
            this.beginTimestamp = beginTimestamp;
            this.endTimestamp = endTimestamp;
            this.fixedTerm = fixedTerm;
            this.fixedBeginTerm = fixedBeginTerm;
        }

        private Integer type;

        private Long beginTimestamp;

        private Long endTimestamp;

        private Integer fixedTerm;

        private Integer fixedBeginTerm;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Long getBeginTimestamp() {
            return beginTimestamp;
        }

        public void setBeginTimestamp(Long beginTimestamp) {
            this.beginTimestamp = beginTimestamp;
        }

        public Long getEndTimestamp() {
            return endTimestamp;
        }

        public void setEndTimestamp(Long endTimestamp) {
            this.endTimestamp = endTimestamp;
        }

        public Integer getFixedTerm() {
            return fixedTerm;
        }

        public void setFixedTerm(Integer fixedTerm) {
            this.fixedTerm = fixedTerm;
        }

        public Integer getFixedBeginTerm() {
            return fixedBeginTerm;
        }

        public void setFixedBeginTerm(Integer fixedBeginTerm) {
            this.fixedBeginTerm = fixedBeginTerm;
        }

    }

    /**
     * 商品信息
     *
     */
    public class Sku {
        private Integer quantity;

        public Sku() {
        }

        public Sku(Integer quantity) {
            super();
            this.quantity = quantity;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

}
