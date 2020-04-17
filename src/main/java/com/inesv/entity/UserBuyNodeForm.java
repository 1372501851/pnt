package com.inesv.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserBuyNodeForm {

    private String tradePasswd;

    private BigDecimal amount;

}
