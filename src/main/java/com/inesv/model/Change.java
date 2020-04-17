package com.inesv.model;

import java.math.BigDecimal;
import java.util.Date;

public class Change {
    private Long id;

    private Long userId;

    private String coinName;

    private BigDecimal coinNum;

    private BigDecimal pnt;

    private String orderNo;

    private String address;

    private BigDecimal ratio;

    private String remark;

    private Date date;

    private Long inCoin;

    private Long outCoin;

    private String dateString;

    private Integer changeFlag;

    private String outCoinName;

    private String inCoinName;

    private BigDecimal frozenAssets;

    private int flag;//0释放中，1释放完毕

    private BigDecimal percentage;//百分比

    private BigDecimal available;

    private Date lastChangeTime;


    public Date getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(Date lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public BigDecimal getFrozenAssets() {
        return frozenAssets;
    }

    public void setFrozenAssets(BigDecimal frozenAssets) {
        this.frozenAssets = frozenAssets;
    }

    public String getOutCoinName() {
        return outCoinName;
    }

    public void setOutCoinName(String outCoinName) {
        this.outCoinName = outCoinName;
    }

    public String getInCoinName() {
        return inCoinName;
    }

    public void setInCoinName(String inCoinName) {
        this.inCoinName = inCoinName;
    }

    public Integer getChangeFlag() {
        return changeFlag;
    }

    public void setChangeFlag(Integer changeFlag) {
        this.changeFlag = changeFlag;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public Long getInCoin() {
        return inCoin;
    }

    public void setInCoin(Long inCoin) {
        this.inCoin = inCoin;
    }

    public Long getOutCoin() {
        return outCoin;
    }

    public void setOutCoin(Long outCoin) {
        this.outCoin = outCoin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName == null ? null : coinName.trim();
    }

    public BigDecimal getCoinNum() {
        return coinNum;
    }

    public void setCoinNum(BigDecimal coinNum) {
        this.coinNum = coinNum;
    }

    public BigDecimal getPnt() {
        return pnt;
    }

    public void setPnt(BigDecimal pnt) {
        this.pnt = pnt;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}