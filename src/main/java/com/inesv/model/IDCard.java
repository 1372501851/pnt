package com.inesv.model;

/**
 * @author: xujianfeng
 * @create: 2018-06-28 20:55
 **/
public class IDCard {
    private transient Integer id;
    private transient Integer userId;
    private String name;
    private transient String gender;
    private String idCardNumber;
    private transient String side;
    private transient String frontUrl;
    private transient String backUrl;
    private transient String frontFilePath;
    private transient String backFilePath;
    private transient String handUrl;
    private Integer state;

    private Integer limitNum;
    private String orderByCondition;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getFrontUrl() {
        return frontUrl;
    }

    public void setFrontUrl(String frontUrl) {
        this.frontUrl = frontUrl;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }

    public String getFrontFilePath() {
        return frontFilePath;
    }

    public void setFrontFilePath(String frontFilePath) {
        this.frontFilePath = frontFilePath;
    }

    public String getBackFilePath() {
        return backFilePath;
    }

    public void setBackFilePath(String backFilePath) {
        this.backFilePath = backFilePath;
    }

    public String getHandUrl() {
        return handUrl;
    }

    public void setHandUrl(String handUrl) {
        this.handUrl = handUrl;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public String getOrderByCondition() {
        return orderByCondition;
    }

    public void setOrderByCondition(String orderByCondition) {
        this.orderByCondition = orderByCondition;
    }
}
