package com.inesv.controller;

import com.alibaba.fastjson.JSONObject;
import com.inesv.model.IDCard;
import com.inesv.service.IDCardService;
import com.inesv.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
public class IDCardController {

    private static final Logger log = LoggerFactory
            .getLogger(IDCardController.class);
    @Autowired
    IDCardService idCardService;

    @PostMapping("/uploadIDCard")
    public BaseResponse uploadIDCard(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile[] file,
            @RequestParam("data") String data) {
        log.info("上传身份证信息" + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("上传身份证信息接口参数:" + data);
        if (ValidataUtil.isNull(data) || file == null)
            return RspUtil.rspError("参数不能为空");
        try {
            JSONObject dataJson = JSONObject.parseObject(data);
            String token = dataJson.getString("token");
            String name = dataJson.getString("name");
            String idCardNumber = dataJson.getString("idCardNumber");
            ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson.getString("language"));
            if (ValidataUtil.isNull(token)){
                return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
            }
            if(!ValidataUtil.isChineseName(name)){
                return RspUtil.rspError(responseParamsDto.CHINESE_NAME_WRONG);
            }
            if(!ValidataUtil.isIDNumber(idCardNumber)){
                return RspUtil.rspError(responseParamsDto.ID_CARD_NUMBER_WRONG);
            }
            if(file.length != 3){
                return RspUtil.rspError(responseParamsDto.FAIL);
            }

            try {
                idCardService.uploadIDCard(name, idCardNumber, file, responseParamsDto, token);
            }catch (RuntimeException e){
                return RspUtil.rspError(e.getMessage());
            }

            return RspUtil.successMsg(responseParamsDto.UPLOAD_SUCCESSD);
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error();
        }
    }

    @PostMapping("/getIDCard")
    public BaseResponse getIDCard(
            HttpServletRequest request,
            @RequestParam("data") String data) {
        log.info("获取身份证信息" + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取身份证信息接口参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            JSONObject dataJson = JSONObject.parseObject(data);
            String token = dataJson.getString("token");
            ResponseParamsDto responseParamsDto = LanguageUtil.proving(dataJson.getString("language"));
            if (ValidataUtil.isNull(token))
                return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

            try {
                IDCard idCard = idCardService.getIDCard(token, responseParamsDto);
                return RspUtil.success(JSONFormat.getStr(idCard));
            }catch (RuntimeException e){
                return RspUtil.rspError(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error();
        }
    }
}
