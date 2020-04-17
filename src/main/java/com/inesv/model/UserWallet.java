/**
 * 
 */
package com.inesv.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Admin 用户钱包
 */
@Data
public class UserWallet {

	private Integer walletState;

	private Long id;

	private Long userId;

	private String address;

	private Date date;

	private Integer type;

	private Integer state;

	private BigDecimal balance;

	private BigDecimal unbalance;
	// 总资产
	private BigDecimal totalBalance;

	// 行情
	private BigDecimal moneyRate;
	// 币名
	private String coinName;
	// 币所属类
	private Integer coinType;
	private String flag;
	private String coinImg;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public void setMoneyRate(BigDecimal moneyRate) {
		this.moneyRate = moneyRate;
	}

	public BigDecimal getMoneyRate() {
		return moneyRate;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public BigDecimal getUnbalance() {
		return unbalance;
	}

	public Integer getCoinType() {
		return coinType;
	}

	public void setCoinType(Integer coinType) {
		this.coinType = coinType;
	}

	public String getCoinImg() {
		return coinImg;
	}

	public void setCoinImg(String coinImg) {
		this.coinImg = coinImg;
	}

	public void setUnbalance(BigDecimal unbalance) {
		this.unbalance = unbalance;
	}

	public BigDecimal getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(BigDecimal totalBalance) {
		this.totalBalance = totalBalance;
	}

	@Override
	public String toString() {
		return "UserWallet{" + "id=" + id + ", userId=" + userId
				+ ", address='" + address + '\'' + ", date=" + date + ", type="
				+ type + ", state=" + state + ", balance=" + balance
				+ ", unbalance=" + unbalance + ", moneyRate=" + moneyRate
				+ ", coinName='" + coinName + '\'' + '}';
	}

	public void setValue(UserWallet copyUserWallet) {
		if (copyUserWallet == null) {
			return;
		}
		this.id = copyUserWallet.id;
		this.userId = copyUserWallet.userId;
		this.address = copyUserWallet.address;
		this.date = copyUserWallet.date;
		this.type = copyUserWallet.type;
		this.state = copyUserWallet.state;
		this.balance = copyUserWallet.balance;
		this.unbalance = copyUserWallet.unbalance;
		this.moneyRate = copyUserWallet.moneyRate;
		this.coinName = copyUserWallet.coinName;
		this.coinImg = copyUserWallet.coinImg;
		this.coinType = copyUserWallet.coinType;
		this.flag = copyUserWallet.flag;
	}
}
