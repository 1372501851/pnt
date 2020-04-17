package com.inesv.util;

import com.inesv.entity.Rate;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class RateUtil2 {


 

	public static String url = "http://api.nowapi.com/?app=finance.rate";

	public static BigDecimal getRate() {
		//String responseBodyString = getResponse();
		//System.out.println(responseBodyString);
		// Map<String, Object> respMap =
		// com.alibaba.fastjson.JSON.parseObject(responseBodyString, Map.class);
		// Map<String, Object> marketMap = (Map) respMap.get(symbol);
		// System.out.println(billWallet);
		BigDecimal realrate = null;
		try {
			//Rate rate = com.alibaba.fastjson.JSON.parseObject(responseBodyString, Rate.class);
			//realrate = rate.getResult() == null ? new BigDecimal(6.6190) : rate.getResult().getRate();
			return realrate.setScale(4, BigDecimal.ROUND_HALF_DOWN);
		} catch (Exception e) {
			// TODO: handle exception
			realrate = new BigDecimal(6.6190);
			return realrate.setScale(4, BigDecimal.ROUND_HALF_DOWN);
		}

	}
}
