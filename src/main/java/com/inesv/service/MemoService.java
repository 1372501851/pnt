package com.inesv.service;

import java.util.List;

import com.inesv.model.Memo;
import com.inesv.util.BaseResponse;

public interface MemoService {
	BaseResponse<List<Memo>> getAllMemos(String data);

	// BaseResponse<Memo> setMemo(String data);
	//
	// BaseResponse<Memo> delMemo(String data);
}
