package com.inesv.service.impl;

import com.inesv.mapper.NodeLevelMapper;
import com.inesv.service.NodeLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NodeLevelServiceImpl implements NodeLevelService {

    @Autowired
    private NodeLevelMapper nodeLevelMapper;

    @Override
    public BigDecimal getNodeBuyMin() {
        return nodeLevelMapper.getNodeBuyMin();
    }
}
