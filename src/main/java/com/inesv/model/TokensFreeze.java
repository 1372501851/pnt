package com.inesv.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 记录ETH代币交易ETH的冻结情况
 */
public class TokensFreeze {
    private Long id;
    private String entrustNo;
    private String orderNo;
    private Integer coinNo;
    private BigDecimal totalUnbalance;
    private BigDecimal unbalance;
    private Date createTime;
    private Date updateTime;

    public TokensFreeze() {
    }

    public TokensFreeze(String entrustNo, String orderNo, Integer coinNo, BigDecimal unbalance) {
        this.entrustNo = entrustNo;
        this.orderNo = orderNo;
        this.coinNo = coinNo;
        this.unbalance = unbalance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntrustNo() {
        return entrustNo;
    }

    public void setEntrustNo(String entrustNo) {
        this.entrustNo = entrustNo;
    }

    public Integer getCoinNo() {
        return coinNo;
    }

    public void setCoinNo(Integer coinNo) {
        this.coinNo = coinNo;
    }

    public BigDecimal getUnbalance() {
        return unbalance;
    }

    public void setUnbalance(BigDecimal unbalance) {
        this.unbalance = unbalance;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public BigDecimal getTotalUnbalance() {
        return totalUnbalance;
    }

    public void setTotalUnbalance(BigDecimal totalUnbalance) {
        this.totalUnbalance = totalUnbalance;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
