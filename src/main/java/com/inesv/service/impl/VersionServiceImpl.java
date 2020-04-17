package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.VersionMapper;
import com.inesv.service.VersionService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import com.inesv.util.ValidataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VersionServiceImpl implements VersionService {

    @Autowired
    private VersionMapper versionMapper;

    @Override
    public BaseResponse getVersionInfo(String data) {
        JSONObject json = JSONObject.parseObject(data);
        if(ValidataUtil.isNull(json.getString("type"))) return RspUtil.rspError("类型不能为空");
        return RspUtil.success(versionMapper.getVersionInfo(Integer.valueOf(json.getString("type"))));
    }
}
