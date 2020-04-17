package com.inesv.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.pool.vendor.NullExceptionSorter;
import com.inesv.mapper.AddressMapper;
import com.inesv.mapper.CoinMapper;
import com.inesv.mapper.CycleTradeMapper;
import com.inesv.mapper.ParamsMapper;
import com.inesv.mapper.TradeInfoMapper;
import com.inesv.mapper.UserWalletMapper;
import com.inesv.model.Address;
import com.inesv.model.Coin;
import com.inesv.model.CycleTrade;
import com.inesv.model.Params;
import com.inesv.model.TradeInfo;
import com.inesv.model.UserWallet;
import com.inesv.service.CycleTradeService;
import com.inesv.service.TradeInfoService;
import com.inesv.util.ValidataUtil;
import com.inesv.util.CoinAPI.BitcoinAPI;
import com.inesv.util.CoinAPI.EthcoinAPI;
import com.inesv.util.CoinAPI.PNTCoinApi;

@Service
@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
public class CycleTradeServiceImpl implements CycleTradeService {

	@Autowired
	CycleTradeMapper cycleTradeMapper;
	@Autowired
	CoinMapper coinMapper;
	@Autowired
	AddressMapper addressMapper;
	@Autowired
	TradeInfoMapper tradeInfoMapper;
	@Autowired
	ParamsMapper paramsMapper;
	@Autowired
	UserWalletMapper userWalletMapper;
	@Autowired
	TradeInfoService tradeInfoService;

	// @Override
	// @Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	// public boolean doCycleTrade(CycleTrade cycleTrade) throws Exception {
	// boolean ok = false;
	// if (cycleTrade.getSurplusPrice().compareTo(BigDecimal.ZERO) == -1) {
	// throw new Exception("剩余金额小于0，资产异常！！！");
	// }
	// if (cycleTrade != null && cycleTrade.getState() == 0) {
	// // 未处理完 判断周期
	// Date curDate = new Date(System.currentTimeMillis());
	// Date beforeDate = cycleTrade.getBeforeDate();
	// int diffDays = differentDays(beforeDate, curDate);
	// int cycleDays = cycleTrade.getCycledDays();
	// if (diffDays >= cycleDays) {
	// // 到了下一个周期 进行处理
	// // 完成的周期加1
	// cycleTrade
	// .setCompletedCycle(cycleTrade.getCompletedCycle() + 1);
	// if (cycleTrade.getCompletedCycle() == cycleTrade
	// .getCountCycle()) {
	// // 周期转账全部完成
	// cycleTrade.setState(1);
	// }
	// // 更新时间
	// cycleTrade.setBeforeDate(curDate);
	// // 计算转账金额总金额*每个周期的比例 如总金额100，比例20%则该周期应为20
	// BigDecimal price = cycleTrade.getSumPrice().multiply(
	// cycleTrade.getProportion());
	// if (price.compareTo(cycleTrade.getSurplusPrice()) > 0
	// || cycleTrade.getCountCycle() == cycleTrade
	// .getCompletedCycle()) {
	// // 如果剩余金额不足或者是最后一个周期，那么直接将所有的金额转过去，并标记为已完成
	// price = cycleTrade.getSurplusPrice();
	// cycleTrade.setState(1);
	// }
	// // 保留六位小数，之后的位数舍掉
	// price = price.setScale(6, BigDecimal.ROUND_DOWN);
	// int count = cycleTradeMapper.modifyCycleTrade(cycleTrade);
	// if (count > 0) {
	// // 更改记录成功 进行转账
	// // 获取api
	// Coin coin = coinMapper.queryCoinByCoinNo(cycleTrade
	// .getCoinid());
	// if (coin == null) {
	// throw new Exception("获取币种信息失败");
	// }
	// Address address = addressMapper.queryAddressInfo(coin
	// .getCoinNo());
	// if (coin.getCoinName().equalsIgnoreCase("ETH")
	// || coin.getCoinName().equalsIgnoreCase("ETC")) {
	// EthcoinAPI ethcoinAPI = new EthcoinAPI(
	// address.getAddress(), address.getPort(),
	// address.getName(), address.getPassword(),
	// address.getLockPassword());
	// // 计算手续费
	// String priceStr = price.multiply(EthcoinAPI.wei)
	// .setScale(0, BigDecimal.ROUND_DOWN).toString();
	// Params params = paramsMapper
	// .queryByKey("ETH_Transfer_Station");
	// BigDecimal fee = ethcoinAPI.getGasAndGasPrice(
	// params.getParamValue(),
	// cycleTrade.getInaddress(),
	// "0x"
	// + new BigInteger(priceStr, 10)
	// .toString(16)).divide(
	// EthcoinAPI.wei);
	// // 添加到转账记录表
	// long tradeId = addTradeInfo(cycleTrade.getOutUserId(),
	// cycleTrade.getCoinid() + "", price.toString(),
	// fee, params.getParamValue(),
	// cycleTrade.getInaddress(),
	// ValidataUtil.generateUUID(), 1, "周期转账---；"
	// + cycleTrade.getRemark());
	// // 减少冻结资产
	// UserWallet outuserWallet = new UserWallet();
	// outuserWallet.setUserId(cycleTrade.getOutUserId());
	// outuserWallet.setCoinType(Integer.parseInt(cycleTrade
	// .getCoinid() + ""));
	// outuserWallet = userWalletMapper
	// .getUserWalletByConditionForUpdate(outuserWallet);
	//
	// // tradeInfoService.editUserWallet(outuserWallet,
	// // price.toString(), "2");
	// // 增加资产
	// UserWallet inuserWallet = new UserWallet();
	// inuserWallet.setAddress(cycleTrade.getInaddress());
	// inuserWallet.setCoinType(Integer.parseInt(cycleTrade
	// .getCoinid() + ""));
	// inuserWallet = userWalletMapper
	// .getUserWalletByConditionForUpdate(inuserWallet);
	// if (inuserWallet != null) {
	// // 不为空就是平台内转账，就要给表资产加
	// tradeInfoService.editUserWallet(inuserWallet,
	// price.toString(), "0");
	// }
	// // 正式转账
	// String result = ethcoinAPI.sendTransaction(
	// params.getParamValue(),
	// cycleTrade.getInaddress(),
	// "0x"
	// + new BigInteger(price
	// .multiply(EthcoinAPI.wei)
	// .setScale(0,
	// BigDecimal.ROUND_DOWN)
	// .toString(), 10).toString(16));
	// System.out.println("中间账号" + params.getParamValue());
	// System.out.println("转入账号" + cycleTrade.getInaddress());
	// System.out.println("price" + price.toString());
	// System.out.println("fee" + fee.toString());
	// if (ValidataUtil.isNull(result)) {
	// throw new Exception("周期转账失败");
	// }
	// } else if (coin.getCoinName().equalsIgnoreCase("PTN")
	// || coin.getCoinName().equalsIgnoreCase("PTNCNY")) {
	// // PTN
	// PNTCoinApi api = PNTCoinApi.getApi(address);
	// // 手续费
	// BigDecimal fee = new BigDecimal("0.01");
	// // 获取中间账号
	// Params params = paramsMapper
	// .queryByKey("PTN_Transfer_Station");
	// // 添加到记录表
	// long tradeId = addTradeInfo(cycleTrade.getOutUserId(),
	// cycleTrade.getCoinid() + "", price.toString(),
	// fee, params.getParamValue(),
	// cycleTrade.getInaddress(),
	// ValidataUtil.generateUUID(), 1, "周期转账---；"
	// + cycleTrade.getRemark());
	// // 减少冻结资产
	// UserWallet outuserWallet = new UserWallet();
	// outuserWallet.setUserId(cycleTrade.getOutUserId());
	// outuserWallet.setType(Integer.parseInt(cycleTrade
	// .getCoinid() + ""));
	// outuserWallet = userWalletMapper
	// .getUserWalletByConditionForUpdate(outuserWallet);
	//
	// // tradeInfoService.editUserWallet(outuserWallet,
	// // price.toString(), "2");
	// // 增加资产
	// UserWallet inuserWallet = new UserWallet();
	// inuserWallet.setAddress(cycleTrade.getInaddress());
	// inuserWallet.setType(Integer.parseInt(cycleTrade
	// .getCoinid() + ""));
	// inuserWallet = userWalletMapper
	// .getUserWalletByConditionForUpdate(inuserWallet);
	// if (inuserWallet != null) {
	// // 不为空就是平台内转账，就要给表资产加
	// tradeInfoService.editUserWallet(inuserWallet,
	// price.toString(), "0");
	// }
	// // 正式转账
	// String result = api
	// .sendTransaction(
	// params.getParamValue(),
	// cycleTrade.getInaddress(),
	// price.toString(),
	// cycleTrade.getPubKey(),
	// cycleTrade.getRemark(),
	// fee.toString(),
	// coin.getCoinName().equalsIgnoreCase(
	// "PTN") ? "ptn" : "ptncny");
	// System.out.println("中间账号" + params.getParamValue());
	// System.out.println("转入账号" + cycleTrade.getInaddress());
	// System.out.println("price" + price.toString());
	// System.out.println("fee" + fee.toString());
	// System.out.println("result" + result);
	// if (ValidataUtil.isNull(result)) {
	// throw new Exception("周期转账失败");
	// }
	// } else {
	// // 其他币种
	// // 添加到记录表
	// long tradeId = addTradeInfo(cycleTrade.getOutUserId(),
	// cycleTrade.getCoinid() + "", price.toString(),
	// BigDecimal.ZERO, "中间账户地址",
	// cycleTrade.getInaddress(),
	// ValidataUtil.generateUUID(), 1, "周期转账---；"
	// + cycleTrade.getRemark());
	// // 减少冻结资产
	// UserWallet outuserWallet = new UserWallet();
	// outuserWallet.setUserId(cycleTrade.getOutUserId());
	// outuserWallet.setCoinType(Integer.parseInt(cycleTrade
	// .getCoinid() + ""));
	// outuserWallet = userWalletMapper
	// .getUserWalletByConditionForUpdate(outuserWallet);
	//
	// // tradeInfoService.editUserWallet(outuserWallet,
	// // price.toString(), "2");
	// // 增加资产
	// UserWallet inuserWallet = new UserWallet();
	// inuserWallet.setAddress(cycleTrade.getInaddress());
	// inuserWallet.setCoinType(Integer.parseInt(cycleTrade
	// .getCoinid() + ""));
	// inuserWallet = userWalletMapper
	// .getUserWalletByConditionForUpdate(inuserWallet);
	// if (inuserWallet != null) {
	// // 不为空就是平台内转账，就要给表资产加
	// tradeInfoService.editUserWallet(inuserWallet,
	// price.toString(), "0");
	// } else {
	// // 平台外地址需要进行地址转账
	// BitcoinAPI bitcoinAPI = new BitcoinAPI(
	// address.getAddress(), address.getPort(),
	// address.getName(), address.getPassword(),
	// address.getLockPassword());
	// bitcoinAPI.closewallet();
	// bitcoinAPI.openwallet();
	// String result = bitcoinAPI.sendToAddress(
	// cycleTrade.getInaddress(), price);
	// bitcoinAPI.closewallet();
	//
	// System.out.println("转入账号"
	// + cycleTrade.getInaddress());
	// System.out.println("price" + price.toString());
	// if (ValidataUtil.isNull(result))
	// throw new Exception(
	// "addTradeInfo | Transfer error ");
	// }
	// }
	// }
	// }
	// } else {
	// // 已经处理完的
	// return ok;
	// }
	// return ok;
	// }
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	@Override
	public boolean doCycleTrade(CycleTrade cycleTrade) throws Exception {
		// TODO Auto-generated method stub
		boolean ok = false;
		if (cycleTrade == null) {
			throw new Exception("Null Exception");
		}
		if (cycleTrade.getSurplusPrice().compareTo(BigDecimal.ZERO) == -1) {
			throw new Exception("金额异常，剩余解冻金额为负数！");
		}
		if (cycleTrade.getState() == 0) {
			Date curDate = new Date(System.currentTimeMillis());
			int difDays = differentDays(cycleTrade.getBeforeDate(), curDate);
			if (difDays >= cycleTrade.getCycledDays()) {
				// 周期到了
				// 更新时间
				cycleTrade.setBeforeDate(curDate);
				cycleTrade
						.setCompletedCycle(cycleTrade.getCompletedCycle() + 1);
				// 这个周期需要解冻的金额
				BigDecimal freezed = cycleTrade.getSumPrice().multiply(
						cycleTrade.getProportion());
				if (cycleTrade.getCompletedCycle() == cycleTrade
						.getCountCycle()
						|| cycleTrade.getSurplusPrice().compareTo(freezed) <= 0) {
					// 最后一个周期 或者剩余金额只有最后一点
					cycleTrade.setState(1);
					// 将剩余需要解冻的金额全解冻
					freezed = cycleTrade.getSurplusPrice();
					cycleTrade.setSurplusPrice(BigDecimal.ZERO);
				} else {
					// 剩余解冻金额减少相应值
					cycleTrade.setSurplusPrice(cycleTrade.getSurplusPrice()
							.subtract(freezed));
				}
				// 进行解冻
				UserWallet userWallet = new UserWallet();
				userWallet.setAddress(cycleTrade.getAddress());
				userWallet.setUserId(cycleTrade.getUserId());
				userWallet
						.setType(Integer.parseInt(cycleTrade.getCoinid() + ""));
				userWallet = userWalletMapper
						.getUserWalletByCondition(userWallet);
				userWallet.setBalance(userWallet.getBalance().add(freezed));
				userWallet.setUnbalance(userWallet.getUnbalance().subtract(
						freezed));
				int count = userWalletMapper
						.updateUserWalletByCondition(userWallet);
				ok = count > 0;
				if (ok) {
					count = cycleTradeMapper.modifyCycleTrade(cycleTrade);
					if (count <= 0) {
						throw new Exception("更新定时任务失败");
					}
				}
			} else {
				// 周期没到
				ok = true;
			}
		} else {
			ok = true;
		}
		return ok;
	}

	@Override
	public List<CycleTrade> getAllNeedDo() throws Exception {
		return cycleTradeMapper.getAllNeedToDo();
	}

	/**
	 * 新增交易记录
	 * 
	 * @throws Exception
	 */
	/*
	 * public Long addTradeInfo(Long userId, String coinNo, String tradeNum,
	 * BigDecimal fee, String outAddress, String inAddress, String orderNo,
	 * Integer state, String remark) throws Exception { TradeInfo tradeInfo =
	 * new TradeInfo(); tradeInfo.setUserId(userId);
	 * tradeInfo.setCoinNo(Integer.valueOf(coinNo)); tradeInfo.setType(0);
	 * tradeInfo.setOrderNo(orderNo); tradeInfo.setTradeNum(new
	 * BigDecimal(tradeNum)); tradeInfo.setRatio(fee);
	 * tradeInfo.setOutAddress(outAddress); tradeInfo.setInAddress(inAddress);
	 * tradeInfo.setRemark(remark); tradeInfo.setState(state);
	 * tradeInfoMapper.insertTradeInfo(tradeInfo); return tradeInfo.getId(); }
	 */

	/**
	 * date2比date1多的天数
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	private int differentDays(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1 = cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if (year1 != year2) // 同一年
		{
			int timeDistance = 0;
			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) // 闰年
				{
					timeDistance += 366;
				} else // 不是闰年
				{
					timeDistance += 365;
				}
			}

			return timeDistance + (day2 - day1);
		} else // 不同年
		{
			return day2 - day1;
		}
	}
}
