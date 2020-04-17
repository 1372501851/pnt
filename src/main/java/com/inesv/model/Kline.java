package com.inesv.model;

public class Kline {
    private Integer id;

    private Long coinNo;

    private String kline;

    private String market;

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCoinNo() {
        return coinNo;
    }

    public void setCoinNo(Long coinNo) {
        this.coinNo = coinNo;
    }

    public String getKline() {
        return kline;
    }

    public void setKline(String kline) {
        this.kline = kline == null ? null : kline.trim();
    }
}