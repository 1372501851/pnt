package com.inesv.model;

import java.util.Date;

public class BonusDetail {
    private Long id;
    private Long userId;
    private Long recId;
    private String recWallet;
    private String bonusKey;
    private Integer coinNo;
    private Double price;
    private Integer state;
    private Integer initialState;
    private String remark;
    private Date date;
    private String sumPrice;
    private String sumNumber;
    private String username;
    private String userphoto;
    private String bonusLevel;

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

    public Long getRecId() {
        return recId;
    }

    public void setRecId(Long recId) {
        this.recId = recId;
    }

    public String getRecWallet() {
        return recWallet;
    }

    public void setRecWallet(String recWallet) {
        this.recWallet = recWallet;
    }

    public String getBonusKey() {
        return bonusKey;
    }

    public void setBonusKey(String bonusKey) {
        this.bonusKey = bonusKey;
    }

    public Integer getCoinNo() {
        return coinNo;
    }

    public void setCoinNo(Integer coinNo) {
        this.coinNo = coinNo;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getInitialState() {
        return initialState;
    }

    public void setInitialState(Integer initialState) {
        this.initialState = initialState;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(String sumPrice) {
        this.sumPrice = sumPrice;
    }

    public String getSumNumber() {
        return sumNumber;
    }

    public void setSumNumber(String sumNumber) {
        this.sumNumber = sumNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserphoto() {
        return userphoto;
    }

    public void setUserphoto(String userphoto) {
        this.userphoto = userphoto;
    }

    public String getBonusLevel() {
        return bonusLevel;
    }

    public void setBonusLevel(String bonusLevel) {
        this.bonusLevel = bonusLevel;
    }
}
