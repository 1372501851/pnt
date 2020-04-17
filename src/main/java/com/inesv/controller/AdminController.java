package com.inesv.controller;

import com.alibaba.fastjson.JSONObject;
import com.inesv.annotation.UnLogin;
import com.inesv.service.SpotDisputeService;
import com.inesv.service.SpotEntrustService;
import com.inesv.service.TradeInfoService;
import com.inesv.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class AdminController {

    private static Logger log = LoggerFactory.getLogger(AdminController.class);
    /*管理员同意*/
    private static Lock agreeDisputeLock = new ReentrantLock();	// 锁对象

    @Autowired
    private SpotDisputeService spotDisputeService;

    @Autowired
    private SpotEntrustService spotEntrustService;

    @Autowired
    private TradeInfoService tradeInfoService;

    @PostMapping("/agreeDisputeByAdmin")
    @UnLogin
    public BaseResponse agreeDisputeByAdmin(String data){
        log.info("管理人员同意纠纷接口"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("管理人员同意纠纷接口参数："+ data);

        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");

        JSONObject json = JSONObject.parseObject(data);
        String idStr = json.getString("disputeId");
        idStr = idStr.replaceAll(" ", "+");

        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));

        if (ValidataUtil.checkParamIfEmpty(idStr))
            return RspUtil.rspError(responseParamsDto.PARAMS_NULL_DESC);

        try {
            Long id = Long.valueOf(DESUtil.decode(idStr));

            agreeDisputeLock.lock();

            BaseResponse baseResponse = spotDisputeService.agreeDispute(id ,responseParamsDto);

            log.info("管理人员 - 同意纠纷参数：{}，执行纠纷结果：{}", data, GsonUtils.toJson(baseResponse));

            return baseResponse;
        } catch (Exception e) {
            log.error("异常！", e);
            return RspUtil.error();
        } finally {
            agreeDisputeLock.unlock();
        }
    }


}
