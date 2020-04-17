package com.inesv.service;

import com.inesv.util.BaseResponse;

import java.util.Map;

public interface DiscoveryService {

    BaseResponse discoveryList();

    BaseResponse queryDiscoveryByCondition(Map<String, String> requestMap);
}
