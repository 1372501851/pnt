package com.inesv.mission;

import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.TradeInfoService;
import com.inesv.util.CoinAPI.PNTCoinApi;
import com.inesv.util.GsonUtils;
import com.inesv.util.MD5Util;
import com.inesv.util.ValidataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 根据邀请注册记录发放奖励
 */
@Component
public class RewardMission {
	private Logger logger = LoggerFactory.getLogger(RewardMission.class);

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

	@Scheduled(cron = "0 0/10 * * * ?")
	public void reward() {
		Params params = paramsMapper.getParams("PTN_Transfer_Station");
		if (ValidataUtil.isNull(params.getParamValue())){
			return;
		}
		Address address = new Address();
		address.setCoinNo(40L);
		address = addressMapper.getAddressByCondition(address);
		if (ValidataUtil.isNull(params.getParamValue())){
			return;
		}

		PNTCoinApi api = PNTCoinApi.getApi(address);
		//获取中间账号钱包地址
		String centerAddress = params.getParamValue();

		//查询未发放的邀请注册奖励
		BonusDetail bonusDetail = new BonusDetail();
		bonusDetail.setState(0);
		bonusDetail.setBonusKey("invitation");
		List<BonusDetail> list = bonusDetailMapper.getBonusDetailByCondition(bonusDetail);
		for (BonusDetail detail : list) {
			String tradeNum = String.valueOf(detail.getPrice());//奖励数量
			String inAddress = detail.getRecWallet();//奖励地址
			String remark = "邀请注册奖励";//备注
			Long recId = detail.getRecId();//推荐人ID
			Long userId = detail.getUserId();//被推荐人ID

			UserWallet userWallet = new UserWallet();
			userWallet.setType(40);
			userWallet.setUserId(recId);
			userWallet = userWalletMapper.getUserWalletByCondition(userWallet);
			if(userWallet == null){
				logger.info("推荐人用户钱包未创建，不发放奖励，奖励记录：{}", GsonUtils.toJson(detail));
				continue;
			}

			UserWallet uWallet = new UserWallet();
			uWallet.setType(40);
			uWallet.setUserId(userId);
			uWallet = userWalletMapper.getUserWalletByCondition(uWallet);
			if(uWallet == null){
				logger.info("被推荐人用户钱包未创建，不发放奖励，奖励记录：{}", GsonUtils.toJson(detail));
				continue;
			}

			//奖励地址不存在，更新推荐人钱包地址到邀请注册奖励表
			if(inAddress == null){
				inAddress = userWallet.getAddress();
				BonusDetail bDetail = new BonusDetail();
				bDetail.setId(detail.getId());
				bDetail.setRecWallet(inAddress);
				int result = bonusDetailMapper.updateBonusDetail(bDetail);
				logger.info("更新推荐人钱包地址到邀请注册奖励表，更新的数据为：{}，更新结果：{}", GsonUtils.toJson(bDetail), result);
			}

			//修改标记1开始
			/*BonusDetail bDetail = new BonusDetail();
			bDetail.setId(detail.getId());
			bDetail.setState(1);
			int count = bonusDetailMapper.updateBonusDetail(bDetail);//更新为奖励已发放
			if(count > 0 ){
				try {
					//新增转账记录并增加用户资产
					String result = "PTN";//当成转账的hash用
					tradeInfoService.addTradeInfoAndAddUserWallet(tradeNum, "0", inAddress, remark, result, userWallet);
					logger.info("发放邀请注册奖励成功，接收地址：{}，接收数量：{}", inAddress, tradeNum);
				} catch (Exception e) {
					logger.error("发放邀请注册奖励失败", e);
				}
			}*/
			//修改标记1结束

			//修改标记1开始
			for(int i=0;i<2;i++){//转账失败重试第二次
				String result = transfer4PTN(api, centerAddress, inAddress, tradeNum, remark);
				if (!ValidataUtil.isNull(result)) {
					String hash = MD5Util.GetMD5Code("PTN" + result);
					TradeInfo tradeInfo = new TradeInfo();
					tradeInfo.setHash(hash);
					List<TradeInfo> tradeInfos = tradeInfoMapper.getTradeInfoByConditions(tradeInfo);//hash不存在才进邀请奖励记录
					if(tradeInfos.size() == 0){
						BonusDetail bDetail = new BonusDetail();
						bDetail.setId(detail.getId());
						bDetail.setState(1);
						int count = bonusDetailMapper.updateBonusDetail(bDetail);//更新为奖励已发放
						if(count > 0 ){
							try {
								//新增转账记录并增加用户资产
								tradeInfoService.addTradeInfoAndAddUserWallet(tradeNum, "0", inAddress, remark, result, userWallet);
							} catch (Exception e) {
								logger.error("发放邀请奖励失败", e);
							}
						}
						break;
					}
				}
			}
			//修改标记1结束
		}
	}

	/**
	 * PTN转账
	 * @param api
	 * @param outAddress
	 * @param inAddress
	 * @param tradeNum
	 * @param remark
     * @return
     */
	private String transfer4PTN(PNTCoinApi api, String outAddress, String inAddress, String tradeNum, String remark) {
		try {
			Thread.sleep(2000);//请不要去掉这行代码，因为转入地址、转出地址、转账数量相同时，2秒内发送的请求都会返回同一个hash
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return api.sendTransaction(outAddress, inAddress, tradeNum, "", remark, "0.0001", "moc");
	}
}
