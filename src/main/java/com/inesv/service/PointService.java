package com.inesv.service;

import com.inesv.model.PayPointRecord;
import com.inesv.model.User;
import com.inesv.util.BaseResponse;

import java.util.List;
import java.util.Map;

public interface PointService {
    Map<String,String> createOrder(User user, String point);

    Map<String,Object> refresh(User user, String orderNo, String payCoin);

    void pay(Long userId, String orderNo);

    BaseResponse payRecord(Long id, Integer pageNum, Integer pageSize,Integer type);

    Map<String,String> balance(Long id, String lang);
}
