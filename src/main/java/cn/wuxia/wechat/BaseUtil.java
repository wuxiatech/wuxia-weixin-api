package cn.wuxia.wechat;

import java.util.Map;
import java.util.Properties;

import cn.wuxia.common.util.*;
import cn.wuxia.common.util.reflection.ReflectionUtil;
import cn.wuxia.common.web.httpclient.*;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.nutz.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import cn.wuxia.common.cached.CacheClient;

/**
 * [ticket id] 微信工具类
 *
 * @author wuwenhao @ Version : V<Ver.No> <2015年4月1日>
 */
public abstract class BaseUtil {

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
     * @return
     * @author songlin
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
     * @param key
     * @throws WeChatException
     * @author songlin
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
     *
     * @author songlin
     */
    protected static void cleanCache() {
        logger.info("清除微信api缓存，namespace：{}", CACHE_NAME_SPACE);
        getCache().flush(CACHE_NAME_SPACE);
    }

    @Deprecated
    protected static Map<String, Object> post(HttpClientRequest httpParam) throws WeChatException {
        return post(httpParam.getUrl() + (StringUtil.indexOf(httpParam.getUrl(), "?") > 0 ? "" : "?") + httpParam.getQueryString(), null);
    }

    @Deprecated
    protected static Map<String, Object> post(String url) throws WeChatException {
        return post(url, null);
    }

    /**
     * 发送URL请求
     *
     * @param url
     * @param param
     * @return
     * @author songlin
     */
    @Deprecated
    protected static Map<String, Object> post(String url, Object param) throws WeChatException {
        // 缓存请求微信返回JSON
        Map<String, Object> map = Maps.newHashMap();
        // 获取微信返回结果
        try {
            HttpClientResponse resp = null;
            if (param == null) {
                resp = HttpClientUtil.post(new HttpClientRequest(url));
            } else {
                resp = HttpClientUtil.postJson(url, Json.toJson(param));
            }
            resp.setCharset("UTF-8");

            map = Json.fromJson(Map.class, resp.getStringResult());
            logger.info("微信返回结果：{}", map);
        } catch (Exception e) {
            logger.error("请求微信 API有误！", e);
            throw new WeChatException(e.getMessage());
        }
        if (MapUtils.isNotEmpty(map) && ((StringUtil.isNotBlank(map.get("errcode")) && MapUtils.getIntValue(map, "errcode") != 0))
                || (StringUtil.isNotBlank(MapUtils.getString(map, "errmsg"))
                        && !StringUtil.equalsIgnoreCase(MapUtils.getString(map, "errmsg"), "ok"))) {
            logger.error("errmsg={}", map);
            throw new WeChatException(MapUtils.getString(map, "errmsg"));
        }
        return map;
    }

    /**
     * @param httpParam
     * @return
     * @author songlin
     * @see {@link BaseUtil#get(String)}
     */
    @Deprecated
    // 发送URL请求
    protected static Map<String, Object> get(HttpClientRequest httpParam) throws WeChatException {
        // 缓存请求微信返回JSON
        Map<String, Object> map = Maps.newHashMap();
        // 获取微信返回结果
        try {
            HttpClientResponse httpUrl = HttpClientUtil.get(httpParam);
            httpUrl.setCharset("UTF-8");
            map = Json.fromJson(Map.class, httpUrl.getStringResult());
            logger.info("微信返回结果：{}", map);
        } catch (Exception e) {
            logger.error("请求微信 API有误！", e);
            throw new WeChatException(e.getMessage());
        }
        if (MapUtils.isNotEmpty(map) && ((StringUtil.isNotBlank(map.get("errcode")) && MapUtils.getIntValue(map, "errcode") != 0))
                || (StringUtil.isNotBlank(MapUtils.getString(map, "errmsg"))
                        && !StringUtil.equalsIgnoreCase(MapUtils.getString(map, "errmsg"), "ok"))) {
            logger.error("errmsg={}", map);
            throw new WeChatException(MapUtils.getString(map, "errmsg"));
        }
        return map;
    }

    /**
     * 发送URL请求Description of the method
     *
     * @param httpParam
     * @return
     * @author songlin
     */
    @Deprecated
    protected static Map<String, Object> get(String url) throws WeChatException {
        // 缓存请求微信返回JSON
        Map<String, Object> map = Maps.newHashMap();
        // 获取微信返回结果
        try {
            HttpClientResponse httpUrl = HttpClientUtil.get(new HttpClientRequest(url));
            httpUrl.setCharset("UTF-8");
            map = Json.fromJson(Map.class, httpUrl.getStringResult());
            logger.info("微信返回结果：{}", map);
        } catch (Exception e) {
            logger.error("请求微信 API有误！", e);
            throw new WeChatException(e.getMessage());
        }
        if (MapUtils.isNotEmpty(map) && ((StringUtil.isNotBlank(map.get("errcode")) && MapUtils.getIntValue(map, "errcode") != 0))
                || (StringUtil.isNotBlank(MapUtils.getString(map, "errmsg"))
                        && !StringUtil.equalsIgnoreCase(MapUtils.getString(map, "errmsg"), "ok"))) {
            logger.error("errmsg={}", map);
            throw new WeChatException(MapUtils.getString(map, "errmsg"));
        }
        return map;
    }



}
