package com.inesv.util.ZbApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inesv.util.GsonUtils;
import com.inesv.util.HttpUtil;
import com.inesv.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 使用区块浏览器api获取交易记录
 */
public class BlockchainBrowserUtil {
	private final static String ETHERSCAN_URL = "http://api.etherscan.io/api?module=account&action=txlist&address=@address&startblock=0&endblock=99999999&sort=asc&apikey=YourApiKeyToken";

	private static Logger logger = LoggerFactory.getLogger(BlockchainBrowserUtil.class);

	/**
	 * ETH交易数据
	 * 
	 * @param address
	 * @return
	 */
	public static List<Map<String, String>> getEthTrans(String address) {
		String responbody = HttpUtil.sendGet(
				"http://api.etherscan.io/api?module=account&action=txlist&address="
						+ address + "&sort=asc", null);
		Map<String, String> resultMap = (Map<String, String>) JSON.parseObject(
				responbody, Map.class);
		Object result = resultMap.get("result");
		if (result == null) {
			return null;
		}

		List<Map<String, String>> trans = JSON.parseObject(result.toString(),
				List.class);
		return trans;
	}

	/**
	 * 获取ETC交易数据
	 * 
	 * @param addresss
	 * @return
	 */
	public static List<Map<String, String>> getEtcTrans(String addresss) {
		String responbody = null;
		try {
			responbody = HttpUtil.sendGet(
					"https://etcchain.com/api/v1/getTransactionsByAddress?address="
							+ addresss + "&sort=desc", null);
			System.out.println("responbody:" + responbody);
		} catch (Exception e) {
			System.out.println("获取ETC交易数据异常...");
			return null;
		}
		List<Map<String, String>> trans = JSON.parseObject(responbody,
				List.class);
		return trans;

	}

	/**
	 * 使用区块浏览器提供的接口获取交易记录
	 * @param address
	 * @return
     */
	public static JSONArray geTransByEtherscan(String address) {
		try {
			String responbody = HttpUtils.doGet(ETHERSCAN_URL.replace("@address", address));
			System.out.println(GsonUtils.toJson(responbody));
			JSONObject jsonObject = JSONObject.parseObject(responbody);
			if(jsonObject == null){
				return null;
			}
			return jsonObject.getJSONArray("result");
		} catch (Exception e) {
			logger.error("获取钱包地址"+address+"交易数据异常", e);
			return null;
		}
	}
}
