package com.inesv.sms;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author sroniya
 * @Description:普通短信发送
 */
public class SmsUtil {

	private static final String ACCOUNT = "N0532446";
	private static final String PSWD = "2EVmRbAJT5d853";
	private static final String URL = "http://smssh1.253.com/msg/send/json";
	private static final String MSG = "【253云通讯】你好,你的验证码是:";
	private static final String REPORT = "true";

	public static String sendSms(String phone, int code) {
		String msg = MSG + code;

		SmsSendRequest smsSingleRequest = new SmsSendRequest(ACCOUNT, PSWD,
				msg, phone, REPORT);

		String requestJson = JSON.toJSONString(smsSingleRequest);

		String response = HttpClientUtil.sendPost(URL, requestJson);

		SmsSendResponse smsSingleResponse = JSON.parseObject(response,
				SmsSendResponse.class);

		return smsSingleResponse.getCode();
	}

	public static void main(String[] args) throws Exception {
		String result = sendSms("13107003828", 12356);
		System.out.println(result);
	}

}
