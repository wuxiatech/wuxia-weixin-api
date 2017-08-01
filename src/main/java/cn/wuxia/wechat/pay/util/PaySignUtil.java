package cn.wuxia.wechat.pay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import cn.wuxia.common.util.MD5Util;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.PayAccount;

public class PaySignUtil extends BaseUtil {

    /**
     * @Description：sign签名
     * @param characterEncoding 编码格式
     * @param parameters 请求参数
     * @return
     */
    public static String createSign(PayAccount account, SortedMap<String, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + account.getAppKey());
        String sign = MD5Util.MD5HexEncode(sb.toString(), "UTF-8").toUpperCase();
        return sign;
    }

    /**
     * 用于ASCII码从小到大排序
     * @author wuwenhao
     * @param packageParams
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getSing(SortedMap<String, Object> signParams) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        Set es = signParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(k + "=" + UrlEncode(v) + "&");
        }

        // 转换成String
        String packageValue = sb.toString();
        System.out.println("packageValue=" + packageValue);
        return packageValue;
    }

    public static String UrlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, "UTF-8").replace("+", "%20");
    }

    /**
     * 随机字符串
     * @author wuwenhao
     * @return
     */
    public static String getNonceStr() {
        Random random = new Random();
        return MD5Util.MD5HexEncode(String.valueOf(random.nextInt(10000)), "UTF-8");
    }

    /**
     * 当前时候
     * @author wuwenhao
     * @return
     */
    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

}
