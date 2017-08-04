package cn.wuxia.wechat;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import cn.wuxia.common.cached.CacheClient;
import cn.wuxia.common.util.ClassLoaderUtil;
import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.util.PropertiesUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;

/**
 * 
 * [ticket id] 微信工具类
 * 
 * @author wuwenhao @ Version : V<Ver.No> <2015年4月1日>
 */
public class BaseUtil {

    protected static Logger logger = LoggerFactory.getLogger(BaseUtil.class);

    // 获取配置文件
    public static Properties properties;

    public final static int expTime = 3600;

    private static CacheClient cacheClient;

    private final static String CACHE_NAME_SPACE = "wxcachespace";
    static {
        if (properties == null) {
            properties = PropertiesUtils.loadProperties("classpath*:wechat.config.properties");
        }
        // CACHED地址
        String cachedAddr = properties.getProperty("cached.addr");
        String cacheImpl = properties.getProperty("cache.impl");
        if (StringUtil.isBlank(cachedAddr) || StringUtil.isBlank(cacheImpl)) {
            logger.error("请检查wechat.config.properties的cached.addr及cache.impl");
            throw new RuntimeException("请检查wechat.config.properties的cached.addr及cache.impl");
        }
        try {
            logger.info("初始化memcached {} 服务器：{}", cacheImpl, cachedAddr);
            cacheClient = (CacheClient) ClassLoaderUtil.loadClass(cacheImpl).newInstance();
            cacheClient.init(StringUtil.split(cachedAddr, ","));
        } catch (InstantiationException | IllegalAccessException e) {
            logger.warn("远程cached服务器{" + cachedAddr + "}不可用", e);
            // 如果远程memcached服务器不可用，则启动本机虚拟memcached服务器
        }
    }

    //    // 转码
    //    protected static String byteToHex(final byte[] hash) {
    //        Formatter formatter = new Formatter();
    //        for (byte b : hash) {
    //            formatter.format("%02x", b);
    //        }
    //        String result = formatter.toString();
    //        formatter.close();
    //        return result;
    //    }

    /**
     * default is in namespace
     * 
     * @author songlin
     * @return
     */
    protected static CacheClient getCache() {
        if (cacheClient == null) {
            logger.error("请检查是否已配置Cache");
            throw new RuntimeException("请检查是否已配置Cache");
        } else {
            return cacheClient;
        }
    }

    protected static Object getOutCache(String appid, String key) {
        // 增加不同公众号的不同cache
        key += appid;
        CacheClient cache = getCache();
        /**
         * sha1加密,返回40位十六进制字符串
         */
        key = DigestUtils.sha1Hex(StringUtils.getBytesUtf8(key));
        /**
         * 限制key的长度为250个字符
         */
        Object value = cache.get(key, CACHE_NAME_SPACE);
        if (value == null)
            return null;
        logger.debug("getOutCache and key is :" + key + " , value is :" + value.toString());
        return value;
    }

    protected static void putInCache(String appid, String key, Object value) {
        // 增加不同公众号的不同cache
        key += appid;
        CacheClient cache = getCache();
        /**
         * sha1加密,返回40位十六进制字符串
         */
        key = DigestUtils.sha1Hex(StringUtils.getBytesUtf8(key));
        // 将旧的删除再新增
        cache.delete(key, CACHE_NAME_SPACE);
        cache.set(key, value, expTime, CACHE_NAME_SPACE);
        Object v = cache.get(key, CACHE_NAME_SPACE);
        if (v == null) {
            logger.error("can't put in cache key[{}], value[{}]", key, value);
        } else {
            logger.debug("putInCache and key is :" + key + " , value is :" + value.toString());
        }

    }

    /**
     * 移除某个缓存
     * 
     * @author songlin
     * @param key
     * @throws WeChatException
     */
    protected static void removeCache(String appid, String key) {
        // 增加不同公众号的不同cache
        key += appid;
        CacheClient cache = getCache();
        key = DigestUtils.sha1Hex(StringUtils.getBytesUtf8(key));
        Object value = cache.get(key, CACHE_NAME_SPACE);
        logger.debug("deleteCache and key is :" + key + " , value is :" + value.toString());
        cache.delete(key, CACHE_NAME_SPACE);
    }

    /**
     * 清除缓存
     * @author songlin
     */
    protected static void cleanCache() {
        logger.info("清除微信api缓存，namespace：{}", CACHE_NAME_SPACE);
        getCache().flush(CACHE_NAME_SPACE);
    }

    protected static Map<String, Object> post(HttpClientRequest httpParam) {
        return post(httpParam, null);
    }

    // 发送URL请求
    protected static Map<String, Object> post(HttpClientRequest httpParam, Map param) {
        // 缓存请求微信返回JSON
        Map<String, Object> map = Maps.newHashMap();
        // 获取微信返回结果
        try {
            HttpClientResponse resp = null;
            if (MapUtils.isEmpty(param)) {
                resp = HttpClientUtil.post(httpParam);
            } else {
                resp = HttpClientUtil.postJSON(httpParam, JsonUtil.toJson(param));
            }
            resp.setCharset("UTF-8");
            map = JsonUtil.fromJson(resp.getStringResult());
            logger.info("微信返回结果：{}", map);
        } catch (Exception e) {
            logger.error("请求微信 API有误！", e);
            return null;
        }
        if (org.apache.commons.collections4.MapUtils.isNotEmpty(map) && StringUtil.isNotBlank(map.get("errcode"))
                && !StringUtil.equals(map.get("errcode") + "", "0")) {
            logger.error(map.get("errmsg").toString());
        }
        return map;
    }

    // 发送URL请求
    protected static Map<String, Object> get(HttpClientRequest httpParam) {
        // 缓存请求微信返回JSON
        Map<String, Object> map = Maps.newHashMap();
        // 获取微信返回结果
        try {
            HttpClientResponse httpUrl = HttpClientUtil.get(httpParam);
            httpUrl.setCharset("UTF-8");
            map = JsonUtil.fromJson(httpUrl.getStringResult());
            logger.info("微信返回结果：{}", map);
        } catch (Exception e) {
            logger.error("请求微信 API有误！", e);
        }
        if (org.apache.commons.collections4.MapUtils.isNotEmpty(map) && StringUtil.isNotBlank(map.get("errcode"))
                && !StringUtil.equals(map.get("errcode") + "", "0")) {
            logger.error(map.get("errmsg").toString());
        }
        return map;
    }

}
