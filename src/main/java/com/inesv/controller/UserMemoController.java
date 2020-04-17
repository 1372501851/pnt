package com.inesv.controller;

import com.alibaba.fastjson.JSONObject;
import com.inesv.annotation.UnLogin;
import com.inesv.mapper.ParamsMapper;
import com.inesv.mapper.UserMapper;
import com.inesv.mapper.UserMemoMapper;
import com.inesv.model.Params;
import com.inesv.model.User;
import com.inesv.model.UserMemo;
import com.inesv.service.UserMemoService;
import com.inesv.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserMemoController {

    private Logger log = LoggerFactory.getLogger(UserMemoController.class);

    @Autowired
    private UserMemoMapper userMemoMapper;


    @Autowired
    private UserMemoService userMemoService;

    @PostMapping("/addMemo")
    public BaseResponse addMemo(@RequestParam("data") String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String memo = json.getString("memo");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
                .getString("language"));
        //在userMemo表里进行查找有无重复助记词，重复则重试
        UserMemo userMemo1 = new UserMemo();
        userMemo1.setUserMemo(memo);
        userMemo1 = userMemoMapper.getUserMemoByCondition(userMemo1);
        if (userMemo1 != null) {
            return RspUtil.rspError(responseParamsDto.EXPORT_MEMO_REPEAT);
        }

        log.info("添加助记词"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("添加助记词参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return userMemoService.addMemo(data);
        } catch (Exception e) {
            log.error("异常！", e);
            return RspUtil.error();
        }
    }

    @PostMapping("/backupMemo")
    public BaseResponse backupMemo(@RequestParam("data") String data) {
        log.info("备份助记词"
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        log.info("备份助记词参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return userMemoService.backupMemo(data);
        } catch (Exception e) {
            log.error("异常！", e);
            return RspUtil.error();
        }
    }

    @PostMapping("/enCodeMemo")
    @UnLogin
    public BaseResponse enCodeMemo(@RequestParam("data") String data) throws Exception {
        log.info("加密助记词参数:" + data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        JSONObject json = JSONObject.parseObject(data);
        String memo = json.getString("memo");
        //将传来的明文助记词加密
        memo = DESUtil.memoEncode(memo);
        Map map = new HashMap();
        map.put("enCodeMemo", memo);
        return RspUtil.success(map);
    }

    @RequestMapping("/getMemo")
    @UnLogin
    public BaseResponse getMemo(@RequestParam("data") String data) throws Exception {
        log.info("获取助记词并检测用户和推荐人是否存在参数:"+ data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return userMemoService.getMemo(data);
        } catch (Exception e) {
            log.error("异常！", e);
            return RspUtil.error();
        }
    }

    @RequestMapping("/getLoginMemo")
    @UnLogin
    public BaseResponse getLoginMemo(@RequestParam("data") String data) throws Exception {
        log.info("根据用户名获取登录助记词(打乱):"+ data);
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        if (ValidataUtil.isNull(data))
            return RspUtil.rspError("参数不能为空");
        try {
            return userMemoService.getLoginMemo(data);
        } catch (Exception e) {
            log.error("异常！", e);
            return RspUtil.error();
        }
    }


}
