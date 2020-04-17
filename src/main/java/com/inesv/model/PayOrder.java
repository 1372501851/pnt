package com.inesv.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayOrder {

    private Long id;

    /**
     * 平台appid
     */
    private String appId;

    /**
     * 0待支付，1支付成功，2支付失败
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


    private String pushUrl;


    private String preOrderNo;
    /**
     * 订单状态: 1成功生成 0撤销
     */
    private Integer orderStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private String tag;
}
