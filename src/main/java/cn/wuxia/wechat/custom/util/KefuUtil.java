package cn.wuxia.wechat.custom.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import cn.wuxia.common.util.MapUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.web.httpclient.HttpClientRequest;
import cn.wuxia.wechat.Account;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.custom.bean.KefuAccount;
import cn.wuxia.wechat.custom.bean.KefuSession;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 客服服务接口
 * 
 * @author songlin
 *
 */
public class KefuUtil extends BaseUtil {

    /**
     * 获取所有的客服账号
     * 
     * @return
     */
    public static List<KefuAccount> getAllKefu(Account account) {
        String url = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=" + TokenUtil.getAccessToken(account);

        HttpClientRequest param = new HttpClientRequest(url);
        Map<String, Object> result = get(param);
        List<KefuAccount> accounts = Lists.newArrayList();
        if (MapUtil.isNotEmpty(result)) {
            List<Map> list = (List) result.get("kf_list");
            for (Map m : list) {
                accounts.add(BeanUtil.mapToBean(m, KefuAccount.class));
            }
        }
        return accounts;
    }

    /**
     * 此接口在客服和用户之间创建一个会话，如果该客服和用户会话已存在，则直接返回0。指定的客服帐号必须已经绑定微信号且在线。
     * 
     * @param account
     * @param kfAccount
     * @param openid
     * @return
     */
    public static boolean createSession(Account account, KefuAccount kfAccount, String openid) {
        String url = "https://api.weixin.qq.com/customservice/kfsession/create?access_token=" + TokenUtil.getAccessToken(account);

        HttpClientRequest request = new HttpClientRequest(url);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("openid", openid);
        param.put("kf_account", kfAccount.getKf_account());
        Map<String, Object> result = post(request, param);
        if (MapUtil.isNotEmpty(result)) {
            if (StringUtil.equalsIgnoreCase("ok", (String) result.get("errmsg"))) {
                return true;
            }

        }
        return false;
    }

    /**
     * 
     * @param account
     * @param kfAccount
     * @param openid
     * @return
     */
    public static boolean closeSession(Account account, KefuAccount kfAccount, String openid) {
        String url = "https://api.weixin.qq.com/customservice/kfsession/close?access_token=" + TokenUtil.getAccessToken(account);

        HttpClientRequest request = new HttpClientRequest(url);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("openid", openid);
        param.put("kf_account", kfAccount.getKf_account());
        Map<String, Object> result = post(request, param);
        if (MapUtil.isNotEmpty(result)) {
            if (StringUtil.equalsIgnoreCase("ok", (String) result.get("errmsg"))) {
                return true;
            }

        }
        return false;
    }

    /**
     * 此接口获取一个客户的会话，如果不存在，则kf_account为空
     * 
     * @param account
     * @param openid
     * @return
     */
    public static KefuSession getSession(Account account, String openid) {
        String url = "https://api.weixin.qq.com/customservice/kfsession/getsession?access_token=" + TokenUtil.getAccessToken(account) + "&openid="
                + openid;

        HttpClientRequest request = new HttpClientRequest(url);
        Map<String, Object> result = get(request);
        /**
         * 返回数据示例（正确时的JSON返回结果）： { "createtime" : 123456789, "kf_account" :
         * "test1@test" }
         */
        if (MapUtil.isNotEmpty(result)) {
            return BeanUtil.mapToBean(result, KefuSession.class);

        }
        return null;
    }

    public static KefuSession getSession(Account account, KefuAccount kefuAccount) {
        String url = "https://api.weixin.qq.com/customservice/kfsession/getsessionlist?access_token=" + TokenUtil.getAccessToken(account)
                + "&kf_account=" + kefuAccount.getKf_account();

        HttpClientRequest request = new HttpClientRequest(url);
        Map<String, Object> result = get(request);
        /**
         * { "sessionlist" : [ { "createtime" : 123456789, "openid" : "OPENID"
         * }, { "createtime" : 123456789, "openid" : "OPENID" } ] }
         */
        List<KefuSession> l = Lists.newArrayList();
        if (MapUtil.isNotEmpty(result)) {
            List<Map> list = (List) result.get("sessionlist");
            for (Map m : list) {
                l.add(BeanUtil.mapToBean(m, KefuSession.class));
            }
        }
        return null;
    }
}