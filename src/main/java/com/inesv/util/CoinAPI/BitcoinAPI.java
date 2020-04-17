package com.inesv.util.CoinAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

public class BitcoinAPI {

	private String rpcurl;
	private String rpcport;
	private String rpcuser;
	private String rpcpassword;
	private String walletpassphrase;

	public BitcoinAPI() {
	}

	public BitcoinAPI(String rpcurl, String rpcport, String rpcuser,
			String rpcpassword, String walletpassphrase) {
		this.rpcurl = rpcurl;
		this.rpcport = rpcport;
		this.rpcuser = rpcuser;
		this.rpcpassword = rpcpassword;
		this.walletpassphrase = walletpassphrase;
	}

	public String getblockcount() {
		String returnAnswer = generateRequest("getblockcount");
		return returnAnswer;
	}

	public String getdifficulty() {
		String returnAnswer = generateRequest("getdifficulty");
		return returnAnswer;
	}

	public String getgenerate() {
		String returnAnswer = generateRequest("getgenerate");
		return returnAnswer;
	}

	public String getstakinginfo() {
		String returnAnswer = generateRequest("getstakinginfo");
		return returnAnswer;
	}

	public String getinfo() {
		String returnAnswer = generateRequest("getinfo");
		return returnAnswer;
	}

	public String getbestblockhash() {
		String returnAnswer = generateRequest("getrawchangeaddress");
		return returnAnswer;
	}

	public String getrawchangeaddress() {
		String returnAnswer = generateRequest("getrawchangeaddress");
		return returnAnswer;
	}

	public String getbalance() {
		String returnAnswer = generateRequest("getbalance");
		return returnAnswer;
	}

	public String getbalanceByAccount(String address) {
		String returnAnswer = generateRequest("getbalance", address);
		return returnAnswer;
	}

	public String listreceivedbyaddress() {
		String returnAnswer = generateRequest("listreceivedbyaddress");
		return returnAnswer;
	}

	public String openwallet() {
		String returnAnswer = generateRequest("walletpassphrase",
				this.walletpassphrase, 600);
		return returnAnswer;
	}

	public String closewallet() {
		String returnAnswer = this.generateRequest("walletlock");
		return returnAnswer;
	}

	public String getconnectioncount() {
		String returnAnswer = generateRequest("getconnectioncount");
		return returnAnswer;
	}

	public String gethashespersec() {
		String returnAnswer = generateRequest("gethashespersec");
		return returnAnswer;
	}

	public String getmininginfo() {
		String returnAnswer = generateRequest("getmininginfo");
		return returnAnswer;
	}

	public String getnettotals() {
		String returnAnswer = generateRequest("getnettotals");
		return returnAnswer;
	}

	public String getpeerinfo() {
		String returnAnswer = generateRequest("getpeerinfo");
		return returnAnswer;
	}

	public String listaddressgroupings() {
		String returnAnswer = generateRequest("listaddressgroupings");
		return returnAnswer;
	}

	public String listlockunspent() {
		String returnAnswer = generateRequest("listlockunspent");
		return returnAnswer;
	}

	public String ping() {
		String returnAnswer = generateRequest("ping");
		return returnAnswer;
	}

	public String stop() {
		String returnAnswer = generateRequest("stop");
		return returnAnswer;
	}

	public String gettxoutsetinfo() {
		String returnAnswer = generateRequest("gettxoutsetinfo");
		return returnAnswer;
	}

	public String getunconfirmedbalance() {
		String returnAnswer = generateRequest("getunconfirmedbalance");
		return returnAnswer;
	}

	public String gettransaction(String txid) {
		String returnAnswer = generateRequest("gettransaction", txid);
		return returnAnswer;
	}

	public String getrawmempool(boolean... verbose) {
		String returnAnswer = "";
		if (verbose.length > 0) {
			returnAnswer = generateRequest("getrawmempool", verbose[0]);
		} else {
			returnAnswer = generateRequest("getrawmempool", false);
		}
		return returnAnswer;
	}

	public String getrawtransaction(String txid) {
		String returnAnswer = generateRequest("getrawtransaction", txid);
		return returnAnswer;
	}

	public String decoderawtransaction(String hexstring) {
		String returnAnswer = generateRequest("decoderawtransaction", hexstring);
		return returnAnswer;
	}

	public String getblock(String hash) {
		String returnAnswer = generateRequest("getblock", hash);
		return returnAnswer;
	}

	public String getblockhash(int index) {
		String returnAnswer = this.generateRequest("getblockhash", index);
		return returnAnswer;
	}

	public String getnewaddress(String label) {
		String returnAnswer = generateRequest("getnewaddress", label);
		return returnAnswer;

	}

	public String getaddressesbyaccount(String account) {
		String returnAnswer = generateRequest("getaddressesbyaccount", account);
		return returnAnswer;
	}

	public String dumpprivkey(String address) {
		this.closewallet();
		this.openwallet();
		String privateKey = generateRequest("dumpprivkey", address);
		this.closewallet();
		return privateKey;
	}

	public String sendRawTransaction(String rawTransactionHex) {
		String returnAnswer = generateRequest("sendrawtransaction",
				rawTransactionHex);
		return returnAnswer;
	}

	public String walletpassphrasechange(String oldpassphrase,
			String newpassphrase) {
		String returnAnswer = generateRequest("walletpassphrasechange",
				oldpassphrase, newpassphrase);
		return returnAnswer;
	}

	public String createmultisig(int nrequired, String... key) {
		JSONArray address = new JSONArray();
		if (key.length > 0) {
			for (int i = 0; i < key.length; i++) {
				address.put(key[i]);
			}
		}
		return generateRequest("createmultisig", nrequired, address);
	}

	public String addmultisigaddress(int nrequired, String label, String... key) {
		JSONArray address = new JSONArray();
		if (key.length > 0) {
			for (int i = 0; i < key.length; i++) {
				address.put(key[i]);
			}
		}
		if (label.equals(""))
			return generateRequest("addmultisigaddress", nrequired, address);
		else
			return generateRequest("addmultisigaddress", nrequired, address,
					label);
	}

	public String createrawtransaction(JSONArray txs, JSONObject sendAddresses) {
		String returnAnswer = generateRequest("createrawtransaction", txs,
				sendAddresses);
		return generateRequest("createrawtransaction", txs, sendAddresses);
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
				this.rpcuser, this.rpcpassword.toCharArray());
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return temp;
			}
		});
		String uri = "http://" + this.rpcurl + ":" + this.rpcport;
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
			ioE.printStackTrace();
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
				try {
					JSONObject json = new JSONObject(responseToString);
					String returnAnswer = json.get("result").toString();
					return returnAnswer;
				} catch (Exception e) {
					return "";
				}
			} else {
				connection.disconnect();
			}
		} catch (Exception e) {
		}
		return "";
	}

	public String sendToAddress(String address, BigDecimal amount) {
		String returnAnswer = generateRequest("sendtoaddress", address, amount);
		return returnAnswer;
	}

	public String validateaddress(String address) {
		String returnAnswer = generateRequest("validateaddress", address);
		return returnAnswer;
	}

	public String listtransactions(String username, int count, int from) {
		String returnAnswer = generateRequest("listtransactions", username,
				count, from);
		return returnAnswer;
	}

	
}
