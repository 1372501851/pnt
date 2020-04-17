package com.inesv.controller;

import com.inesv.service.BonusService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import com.inesv.util.ValidataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class BonusController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource
    private BonusService bonusService;

    /**
     * 分润记录
     * @param data
     * @return
     */
    @PostMapping("/getBonusDetailByUserNo")
    public BaseResponse getBonusDetailByUserNo(String data) {
        log.info("分润记录接口"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("分润记录接口参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return bonusService.getBonusDetailByUserNo(data);
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error();
        }
    }
}
