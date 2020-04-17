package com.inesv.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Bitstamp {
	/**
	 * 最高价
	 * */
	private BigDecimal high;
	/**
	 * 最新价
	 * */
	private BigDecimal last;
	/**
	 * 时间戳
	 * */
	private Timestamp timestamp;
	/**
	 * 最近24h高成交价
	 * */
	private BigDecimal bid;
	/**
	 * 最后24小时成交量加权平均价格。
	 * */
	private BigDecimal vwap;
	/**
	 * 最近24小时的成交量
	 * */
	private BigDecimal volume;
	/**
	 * 最近24小时的最低价
	 * */
	private BigDecimal low;
	/**
	 * 最低成交单定价格
	 * */
	private BigDecimal ask;
	/**
	 * 顾名思义就是开盘价了
	 * */
	private BigDecimal open;

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLast() {
		return last;
	}

	public void setLast(BigDecimal last) {
		this.last = last;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public BigDecimal getVwap() {
		return vwap;
	}

	public void setVwap(BigDecimal vwap) {
		this.vwap = vwap;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getAsk() {
		return ask;
	}

	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	@Override
	public String toString() {
		return "Bitstamp [high=" + high + ", last=" + last + ", timestamp="
				+ timestamp + ", bid=" + bid + ", vwap=" + vwap + ", volume="
				+ volume + ", low=" + low + ", ask=" + ask + ", open=" + open
				+ "]";
	}

}
