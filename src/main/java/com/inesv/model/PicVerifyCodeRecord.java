package com.inesv.model;

import java.util.Date;

/**
 * 验证码短信记录
 * @author 作者xujianfeng 
 * @date 创建时间：2016年12月22日 上午10:35:21
 */
public class PicVerifyCodeRecord {
	private Integer id;
	private String mobile;//手机号码
	private String verifyCode;//验证码
	private Integer type;//发送类型1:用户注册,2:用户忘记密码,3:用户修改交易密码,4:用户忘记交易密码
	private String ip;//来源ip
	private Integer state;//短信验证状态0未使用1已使用
	private Date sendTime;

	//数据库查询参数
	private Integer validTime;//验证码有效时间（秒）
	//private Integer sendLimitTime;//验证码发送限制时间（秒）
	private String dateFormat;//限制日发送数量传当前日期

	public PicVerifyCodeRecord() {
	}

	public PicVerifyCodeRecord(String mobile, String verifyCode, Integer type, String ip) {
		this.mobile = mobile;
		this.verifyCode = verifyCode;
		this.type = type;
		this.ip = ip;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Integer getValidTime() {
		return validTime;
	}

	public void setValidTime(Integer validTime) {
		this.validTime = validTime;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
}
