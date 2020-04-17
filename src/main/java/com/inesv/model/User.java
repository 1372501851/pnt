package com.inesv.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class User {
	private Long id;
	private String username;
	private String nickname;
	private String photo;
	private String password;
	private String tradePassword;
	private Integer state;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd hh:mm:ss")
	private Date date;
	private String token;
	private Date timeout;
	private String ptnaddress;
	private String weChat;
	private String apay;
	private String invitationCode;
	private String phone;
	private String areaCode;
	private String imToken;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTradePassword() {
		return tradePassword;
	}

	public void setTradePassword(String tradePassword) {
		this.tradePassword = tradePassword;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getTimeout() {
		return timeout;
	}

	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}

	public String getPtnaddress() {
		return ptnaddress;
	}

	public void setPtnaddress(String ptnaddress) {
		this.ptnaddress = ptnaddress;
	}

	public String getApay() {
		return apay;
	}

	public void setApay(String apay) {
		this.apay = apay;
	}

	public String getWeChat() {
		return weChat;
	}

	public void setWeChat(String weChat) {
		this.weChat = weChat;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	public void setValue(User copyUser) {
		if (copyUser == null) {
			return;
		}
		this.date = copyUser.date;
		this.id = copyUser.id;
		this.nickname = copyUser.nickname;
		this.password = copyUser.password;
		this.photo = copyUser.photo;
		this.ptnaddress = copyUser.ptnaddress;
		this.state = copyUser.state;
		this.timeout = copyUser.timeout;
		this.token = copyUser.token;
		this.tradePassword = copyUser.tradePassword;
		this.username = copyUser.username;
		this.weChat = copyUser.weChat;
		this.apay = copyUser.apay;
		this.invitationCode = copyUser.invitationCode;
	}
}
