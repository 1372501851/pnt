package com.inesv.controller;


import com.alibaba.fastjson.JSONObject;
import com.inesv.annotation.UnLogin;
import com.inesv.service.DiscoveryService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RLY
 */
@RestController
@Slf4j
public class DiscoveryController {


    @Autowired
    DiscoveryService discoveryService;


    /**
     * 获取发现列表
     * @return
     */
    @GetMapping("/getDiscoveryList")
    @UnLogin
    public BaseResponse getDiscoveryList(){
        log.info("获取发现列表"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        try{
            return discoveryService.discoveryList();
        }catch (Exception e){
            return RspUtil.error();
        }
    }


    /**
     * 获取具体模块信息
     * @return
     */
    @GetMapping("/getDiscoveryInfo")
    @UnLogin
    public BaseResponse getDiscoveryInfo(@RequestBody Map<String, String> requestMap){
        log.info("获取具体模块信息"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取具体模块信息参数:" + requestMap);
        try{
            return discoveryService.queryDiscoveryByCondition(requestMap);
        }catch (Exception e){
            return RspUtil.error();
        }
    }


}
