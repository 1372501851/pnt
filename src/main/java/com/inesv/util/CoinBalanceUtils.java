/**
 * 
 */
package com.inesv.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.inesv.sms.HttpClientUtil;

/**
 * @author Admin
 *  
 */
public class CoinBalanceUtils {
	
	 private static final Logger logger = LoggerFactory.getLogger(CoinBalanceUtils.class);
	
	 /**
	  * 根据货币的服务器地址和钱包地址获得货币资产
	  * 
	  * @param serverUrl 货币服务器地址
	  * @param serverPort 货币服务器端口
	  * @param serverName 货币服务器名
	  * @param serverPassword 货币服务器密码
	  * @param address  钱包地址
	  * @return
	  */
	public static String getBalance(String methodName,String serverUrl,String serverPort,String serverName,String serverPassword,String address) {
		
		 Object arr[] = {address,"latest"};
	     return sendRequest(methodName,arr,serverUrl,serverPort,serverName,serverPassword);
	}
	
	private static String sendRequest(String methodName,Object[] params,String url,String port,String userName,String password){
        String response = "";
        JSONObject jsonObject = null;
        try {
            final PasswordAuthentication passwordAuthentication = new PasswordAuthentication(userName,password.toCharArray());
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return passwordAuthentication;
                }
            });

            JSONObject json = new JSONObject();
            json.put("id","1");
            json.put("params",params);
            json.put("method",methodName);

            response = HttpClientUtil.sendPost("http://"+url+":"+port,json.toJSONString(),3000,3000);

            jsonObject = JSONObject.parseObject(response);
        }catch (Throwable throwable){
            logger.error(throwable.getMessage());
        }
        if(jsonObject ==null) {
        	logger.info("获取服务器货币结果为空");
        	return null;
        };
        return jsonObject.getString("result");
    }

}
