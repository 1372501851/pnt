package com.inesv.controller;

import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.ParamsMapper;
import com.inesv.mapper.UserMapper;
import com.inesv.model.Params;
import com.inesv.model.User;
import com.inesv.service.SpotDealDetailService;
import com.inesv.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class SpotTradeDetailController {

	private static Logger log = LoggerFactory.getLogger(SpotTradeDetailController.class);

	@Autowired
	private SpotDealDetailService spotDealDetailService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private ParamsMapper paramsMapper;

	private static String CONFIRM_RECEIVE = "confirm_Receive";
	private static String CANCEL_DEALDETAIL = "cancel_DealDetail";

	/**
	 * 用户确认收款
	 */
	@ResponseBody
	@RequestMapping(value = "/confirmReceive", method = RequestMethod.POST)
	public BaseResponse<Map<String, Object>> confirmReceive(String data, HttpSession session) throws ParseException {
		log.info(" confirmReceive|卖方确认收款| " + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info(" confirmReceive|卖方确认收款|data:" + data);
		if (ValidataUtil.isNull(data)){
			return RspUtil.rspError("参数不能为空");
		}
		JSONObject json = JSONObject.parseObject(data);
		String token = json.getString("token");
		String language = json.getString("language");
		String orderNo = json.getString("orderNo");
		String tradePassword = json.getString("tradePassword");
		boolean flag = ValidataUtil.checkParamIfEmpty(token, language, orderNo, tradePassword);
		if(flag){
			return RspUtil.rspError("必填参数不能为空");
		}

		//选择语言
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(language);


		/**交易时间段限制**/
		Params params = paramsMapper.getParams("C2C_TRADE_TIME");
		String time=params.getParamValue();
		String[] split = time.split("-");
		List<String> list = new ArrayList<>();
		Collections.addAll(list, split);
		String beginTime=list.get(0);
		String endTime=list.get(1);
		boolean timeFlag=DateTools.isEffectiveDate(beginTime,endTime);
		if (timeFlag==false){
			return RspUtil.rspError(responseParamsDto.C2C_TRADE_TIME_ERROR+"("+time+")");
		}


		// 防止重复提交
		Integer add_entrust = (Integer) session.getAttribute(CONFIRM_RECEIVE);
		if (add_entrust != null && add_entrust == 1){
			return RspUtil.rspError("请勿重复提交");
		}
		session.setAttribute(CONFIRM_RECEIVE, 1);

		try {
			BaseResponse<Map<String, Object>> result =  spotDealDetailService.confirmReceive(token, orderNo, tradePassword, responseParamsDto);
			log.info(" confirmReceive|卖方确认收款|返回result:" + JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String exception = sw.toString();
			log.error(exception);
			return RspUtil.error();
		} finally {
			session.setAttribute(CONFIRM_RECEIVE, 0);
		}
	}

	/**
	 * 用户确认付款
	 */
	@ResponseBody
	@RequestMapping(value = "/confirmPay", method = RequestMethod.POST)
	public BaseResponse<Map<String, Object>> confirmPay(String data) throws ParseException {

		log.info(" confirmPay|买方确认付款| " + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info(" confirmPay|买方确认付款|data:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");

		JSONObject json = JSONObject.parseObject(data);
		// 用户验证
		String token = json.getString("token");
		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError("user is null");

		// 选择语言
		String language = json.getString("language");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(language);


		/**交易时间段限制**/
		Params params = paramsMapper.getParams("C2C_TRADE_TIME");
		String time=params.getParamValue();
		String[] split = time.split("-");
		List<String> list = new ArrayList<>();
		Collections.addAll(list, split);
		String beginTime=list.get(0);
		String endTime=list.get(1);
		boolean timeFlag=DateTools.isEffectiveDate(beginTime,endTime);
		if (timeFlag==false){
			return RspUtil.rspError(responseParamsDto.C2C_TRADE_TIME_ERROR+"("+time+")");
		}




		// 参数
		String tradePassword = json.getString("tradePassword");
		String orderNo = json.getString("orderNo");

		if (StringUtils.isBlank(tradePassword) || StringUtils.isBlank(orderNo)) {
			return RspUtil.rspError("必填参数不可为空");
		}

		try {
			BaseResponse<Map<String, Object>> result = spotDealDetailService.confirmPay(orderNo,
					user.getId().intValue(), tradePassword, responseParamsDto);
			log.info(" confirmPay|买方确认付款|返回result:" + JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String exception = sw.toString();
			log.error(exception);
			return RspUtil.error();
		}
	}

	/**
	 * 用户取消订单
	 */
	@ResponseBody
	@RequestMapping(value = "/cancelDealDetail", method = RequestMethod.POST)
	public BaseResponse<Map<String, Object>> cancelDealDetail(String data,HttpSession session) throws ParseException {

		log.info(" cancelDealDetail|用户取消订单| " + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info(" cancelDealDetail|用户取消|data:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");

		JSONObject json = JSONObject.parseObject(data);
		// 用户验证
		String token = json.getString("token");
		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError("user is null");

		// 选择语言
		String language = json.getString("language");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(language);

		/**交易时间段限制**/
		Params params = paramsMapper.getParams("C2C_TRADE_TIME");
		String time=params.getParamValue();
		String[] split = time.split("-");
		List<String> list = new ArrayList<>();
		Collections.addAll(list, split);
		String beginTime=list.get(0);
		String endTime=list.get(1);
		boolean timeFlag=DateTools.isEffectiveDate(beginTime,endTime);
		if (timeFlag==false){
			return RspUtil.rspError(responseParamsDto.C2C_TRADE_TIME_ERROR+"("+time+")");
		}



		// 参数
		String orderNo = json.getString("orderNo");

		if (StringUtils.isBlank(orderNo)) {
			return RspUtil.rspError("必填参数不可为空");
		}
		// 取消方式 ：1、手动；2、定时器
		Integer type = 1;

		// 防止重复提交
		Integer add_entrust = (Integer) session.getAttribute(CANCEL_DEALDETAIL);
		if (add_entrust != null && add_entrust == 1)
			return RspUtil.rspError("cant not repeat to submit");
		session.setAttribute(CANCEL_DEALDETAIL, 1);

		try {
			BaseResponse<Map<String, Object>> result = spotDealDetailService.cancel(orderNo, user.getId(), type,
					responseParamsDto);
			log.info(" cancelDealDetail|用户取消|返回result:" + JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String exception = sw.toString();
			log.error(exception);
			return RspUtil.error();
		} finally {
			session.setAttribute(CANCEL_DEALDETAIL, 0);
		}
	}

	/**
	 * 获取个人C2C订单
	 */
	@ResponseBody
	@PostMapping("/getDealDetailByUserNo")
	public BaseResponse getDealDetailByUserNo(@RequestParam("data") String data) {
		log.info("获取用户C2C订单" + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("获取用户C2C订单参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return spotDealDetailService.getSpotDealDetailByUserNo(data);
		} catch (Exception e) {
			e.printStackTrace();
			return RspUtil.error();
		}
	}

	/**
	 * 获取C2C订单详情
	 */
	@ResponseBody
	@PostMapping("/getDealDetailById")
	public BaseResponse getDealDetailById(@RequestParam("data") String data) {
		log.info("获取C2C订单详情" + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("获取C2C订单详情参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return spotDealDetailService.getSpotDealDetailById(data);
		} catch (Exception e) {
			e.printStackTrace();
			return RspUtil.error();
		}
	}

	/**
	 * 发送短信通知卖方，买方已付款
	 *
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/remindSms", method = RequestMethod.POST)
	public BaseResponse<Map<String, Object>> remindSms(String data) {

		log.info(" remindSms|发送短信通知卖方，买方已付款| " + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info(" remindSms|发送短信通知卖方，买方已付款|data:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");

		JSONObject json = JSONObject.parseObject(data);
		// 用户验证
		String token = json.getString("token");
		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError("user is null");

		// 选择语言
		String language = json.getString("language");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(language);

		// 参数
		String orderNo = json.getString("orderNo");

		if (StringUtils.isBlank(orderNo)) {
			return RspUtil.rspError("必填参数不可为空");
		}

		try {
			BaseResponse<Map<String, Object>> result = spotDealDetailService.remindSms(orderNo, user.getId().intValue(),
					responseParamsDto);
			log.info(" remindSms|发送短信通知卖方，买方已付款|返回result:" + result);
			return result;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String exception = sw.toString();
			log.error(exception);
			return RspUtil.error();
		}
	}

}
