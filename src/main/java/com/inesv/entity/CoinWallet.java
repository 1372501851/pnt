package com.inesv.entity;


import java.math.BigDecimal;


public class CoinWallet {
	private BigDecimal amount;//成交量
	
	private BigDecimal level;//涨跌幅
	
	private BigDecimal p_high;//最高价
	
	private BigDecimal p_last;
	
	private BigDecimal plow;
	
	private BigDecimal p_new;//最新价

	private BigDecimal p_open;//开盘价
	
	private BigDecimal total;
		
	private String symbol;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getLevel() {
		return level;
	}

	public void setLevel(BigDecimal level) {
		this.level = level;
	}

	public BigDecimal getP_high() {
		return p_high;
	}

	public void setP_high(BigDecimal p_high) {
		this.p_high = p_high;
	}

	public BigDecimal getP_last() {
		return p_last;
	}

	public void setP_last(BigDecimal p_last) {
		this.p_last = p_last;
	}

	public BigDecimal getPlow() {
		return plow;
	}

	public void setPlow(BigDecimal plow) {
		this.plow = plow;
	}

	public BigDecimal getP_new() {
		return p_new;
	}

	public void setP_new(BigDecimal p_new) {
		this.p_new = p_new;
	}

	public BigDecimal getP_open() {
		return p_open;
	}

	public void setP_open(BigDecimal p_open) {
		this.p_open = p_open;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return "CoinWallet [amount=" + amount + ", level=" + level
				+ ", p_high=" + p_high + ", p_last=" + p_last + ", plow="
				+ plow + ", p_new=" + p_new + ", p_open=" + p_open + ", total="
				+ total + ", symbol=" + symbol + "]";
	}

	
	
	
	
}
