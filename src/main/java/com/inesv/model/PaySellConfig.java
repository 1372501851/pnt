package com.inesv.model;

import lombok.Data;

@Data
public class PaySellConfig {

    private Integer uid;
    private Integer currencyId;
    private Integer baseCurrencyId;
    private Integer type;
    private Integer buyOrSell;
    private Double price;
    private Double num;
    private Integer source;

}
