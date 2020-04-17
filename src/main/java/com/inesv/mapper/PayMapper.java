package com.inesv.mapper;

import com.inesv.model.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface PayMapper {


    void register(PayRegister payRegister);

    void update(@Param("appId") String appId, @Param("id") Long id,@Param("dataKey") String dataKey,@Param("signature") String signature);

    PayRegister isExistUsername(PayRegister payRegister);

    String getDataKey(@Param("signature") String signature);

    void createOrder(PayOrder payOrder);

    PayOrderVo getOrder(PayOrder payOrder);

    PayOrderNotify queryOrder(PayOrder payOrder);

    String getTag(@Param("appId") String appId,@Param("orderNo") String orderNo);

    List<String> getCoinType(@Param("userId") Long userId);

    Integer isRepeatOrder(@Param("appId") String appId, @Param("orderNo") String orderNo);

    void insertUserId(@Param("appId") String appId, @Param("orderNo") String orderNo, @Param("userId") Long userId);

    Integer getPayStatus(@Param("appId") String appId, @Param("orderNo") String orderNo, @Param("userId") Long userId);

    void freeze(@Param("userId") Long userId, @Param("coinType") String coinType,@Param("coinQuantity") String coinQuantity);

    void updateOrderStatus(@Param("appId") String appId, @Param("orderNo") String orderNo, @Param("userId") Long userId,@Param("tradeOrderId") String tradeOrderId,@Param("coinType") String coinType);

    BigDecimal getMoney(@Param("userId") Long userId, @Param("coinType") String coinType);

    void updateEncode(@Param("id") Long id, @Param("data") String data, @Param("encode") String encode,@Param("moneyMark") String moneyMark);

    Map<String,String> getData(@Param("encode") String encode);

    String getSignature( @Param("appId") String appId);

    String getPreOrderNo(@Param("appId") String appId, @Param("orderNo") String orderNo);

    void updateOrderQuantity(@Param("appId") String appId, @Param("orderNo") String orderNo, @Param("userId") Long userId,@Param("tradePrice") String tradePrice,@Param("quantity") String quantity,@Param("coinType") String coinType,@Param("mainMoney") String mainMoney);

    void getRegisterEncode(@Param("encode") String encode, @Param("encodeStr") String encodeStr);

    List<Map<String,Object>> getPayOrder();

    PaySellConfig getSellInfo(@Param("appId") String appId, @Param("coinType") String coinType);

    String getAddress(@Param("appId") String appId, @Param("type") int type);

    void updateActualExchange(@Param("totalExchange") String totalExchange, @Param("tradeOrderId") String tradeOrderId);

    void freeFreeze(@Param("userId") Long userId, @Param("coinNo") Long coinNo,@Param("coinQuantity") String coinQuantity);

    void grantMoney(@Param("appId") String appId, @Param("quantity") String quantity, @Param("coinNo") Long coinNo);

    void updatePayStatus(@Param("payStatus") int payStatus, @Param("preOrderNo") String preOrderNo,@Param("userId") String userId);

    String getPlatformAddress(@Param("appId") String appId);

    void insertRecord(@Param("appId") String appId, @Param("address") String address, @Param("userId") Long userId,
                      @Param("coinType") String coinType, @Param("hash") String hash ,@Param("quantity") String quantity) ;

    BigDecimal isEnoughPoint(@Param("userId") Long userId);

    String getMainMoney(@Param("preOrderNo") String preOrderNo, @Param("orderNo") String orderNo, @Param("appId") String appId, @Param("userId") Long userId);

    void deduct(@Param("userId") Long userId, @Param("point") BigDecimal point);

    String getMerchantAddress(@Param("appId") String appId);

    void addPoint(@Param("userId") Long userId, @Param("point") BigDecimal point);

    void addPointRecord(@Param("userId") Long userId, @Param("merchantId") Long merchantId, @Param("point") BigDecimal point);

    void updateOrderByPoint(@Param("preOrderNo") String preOrderNo, @Param("orderNo") String orderNo, @Param("appId") String appId, @Param("userId") Long userId);

    Integer isExistPointAddress(@Param("address") String address);

    void insertPointInfo(@Param("address") String address, @Param("userId") Long userId);

    Integer getPayState(@Param("appId") String appId, @Param("orderNo") String orderNo);

    void updateOrderState(@Param("appId") String appId, @Param("orderNo") String orderNo, @Param("userId") Long userId,@Param("coinType") String coinType);

    String getPayCoinType(@Param("userId") Long userId, @Param("orderNo") String orderNo);

    void updateNotice(@Param("appId") String appId, @Param("orderNo") String orderNo, @Param("userId") Long userId);

    Map<String,Object> getNameAndUserId(@Param("appId") String appId);

    PayRegister isExistAddress(PayRegister payRegister);

    void updateOrderTag(@Param("appId") String appId, @Param("preOrderNo") String preOrderNo);

    List<Long> getWalletUserId();
}
