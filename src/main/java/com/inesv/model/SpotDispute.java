package com.inesv.model;


import lombok.Data;

import java.util.Date;

@Data
public class SpotDispute {
    private Long id;
    private Integer buyUserNo;
    private Integer sellUserNo;
    private Long dealNo;
    private Integer userNo;
    private String disputePhone;
    private String remark;
    private Integer state;
    private Integer type;
    private String reason;
    private Date date;
    private String buyMatchUserName;
    private String sellMatchUserName;
    private String orderNo;
    private String img1;
    private String img2;
    private String img3;
    private String dateStr;
    private Integer originalState;

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBuyUserNo() {
        return buyUserNo;
    }

    public void setBuyUserNo(Integer buyUserNo) {
        this.buyUserNo = buyUserNo;
    }

    public Integer getSellUserNo() {
        return sellUserNo;
    }

    public void setSellUserNo(Integer sellUserNo) {
        this.sellUserNo = sellUserNo;
    }

    public Long getDealNo() {
        return dealNo;
    }

    public void setDealNo(Long dealNo) {
        this.dealNo = dealNo;
    }

    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }

    public String getDisputePhone() {
        return disputePhone;
    }

    public void setDisputePhone(String disputePhone) {
        this.disputePhone = disputePhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getBuyMatchUserName() {
        return buyMatchUserName;
    }

    public void setBuyMatchUserName(String buyMatchUserName) {
        this.buyMatchUserName = buyMatchUserName;
    }

    public String getSellMatchUserName() {
        return sellMatchUserName;
    }

    public void setSellMatchUserName(String sellMatchUserName) {
        this.sellMatchUserName = sellMatchUserName;
    }
}
