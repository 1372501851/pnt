package com.inesv.task;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.inesv.entity.CoinParams;
import com.inesv.mapper.AddressMapper;
import com.inesv.mapper.CoinMapper;
import com.inesv.mapper.PayMapper;
import com.inesv.mapper.UserWalletMapper;
import com.inesv.model.Address;
import com.inesv.model.Coin;
import com.inesv.model.Params;
import com.inesv.model.UserWallet;
import com.inesv.service.TradeInfoService;
import com.inesv.util.CoinAPI.PNTCoinApi;
import com.inesv.util.HttpUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PayTask {

    @Autowired
    private PayMapper payMapper;

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private TradeInfoService tradeInfoService;



    //先查询订单状态是否完成，完成，用户支付给商户
//    @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
//    @Scheduled(fixedDelay = 1000*30)
    public void pay() {
        List<Map<String,Object>> listM=payMapper.getPayOrder();
        if (CollectionUtils.isNotEmpty(listM)){
            for (Map<String,Object> map : listM){
                String appId = (String) map.get("appId");
                String userId = map.get("userId").toString();
                String quantity = (String) map.get("quantity");
                String coinType = (String) map.get("coinType");
                String tradePrice = (String) map.get("tradePrice");
                String preOrderNo = (String) map.get("preOrderNo");
                Object tradeOrderId =  map.get("tradeOrderId");
                //是否委托
                int i=0;
                if (tradeOrderId==null){
                    //不委托的
                    i=1;
                }else {
                    //2代表查询接口
                    String address = payMapper.getAddress(appId, 2);
                    Map<String,Object> map1=new HashMap<>();
                    map1.put("orderId",tradeOrderId);
                    try {
                        String result = HttpUtil.sendPost_common(address, map1);
                        log.info("交易所订单查询接口orderId={},result={}",tradeOrderId,result);
                        JSONObject jsonObject = JSON.parseObject(result);
                        //{"attachment":{"totalExchange":"1.0","trades":[{"tradePrice":"0.0060000000000000","tradeNum":"1.0000000000000000"}]},"status":200,"message":null}
                        if (jsonObject.get("status").toString().equals("200")){
                            Map<String,Object> attachment = (Map<String, Object>) jsonObject.get("attachment");
                            if (MapUtils.isNotEmpty(attachment)){
                                String totalExchange = (String) attachment.get("totalExchange");
                                //更新实际成交的数量  去交易所那边查订单是否完成
                                payMapper.updateActualExchange(totalExchange,tradeOrderId.toString());
                                List<Map<String,Object>>  listT= (List<Map<String, Object>>) attachment.get("trades");
                                if (CollectionUtils.isNotEmpty(listT)){
                                    for (Map<String,Object> map2 : listT){
                                        if (new BigDecimal(quantity).setScale(6).compareTo(new BigDecimal(map2.get("tradeNum").toString()).setScale(6))==0
                                                &&new BigDecimal(tradePrice).setScale(6).compareTo(new BigDecimal(map2.get("tradePrice").toString()).setScale(6))==0){
                                            i=1;
                                        }
                                    }
                                }
                            }
                        }
                    }catch (Exception e){
                        log.error("调用交易所查询订单接口失败:{}",e);
                    }
                }
                //修改订单状态为支付成功
                //发钱给商户 先更新数据库，再在链上转账
                if (i==1){
                    //修改订单支付状态为成功
                    payMapper.updatePayStatus(2,preOrderNo,userId);
                    Coin coin=new Coin();
                    coin.setCoinName(coinType);
                    Coin coin1 = coinMapper.findCoinByFiled(coin);
                    if (coin1==null){
                        throw new RRException(RErrorEnum.NO_TRADE_COIN);
                    }
                    //冻结数量要大于等于quantity
                    UserWallet userWallet1=new UserWallet();
                    userWallet1.setType(Integer.valueOf(coin1.getCoinNo().toString()));
                    userWallet1.setUserId(Long.valueOf(userId));
                    UserWallet wallet = userWalletMapper.getUserWalletByCondition(userWallet1);
                    Address PTNaddress = addressMapper.queryAddressInfo(coinMapper.queryByCoinName("MOC").getCoinNo());
                    //链上的余额
                    BigDecimal pinBalance = new BigDecimal(PNTCoinApi.getApi(PTNaddress).getBalance(wallet.getAddress(),coin1.getCoinName()));
                    if (wallet.getUnbalance().compareTo(new BigDecimal(quantity).add(BigDecimal.valueOf(0.01)))<0||
                            wallet.getUnbalance().add(wallet.getBalance()).compareTo(pinBalance)!=0){
                        //在订单中添加一个标识，用户是因为余额不匹配转不了钱给商家
                        payMapper.updateOrderTag(appId,preOrderNo);
                        throw new RRException(RErrorEnum.AMOUNT_ERROR);
                    }
                    String inAddress=payMapper.getPlatformAddress(appId);
                    String hash = tradeInfoService.payTrade(Long.valueOf(userId), coinType, new BigDecimal(quantity), BigDecimal.valueOf(0.01), inAddress, "用户支付给商户的" + appId, appId);
                    log.info("用户支付给商户返回的hash={},userId={}",hash,userId);
                    if (StringUtils.isNotBlank(hash)){
                        log.info("用户支付给商户转账成功");
                    }else{
                        //修改订单支付状态为支付中
                        payMapper.updatePayStatus(1,preOrderNo,userId);
                        log.info("用户支付给商户转账失败");
                        throw new RRException(RErrorEnum.TRANSFER_ERROR);
                    }
                }
            }
        }
    }
}
