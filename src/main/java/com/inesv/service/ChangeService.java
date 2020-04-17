package com.inesv.service;

import com.inesv.util.BaseResponse;

public interface ChangeService {
    BaseResponse change(String data);

    BaseResponse changeNum(String data);

    BaseResponse changeListByUser(String data);

    BaseResponse queryDetail(String data);
}
