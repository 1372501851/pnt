package com.inesv.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Result {
 
	private String status;
 
	private String scur;
	
	private String tcur;
	
	private String ratenm;
	
	private BigDecimal rate;
	
	private Date update;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getScur() {
		return scur;
	}

	public void setScur(String scur) {
		this.scur = scur;
	}

	public String getTcur() {
		return tcur;
	}

	public void setTcur(String tcur) {
		this.tcur = tcur;
	}

	public String getRatenm() {
		return ratenm;
	}

	public void setRatenm(String ratenm) {
		this.ratenm = ratenm;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Date getUpdate() {
		return update;
	}

	public void setUpdate(Date update) {
		this.update = update;
	}
	
	
}
