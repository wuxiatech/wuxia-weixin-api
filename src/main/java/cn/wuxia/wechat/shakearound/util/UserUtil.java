/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 songlin.li All right reserved.
*/
package cn.wuxia.wechat.shakearound.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.BasicAccount;
import cn.wuxia.wechat.WeChatException;
import cn.wuxia.wechat.token.util.TokenUtil;

/**
 * 
 * 用户Util 主要用于微信摇一摇
 * @author guwen
 */
public class UserUtil extends BaseUtil {

    private final static String getshakeinfoUrl = properties.getProperty("shakearound.user.getshakeinfo");

    /**
    * 获取摇周边的设备及用户信息 
    * @param ticket 摇周边业务的ticket，可在摇到的URL中得到，ticket生效时间为30分钟，每一次摇都会重新生成新的ticket
    * @param needPoi 是否需要返回门店poi_id，传1则返回，否则不返回
    * @return
    * @throws UnsupportedEncodingException
     * @throws WeChatException 
    */
    public static Map<String, Object> getshakeinfo(BasicAccount account, String ticket, Integer needPoi) throws UnsupportedEncodingException, WeChatException {
        String access_token = TokenUtil.getAccessToken(account);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ticket", ticket);
        map.put("need_poi", needPoi);

        return post(getshakeinfoUrl + "?access_token=" + access_token, map);
    }

}
