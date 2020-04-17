package com.inesv.model;

import java.math.BigDecimal;
import java.util.Date;

public class Poundage {
    private Long id;
    private Integer userNo;    //用户ID
    private String userName;    //用户账号
    private String userCode;    //用户编号
    private Integer optype;    //手续费来源类型
    private Integer type;  //手续费货币类型
    private BigDecimal money;   //手续费
    private BigDecimal sumMoney;  //订单总金额
    private Date date;  //时间
    private String attr1;
    private String attr2;
    private String orderNo;
    private Integer state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Integer getOptype() {
        return optype;
    }

    public void setOptype(Integer optype) {
        this.optype = optype;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(BigDecimal sumMoney) {
        this.sumMoney = sumMoney;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }

    public Poundage() {
    }
    

    public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Poundage(Long id, Integer userNo, String userName, String userCode, Integer optype, Integer type, BigDecimal money, BigDecimal sumMoney, Date date, String attr1, String attr2) {
        this.id = id;
        this.userNo = userNo;
        this.userName = userName;
        this.userCode = userCode;
        this.optype = optype;
        this.type = type;
        this.money = money;
        this.sumMoney = sumMoney;
        this.date = date;
        this.attr1 = attr1;
        this.attr2 = attr2;
    }
}
