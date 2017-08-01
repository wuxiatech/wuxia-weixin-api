/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.fans.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

import cn.wuxia.common.util.JsonUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.web.httpclient.HttpAsyncClientUtil;
import cn.wuxia.common.web.httpclient.HttpClientException;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.common.web.httpclient.HttpClientResponse;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * 粉丝相关功能
 * @author guwen
 * @ Version : V<Ver.No> <7 Apr, 2015>
 */
public class UserUtil extends BaseUtil {

    /**
     * 获取帐号的关注者列表 每次获取10000个
     * @author guwen
     * @param nextOpenid 第一个拉取的OPENID，不填默认从头开始拉取
     * @return
     */
    public static Map<String, Object> userGet(BasicAccount account, String nextOpenid) {
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + access_token;
        if (StringUtil.isNotBlank(nextOpenid)) {
            url += "&next_openid=" + nextOpenid;
        }
        HttpClientRequest param = new HttpClientRequest(url);
        return get(param);
    }

    /**
     * 得到粉丝信息
     * @author guwen
     * @param openid
     * @return
     */
    public static Map<String, Object> info(BasicAccount account, String openid) {
        Assert.notNull(openid);
        logger.info("根据openid获取微信用户信息开始");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";
        HttpClientRequest param = new HttpClientRequest(url);
        return get(param);
    }

    public static List<Map<String, Object>> infos(BasicAccount account, String... openid) {
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + access_token + "&openid=%s&lang=zh_CN";
        HttpClientRequest param = new HttpClientRequest();
        param.setUrl(url);
        HttpClientRequest[] requests = new HttpClientRequest[openid.length];
        int i = 0;
        for (String openid_ : openid) {
            String url_ = String.format(url, openid_);
            requests[i] = new HttpClientRequest();
            requests[i].setUrl(url_);
            i++;
        }
        List<HttpClientResponse> respone;
        try {
            respone = HttpAsyncClientUtil.gets(requests);
        } catch (HttpClientException e1) {
            logger.error("", e1);
            throw new RuntimeException(e1);
        }
        List<Map<String, Object>> list = Lists.newArrayList();
        for (HttpClientResponse resp : respone) {
            try {
                Map<String, Object> map = JsonUtil.fromJson(new String(resp.getByteResult(), "UTF-8"));
                if (MapUtils.isNotEmpty(map)) {
                    list.add(map);
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("", e);
            }
        }
        if (list.size() != respone.size()) {
            logger.warn("微信返回有可能为空");
        }
        return list;
    }

    /**
     * 设置备注名
     * @author guwen
     * @param openid 粉丝openid
     * @param remark 备注名
     * @return
     */
    public static Map<String, Object> updateremark(BasicAccount account, String openid, String remark) {
        Assert.hasText(openid, "openid 参数错误");
        Assert.hasText(remark, "remark 参数错误");
        String access_token = TokenUtil.getAccessToken(account);
        String url = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=" + access_token;
        HttpClientRequest param = new HttpClientRequest(url);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openid", openid);
        map.put("remark", remark);
        return post(param, map);
    }

}
