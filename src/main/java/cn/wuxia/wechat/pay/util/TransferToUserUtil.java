package cn.wuxia.wechat.pay.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

import cn.wuxia.wechat.pay.bean.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jdom2.JDOMException;

import cn.wuxia.common.exception.ValidateException;
import cn.wuxia.common.util.*;
import cn.wuxia.common.util.reflection.BeanUtil;
import cn.wuxia.common.web.httpclient.HttpAction;
import cn.wuxia.common.web.httpclient.HttpClientMethod;
import cn.wuxia.wechat.BaseUtil;
import cn.wuxia.wechat.PayAccount;
import cn.wuxia.wechat.WeChatException;

import javax.validation.constraints.NotNull;

public class TransferToUserUtil extends BaseUtil {
    private final static HttpAction transfersUrl = HttpAction.Action("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers",
            HttpClientMethod.POST);

    /**
     *
     * @param payAccount
     * @param amount
     * @param openid
     * @param realname
     * @param remark
     * @return
     * @throws WeChatException
     */
    public static TransferToUserResult cash(@NotNull PayAccount payAccount, String orderNo, double amount, @NotNull String openid, String realname,
            @NotNull String remark) throws WeChatException {
        TransferToUserBean transferToUserBean = new TransferToUserBean();
        if (StringUtil.isBlank(orderNo)) {
            transferToUserBean.setPartner_trade_no(NoGenerateUtil.generateNo(payAccount.getPartner(), 18));
        } else {
            transferToUserBean.setPartner_trade_no(orderNo);
        }
        transferToUserBean.setSpbill_create_ip(SystemUtil.getOSIpAddr());
        transferToUserBean.setNonce_str(PaySignUtil.getNonceStr());
        transferToUserBean.setMchid(payAccount.getPartner());
        transferToUserBean.setMch_appid(payAccount.getAppid());
        transferToUserBean.setOpenid(openid);
        if (StringUtil.isNotBlank(realname)) {
            transferToUserBean.setCheck_name("FORCE_CHECK");
            transferToUserBean.setRe_user_name(realname);
        } else {
            transferToUserBean.setCheck_name("NO_CHECK");
        }
        DecimalFormat df = new DecimalFormat("######.##");
        String financingNum = df.format(amount);
        logger.info("amount:{}", financingNum);
        transferToUserBean.setAmount(NumberUtil.toInt(PayUtil.changeY2F(financingNum)));
        transferToUserBean.setDesc(remark);
        String sign = PaySignUtil.createSign(payAccount, new TreeMap<>(BeanUtil.beanToMap(transferToUserBean)));
        transferToUserBean.setSign(sign);
        String result = "";
        try {
            logger.info(ToStringBuilder.reflectionToString(transferToUserBean));
            ValidatorUtil.validate(transferToUserBean);
            result = PayHttpsRequest.init(payAccount).send(transfersUrl, transferToUserBean);
        } catch (IOException | KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("", e);
            throw new WeChatException("初始化证书出错。", e);
        } catch (ValidateException e1) {
            logger.error("", e1);
            throw new WeChatException("参数信息有误。", e1);
        }
        try {
            Map<String, String> resultMap = XMLUtil.doXMLParse(result);
            if (StringUtil.equalsIgnoreCase("FAIL", MapUtil.getString(resultMap, "return_code"))
                    || StringUtil.equalsIgnoreCase("FAIL", MapUtil.getString(resultMap, "result_code"))) {
                throw new WeChatException(MapUtil.getString(resultMap, "return_msg") + "" + MapUtil.getString(resultMap, "err_code_des"));
            }
            return MapUtil.mapToBean(resultMap, TransferToUserResult.class);
        } catch (JDOMException | IOException e) {
            logger.warn("xml解析有误", e);
            throw new WeChatException("xml解析有误。");
        }

    }
}
