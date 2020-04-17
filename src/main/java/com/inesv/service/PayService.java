package com.inesv.service;

import com.inesv.model.PayOrderVo;
import com.inesv.model.PayRegister;
import com.inesv.model.PaySellConfig;
import com.inesv.model.User;
import com.inesv.util.BaseResponse;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface PayService {
    BaseResponse  register(Map<String,String> payRegister);

    String getDataKey(String appId);

    PayOrderVo createOrder(String orderNo, String name, String price,String appId,String createTime,String pushUrl,String preOrderNo,String tag,String data,String encode,String moneyMark) ;

    BaseResponse queryOrder(String orderNo, String appId, String preOrderNo);

    String getTag(String appId, String orderNo);

    List<String> getCoinType(Long userId);

    Integer isRepeatOrder(String appId, String orderNo);

    void insertUserId(String appId, String orderNo, Long userId);

    Integer getPayStatus(String appId, String orderNo, Long userId);

    void freeze(Long userId, String coinType,String coinQuantity);

    void updateOrderStatus(String appId, String orderNo, Long userId,String attachment,String coinType);

    BigDecimal getMoney(Long userId, String coinType);

    Map<String,String> getData(String data);

    String getSignature(String appId1);

    String getPreOrderNo(String appId, String orderNo);

    void updateOrderQuantity(String appId, String orderNo, Long userId, String tradePrice,String quantity,String payTypeId,String mainMoney);

    String getRegisterEncode();

    PaySellConfig getSellInfo(String appId, String coinType);

    String getAddress(String appId, int i);

    BigDecimal isEnoughPoint(Long userId);

    BaseResponse pointPay(String preOrderNo, String orderNo, String appId, Long userId,String pushUrl,String orderInfo);

    BaseResponse refresh(String price, String appId, String payTypeId, String rate,Long userId,String orderNo);

    Map<String,Object> orderInfo(String price, String rate, String appId,Long userId,String orderNo);

    void coinPay(String coinType, String preOrderNo, String orderNo, String appId, Long userId, String pushUrl);

    String getUsdtExchangeCny();
}
