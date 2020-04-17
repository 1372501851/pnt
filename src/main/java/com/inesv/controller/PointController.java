package com.inesv.controller;

import com.inesv.annotation.Login;
import com.inesv.annotation.LoginUser;
import com.inesv.annotation.UnLogin;
import com.inesv.common.constant.RErrorEnum;
import com.inesv.common.exception.RRException;
import com.inesv.mapper.ParamsMapper;
import com.inesv.model.Params;
import com.inesv.model.PayPointRecord;
import com.inesv.model.User;
import com.inesv.service.PayService;
import com.inesv.service.PointService;
import com.inesv.service.UserService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
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

import static com.inesv.entity.CoinParams.USD_EXCHANGE_CNY;

@Slf4j
@RestController
@RequestMapping("/point")
public class PointController {

    @Autowired
    private PointService pointService;

    @Autowired
    private ParamsMapper paramsMapper;

    @Autowired
    private PayService payService;

    @Autowired
    private UserService userService;


    //积分与法币的转换
    @RequestMapping("/convert")
    @UnLogin
    @Login
    public BaseResponse getRegisterEncode(@RequestHeader("lang") String lang, @RequestParam("point") String point) {
        Map<String,String> map=new HashMap<>();
        if ("zh_CN".equals(lang)) {
            BigDecimal amount = new BigDecimal(point).divide(BigDecimal.valueOf(10)).setScale(6, BigDecimal.ROUND_HALF_UP);
            map.put("amount",amount.toString());
        }else {
            Params params = paramsMapper.getParams(USD_EXCHANGE_CNY);
            BigDecimal amountCNY = new BigDecimal(point).divide(BigDecimal.valueOf(10)).setScale(6, BigDecimal.ROUND_HALF_UP);
            BigDecimal amount = new BigDecimal(params.getParamValue()).multiply(amountCNY).setScale(6, BigDecimal.ROUND_HALF_UP);
            map.put("amount",amount.toString());
        }
        map.put("point",point);
        return RspUtil.success(map);
    }


    //购买积分生成订单接口
    @RequestMapping("/createOrder")
    @UnLogin
    @Login
    public BaseResponse createOrder(@LoginUser User user, @RequestParam("point") String point) {
        Map<String,String> map=pointService.createOrder(user,point);
        return RspUtil.success(map);
    }


    //购买积分币种刷新接口
    @RequestMapping("/refresh")
    @UnLogin
    @Login
    public BaseResponse refresh(@LoginUser User user, @RequestParam("orderNo") String orderNo,@RequestParam("payCoin") String payCoin) {
        Map<String,Object> map=pointService.refresh(user,orderNo,payCoin);
        return RspUtil.success(map);
    }

    //获取用户拥有的币种
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
        }
        return RspUtil.success(listM);
    }

    //购买积分支付
    @RequestMapping("/pay")
    @Login
    @UnLogin
    public BaseResponse pay(@LoginUser User user,@RequestBody Map<String, String> map) {
        if (!MapUtils.isNotEmpty(map)) {
            throw new RRException(RErrorEnum.PARAMETER_EMPTY);
        }
        String orderNo = map.get("orderNo");
        String password = map.get("password");
        Long userId = user.getId();
        boolean isPassword = userService.isTradePaswd(userId, password);
        if (!isPassword) {
            throw new RRException(RErrorEnum.TRADEPASSWD_ERROR);
        }
        pointService.pay(userId,orderNo);
        return RspUtil.success();
    }

    //零钱包充值、收款、付款、mocPay付款、mocPay收款明细
    @RequestMapping("/record")
    @Login
    @UnLogin
    public BaseResponse payRecord(@LoginUser User user,@RequestParam(required = false, defaultValue = "1") Integer pageNum, @RequestParam(required = false, defaultValue = "10") Integer pageSize,@RequestParam("type") Integer type) {
        BaseResponse baseResponse=pointService.payRecord(user.getId(),pageNum,pageSize,type);
        return baseResponse;
    }


    //显示余额
    @RequestMapping("/balance")
    @Login
    @UnLogin
    public BaseResponse balance(@LoginUser User user,@RequestHeader("lang") String lang) {
        Map<String,String> map=pointService.balance(user.getId(),lang);
        return RspUtil.success(map);
    }



    //付款接口



    //收款接口




}
