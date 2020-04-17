package com.inesv.task;

import com.inesv.mapper.KlineMapper;
import com.inesv.model.Kline;
import com.inesv.util.HttpUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class KlineTask {

	@Resource
	private KlineMapper klineMapper;

	private static String btc = "btc_usdt";

	private static String ltc = "ltc_usdt";

	private static String eth = "eth_usdt";

	private static String etc = "etc_usdt";

	private static String hsr = "hsr_usdt";

	private static String qtum = "qtum_usdt";

	@Scheduled(cron = "0 0 0/1 * * ?")
	// @Scheduled(cron = "0/10 * * * * ?")
	public void kline() {
		Kline kline = null;
		try {
			String btcResult = HttpUtil.Kline(btc, "1day");
			if (!"".equals(btcResult)) {
				kline = klineMapper.queryKlineByCoinNo(Long.valueOf(10));
				kline.setKline(btcResult);
				klineMapper.updateByCoinNo(kline);
			}
			String ltcResult = HttpUtil.Kline(eth, "1day");
			if (!"".equals(ltcResult)) {
				kline = klineMapper.queryKlineByCoinNo(Long.valueOf(30));
				kline.setKline(ltcResult);
				klineMapper.updateByCoinNo(kline);
			}
			String ethResult = HttpUtil.Kline(ltc, "1day");
			if (!"".equals(ethResult)) {
				kline = klineMapper.queryKlineByCoinNo(Long.valueOf(20));
				kline.setKline(ethResult);
				klineMapper.updateByCoinNo(kline);
			}
			String etcResult = HttpUtil.Kline(etc, "1day");
			if (!"".equals(etcResult)) {
				kline = klineMapper.queryKlineByCoinNo(Long.valueOf(60));
				kline.setKline(etcResult);
				klineMapper.updateByCoinNo(kline);
			}
			String hsrResult = HttpUtil.Kline(hsr, "1day");
			if (!"".equals(hsrResult)) {
				kline = klineMapper.queryKlineByCoinNo(Long.valueOf(70));
				kline.setKline(hsrResult);
				klineMapper.updateByCoinNo(kline);
			}
			String qtumResult = HttpUtil.Kline(qtum, "1day");
			if (!"".equals(qtumResult)) {
				kline = klineMapper.queryKlineByCoinNo(Long.valueOf(80));
				kline.setKline(qtumResult);
				klineMapper.updateByCoinNo(kline);
			}
		} catch (Exception e) {
			System.out.println("K线图数据更新失败，一小时后重新更新");
		}

	}
}
