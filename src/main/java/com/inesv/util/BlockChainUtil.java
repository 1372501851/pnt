package com.inesv.util;

import com.alibaba.fastjson.JSONObject;
import com.inesv.sms.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

@Component
public class BlockChainUtil {

    private static final Logger logger = LoggerFactory.getLogger(BlockChainUtil.class);

    private static final String RPC_URL = "39.104.59.52";
    private static final String RPC_PORT = "8545";
    private static final String RPC_USERNAME = "dirhams!";
    private static final String RPC_PASSWORD = "dirhams!";
    private static final String DEFAULT_PASSWORD = "123456789";

    public String getWalletAddress(){
        Object arr[] = {DEFAULT_PASSWORD};
        return sendRequest("personal_newAccount",arr);
    }

    public String getWalletBalance(String address){
        Object arr[] = {address,"latest"};
        return sendRequest("eth_getBalance",arr);
    }

    public String sendTransaction(String outAddress, String inAddress, String value){
        Object arr[] = {outAddress,inAddress,value};
        return sendRequest("eth_sendTransaction",arr);
    }

    public String unlock(String address){
        Object arr[] = {address,DEFAULT_PASSWORD};
        return sendRequest("personal_unlockAccount",arr);
    }

    private String sendRequest(String methodName,Object[] params){
        String response = "";
        JSONObject jsonObject = null;
        try {
            final PasswordAuthentication passwordAuthentication = new PasswordAuthentication(RPC_USERNAME,RPC_PASSWORD.toCharArray());
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

            response = HttpClientUtil.sendPost("http://"+RPC_URL+":"+RPC_PORT,json.toJSONString());

            jsonObject = JSONObject.parseObject(response);
        }catch (Throwable throwable){
            logger.error(throwable.getMessage());
        }
        return jsonObject.getString("result");
    }

    public static void main(String[] args) {
        System.out.println(new BlockChainUtil().getWalletBalance("0xd1e60a574d1d8a4ea716f33c59d3c0c3ee820d0f"));
    }

}
