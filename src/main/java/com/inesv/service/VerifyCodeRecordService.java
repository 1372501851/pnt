package com.inesv.service;

import com.inesv.util.BaseResponse;
import com.inesv.util.ResponseParamsDto;

public interface VerifyCodeRecordService {
	/**
	 * 查询是否超过限制
	 * @param phone
	 * @param ip
	 * @return
	 * @throws Exception
     */
	BaseResponse checkIfIllegal(String phone, String ip, ResponseParamsDto responseParamsDto) throws Exception;
}
