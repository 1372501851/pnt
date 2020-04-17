package com.inesv.util.CoinAPI;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.inesv.model.Address;
import com.inesv.util.ValidataUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class XrpcoinApi {
	private String rpcurl = "";
	private String rpcport = "";
	private String rpcuser = "";
	private String rpcpassword = "";
	private String walletpassphrase = "";
	private static XrpcoinApi api = new XrpcoinApi();

	private XrpcoinApi() {

	}

	public synchronized static XrpcoinApi getApi(Address address) {
		if (addressIsChange(address, api)) {
			api.rpcurl = address.getAddress();
			api.rpcurl = address.getPort();
			api.rpcuser = address.getName();
			api.rpcpassword = address.getPassword();
			api.walletpassphrase = address.getLockPassword();
		}
		return api;
	}

	private static boolean addressIsChange(Address address, XrpcoinApi api) {
		boolean ok = api == null || ValidataUtil.isNull(api.rpcurl)
				|| ValidataUtil.isNull(api.rpcport)
				|| ValidataUtil.isNull(api.rpcuser)
				|| ValidataUtil.isNull(api.rpcpassword)
				|| ValidataUtil.isNull(api.walletpassphrase)
				|| !api.rpcurl.equals(address.getAddress())
				|| !api.rpcport.equals(address.getPort())
				|| !api.rpcpassword.equals(address.getPassword())
				|| !api.rpcuser.equals(address.getName())
				|| !api.walletpassphrase.equals(address.getLockPassword());
		return ok;
	}

	protected String sendCommand(String methodName, JSONArray params) {
		String result = "";
		HttpURLConnection connection = null;
		JSONObject body = new JSONObject();
		body.put("method", methodName);
		body.put("params", params);
		try {
			URL url = new URL("http://" + rpcurl + ":" + rpcport);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setUseCaches(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(6000);
			connection.setReadTimeout(6000);
			OutputStream ops = connection.getOutputStream();
			ops.write(body.toJSONString().getBytes());
			ops.flush();
			ops.close();
		} catch (Exception e) {
			connection.disconnect();
			return "";
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
				result = response.toString();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String args[]) {
		XrpcoinApi api = new XrpcoinApi();
		api.rpcurl = "47.92.91.49";
		api.rpcport = "8089";
		api.rpcuser = "dirhams!";
		api.rpcpassword = "123456";
		api.walletpassphrase = "123456";
		String test = api.sendCommand("validation_create", new JSONArray());
		System.out.println("test:" + test);
		/*
		 * { "result" : { "status" : "success", "validation_key" :
		 * "FAWN JAVA JADE HEAL VARY HER REEL SHAW GAIL ARCH BEN IRMA",
		 * "validation_public_key" :
		 * "n9Mxf6qD4J55XeLSCEpqaePW4GjoCR5U1ZeGZGJUCNe3bQa4yQbG",
		 * "validation_seed" : "ssZkdwURFMBXenJPbrpE14b6noJSu" } }
		 */
	}
}
