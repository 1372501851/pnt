package com.inesv.model;

import java.math.BigDecimal;
import java.util.Date;

public class CycleTrade {
	// id
	private long id;
	// 单个周期的天数
	private int cycledDays;
	// 总的交易额
	private BigDecimal sumPrice;
	// 单笔转账的比例
	private BigDecimal proportion;
	// 总周期数
	private int countCycle;
	// 完成的周期数
	private int completedCycle;
	// 剩余金额
	private BigDecimal surplusPrice;
	// 转入地址
	private String address;
	// 转出用户的id
	private long userId;
	// 上个周期的时间
	private Date beforeDate;
	// 备注
	private String remark;
	// 状态0未完成，1已完成
	private int state;
	// 币种id
	private long coinid;
	// PTN用的公钥
	private String pubKey;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCycledDays() {
		return cycledDays;
	}

	public void setCycledDays(int cycledDays) {
		this.cycledDays = cycledDays;
	}

	public BigDecimal getSumPrice() {
		return sumPrice;
	}

	public void setSumPrice(BigDecimal sumPrice) {
		this.sumPrice = sumPrice;
	}

	public BigDecimal getProportion() {
		return proportion;
	}

	public void setProportion(BigDecimal proportion) {
		this.proportion = proportion;
	}

	public int getCountCycle() {
		return countCycle;
	}

	public void setCountCycle(int countCycle) {
		this.countCycle = countCycle;
	}

	public int getCompletedCycle() {
		return completedCycle;
	}

	public void setCompletedCycle(int completedCycle) {
		this.completedCycle = completedCycle;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getBeforeDate() {
		return beforeDate;
	}

	public void setBeforeDate(Date beforeDate) {
		this.beforeDate = beforeDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getCoinid() {
		return coinid;
	}

	public void setCoinid(long coinid) {
		this.coinid = coinid;
	}

	public BigDecimal getSurplusPrice() {
		return surplusPrice;
	}

	public void setSurplusPrice(BigDecimal surplusPrice) {
		this.surplusPrice = surplusPrice;
	}

	public String getPubKey() {
		return pubKey;
	}

	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}
}
