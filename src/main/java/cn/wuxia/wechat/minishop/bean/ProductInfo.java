package cn.wuxia.wechat.minishop.bean;

import java.util.List;

import org.nutz.json.JsonField;

public class ProductInfo {
    @JsonField("product_id")
    String productid;

    String name;

    @JsonField("category_id")
    String[] categoryid;

    String[] img;

    List<ProductProperty> property;

    @JsonField("sku_info")
    List<ProductProperty> skuInfo;

    @JsonField("buy_limit")
    int buyLimit;

    @JsonField("main_img")
    String mainImg;

    @JsonField("detail_html")
    String detailHtml;

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String[] categoryid) {
        this.categoryid = categoryid;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        this.img = img;
    }

    public List<ProductProperty> getProperty() {
        return property;
    }

    public void setProperty(List<ProductProperty> property) {
        this.property = property;
    }

    public List<ProductProperty> getSkuInfo() {
        return skuInfo;
    }

    public void setSkuInfo(List<ProductProperty> skuInfo) {
        this.skuInfo = skuInfo;
    }

    public int getBuyLimit() {
        return buyLimit;
    }

    public void setBuyLimit(int buyLimit) {
        this.buyLimit = buyLimit;
    }

    public String getMainImg() {
        return mainImg;
    }

    public void setMainImg(String mainImg) {
        this.mainImg = mainImg;
    }

    public String getDetailHtml() {
        return detailHtml;
    }

    public void setDetailHtml(String detailHtml) {
        this.detailHtml = detailHtml;
    }
}
