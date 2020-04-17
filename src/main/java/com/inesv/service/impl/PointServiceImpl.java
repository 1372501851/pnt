package com.inesv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.PayService;
import com.inesv.service.PointService;
import com.inesv.service.TradeInfoService;
import com.inesv.util.BaseResponse;
import com.inesv.util.HttpUtil;
import com.inesv.util.OrderUtil;
import com.inesv.util.RspUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inesv.entity.CoinParams.DEFAULT_COIN_TYPE;
import static com.inesv.entity.CoinParams.USD_EXCHANGE_CNY;

@Slf4j
@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private PointMapper pointMapper;

    @Autowired
    private ParamsMapper paramsMapper;

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private PayMapper payMapper;

    @Autowired
    private PayService payService;

    @Autowired
    private TradeInfoService tradeInfoService;

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Override
    public Map<String, String> createOrder(User user, String point) {
        synchronized (this) {
        BigDecimal amount = new BigDecimal(point).divide(BigDecimal.valueOf(10)).setScale(6, BigDecimal.ROUND_HALF_UP);
        //调取usdt对人民币汇率接口
        String paramValue = payService.getUsdtExchangeCny();
        //计算有多少usdt
        BigDecimal usdt = amount.divide(new BigDecimal(paramValue),8, BigDecimal.ROUND_HALF_UP);
        //取一个默认的币种
        Params params = paramsMapper.getParams(DEFAULT_COIN_TYPE);
        String defaultCoin = params.getParamValue();
        //调用交易当前币种与usdt的汇率接口
        //添加积分记录
        String orderNo = OrderUtil.makeOrderCode();
        pointMapper.createOrder(user.getId(),point,defaultCoin,"12.8",orderNo);
        Map<String,String> map=new HashMap<>();
        map.put("point",point);
        map.put("account",user.getUsername());
        map.put("orderNo",orderNo);
        map.put("payCoin",defaultCoin);
        map.put("payAmount","1221");
        return map;
        }
    }

    @Override
    public Map<String, Object> refresh(User user, String orderNo, String payCoin) {
        Long userId = user.getId();
        //获取订单中的积分
        String point=pointMapper.getPoint(userId,orderNo);
        if (point==null){
            //订单异常
            throw new RRException(RErrorEnum.ORDER_ABNORMAL);
        }
        //调取usdt对人民币汇率接口
        String paramValue = payService.getUsdtExchangeCny();
        //计算有多少usdt
        BigDecimal usdt = new BigDecimal(point).divide(new BigDecimal(paramValue),8, BigDecimal.ROUND_HALF_UP);
        //更新币种和数量
        Coin coin=new Coin();
        coin.setCoinName(payCoin);
        Coin coin1 = coinMapper.findCoinByFiled(coin);
        if (coin1==null){
            throw new RRException(RErrorEnum.NO_TRADE_COIN);
        }
        String coinNo = coin1.getCoinNo().toString();
        //判断余额是否够
        BigDecimal balance=payMapper.getMoney(userId,coinNo);
        Map<String,Object> map=new HashMap<>();
        map.put("coinQuantity",12.34);
        //true余额够,要加上手续费
        if (balance==null||new BigDecimal(12.34).add(BigDecimal.valueOf(0.01)).compareTo(balance)>0){
            map.put("isBalance",false);
        }else {
            map.put("isBalance",true);
        }
        return map;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
    public void pay(Long userId, String orderNo) {
        synchronized (this){
            //防止重复支付
            PayPointOrder payPointOrder=pointMapper.getOrderInfo(userId,orderNo);
            if (payPointOrder==null){
                throw new RRException(RErrorEnum.ORDER_ABNORMAL);
            }
            if (payPointOrder.getPayState()==1){
                throw new RRException(RErrorEnum.ORDER_ALREADY_PAY);
            }
            Coin coin=new Coin();
            String payCoin = payPointOrder.getPayCoin();
            coin.setCoinName(payCoin);
            Coin coin1 = coinMapper.findCoinByFiled(coin);
            if (coin1==null){
                throw new RRException(RErrorEnum.NO_TRADE_COIN);
            }
            String coinNo = coin1.getCoinNo().toString();
            //判断余额是否够
            BigDecimal balance=payMapper.getMoney(userId,coinNo);
            String amount=payPointOrder.getPayAmount();
            if (balance==null||new BigDecimal(amount).add(BigDecimal.valueOf(0.01)).compareTo(balance)>0){
                throw new RRException(RErrorEnum.INSUFFICIENT_AMOUNT);
            }
            //String txHash = tradeInfoService.buyPointTrade(userId, new BigDecimal(amount), new BigDecimal("0.01"),
                    //"中心账户地址", payCoin,"用户支付币种购买积分");
            String txHash="1";
            if (StringUtils.isBlank(txHash)){
                throw new RRException(RErrorEnum.TRANSFER_ERROR);
            }else {
                String point = payPointOrder.getPoint();
                //修改订单状态
                pointMapper.updatePayState(userId,orderNo);
                //添加充值记录
                pointMapper.insertPointPay(userId,orderNo,"中心账户名称",point,payCoin,amount);
                //添加积分数值
                pointMapper.updatePoint(userId,new BigDecimal(point));

            }
        }
    }

    @Override
    public BaseResponse payRecord(Long userId, Integer pageNum, Integer pageSize,Integer type) {
        PageHelper.startPage(pageNum, pageSize);
        List<PayPointRecord> pointRecords=new ArrayList<>();
        switch (type){
            case 0:pointRecords=pointMapper.payRecord(userId);break;
            case 1:pointRecords=pointMapper.payRecordByType(userId,type);break;
            case 2:pointRecords=pointMapper.payRecordByType(userId,type);break;
            case 3:pointRecords=pointMapper.payRecordByType(userId,type);break;
            case 4:pointRecords=pointMapper.payRecordByType(userId,type);break;
            case 5:pointRecords=pointMapper.payRecordByType(userId,type);break;
            case 6:pointRecords=pointMapper.PayRecordBySDK(userId);break;
            default:break;
        }
        PageInfo<PayPointRecord> tradePageInfo = new PageInfo<PayPointRecord>(pointRecords);
        return RspUtil.success(tradePageInfo.getList());
    }

    @Override
    public Map<String, String> balance(Long userId, String lang) {
        Map<String,String> map=new HashMap<>();
        BigDecimal point = payMapper.isEnoughPoint(userId);
        map.put("pointBalance",point.toString());
        if (point.compareTo(BigDecimal.ZERO)==0){
            map.put("moneyBalance",point.toString());
        }else {
            BigDecimal moneyBalance = point.divide(BigDecimal.valueOf(10), 6, BigDecimal.ROUND_HALF_UP);
            if ("zh_CN".equals(lang)) {
                map.put("moneyBalance",moneyBalance.toString());
            }else {
                Params params = paramsMapper.getParams(USD_EXCHANGE_CNY);
                BigDecimal usdMoney = new BigDecimal(params.getParamValue()).multiply(moneyBalance).setScale(6, BigDecimal.ROUND_HALF_UP);
                map.put("moneyBalance",usdMoney.toString());
            }
        }
        return map;
    }
}
