package com.inesv.controller;

import com.inesv.annotation.UnLogin;
import com.inesv.service.TradeInfoService;
import com.inesv.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TradeInfoController {

	private static final Logger log = LoggerFactory
			.getLogger(TradeInfoController.class);

	@Autowired
	private TradeInfoService tradeInfoService;

	@PostMapping("/addTradeInfo")
	@UnLogin
	public BaseResponse addTradeInfo(@RequestParam("data") String data) {
		log.info("获取转账参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			BaseResponse baseResponse = tradeInfoService.addTradeInfo(data);
			log.info("转账参数：{}，执行转账结果：{}", data, GsonUtils.toJson(baseResponse));
			return baseResponse;
		} catch (Exception e) {
			log.error("执行转账失败", e);
			return RspUtil.error();
		}
	}

	@GetMapping("/getTradeInfo")
	public BaseResponse getTradeInfo(@RequestParam("data") String data) {
		log.info("获取交易记录"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("获取交易记录接口参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return tradeInfoService.getTradeInfo(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	@PostMapping("/getTradeInfoByNo")
	@UnLogin
	public BaseResponse getTradeInfoByNo(@RequestParam("data") String data) {
		log.info("获取交易记录"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("获取交易记录接口参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return tradeInfoService.getTradeInfoByNo(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	@GetMapping("/getTradeFriends")
	public BaseResponse getTradeFriends(@RequestParam("data") String data) {
		log.info("获取交易好友"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("获取交易好友接口参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return tradeInfoService.getTradeFriends(data);
		} catch (Exception e) {
			e.printStackTrace();
			return RspUtil.error();
		}
	}
}
