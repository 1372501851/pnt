package com.inesv.service.impl;

import com.inesv.mapper.ParamsMapper;
import com.inesv.model.Params;
import com.inesv.service.ParamsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class ParamsServiceImpl implements ParamsService {

    @Resource
    private ParamsMapper paramsMapper;

    @Override
    public Params queryByKey(String paramsKey) {
        return paramsMapper.queryByKey(paramsKey);
    }
}
