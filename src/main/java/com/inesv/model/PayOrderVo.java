package com.inesv.model;


import lombok.Data;



@Data
public class PayOrderVo {


    /**
     * 商品名称
     */
    private String orderInfo;

    /**
     * 价格
     */
    private String price;

    /**
     * 自定义生成订单号
     */
    private String orderNo;

    /**
     * 创建时间
     */
    private String createTime;

    private String preOrderNo;


}
