package com.inesv.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Coin {

	private Integer tradeState;

	private Long id;

	private Long coinNo;

	private String coinName;

	private String coinRemark;

	private Integer state;

	private BigDecimal minFee;

	private BigDecimal maxFee;

	private BigDecimal maximum;

	private Date date;

	private BigDecimal pntRatio;

	private BigDecimal quota;
	/**
	 * 对应当前中英文版本的价格，英文版本为美元，中文版本为人民币
	 */
	private BigDecimal coinPriceBySys;
	/**
	 * 如果当前版本为中文，则显示美元，与之相反
	 */
	private BigDecimal coinPrice;

	private Integer changeState;

	private String coinImg;

	private Integer coinBlock;

	private BigDecimal changeFee;
	// 资产列表用来判断用户是否开启该币种的状态
	private Integer walletState;
	//实时金额
	private BigDecimal freePrice;
	//解锁比例
	private BigDecimal unlockRatio;
	//解锁天数
	private Integer unlockDay;
	//解锁状态
	private Integer unlockState;
	//调用的RPC接口类型
	private String apiType;

	public BigDecimal getFreePrice() {
		return freePrice;
	}

	public void setFreePrice(BigDecimal freePrice) {
		this.freePrice = freePrice;
	}

	public BigDecimal getUnlockRatio() {
		return unlockRatio;
	}

	public void setUnlockRatio(BigDecimal unlockRatio) {
		this.unlockRatio = unlockRatio;
	}

	public Integer getUnlockDay() {
		return unlockDay;
	}

	public void setUnlockDay(Integer unlockDay) {
		this.unlockDay = unlockDay;
	}

	public Integer getUnlockState() {
		return unlockState;
	}

	public void setUnlockState(Integer unlockState) {
		this.unlockState = unlockState;
	}

	public Integer getWalletState() {
		return walletState;
	}

	public void setWalletState(Integer walletState) {
		this.walletState = walletState;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getChangeState() {
		return changeState;
	}

	public void setChangeState(Integer changeState) {
		this.changeState = changeState;
	}

	public BigDecimal getCoinPriceBySys() {
		return coinPriceBySys;
	}

	public void setCoinPriceBySys(BigDecimal coinPriceBySys) {
		this.coinPriceBySys = coinPriceBySys;
	}

	public BigDecimal getCoinPrice() {
		return coinPrice;
	}

	public void setCoinPrice(BigDecimal coinPrice) {
		this.coinPrice = coinPrice;
	}

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

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName == null ? null : coinName.trim();
	}

	public String getCoinRemark() {
		return coinRemark;
	}

	public void setCoinRemark(String coinRemark) {
		this.coinRemark = coinRemark;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getPntRatio() {
		return pntRatio;
	}

	public void setPntRatio(BigDecimal pntRatio) {
		this.pntRatio = pntRatio;
	}

	public BigDecimal getQuota() {
		return quota;
	}

	public void setQuota(BigDecimal quota) {
		this.quota = quota;
	}

	public BigDecimal getMinFee() {
		return minFee;
	}

	public void setMinFee(BigDecimal minFee) {
		this.minFee = minFee;
	}

	public BigDecimal getMaxFee() {
		return maxFee;
	}

	public void setMaxFee(BigDecimal maxFee) {
		this.maxFee = maxFee;
	}

	public BigDecimal getMaximum() {
		return maximum;
	}

	public void setMaximum(BigDecimal maximum) {
		this.maximum = maximum;
	}

	public Integer getCoinBlock() {
		return coinBlock;
	}

	public void setCoinBlock(Integer coinBlock) {
		this.coinBlock = coinBlock;
	}

	public String getCoinImg() {
		return coinImg;
	}

	public void setCoinImg(String coinImg) {
		this.coinImg = coinImg;
	}

	public BigDecimal getChangeFee() {
		return changeFee;
	}

	public void setChangeFee(BigDecimal changeFee) {
		this.changeFee = changeFee;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public void setValue(Coin copyCoin) {
		if (copyCoin == null) {
			return;
		}
		this.changeFee = copyCoin.changeFee;
		this.changeState = copyCoin.changeState;
		this.coinBlock = copyCoin.coinBlock;
		this.coinImg = copyCoin.coinImg;
		this.coinName = copyCoin.coinName;
		this.coinRemark = copyCoin.coinRemark;
		this.coinNo = copyCoin.coinNo;
		this.coinPrice = copyCoin.coinPrice;
		this.coinPriceBySys = copyCoin.coinPriceBySys;
		this.date = copyCoin.date;
		this.freePrice = copyCoin.freePrice;
		this.id = copyCoin.id;
		this.maxFee = copyCoin.maxFee;
		this.minFee = copyCoin.minFee;
		this.pntRatio = copyCoin.pntRatio;
		this.quota = copyCoin.quota;
		this.state = copyCoin.state;
		this.unlockDay = copyCoin.unlockDay;
		this.unlockRatio = copyCoin.unlockRatio;
		this.unlockState = copyCoin.unlockState;
		this.walletState = copyCoin.walletState;
		this.apiType = copyCoin.apiType;
	}
}
