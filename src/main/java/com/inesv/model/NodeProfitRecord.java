package com.inesv.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class NodeProfitRecord {

    private Long id;

    private Long userId;

    private Integer type;

    private BigDecimal amount;

    private Long fromId;

    private Date createTime;

    private Integer tag;

}
