package com.inesv.controller;

import com.inesv.service.OpinionService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import com.inesv.util.ValidataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpinionController {

    private static final Logger log = LoggerFactory.getLogger(OpinionController.class);

    @Autowired
    private OpinionService opinionService;

    /**
     * 新增反馈意见
     * @return
     */
    @PostMapping("/addOpinion")
    public BaseResponse addOpinion(@RequestParam("data") String data){
        log.info("新增反馈意见"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("新增反馈意见参数:"+data);
        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
        try{
            return opinionService.addOpinion(data);
        }catch (Exception e){
            return RspUtil.error();
        }
    }

}
