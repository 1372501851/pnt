package com.inesv.task;

import com.inesv.mapper.ParamsMapper;
import com.inesv.model.Params;
import com.inesv.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
public class MarketTask {
	private static final Logger log = LoggerFactory.getLogger(MarketTask.class);

	@Resource
	private ParamsMapper paramsMapper;

//5分钟获取一次

//	@Scheduled(cron = "0 */1 * * * ?")
	@Scheduled(cron = "0 */5 * * * ? ")
	public void Market() {
		try {
			BigDecimal btcMarket = HttpUtil.getcoinRate("btc_usdt");
			log.info("查询币种btc_usdt行情："+btcMarket);
			if (btcMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("btc_usdt");
				params.setParamValue(btcMarket.toString());
				paramsMapper.updateParams(params);
			}

			BigDecimal ltcMarket = HttpUtil.getcoinRate("ltc_usdt");
			if (ltcMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("ltc_usdt");
				params.setParamValue(ltcMarket.toString());
				paramsMapper.updateParams(params);
			}

			BigDecimal ethMarket = HttpUtil.getcoinRate("eth_usdt");
			if (ethMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("eth_usdt");
				params.setParamValue(ethMarket.toString());
				paramsMapper.updateParams(params);
			}
			BigDecimal etcMarket = HttpUtil.getcoinRate("etc_usdt");
			if (etcMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("etc_usdt");
				params.setParamValue(etcMarket.toString());
				paramsMapper.updateParams(params);
			}
			BigDecimal hsrMarket = HttpUtil.getcoinRate("hsr_usdt");
			if (hsrMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("hsr_usdt");
				params.setParamValue(hsrMarket.toString());
				paramsMapper.updateParams(params);
			}
			BigDecimal qtumMarket = HttpUtil.getcoinRate("qtum_usdt");
			if (qtumMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("qtum_usdt");
				params.setParamValue(qtumMarket.toString());
				paramsMapper.updateParams(params);
			}
//			BigDecimal ensaMarket = HttpUtil.getcoinRate("ensa_btc");
//			if (ensaMarket.compareTo(new BigDecimal("0")) == 1) {
//				ensaMarket = ensaMarket.multiply(btcMarket).setScale(3,BigDecimal.ROUND_HALF_UP);
//				Params params = new Params();
//				params.setParamKey("ensa_usdt");
//				params.setParamValue(ensaMarket.toString());
//				paramsMapper.updateParams(params);
//			}
//			BigDecimal ptnMarket = HttpUtil.getcoinRateFromBihuex("ptn_usdt");
//			if (hsrMarket.compareTo(new BigDecimal("0")) == 1) {
//				Params params = new Params();
//				params.setParamKey("ptn_usdt");
//				params.setParamValue(ptnMarket.toString());
//				paramsMapper.updateParams(params);
//			}
		} catch (Exception e) {
			System.out.println("定时器异常");
		}

	}

	@Scheduled(cron = "0 */5 * * * ?")
	public void last() {
		try {

			BigDecimal btcMarket = HttpUtil.getcoinRate("btc_usdt");
			if (btcMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("btc_last");
				params.setParamValue(btcMarket.toString());
				paramsMapper.updateParams(params);
			}

			BigDecimal ltcMarket = HttpUtil.getcoinRate("ltc_usdt");
			if (ltcMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("ltc_last");
				params.setParamValue(ltcMarket.toString());
				paramsMapper.updateParams(params);
			}

			BigDecimal ethMarket = HttpUtil.getcoinRate("eth_usdt");
			if (ethMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("eth_last");
				params.setParamValue(ethMarket.toString());
				paramsMapper.updateParams(params);
			}

			BigDecimal etcMarket = HttpUtil.getcoinRate("etc_usdt");
			if (btcMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("etc_last");
				params.setParamValue(etcMarket.toString());
				paramsMapper.updateParams(params);
			}

			BigDecimal hsrMarket = HttpUtil.getcoinRate("hsr_usdt");
			if (ltcMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("hsr_last");
				params.setParamValue(hsrMarket.toString());
				paramsMapper.updateParams(params);
			}

			BigDecimal qtumMarket = HttpUtil.getcoinRate("qtum_usdt");
			if (ethMarket.compareTo(new BigDecimal("0")) == 1) {
				Params params = new Params();
				params.setParamKey("qtum_last");
				params.setParamValue(qtumMarket.toString());
				paramsMapper.updateParams(params);
			}

		} catch (Exception e) {
			System.out.println("定时器异常");
		}

	}
}
