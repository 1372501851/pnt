package com.inesv.controller;

import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.SpotDealDetailService;
import com.inesv.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONObject;
import com.inesv.annotation.UnLogin;
import com.inesv.service.SpotEntrustService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 
 * @Description:c2c广告控制器
 * @author: cmk
 * @date: 2018年3月16日 下午1:30:47
 */
@Controller
public class SpotEntrsutController {

	@Autowired
	private UserMapper userMapper;

	private static Logger log = LoggerFactory.getLogger(SpotEntrsutController.class);

	@Autowired
	private SpotEntrustService spotEntrustService;
	@Autowired
	private SpotDealDetailMapper spotDealDetailMapper;
	@Autowired
	private SpotEntrustMapper spotEntrustMapper;
	@Autowired
	ParamsMapper paramsMapper;
	@Autowired
	private LocaleMessageUtils messageUtils;
	@Autowired
	UserWalletMapper userWalletMapper;

	private static String ADD_ENTRUST = "add_entrust";
	private static String ADD_SPOT_DEAL = "add_spot_deal";
	private static String CANCEL_ENTRUST = "cancel_entrust";

	//发布C2C广告交易设置的开关
	private final String C2C_TRANSFER_STATE ="C2C_TRANSFER_STATE";
	//匹配C2C广告交易设置的开关
	private final String C2C_MATCH_TRANSFER_STATE ="C2C_MATCH_TRANSFER_STATE";
	/**
	 * 发布C2C广告交易入口
	 */
	@PostMapping("/addSpotEntrust")
	@ResponseBody
	public BaseResponse addSpotEntrust(String data, HttpSession session) throws Exception {
		log.info("spotEntrsut |发布C2C广告| " + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("spotEntrsut |发布C2C广告|请求data:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");

		JSONObject json = JSONObject.parseObject(data);
		ResponseParamsDto rpd = LanguageUtil.proving(json.getString("language"));
		Params params = paramsMapper.queryByKey(C2C_TRANSFER_STATE);
		if (params.getParamValue().equals("0")) {
			return RspUtil.rspError(rpd.NOT_YET_OPNE);
		}


        /**交易时间段限制**/
        Params timeParams = paramsMapper.getParams("C2C_TRADE_TIME");
        String time=timeParams.getParamValue();
        String[] split = time.split("-");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, split);
        String beginTime=list.get(0);
        String endTime=list.get(1);
        boolean timeFlag=DateTools.isEffectiveDate(beginTime,endTime);
        if (timeFlag==false){
            return RspUtil.rspError(rpd.C2C_TRADE_TIME_ERROR+"("+time+")");
        }



		// 用户验证
		String token = json.getString("token");
		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError("user is null");
		if (ValidataUtil.isNull(user.getPhone()) || user.getPhone().equalsIgnoreCase("") )
			return RspUtil.rspError("phoneNumber is null");

		if(user.getState()==0){
			//如果状态为0不能参与C2C
			return RspUtil.rspError("本账户已冻结，无法进行操作！！");
		}


		// 选择语言
		String language = json.getString("language");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(language);

		// 参数
		Integer coinNo = json.getInteger("coinNo");
		String tradeType = json.getString("tradeType");
		BigDecimal tradePrice = json.getBigDecimal("tradePrice");
		BigDecimal tradeNum = json.getBigDecimal("tradeNum");
		Integer receivablesType = json.getInteger("receivablesType");
		BigDecimal minTradeNum = json.getBigDecimal("minPrice");
		String tradePassword = json.getString("tradePassword");
		String remark = json.getString("remark");
		Integer bankcardId = json.getInteger("bankcardId");

		//如果交易数量小于100
		if(tradeNum.compareTo(new BigDecimal(100)) == -1){
			return RspUtil.rspError(responseParamsDto.C2C_TRADE_NUM_LIMIT);
		}

		if (coinNo == null || StringUtils.isBlank(tradeType) || tradePrice == null || tradeNum == null
				|| receivablesType == null || minTradeNum == null || StringUtils.isBlank(tradePassword)) {
			return RspUtil.rspError("必填参数不可为空");
		}

		/**广告方发布数量限制*/
		SpotEntrust spotEntrust=new SpotEntrust();
		spotEntrust.setUserNo(Math.toIntExact(user.getId()));
		spotEntrust.setState(0);
		Integer num=spotEntrustMapper.selectCountByCondition(spotEntrust);
		if(num>=1){
			//您存在委托中的广告，请完成后重试！
			return RspUtil.rspError(responseParamsDto.CUSTOMER_NUM_ENTRUST_LIMIT);
		}
		/**匹配单（被动方）*/
		SpotDealDetail spotDealDetail=new SpotDealDetail();
		spotDealDetail.setSellUserNo(Math.toIntExact(user.getId()));
		Integer num1=spotDealDetailMapper.selectCountByState(spotDealDetail);
		if(num1>=1){
			//您存在进行中的广告，请完成后重试！
			return RspUtil.rspError(responseParamsDto.NUM_ENTRUST_LIMIT);
		}



		// 防止重复提交
		Integer add_entrust = (Integer) session.getAttribute(ADD_ENTRUST);
		if (add_entrust != null && add_entrust == 1)
			return RspUtil.rspError("cant not repeat to submit");
		session.setAttribute(ADD_ENTRUST, 1);

		try {
			BaseResponse<Map<String, Object>> result = spotEntrustService.addEntrust(user.getId(), coinNo, tradeType, tradePrice,
					tradeNum, receivablesType, minTradeNum, tradePassword,
					remark, bankcardId, responseParamsDto);
			log.info("spotEntrsut |发布C2C广告|返回result:" + JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String exception = sw.toString();
			log.error(exception);
			return RspUtil.error();
		} finally {
			// 还原ADD_ENTRUST为0，表示可提交
			session.setAttribute(ADD_ENTRUST, 0);
		}
	}

	/**
	 * C2C点对点交易
	 */
	@ResponseBody
	@RequestMapping(value = "/spotTrade", method = RequestMethod.POST)
	public BaseResponse<Map<String, Object>> spotTrade(String data, HttpSession session) throws Exception {

		log.info("spotTrade |创建c2c订单| " + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("spotTrade |创建c2c订单|data:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");

		JSONObject json = JSONObject.parseObject(data);

		// 用户验证
		String token = json.getString("token");
		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError("user is null");

		if(user.getState()==0){
			//如果状态为0不能参与C2C
			return RspUtil.rspError("本账户已冻结，无法进行操作！！");
		}

		// 选择语言
		String language = json.getString("language");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(language);


        /**交易时间段限制**/
        Params timeParams = paramsMapper.getParams("C2C_TRADE_TIME");
        String time=timeParams.getParamValue();
        String[] split = time.split("-");
        List<String> list = new ArrayList<>();
        Collections.addAll(list, split);
        String beginTime=list.get(0);
        String endTime=list.get(1);
        boolean timeFlag=DateTools.isEffectiveDate(beginTime,endTime);
        if (timeFlag==false){
			return RspUtil.rspError(responseParamsDto.C2C_TRADE_TIME_ERROR+"("+time+")");
        }



		Params params = paramsMapper.queryByKey(C2C_MATCH_TRANSFER_STATE);
		if (params.getParamValue().equals("0")) {
			return RspUtil.rspError(responseParamsDto.NOT_YET_OPNE);
		}
		// 参数
		Long userNo = user.getId();
		String entrustNo = json.getString("entrustNo");
		BigDecimal tradeNum = json.getBigDecimal("tradeNum");
		String tradePassword = json.getString("tradePassword");
		String remark = json.getString("remark");
		Integer bankId = json.getInteger("bankId");

		if (userNo == null || StringUtils.isBlank(entrustNo) || tradeNum == null
				|| StringUtils.isBlank(tradePassword)) {
			return RspUtil.rspError("必填参数不可为空");
		}


		/**分币种限制交易*/
		SpotEntrust spotEntrust=new SpotEntrust();
		spotEntrust.setEntrustNo(entrustNo);
		spotEntrust=spotEntrustMapper.selectSpotEntrustByCondition(spotEntrust);
		//  EntrustType 委托类型0.买1.卖
		if( spotEntrust.getEntrustType()==0){
			// 转出账户
			UserWallet sellUserWallet=new UserWallet();
			sellUserWallet.setUserId(userNo);
			sellUserWallet.setType(Integer.valueOf(spotEntrust.getEntrustCoin()));
			sellUserWallet = userWalletMapper.getUserWalletByCondition(sellUserWallet);
			//19-10-10判断币种状态  0开启，1关闭
			if(sellUserWallet.getWalletState()==1){
				return RspUtil.rspError(responseParamsDto.TRADE_COIN_SUSPENDED);
			}
		}


		/**广告方匹配数量限制*/
		//匹配时，判断这条订单进行中的数量，只能存在一条进行中的匹配单
		SpotDealDetail spotDealDetail=new SpotDealDetail();
		spotDealDetail.setEntrustNo(entrustNo);
		Integer num=spotDealDetailMapper.selectCountByState(spotDealDetail);
		log.info("广告方匹配数量限制:{}",num);
		if(num>=1){
			//(广告方)该订单存在进行中的订单，请稍后重试！
			return RspUtil.rspError(responseParamsDto.NUM_MATCH_LIMIT);
		}
		/**用户匹配数量限制*/
		//匹配时，判断这条订单进行中的数量，只能存在三条进行中的匹配单
		SpotDealDetail spotNum=new SpotDealDetail();
		spotNum.setBuyUserNo(Math.toIntExact(userNo));
		Integer num1=spotDealDetailMapper.selectCountByState(spotNum);
		log.info("用户匹配数量限制:{}",num1);
		if(num1>=3){
			//(用户自身)存在三条进行中的订单，请完成交易后重试！
			return RspUtil.rspError(responseParamsDto.CUSTOMER_NUM_MATCH_LIMIT);
		}
		/**用户匹配数量限制*/
		//匹配时，判断这条订单进行中的数量，只能存在三条进行中的匹配单
		SpotDealDetail spotNum1=new SpotDealDetail();
		spotNum1.setSellUserNo(Math.toIntExact(userNo));
		Integer num2=spotDealDetailMapper.selectCountByState(spotNum1);
		log.info("用户匹配数量限制:{}",num2);
		if(num2>=3){
			//(用户自身)存在三条进行中的订单，请完成交易后重试！
			return RspUtil.rspError(responseParamsDto.CUSTOMER_NUM_MATCH_LIMIT);
		}


		// 防止重复提交
		Integer add_entrust = (Integer) session.getAttribute(ADD_SPOT_DEAL);
		if (add_entrust != null && add_entrust == 1)
			return RspUtil.rspError("cant not repeat to submit");
		session.setAttribute(ADD_SPOT_DEAL, 1);

		try {
			BaseResponse<Map<String, Object>> result = spotEntrustService.spotTrade(userNo, entrustNo, tradeNum,
					tradePassword, remark, bankId, responseParamsDto);
			log.info("spotTrade |创建c2c订单|返回data:" + JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String exception = sw.toString();
			log.error(exception);
			return RspUtil.error();
		} finally {
			session.setAttribute(ADD_SPOT_DEAL, 0);
		}
	}

	/**
	 * 撤销广告交易入口(用户)
	 */
	@ResponseBody
	@RequestMapping(value = "/delSpotEntrust", method = RequestMethod.POST)
	public BaseResponse<Map<String, Object>> delSpotEntrust(String data, HttpSession session) throws ParseException {
		log.info("delSpotEntrust |c2c:用户撤销委托| " + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("delSpotEntrust |c2c:用户撤销委托|data:" + data);
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
        Params timeParams = paramsMapper.getParams("C2C_TRADE_TIME");
        String time=timeParams.getParamValue();
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
		String entrustNo = json.getString("entrustNo");

		if (StringUtils.isBlank(entrustNo)) {
			return RspUtil.rspError("必填参数不可为空");
		}

		// 防止重复提交
		Integer add_entrust = (Integer) session.getAttribute(CANCEL_ENTRUST);
		if (add_entrust != null && add_entrust == 1)
			return RspUtil.rspError("cant not repeat to submit");
		session.setAttribute(CANCEL_ENTRUST, 1);

		try {
			BaseResponse<Map<String, Object>> result = spotEntrustService.delEntrust(user.getId().intValue(), entrustNo,
					responseParamsDto);
			log.info("delSpotEntrust |c2c:用户撤销委托|返回data:" + JSONObject.toJSONString(result));
			return result;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String exception = sw.toString();
			log.error(exception);
			return RspUtil.error();
		} finally {
			session.setAttribute(CANCEL_ENTRUST, 0);
		}
	}


	/**
	 * 获取平台广告
	 */
	@ResponseBody
	@PostMapping("/getSpotEntrust")
	@UnLogin
	public BaseResponse<Map<String, Object>> getSpotEntrust(@RequestParam("data") String data) {
		log.info("获取平台广告" + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("获取平台广告参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return spotEntrustService.getSpotEntrust(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 获取用户发布的广告
	 */
	@ResponseBody
	@PostMapping("/getSpotEntrustByUserNo")
	public BaseResponse<Map<String, Object>> getSpotEntrustByUserNo(@RequestParam("data") String data) {
		log.info("获取用户广告" + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("获取用户广告参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return spotEntrustService.getSpotEntrustByUserNo(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 获取用户发布的广告详情
	 */
	@ResponseBody
	@PostMapping("/getSpotEntrustById")
	@UnLogin
	public BaseResponse<Map<String, Object>> getSpotEntrustById(@RequestParam("data") String data) {
		log.info("获取广告详情" + Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("获取广告详情参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return spotEntrustService.getSpotEntrustById(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 获取开启C2C广告的货币列表
	 */
	@ResponseBody
	@GetMapping("/getOpenTransCoins")
	@UnLogin
	public BaseResponse getOpenTransCoins() {
		log.info("获取开启C2C广告的货币列表" + Thread.currentThread().getStackTrace()[1].getMethodName());
		try {
			return spotEntrustService.getOpenTransCoins();
		} catch (Exception e) {
			return RspUtil.error();
		}
	}
}
