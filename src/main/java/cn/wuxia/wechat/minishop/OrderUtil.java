package cn.wuxia.wechat.minishop;

import java.util.HashMap;
import java.util.Map;

import cn.wuxia.common.util.MapUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.wechat.minishop.bean.OrderDetail;
import org.nutz.json.Json;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.minishop.bean.ProductInfo;
import cn.wuxia.wechat.token.util.TokenUtil;

public class OrderUtil extends BaseUtil {

    private final static String query_order_url = "https://api.weixin.qq.com/merchant/order/getbyid?access_token=";

    /**
     * 查找商品信息
     * @param account
     * @param productid
     *
     *
     * <pre>
     *     "order": {
    "order_id": "7197417460812533543",
    "order_status": 6,
    "order_total_price": 6,
    "order_create_time": 1394635817,
    "order_express_price": 5,
    "buyer_openid": "oDF3iY17NsDAW4UP2qzJXPsz1S9Q",
    "buyer_nick": "likeacat",
    "receiver_name": "张小猫",
    "receiver_province": "广东省",
    "receiver_city": "广州市",
    "receiver_zone": "天河区",
    "receiver_address": "华景路一号南方通信大厦5楼",
    "receiver_mobile": "123456789",
    "receiver_phone": "123456789",
    "product_id": "pDF3iYx7KDQVGzB7kDg6Tge5OKFo",
    "product_name": "安莉芳E-BRA专柜女士舒适内衣蕾丝3/4薄杯聚拢上托性感文胸KB0716",
    "product_price": 1,
    "product_sku": "10000983:10000995;10001007:10001010",
    "product_count": 1,
    "product_img": "http://img2.paipaiimg.com/00000000/item-52B87243-63CCF66C00000000040100003565C1EA.0.300x300.jpg",
    "delivery_id": "1900659372473",
    "delivery_company": "059Yunda",
    "trans_id": "1900000109201404103172199813",
    "products": [
    {
    "product_id": "p8BCTv77lY4io_q00F9qsaniimFc",
    "product_name": "product_name",
    "product_price": 1,
    "product_sku": "",
    "product_count": 1,
    "product_img": "http://mmbiz.qpic.cn/mmbiz_gif/KfrZwACMrmxj8XRiaTUzFNsTkWdTEJySicGKMHxuG0ibDfjTtb6ZIjNgakbnKq569TbBjvicSnWdnt46gEKjWe6Vcg/0?wx_fmt=gif"
    }
    ]
    }
    
     * </pre>
     * @return
     * @throws WeChatException 
     */
    public static OrderDetail queryOrder(BasicAccount account, String orderid) throws WeChatException {
        String access_token = TokenUtil.getAccessToken(account);
        String url = query_order_url + access_token;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("order_id", orderid);

        Map<String, Object> result = post(url, map);
        Map info = (Map) result.get("order");
        return BeanUtil.mapToBean(info, OrderDetail.class);
    }
}
