package com.inesv.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.MemoMapper;
import com.inesv.model.Memo;
import com.inesv.service.MemoService;
import com.inesv.util.BaseResponse;
import com.inesv.util.LanguageUtil;
import com.inesv.util.ResponseParamsDto;
import com.inesv.util.ValidataUtil;

@Service
@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
public class MemoServiceImpl implements MemoService {
	@Autowired
	MemoMapper memoMapper;

	public BaseResponse<List<Memo>> getAllMemos(String data) {
		BaseResponse<List<Memo>> response = new BaseResponse<>();
		List<Memo> memos = new ArrayList<Memo>();
		JSONObject json = JSONObject.parseObject(data);
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
				.getString("language"));
		try {
			memos = memoMapper.getAllMemo();
			// 200成功100失败
			response.setCode(200);
			response.setMessage(responseParamsDto.GET_MEMO_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			// 200成功100失败
			response.setCode(100);
			response.setMessage(responseParamsDto.GET_MEMO_FAIL);
		}
		response.setData(memos);
		return response;
	}

	// @Override
	// public BaseResponse<Memo> setMemo(String data) {
	// JSONObject jsonData = JSONObject.parseObject(data);
	// String memo = jsonData.getString("memo");
	// BaseResponse<Memo> response = new BaseResponse<>();
	// try {
	// if (!ValidataUtil.isNull(memo)) {
	// Memo findMemo = memoMapper.findByMemo(memo);
	// if (findMemo == null || findMemo.getId() == 0l) {
	// memoMapper.addMemo(memo);
	// findMemo = memoMapper.findByMemo(memo);
	// response.setMessage("添加成功");
	// response.setCode(200);
	// response.setData(findMemo);
	// } else {
	// response.setMessage("该关键字已存在，无需重复添加");
	// response.setData(findMemo);
	// // 200成功100失败
	// response.setCode(100);
	// }
	// } else {
	// response.setMessage("关键字不能为空");
	// // 200成功100失败
	// response.setCode(100);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// response.setMessage("添加失败");
	// // 200成功100失败
	// response.setCode(100);
	// }
	// return response;
	// }
	//
	// @Override
	// public BaseResponse<Memo> delMemo(String data) {
	// JSONObject jsonData = JSONObject.parseObject(data);
	// String id = jsonData.getString("id");
	// BaseResponse<Memo> response = new BaseResponse<>();
	// try {
	// memoMapper.delMemo(id);
	// response.setCode(200);
	// response.setMessage("删除成功");
	// } catch (Exception e) {
	// e.printStackTrace();
	// response.setMessage("删除失败");
	// // 200成功100失败
	// response.setCode(100);
	// }
	// return response;
	// }
}
