package cn.wuxia.wechat.custom.bean;

/**
 * 客服会话
 * 
 * @author songlin
 *
 */
public class KefuSession {
	String kf_account;

	Long createtime;

	String openid;

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

	public String getKf_account() {
		return kf_account;
	}

	public void setKf_account(String kf_account) {
		this.kf_account = kf_account;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

}
