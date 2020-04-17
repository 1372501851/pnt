package com.inesv.controller;

import com.alibaba.fastjson.JSONObject;
import com.inesv.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inesv.service.UserWalletService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class UserWalletController {

    @Autowired
    private UserWalletService userWaletService;

    ConcurrentMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory
            .getLogger(UserWalletController.class);

    static Lock getBalanceLock = new ReentrantLock();// 锁对象

    /**
     * 获取用户钱包资产
     *
     * @param data
     * @return
     */
    @PostMapping(value = "/getBalance")
    public BaseResponse getBalance(@RequestParam("data") String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        String token = jsonObject.getString("token");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(jsonObject
                .getString("language"));

        getBalanceLock.lock();

        try {
            BaseResponse baseResponse;
            if (concurrentHashMap.containsKey(token)) {
                //防止访问过快，
                return RspUtil.success(RspUtil.QUICK_ACCESS_FAIL, responseParamsDto.QUICK_ACCESS_FAIL, "");
            } else {
                concurrentHashMap.put(token, token);
                baseResponse = userWaletService.getWalletBalance(data);
                concurrentHashMap.remove(token);
            }
            return baseResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error();
        } finally {
            getBalanceLock.unlock();
        }
    }

    /**
     * 新增用户钱包
     *
     * @param data
     * @return
     */
    @PostMapping("/addWallet")
    public BaseResponse addWallet(@RequestParam("data") String data) {
        log.info("addWallet-参数：" + data);
        return userWaletService.addWallet(data);
    }

    @PostMapping(value = "/getWalletStates")
    public BaseResponse getStates(@RequestParam("data") String data) {
        return userWaletService.getStates(data);
    }

    @PostMapping(value = "/editWalletState")
    public BaseResponse editState(@RequestParam("data") String data) {
        return userWaletService.editState(data);
    }

    // 代币详情
    @PostMapping(value = "/getCoinDetail")
    public BaseResponse getCoinDetail(@RequestParam("data") String data) {
        return userWaletService.getCoinDetail(data);
    }

    @PostMapping("/getCoinWallet")
    public BaseResponse getCoinWallet(@RequestParam("data") String data) {
        log.info("获取货币与钱包接口"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取货币与钱包接口参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return userWaletService.getCoinWallet(data);
        } catch (Exception e) {
            return RspUtil.error();
        }
    }

    /*
     * //添加资产
     *
     * @PostMapping("/addCoinWallet") public BaseResponse
     * addCoinWallet(@RequestParam("data") String data){ return
     * userWaletService.openWallet(data); }
     *
     * //资产列表
     *
     * @GetMapping("/assetsList") public BaseResponse
     * assetsList(@RequestParam("data") String data){ return
     * userWaletService.assetsList(data); }
     */
}
