package com.inesv.controller;


import com.alibaba.fastjson.JSON;
import com.inesv.annotation.Login;
import com.inesv.annotation.LoginUser;
import com.inesv.annotation.UnLogin;
import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.inesv.mapper.ParamsMapper;
import com.inesv.mapper.PayMapper;
import com.inesv.mapper.UserMapper;
import com.inesv.model.*;
import com.inesv.service.PayService;
import com.inesv.service.UserService;
import com.inesv.util.*;
import com.jckj.pay.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inesv.entity.CoinParams.*;


@Slf4j
@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayService payService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PayMapper payMapper;

    @Autowired
    private ParamsMapper paramsMapper;


    //平台注册加密接口
    @RequestMapping("/getRegisterEncode")
    @UnLogin
    public String getRegisterEncode() {
        String baseResponse = payService.getRegisterEncode();
        return baseResponse;
    }


    //平台注册接口
    @RequestMapping("/register")
    @UnLogin
    public BaseResponse register(@RequestBody Map<String, String> map) {
        if (!MapUtils.isNotEmpty(map) || map.size() < 2) {
            return RspUtil.error("传入的参数为空", 10002);
        }
        BaseResponse baseResponse = payService.register(map);
        return baseResponse;
    }

    //创建预订单
    @RequestMapping("/createOrder")
    @UnLogin
    public BaseResponse createOrder(@RequestBody Map<String, String> map) {
        if (!MapUtils.isNotEmpty(map)) {
            return RspUtil.error("传入的参数为空", 10002);
        }
        String data = map.get("data");
        String tag = map.get("tag");
        String signature = map.get("signature");
        if (data == null || signature == null || tag == null) {
            return RspUtil.error("传入的参数为空", 10002);
        }

        String dataKey = payService.getDataKey(signature);
        if (dataKey == null) {
            return RspUtil.error("验签失败", 10003);
        }
        String decodeData;
        try {
            decodeData = ApiUtils.decodeData(dataKey, data);
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error("验签失败", 10003);
        }
        String md5 = PayMD5Uitl.MD5(decodeData);
        if (!md5.equals(tag)) {
            return RspUtil.error("验签失败", 10003);
        }
        Map<String, String> order = (Map<String, String>) JSON.parse(decodeData);
        if (!MapUtils.isNotEmpty(order) || order.size() < 6) {
            return RspUtil.error("传入的参数为空", 10002);
        }
        String name = order.get("orderInfo");
        String price = order.get("price");
        String orderNo = order.get("orderNo");
        String moneyMark = order.get("moneyMark");
        String appId = order.get("appId");
        String createTime = order.get("createTime");
        String pushUrl = order.get("pushUrl");
        String preOrderNo;
        //判断金额是否大于0
        if (new BigDecimal(price).compareTo(BigDecimal.ZERO) <= 0) {
            return RspUtil.error("输入的价格不正确", 10004);
        }
        Map<String, Object> mapResult = new HashMap<>();
        synchronized (this) {
            //防止订单重复生成
            Integer count = payService.isRepeatOrder(appId, orderNo);
            if (count == 1) {
                return RspUtil.error("订单已生成，请勿重复提交订单", 10005);
            }
            preOrderNo = OrderUtil.makeOrderCode();
            String code = MD5Util.GetMD5Code(data + signature);
            PayOrderVo payOrderVo = payService.createOrder(orderNo, name, price, appId, createTime, pushUrl, preOrderNo, tag, data, code, moneyMark);
            mapResult.put("payOrderVo", payOrderVo);
            mapResult.put("encode", code);
        }
        return RspUtil.success(mapResult);
    }


    //第三方查询订单接口
    @RequestMapping("/queryOrder")
    @UnLogin
    public BaseResponse queryOrder(@RequestBody Map<String, String> map) {
        if (!MapUtils.isNotEmpty(map)) {
            return RspUtil.error("传入的参数为空", 10002);
        }
        String data = map.get("data");
        String signature = map.get("signature");
        if (data == null || signature == null) {
            return RspUtil.error("传入的参数为空", 10002);
        }
        String dataKey = payService.getDataKey(signature);
        if (dataKey == null) {
            return RspUtil.error("验签失败", 10003);
        }
        String decodeData;
        try {
            decodeData = ApiUtils.decodeData(dataKey, data);
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error("验签失败", 10003);
        }
        Map<String, String> order = (Map<String, String>) JSON.parse(decodeData);
        if (!MapUtils.isNotEmpty(order) || order.size() < 3) {
            return RspUtil.error("传入的参数为空", 10002);
        }
        String orderNo = order.get("orderNo");
        String appId = order.get("appId");
        String preOrderNo = order.get("preOrderNo");
        BaseResponse baseResponse = payService.queryOrder(orderNo, appId, preOrderNo);
        return baseResponse;
    }


    //支付接口
    @RequestMapping("/money")
    @Login
    @UnLogin
    public BaseResponse money(@LoginUser User user, @RequestBody Map<String, String> map) {
        if (!MapUtils.isNotEmpty(map)) {
            throw new RRException(RErrorEnum.PARAMETER_EMPTY);
        }
        String data = map.get("encode");
        String password = map.get("password");
        String coinType = map.get("payTypeId");
        Long userId = user.getId();
        boolean isPassword = userService.isTradePaswd(userId, password);
        if (!isPassword) {
            throw new RRException(RErrorEnum.TRADEPASSWD_ERROR);
        }
        Map<String, String> map3 = payService.getData(data);
        if (!MapUtils.isNotEmpty(map3)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String data1 = map3.get("data");
        String appId1 = map3.get("appId");
        String signature = payService.getSignature(appId1);
        String code = MD5Util.GetMD5Code(data1 + signature);
        if (!code.equals(data)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String dataKey = payService.getDataKey(signature);
        if (dataKey == null) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String decodeData;
        try {
            decodeData = ApiUtils.decodeData(dataKey, data1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        Map<String, String> order = (Map<String, String>) JSON.parse(decodeData);
        String appId = order.get("appId");
        String orderNo = order.get("orderNo");
        String pushUrl = order.get("pushUrl");
        String preOrderNo = payService.getPreOrderNo(appId, orderNo);
        String tag = payService.getTag(appId, orderNo);
        String md5 = PayMD5Uitl.MD5(decodeData);
        if (!md5.equals(tag)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        //支付的币种是否和数据库订单中的一致
        String payCoinType = payMapper.getPayCoinType(userId, orderNo);
        if (!coinType.equals(payCoinType)) {
            throw new RRException(RErrorEnum.COIN_TYPE_DIFFERENT);
        }
        //积分支付的
        if ("POINT".equals(coinType)) {
            synchronized (this) {
                String orderInfo = order.get("orderInfo");
                BaseResponse baseResponse = payService.pointPay(preOrderNo, orderNo, appId, userId, pushUrl,orderInfo);
                return baseResponse;
            }
        } else {
            //币种支付的
            payService.coinPay(coinType, preOrderNo, orderNo, appId, userId, pushUrl);
        }
        return RspUtil.success();
    }

    //moc钱包查询订单接口前调用的验签接口
    @RequestMapping("/orderCheck")
    @UnLogin
    public BaseResponse orderCheck(@RequestParam("encode") String encode) {
        if (encode == null || "".equals(encode)) {
            throw new RRException(RErrorEnum.PARAMETER_EMPTY);
        }
        Map<String, String> map3 = payService.getData(encode);
        if (!MapUtils.isNotEmpty(map3)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String data1 = map3.get("data");
        String appId1 = map3.get("appId");
        String signature = payService.getSignature(appId1);
        String code = MD5Util.GetMD5Code(data1 + signature);
        if (!code.equals(encode)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String dataKey = payService.getDataKey(signature);
        if (dataKey == null) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String decodeData;
        try {
            decodeData = ApiUtils.decodeData(dataKey, data1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        Map<String, String> order = (Map<String, String>) JSON.parse(decodeData);
        if (!MapUtils.isNotEmpty(order)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String appId = order.get("appId");
        String orderNo = order.get("orderNo");
        String tag = payService.getTag(appId, orderNo);
        String md5 = PayMD5Uitl.MD5(decodeData);
        if (!md5.equals(tag)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        /*Integer payStatus = payMapper.getPayState(appId, orderNo);
        if (payStatus != 0) {
            throw new RRException(RErrorEnum.ORDER_ALREADY_PAY);
        }*/
        return RspUtil.success();
    }


    //moc钱包查询订单接口
    @RequestMapping("/orderInfo")
    @Login
    @UnLogin
    public BaseResponse orderInfo(@LoginUser User user, @RequestBody Map<String, String> map, @RequestHeader("lang") String lang) {
        if (!MapUtils.isNotEmpty(map)) {
            throw new RRException(RErrorEnum.PARAMETER_EMPTY);
        }
        String data = map.get("encode");
        Map<String, String> map3 = payService.getData(data);
        if (!MapUtils.isNotEmpty(map3)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String data1 = map3.get("data");
        String appId1 = map3.get("appId");
        String signature = payService.getSignature(appId1);
        String code = MD5Util.GetMD5Code(data1 + signature);
        if (!code.equals(data)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String dataKey = payService.getDataKey(signature);
        if (dataKey == null) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String decodeData;
        try {
            decodeData = ApiUtils.decodeData(dataKey, data1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        Long userId = user.getId();
        Map<String, String> order = (Map<String, String>) JSON.parse(decodeData);
        String appId = order.get("appId");
        String orderNo = order.get("orderNo");
        String price = order.get("price");
        String moneyMark = order.get("moneyMark");
        String tag = payService.getTag(appId, orderNo);
        String orderInfo = order.get("orderInfo");
        String md5 = PayMD5Uitl.MD5(decodeData);
        if (!md5.equals(tag)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        Map<String, Object> map1 = new HashMap<>();
        map1.put("orderInfo", orderInfo);
        User userInfoById = userMapper.getUserInfoById(userId);
        map1.put("username", userInfoById.getUsername());
        Params params = paramsMapper.getParams(DEFAULT_COIN_TYPE);
        map1.put("coinName", params.getParamValue());
        map1.put("coinId", params.getParamValue());
        String rate = getRate(moneyMark, price);
        String actualPrice = price;
        if ("zh_CN".equals(lang)) {
            //各种货币对人民币的汇率
            if (USD_MARK_$.contains(moneyMark)) {
                Params params1 = paramsMapper.getParams(USD_EXCHANGE_CNY);
                BigDecimal decimal = new BigDecimal(params1.getParamValue()).multiply(new BigDecimal(price)).setScale(6, BigDecimal.ROUND_HALF_UP);
                actualPrice = decimal.toString();
            } else if (KRW_MARK_₩.contains(moneyMark)) {
                Params params1 = paramsMapper.getParams(KRW_EXCHANGE_CNY);
                BigDecimal decimal = new BigDecimal(params1.getParamValue()).multiply(new BigDecimal(price)).setScale(6, BigDecimal.ROUND_HALF_UP);
                actualPrice = decimal.toString();
            } else if (HKD_MARK_HK$.contains(moneyMark)) {
                Params params1 = paramsMapper.getParams(HKD_EXCHANGE_CNY);
                BigDecimal decimal = new BigDecimal(params1.getParamValue()).multiply(new BigDecimal(price)).setScale(6, BigDecimal.ROUND_HALF_UP);
                actualPrice = decimal.toString();
            }
        } else {
            //各种货币对美元的汇率
            if (CNY_MARK_¥.contains(moneyMark)) {
                Params params1 = paramsMapper.getParams(CNY_EXCHANGE_USD);
                BigDecimal decimal = new BigDecimal(params1.getParamValue()).multiply(new BigDecimal(price)).setScale(6, BigDecimal.ROUND_HALF_UP);
                actualPrice = decimal.toString();
            } else if (KRW_MARK_₩.contains(moneyMark)) {
                Params params1 = paramsMapper.getParams(KRW_EXCHANGE_USD);
                BigDecimal decimal = new BigDecimal(params1.getParamValue()).multiply(new BigDecimal(price)).setScale(6, BigDecimal.ROUND_HALF_UP);
                actualPrice = decimal.toString();
            } else if (HKD_MARK_HK$.contains(moneyMark)) {
                Params params1 = paramsMapper.getParams(HKD_EXCHANGE_USD);
                BigDecimal decimal = new BigDecimal(params1.getParamValue()).multiply(new BigDecimal(price)).setScale(6, BigDecimal.ROUND_HALF_UP);
                actualPrice = decimal.toString();
            }
        }
        map1.put("price", actualPrice);
        //获取数量
        Map<String, Object> map2 = payService.orderInfo(price, rate, appId, userId, orderNo);
        Object quantity = map2.get("coinQuantity");
        if (quantity == null) {
            //调用交易所深度接口失败
            throw new RRException(RErrorEnum.EXCHANGE_FAIL);
        }
        map1.put("coinQuantity", quantity);
        return RspUtil.success(map1);
    }

    //moc钱包查询币种类型
    @RequestMapping("/payType")
    @Login
    @UnLogin
    public BaseResponse payType(@LoginUser User user) {

        Long userId = user.getId();
        List<String> list = payService.getCoinType(userId);
        List<Map<String, String>> listM = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (String name : list) {
                Map<String, String> map = new HashMap<>();
                map.put("coinId", name);
                map.put("coinName", name);
                listM.add(map);
            }
            //积分开关  1是开
            Params params = paramsMapper.getParams(PAY_POINT_SWITCH);
            if (params.getParamValue().equals("1")){
                Map<String, String> map2 = new HashMap<>();
                map2.put("coinId", "POINT");
                map2.put("coinName", "积分");
                listM.add(map2);
            }
        }
        return RspUtil.success(listM);
    }

    //订单汇率  Refresh
    @RequestMapping("/refresh")
    @Login
    @UnLogin
    public BaseResponse refresh(@LoginUser User user, @RequestParam("payTypeId") String payTypeId, @RequestParam("encode") String encode) {
        Map<String, String> map3 = payService.getData(encode);
        if (!MapUtils.isNotEmpty(map3)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String data1 = map3.get("data");
        String appId1 = map3.get("appId");
        String signature = payService.getSignature(appId1);
        String code = MD5Util.GetMD5Code(data1 + signature);
        if (!code.equals(encode)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String dataKey = payService.getDataKey(signature);
        if (dataKey == null) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        String decodeData;
        try {
            decodeData = ApiUtils.decodeData(dataKey, data1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        Long userId = user.getId();
        Map<String, String> order = (Map<String, String>) JSON.parse(decodeData);
        String appId = order.get("appId");
        String orderNo = order.get("orderNo");
        String price = order.get("price");
        String moneyMark = order.get("moneyMark");
        String tag = payService.getTag(appId, orderNo);
        String md5 = PayMD5Uitl.MD5(decodeData);
        if (!md5.equals(tag)) {
            throw new RRException(RErrorEnum.CHECK_FAIL);
        }
        //语言（lang=zh_CN 中文，lang=en_US 英文，lang=ko_KR 韩语）
        String rate = getRate(moneyMark, price);
        BaseResponse baseResponse = payService.refresh(price, appId, payTypeId, rate, userId, orderNo);
        return baseResponse;
    }


    //获取汇率
    public String getRate(String moneyMark, String price) {
        String rate;
        /*if (CNY_MARK_¥.contains(moneyMark)){
            Params params1 = paramsMapper.getParams(CNY_MARK_¥);
            rate=params1.getParamValue();
        }else if (USD_MARK_$.contains(moneyMark)){
            Params params2 = paramsMapper.getParams(USD_MARK_$);
            rate=params2.getParamValue();
        }else if (KRW_MARK_₩.contains(moneyMark)){
            Params params3 = paramsMapper.getParams(KRW_MARK_₩);
            rate=params3.getParamValue();

        }else if (HKD_MARK_HK$.contains(moneyMark)){
            Params params4 = paramsMapper.getParams(HKD_MARK_HK$);
            rate=params4.getParamValue();
        }*/
        if (USD_MARK_$.contains(moneyMark)) {
            Params params1 = paramsMapper.getParams(USD_EXCHANGE_CNY);
            BigDecimal decimal = new BigDecimal(params1.getParamValue()).multiply(new BigDecimal(price)).setScale(6, BigDecimal.ROUND_HALF_UP);
            rate = decimal.toString();
        } else if (CNY_MARK_¥.contains(moneyMark)) {
            rate = price;
        } else if (KRW_MARK_₩.contains(moneyMark)) {
            Params params1 = paramsMapper.getParams(KRW_EXCHANGE_CNY);
            BigDecimal decimal = new BigDecimal(params1.getParamValue()).multiply(new BigDecimal(price)).setScale(6, BigDecimal.ROUND_HALF_UP);
            rate = decimal.toString();
        } else if (HKD_MARK_HK$.contains(moneyMark)) {
            Params params1 = paramsMapper.getParams(HKD_EXCHANGE_CNY);
            BigDecimal decimal = new BigDecimal(params1.getParamValue()).multiply(new BigDecimal(price)).setScale(6, BigDecimal.ROUND_HALF_UP);
            rate = decimal.toString();
        } else {
            throw new RRException(RErrorEnum.MONEY_MARK_ERROR);
        }
        return rate;
    }

}
