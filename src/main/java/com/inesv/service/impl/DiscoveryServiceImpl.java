package com.inesv.service.impl;

import com.inesv.mapper.DiscoveryMapper;
import com.inesv.model.Discovery;
import com.inesv.service.DiscoveryService;
import com.inesv.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    @Autowired
    private DiscoveryMapper discoveryMapper;


    @Override
    public BaseResponse discoveryList() {
        List<Discovery> list=discoveryMapper.discoveryList();
        return RspUtil.success(list);
    }

    @Override
    public BaseResponse queryDiscoveryByCondition(Map<String, String> requestMap) {
        String moduleName = requestMap.get("moduleName");
        ResponseParamsDto responseParamsDto = LanguageUtil.proving(requestMap.get("language"));
        if(StringUtils.isBlank(moduleName)) {
            return RspUtil.rspError(responseParamsDto.PRIMARY_PARAMS_NOT_NULL);
        }
        Discovery discovery=new Discovery();
        discovery.setModuleName(moduleName);
        discovery=discoveryMapper.queryDiscoveryByCondition(discovery);
        return RspUtil.success(JSONFormat.getStr(discovery));
    }
}
