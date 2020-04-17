package com.inesv.controller;

import com.inesv.annotation.UnLogin;
import com.inesv.service.VersionService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    private static final Logger log = LoggerFactory.getLogger(VersionController.class);

    @Autowired
    private VersionService versionService;

    /**
     * 获取版本信息
     * @return
     */
    @GetMapping("/getVersionInfo")
    @UnLogin
    public BaseResponse getVersionInfo(@RequestParam("data") String data){
        log.info("获取版本信息"+ Thread.currentThread().getStackTrace()[1].getMethodName());
        try{
            return versionService.getVersionInfo(data);
        }catch (Exception e){
            return RspUtil.error();
        }
    }
}
