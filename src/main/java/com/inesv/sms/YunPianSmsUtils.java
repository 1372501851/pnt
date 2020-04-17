package com.inesv.sms;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class YunPianSmsUtils {

    /**
     * 尊敬的用户：您的验证码是:#code#，请不要把验证码泄漏给其他人，如非本人请勿操作。
     */
    private static final long BIND_PHONE_TPI_ID_CN = 3149856;
    private static final long BIND_PHONE_TPI_ID_KR = 3156918;
    private static final long BIND_PHONE_TPI_ID_EN = 3156894;


    /**
     * 尊敬的用户：您有匹配的订单已付款，请到钱包app进行确认。
     */
    private static long ORDER_PAY_TPI_ID_CN = 3149840;
    private static long ORDER_PAY_TPI_ID_KR = 3156924;
    private static long ORDER_PAY_TPI_ID_EN = 3156876;


    /**
     * 尊敬的用户：您的订单已成功匹配，请到钱包app进行确认，您可在订单中查看状态。
     */
    private static long ORDER_MATCH_TPI_ID_CN = 3149838;
    private static long ORDER_MATCH_TPI_ID_KR = 3156920;
    private static long ORDER_MATCH_TPI_ID_EN = 3156916;

    //新增
    private static long TPIS_ORDER_MATCH_TPI_ID_CN = 3642640;

    //ApiKey
    private static String apikey = "a3ae133468a061cb9e12a1607e7cf857";
//    private static String apikey = "";

    //智能匹配模板发送接口的http地址
    private static String URI_SEND_SMS =
            "https://sms.yunpian.com/v2/sms/single_send.json";

    //模板发送接口的http地址
    private static String URI_TPL_SEND_SMS =
            "https://sms.yunpian.com/v2/sms/tpl_single_send.json";


    //编码格式。发送编码格式统一用UTF-8
    private static String ENCODING = "UTF-8";


    /**
     * 智能匹配模板接口发短信
     *
     * @param apikey apikey
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */

    public static String sendSms(String apikey, String text,
                                 String mobile) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }

    /**
     * 通过模板发送短信(不推荐)
     *
     * @param apikey    apikey
     * @param tpl_id    　模板id
     * @param tpl_value 　模板变量值
     * @param mobile    　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */

    public static String tplSendSms(String apikey, long tpl_id, String tpl_value,
                                    String mobile) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("apikey", apikey);
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", tpl_value);
        params.put("mobile", mobile);
        return post(URI_TPL_SEND_SMS, params);
    }


    /**
     * 基于HttpClient 4.3的通用POST方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */
    public static String post(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<
                        NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(),
                            param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList,
                        ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity, ENCODING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseText;
    }


    /***
     * 发送验证码
     * @param areaCode
     * @param mobile
     * @param code
     * @return
     * @throws IOException
     */
    public static String sendSmsByTpl(String areaCode, String mobile, String code) throws IOException {
        String tpl_value = URLEncoder.encode("#code#", ENCODING) + "=" +
                URLEncoder.encode(code, ENCODING);
        String result = null;
        //中
        if ("+86".equals(areaCode)) {
            result = YunPianSmsUtils.tplSendSms(apikey, BIND_PHONE_TPI_ID_CN, tpl_value,
                    areaCode + mobile);

        } else if ("+82".equals(areaCode)) {
            //韩
            result = YunPianSmsUtils.tplSendSms(apikey, BIND_PHONE_TPI_ID_KR, tpl_value,
                    areaCode + mobile);

        } else {
            //英
            result = YunPianSmsUtils.tplSendSms(apikey, BIND_PHONE_TPI_ID_EN, tpl_value,
                    areaCode + mobile);
        }

        log.info("区号：{},手机号: {},验证码: {}", areaCode, mobile, code);
        log.info("发送短信返回：{} ", result);
        JSONObject json = JSONObject.parseObject(result);
        int statusCode = json.getIntValue("code");
        if (0 == statusCode) {
            return "success";
        }
        if (22 == statusCode) {
            return "error";
        }
        return "";
    }


    /**
     * 发送短信通知卖方，买方已付款
     *
     * @param mobile
     * @return
     * @throws IOException
     */
    public static String sendOrderPayed(String areaCode, String mobile) throws IOException {
        String result;
        if ("+86".equals(areaCode)) {
            result = YunPianSmsUtils.tplSendSms(apikey, ORDER_PAY_TPI_ID_CN, null,
                    areaCode + mobile);
        } else if ("+82".equals(areaCode)) {
            result = YunPianSmsUtils.tplSendSms(apikey, ORDER_PAY_TPI_ID_KR, null,
                    areaCode + mobile);
        } else {
            result = YunPianSmsUtils.tplSendSms(apikey, ORDER_PAY_TPI_ID_EN, null,
                    areaCode + mobile);
        }

        log.info("短信提醒买家已付款，手机号: {}", mobile);
        log.info(result);
        JSONObject json = JSONObject.parseObject(result);
        int statusCode = json.getIntValue("code");
        if (0 == statusCode) {
            return "success";
        }
        return result;
    }


    /**
     * 发送短信通知卖方，订单已成功匹配
     *
     * @param mobile
     * @return
     * @throws IOException
     */
    public static String sendOrderMatch(String areaCode, String mobile) throws IOException {
        String result;

        if ("+86".equals(areaCode)) {
            //中
            result = YunPianSmsUtils.tplSendSms(apikey, ORDER_MATCH_TPI_ID_CN, null,
                    areaCode + mobile);

        } else if ("+82".equals(areaCode)) {
            //韩
            result = YunPianSmsUtils.tplSendSms(apikey, ORDER_MATCH_TPI_ID_KR, null,
                    areaCode + mobile);

        } else {
            //英
            result = YunPianSmsUtils.tplSendSms(apikey, ORDER_MATCH_TPI_ID_EN, null,
                    areaCode + mobile);

        }

        log.info("短信提醒卖家订单已成功匹配，区号号: {},手机号: {}", areaCode, mobile);
        log.info(result);
        JSONObject json = JSONObject.parseObject(result);
        int statusCode = json.getIntValue("code");
        if (0 == statusCode) {
            return "success";
        }
        return "";
    }


    /**
     * 发送短信通知管理员
     *
     * @param mobile
     * @return
     * @throws IOException
     */
    public static String tipsOrderMatch(String areaCode, String mobile) throws IOException {
        String result;

        //中
        result = YunPianSmsUtils.tplSendSms(apikey, TPIS_ORDER_MATCH_TPI_ID_CN, null,
                areaCode + mobile);


        log.info("短信提醒管理员，区号号: {},手机号: {}", areaCode, mobile);
        log.info(result);
        JSONObject json = JSONObject.parseObject(result);
        int statusCode = json.getIntValue("code");
        if (0 == statusCode) {
            return "success";
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        //验证码方法测试
//        System.out.println(YunPianSmsUtils.sendSmsByTpl("+86","1088955168","4321"));
//        System.out.println(YunPianSmsUtils.sendSmsByTpl("+82","1088955168","4321"));
//        System.out.println(YunPianSmsUtils.sendOrderPayed("+8616675385441"));
//        System.out.println(YunPianSmsUtils.sendOrderPayed("+86","1088955168"));
        System.out.println(YunPianSmsUtils.tplSendSms(apikey, TPIS_ORDER_MATCH_TPI_ID_CN, null,
                "+8616675385441"));


    }
}