package com.inesv.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Kline {


    private List<String> dates;
    private List<BigDecimal[]> data;
    private List<BigDecimal> volumes;

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<BigDecimal[]> getData() {
        return data;
    }

    public void setData(List<BigDecimal[]> data) {
        this.data = data;
    }

    public List<BigDecimal> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<BigDecimal> volumes) {
        this.volumes = volumes;
    }
}
