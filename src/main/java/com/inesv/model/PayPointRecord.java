package com.inesv.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayPointRecord {

    //private Integer userId;

    private String point;

    private String coinType;

    private String coinQuantity;

    private Integer type;

    private String orderNo;

    private String orderInfo;

    private String tradeTarget;

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "GMT+8"
    )
    private Date createTime;
}
