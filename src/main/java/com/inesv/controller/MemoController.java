package com.inesv.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.inesv.annotation.UnLogin;
import com.inesv.model.Memo;
import com.inesv.service.MemoService;
import com.inesv.util.BaseResponse;

@RestController
public class MemoController {

	private static final Logger log = LoggerFactory
			.getLogger(UserController.class);
	@Autowired
	MemoService memoService;

	// 获取所有关键字
	@UnLogin
	@RequestMapping(value = "memo/getMemos")
	public BaseResponse<List<Memo>> getMemos(String data) {
		log.info("获取关键字"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("获取关键字参数:" + data);
		BaseResponse<List<Memo>> response = memoService.getAllMemos(data);
		return response;
	}

	// @UnLogin
	// @RequestMapping(value = "memo/setMemos", method = RequestMethod.POST)
	// public BaseResponse<Memo> setMemo(String data) {
	// log.info("======获取关键字入参=====");
	// log.info(data);
	// BaseResponse<Memo> response = memoService.setMemo(data);
	// log.info("======获取关键字出参=====");
	// log.info(JSONObject.toJSONString(response));
	// return response;
	// }
	//
	// @UnLogin
	// @RequestMapping(value = "memo/delMemo", method = RequestMethod.POST)
	// public BaseResponse<Memo> delMemo(String data) {
	// log.info("======获取关键字入参=====");
	// log.info(data);
	// BaseResponse<Memo> response = memoService.delMemo(data);
	// log.info("======获取关键字出参=====");
	// log.info(JSONObject.toJSONString(response));
	// return response;
	// }

	// 关键字绑定钱包地址
}
