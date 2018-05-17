package cn.wuxia.wechat.minishop;

import java.util.HashMap;
import java.util.Map;

import cn.wuxia.wechat.WeChatException;
import org.nutz.json.Json;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.minishop.bean.ProductInfo;
import cn.wuxia.wechat.token.util.TokenUtil;

public class ProductUtil extends BaseUtil {

    private final static String query_product_url = "https://api.weixin.qq.com/merchant/get?access_token=";

    /**
     * 查找商品信息
     * @param account
     * @param productid
     * @return
     */
    public static ProductInfo queryProduct(BasicAccount account, String productid) throws WeChatException {
        String access_token = TokenUtil.getAccessToken(account);
        String url = query_product_url + access_token;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("product_id", productid);

        Map<String, Object> result = post(url, map);
        Map info = (Map) result.get("product_info");

        Map base = (Map) info.get("product_base");
        String json = Json.toJson(base);
        ProductInfo productInfo = Json.fromJson(ProductInfo.class, json);
        productInfo.setProductid(productid);
        return productInfo;
    }
}
