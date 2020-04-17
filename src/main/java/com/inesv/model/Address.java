package com.inesv.model;

public class Address {
	private Long id;
	private Long coinNo;
	private String address;
	private String port;
	private String name;
	private String password;
	private String lockPassword;
	private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCoinNo() {
		return coinNo;
	}

	public void setCoinNo(Long coinNo) {
		this.coinNo = coinNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLockPassword() {
		return lockPassword;
	}

	public void setLockPassword(String lockPassword) {
		this.lockPassword = lockPassword;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setValue(Address copyAddress) {
		if (copyAddress == null) {
			return;
		}
		this.id = copyAddress.id;
		this.address = copyAddress.address;
		this.coinNo = copyAddress.coinNo;
		this.port = copyAddress.port;
		this.name = copyAddress.name;
		this.password = copyAddress.password;
		this.lockPassword = copyAddress.lockPassword;
		this.status = copyAddress.status;
	}
}
