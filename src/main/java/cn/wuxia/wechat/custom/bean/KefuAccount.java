package cn.wuxia.wechat.custom.bean;

import java.io.Serializable;

/**
 * "kf_account" : "test1@test", "kf_headimgurl" :
 * "http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjfUS8Ym0GSaLic0FD3vN0V8PILcibEGb2fPfEOmw/0",
 * "kf_id" : "1001", "kf_nick" : "ntest1", "kf_wx" : "kfwx1"
 * 
 * @author songlin
 *
 */
public class KefuAccount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 完整客服帐号，格式为：帐号前缀@公众号微信号
	 */
	String kf_account;
	/**
	 * 客服昵称
	 */
	String kf_nick;
	/**
	 * 客服编号
	 */
	Integer kf_id;
	/**
	 * 客服头像
	 */
	String kf_headimgurl;
	/***
	 * 如果客服帐号已绑定了客服人员微信号， 则此处显示微信号
	 */
	String kf_wx;
	/**
	 * 如果客服帐号尚未绑定微信号，但是已经发起了一个绑定邀请， 则此处显示绑定邀请的微信号
	 */
	String invite_wx;
	/**
	 * 如果客服帐号尚未绑定微信号，但是已经发起过一个绑定邀请， 邀请的过期时间，为unix 时间戳
	 */
	Long invite_expire_time;
	/**
	 * 邀请的状态，有等待确认“waiting”，被拒绝“rejected”， 过期“expired”
	 */
	String invite_status;

	public String getKf_account() {
		return kf_account;
	}

	public void setKf_account(String kf_account) {
		this.kf_account = kf_account;
	}

	public String getKf_headimgurl() {
		return kf_headimgurl;
	}

	public void setKf_headimgurl(String kf_headimgurl) {
		this.kf_headimgurl = kf_headimgurl;
	}

	public Integer getKf_id() {
		return kf_id;
	}

	public void setKf_id(Integer kf_id) {
		this.kf_id = kf_id;
	}

	public String getKf_nick() {
		return kf_nick;
	}

	public void setKf_nick(String kf_nick) {
		this.kf_nick = kf_nick;
	}

	public String getKf_wx() {
		return kf_wx;
	}

	public void setKf_wx(String kf_wx) {
		this.kf_wx = kf_wx;
	}

	public String getInvite_wx() {
		return invite_wx;
	}

	public void setInvite_wx(String invite_wx) {
		this.invite_wx = invite_wx;
	}

	public Long getInvite_expire_time() {
		return invite_expire_time;
	}

	public void setInvite_expire_time(Long invite_expire_time) {
		this.invite_expire_time = invite_expire_time;
	}

	public String getInvite_status() {
		return invite_status;
	}

	public void setInvite_status(String invite_status) {
		this.invite_status = invite_status;
	}

}
