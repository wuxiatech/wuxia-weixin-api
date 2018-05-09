package cn.wuxia.wechat;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.nutz.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpAction;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.common.web.httpclient.HttpClientUtil;
import cn.wuxia.wechat.token.util.TokenUtil;

public class WechatHttpRequest {

    protected static Logger logger = LoggerFactory.getLogger(WechatHttpRequest.class);

    private HttpAction action;

    String access_token;

    private RequestType type = RequestType.FORM;

    private String json;

    private Map<String, Object> formdata;

    public enum RequestType {
        JSON, BYTE, FILE, FORM;
    }

    public WechatHttpRequest(String access_token) {
        this.access_token = access_token;
    }

    public static WechatHttpRequest build(String access_token) {
        return new WechatHttpRequest(access_token);
    }

    public static WechatHttpRequest build(BasicAccount account) {
        return new WechatHttpRequest(TokenUtil.getAccessToken(account));
    }

    public static WechatHttpRequest build(BasicAccount account, HttpAction action) {
        WechatHttpRequest request = new WechatHttpRequest(TokenUtil.getAccessToken(account));
        request.action = action;
        return request;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public WechatHttpRequest json(Object param) {
        this.type = RequestType.JSON;
        this.json = Json.toJson(param);
        return this;
    }

    public WechatHttpRequest json(String json) {
        this.type = RequestType.JSON;
        this.json = json;
        return this;
    }

    public void setFormdata(Map<String, Object> formdata) {
        this.formdata = formdata;
    }

    public WechatHttpRequest addParam(String name, Object value) {

        if (MapUtils.isEmpty(this.formdata)) {
            this.formdata = Maps.newHashMap();
        }
        this.formdata.put(name, value);
        return this;
    }

    public Map<String, Object> execute() throws WeChatException {
        return execute(this.action);
    }

    /**
     * 发送URL请求
     *
     * @param url
     * @param param
     * @return
     * @author songlin
     */
    public Map<String, Object> execute(HttpAction action) throws WeChatException {
        // 缓存请求微信返回JSON
        Map<String, Object> map = Maps.newHashMap();
        // 获取微信返回结果
        try {
            HttpClientResponse resp = null;
            switch (this.type) {
                case JSON:
                    resp = HttpClientUtil.postJson(action.getUrl() + "?access_token=" + access_token, json);
                    break;
                case BYTE:
                case FILE:
                case FORM:
                    resp = HttpClientRequest.create(action).addParam("access_token", access_token).setParam(formdata).execute();
                    break;
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
}
