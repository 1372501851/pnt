package com.inesv.service;

import com.inesv.util.BaseResponse;

public interface UserMemoService {

    BaseResponse getLoginMemo(String data) throws Exception;

    BaseResponse getMemo(String data) throws Exception;

    BaseResponse addMemo(String data) throws Exception;

    BaseResponse backupMemo(String data) throws Exception;
}
