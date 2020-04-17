package com.inesv.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonView;
import com.inesv.annotation.UnLogin;
import com.inesv.model.Coin;
import com.inesv.service.CoinService;
import com.inesv.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CoinController {

    private static final Logger log = LoggerFactory.getLogger(CoinController.class);

    @Autowired
    private CoinService coinService;

    /**
     * 获取货币
     * @param data
     * @return
     */
    @GetMapping("/getCoin")
    @UnLogin
    public BaseResponse getCoin(@RequestParam("data") String data){
        log.info("获取货币"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取货币参数:"+data);
        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
        try{
            return coinService.getCoin(data);
        }catch (Exception e){
            return RspUtil.error();
        }
    }

     /* 获取指定货币
     * @param data
     * @return
     */
    @GetMapping("/getCoinByNo")
    @UnLogin
    public BaseResponse getCoinByNo(@RequestParam("data") String data){
        log.info("获取货币"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取货币参数:"+data);
        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
        try{
            return coinService.getCoinByNo(data);
        }catch (Exception e){
            e.printStackTrace();
            return RspUtil.error();
        }
    }
     /* 获取常规转换货币列表
      * @param data
      * @return
      */
    @GetMapping("/queryRoutineList")
    @UnLogin
    public BaseResponse queryRoutineList(@RequestParam("data") String data){
            log.info("获取货币"+ Thread.currentThread().getStackTrace()[1].getMethodName());
            log.info("获取货币参数:"+data);
            if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
            try{
                return coinService.queryRoutineList(data);
            }catch (Exception e){
                e.printStackTrace();
                return RspUtil.error();
            }
        }

    /**
     * 获取光子专区转换货币列表
     * @param data
     * @return
     */
    @GetMapping("/queryPntList")
    @UnLogin
    public BaseResponse queryPtnList(@RequestParam("data") String data){
        log.info("获取货币"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取货币参数:"+data);
        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
        try{
            return coinService.queryOpenPntCoin(data);
        }catch (Exception e){
            e.printStackTrace();
            return RspUtil.error();
        }
    }
    
    
    /**
     * 获取代币K线图数据
     * @param data
     * @return
     */
//    @GetMapping("/CoinKchartsData")
    @RequestMapping("CoinKchartsData")
//    @ResponseBody
    @UnLogin
    public BaseResponse CoinKchartsData(@RequestParam("data") String data){
       
    	 return coinService.coinKchartsData(data);
    }

    /**
     * 获取单个币种的行情数据
     * **/
    @GetMapping("market")
    @UnLogin
    public BaseResponse market(String data){
        log.info("获取单个币种的行情数据"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取货币参数:"+data);
        return coinService.coinMarket(data);
    }

    /**
     * 获取单个币种的K线
     * **/
    @GetMapping("kline")
    @UnLogin
    public BaseResponse Kline(String data){
        log.info("获取单个币种的K线数据"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取货币参数:"+data);
        return coinService.Kline(data);
    }

    /**
     * 获取行情列表
     * **/
    @GetMapping("openCoinList")
    @UnLogin
    public BaseResponse openCoinList(String data){
        log.info("获取行情列表"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取货币参数:"+data);
        return coinService.queryOpenCoinList(data);
    }


    @GetMapping("allCoinList")
    @UnLogin
    public BaseResponse allCoinList(String data){
        log.info("获取行情列表"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取行情列表参数:"+data);

        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
        try{
            return coinService.queryAllCoinList(data);
        }catch (Exception e){
            log.error("异常！", e);
            return RspUtil.error();
        }
    }
}
