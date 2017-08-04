/*
* Created on :7 Apr, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.sign.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import cn.wuxia.wechat.BaseUtil;

/**
 * 微信接入签名
 * 
 * @author guwen
 *
 */
public class SignUtil extends BaseUtil {

    /**
     * 微信接入验签
     */
    public static boolean verify(String token, String timestamp, String nonce, String signature) {
        String[] arr = new String[] { token, timestamp, nonce };
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }

        // sha1加密
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            logger.error("加密出错 ： ", e);
        }
        sha1.update(content.toString().getBytes());
        byte[] domain = sha1.digest();
        StringBuffer sha1StrBuff = new StringBuffer();

        for (int i = 0; i < domain.length; i++) {
            if (Integer.toHexString(0xFF & domain[i]).length() == 1) {
                sha1StrBuff.append("0").append(Integer.toHexString(0xFF & domain[i]));
            } else {
                sha1StrBuff.append(Integer.toHexString(0xFF & domain[i]));
            }
        }
        return sha1StrBuff.toString().equals(signature);
    }

    /**
     * 微信接入生成签名
     */
    public static String sign(String token, String timestamp, String nonce) {
        String[] arr = new String[] { token, timestamp, nonce };
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }

        // sha1加密
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            logger.error("加密出错 ： ", e);
        }
        sha1.update(content.toString().getBytes());
        byte[] domain = sha1.digest();
        StringBuffer sha1StrBuff = new StringBuffer();

        for (int i = 0; i < domain.length; i++) {
            if (Integer.toHexString(0xFF & domain[i]).length() == 1) {
                sha1StrBuff.append("0").append(Integer.toHexString(0xFF & domain[i]));
            } else {
                sha1StrBuff.append(Integer.toHexString(0xFF & domain[i]));
            }
        }
        return sha1StrBuff.toString();
    }

    // 随机字符，认证
    public static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    // 时间，认证
    public static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
