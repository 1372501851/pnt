package com.inesv.controller;

import com.alibaba.fastjson.JSONObject;
import com.inesv.annotation.UnLogin;
import com.inesv.model.PicVerifyCodeRecord;
import com.inesv.service.PicVerifyCodeRecordService;
import com.inesv.service.VerifyCodeRecordService;
import com.inesv.util.*;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class CodeController {

    private static final Logger log = LoggerFactory.getLogger(CodeController.class);
    @Autowired
    private MapUtil mapUtil;
    @Autowired
    private VerifyCodeRecordService verifyCodeRecordService;
    @Autowired
    private PicVerifyCodeRecordService picVerifyCodeRecordService;

    private final static Integer IP_DAILY_LIMIT = 20;//IP验证请求日限制
    private final static Integer MOBILE_DAILY_LIMIT = 15;//手机号码验证请求日限制
    public static final Map<String, Integer> IP_MAP = new ConcurrentHashMap<>();
    public static final Map<String, Integer> MOBILE_MAP = new ConcurrentHashMap<>();


    /**
     * 云片发送国际短信验证码
     *
     * @param data
     * @return
     */
    @PostMapping("/sendCloudSmsCodeLimit")
    @UnLogin
    public BaseResponse sendCloudSmsCodeLimit(@RequestParam("data") String data, HttpServletRequest request) {
        String ipAddr = ValidataUtil.getIpAddr(request);

        log.info("发送国际短信验证码" + Thread.currentThread().getStackTrace()[1].getMethodName() + ",来源IP：" + ipAddr);
        log.info("发送国际短信验证码参数:" + data);
        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
        try {
            JSONObject json = JSONObject.parseObject(data);
            String phone = json.getString("phone");
            String type = json.getString("type");
            ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
            //区号
            String areaCode = json.getString("areaCode");
            areaCode=areaCode.replaceAll(" ","+");
            if(areaCode.indexOf("+")<0){
                return  RspUtil.rspError("areaCode error!");
            }

            if (ValidataUtil.isNull(phone)) return RspUtil.rspError(responseParamsDto.FAIL_PHONE_DATA_DESC);
            if (ValidataUtil.isNull(type)) return RspUtil.rspError(responseParamsDto.TYPE_NULL_DESC);
            return mapUtil.sendYunPianCode(areaCode,phone, type, ipAddr, responseParamsDto);
        } catch (Exception e) {
            return RspUtil.error();
        }
    }

//    private static Lock lock = new ReentrantLock();
//    /**
//     * 发送短信验证码
//     *
//     * @param data
//     * @return
//     */
//    @PostMapping("/sendSmsCode")
//    @UnLogin
//    public BaseResponse sendSmsCode(@RequestParam("data") String data, HttpServletRequest request) {
//        log.info("发送短信验证码" + Thread.currentThread().getStackTrace()[1].getMethodName() + ",来源IP：" + ValidataUtil.getIpAddr(request));
//        log.info("发送短信验证码参数:" + data);
//        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
//        try {
//            JSONObject json = JSONObject.parseObject(data);
//            ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
//            return RspUtil.rspError(responseParamsDto.VERSION_UPDATE_TIP);
//            /*JSONObject json = JSONObject.parseObject(data);
//            String username = json.getString("username");
//            username = DESUtil.decode(username);
//            if (username == null) {
//                return RspUtil.error("请更新到最新版本", 500);
//            }
//            String type = json.getString("type");
//            if (ValidataUtil.isNull(username)) return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
//            if (!ValidataUtil.isMobile(username)) return RspUtil.rspError(responseParamsDto.PHONE_REGEX_WRONG);
//            if (ValidataUtil.isNull(type)) return RspUtil.rspError(responseParamsDto.TYPE_NULL_DESC);
//            return MapUtil.sendCode(username, type);*/
//        } catch (Exception e) {
//            return RspUtil.error();
//        }
//    }

//    /**
//     * 发送短信验证码
//     *
//     * @param data
//     * @return
//     */
//    @PostMapping("/sendSmsCodeLimit")
//    @UnLogin
//    public BaseResponse sendSmsCodeLimit(@RequestParam("data") String data, HttpServletRequest request) {
//        String ipAddr = ValidataUtil.getIpAddr(request);
//
//        log.info("发送短信验证码" + Thread.currentThread().getStackTrace()[1].getMethodName() + ",来源IP：" + ipAddr);
//        log.info("发送短信验证码参数:" + data);
//        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
//        try {
//            JSONObject json = JSONObject.parseObject(data);
//            String username = json.getString("username");
//            String type = json.getString("type");
//            ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
//            if (ValidataUtil.isNull(username)) return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
//            //if (!ValidataUtil.isMobile(username)) return RspUtil.rspError(responseParamsDto.PHONE_REGEX_WRONG);
//            if (ValidataUtil.isNull(type)) return RspUtil.rspError(responseParamsDto.TYPE_NULL_DESC);
//            if (ValidataUtil.validateIP(ipAddr, username)) return RspUtil.error();
//
//            BaseResponse baseResponse = verifyCodeRecordService.checkIfIllegal(username, ipAddr, responseParamsDto);
//            if (baseResponse.getCode() != 200) {
//                return baseResponse;
//            }
//
//            return mapUtil.sendCode(username, type, ipAddr, responseParamsDto);
//        } catch (Exception e) {
//            return RspUtil.error();
//        }
//    }

//    /**
//     * 获取图片验证码
//     *
//     * @param data
//     * @return
//     */
//    @PostMapping("/sendPicCode")
//    @UnLogin
//    public BaseResponse sendPicCode(@RequestParam("data") String data, HttpServletRequest request) {
//        String ipAddr = ValidataUtil.getIpAddr(request);
//
//        log.info("发送图片验证码" + Thread.currentThread().getStackTrace()[1].getMethodName());
//        log.info("发送图片验证码参数:" + data);
//        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
//        try {
//            JSONObject json = JSONObject.parseObject(data);
//            String username = json.getString("username");
//            username = DESUtil.decode(username);
//            if (username == null) {
//                return RspUtil.error("请输入正确的手机号码", 500);
//            }
//            String type = json.getString("type");
//            ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
//            if (ValidataUtil.isNull(username)) return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
//            //if (!ValidataUtil.isMobile(username)) return RspUtil.rspError(responseParamsDto.PHONE_REGEX_WRONG);
//            if (ValidataUtil.isNull(type)) return RspUtil.rspError(responseParamsDto.TYPE_NULL_DESC);
//
//            BaseResponse bResponse = picVerifyCodeRecordService.checkIfIllegal(username, ipAddr, responseParamsDto);
//            if (bResponse.getCode() != 200) {
//                return bResponse;
//            }
//
//            BaseResponse baseResponse = mapUtil.setPicCode(username, type, ipAddr);
//            log.info("发送图片验证码返回:" + GsonUtils.toJson(baseResponse));
//            return baseResponse;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return RspUtil.error();
//        }
//    }

//    /**
//     * 校验图片验证码
//     *
//     * @param data
//     * @return
//     */
//    @PostMapping("/verifyPicCode")
//    @UnLogin
//    public BaseResponse verifyPicCode(@RequestParam("data") String data, HttpServletRequest request) {
//        String ipAddr = ValidataUtil.getIpAddr(request);
//
//        log.info("校验图片验证码" + Thread.currentThread().getStackTrace()[1].getMethodName());
//        log.info("校验图片验证码参数:" + data);
//        if (ValidataUtil.isNull(data)) return RspUtil.rspError("参数不能为空");
//        try {
//            JSONObject json = JSONObject.parseObject(data);
//            String username = json.getString("username");
//            username = DESUtil.decode(username);
//            if (username == null) {
//                return RspUtil.error("请输入正确的手机号码", 500);
//            }
//            String type = json.getString("type");
//            String picCode = json.getString("picCode");
//            ResponseParamsDto responseParamsDto = LanguageUtil.proving(json.getString("language"));
//            if (ValidataUtil.isNull(username)) return RspUtil.rspError(responseParamsDto.USERNAME_NULL_DESC);
//            //if (!ValidataUtil.isMobile(username)) return RspUtil.rspError(responseParamsDto.PHONE_REGEX_WRONG);
//            if (ValidataUtil.isNull(type)) return RspUtil.rspError(responseParamsDto.TYPE_NULL_DESC);
//            if (ValidataUtil.isNull(picCode)) return RspUtil.rspError("参数不能为空");
//
//            lock.lock();
//            //内存限制手机号码验证访问次数
//            Integer countMobile = MOBILE_MAP.get(username);
//            if(countMobile == null){
//                countMobile = 0;
//                MOBILE_MAP.put(username, countMobile);
//            }
//            if(countMobile >= MOBILE_DAILY_LIMIT) return RspUtil.rspError(responseParamsDto.ILLEGAL_REQUEST);
//
//            //内存限制ip验证访问次数
//            Integer countIP = IP_MAP.get(ipAddr);
//            if(countIP == null){
//                countIP = 0;
//                IP_MAP.put(ipAddr, countIP);
//            }
//            if(countIP >= IP_DAILY_LIMIT) return RspUtil.rspError(responseParamsDto.ILLEGAL_REQUEST);
//
//            BaseResponse baseResponse = verifyPicCode(username, type, picCode, ipAddr, responseParamsDto);
//            log.info("校验图片验证码返回:" + GsonUtils.toJson(baseResponse));
//
//            //增加手机号码验证访问次数
//            countMobile = MOBILE_MAP.get(username) + 1;
//            MOBILE_MAP.put(username, countMobile);
//            //增加IP验证访问次数
//            countIP = IP_MAP.get(ipAddr) + 1;
//            IP_MAP.put(ipAddr, countIP);
//
//            return baseResponse;
//        } catch (Exception e) {
//            return RspUtil.error();
//        }finally {
//            lock.unlock();
//        }
//    }

//    /**
//     * 校验图片验证码
//     *
//     * @param username
//     * @param type
//     * @return
//     * @throws IOException
//     */
//    private BaseResponse verifyPicCode(String username, String type, String picCode, String ipAddr, ResponseParamsDto responseParamsDto) throws Exception {
//        //log.info("verifyPicCode方法下的PIC_CODE_MAP："+GsonUtils.toJson(MapUtil.PIC_CODE_MAP));
//        String codeKey = MapUtil.getPicCodeKey(username, type);
//        PicVerifyCodeRecord record = new PicVerifyCodeRecord();
//        record.setMobile(username);
//        record.setType(CastUtils.castInt(type));
//        record.setValidTime(5 * 60);//验证码5分钟内有效
//        record.setState(0);
//        PicVerifyCodeRecord codeRecord = picVerifyCodeRecordService.getValidVerifyCode4LastOne(record);
//        if (codeRecord == null || !picCode.equals(codeRecord.getVerifyCode()))
//            return RspUtil.rspError(responseParamsDto.CODE_FAIL_DESC);
//
//        //更新验证码状态为已使用
//        codeRecord.setState(1);
//        picVerifyCodeRecordService.update(codeRecord);
//
//        //验证通过，删除本地图片文件
//        String picCodeName = codeKey + ".jpg";
//        String basePath = mapUtil.codepicPath;
//        String picCodePath = basePath + picCodeName;
//        File file = new File(picCodePath);
//        if (file.exists()) {
//            file.delete();
//        }
//
//        //发送短信
//        return  mapUtil.sendCode(username, type, ipAddr, responseParamsDto);
//    }

//    /**
//     * 生成图片验证码
//     *
//     * @param username
//     * @param type
//     * @return
//     * @throws IOException
//     */
//    private BaseResponse handlePicCode(String username, String type) throws Exception {
//        /*// 在内存中创建图象
//        int width = 120, height = 30;
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//
//        // 获取图形上下文
//        Graphics2D g = (Graphics2D) image.getGraphics();
//        //生成随机类
//        Random random = new Random();
//
//        // 设定背景色
//        g.setColor(getRandColor(240, 250));
//        g.fillRect(0, 0, width, height);
//
//        //这里很重要  你的环境里所拥有的汉字字体  不然汉字会乱码
//        Font font = new Font("新宋体", Font.PLAIN, 25);
//        //设定字体
//        g.setFont(font);
//
//        // 随机产生90条干扰线，更改getRandColor（）方法中的参数可以改变干扰线的粗细程度
//        g.setColor(getRandColor(90, 230));
//        for (int i = 0; i < 90; i++) {
//            int x = random.nextInt(width);
//            int y = random.nextInt(height);
//            int xl = random.nextInt(1200);
//            int yl = random.nextInt(1200);
//            g.drawLine(x, y, x + xl, y + yl);
//        }
//
//        //验证码，由2个一位数的加减乘三种运算法构成
//        int num1 = (int) (Math.random() * 10) + 1;
//        int num2 = (int) (Math.random() * 10) + 1;
//        int funNo = random.nextInt(3); //产生[0,2]之间的随机整数
//        String funMethod = "";
//        int result = 0;
//        switch (funNo) {
//            case 0:
//                funMethod = "加";
//                result = num1 + num2;
//                break;
//            case 1:
//                funMethod = "减";
//                result = (num1 - num2) > 0 ? (num1 - num2) : (num2 - num1);
//                break;
//            case 2:
//                funMethod = "乘";
//                result = num1 * num2;
//                break;
//        }
//
//        String calc = funMethod == "减" ? ((num1 - num2) > 0 ? (num1 + funMethod + num2 + "=?") : (num2 + funMethod + num1 + "=?")) : (num1 + funMethod + num2 + "=?");
//        g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
//
//        FontRenderContext context = g.getFontRenderContext();
//        Rectangle2D bounds = (font).getStringBounds(calc, context);
//        double x = (width - bounds.getWidth()) / 2;
//        double y = (height - bounds.getHeight()) / 2;
//        double ascent = -bounds.getY();
//        double baseY = y + ascent;
//
//        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        g.drawString(calc, (int) x, (int) baseY);
//
//        // 图象生效
//        g.dispose();
//
//        return mapUtil.setPicCode(username, type, String.valueOf(result), image);
//        Captcha captcha = new GifCaptcha(146,33,4);
//        return mapUtil.setPicCode(username, type, captcha);*/
//        return null;
//    }
//
//    // 给定范围获得随机颜色
//    public Color getRandColor(int fc, int bc) {
//        Random random = new Random();
//        if (fc > 255) {
//            fc = 255;
//        }
//        if (bc > 255) {
//            bc = 255;
//        }
//        int r = fc + random.nextInt(bc - fc);
//        int g = fc + random.nextInt(bc - fc);
//        int b = fc + random.nextInt(bc - fc);
//        return new Color(r, g, b);
//    }
}
