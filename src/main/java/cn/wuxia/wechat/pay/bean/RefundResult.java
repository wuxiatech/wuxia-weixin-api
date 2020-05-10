package cn.wuxia.wechat.pay.bean;

import cn.wuxia.common.util.StringUtil;
import com.github.wxpay.sdk.WXPayConstants;
import lombok.Data;

/**
 *
 */
@Data
public class RefundResult {
    String transaction_id;
    String out_refund_no;
    String return_msg;
    String mch_id;
    String refund_id;
    String cash_fee;
    String out_trade_no;
    String coupon_refund_fee;
    String refund_channel;
    String appid;
    String refund_fee;
    String total_fee;
    String result_code;
    String coupon_refund_count;
    String cash_refund_fee;
    String return_code;

    public boolean isSuccess(){
        return StringUtil.equals(WXPayConstants.SUCCESS, return_code) && StringUtil.equals(WXPayConstants.SUCCESS, result_code);
    }

    String err_code;
    String err_code_des;
}
