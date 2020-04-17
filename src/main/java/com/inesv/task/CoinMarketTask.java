package com.inesv.task;

import com.inesv.mapper.KlineMapper;
import com.inesv.model.Kline;
import com.inesv.util.HttpUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CoinMarketTask {
	@Resource
	private KlineMapper klineMapper;

	private static String btc = "btc_usdt";

	private static String ltc = "ltc_usdt";

	private static String eth = "eth_usdt";

	private static String etc = "etc_usdt";

	private static String hsr = "hsr_usdt";

	private static String qtum = "qtum_usdt";

	@Scheduled(cron = "0 */2 * * * ?")
	public void last() {
		try {
			String btcResult = HttpUtil.getMarket(btc);
			Kline btckline = klineMapper.queryKlineByCoinNo(Long.valueOf(10));
			btckline.setMarket(btcResult);
			klineMapper.updateByCoinNo(btckline);

			String ethResult = HttpUtil.getMarket(eth);
			Kline ethkline = klineMapper.queryKlineByCoinNo(Long.valueOf(20));
			ethkline.setMarket(ethResult);
			klineMapper.updateByCoinNo(ethkline);

			String ltcResult = HttpUtil.getMarket(ltc);
			Kline ltckline = klineMapper.queryKlineByCoinNo(Long.valueOf(30));
			ltckline.setMarket(ltcResult);
			klineMapper.updateByCoinNo(ltckline);

			String etcResult = HttpUtil.getMarket(etc);
			Kline etckline = klineMapper.queryKlineByCoinNo(Long.valueOf(60));
			etckline.setMarket(etcResult);
			klineMapper.updateByCoinNo(etckline);

			String hsrResult = HttpUtil.getMarket(hsr);
			Kline hsrkline = klineMapper.queryKlineByCoinNo(Long.valueOf(70));
			hsrkline.setMarket(hsrResult);
			klineMapper.updateByCoinNo(hsrkline);

			String qtumResult = HttpUtil.getMarket(qtum);
			Kline qtumkline = klineMapper.queryKlineByCoinNo(Long.valueOf(80));
			qtumkline.setMarket(qtumResult);
			klineMapper.updateByCoinNo(qtumkline);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("行情数据更新失败，十秒后重新更新");
		}
	}
}
