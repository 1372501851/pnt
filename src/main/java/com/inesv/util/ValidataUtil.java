package com.inesv.util;

import com.inesv.controller.CodeController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidataUtil {
    private static final Logger log = LoggerFactory.getLogger(ValidataUtil.class);

    public static final String DATETIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static ConcurrentHashMap<String, Map> phoneLimitMap = new ConcurrentHashMap<>();
    private static int countLimit = 10;//一天发送频率限制10次

    /**
     * 判断是否为null
     * @param value
     * @return
     */
    public static boolean isNull(Object value){
        if("".equals(value)  || "null".equals(value) || " ".equals(value) || value == null) return true;
        return false;
    }
    
    public static boolean checkFieldIsNotNull(Object obj){
        try {
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(obj) != null || "".equals(f.get(obj))) {
                    return true;
                }
            }
        }catch (IllegalAccessException e){
        	e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成UUID
     * @return
     */
    public static String generateUUID(){
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid;
    }

    /**
     * 16进制转为10进制
     * @param progressive
     * @return
     */
    public static BigDecimal Progressive(String progressive){
        progressive = progressive.replaceAll("0x","");
        BigInteger bi = new BigInteger(progressive,16);
        return new BigDecimal(bi.toString(10));
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String dateStr) throws Exception{
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATETIME_DEFAULT_FORMAT);
        Date date = dateTimeFormat.parse(dateStr);
        return date.getTime();
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateFormat(Date date) throws Exception{
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(date);
    }

    /**
     * 获取今日或昨日的日期
     */
    public static String getDate(Integer type) throws Exception{
        String dateFormat = "";
        Date date = new Date();//取时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        if (type == 1){		// 获取今天的日期
            dateFormat = sdf.format(date);
        }
        if (type == 2){		// 获取昨天的日期
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE,-1);	//把日期往前减少一天，若想把日期向后推一天则将负数改为正数
            date = calendar.getTime();
            dateFormat = sdf.format(date);
        }
        return dateFormat;
    }

    /**
     * 判断当前日期是否大于存储日期
     * @param date
     * @param formatPattern
     * @return
     * @throws Exception
     */
    public static boolean ifNowDateAfterParamDate(String dateStr, String formatPattern) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatPattern);
        String nowStr = dateFormat.format(new Date());

        Date dateNow = dateFormat.parse(nowStr);//当前时间
        Date dateParam = dateFormat.parse(dateStr);//参数时间

        return dateNow.after(dateParam);
    }

    /**
     * 判断当前日期是否小于传参日期
     * @param date
     * @param formatPattern
     * @return
     * @throws Exception
     */
    public static boolean ifNowDateBeforeParamDate(String dateStr, String formatPattern) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatPattern);
            String nowStr = dateFormat.format(new Date());
            Date dateNow = dateFormat.parse(nowStr);//当前时间
            Date dateParam = dateFormat.parse(dateStr);//参数时间

            return dateNow.before(dateParam);
        }catch (Exception e){
            log.error("判断当前日期是否小于传参日期异常", e);
            return false;
        }
    }

    /*
     * 检查多个参数是否为null或者字符串长度为0
     */
    public static boolean checkParamIfEmpty(String ... params) {
        boolean flag = false;
        for (String param : params) {
            if(StringUtils.isEmpty(param)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 获取来源IP
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 手机号码验证
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        String regex = "^1\\d{10}$";
        if (mobile.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(mobile);
            boolean isMatch = m.matches();
            return isMatch;
        }
    }

    /**
     * 验证发送频率
     * @param ipAddr 访问来源IP
     * @param username 手机号码
     * @return
     */
    public static boolean validateIP(String ipAddr, String username) {
        String key = username + "_" + ipAddr;
        Map<String, Object> params = phoneLimitMap.get(key);
        try {
            if (params == null) {
                //初始化数据
                Map<String, Object> map = new HashMap<>();
                map.put("count", 1);
                map.put("visitDate", ValidataUtil.getDate(1));
                phoneLimitMap.put(key, map);

            } else {
                //当前日期大于存放的日期，则计数器重置
                Object count = params.get("count");
                Object visitDate = params.get("visitDate");
                boolean flag = ValidataUtil.ifNowDateAfterParamDate(String.valueOf(visitDate), "yyyy-MM-dd");
                if (flag) {
                    params.put("count", 1);
                    params.put("visitDate", ValidataUtil.getDate(1));
                } else {//增加访问次数
                    params.put("count", (Integer) count + 1);
                }

                Object countCurrent = params.get("count");
                if ((Integer) countCurrent > countLimit) {
                    log.info("IP{}发送频率过高", ipAddr);
                    return true;
                }
            }
        } catch (Exception e) {
            log.info("初始化数据失败", e);
        }
        return false;
    }

    /**
     * 身份证号判断
     * @param IDNumber
     * @return
     */
    public static boolean isIDNumber(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        //假设18位身份证号码:41000119910101123X  410001 19910101 123X
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //(18|19|20)                19（现阶段可能取值范围18xx-20xx年）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十七位奇数代表男，偶数代表女）
        //[0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
        //$结尾

        //假设15位身份证号码:410001910101123  410001 910101 123
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十五位奇数代表男，偶数代表女），15位身份证不含X
        //$结尾


        boolean matches = IDNumber.matches(regularExpression);

        //判断第18位校验值
        if (matches) {

            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        System.out.println("身份证最后一位:" + String.valueOf(idCardLast).toUpperCase() +
                                "错误,正确的应该是:" + idCardY[idCardMod].toUpperCase());
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("异常:" + IDNumber);
                    return false;
                }
            }

        }
        return matches;
    }

    /**
     * 正则验证姓名
     * @param name
     * @return
     */
    public static boolean isChineseName(String name) {
        if (!name.matches("[\u4e00-\u9fa5]{1,15}")) {
            return false;
        }else return true;
    }

    /**
     * 正则验证用户名
     * @param username
     * @return
     */
    public static boolean isUserName(String username) {
        if (!username.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$")) {
            return false;
        }else
            return true;
    }

    /**
     * 正则验证Moc地址
     * @param address
     * @return
     */
    public static boolean isETHAddress(String address) {
        if (!address.matches("^MOx[a-fA-F0-9]{40}$")) {
            return false;
        }else
            return true;
    }


    /**
     * 正则验证  usdt(OMNI链)/btc  地址
     * @param address
     * @return
     */
    public static boolean isUsdtAddress(String address) {
        if (  ! ((address.matches("^(1|3)[a-zA-Z\\d]{24,33}$")) && (address.matches("^[^0OlI]{25,34}$")) )  ) {
            return false;
        }else
            return true;
    }

    public static void main(String[] args) {
        String address="255fzsEBHy9Ri2bMQ8uuuR3tv1YzcDywd4";
        //地址筛选判断
        if(!ValidataUtil.isUsdtAddress(address)){
            System.out.println("false");
        }else{
            System.out.println("true");
        }
    }
}
