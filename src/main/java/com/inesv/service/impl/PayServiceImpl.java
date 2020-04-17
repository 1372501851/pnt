package com.inesv.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.PayService;
import com.inesv.util.*;
import com.inesv.util.CoinAPI.PNTCoinApi;
import com.jckj.pay.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;

import static com.inesv.entity.CoinParams.BAIL_SWITCH;
import static com.inesv.entity.CoinParams.DEFAULT_COIN_TYPE;

@Slf4j
@Service
public class PayServiceImpl implements PayService {


    @Autowired
    private PayMapper payMapper;

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private ParamsMapper paramsMapper;

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private PointMapper pointMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;


    @Override
    public String getRegisterEncode() {
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString();
        String encodeStr = uuidStr.replaceAll("-", "");
        String encode = encodeStr.substring(0, 16);
        payMapper.getRegisterEncode(encode,encodeStr);
        return encodeStr;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
    public BaseResponse  register(Map<String,String> map1) {
        String data = map1.get("data");
        String encode = map1.get("encode");
        String decodeData;
        try {
            decodeData = ApiUtils.decodeData(encode.substring(0,16), data);
        }catch (Exception e){
            e.printStackTrace();
            return RspUtil.error("解密失败",10006);
        }
        Map<String,String> register = (Map<String, String>) JSON.parse(decodeData);
        if (register.size()<6){
            return RspUtil.error("传入的参数为空",10002);
        }
        String mocAddress = register.get("mocAddress");
        List<UserWallet> wallet = userWalletMapper.getUserWalletByAddress(mocAddress);
        if (!CollectionUtils.isNotEmpty(wallet)){
            return RspUtil.error("输入的钱包地址不存在或有误",10007);
        }
        //判断名称是否存在了
        PayRegister payRegister = new PayRegister();
        payRegister.setPlatformName(register.get("platformName"));
        payRegister.setPlatformPackageName(register.get("platformPackageName"));
        payRegister.setMocAddress(mocAddress);
        payRegister.setTradeUserId(register.get("tradeUserId"));
        payRegister.setPassword(register.get("password"));
        payRegister.setRemark(register.get("remark"));
        PayRegister payRegister1= payMapper.isExistUsername(payRegister);
        if (payRegister1 !=null){
            return RspUtil.error("平台名或商户名已存在",10001);
        }
        //当前地址是否被使用了
        PayRegister payRegister2= payMapper.isExistAddress(payRegister);
        if (payRegister2!=null){
            return RspUtil.error("钱包地址已被使用",10008);
        }
        //判断当前地址是否在积分表中使用了
        /*Integer count=payMapper.isExistPointAddress(mocAddress);
        if (count!=0){
            return RspUtil.error("钱包地址已被使用",10008);
        }*/
        Map<String,String> map=new HashMap<>();
        try {
            payMapper.register(payRegister);
            UUID uuid = UUID.randomUUID();
            String appIdStr = uuid.toString();
            String appId = appIdStr.replaceAll("-", "");
            UUID uuidData = UUID.randomUUID();
            String uuidStr = uuidData.toString();
            String dataKeyStr = uuidStr.replaceAll("-", "");
            String dataKey= dataKeyStr.substring(0, 16);
            UUID uuidSign = UUID.randomUUID();
            String signStr = uuidSign.toString();
            String signature= signStr.replaceAll("-", "");
            payMapper.update(appId,payRegister.getId(),dataKey,signature);
            //在积分表中添加记录
            //payMapper.insertPointInfo(mocAddress,wallet.get(0).getUserId());
            map.put("appId",appId);
            map.put("dataKey",dataKey);
            map.put("signature",signature);
        }catch (Exception e){
            e.printStackTrace();
            return RspUtil.error("网络原因，注册失败",10009);
        }
        return RspUtil.success(map);
    }

    @Override
    public String getDataKey(String signature) {
        String dataKey=payMapper.getDataKey(signature);
        return dataKey;
    }

    @Override
    public PayOrderVo createOrder(String orderNo, String name, String price,String appId,String createTime,String pushUrl,String preOrderNo,String tag,String data,String encode,String moneyMark)  {
        PayOrder payOrder=new PayOrder();
        payOrder.setCreateTime(createTime);
        payOrder.setPushUrl(pushUrl);
        preOrderNo="moc"+preOrderNo;
        payOrder.setPreOrderNo(preOrderNo);
        payOrder.setOrderInfo(name);
        payOrder.setOrderNo(orderNo);
        payOrder.setPrice(price);
        payOrder.setAppId(appId);
        payOrder.setTag(tag);
        payMapper.createOrder(payOrder);
        payMapper.updateEncode(payOrder.getId(),data,encode,moneyMark);
        PayOrderVo payOrderVo= payMapper.getOrder(payOrder);
        return payOrderVo;
    }

    @Override
    public BaseResponse queryOrder(String orderNo, String appId, String preOrderNo) {
        PayOrder payOrder=new PayOrder();
        payOrder.setPreOrderNo(preOrderNo);
        payOrder.setOrderNo(orderNo);
        payOrder.setAppId(appId);
        PayOrderNotify payOrder1=payMapper.queryOrder(payOrder);
        if (payOrder1!=null){
            payOrder1.setTradePrice(null);
        }
        return RspUtil.success(payOrder1);
    }

    @Override
    public String getTag(String appId, String orderNo) {
        String tag=payMapper.getTag(appId,orderNo);
        return tag;
    }

    @Override
    public List<String> getCoinType(Long userId) {
        List<String> list=payMapper.getCoinType(userId);
        return list;
    }

    @Override
    public Integer isRepeatOrder(String appId, String orderNo) {
        Integer count=payMapper.isRepeatOrder(appId,orderNo);
        return count;
    }

    @Override
    public void insertUserId(String appId, String orderNo, Long userId) {
        payMapper.insertUserId(appId,orderNo,userId);
    }

    @Override
    public Integer getPayStatus(String appId, String orderNo, Long userId) {
        Integer payStatus=payMapper.getPayStatus(appId,orderNo,userId);
        return payStatus;
    }

    @Override
    public void freeze(Long userId, String coinType,String coinQuantity) {
        payMapper.freeze(userId,coinType,coinQuantity);
    }

    @Override
    public void updateOrderStatus(String appId, String orderNo, Long userId,String attachment,String coinType) {
        payMapper.updateOrderStatus(appId,orderNo,userId,attachment,coinType);
    }

    @Override
    public BigDecimal getMoney(Long userId, String coinType) {
         BigDecimal bigDecimal=payMapper.getMoney(userId,coinType);
        return bigDecimal;
    }

    @Override
    public Map<String, String> getData(String data) {
        Map<String, String> map=payMapper.getData(data);
        return map;
    }

    @Override
    public String getSignature(String appId1) {
        String dataKey=payMapper.getSignature(appId1);
        return dataKey;
    }

    @Override
    public String getPreOrderNo(String appId, String orderNo) {
        String preOrderNo=payMapper.getPreOrderNo(appId,orderNo);
        return preOrderNo;
    }

    @Override
    public void updateOrderQuantity(String appId, String orderNo, Long userId,String tradePrice, String quantity,String coinType,String mainMoney) {
        payMapper.updateOrderQuantity(appId,orderNo,userId,tradePrice,quantity,coinType,mainMoney);
    }

    @Override
    public PaySellConfig getSellInfo(String appId, String coinType) {
        PaySellConfig paySellConfig=payMapper.getSellInfo(appId,coinType);
        return paySellConfig;
    }

    @Override
    public String getAddress(String appId, int type) {
        String address=payMapper.getAddress(appId,type);
        return address;
    }

    @Override
    public BigDecimal isEnoughPoint(Long userId) {
        BigDecimal point=payMapper.isEnoughPoint(userId);
        return point;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
    public BaseResponse pointPay(String preOrderNo, String orderNo, String appId, Long userId,String pushUrl,String orderInfo) {
        //判断订单是否支付了
        Integer payStatus = getPayStatus(appId, orderNo, userId);
        if (payStatus!=0){
            throw new RRException(RErrorEnum.ORDER_ALREADY_PAY);
        }
        //先判断积分是否够
        String mainMoney=payMapper.getMainMoney(preOrderNo,orderNo,appId,userId);
        BigDecimal point = isEnoughPoint(userId);
        if (new BigDecimal(mainMoney).compareTo(point)>0){
            throw new RRException(RErrorEnum.POINT_NOT_ENOUGH);
        }
        //之后操作数据库进行支付,当前用户扣除积分，商户添加积分,添加积分记录表
        payMapper.deduct(userId,new BigDecimal(mainMoney));
        String address=payMapper.getMerchantAddress(appId);
        List<UserWallet> wallet = userWalletMapper.getUserWalletByAddress(address);
        if (!CollectionUtils.isNotEmpty(wallet)){
            throw new RRException(RErrorEnum.USER_ADDRESS_NO_EXIST);
        }
        Long pUserId = wallet.get(0).getUserId();
        payMapper.addPoint(pUserId,new BigDecimal(mainMoney));
        payMapper.addPointRecord(userId,pUserId,new BigDecimal(mainMoney));
        //更改订单状态
        payMapper.updateOrderByPoint(preOrderNo,orderNo,appId,userId);
        //在零钱包中添加记录
        Map<String,Object> map=payMapper.getNameAndUserId(appId);
        //用户的记录
        pointMapper.insertPayOrder(orderNo,userId,mainMoney,map.get("username").toString(),orderInfo,4);
        //商户的记录
        User info = userMapper.getUserInfoById(userId);
        pointMapper.insertPayOrder(orderNo,Long.valueOf(map.get("userId").toString()),mainMoney,info.getUsername(),orderInfo,5);
        //异步通知第三方接口
        PayOrder payOrder=new PayOrder();
        payOrder.setPreOrderNo(preOrderNo);
        payOrder.setOrderNo(orderNo);
        payOrder.setAppId(appId);
        PayOrderNotify payOrder1=payMapper.queryOrder(payOrder);
        int j=0;
        /*while (j<2){
            try {
                payOrder1.setTradePrice(null);//为了防止返回给前端
                String result1 = PayHttpUtils.postJson(pushUrl, JSON.toJSONString(payOrder1));//发送请求
                log.info("异步通知第三方平台接口:result1={},appId={}",result1,appId);
                if (result1!=null){
                    JSONObject jsonObject1 = JSON.parseObject(result1);
                    if ("200".equals(jsonObject1.get("code").toString())){
                    //在订单中修改第三方App已收到异步通知
                                        payMapper.updateNotice(appId,orderNo,userId);
                        break;
                    }
                }
                j++;
            }catch (Exception e){
                e.printStackTrace();
                j++;
            }
        }*/
        return RspUtil.success();
    }

    @Override
    public BaseResponse refresh(String price, String appId, String payTypeId, String rate,Long userId,String orderNo) {
        if ("POINT".equals(payTypeId)){
            Map<String,Object> map=new HashMap<>();
            //积分换算 1人民币等于10积分
            BigDecimal moneyPoint = new BigDecimal(rate).multiply(new BigDecimal(10)).setScale(6, BigDecimal.ROUND_HALF_UP);
            payMapper.updateOrderQuantity(appId,orderNo,userId,"0.01",moneyPoint.toString(),payTypeId,moneyPoint.toString());
            map.put("coinQuantity",moneyPoint);
            //判断是否是积分
            BigDecimal point=payMapper.isEnoughPoint(userId);
            //true余额够
            if (moneyPoint.compareTo(point)>0){
                map.put("isBalance",false);
            }else {
                map.put("isBalance",true);
            }
            return RspUtil.success(map);
        }else {
            Map<String,Object> map2=new HashMap<>();
            map2.put("coins","usdt_"+payTypeId.toLowerCase());
            String coinQuantity="23.03";
            //调取usdt对人民币汇率接口
            //String paramValue = getUsdtExchangeCny();
            //BigDecimal decimal = new BigDecimal(rate).divide(new BigDecimal(paramValue),6, BigDecimal.ROUND_HALF_UP);
            int i=0;
            //while (i<3){
                /*try {
                    String address=payMapper.getAddress(appId,3);
                    String result = HttpUtils.doGet(address, map2);
                    log.info("成功深度接口{}",result);
                    JSONObject jsonObject = JSON.parseObject(result);
                    Object status = jsonObject.get("status");
                    if (status.toString().equals("200")){
                        Map<String,List<List<String>>> lists= (Map<String, List<List<String>>>) jsonObject.get("attachment");
                        if (MapUtils.isNotEmpty(lists)){
                            List<List<String>> lists1=lists.get("bids");
                            for (List<String> list :lists1){
                                BigDecimal bigDecimal = new BigDecimal(list.get(0)).multiply(new BigDecimal(list.get(1)));
                                if (bigDecimal.compareTo(decimal)<=0){
                                    continue;
                                }else {
                                    coinQuantity=decimal.divide(new BigDecimal(list.get(0)),6,BigDecimal.ROUND_HALF_UP).toString();
                                    payMapper.updateOrderQuantity(appId,orderNo,userId,list.get(0),coinQuantity,payTypeId,decimal.toString());
                                    i=4;
                                    break;
                                }
                            }
                        }
                    }else {
                        log.info("异常深度接口{}",result);
                        i++;
                    }
                }catch (Exception e){
                    log.error("调交易所深度接口失败:{}",e);
                    i++;
                }*/
            //}
            /*if (coinQuantity.equals("")){
                //调用交易所深度接口失败
                throw new RRException(RErrorEnum.EXCHANGE_FAIL);
            }*/
            //payMapper.updateOrderQuantity(appId,orderNo,userId,"1.1",coinQuantity,payTypeId,decimal.toString());
            Coin coin=new Coin();
            coin.setCoinName(payTypeId);
            Coin coin1 = coinMapper.findCoinByFiled(coin);
            if (coin1==null){
                throw new RRException(RErrorEnum.NO_TRADE_COIN);
            }
            BigDecimal add = new BigDecimal(rate).add(BigDecimal.valueOf(1));
            BigDecimal decimal = add.divide(BigDecimal.valueOf(7), 6, BigDecimal.ROUND_HALF_UP);
            payMapper.updateOrderQuantity(appId,orderNo,userId,"1.1",decimal.toString(),payTypeId,decimal.toString());
            String coinNo = coin1.getCoinNo().toString();
            //判断余额是否够
            //数据库的余额
            BigDecimal balance=payMapper.getMoney(userId,coinNo);
            Map<String,Object> map4=new HashMap<>();
            map4.put("coinQuantity",decimal.toString());
            //true余额够,要加上手续费
            List<UserWallet> wallets = userWalletMapper.getuserWallet(userId);
            String address="";
            for (UserWallet userWallet : wallets){
                if (userWallet.getType().toString().equals(coinNo)){
                    address=userWallet.getAddress();
                    break;
                }
            }
            Address PTNaddress = addressMapper.queryAddressInfo(coinMapper.queryByCoinName("MOC").getCoinNo());
            //链上的余额
            BigDecimal pinBalance = new BigDecimal(PNTCoinApi.getApi(PTNaddress).getBalance(address,payTypeId));
            if (balance==null||decimal.add(BigDecimal.valueOf(0.01)).compareTo(balance)>0||decimal.add(BigDecimal.valueOf(0.01)).compareTo(pinBalance)>0){
                map4.put("isBalance",false);
            }else {
                map4.put("isBalance",true);
            }
            return RspUtil.success(map4);
        }
    }


    @Override
    public Map<String, Object> orderInfo(String price, String rate, String appId,Long userId,String orderNo) {
        //调取usdt对人民币汇率接口
        //String paramValue = getUsdtExchangeCny();
        //BigDecimal decimal = new BigDecimal(rate).divide(new BigDecimal(paramValue),6, BigDecimal.ROUND_HALF_UP);
        Map<String,Object> map=new HashMap<>();
        Params params = paramsMapper.getParams(DEFAULT_COIN_TYPE);
        map.put("coins","usdt_"+params.getParamValue().toLowerCase());
        int i=0;
        //while (i<3){
            try {
                //获取深度接口地址，3为深度接口类型
                /*String address=payMapper.getAddress(appId,3);
                String result = HttpUtils.doGet(address, map);
                log.info("成功深度接口{}",result);
                JSONObject jsonObject = JSON.parseObject(result);
                Object status = jsonObject.get("status");
                if (status.toString().equals("200")){
                    Map<String,List<List<String>>> lists= (Map<String, List<List<String>>>) jsonObject.get("attachment");
                    if (MapUtils.isNotEmpty(lists)){
                        List<List<String>> lists1=lists.get("bids");
                        for (List<String> list :lists1){
                            BigDecimal bigDecimal = new BigDecimal(list.get(0)).multiply(new BigDecimal(list.get(1)));
                            if (bigDecimal.compareTo(decimal)<=0){
                                continue;
                            }else {
                                BigDecimal quantity = decimal.divide(new BigDecimal(list.get(0)), 6, BigDecimal.ROUND_HALF_UP);
                                map.put("coinQuantity",quantity);
                                //更订单对应的币数量
                                payMapper.updateOrderQuantity(appId,orderNo,userId,list.get(0),quantity.toString(),params.getParamValue(),decimal.toString());
                                i=4;
                                break;
                            }
                        }
                    }

                }else {
                    log.info("异常深度接口:{}",result);
                    i++;
                }*/
            }catch (Exception e){
                log.error("调交易所深度接口失败:{}",e);
                i++;
            }
        //}
        BigDecimal add = new BigDecimal(rate).add(BigDecimal.valueOf(1));
        BigDecimal decimal = add.divide(BigDecimal.valueOf(7), 6, BigDecimal.ROUND_HALF_UP);
        payMapper.updateOrderQuantity(appId,orderNo,userId,"1.1",decimal.toString(),params.getParamValue(),decimal.toString());
        map.put("coinQuantity",decimal.toString());
        return map;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
    public void coinPay(String coinType, String preOrderNo, String orderNo, String appId, Long userId, String pushUrl) {
        Data data=new Data();
        Future<Data> future= ExecutorConfig.threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    //订单已支付，请勿重新支付
                    Integer payStatus=payMapper.getPayStatus(appId,orderNo,userId);
                    if (payStatus!=0){
                        data.setCode(1022);
                        return;
                    }
                    Coin coin=new Coin();
                    coin.setCoinName(coinType);
                    Coin coin1 = coinMapper.findCoinByFiled(coin);
                    if (coin1==null){
                        data.setCode(1019);
                        return;
                    }
                    String coinNo = coin1.getCoinNo().toString();
                    //先获取订单价格和数量
                    PayOrder payOrder=new PayOrder();
                    payOrder.setPreOrderNo(preOrderNo);
                    payOrder.setOrderNo(orderNo);
                    payOrder.setAppId(appId);
                    PayOrderNotify payOrder1=payMapper.queryOrder(payOrder);
                    String coinQuantity =payOrder1.getQuantity();
                    String tradePrice =payOrder1.getTradePrice();
                    //判断余额是否够
                    List<UserWallet> wallets = userWalletMapper.getuserWallet(userId);
                    String address1="";
                    for (UserWallet userWallet : wallets){
                        if (userWallet.getType().toString().equals(coinNo)){
                            address1=userWallet.getAddress();
                            break;
                        }
                    }
                    Address PTNaddress = addressMapper.queryAddressInfo(coinMapper.queryByCoinName("MOC").getCoinNo());
                    //链上的余额
                    BigDecimal pinBalance = new BigDecimal(PNTCoinApi.getApi(PTNaddress).getBalance(address1,coinType));
                    //BigDecimal pinBalance = new BigDecimal("100000");
                    //数据库的余额
                    BigDecimal balance=payMapper.getMoney(userId,coinNo);
                    if (balance==null||new BigDecimal(coinQuantity).add(new BigDecimal("0.01")).compareTo(balance)>0||new BigDecimal(coinQuantity).add(new BigDecimal("0.01")).compareTo(pinBalance)>0){
                        data.setCode(1017);
                        return;
                    }
                    Params params = paramsMapper.getParams(BAIL_SWITCH);
                    String value = params.getParamValue();
                    //是否委托，1是委托
                    if (value.equals("1")){
                        int i=0;
                        //调用交易所接口
                        PaySellConfig paySellConfig=payMapper.getSellInfo(appId,coinType);
                        //paySellConfig.setPrice(Double.valueOf(tradePrice));
                        //paySellConfig.setNum(Double.valueOf(coinQuantity));
                        while (i<2){
                            try {
                            /*"uid":交易所的用户uid,
                                    "currencyId":币种id, //GBL=100
                                    "baseCurrencyId":交易区币种id, //usdt=4
                                    "type":1, //1限价单 2市价单  默认应传1
                                    "buyOrSell":2, //买卖方向  1.买 2卖
                                    "price":100, //单价
                                    "num":100 ,//数量
                                    "fdPassword":"交易密码 可选 设置了就要填写",
                                    "source":1 ,//来源 默认填写1*/
                                //{"attachment":"15694683251937390370521110017259","status":200,"message":null}
                                //地址做成配置的
                                String address=payMapper.getAddress(appId,1);
                                //String result = PayHttpUtils.postJson( address, JSON.toJSONString(paySellConfig));
                                String result="1";
                                log.info("调用交易所卖出接口:result={},userId={}",result,userId);
                                if (result!=null){
                                    //JSONObject jsonObject = JSON.parseObject(result);
                                    if (/*jsonObject.get("status").toString().equals("200")*/1==1){
                                        //进行冻结
                                        payMapper.freeze(userId,coinNo,new BigDecimal(coinQuantity).add(BigDecimal.valueOf(0.01)).toString());
                                        //String attachment = jsonObject.get("attachment").toString();
                                        String attachment = "1234455";
                                        //修改支付状态和添加交易id
                                        payMapper.updateOrderStatus(appId,orderNo,userId,attachment,coinType);
                                        //异步通知第三方接口
                                        int j=0;
                                        while (j<2){
                                            try {
                                                payOrder1.setTradePrice(null);//为了防止返回给前端
                                                String result1 = PayHttpUtils.postJson(pushUrl, JSON.toJSONString(payOrder1));//发送请求
                                                System.out.println(result1);
                                                log.info("异步通知第三方平台接口:result1={},appId={}",result1,appId);
                                                if (result1!=null){
                                                    JSONObject jsonObject1 = JSON.parseObject(result1);
                                                    if ("200".equals(jsonObject1.get("code").toString())){
                                                        //在订单中修改第三方App已收到异步通知
                                                        payMapper.updateNotice(appId,orderNo,userId);
                                                        break;
                                                    }
                                                }
                                                j++;
                                            }catch (Exception e){
                                                log.error("通知第三方异步失败:{}",e);
                                                j++;
                                            }
                                        }
                                        data.setCode(-1);
                                        break;
                                    }else {
                                        data.setCode(1023);
                                    }
                                }
                                i++;
                            }catch (Exception e){
                                log.error("调交易所委托接口失败:{}",e);
                                i++;
                                data.setCode(1023);
                            }
                        }
                    }else {
                        payMapper.freeze(userId,coinNo,new BigDecimal(coinQuantity).add(BigDecimal.valueOf(0.01)).toString());
                        payMapper.updateOrderState(appId,orderNo,userId,coinType);
                        payOrder1.setPayStatus(1);
                        int j=0;
                        while (j<2){
                            try {
                                payOrder1.setTradePrice(null);//为了防止返回给前端
                                String result1 = PayHttpUtils.postJson(pushUrl, JSON.toJSONString(payOrder1));//发送请求
                                log.info("异步通知第三方平台接口:result1={},appId={}",result1,appId);
                                if (result1!=null){
                                    JSONObject jsonObject1 = JSON.parseObject(result1);
                                    if ("200".equals(jsonObject1.get("code").toString())){
                                        //在订单中修改第三方App已收到异步通知
                                        payMapper.updateNotice(appId,orderNo,userId);
                                        break;
                                    }
                                }
                                j++;
                            }catch (Exception e){
                                log.error("通知第三方异步失败:{}",e);
                                j++;
                            }
                        }
                    }
                }
            }
        },data);
        Data data2=null;
        try {
            data2= future.get();
        } catch (Exception e) {
            log.error("线程异步执行返回的结果:{}",e);
        }
        if (data2 != null&& data2.getCode()!=null){
            if (data2.getCode()==1017){
                throw new RRException(RErrorEnum.INSUFFICIENT_AMOUNT);
            }else if (data2.getCode()==1022){
                throw new RRException(RErrorEnum.ORDER_ALREADY_PAY);
            }else if (data2.getCode()==1023){
                //支付失败
                throw new RRException(RErrorEnum.PAY_FAIL);
            }else if(data2.getCode()==1019){
                throw new RRException(RErrorEnum.NO_TRADE_COIN);
            }
        }
    }

    @Override
    public String getUsdtExchangeCny() {
        //调取usdt对人民币汇率接口
        String paramValue;
        try {
            String financeRate =  HttpUtil.getUsdtQcMarket("usdt_qc");
            JSONObject json = JSON.parseObject(financeRate);
            String ticker =json.getString("ticker");
            JSONObject json1 = JSON.parseObject(ticker);
            paramValue= json1.getString("last");
        }catch (Exception e){
            e.printStackTrace();
            throw new RRException(RErrorEnum.EXCHANGE_FAIL);
        }
        return paramValue;
    }
}
