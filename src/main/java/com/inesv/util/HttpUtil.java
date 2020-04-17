package com.inesv.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inesv.entity.Kline;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HttpUtil {

	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

	// private HttpUtil() {
	/**
	 * 发送Post请求
	 *
	 * @param url
	 *            : 请求的连接
	 * @param params
	 *            ： 请求参数，无参时传null
	 * @return
	 */
	public static String sendPostToJson(String url, Map<String, Object> params) {
		HttpRequest request = HttpRequest.post(url);
		request.contentType("application/json");
		request.charset("utf-8");

		// 参数详情
		if (params != null) {
			request.body(JSON.toJSONString(params));
		}

		HttpResponse response = request.send();
		String respJson = response.bodyText();
		return respJson;
	}

	// }

	/**
	 * 查询币种行情
	 *
	 * @param coinType
	 * @return
	 */
	public static BigDecimal getcoinRate(String coinType) {
		String responbody="";
		BigDecimal rate =new BigDecimal("0");
		try {
			if (coinType.equals("ensa_btc")){
				responbody = HttpUtil.sendGet("http://api.exxvip.com/data/v1/ticker?currency=" + coinType, null);
			}else {
				responbody = HttpUtil.sendGet("http://api.zb.live/data/v1/ticker?market=" + coinType, null);
			}
			JSONObject jsonObject = JSONObject.parseObject(responbody);
			JSONObject jsonObject1 = jsonObject.getJSONObject("ticker");
			String str = jsonObject1.getString("last");
			if (StringUtils.isBlank(str)) {
				return rate;
			}
			rate = new BigDecimal(str);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("行情接口返回数据--" + responbody);
		}
		return rate;

	}

	public static BigDecimal getcoinRateFromBihuex(String coinType) {
		String responbody="";
		BigDecimal rate =new BigDecimal("0");
		try {
			responbody = HttpUtil.sendGet("http://home.bihuex.com/mobile/api/ticker/getTicker?market=" + coinType, null);
			JSONObject jsonObject = JSONObject.parseObject(responbody);
			if(jsonObject.getIntValue("error") == 0){
				JSONObject data = jsonObject.getJSONObject("data");
				JSONObject cType = data.getJSONObject(coinType);
				String last = cType.getString("last");
				rate = new BigDecimal(last);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("行情接口http://home.bihuex.com返回数据--" + responbody);
		}
		return rate;

	}

	/**
	 * 代币K线图数据 coinType，代币类型:btc_usdt eth_usdt ltc_usdt since,数据开始时间戳
	 * size,返回记录数,至多1000条 type,时间区间：格式为1min 3min 5min 15min 30min 1day 3day 1week
	 * 1hour 2hour 4hour 6hour 12hour
	 *
	 * @param market
	 * @param since
	 * @param size
	 * @param type
	 * @return json
	 */
	public static String getcoinKcharts(String market, Long since, Long size, String type) {
		String url = "http://api.zb.live/data/v1/kline?market=" + market + "&since=" + since + "&size=" + size + "&type="
				+ type;
		String responbody = HttpUtil.sendGet(url, null);
		return responbody;
	}

	/**
	 * 查询两种货币汇率
	 *
	 * @param financeOne
	 * @param financeTwo
	 * @return
	 */
	public static BigDecimal getFinanceRate(String financeOne, String financeTwo) {
		String responbody = HttpUtil.sendGet("http://api.k780.com/?app=finance.rate&scur=" + financeOne + "&tcur="
				+ financeTwo + "&appkey=31652&sign=ef25492d3555e49965f08ed956db6ad0", null);
		log.info("货币汇率返回数据--" + responbody);
		JSONObject jsonObject = JSONObject.parseObject(responbody);
		String success = jsonObject.getString("success");
		// 调取汇率成功
		if ("1".equals(success)) {
			JSONObject jsonObject1 = jsonObject.getJSONObject("result");
			String str = jsonObject1.getString("rate");
			if (StringUtils.isBlank(str)) {
				BigDecimal bigDecimal = new BigDecimal(0);
				return bigDecimal;
			}
			BigDecimal bigDecimal = new BigDecimal(str);
			return bigDecimal;
		}
		// 失败
		log.warn("获取货币汇率失败");
		return new BigDecimal("0");
	}

	/**
	 * 发送Get请求
	 *
	 * @param url
	 *            : 请求的连接
	 * @param params
	 *            ： 请求参数，无参时传null
	 * @return
	 */
	public static String sendGet(String url, Map<String, String> params, int timeOut) {
		try {
			HttpRequest request = HttpRequest.get(url);
			request.connectionTimeout(timeOut);
			if (params != null) {
				request.query(params);
			}
			HttpResponse response = request.send();
			String respJson = response.bodyText();
			return respJson;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 发送Get请求
	 *
	 * @param url
	 *            : 请求的连接
	 * @param params
	 *            ： 请求参数，无参时传null
	 * @return
	 */
	public static String sendGet(String url, Map<String, String> params) {
		HttpRequest request = HttpRequest.get(url);
		request.connectionTimeout(2000);
		if (params != null) {
			request.query(params);
		}
		HttpResponse response = request.send();
		String respJson = response.bodyText();
		return respJson;
	}

	/**
	 * 发送json-Post请求
	 *
	 * @param url
	 *            请求的连接
	 * @param params
	 *            ： 请求参数，无参时传null
	 * @return
	 */
	public static String sendPost(String url, Map<String, Object> params, int timeout) {
		HttpRequest request = HttpRequest.post(url);
		request.contentType("application/json");
		request.charset("utf-8");
		request.timeout(timeout);
		// 参数详情
		if (params != null) {
			request.form(params);
		}
		HttpResponse response = request.send();
		String respJson = response.bodyText();
		return respJson;
	}

	/**
	 * 普通post请求
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static String sendPost_common(String url, Map<String, Object> params) {
		HttpRequest request = HttpRequest.post(url);
		// 参数详情
		if (params != null) {
			request.form(params);
		}
		HttpResponse response = request.send();
		String respJson = response.bodyText();
		return respJson;
	}

	/**
	 * 发送Delete请求
	 *
	 * @param url
	 *            : 请求的连接
	 * @param params
	 *            ： 请求参数，无参时传null
	 * @return
	 */
	public static String sendDelete(String url, Map<String, Object> params) {
		HttpRequest request = HttpRequest.delete(url);

		if (params != null) {
			request.form(params);
		}
		HttpResponse response = request.send();
		String respJson = response.bodyText();
		return respJson;
	}

	/**
	 * post请求数据
	 *
	 * @param url
	 * @param param
	 * @return
	 */
	public static String postData(String url, String param) {
		StringBuffer sb = new StringBuffer();
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStreamWriter oos = new OutputStreamWriter(conn.getOutputStream());
			oos.write(param);
			oos.flush();
			InputStream ips = conn.getInputStream();
			byte[] bts = new byte[1024];
			int len;
			while ((len = ips.read(bts)) != -1) {
				sb.append(new String(bts, 0, len, "UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String getMarket(String coinType) {
		String responbody = HttpUtil.sendGet("http://api.zb.live/data/v1/ticker?market=" + coinType, null);
		log.info("行情接口返回数据--" + responbody);
		return responbody;
	}

	// K线图数据
	public static String Kline(String coinType, String type) {
		String responbody = "";
		try{
			responbody = HttpUtil.sendGet("http://api.zb.live/data/v1/kline?market=" + coinType + "&type=" + type,
					null);
			JSONObject json = JSON.parseObject(responbody);
			if (ValidataUtil.isNull(json.getString("error"))){
				return responbody;
			}else {
				return "";
			}
		}catch (Exception e){
			e.printStackTrace();
			return "";
		}
//		log.info("K线接口返回数据--" + responbody);
//		return responbody;
	}

	public static void main(String[] args) {
		String result = Kline("ltcusdt","1week");

		JSONObject resultJson = JSONObject.parseObject(result);
		JSONArray jsonData = resultJson.getJSONArray("data");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		BigDecimal[] kline = null;
		Kline data = new Kline();
		List<BigDecimal[]> dataList = new ArrayList<BigDecimal[]>();
		List<String> dates = new ArrayList<String>();
		List<BigDecimal> volumes = new ArrayList<BigDecimal>();
		for (int i = 0; i < jsonData.size(); i++) {

			String[] str = jsonData.get(i).toString().split(",");
			String dateStr = str[0].split("\\[")[1];
			String date = sdf.format(new Date(Long.parseLong(dateStr)));
			kline[0] = new BigDecimal(str[2]);
			kline[1] = new BigDecimal(str[1]);
			kline[2] = new BigDecimal(str[4]);
			kline[3] = new BigDecimal(str[5]);
			kline[4] = new BigDecimal(str[3]);
			dataList.add(kline);
			dates.add(date);
			volumes.add(new BigDecimal(str[5]));
		}
		data.setVolumes(volumes);
		data.setData(dataList);
		data.setDates(dates);
	}

	public static String getUsdtQcMarket(String coinType) {
		String responbody = HttpUtil.sendGet("http://api.zb.live/data/v1/ticker?market=" + coinType, null);
		log.info("行情接口返回数据--" + responbody);
		return responbody;
	}
}