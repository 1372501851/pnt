package com.inesv.model;

import java.util.Date;

public class Bonus {
    private Long id;
    private String bonusKey;
    private Double bonusOne;
    private Double bonusTwo;
    private Double bonusThree;
    private Integer state;
    private String remark;
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBonusKey() {
        return bonusKey;
    }

    public void setBonusKey(String bonusKey) {
        this.bonusKey = bonusKey;
    }

    public Double getBonusOne() {
        return bonusOne;
    }

    public void setBonusOne(Double bonusOne) {
        this.bonusOne = bonusOne;
    }

    public Double getBonusTwo() {
        return bonusTwo;
    }

    public void setBonusTwo(Double bonusTwo) {
        this.bonusTwo = bonusTwo;
    }

    public Double getBonusThree() {
        return bonusThree;
    }

    public void setBonusThree(Double bonusThree) {
        this.bonusThree = bonusThree;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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
}
