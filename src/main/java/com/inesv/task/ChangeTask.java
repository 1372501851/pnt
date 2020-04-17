package com.inesv.task;

import com.alibaba.fastjson.JSONObject;
import com.inesv.mapper.*;
import com.inesv.model.*;
import com.inesv.service.ChangeService;
import com.inesv.service.ParamsService;
import com.inesv.service.TradeInfoService;
import com.inesv.util.CoinAPI.PNTCoinApi;
import com.inesv.util.OrderUtil;
import com.inesv.util.ValidataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ChangeTask {

	@Resource
	private ParamsMapper paramsMapper;

	@Resource
	private ChangeMapper changeMapper;

	@Resource
	private TradeInfoMapper tradeInfoMapper;

	@Resource
	private AddressMapper addressMapper;

	@Resource
	private CoinMapper coinMapper;

	@Resource
	private UserWalletMapper userWalletMapper;

	@Scheduled(cron = "0 0 12 * * ?")
	// @Scheduled(cron = "0 0 12 * * ?")////"0 */5 * * * ?"
	public void Change() {
		// Long PTNCoinNo = coinMapper.queryByCoinName("PTN").getCoinNo();
		// Address PTNaddress = addressMapper.queryAddressInfo(PTNCoinNo);
		String TransferStation = paramsMapper
				.queryByKey("PTN_Transfer_Station").getParamValue();
		List<Change> changes = changeMapper.queryFrozenAssets();
		for (Change change : changes) {
			Address PTNaddress = addressMapper.queryAddressInfo(change
					.getInCoin());
			Coin outCoin = coinMapper.getCoinByCoinNo(change.getOutCoin());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("userId", change.getUserId());
			params.put("coinNo", change.getInCoin());
			UserWallet inWallet = userWalletMapper.queryByUserState(params);
			// 冻结转换数量
			BigDecimal Price = change.getPnt().subtract(
					change.getPnt().multiply(outCoin.getFreePrice())
							.setScale(6, BigDecimal.ROUND_HALF_UP));

			// 对比当前冻结资产是否大于解锁金额
			BigDecimal changeNum = Price.multiply(outCoin.getUnlockRatio())
					.setScale(6, BigDecimal.ROUND_HALF_UP);
			if (changeNum.longValue() > change.getFrozenAssets().longValue()) {
				changeNum = change.getFrozenAssets();
			}

			// 查询中间账户金额是否足够
			BigDecimal TransferStationBalance = new BigDecimal(PNTCoinApi
					.getApi(PTNaddress).getBalance(TransferStation, "moc"));
			if (changeNum.multiply(outCoin.getPntRatio())
					.setScale(4, BigDecimal.ROUND_HALF_UP)
					.compareTo(TransferStationBalance) > 0) {
				continue;
			}

			if (change.getFrozenAssets().longValue() > 0) {
				// 由中间账户帮忙转出到的PTN账户
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(change.getLastChangeTime());
				c.add(Calendar.DATE, outCoin.getUnlockDay());
				Date changeDate = c.getTime();
				// 判断当前时间是否等于设置的时间
				if (format.format(changeDate).equals(format.format(date))) {
					PNTCoinApi.getApi(PTNaddress).sendTransaction(
							TransferStation, inWallet.getAddress(),
							changeNum.toString(), "", "中间账号转账", "0", "moc");

					TradeInfo tradeInfo = new TradeInfo();
					tradeInfo.setUserId(0L);
					tradeInfo.setCoinNo(Integer.valueOf(change.getInCoin()
							.toString()));
					tradeInfo.setType(0);
					tradeInfo.setOrderNo(ValidataUtil.generateUUID() + "0000");
					tradeInfo.setTradeNum(changeNum);
					tradeInfo.setRatio(new BigDecimal(0));
					tradeInfo.setOutAddress(TransferStation);
					tradeInfo.setInAddress(inWallet.getAddress());
					tradeInfo.setRemark("MOC中间账户转账");
					tradeInfo.setState(1);
					tradeInfoMapper.insertTradeInfo(tradeInfo);
					change.setFrozenAssets(change.getFrozenAssets().subtract(
							changeNum));
					change.setLastChangeTime(new Date());
					changeMapper.updateFrozenAssets(change);
				}
			}

		}
	}

}
