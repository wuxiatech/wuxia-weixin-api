package cn.wuxia.wechat.pay.bean;


import cn.wuxia.common.util.MapUtil;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.wechat.WeChatException;

public abstract class ResultCodeBean {


    /*
    <return_code><![CDATA[SUCCESS]]></return_code>
    <return_msg><![CDATA[OK]]></return_msg>
    <result_code><![CDATA[SUCCESS]]></result_code>
    <err_code><![CDATA[SUCCESS]]></err_code>
    <err_code_des><![CDATA[OK]]></err_code_des>
    */


    String return_code;
    String return_msg;
    String result_code;
    String err_code;
    String err_code_des;


    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getResult_code() {
        return result_code;
    }

    public void setResult_code(String result_code) {
        this.result_code = result_code;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public String getErr_code_des() {
        return err_code_des;
    }

    public void setErr_code_des(String err_code_des) {
        this.err_code_des = err_code_des;
    }


    public String errorMsg() {
        return this.getReturn_msg() + ":" + this.getErr_code_des();
    }

    /**
     * @return
     */
    public boolean isok() throws WeChatException {
        if (StringUtil.equalsIgnoreCase("FAIL", this.getReturn_code())
                || StringUtil.equalsIgnoreCase("FAIL", this.getResult_code())) {
            return false;
        } else {
            return true;
        }
    }
}
