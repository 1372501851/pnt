package com.inesv.service;

import com.inesv.model.PicVerifyCodeRecord;
import com.inesv.util.BaseResponse;
import com.inesv.util.ResponseParamsDto;

public interface PicVerifyCodeRecordService {
	/**
	 * 查询是否超过限制
	 * @param phone
	 * @param ip
	 * @return
	 * @throws Exception
     */
	BaseResponse checkIfIllegal(String phone, String ip, ResponseParamsDto responseParamsDto) throws Exception;

	PicVerifyCodeRecord getValidVerifyCode4LastOne(PicVerifyCodeRecord record);

	Integer update(PicVerifyCodeRecord codeRecord);
}
