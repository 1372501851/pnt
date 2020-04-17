package com.inesv.model;

import java.math.BigInteger;
import java.sql.Date;

public class MemoAddress {
	private BigInteger id;
	private String memo;
	private String address;
	private Date adddate;

	public String getAddress() {
		return address;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Date getDate() {
		return adddate;
	}

	public void setDate(Date date) {
		this.adddate = date;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
