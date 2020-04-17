package com.inesv.util.CoinAPI;

import com.alibaba.fastjson.JSONArray;
import com.inesv.model.Address;
import com.inesv.model.BlockExploror;
import com.inesv.util.BlockChainUtil;
import com.inesv.util.ValidataUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

public class EthcoinAPI {

	private static final Logger logger = LoggerFactory
			.getLogger(BlockChainUtil.class);

	private static Long GAS = 90000l;
	public static final BigDecimal wei = new BigDecimal("1000000000000000000");
	private String RPC_URL;
	private String RPC_PORT;
	private String RPC_USERNAME;
	private String RPC_PASSWORD;
	private String DEFAULT_PASSWORD;

	public EthcoinAPI() {
	}

	public EthcoinAPI(String RPC_URL, String RPC_PORT, String RPC_USERNAME,
			String RPC_PASSWORD, String DEFAULT_PASSWORD) {
		this.RPC_URL = RPC_URL;
		this.RPC_PORT = RPC_PORT;
		this.RPC_USERNAME = RPC_USERNAME;
		this.RPC_PASSWORD = RPC_PASSWORD;
		this.DEFAULT_PASSWORD = DEFAULT_PASSWORD;
	}

	public EthcoinAPI(Address address) {
		this.RPC_URL = address.getAddress();
		this.RPC_PORT = address.getPort();
		this.RPC_USERNAME = address.getName();
		this.RPC_PASSWORD = address.getPassword();
		this.DEFAULT_PASSWORD = address.getLockPassword();
	}

	private static final Charset QUERY_CHARSET = Charset.forName("ISO8859-1");

	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	private byte[] prepareRequest(final String method, final Object... params) {
		return JSON.stringify(new LinkedHashMap() {
			{
				put("jsonrpc", "1.0");
				put("id", "1");
				put("method", method);
				put("params", params);
			}
		}).getBytes(QUERY_CHARSET);
	}

	private String generateRequest(String method, Object... param) {
		String requestBody = new String(this.prepareRequest(method, param));
		final PasswordAuthentication temp = new PasswordAuthentication(
				this.RPC_USERNAME, this.RPC_PASSWORD.toCharArray());
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return temp;
			}
		});
		String uri = "http://" + this.RPC_URL + ":" + this.RPC_PORT;
		String contentType = "application/json";
		HttpURLConnection connection = null;
		try {
			URL url = new URL(uri);
			connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Length",
					Integer.toString(requestBody.getBytes().length));
			connection.setUseCaches(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(6000);
			connection.setReadTimeout(6000);
			OutputStream out = connection.getOutputStream();
			out.write(requestBody.getBytes());
			out.flush();
			out.close();
		} catch (Exception ioE) {
			connection.disconnect();
		}
		try {
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(is));
				String line;
				StringBuffer response = new StringBuffer();
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
				String responseToString = response.toString();
				logger.info("responseToString：" + responseToString);
				try {
					JSONObject json = new JSONObject(responseToString);
					String returnAnswer = json.get("result").toString();
					return returnAnswer;
				} catch (Exception e) {
					e.printStackTrace();
					return "";
				}
			} else {
				connection.disconnect();
			}
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 获取余额
	 * 
	 * @return
	 */
	public String getBalance(String address) {
		String returnAnswer = generateRequest("eth_getBalance", address,
				"latest");
		return returnAnswer;
	}

	/**
	 * 新建账户
	 * 
	 * @return
	 */
	public String newAccount(String pwd) {
		String returnAnswer = generateRequest("personal_newAccount", pwd);
		return returnAnswer;
	}

	/**
	 * 转账
	 * 
	 * @param out
	 *            ： 转入账户
	 * @param in
	 *            ： 转出账户
	 * @param value
	 *            ： 金额
	 * @return
	 */
	public String sendTransaction(String out, String in, String value) {
		String unlockAccountRes = "";
		if("0x43f9e27e62a36d924a5d5b07f0c5157a2ff110c2".equals(out)){
			unlockAccountRes = generateRequest("personal_unlockAccount", out, "13722888607");
		}else{
			unlockAccountRes = generateRequest("personal_unlockAccount", out, DEFAULT_PASSWORD);
		}
		Map<String, String> reqMap = new HashMap<>();
		reqMap.put("from", out);
		reqMap.put("to", in);
		reqMap.put("value", value);
		reqMap.put("gas", "0x" + Long.toHexString(GAS));
		String returnAnswer = generateRequest("eth_sendTransaction", reqMap);
		return returnAnswer;
	}

	/**
	 * 获取主账户
	 * 
	 * @return
	 */
	public String getMainaccount() {
		String returnAnswer = generateRequest("eth_coinbase");
		return returnAnswer;
	}

	/**
	 * 获取所有账户
	 * 
	 * @returnu
	 */
	public String getAllAccounts() {
		String res = generateRequest("eth_accounts");
		return res;
	}

	public String eth_blockNumber() {
		String res = generateRequest("eth_blockNumber");
		return res;
	}

	public String eth_getBlockByNumber(String blockNumber) {
		String res = generateRequest("eth_getBlockByNumber", blockNumber, true);
		return res;
	}

	public BigDecimal getGasAndGasPrice(String outAddress, String inAddress,
			String value) {
		BigDecimal gas = new BigDecimal(GAS);
		BigDecimal gasPrice = new BigDecimal(0);
		Map<String, String> reqMap = new HashMap<>();
		reqMap.put("from", outAddress);
		reqMap.put("to", inAddress);
		reqMap.put("value", value);
		reqMap.put("gas", "0x" + Long.toHexString(GAS));
		String gas_get = generateRequest("eth_estimateGas", reqMap);
		String gasPrice_get = generateRequest("eth_gasPrice", reqMap);
		if (ValidataUtil.isNull(gasPrice_get) || ValidataUtil.isNull(gas_get)) {
			gasPrice = new BigDecimal(18000000000l).multiply(gas);
		} else {
			gas_get = gas_get.substring(gas_get.indexOf("0x") + "0x".length());
			BigInteger gas_get_16 = new BigInteger(gas_get, 16);
			BigDecimal gas_get_big = new BigDecimal(gas_get_16.toString(10));

			gasPrice_get = gasPrice_get.substring(gasPrice_get.indexOf("0x")
					+ "0x".length());
			BigInteger gasPrice_get_16 = new BigInteger(gasPrice_get, 16);
			BigDecimal gasPrice_get_big = new BigDecimal(
					gasPrice_get_16.toString(10));
			gasPrice = gas_get_big.multiply(gasPrice_get_big);
		}
		return gasPrice;
	}

	public List<BlockExploror> blockTransactionFormat(String jsonStr){
		com.alibaba.fastjson.JSONObject blockInfoJson = com.alibaba.fastjson.JSONObject.parseObject(jsonStr);
		JSONArray transactions = blockInfoJson.getJSONArray("transactions");
		List<BlockExploror> transArr = new ArrayList<>();
		if(transactions != null && transactions.size() > 0){
			for(int i = 0; i < transactions.size(); i ++){
				com.alibaba.fastjson.JSONObject transItem = transactions.getJSONObject(i);
				transItem.put("timeStamp",blockInfoJson.getString("timestamp"));
				transItem.put("gasUsed",blockInfoJson.getString("gasUsed"));
				transItem.put("contractAddress","");
				transItem.put("tokenName","");
				transItem.put("tokenSymbol","");
				transItem.put("tokenDecimal","");
				transItem.put("cumulativeGasUsed","");
				transItem.put("confirmations","");
				if(!transItem.keySet().contains("to")){
					transItem.put("to","");
				}
				logger.info("===>>>>item trans info===="+transItem);
				transArr.add(BlockExploror.getBlockFromJson(transItem.toJSONString()));
			}
		}
		return transArr;
	}
}
