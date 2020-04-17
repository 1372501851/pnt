package com.inesv.util.CoinAPI;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inesv.model.Address;
import com.inesv.model.TradeInfo;
import com.inesv.util.DateTools;
import com.inesv.util.HttpUtil;
import com.inesv.util.ValidataUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PNTCoinApi {
	private String rpcurl = "";
	private String rpcport = "";
	private String rpcuser = "";
	private String rpcpassword = "";
	private String walletpassphrase = "";
	public final static String CODE_100 = "100"; // 获取数据成功
	public final static String CODE_101 = "101"; // 获取数据失败
	public final static String CODE_102 = "102"; // 该账户不存在
	public final static String CODE_103 = "103"; // 创建账号失败,密码不能为空
	public final static String CODE_104 = "104"; // 正在同步区块，无法转账
	public final static String CODE_105 = "105"; // 发送方账户不存在
	public final static String CODE_106 = "106"; // 可用余额不足
	public final static String CODE_107 = "107"; // 导出成功
	public final static String CODE_108 = "108"; // 助记词错误
	public final static String CODE_109 = "109"; // 已经存在该钱包，无法导入;
	public final static String CODE_110 = "110"; // 导入成功
	public final static String CODE_111 = "111"; // 导入失败
	public final static String CODE_112 = "112"; // 加载账号成功
	public final static String CODE_113 = "113"; // 加载账号失败
	public final static String CODE_114 = "114"; // 交易hash不存在
	public final static String CODE_115 = "115"; // 正在同步区块，无法启动
	public final static String CODE_116 = "116"; // 铸造机启动成功
	public final static String CODE_117 = "117"; // 铸造机停止失败
	public final static String CODE_118 = "118"; // 铸造机停止成功
	public final static String CODE_119 = "119"; // 发行代币成功
	public final static String CODE_120 = "120"; // 挖矿中
	public final static String CODE_121 = "121"; // 未挖矿
	public final static String CODE_122 = "122"; // 操作成功
	public final static String CODE_200 = "200"; // 转账申请已提交
	public final static String CODE_201 = "201"; // 接收账户未产生交易，请提供钱包公钥
	public final static String CODE_301 = "301"; // 密码错误
	public final static String CODE_401 = "401"; // 公钥错误

	private PNTCoinApi(String RPC_URL, String RPC_PORT, String RPC_USERNAME,
			String RPC_PASSWORD, String DEFAULT_PASSWORD) {
		this.rpcurl = RPC_URL == null ? "" : RPC_URL;
		this.rpcport = RPC_PORT == null ? "" : RPC_PORT;
		this.rpcuser = RPC_USERNAME == null ? "" : RPC_USERNAME;
		this.rpcpassword = RPC_PASSWORD == null ? "" : RPC_PASSWORD;
		this.walletpassphrase = DEFAULT_PASSWORD == null ? ""
				: DEFAULT_PASSWORD;
	}

	private static PNTCoinApi api = null;
	private static final Object lock = new Object();

	public static PNTCoinApi getApi(Address address) {
		synchronized (lock) {
			if (addressChange(address)) {
				api = new PNTCoinApi(address.getAddress(), address.getPort(),
						address.getName(), address.getPassword(),
						address.getLockPassword());
			}
		}
		return api;
	}

	// 判断服务器地址是不是变了
	private static boolean addressChange(Address address) {
		if (address != null
				&& (api == null || !api.rpcurl.equals(address.getAddress())
						|| !api.rpcport.equals(address.getPort())
						|| !api.rpcpassword.equals(address.getPassword()) || !api.walletpassphrase
							.equals(address.getLockPassword()))) {
			return true;
		}
		return false;
	}

	// 备份钱包地址
	public String exportAddressJson(String address, String memo) {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("address", address);
			map.put("passWord", walletpassphrase);
			map.put("mnemonic", memo);
			String result = HttpUtil.sendPost(rpcurl + ":" + rpcport
					+ "/WalletController/exportWallet.do", map, 1000);
			JSONObject json = JSONObject.parseObject(result);
			String code = json.getString("code");
			if (CODE_107.equals(code)) {
				JSONObject dataJson = json.getJSONObject("data");
				if (dataJson != null
						&& !ValidataUtil.isNull(dataJson.toJSONString())) {
					JSONObject addressJson = dataJson.getJSONObject("jsonStr");
					if (addressJson != null
							&& !ValidataUtil.isNull(addressJson.toJSONString())) {
						return addressJson.toJSONString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// 导入钱包地址
	public String importAddressJson(String json, String memo) {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("mnemonicText", memo);
			map.put("jsonStr", json);
			String result = HttpUtil.sendPost(rpcurl + ":" + rpcport
					+ "/WalletController/importWallet.do", map, 1000);
			JSONObject jsonResult = JSONObject.parseObject(result);
			String code = jsonResult.getString("code");

			if (CODE_110.equals(code) || CODE_109.equals(code)) {
				JSONObject dataJson = jsonResult.getJSONObject("data");
				if (dataJson != null
						&& !ValidataUtil.isNull(dataJson.toJSONString())) {
					String addressStr = dataJson.getString("address");
					if (!ValidataUtil.isNull(addressStr)) {
						return addressStr;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// 新增钱包地址
	public String createAddress() {
		try {
			if (ValidataUtil.isNull(walletpassphrase)) {
				// 密码为空则创建失败
				return "";
			}
			// System.out.println("password" + walletpassphrase);
			Map<String, Object> map = new HashMap<>();
			map.put("passWord", walletpassphrase);
			String result = HttpUtil.sendPost(rpcurl + ":" + rpcport
					+ "/WalletController/createAccount.do", map, 1000);
			JSONObject jsonResult = JSONObject.parseObject(result);
			String code = jsonResult.getString("code");
			if (CODE_100.equals(code)) {
				JSONObject dataJson = jsonResult.getJSONObject("data");
				if (dataJson != null
						&& !ValidataUtil.isNull(dataJson.toJSONString())) {
					String addressStr = dataJson.getString("address");
					if (!ValidataUtil.isNull(addressStr)) {
						return addressStr;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// 新增钱包地址和私钥
	public String createAddressAndKey() {
		try {
			if (ValidataUtil.isNull(walletpassphrase)) {
				// 密码为空则创建失败
				return "";
			}
			Map<String, Object> map = new HashMap<>();
			map.put("passWord", walletpassphrase);
			String result = HttpUtil.sendPost(rpcurl + ":" + rpcport
					+ "/WalletController/createAccount.do", map, 1000);
			JSONObject jsonResult = JSONObject.parseObject(result);
			String code = jsonResult.getString("code");
			if (CODE_100.equals(code)) {
				JSONObject dataJson = jsonResult.getJSONObject("data");
				if (dataJson != null
						&& !ValidataUtil.isNull(dataJson.toJSONString())) {
					String addressStr = dataJson.getString("address");
					String privateKey = dataJson.getString("privateKey");
					if (!ValidataUtil.isNull(addressStr)
							&& !ValidataUtil.isNull(privateKey)) {
						return dataJson.toJSONString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// 钱包地址转账
	public String sendTransaction(String out, String in, String value,
			String pubKey, String remark, String fee, String tokenName) {
		if (ValidataUtil.isNull(remark)) {
			remark = "MOC转账";
		}
		String url = rpcurl + ":" + rpcport
				+ "/WalletController/transferAccounts.do";
		Map<String, Object> map = new HashMap<String, Object>();
		// System.out.println("outaddress" + out);
		map.put("transFrom", out);
		map.put("transTo", in);
		map.put("transValue", value);
		map.put("fee", fee);
		map.put("passWord", walletpassphrase);
		map.put("transToPubkey", pubKey);
		map.put("remark", remark);
		map.put("tokenName", tokenName.toLowerCase());
		// 转账结果
		String resultJsonStr = HttpUtil.sendPost(url, map, 8000);
		log.info("调用Moc转账返回信息：{}", resultJsonStr);
		JSONObject resultJson = JSONObject.parseObject(resultJsonStr);
		String resultCode = resultJson.getString("code");
		// System.out.println("ptn result params : " + resultJson);
		if (CODE_200.equals(resultCode)) {
			JSONObject resultDataJson = resultJson.getJSONObject("data");
			return resultDataJson.getString("transHash");
		} else if (CODE_201.equals(resultCode)) {
			String infoJson = getAddressInfos(in, 1, 1, tokenName.toLowerCase());
			JSONObject json = JSONObject.parseObject(infoJson);
			String infoCode = json.getString("code");
			if (!CODE_100.equals(infoCode.toString())) {
				return "";
			}
			JSONObject dataJson = json.getJSONObject("data");
			JSONObject accountInfo = dataJson.getJSONObject("accountMap");
			// 获取公钥
			String pubKey_ = accountInfo.getString("pubKey");
			// System.out.println("pubKey" + pubKey_);
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("transFrom", out);
			map1.put("transTo", in);
			map1.put("transValue", value);
			map1.put("fee", fee);
			map1.put("passWord", walletpassphrase);
			map1.put("transToPubkey", pubKey_);
			map1.put("remark", remark);
			map1.put("tokenName", tokenName);
			// 转账结果
			String resultJsonStr1 = HttpUtil.sendPost(url, map1, 1000);
			JSONObject resultJson1 = JSONObject.parseObject(resultJsonStr1);
			String resultCode1 = resultJson1.getString("code");
			if (!CODE_200.equals(resultCode1)) {
				return "";
			}
			JSONObject resultDataJson1 = resultJson1.getJSONObject("data");
			return resultDataJson1.getString("transHash");
		} else {
			return "";
		}
	}

	// 获取交易记录
	public String getAddressTradeInfos(String pubKey, int pageNumber,
			int pageSize, String tokenName) {
		String url = "39.108.168.179:7877"
				+ "/WalletController/getTransactionByPubkey.do";

		// System.out.println(url);
		Map<String, String> map = new HashMap<String, String>();
		map.put("pageNumber", pageNumber + "");
		map.put("pageSize", pageSize + "");
		map.put("tokenName", tokenName);
		map.put("pubKey", pubKey);

		String result = HttpUtil.sendGet(url, map, 1000);
		if (ValidataUtil.isNull(result)) {
			return "";
		}
		return result;
	}

	public String getAddressTradeInfosNew(String pubKey, int pageNumber,
									   int pageSize, String tokenName, String ieType) {
		String url = "122.114.223.247:7877"
				+ "/WalletController/getTransactionByPubkey.do";

		// System.out.println(url);
		Map<String, String> map = new HashMap<String, String>();
		map.put("pageNumber", pageNumber + "");
		map.put("pageSize", pageSize + "");
		map.put("tokenName", tokenName);
		map.put("pubKey", pubKey);
		map.put("ieType", ieType);

		String result = HttpUtil.sendGet(url, map, 1000);
		if (ValidataUtil.isNull(result)) {
			return "";
		}
		return result;
	}

	// 获取账号信息
	public String getAddressInfos(String Address, int pageNumber, int pageSize,
			String tokenName) {
		String url = rpcurl + ":" + rpcport
				+ "/WalletController/getAccountInfo.do";

		// System.out.println(url);
		Map<String, String> map = new HashMap<String, String>();
		map.put("address", Address);
		map.put("pageNumber", pageNumber + "");
		map.put("pageSize", pageSize + "");
		map.put("tokenName", tokenName.toLowerCase());

		String result = HttpUtil.sendGet(url, map, 1000);
		if (ValidataUtil.isNull(result)) {
			return "";
		}
		return result;
	}

	// 获取地址资产
	public String getBalance(String address, String tokenName) {
		String jsonStr = getAddressInfos(address, 1, 1, tokenName.toLowerCase());
		if (ValidataUtil.isNull(jsonStr)) {
			return "0";
		}
		JSONObject jsonObj = JSONObject.parseObject(jsonStr);
		String code = jsonObj.getString("code");
		if (CODE_100.equalsIgnoreCase(code)) {
			return jsonObj.getJSONObject("data").getJSONObject("accountMap")
					.getBigDecimal("balance").toString();
		}
		return "0";
	}

	public String getAllAddress() {
		String url = rpcurl + ":" + rpcport
				+ "/WalletController/getAllAccount.do";

		// System.out.println(url);
		Map<String, String> map = new HashMap<String, String>();
		map.put("tokenName", "ptn");
		String result = HttpUtil.sendGet(url, map, 1000);
		if (ValidataUtil.isNull(result)) {
			return "";
		}
		return result;
	}

	public String getPublicKey(String address, String tokenName) {
		String url = rpcurl + ":" + rpcport
				+ "/WalletController/getAccountInfo.do";

		Map<String, String> map = new HashMap<String, String>();
		map.put("address", address);
		map.put("pageNumber", 1 + "");
		map.put("pageSize", 1 + "");
		map.put("tokenName", tokenName.toLowerCase());

		String result = HttpUtil.sendGet(url, map, 1000);
		JSONObject json = JSON.parseObject(result);
		if (100 != json.getInteger("code")) {
			// System.out.println("ptn钱包服务器返回的信息：" + result);
			return "";
		}
		JSONObject data = json.getJSONObject("data");
		JSONObject account = data.getJSONObject("accountMap");
		String publicKey = account.getString("pubKey");
		return publicKey;
	}

	/**
	 * 根据区块高度获取流水列表
	 * @param blockHeight
	 * @return
	 */
	public List<TradeInfo> getTransactionsByBlockHeight(int blockHeight) {
		List<TradeInfo> list = new ArrayList<>();

		String url = rpcurl + ":" + rpcport + "/WalletController/getBlockInfoByBlockHeightDealWithData.do";
		Map<String, String> map = new HashMap<>();
		map.put("blockHeight", String.valueOf(blockHeight));
		String result = HttpUtil.sendGet(url, map, 1000);
		JSONObject json = JSON.parseObject(result);
		if (100 != json.getInteger("code")) {
			return list;
		}

		JSONObject data = json.getJSONObject("data");
		JSONObject block = data.getJSONObject("block");
		JSONArray transactions = block.getJSONArray("blockTransactions");
		String tokenName;
		for(int i=0;i<transactions.size();i++){
			JSONObject transaction = transactions.getJSONObject(i);
			tokenName=transaction.getString("tokenName");
			int transType = transaction.getInteger("transType");
//			//排除合约流水
//			if (3 == transType|| 4 == transType){
//				continue;
//			}else if(!tokenName.equalsIgnoreCase("moc") ){
//				continue;
//			}

			if (3 == transType|| 4 == transType || 5 == transType|| 7 == transType){
				continue;
			}
			TradeInfo tradeInfo = new TradeInfo();
			//交易货币名称
			tradeInfo.setTradeTokenName(tokenName);
			tradeInfo.setOutAddress(transaction.getString("from"));
			tradeInfo.setInAddress(transaction.getString("to"));
			tradeInfo.setTradeNum(new BigDecimal(transaction.getString("value")));
			tradeInfo.setHash(transaction.getString("txHash"));
			tradeInfo.setDate(DateTools.stringToDate(transaction.getString("date")));
			tradeInfo.setRatio(new BigDecimal(transaction.getString("fee")));
			list.add(tradeInfo);
		}
		return list;
	}

	public static void main(String[] args) {
		Address address = new Address();
		address.setPort("7877");
		address.setLockPassword("123456");
		address.setAddress("47.75.248.144");

		PNTCoinApi api = PNTCoinApi.getApi(address);
		api.getTransactionsByBlockHeight(1);
	}

	public boolean addressPubKeyIsRight(String address, String pubKey) {
		boolean isTrue = false;
		try {
			String infoStr = getAddressInfos(address, 1, 1, "ptn");
			JSONObject infoJson = JSONObject.parseObject(infoStr);
			String code = infoJson.getString("code");
			if (CODE_100.equals(code)) {
				String pubKey_ = infoJson.getJSONObject("data")
						.getJSONObject("accountMap").getString("pubKey");
				isTrue = pubKey.equals(pubKey_);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isTrue;
	}
}
