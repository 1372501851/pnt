package com.inesv.controller;

import com.inesv.annotation.UnLogin;
import com.inesv.service.BankCardService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import com.inesv.util.ValidataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class BankCardController {

    private static final Logger log = LoggerFactory.getLogger(BankCardController.class);

    @Resource
    private BankCardService bankCardService;

    /**
     * 绑定银行卡
     * @param data
     * @return
     */
    @PostMapping("/addBankCard")
    public BaseResponse addBankCard(String data) {
        log.info("绑定银行卡接口"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("绑定银行卡接口参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return bankCardService.addBankCard(data);
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error();
        }
    }

    /**
     * 获取用户银行卡信息
     * @param data
     * @return
     */
    @PostMapping("/getBankCard")
    public BaseResponse getBankCard(String data) {
        log.info("获取银行卡接口"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取银行卡接口参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return bankCardService.getBankCard(data);
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error();
        }
    }

    /**
     * 获取银行卡详情接口
     * @param data
     * @return
     */
    @PostMapping("/getBankCardByID")
    @UnLogin
    public BaseResponse getBankCardByID(String data) {
        log.info("获取银行卡详情接口"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("获取银行卡详情接口参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return bankCardService.getBankCardById(data);
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error();
        }
    }

    /**
     * 修改银行卡信息接口
     * @param data
     * @return
     */
    @PostMapping("/editBankCard")
    public BaseResponse editBankCard(String data) {
        log.info("修改银行卡接口"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("修改银行卡接口参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return bankCardService.editBankCardById(data);
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error();
        }
    }

    /**
     * 删除银行卡信息接口
     * @param data
     * @return
     */
    @PostMapping("/deleteBankCard")
    public BaseResponse deleteBankCard(String data) {
        log.info("删除银行卡接口"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("删除银行卡接口参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return bankCardService.deleteBankCardById(data);
        } catch (Exception e) {
            e.printStackTrace();
            return RspUtil.error();
        }
    }
}
