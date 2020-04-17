package com.inesv.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AliSmsUtil {

    private static Logger logger = LoggerFactory.getLogger(AliSmsUtil.class);

    // 产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    // 产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "LTAIklhTG9KiACuG";
    static final String accessKeySecret = "MEmCEmF2P8mQA3YzxGvKE0xoXHuB80";

    private static SendSmsResponse sendSms(String phoneNumber, String contant)
            throws ClientException {

        // 可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        // 初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product,domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        // 必填:待发送手机号
        request.setPhoneNumbers(phoneNumber);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName("光子链");
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_135037361");
        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"code\":\"" + contant + "\"}");

        // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        // request.setSmsUpExtendCode("90997");

        // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        // hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

        return sendSmsResponse;
    }

    private static QuerySendDetailsResponse querySendDetails(String bizId,
                                                             String phoneNumber, String contant) throws ClientException {

        // 可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        // 初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product,
                domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        // 组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        // 必填-号码
        request.setPhoneNumber(phoneNumber);
        // 可选-流水号
        request.setBizId(bizId);
        // 必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        // 必填-页大小
        request.setPageSize(10L);
        // 必填-当前页码从1开始计数
        request.setCurrentPage(1L);
        // hint 此处可能会抛出异常，注意catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient
                .getAcsResponse(request);

        return querySendDetailsResponse;
    }

    //发送验证码
    public static String sendMySms(String phone, String contant)
            throws ClientException, InterruptedException {
        logger.info("{}收到的验证码是{}: ", phone, contant);
        String msgContent = "";// 发送的短信内容
        // 发短信
        SendSmsResponse response = sendSms(phone, contant);
        String code = response.getCode();
        logger.info("sms_code : " + code);
        Thread.sleep(3000L);
        // 查明细
        if (response.getCode() != null && response.getCode().equals("OK")) {
            QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(
                    response.getBizId(), phone, contant);
            int i = 0;
            for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse
                    .getSmsSendDetailDTOs()) {
                String content = smsSendDetailDTO.getContent();
                msgContent += content;
            }
            return msgContent;
        }
        return null;
    }
    
    
    /**
     * 发送短信方法，传入手机号和模板code参数
     * @param phoneNumber
     * @param TemplateCode
     * @return
     * @throws ClientException
     */
    private static SendSmsResponse sendSmsTemplet(String phoneNumber,String TemplateCode)
            throws ClientException {

        // 可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        // 初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product,
                domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        // 必填:待发送手机号
        request.setPhoneNumbers(phoneNumber);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName("光子链");
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(TemplateCode);
        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
       //  request.setTemplateParam("{\"code\":\"" + contant + "\"}");

        // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        // request.setSmsUpExtendCode("90997");

        // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        // hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

        return sendSmsResponse;
    }

    /**
     * 发送余额不足短信
     * @param phone
     * @return
     * @throws ClientException
     * @throws InterruptedException
     */
    public static String sendBalanceSms(String phone)
            throws ClientException, InterruptedException {
        String msgContent = "";// 发送的短信内容
        String contant="";
        String templetCode="SMS_122296197";//余额不足短信模板
        // 发短信
        SendSmsResponse response = sendSmsTemplet(phone,templetCode);
        String code = response.getCode();
        logger.info("sms_code : " + code);
        Thread.sleep(3000L);
        // 查明细
        if (response.getCode() != null && response.getCode().equals("OK")) {
            QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(
                    response.getBizId(), phone, contant);
            for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse
                    .getSmsSendDetailDTOs()) {
                String content = smsSendDetailDTO.getContent();
                msgContent += content;
            }
            return msgContent;
        }
        return null;
    }
    
    /**
     * 订单匹配成功短信
     * @param phone
     * @return
     * @throws ClientException
     * @throws InterruptedException
     */
    public static String sendOrderSms(String phone)
            throws ClientException, InterruptedException {
        String msgContent = "";// 发送的短信内容
        String contant="";
        String templetCode="SMS_135042515";//订单匹配成功短信模板
        // 发短信
        SendSmsResponse response = sendSmsTemplet(phone,templetCode);
        String code = response.getCode();
        logger.info("sms_code : " + code);
        Thread.sleep(3000L);
        // 查明细
        if (response.getCode() != null && response.getCode().equals("OK")) {
            QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(
                    response.getBizId(), phone, contant);
            for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse
                    .getSmsSendDetailDTOs()) {
                String content = smsSendDetailDTO.getContent();
                msgContent += content;
            }
            return msgContent;
        }
        return null;
    }
    
    
    /**
     * 提示卖方，买方已付款
     * @param phone
     * @return
     * @throws ClientException
     * @throws InterruptedException
     */
    public static String sendOrderPayed(String phone)
    		throws ClientException, InterruptedException {
    	String msgContent = "";// 发送的短信内容
    	String contant="";
    	String templetCode="SMS_135032459";//短信模板：提示卖方，买方已付款
    	// 发短信
    	SendSmsResponse response = sendSmsTemplet(phone,templetCode);
    	String code = response.getCode();
    	logger.info("sms_code : " + code);
    	// 查明细
    	if (response.getCode() != null && code.equals("OK")) {
    		return "success";
    	}
    	return null;
    }
    
    /**
     * 提示卖方，矿工费不足于匹配订单
     * @param phone
     * @return
     * @throws ClientException
     * @throws InterruptedException
     */
    public static String sendMinerFee(String phone)
    		throws ClientException, InterruptedException {
    	String msgContent = "";// 发送的短信内容
    	String contant="";
    	String templetCode="SMS_127152281";//短信模板：提示卖方，矿工费不足于匹配订单
    	// 发短信
    	SendSmsResponse response = sendSmsTemplet(phone,templetCode);
    	String code = response.getCode();
    	logger.info("sms_code : " + code);
    	Thread.sleep(3000L);
    	// 查明细
    	if (response.getCode() != null && response.getCode().equals("OK")) {
    		QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(
    				response.getBizId(), phone, contant);
    		for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse
    				.getSmsSendDetailDTOs()) {
    			String content = smsSendDetailDTO.getContent();
    			msgContent += content;
    		}
    		return msgContent;
    	}
    	return null;
    }
    
    

    public static void main(String[] args) throws ClientException, InterruptedException {
        sendMySms("13107003828", "123456");
        /*System.out.println(MapUtil.getCode("add_user_18688491013"));
        System.out.println(MapUtil.CODE_MAP);*/
    }
}
