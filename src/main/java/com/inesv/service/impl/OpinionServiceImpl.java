package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.OpinionMapper;
import com.inesv.mapper.UserMapper;
import com.inesv.model.Opinion;
import com.inesv.model.User;
import com.inesv.service.OpinionService;
import com.inesv.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpinionServiceImpl implements OpinionService{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OpinionMapper opinionMapper;

    /**
     * 新增意见反馈
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public BaseResponse addOpinion(String data) throws Exception {
        JSONObject json = JSONObject.parseObject(data);
        String token = json.getString("token");
        String remark = json.getString("remark");
        String contact = json.getString("contact");
        String type = json.getString("type");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
        if (ValidataUtil.isNull(token)) return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
        if (ValidataUtil.isNull(remark)) return RspUtil.rspError(responseParamsDto.REMARK_NULL_DESC);
        if (ValidataUtil.isNull(contact)) return RspUtil.rspError(responseParamsDto.CONTACT_NULL_DESC);
        if (ValidataUtil.isNull(type)) return RspUtil.rspError(responseParamsDto.TYPE_NULL_DESC);

        User user = userMapper.getUserInfoByToken(token);
        if (user == null) return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

        Opinion opinion = new Opinion();
        opinion.setUserNo(Long.valueOf(user.getId()));
        opinion.setContact(contact);
        opinion.setRemark(remark);
        opinion.setType(Integer.valueOf(type));
        opinionMapper.insertOpinion(opinion);

        return RspUtil.success();
    }
}
