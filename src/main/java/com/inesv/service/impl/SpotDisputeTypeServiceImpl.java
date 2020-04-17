package com.inesv.service.impl;

import com.inesv.mapper.SpotDisputeTypeMapper;
import com.inesv.model.SpotDisputeType;
import com.inesv.service.SpotDisputeTypeService;
import com.inesv.util.BaseResponse;
import com.inesv.util.RspUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RLY
 */
@Service
public class SpotDisputeTypeServiceImpl implements SpotDisputeTypeService {

    @Autowired
    SpotDisputeTypeMapper spotDisputeTypeMapper;

    @Override
    public BaseResponse disputeTypeList() {
        List<SpotDisputeType> spotDisputeTypes=spotDisputeTypeMapper.disputeTypeList();
        return RspUtil.success(spotDisputeTypes);
    }

}
