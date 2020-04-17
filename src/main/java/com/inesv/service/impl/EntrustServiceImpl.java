package com.inesv.service.impl;

import com.inesv.mapper.SpotEntrustMapper;
import com.inesv.mapper.TokensFreezeMapper;
import com.inesv.mapper.UserWalletMapper;
import com.inesv.model.Coin;
import com.inesv.model.SpotEntrust;
import com.inesv.model.TokensFreeze;
import com.inesv.model.UserWallet;
import com.inesv.service.EntrustService;
import com.inesv.service.TradeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class EntrustServiceImpl implements EntrustService {

	private static Logger logger = LoggerFactory.getLogger(EntrustServiceImpl.class);
	
	@Autowired
	private SpotEntrustMapper spotEntrusMapper;
	
	@Resource
	private TradeInfoService tradeInfoService;

	@Autowired
	private UserWalletMapper userWalletMapper;
	@Autowired
	private TokensFreezeMapper tokensFreezeMapper;

	/**
	 * 添加委托记录及冻结用户资产
	 * @param spotEntrust 委托实体
	 * @param userWallet 用户钱包
	 * @param freezePrice 冻结资产数量
	 * @param totalETHFreeze 委托ETH代币出售时需要冻结的ETH资产数量
	 * @return
     * @throws Exception
     */
	public Long addSpotEntrust(SpotEntrust spotEntrust, UserWallet userWallet, BigDecimal freezePrice, BigDecimal totalETHFreeze, Coin coin)
			throws Exception {
		//添加委托记录
		spotEntrusMapper.insertSpotEntrust(spotEntrust);

		//冻结用户资产
		int result = tradeInfoService.freezeUserWalletBalance(userWallet, freezePrice);
		if(result <= 0) {
			logger.warn("添加卖方委托记录时，冻结资产失败！");
			throw new Exception("冻结资产失败！");
		}

		if(spotEntrust.getEntrustType() == 1 && "eth_tokens_api".equals(coin.getApiType())){
			//委托代币出售时，需要冻结ETH资产
			tradeInfoService.freezeETH4Tokens(totalETHFreeze, userWallet);
			//添加ETH代币交易冻结的ETH记录
			//String entrustNo, String orderNo, Integer coinNo, BigDecimal unbalance
			tokensFreezeMapper.add(new TokensFreeze(spotEntrust.getEntrustNo(), null, spotEntrust.getEntrustCoin(), totalETHFreeze));
		}

		//返回委托记录主键ID
		return spotEntrust.getId();
	}

}
