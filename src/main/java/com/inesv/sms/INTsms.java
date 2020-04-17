package com.inesv.sms;


import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 国际短信
 */
public class INTsms {
    private static String url = "https://sms.yunpian.com/v2/sms/single_send.json";
    private static String apikey = "6baaf858e7c3323df794e1abedc12735";
    private static String temple = "【MoCloud】인증번호#code#. 본인이직접하지않으면 메세지 확인 안하셔도 됩니다.";


    public static void sendRequest(String smscode, String phone){
        String realContent = temple.replace("#code#", smscode);
        HttpRequest request = HttpRequest.post(url)
                .accept("application/json;charset=utf-8;")
                .contentType("application/x-www-form-urlencoded", "utf-8");
        Map<String, String> parmasMap = new HashMap<>();
        parmasMap.put("apikey", apikey);
        parmasMap.put("mobile", phone);
        parmasMap.put("text", realContent);
        request.query(parmasMap);
        System.out.println(request);
        HttpResponse response = request.send();
        System.out.println(response.bodyText());
    }


    public static void main(String[] args) {

        sendRequest("1234", "+821088955168");

    }
}
