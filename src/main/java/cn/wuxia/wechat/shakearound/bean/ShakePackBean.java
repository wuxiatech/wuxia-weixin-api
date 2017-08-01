/*
* Created on :12 Oct, 2015
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 www.ibmall.cn All right reserved.
*/
package cn.wuxia.wechat.shakearound.bean;

public class ShakePackBean extends PackBean {
    private String hb_type;

    //用于发红包时微信支付识别摇周边红包，所有开发者统一填写摇周边平台的商户号：1000052601 
    private String auth_mchid;

    private String auth_appid;

    //用于管控接口风险。具体值如下：NORMAL—正常情况；IGN_FREQ_LMT—忽略防刷限制，强制发放；IGN_DAY_LMT—忽略单用户日限额限制，强制发放；IGN_FREQ_DAY_LMT—忽略防刷和单用户日限额限制，强制发放；如无特殊要求，请设为NORMAL。若忽略某项风险控制，可能造成资金损失，请谨慎使用。
    private String risk_cntl = "NORMAL";

    public String getHb_type() {
        return hb_type;
    }

    public void setHb_type(String hb_type) {
        this.hb_type = hb_type;
    }

    public String getAuth_mchid() {
        return auth_mchid;
    }

    public void setAuth_mchid(String auth_mchid) {
        this.auth_mchid = auth_mchid;
    }

    public String getAuth_appid() {
        return auth_appid;
    }

    public void setAuth_appid(String auth_appid) {
        this.auth_appid = auth_appid;
    }

    public String getRisk_cntl() {
        return risk_cntl;
    }

    public void setRisk_cntl(String risk_cntl) {
        this.risk_cntl = risk_cntl;
    }

}
