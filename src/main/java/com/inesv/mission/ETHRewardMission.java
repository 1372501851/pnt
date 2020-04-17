package com.inesv.mission;

import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.TradeInfoService;
import com.inesv.service.UserWalletService;
import com.inesv.util.CastUtils;
import com.inesv.util.CoinAPI.EthcoinAPI;
import com.inesv.util.MD5Util;
import com.inesv.util.ValidataUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * ETH根据区块高度定时充值
 */
//@Component
public class ETHRewardMission {
	private Logger logger = LoggerFactory.getLogger(ETHRewardMission.class);

	@Autowired
	private BonusDetailMapper bonusDetailMapper;
	@Autowired
	private ParamsMapper paramsMapper;
	@Autowired
	private AddressMapper addressMapper;
	@Autowired
	private TradeInfoMapper tradeInfoMapper;
	@Autowired
	private TradeInfoService tradeInfoService;
	@Autowired
	private UserWalletMapper userWalletMapper;
	@Autowired
	UserWalletService userWalletService;

	//@Scheduled(cron = "0 0/3 * * * ?")
	public void ethCharge() throws Exception{
		//上次同步的ETH区块高度
		Params params = paramsMapper.getParams("LastSyncETHBlockNumber");
		if (ValidataUtil.isNull(params.getParamValue())){
			return;
		}

		//ETH接口信息
		Address address = new Address();
		address.setCoinNo(20L);
		address = addressMapper.getAddressByCondition(address);
		if(address.getStatus() == 0){
			return;
		}

		EthcoinAPI eth = new EthcoinAPI(address.getAddress(), address.getPort(), address.getName(), address.getPassword(), address.getLockPassword());
		
		String blockNumber = eth.eth_blockNumber();//当前节点区块高度-16进制
		int blockNumberInt = new BigInteger(blockNumber.substring(blockNumber.indexOf("0x")+"0x".length()),16).intValue();
		int lastSyncBlockNumber = CastUtils.castInt(params.getParamValue())+1;//上次定时充值以太坊区块高度+1
		
		for(int i=lastSyncBlockNumber;i<=blockNumberInt;i++){
			String blockNumberHex = eth.eth_getBlockByNumber("0x"+new BigInteger(i+"",10).toString(16));
			if(!StringUtils.isNotEmpty(blockNumberHex)){
				List<BlockExploror> blockContatens = eth.blockTransactionFormat(blockNumberHex);
				if(blockContatens.size() > 0){
					for (BlockExploror blockExploror : blockContatens) {
						//交易数量为16进制，需要转换为10进制
						String transValueHex = blockExploror.getTransValue();//16进制转账金额
						BigDecimal transValueAlgorism = ValidataUtil.Progressive(transValueHex);//转换为10进制
						BigDecimal value = transValueAlgorism.divide(EthcoinAPI.wei);//交易数量（除ETH单位）

						String transHash = blockExploror.getTransHash();//转账hash
						String transTo = blockExploror.getTransTo();//接收地址
						String confirmations = blockExploror.getConfirmations();

						//是否为本节点创建钱包地址
						UserWallet userWallet = new UserWallet();
						userWallet.setType(20);
						userWallet.setAddress(transTo);
						userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
						if(userWallet != null){
							boolean flag = reacher(transHash, CastUtils.castInt(confirmations) , "receive", userWallet, value, "ETH", address);
							logger.info("当前获取到的ETH流水记录transHash：{}，transTo：{}，confirmations：{}，充值结果：{}", transHash, transTo, confirmations, flag);
						}
					}
				}
			}
		}
	}

	protected boolean reacher(String hash, int confirmations, String type,
							  UserWallet wallet, BigDecimal money, String coinName,
							  Address address) throws Exception {
		//处理交易记录中to（接收地址）和当前遍历的用户钱包地址相等的记录
		if(!"receive".equals(type) || confirmations <= 0){
			logger.info("userId：{}，钱包地址：{}，只处理充值记录，转账不处理且区块高度必须大于0", wallet.getUserId() + address.getAddress());
			return false;
		}

		hash = MD5Util.GetMD5Code(coinName + hash);
		TradeInfo tradeInfo = new TradeInfo();
		tradeInfo.setHash(hash);
		List<TradeInfo> tradeInfos = tradeInfoMapper.getTradeInfoByConditions(tradeInfo);
		if (tradeInfos == null || tradeInfos.size() == 0) {
			// 没有充值过 进行充值
			tradeInfo.setAddress(wallet.getAddress());
			tradeInfo.setCoinNo(wallet.getType());
			tradeInfo.setDate(new Date(System.currentTimeMillis()));
			tradeInfo.setUserId(0l);
			tradeInfo.setType(0);// 0为转入1为转出
			tradeInfo.setOrderNo(ValidataUtil.generateUUID());
			tradeInfo.setTradeNum(money);
			tradeInfo.setOutAddress("");
			tradeInfo.setInAddress(wallet.getAddress());
			tradeInfo.setState(1);// 1转账成功
			tradeInfo.setRatio(new BigDecimal("0"));
			tradeInfo.setRemark("转入");
			return userWalletService.rechargeAddress(wallet.getId(), tradeInfo, coinName, address);
		}
		return false;
	}
}
