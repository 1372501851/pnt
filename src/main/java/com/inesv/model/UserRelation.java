package com.inesv.model;

import java.util.Date;

public class UserRelation {
    private Long id;
    private Long userId;
    private Long recId;
    private Date date;
    private Integer treeTrade;

    private String path;

    public Integer getTreeTrade() {
        return treeTrade;
    }

    public void setTreeTrade(Integer treeTrade) {
        this.treeTrade = treeTrade;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public Long getRecId() {
        return recId;
    }

    public void setRecId(Long recId) {
        this.recId = recId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
