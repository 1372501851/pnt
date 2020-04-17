package com.inesv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayOrderNotify {


    /**
     * 0待支付，1支付中，2支付成功，3支付失败
     */
    private Integer payStatus;


    /**
     * 商品名称
     */
    private String orderInfo;

    /**
     * 价格
     */
    private String price;


    /**
     * 数量
     */
    private String quantity;

    /**
     * 自定义生成订单号
     */
    private String orderNo;



    private String preOrderNo;
    /**
     * 订单状态: 1成功生成 0撤销
     */
    private Integer orderStatus;


    /**
     * 创建时间
     */
    private String createTime;


    private String coinType;

    @JsonIgnore
    private String tradePrice;
}
