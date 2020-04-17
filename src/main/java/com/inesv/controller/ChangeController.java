package com.inesv.controller;

import com.inesv.annotation.UnLogin;
import com.inesv.service.ChangeService;
import com.inesv.service.CoinService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import com.inesv.util.ValidataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/change")
public class ChangeController {
    @Autowired
    private ChangeService changeService;

    @Autowired
    private CoinService coinService;

    private static final Logger log = LoggerFactory.getLogger(ChangeController.class);

    @PostMapping("/changeCoin")
    public BaseResponse changeCoin(String data){
        log.info("======获取关键字入参======");
        log.info("参数："+data);
        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
        return changeService.change(data);
    }

    @PostMapping("/changeNum")
    @UnLogin
    public BaseResponse changeNum(String data){
        log.info("======获取关键字入参======");
        log.info("参数："+data);
        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
        return changeService.changeNum(data);
    }

    @GetMapping("/changeList")
    @UnLogin
    public BaseResponse changeList(String data){
        log.info("======获取关键字入参======");
        log.info("参数："+data);
        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
        return changeService.changeListByUser(data);
    }

    @GetMapping("/detail")
    @UnLogin
    public BaseResponse detail(String data){
        log.info("======获取关键字入参======");
        log.info("参数："+data);
        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
        return changeService.queryDetail(data);
    }

}
