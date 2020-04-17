package com.inesv.service;

import com.inesv.model.*;
import com.inesv.util.BaseResponse;
import com.inesv.util.ResponseParamsDto;

import java.math.BigDecimal;
import java.util.Map;

public interface TradeInfoService {

	BaseResponse addTradeInfo(String data) throws Exception;

	BaseResponse getTradeInfo(String data) throws Exception;

	BaseResponse getTradeInfoByNo(String data) throws Exception;

	BaseResponse getTradeFriends(String data);
	
    //修改表资产方式
	public int editUserWallet(UserWallet userWallet, BigDecimal price, String editType) throws Exception;
	
	//下单
	public Map<String,Object> spotManualTrade(String entrustNo, SpotDealDetail spotDealDetail, UserWallet userWallet, BigDecimal freezeNum, BigDecimal totalETHFreeze, Coin coin) throws Exception;
	
	//冻结资产
	public int freezeUserWalletBalance(UserWallet userWallet, BigDecimal price) throws Exception;
	
	//解冻资产
	public int unfreezeUserWalletBalance(UserWallet userWallet, BigDecimal price) throws Exception;
	
	//c2c:钱包转账资产更新(只走库)
	public boolean tradeAndEditWalletWithLocal(UserWallet buyUserWallet, UserWallet sellUserWallet, BigDecimal buyPrice, BigDecimal tradeAndPoundagePrice, BigDecimal minerFee, String orderNo) throws Exception;

	//c2c:钱包转账资产更新（涉及钱包服务器）
	public boolean tradeAndEditWalletWithServer(UserWallet buyUserWallet, UserWallet sellUserWallet, BigDecimal buyPrice, BigDecimal tradeAndPoundagePrice, BigDecimal minerFee, BigDecimal poundage, String orderNo) throws Exception;

	//c2c:钱包转账
	public BaseResponse transfer4C2C(UserWallet buyUserWallet, UserWallet sellUserWallet, BigDecimal buyPrice, BigDecimal tradeAndPoundagePrice, BigDecimal minerFee, SpotDealDetail spotDealDetail, ResponseParamsDto responseParamsDto) throws Exception;

	//获取矿工费
	public BigDecimal getMinerFee(Long coinType, String outAddress, String inAddress, BigDecimal tradeNum);

	//内部和外部转账
	public BaseResponse handleTransfer(ResponseParamsDto responseParamsDto, String tradeNum, String fee, BigDecimal sumNum,
									   Coin coin, UserWallet outUserWalletForUpdate, UserWallet inUserWalletForUpdate, String outAddress,
									   String inAddress, Address address, User user, String pubKey, String remark) throws Exception;

	public void addTradeInfoAndAddUserWallet(String tradeNum, String fee, String address, String remark, String transferResult, UserWallet userWallet) throws Exception;

	public void freezeETH4Tokens(BigDecimal totalETHFreeze, UserWallet userWallet) throws Exception;

	public void freezeReturn4ETHTokens(UserWallet userWallet, String enstrustNo, String orderNo, int num) throws Exception;

	public void freezeAllReturn4ETHTokens(UserWallet userWallet, String enstrustNo, int successNum) throws Exception;

	String tradeMocToSystem(Long userId, BigDecimal amount, BigDecimal fee, String toAddress, String remark);

	public String payTrade(Long userId, String coinType,BigDecimal amount, BigDecimal fee,
								   String toAddress, String remark,String appId) ;

	String buyPointTrade(Long userId, BigDecimal amount, BigDecimal fee, String toAddress, String coinName,String remark);

	String systemToUserTrade(String mocAddress, Long userId,String amount, String pubKey,  String remark,String fee,String coinName);
}
