package com.inesv.model;

import lombok.Data;

@Data
public class PayPointOrder {

    private String point;

    private String payCoin;

    private String payAmount;

    private Integer payState;
}
