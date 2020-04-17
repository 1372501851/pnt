package com.inesv.mapper;

import com.inesv.model.PayPointOrder;
import com.inesv.model.PayPointRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PointMapper {
    void createOrder(@Param("userId") Long userId, @Param("point") String point, @Param("defaultCoin") String defaultCoin, @Param("amount") String amount, @Param("orderNo") String orderNo);

    String getPoint(@Param("userId") Long userId, @Param("orderNo") String orderNo);

    String getOrderAmount(@Param("userId") Long userId, @Param("orderNo") String orderNo);

    void updatePayState(@Param("userId") Long userId, @Param("orderNo") String orderNo);

    PayPointOrder getOrderInfo(@Param("userId") Long userId, @Param("orderNo") String orderNo);

    void insertPayOrder(@Param("orderNo") String orderNo, @Param("userId") Long userId, @Param("point") String point, @Param("username") String username,@Param("orderInfo") String orderInfo,@Param("payType") Integer payType);

    List<PayPointRecord> payRecord(@Param("userId") Long userId);

    List<PayPointRecord> payRecordByType(@Param("userId") Long userId, @Param("type") Integer type);

    List<PayPointRecord> PayRecordBySDK(@Param("userId") Long userId);

    void insertPointPay(@Param("userId") Long userId, @Param("orderNo") String orderNo, @Param("tradeTarget") String tradeTarget, @Param("point") String point, @Param("payCoin") String payCoin, @Param("amount") String amount);

    void updatePoint(@Param("userId") Long userId, @Param("point") BigDecimal point);
}
