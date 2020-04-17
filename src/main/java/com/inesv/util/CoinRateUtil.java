package com.inesv.util;

import com.inesv.entity.Bitstamp;
import com.inesv.entity.CoinWallet;
import com.inesv.model.Params;
import com.inesv.service.ParamsService;
import com.inesv.util.ZbApi.zbApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;

@Component
public class CoinRateUtil {
	@Autowired
	private ParamsService paramService;

	private static ParamsService paramService2;
	// private static IParamService paraService;

	
	@PostConstruct
	public void init() {
		paramService2 = this.paramService;

	}
 

	public static CoinWallet getRateByCoin(String symbol) {
		

		String responseBodyString = "";
		CoinWallet coinWallet = new CoinWallet();
		Bitstamp bitstamp = null;
		
		RateUtil2 rateutil= new RateUtil2();
		//BigDecimal rate = rateutil.getRateNotStatic();//汇率
	 
 
		if (symbol.equalsIgnoreCase("pnt")||symbol.equalsIgnoreCase("pnt_cny")) {
			CoinWallet itccoinWallet = new CoinWallet();
			itccoinWallet.setAmount(new BigDecimal(0));
			itccoinWallet.setLevel(new BigDecimal(0));
			itccoinWallet.setP_high(new BigDecimal(0));
			itccoinWallet.setP_last(new BigDecimal(0));
			itccoinWallet.setPlow(new BigDecimal(0));
			if (symbol.equalsIgnoreCase("pnt")||symbol.equalsIgnoreCase("pnt_cny")) {
				//String name = "EC";
				Params param = paramService2.queryByKey(symbol);
				if (param != null) {
					if (param.getParamValue() != null && !"".equals(param.getParamValue())) {
						itccoinWallet.setP_new(new BigDecimal(param.getParamValue()));
					} else {
						itccoinWallet.setP_new(new BigDecimal(0.8));
					}
				} else {
					itccoinWallet.setP_new(new BigDecimal(0.8));
				}
			} else {
				itccoinWallet.setP_new(new BigDecimal(0.8));
			}
			itccoinWallet.setP_open(new BigDecimal(0.8));
			itccoinWallet.setSymbol(symbol);
			itccoinWallet.setTotal(new BigDecimal(5677));
			return itccoinWallet;
		}else if(symbol.equalsIgnoreCase("ecz")){  //行情固定
			coinWallet.setP_new(new BigDecimal(6.7));
			coinWallet.setP_high(new BigDecimal(6.7));
			coinWallet.setP_open(new BigDecimal(6.7));
			coinWallet.setAmount(new BigDecimal(0));
			coinWallet.setPlow(new BigDecimal(6.7));
		}else {
		 
			bitstamp = getResponse_ZB(symbol);
 
			coinWallet.setP_new(bitstamp.getLast());
			coinWallet.setP_high(bitstamp.getHigh());
			coinWallet.setP_open(bitstamp.getOpen());
			coinWallet.setAmount(bitstamp.getVolume());
			coinWallet.setPlow(bitstamp.getLow());
		} 
		return coinWallet;
	}

 
	
	
	private static Bitstamp getResponse_ZB(String symbol) {
		Bitstamp bitstamp = new Bitstamp();
 
		try {
			zbApi zb = new zbApi();
			String response = zb.testTicker(symbol);
			Map<String, Object> resMap = com.alibaba.fastjson.JSON.parseObject(response, Map.class);
			Map<String, Object> ticker = com.alibaba.fastjson.JSON.parseObject(resMap.get("ticker").toString(),
					Map.class);
			BigDecimal last = new BigDecimal(ticker.get("last").toString());
			BigDecimal high = new BigDecimal(ticker.get("high").toString());
			BigDecimal low = new BigDecimal(ticker.get("low").toString());
			BigDecimal buy = new BigDecimal(ticker.get("buy").toString());
			BigDecimal sell = new BigDecimal(ticker.get("sell").toString());
			BigDecimal vol = new BigDecimal(ticker.get("vol").toString());
			bitstamp.setHigh(high);
			bitstamp.setLast(last);
			bitstamp.setLow(low);
			bitstamp.setOpen(low);
			bitstamp.setVolume(vol);
		} catch (Exception e) {
			e.printStackTrace();
			bitstamp.setHigh(new BigDecimal(0d));
			bitstamp.setLast(new BigDecimal(0d));
			bitstamp.setLow(new BigDecimal(0d));
			bitstamp.setOpen(new BigDecimal(0d));
			bitstamp.setVolume(new BigDecimal(0d));
		}
		return bitstamp;

	}

	public static void main(String[] args) {

 
		/*
		 * Bitstamp res = CoinRateUtil.getResponse_okcom(ETC); System.out.println(res);
		 */
	}

}
