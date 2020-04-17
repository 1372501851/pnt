package com.inesv.service;

import com.inesv.model.Address;
import com.inesv.model.Coin;
import com.inesv.model.TradeInfo;
import com.inesv.model.UserWallet;
import com.inesv.util.BaseResponse;

import java.math.BigDecimal;

public interface UserWalletService {

	BaseResponse getWalletBalance(String data);

	BaseResponse getStates(String data);

	BaseResponse editState(String data);

	/**
	 * 新增钱包地址
	 * 
	 * @param data
	 *            参数
	 * @return
	 */
	BaseResponse addWallet(String data);

	/**
	 * 定时器处理充值
	 * 
	 * @param userWalletId
	 * @param tradeInfo
	 * @return
	 * @throws Exception
	 */
	boolean rechargeAddress(long userWalletId, TradeInfo tradeInfo,
							String coinName, Address address) throws Exception;

	BaseResponse getCoinDetail(String data);

	BaseResponse assetsList(String data);

	BaseResponse getCoinWallet(String data);

	// 获取用户某货币可用资产
	public BigDecimal getEnableBalance(UserWallet userWallet, Coin coin, Address address);














	/************ C2C管理员纠纷相关   ****/
	int editUserWalletBalance(UserWallet userWallet, BigDecimal price, Integer type, String remark) throws Exception;
}
