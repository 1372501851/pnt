package com.inesv.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.SpotDealDetailService;
import com.inesv.service.TradeInfoService;
import com.inesv.service.UserWalletService;
import com.inesv.sms.AliSmsUtil;
import com.inesv.sms.YunPianSmsUtils;
import com.inesv.util.*;
import com.inesv.util.CoinAPI.PNTCoinApi;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SpotDealDetailServiceImpl implements SpotDealDetailService {

	private static Logger logger = LoggerFactory
			.getLogger(SpotDealDetailServiceImpl.class);

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private SpotDealDetailMapper spotDealDetailMapper;

	@Autowired
	private SpotDisputeMapper spotDisputeMapper;

	@Autowired
	private UserWalletMapper userWalletMapper;

	@Autowired
	private UserWalletService userWalletService;

	@Autowired
	private SpotEntrustMapper spotEntrustMapper;

	@Autowired
	private PoundageMapper poundageMapper;

	@Autowired
	private TradeInfoService tradeInfoService;

	@Autowired
	private TokensFreezeMapper tokensFreezeMapper;
	@Autowired
	private CoinMapper coinMapper;
	@Autowired
	private AddressMapper addressMapper;
	@Autowired
	private TradeInfoMapper tradeInfoMapper;
	/**
	 * 确认收款，转币
	 */
	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public BaseResponse<Map<String, Object>> confirmReceive(String token, String orderNo, String tradePassword, ResponseParamsDto responseParamsDto) throws Exception {
		//用户验证
		User user = userMapper.getUserInfoByToken(token);
		if (user == null){
			return RspUtil.rspError("用户不存在");
		}
		//支付密码验证
		if (!tradePassword.equals(user.getTradePassword())) {
			return RspUtil.rspError(responseParamsDto.FAIL_TRADE_PASSWORD_DESC);
		}

		//用户匹配记录
		SpotDealDetail spotDealDetail = new SpotDealDetail();
		spotDealDetail.setOrderNo(orderNo);
		spotDealDetail.setSellUserNo(user.getId().intValue());
		spotDealDetail.setState(4);
		spotDealDetail = spotDealDetailMapper.selectSpotDealDetailByCondition(spotDealDetail);
		if (spotDealDetail == null || spotDealDetail.getState() != 4) {
			return RspUtil.rspError(responseParamsDto.FAIL_MATCH_FIND_DESC);
		}
		spotDealDetail = spotDealDetailMapper.selectSpotDealDetailByPrimaryKey(spotDealDetail); // 锁行
		if (spotDealDetail.getState() != 4) {
			return RspUtil.rspError(responseParamsDto.FAIL_MATCH_FIND_DESC);
		}
		//匹配记录是否有纠纷
		SpotDispute spotDispute = new SpotDispute();
		spotDispute.setDealNo(spotDealDetail.getId());
		spotDispute = spotDisputeMapper.getSpotDisputeByCondition(spotDispute);
		if (spotDispute != null
				&& spotDispute.getState().toString().equals("0")) {
			return RspUtil.rspError(responseParamsDto.FAIL_DEAL_DISPUTE_DESC);
		}

		//买方钱包
		UserWallet buyUserWallet = new UserWallet();
		buyUserWallet.setUserId(spotDealDetail.getBuyUserNo().longValue());
		buyUserWallet.setType(spotDealDetail.getCoinNo());
		buyUserWallet = userWalletMapper.getUserWalletByCondition(buyUserWallet);
		//钱包不存在则返回
		if (buyUserWallet == null) {
			return RspUtil.rspError(responseParamsDto.FAIL_TRADE_BALANCE_DESC);
		}

		//卖方钱包
		UserWallet sellUserWallet = new UserWallet();
		sellUserWallet.setUserId((long) spotDealDetail.getSellUserNo());
		sellUserWallet.setType(spotDealDetail.getCoinNo());
		sellUserWallet = userWalletMapper.getUserWalletByCondition(sellUserWallet);
		//钱包不存在则返回
		if (sellUserWallet == null) {
			return RspUtil.rspError(responseParamsDto.FAIL_TRADE_BALANCE_DESC);
		}

		//卖方已冻结资产
		BigDecimal sellUserUnBalance = sellUserWallet.getUnbalance();
		// 冻结资产待减去的数量(交易量+手续费+矿工费)
		BigDecimal subFreezeNum = (spotDealDetail.getDealNum()
				.add(spotDealDetail.getPoundage())
				.add(spotDealDetail.getMinerFee()));
		//确保冻结资产足够
		if (sellUserUnBalance.compareTo(subFreezeNum) == -1) {
			return RspUtil.rspError(responseParamsDto.FAIL_MATCH_CONFIRM_DESC);
		}

		// 确认收款
		try {
			BaseResponse response = confirmMatchLogic(spotDealDetail, buyUserWallet,
					spotDealDetail.getDealNum(), sellUserWallet, subFreezeNum,
					spotDealDetail.getPoundage(), spotDealDetail.getMinerFee(), responseParamsDto);

			if (response.getCode() != 200){
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return response;
			}
		} catch (Exception e) {
			logger.error("C2C确认收款失败", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return RspUtil.rspError(responseParamsDto.FAIL_MATCH_CONFIRM_DESC);
		}
		return RspUtil.successMsg(responseParamsDto.SUCCESS_MATCH_CONFIRM_DESC);
	}

	/**
	 * 确认收款逻辑
	 * @param spotDealDetail
	 * @param buyUserWallet
	 * @param dealNum 交易数量
	 * @param sellUserWallet
	 * @param subFreezeNum 交易数量+手续费+矿工费
	 * @param poundage 手续费
	 * @param minerFee 矿工费
     * @throws Exception
     */
	public BaseResponse confirmMatchLogic(SpotDealDetail spotDealDetail,
			UserWallet buyUserWallet, BigDecimal dealNum,
			UserWallet sellUserWallet, BigDecimal subFreezeNum,
			BigDecimal poundage, BigDecimal minerFee, ResponseParamsDto responseParamsDto) throws Exception {
		// 修改匹配记录(订单)
		SpotDealDetail confirmSpotDealDetail = new SpotDealDetail();
		confirmSpotDealDetail.setOrderNo(spotDealDetail.getOrderNo());
		confirmSpotDealDetail.setState(1);
		int result = spotDealDetailMapper.updateSpotDealDetail(confirmSpotDealDetail);
		if (result <= 0){
			throw new Exception("更新订单失败！");
		}

		// 查询买委托记录
		SpotEntrust buySpotEntrust = new SpotEntrust();
		if (spotDealDetail.getBuyEntrust() != 0) {
			buySpotEntrust.setId(spotDealDetail.getBuyEntrust());
			buySpotEntrust = spotEntrustMapper.selectSpotEntrustByCondition(buySpotEntrust);
		} else {
			buySpotEntrust = null;
		}
		// 查询卖委托记录
		SpotEntrust sellSpotEntrust = new SpotEntrust();
		if (spotDealDetail.getSellEntrust() != 0) {
			sellSpotEntrust.setId(spotDealDetail.getSellEntrust());
			sellSpotEntrust = spotEntrustMapper.selectSpotEntrustByCondition(sellSpotEntrust);
		} else {
			sellSpotEntrust = null;
		}

		// 新增手续费记录
		addPoundage(sellUserWallet.getUserId().intValue(),spotDealDetail.getOrderNo(), spotDealDetail.getCoinNo(), poundage);
		// 修改委托记录
		editSpotEntrust(buySpotEntrust, sellSpotEntrust, spotDealDetail.getDealNum());

		//修改用户资产
		Integer buyCoinType = buyUserWallet.getType();//购买用户货币类型
		Integer sellCoinType = sellUserWallet.getType();//出售用户货币类型
		if (buyCoinType != sellCoinType){
			throw new Exception("买方和卖方钱包货币不一致");
		}

		//买方添加可用资产
		UserWallet updateBuyUserWallet = new UserWallet();
		updateBuyUserWallet.setId(buyUserWallet.getId());
		updateBuyUserWallet.setBalance(buyUserWallet.getBalance().add(dealNum));//可用资产加上成交量
		int buyResult = userWalletMapper.updateUserWalletByCondition(updateBuyUserWallet);
		if (buyResult <= 0) {
			throw new Exception("c2c:添加买方资产失败");
		}

		//卖方减少冻结资产
		UserWallet updateSellUserWallet = new UserWallet();
		updateSellUserWallet.setId(sellUserWallet.getId());
		updateSellUserWallet.setUnbalance(sellUserWallet.getUnbalance().subtract(subFreezeNum));//冻结资产减去成交量、手续费和矿工费
		int sellResult = userWalletMapper.updateUserWalletByCondition(updateSellUserWallet);
		if (sellResult <= 0) {
			throw new Exception("c2c:修改卖方资产失败");
		}

		//把产生逻辑错误前的数据进行回滚
		BaseResponse baseResponse = tradeInfoService.transfer4C2C(buyUserWallet, sellUserWallet, dealNum, subFreezeNum, poundage, spotDealDetail, responseParamsDto);
		return baseResponse;
	}

	/**
	 * 新增手续费记录
	 * 
	 * @param userNo
	 * @param coinType
	 * @param money
	 * @throws Exception
	 */
	public void addPoundage(Integer userNo, String orderNo, Integer coinType,
			BigDecimal money) throws Exception {
		List<Poundage> poundageDtos = new ArrayList<>();
		Poundage poundageDto = new Poundage();
		poundageDto.setUserNo(userNo);
		poundageDto.setOrderNo(orderNo);
		poundageDto.setOptype(1);
		poundageDto.setType(coinType);
		poundageDto.setMoney(money);
		poundageDtos.add(poundageDto);
		poundageMapper.insertPoundatges(poundageDtos);
	}

	/**
	 * 确认收款--修改委托记录
	 * @param buySpotEntrustDto
	 * @param sellSpotEntrustDto
	 * @param tradeNum 交易记录
	 * @throws Exception
	 */
	public void editSpotEntrust(SpotEntrust buySpotEntrust, SpotEntrust sellSpotEntrust, BigDecimal tradeNum) throws Exception {
		if (buySpotEntrust == null && sellSpotEntrust == null){
			throw new Exception("委托记录不存在");
		}

		if (buySpotEntrust != null) {
			SpotEntrust buySpotEntrustMatch = new SpotEntrust();
			buySpotEntrustMatch.setId(buySpotEntrust.getId());
			buySpotEntrustMatch.setState(buySpotEntrust.getState());
			buySpotEntrustMatch.setDealNum(buySpotEntrust.getDealNum().add(tradeNum));
			buySpotEntrustMatch.setMatchNum(buySpotEntrust.getMatchNum().subtract(tradeNum));
			// 如果广告状态为进行中，且成交量与委托量相等,修改状态为完成
			if (buySpotEntrust.getState() == 0 && buySpotEntrustMatch.getDealNum().compareTo(buySpotEntrust.getEntrustNum()) == 0) {
				buySpotEntrustMatch.setState(1);
			}
			int result = spotEntrustMapper.updateSpotEntrust(buySpotEntrustMatch);
			if (result <= 0){
				throw new Exception("修改购买委托记录失败");
			}
		}
		if (sellSpotEntrust != null) {
			SpotEntrust sellSpotEntrustMatch = new SpotEntrust();
			sellSpotEntrustMatch.setId(sellSpotEntrust.getId());
			sellSpotEntrustMatch.setState(sellSpotEntrust.getState());
			sellSpotEntrustMatch.setDealNum(sellSpotEntrust.getDealNum().add(tradeNum));
			sellSpotEntrustMatch.setMatchNum(sellSpotEntrust.getMatchNum().subtract(tradeNum));
			// 如果广告状态为进行中，且成交量与委托量相等，修改状态为完成
			if (sellSpotEntrust.getState() == 0 && sellSpotEntrustMatch.getDealNum().compareTo(sellSpotEntrust.getEntrustNum()) == 0) {
				sellSpotEntrustMatch.setState(1);
			}
			int result = spotEntrustMapper.updateSpotEntrust(sellSpotEntrustMatch);
			if (result <= 0){
				throw new Exception("修改出售委托记录失败");
			}
		}
	}

	/**
	 * 买方确认支付
	 */
	@Override
	public BaseResponse<Map<String, Object>> confirmPay(String orderNo,
			Integer userNo, String tradePassword,
			ResponseParamsDto responseParamsDto) throws Exception {

		// 支付密码验证
		User user = userMapper.getUserInfoById((long) userNo);
		if (!tradePassword.equals(user.getTradePassword())) {
			return RspUtil.rspError(responseParamsDto.FAIL_TRADE_PASSWORD_DESC);
		}

		// 订单状态验证
		SpotDealDetail spotDealDetail = new SpotDealDetail();
		spotDealDetail.setOrderNo(orderNo);
		spotDealDetail.setBuyUserNo(userNo);
		spotDealDetail.setState(3);
		spotDealDetail = spotDealDetailMapper
				.selectSpotDealDetailByConditionAndDate(spotDealDetail);
		if (spotDealDetail == null) {
			return RspUtil.rspError(responseParamsDto.FAIL_MATCH_FIND_DESC);
		}

		spotDealDetail = spotDealDetailMapper
				.selectSpotDealDetailByPrimaryKey(spotDealDetail);
		if (spotDealDetail.getState() != 3) {
			return RspUtil.rspError(responseParamsDto.FAIL_MATCH_FIND_DESC);
		}
		SpotDealDetail spotDealDetailUpdate = new SpotDealDetail();
		spotDealDetailUpdate.setOrderNo(orderNo);
		spotDealDetailUpdate.setState(4); // 买家已付款
		spotDealDetailMapper.updateSpotDealDetail(spotDealDetailUpdate);
		final Long matchUserNo = Long.valueOf(spotDealDetail.getSellUserNo());
		// 异步发送短信通知卖方，买方已付款
		try {
			ThreadUtil.execute(new MyTaskUtil() {
				@Override
				public void run() {
					try {
						User user = new User();
						user = userMapper.getUserInfoById(matchUserNo);
						if (user == null || StringUtils.isBlank(user.getPhone()) || StringUtils.isBlank(user.getAreaCode())) {
							return;
						}
//						AliSmsUtil.sendOrderPayed(user.getUsername());
						YunPianSmsUtils.sendOrderPayed(user.getAreaCode(),user.getPhone());
					} catch (Exception e) {
					}
				}
			}, 2, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
		}
		return RspUtil.successMsg(responseParamsDto.SUCCESS_MATCH_CONFIRM_DESC);
	}

	/**
	 * 取消订单
	 */
	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public BaseResponse<Map<String, Object>> cancel(String orderNo,
			Long userNo, Integer editType, ResponseParamsDto responseParamsDto)
			throws Exception {

		// 查找订单记录
		SpotDealDetail spotDealDetail = new SpotDealDetail();
		spotDealDetail.setOrderNo(orderNo);
		spotDealDetail.setState(3);
		if (editType == 1) { // 用户手动取消
			spotDealDetail = spotDealDetailMapper
					.selectSpotDealDetailByConditionAndDate(spotDealDetail);
		}
		if (editType == 2) { // 定时器处理
			spotDealDetail = spotDealDetailMapper
					.selectSpotDealDetailByCondition(spotDealDetail);
		}
		if (spotDealDetail == null) {
			return RspUtil.rspError(responseParamsDto.FAIL_MATCH_FIND_DESC);
		}

		if (userNo.intValue() != spotDealDetail.getBuyUserNo()
				&& userNo.intValue() != spotDealDetail.getSellUserNo()) {
			return RspUtil.rspError(responseParamsDto.FAIL_MATCH_FIND_DESC);
		}

		// 锁行数据
		spotDealDetail = spotDealDetailMapper
				.selectSpotDealDetailByPrimaryKey(spotDealDetail);
		if (spotDealDetail.getState() != 3) {
			return RspUtil.rspError(responseParamsDto.FAIL_MATCH_FIND_DESC);
		}

		// 修改订单记录
		SpotDealDetail spotDealDetailUpdate = new SpotDealDetail();
		spotDealDetailUpdate.setOrderNo(orderNo);

		if (editType == 1) { // 用户手动取消
			spotDealDetailUpdate.setState(2);
		}
		if (editType == 2) { // 定时器处理
			spotDealDetailUpdate.setState(5);
		}
		spotDealDetailMapper.updateSpotDealDetail(spotDealDetailUpdate);

		UserWallet tokensUserWallet = null;//代币钱包

		// 修改委托记录-买
		if (spotDealDetail.getBuyEntrust() != 0) {
			SpotEntrust buySpotEntrust = spotEntrustMapper
					.selectSpotEntrustByPrimaryKey(spotDealDetail
							.getBuyEntrust());
			if (buySpotEntrust.getMatchNum().compareTo(
					spotDealDetail.getDealNum()) == 1
					|| buySpotEntrust.getMatchNum().compareTo(
							spotDealDetail.getDealNum()) == 0) {

				// 已匹配减去订单的交易量
				buySpotEntrust.setMatchNum(buySpotEntrust.getMatchNum()
						.subtract(spotDealDetail.getDealNum()));

				int state = buySpotEntrust.getState();
				// 如果state=2(广告已取消)
				if (state == 2) {
					// cancel量加上订单的交易量
					buySpotEntrust.setCancelNum(buySpotEntrust.getCancelNum()
							.add(spotDealDetail.getDealNum()));
				}
				// 委托中
				if (state == 0) {
					// 最大限额加上订单的交易量
					buySpotEntrust.setEntrustMaxPrice(buySpotEntrust
							.getEntrustMaxPrice().add(
									spotDealDetail.getDealNum()));
					// 剩余量
					BigDecimal lastNum = buySpotEntrust.getEntrustNum()
							.subtract(buySpotEntrust.getDealNum())
							.subtract(buySpotEntrust.getMatchNum());
					// 剩余量大于最小限额记录,则设置最小限额为最小限额记录，否则设置最小限额为剩余量
					if (lastNum.compareTo(buySpotEntrust.getRecordMinPrice()) >= 0) {
						buySpotEntrust.setEntrustMinPrice(buySpotEntrust
								.getRecordMinPrice());
					} else {
						buySpotEntrust.setEntrustMinPrice(lastNum);
					}
				}

				spotEntrustMapper.updateByPrimaryKey(buySpotEntrust);

				//ETH代币交易，匹配代币购买单时，买卖双方其中一个取消订单都要释放卖方的冻结ETH资产
				UserWallet userWallet = new UserWallet();
				userWallet.setUserId((long) spotDealDetail.getSellUserNo());
				userWallet.setType(spotDealDetail.getCoinNo());
				userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
				tokensUserWallet = userWallet;
			} else {
				throw new Exception(" spot cancel | match num error ！");
			}
		}



		// 修改委托记录-卖
		if (spotDealDetail.getSellEntrust() != 0) {
			SpotEntrust sellSpotEntrust = spotEntrustMapper
					.selectSpotEntrustByPrimaryKey(spotDealDetail
							.getSellEntrust());
			if (sellSpotEntrust.getMatchNum().compareTo(
					spotDealDetail.getDealNum()) == 1
					|| sellSpotEntrust.getMatchNum().compareTo(
							spotDealDetail.getDealNum()) == 0) {

				sellSpotEntrust.setMatchNum(sellSpotEntrust.getMatchNum()
						.subtract(spotDealDetail.getDealNum()));

				int state = sellSpotEntrust.getState();
				// 如果state=2(广告已取消)
				if (state == 2) {
					// cancel量加上订单的交易量
					sellSpotEntrust.setCancelNum(sellSpotEntrust.getCancelNum()
							.add(spotDealDetail.getDealNum()));

					//ETH代币交易，购买方匹配出售单，其中一方取消订单且委托单已经取消需要释放卖方的冻结ETH资产
					UserWallet userWallet = new UserWallet();
					userWallet.setUserId((long) spotDealDetail.getSellUserNo());
					userWallet.setType(spotDealDetail.getCoinNo());
					userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
					tokensUserWallet = userWallet;
				}
				// 委托中
				if (state == 0) {
					// 最大限额加上订单的交易量
					sellSpotEntrust.setEntrustMaxPrice(sellSpotEntrust
							.getEntrustMaxPrice().add(
									spotDealDetail.getDealNum()));
					// 剩余量
					BigDecimal lastNum = sellSpotEntrust.getEntrustNum()
							.subtract(sellSpotEntrust.getDealNum())
							.subtract(sellSpotEntrust.getMatchNum());
					// 剩余量大于最小限额记录,则设置最小限额为最小限额记录，否则设置最小限额为剩余量
					if (lastNum.compareTo(sellSpotEntrust.getRecordMinPrice()) >= 0) {
						sellSpotEntrust.setEntrustMinPrice(sellSpotEntrust
								.getRecordMinPrice());
					} else {
						sellSpotEntrust.setEntrustMinPrice(lastNum);
					}
				}
				spotEntrustMapper.updateByPrimaryKey(sellSpotEntrust);

				UserWallet userWallet = new UserWallet();
				userWallet.setUserId((long) spotDealDetail.getSellUserNo());
				userWallet.setType(spotDealDetail.getCoinNo());
				userWallet = userWalletMapper
						.getUserWalletByConditionForUpdate(userWallet);
				// 矿工费
				BigDecimal unFreezeNum = spotDealDetail.getMinerFee();
				// 如果state=2(广告已取消),解冻资产为订单交易量+手续费+矿工费
				if (state == 2) {
					unFreezeNum = unFreezeNum.add(spotDealDetail.getDealNum())
							.add(spotDealDetail.getPoundage());
				}
				// 解冻卖方资产
				int result = tradeInfoService.unfreezeUserWalletBalance(
						userWallet, unFreezeNum);
				if (result != 1)
					throw new Exception("c2c：解冻卖方资产失败！");
			} else {
				throw new Exception(" spot cancel | match num error ！");
			}
		} else {
			UserWallet userWallet = new UserWallet();
			userWallet.setUserId((long) spotDealDetail.getSellUserNo());
			userWallet.setType(spotDealDetail.getCoinNo());
			userWallet = userWalletMapper
					.getUserWalletByConditionForUpdate(userWallet);
			BigDecimal unFreezeNum = spotDealDetail.getDealNum()
					.add(spotDealDetail.getPoundage())
					.add(spotDealDetail.getMinerFee());
			// 解冻卖方资产
			int result = tradeInfoService.unfreezeUserWalletBalance(userWallet,
					unFreezeNum);
			if (result != 1)
				throw new Exception("c2c：解冻卖方资产失败！");
		}

		//代币还需要解冻ETH冻结资产
		if(tokensUserWallet != null){
			Coin coin = new Coin();
			coin.setCoinNo(tokensUserWallet.getType().longValue());
			coin.setState(1);
			coin = coinMapper.getCoinByCondition(coin);
			if("eth_tokens_api".equals(coin.getApiType())){
				//返还之前代币交易时冻结的ETH
				tradeInfoService.freezeReturn4ETHTokens(tokensUserWallet, spotDealDetail.getEntrustNo(), spotDealDetail.getOrderNo(), 1);
			}
		}

		return RspUtil
				.successMsg(responseParamsDto.SUCCESS_SPOT_CANCEL_STATE_DESC);
	}

	@Override
	public BaseResponse getSpotDealDetailByUserNo(String data) throws Exception {
		JSONObject json = JSONObject.parseObject(data);
		String coinNo = json.getString("coinNo");
		String token = json.getString("token");
		// 状态，1：已完成，2：已取消，3：等待买方付款，4：等待卖方确认，5：自动取消，6：纠纷中， 7：纠纷强制打款，8：纠纷强制退款'

		String entrustState = json.getString("entrustState");
		String pageSize = json.getString("pageSize");
		String lineSize = json.getString("lineSize");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
				.getString("language"));
		if (ValidataUtil.isNull(token))
			return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);
		if (ValidataUtil.isNull(coinNo))
			return RspUtil.rspError(responseParamsDto.COIN_NULL_DESC);
		if (ValidataUtil.isNull(entrustState))
			return RspUtil.rspError(responseParamsDto.STATE_NULL_DESC);
		if (ValidataUtil.isNull(pageSize))
			return RspUtil.rspError(responseParamsDto.PAGEORLINE_NULL_DESC);
		if (ValidataUtil.isNull(lineSize))
			return RspUtil.rspError(responseParamsDto.PAGEORLINE_NULL_DESC);

		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

		SpotDealDetail spotDealDetail = new SpotDealDetail();
		spotDealDetail.setUserNo(Integer.valueOf(user.getId().toString()));
		spotDealDetail.setCoinNo(Integer.valueOf(coinNo));
		if (entrustState.equals("0")) { // 进行中
			spotDealDetail.setConductState(1);
		}
		if (entrustState.equals("1")) { // 已完成
			spotDealDetail.setCompleteState(1);
		}

		List<SpotDealDetail> spotDealDetails = new ArrayList<>();

		if (pageSize.equals("0") && lineSize.equals("0")) {
			spotDealDetails = spotDealDetailMapper
					.selectSpotDealDetailByConditions(spotDealDetail);
			for (int i = 0, len = spotDealDetails.size(); i < len; i++) {
				spotDealDetails.get(i).setUsername(
						spotDealDetails.get(i).getBuyUserName());
				spotDealDetails.get(i).setPhoto(
						spotDealDetails.get(i).getBuyUserPhoto());
				spotDealDetails.get(i).setDealType(1);
				if (spotDealDetails.get(i).getBuyUserNo().toString()
						.equals(user.getId().toString())) {
					spotDealDetails.get(i).setUsername(
							spotDealDetails.get(i).getSellUserName());
					spotDealDetails.get(i).setPhoto(
							spotDealDetails.get(i).getSellUserPhoto());
					spotDealDetails.get(i).setDealType(0);
				}
			}
		} else {
			PageHelper.startPage(Integer.valueOf(pageSize),
					Integer.valueOf(lineSize));
			spotDealDetails = spotDealDetailMapper
					.selectSpotDealDetailByConditions(spotDealDetail);
			for (int i = 0, len = spotDealDetails.size(); i < len; i++) {
				spotDealDetails.get(i).setUsername(
						spotDealDetails.get(i).getBuyUserName());
				spotDealDetails.get(i).setPhoto(
						spotDealDetails.get(i).getBuyUserPhoto());
				spotDealDetails.get(i).setDealType(1);
				if (spotDealDetails.get(i).getBuyUserNo().toString()
						.equals(user.getId().toString())) {
					spotDealDetails.get(i).setUsername(
							spotDealDetails.get(i).getSellUserName());
					spotDealDetails.get(i).setPhoto(
							spotDealDetails.get(i).getSellUserPhoto());
					spotDealDetails.get(i).setDealType(0);
				}
			}
		}
		for (SpotDealDetail sdd :spotDealDetails) {
			//获取货币信息拿到货币图片
			Coin coin=coinMapper.getCoinByCoinNo(Long.valueOf(sdd.getCoinNo()));
			if (coin !=null && !ValidataUtil.isNull(coin.getCoinImg())){
				sdd.setCoinImg(coin.getCoinImg());
			}
		}
		return RspUtil.success(JSONFormat.getStr(spotDealDetails));
	}

	@Override
	public BaseResponse getSpotDealDetailById(String data) throws Exception {
		JSONObject json = JSONObject.parseObject(data);
		String id = json.getString("id");
		String token = json.getString("token");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
				.getString("language"));
		if (ValidataUtil.isNull(id))
			return RspUtil.rspError(responseParamsDto.ID_NULL_DESC);
		if (ValidataUtil.isNull(token))
			return RspUtil.rspError(responseParamsDto.TOKEN_NULL_DESC);

		User user = userMapper.getUserInfoByToken(token);
		if (user == null)
			return RspUtil.rspError(responseParamsDto.ACCOUNT_NULL_DESC);

		SpotDealDetail spotDealDetail = new SpotDealDetail();
		spotDealDetail.setId(Long.valueOf(id));
		spotDealDetail = spotDealDetailMapper
				.selectSpotDealDetailByCondition(spotDealDetail);

		String entrustNo = spotDealDetail.getEntrustNo();
		SpotEntrust condition = new SpotEntrust();
		condition.setEntrustNo(entrustNo);
		SpotEntrust spotEntrust = spotEntrustMapper
				.selectSpotEntrustByCondition(condition);

		// 1：银行卡，2：支付宝，3：微信，4：其他)
		int receiveType = spotEntrust.getReceivablesType();
		if (receiveType == 1)
			spotDealDetail.setAttr1("银行卡");
		if (receiveType == 2)
			spotDealDetail.setAttr1("支付宝");
		if (receiveType == 3)
			spotDealDetail.setAttr1("微信");
		if (receiveType == 4)
			spotDealDetail.setAttr1("其他");
		if (receiveType == 5)
			spotDealDetail.setAttr1("ImToken");
		spotDealDetail.setUsername(spotDealDetail.getBuyUserName());
		spotDealDetail.setPhoto(spotDealDetail.getBuyUserPhoto());
		spotDealDetail.setDealType(1);
		if (spotDealDetail.getBuyUserNo().toString()
				.equals(user.getId().toString())) {
			spotDealDetail.setUsername(spotDealDetail.getSellUserName());
			spotDealDetail.setPhoto(spotDealDetail.getSellUserPhoto());
			spotDealDetail.setDealType(0);
		}
		//获取货币信息拿到货币图片
		Coin coin=coinMapper.getCoinByCoinNo(Long.valueOf(spotDealDetail.getCoinNo()));
		if (coin !=null && !ValidataUtil.isNull(coin.getCoinImg())){
			spotDealDetail.setCoinImg(coin.getCoinImg());
		}

		//出售方用户
		User sellUser=userMapper.getUserInfoById(Long.valueOf(spotDealDetail.getSellUserNo()));
		//购买方用户
		User buyUser=userMapper.getUserInfoById(Long.valueOf(spotDealDetail.getBuyUserNo()));

		spotDealDetail.setSellAccountPhone(sellUser.getAreaCode()+sellUser.getPhone());
		spotDealDetail.setBuyAccountPhone(buyUser.getAreaCode()+buyUser.getPhone());

		return RspUtil.success(JSONFormat.getStr(spotDealDetail));
	}

	/**
	 * 发送短信通知卖方，买方已付款
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BaseResponse<Map<String, Object>> remindSms(String orderNo,
			Integer userId, ResponseParamsDto responseParamsDto)
			throws Exception {

		SpotDealDetail condition = new SpotDealDetail();
		condition.setOrderNo(orderNo);
		condition.setUserNo(userId);
		condition.setState(4);
		SpotDealDetail spotDealDetail = spotDealDetailMapper
				.selectSpotDealDetailByCondition(condition);
		if (spotDealDetail != null) {

			String reminded = spotDealDetail.getAttr2();
			if ("1".equals(reminded))
				return RspUtil.rspError(responseParamsDto.SEND_SELLER_NITY);

			// 设置订单状态不可发送短信
			SpotDealDetail updateSpotDealDetail = new SpotDealDetail();
			updateSpotDealDetail.setOrderNo(spotDealDetail.getOrderNo());
			updateSpotDealDetail.setAttr2("1");
			spotDealDetailMapper.updateSpotDealDetail(updateSpotDealDetail);

			Long sellUserNo = (long) spotDealDetail.getSellUserNo();
			User sellUser = userMapper.getUserInfoById(sellUserNo);

//			String result = AliSmsUtil.sendOrderPayed(sellUser.getUsername());
            if (StringUtils.isBlank(sellUser.getPhone()) || StringUtils.isBlank(sellUser.getAreaCode())) {
                return RspUtil.rspError(responseParamsDto.SEND_SELLER_NITY);
            }
			//TODO 云片国际短信发送短信
			String result = YunPianSmsUtils.sendOrderPayed(sellUser.getAreaCode(),sellUser.getPhone());
			if (result.equals("success")) {
				return RspUtil.successMsg(result);
			}else{
				throw new RuntimeException(result);
			}
		}
		return RspUtil.error();
	}





























	/************ C2C管理员纠纷相关   ****/

	/**
	 * 确认收款逻辑(强制拨款逻辑)
	 * @param spotDealDetail
	 * @param buyUserWallet
	 * @param sellUserWallet
	 * @param coin
	 * @param dealNum 交易数量
	 * @param poundage 手续费
	 * @param sumNum 交易总额
	 * @param type 类型（0：卖方确认收款，1：管理员同意纠纷，强制拨款）
	 * @param responseParamsDto
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public void confirmMatchLogic(SpotDealDetail spotDealDetail, UserWallet buyUserWallet, UserWallet sellUserWallet, Coin coin,
										  BigDecimal dealNum, BigDecimal poundage, BigDecimal sumNum, Integer type, ResponseParamsDto responseParamsDto) throws Exception {
		if (type != 0 && type != 1){
			throw new Exception("确认收款逻辑(强制拨款逻辑)，类型异常！type：" + type + "，单号：" + spotDealDetail.getId());
		}
		String errMsg1 = "纠纷 : 更新订单失败！单号Id：";
		String errMsg2 = "纠纷 : 管理员强制拨款-买方收币，单号Id：";
		String errMsg3 = "纠纷 : 管理员强制拨款-卖方拨币，单号Id：";

		SpotDealDetail confirmSpotDealDetail = new SpotDealDetail();
		confirmSpotDealDetail.setOrderNo(spotDealDetail.getOrderNo());
		if (type == 1){
			confirmSpotDealDetail.setState(7);//7：管理员纠纷强制打款
		}
		int code = spotDealDetailMapper.updateSpotDealDetail(confirmSpotDealDetail);
		if (code <= 0){
			throw new Exception(errMsg1 + spotDealDetail.getId());
		}

		SpotEntrust buySpotEntrust = new SpotEntrust();		// 查询买委托记录
		if (spotDealDetail.getBuyEntrust() != 0) {
			buySpotEntrust.setId(spotDealDetail.getBuyEntrust());
			buySpotEntrust = spotEntrustMapper.selectSpotEntrustByCondition(buySpotEntrust);
		}

		SpotEntrust sellSpotEntrust = new SpotEntrust();	// 查询卖委托记录
		if (spotDealDetail.getSellEntrust() != 0) {
			sellSpotEntrust.setId(spotDealDetail.getSellEntrust());
			sellSpotEntrust = spotEntrustMapper.selectSpotEntrustByCondition(sellSpotEntrust);
		}
		Address address=new Address();
		address.setCoinNo(coin.getCoinNo());
		address=addressMapper.getAddressByCondition(address);
		String hash = null;
		if (address != null){
			if (coin.getCoinNo().intValue() == spotDealDetail.getCoinNo()){
			    try{
                    PNTCoinApi pntCoinApi =  PNTCoinApi.getApi(address);
                    hash=pntCoinApi.sendTransaction(sellUserWallet.getAddress(),buyUserWallet.getAddress(),String.valueOf(dealNum.doubleValue()),"","C2C管理员强制放行", String.valueOf(poundage.doubleValue()),coin.getCoinName().toLowerCase());
                    if (ValidataUtil.isNull(hash)) {
                        logger.info(responseParamsDto.BALANCE_LOCKFAIL_DESC);
                    }
                    logger.info("本次放行的记录Hash: {}",hash);
                }catch (Exception e){
			        throw e;
                }
			}
		}

		addPoundage(sellUserWallet.getUserId().intValue(), spotDealDetail.getOrderNo(), spotDealDetail.getCoinNo(), poundage);	// 新增手续费记录
		editSpotEntrustAdmin(buySpotEntrust, sellSpotEntrust, spotDealDetail.getDealNum(), type);	// 修改委托记录

		userWalletService.editUserWalletBalance(buyUserWallet, dealNum, 0, errMsg2 + spotDealDetail.getId());
		userWalletService.editUserWalletBalance(sellUserWallet, sumNum, 4, errMsg3 + spotDealDetail.getId());

        //添加转账记录
		Long tradeInfoId=addTradeInfo(sellUserWallet.getUserId(), 40, dealNum.toString(),
                poundage, sellUserWallet.getAddress(), buyUserWallet.getAddress(), spotDealDetail.getOrderNo(), 1, "c2c:管理员强制放行", 0, null);

		editTradeInfo(tradeInfoId, hash);


	}
	
	/**
	 * 修改交易记录订单号
	 *
	 * @throws Exception
	 */
	public void editTradeInfo(Long tradeInfoId, String hash)
			throws Exception {
		TradeInfo tradeInfo = new TradeInfo();
		tradeInfo.setId(tradeInfoId);
		tradeInfo.setHash(hash);
		tradeInfoMapper.updateTradeInfo(tradeInfo);
	}
	
    /**
     * 新增交易记录
     *
     * @throws Exception
     */
    public Long addTradeInfo(Long userId, Integer coinNo, String tradeNum,
                             BigDecimal fee, String outAddress, String inAddress,
                             String orderNo, Integer state, String remark, int tradeType,
                             BigDecimal unTradeNum) throws Exception {
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setUserId(userId);
        tradeInfo.setCoinNo(coinNo);
        tradeInfo.setType(0);
        tradeInfo.setOrderNo(orderNo);
        tradeInfo.setTradeNum(new BigDecimal(tradeNum));
        tradeInfo.setRatio(fee);
        tradeInfo.setOutAddress(outAddress);
        tradeInfo.setInAddress(inAddress);
        tradeInfo.setRemark(remark);
        tradeInfo.setState(state);
        tradeInfo.setType(tradeType);
        tradeInfo.setUnTradeNum(unTradeNum == null ? BigDecimal.ZERO : unTradeNum);
        int result = tradeInfoMapper.insertTradeInfo(tradeInfo);
        if (result <= 0) {
            throw new Exception("转账：生成订单异常！");
        }
        return tradeInfo.getId();
    }





	/**
	 * 修改委托记录
	 * @param buySpotEntrust 买委托广告
	 * @param sellSpotEntrust 卖委托广告
	 * @param tradeNum 交易数量
	 * @throws Exception
	 */
	public void editSpotEntrustAdmin(SpotEntrust buySpotEntrust, SpotEntrust sellSpotEntrust, BigDecimal tradeNum, Integer type) throws Exception {
		String errMsg1 = " C2C : 买卖双方广告为空 !";
		String errMsg2 = " C2C : 卖方确认收款，修改委托广告异常，广告单号：";
		String errMsg3 = "委托广告匹配数量小于订单交易数量异常";
		if (type == 1){
			errMsg1 = " 纠纷 - C2C : 买卖双方广告为空 !";
			errMsg2 = " 纠纷 - 管理员强制卖方拨款，修改委托广告异常，广告单号：";
		}

		if (buySpotEntrust == null && sellSpotEntrust == null) throw new Exception(errMsg1);

		if (buySpotEntrust != null && !ValidataUtil.isNull(buySpotEntrust.getId())) {
			if (buySpotEntrust.getMatchNum().compareTo(tradeNum) == -1){
				throw new Exception(errMsg3);
			}
			SpotEntrust buySpotEntrustMatch = new SpotEntrust();
			buySpotEntrustMatch.setId(buySpotEntrust.getId());
			buySpotEntrustMatch.setState(buySpotEntrust.getState());
			buySpotEntrustMatch.setDealNum(buySpotEntrust.getDealNum().add(tradeNum));
			buySpotEntrustMatch.setMatchNum(buySpotEntrust.getMatchNum().subtract(tradeNum));
			if (buySpotEntrust.getState() == 3
					&& (buySpotEntrustMatch.getDealNum().add(buySpotEntrust.getCancelNum())).compareTo(buySpotEntrust.getEntrustNum()) == 0) {
				buySpotEntrustMatch.setState(1);
			}
			int buyFlag = spotEntrustMapper.updateSpotEntrust(buySpotEntrustMatch);
			if (buyFlag != 1) {
				throw new Exception(errMsg2 + buySpotEntrustMatch.getId());
			}
		}

		if (sellSpotEntrust != null && !ValidataUtil.isNull(sellSpotEntrust.getId())) {
			if (sellSpotEntrust.getMatchNum().compareTo(tradeNum) == -1){
				throw new Exception(errMsg3);
			}
			SpotEntrust sellSpotEntrustMatch = new SpotEntrust();
			sellSpotEntrustMatch.setId(sellSpotEntrust.getId());
			sellSpotEntrustMatch.setState(sellSpotEntrust.getState());
			sellSpotEntrustMatch.setDealNum(sellSpotEntrust.getDealNum().add(tradeNum));
			sellSpotEntrustMatch.setMatchNum(sellSpotEntrust.getMatchNum().subtract(tradeNum));
			if (sellSpotEntrust.getState() == 3
					&& (sellSpotEntrustMatch.getDealNum().add(sellSpotEntrust.getCancelNum())).compareTo(sellSpotEntrust.getEntrustNum()) == 0) {
				sellSpotEntrustMatch.setState(1);
			}
			int sellFlag = spotEntrustMapper.updateSpotEntrust(sellSpotEntrustMatch);
			if (sellFlag != 1){
				throw new Exception(errMsg2 + sellSpotEntrustMatch.getId());
			}
		}
	}
}
