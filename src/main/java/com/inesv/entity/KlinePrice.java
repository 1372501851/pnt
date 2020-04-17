package com.inesv.entity;

import java.math.BigDecimal;

public class KlinePrice {

    private BigDecimal open;

    private BigDecimal off;

    private BigDecimal low;

    private BigDecimal high;

    private BigDecimal vol;


    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getOff() {
        return off;
    }

    public void setOff(BigDecimal off) {
        this.off = off;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(BigDecimal vol) {
        this.vol = vol;
    }
}
