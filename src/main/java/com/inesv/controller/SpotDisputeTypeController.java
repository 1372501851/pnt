package com.inesv.controller;

import com.inesv.annotation.UnLogin;
import com.inesv.service.SpotDisputeTypeService;
import com.inesv.util.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spotDisputeType")
public class SpotDisputeTypeController {

    private static Logger logger = LoggerFactory.getLogger(SpotDisputeTypeController.class);

    @Autowired
    private SpotDisputeTypeService spotDisputeTypeService;


    @UnLogin
    @GetMapping("/type")
    public BaseResponse spotDisputeType(){
        logger.info("纠纷类型接口"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        return spotDisputeTypeService.disputeTypeList();
    }
}
