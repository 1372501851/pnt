package com.inesv.util;

import com.inesv.mapper.PicVerifyCodeRecordMapper;
import com.inesv.mapper.VerifyCodeRecordMapper;
import com.inesv.model.PicVerifyCodeRecord;
import com.inesv.model.VerifyCodeRecord;
import com.inesv.sms.AliSmsUtil;
import com.inesv.sms.INTsms;
import com.inesv.sms.YunPianSmsUtils;
import com.inesv.util.codeutils.Captcha;
import com.inesv.util.codeutils.GifCaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class MapUtil {
	private static final Logger log = LoggerFactory.getLogger(MapUtil.class);

	public static final Map<String, Object> CODE_MAP = new ConcurrentHashMap<>();

	public static String addUser = "add_user_";

	public static String forgetPwd = "forget_pwd_";

	public static String editDealPwd = "edit_deal_pwd_";

	public static String forgetDealPwd = "forget_deal_pwd_";

	public static final Map<String, Object> PIC_CODE_MAP = new ConcurrentHashMap<>();

	public static String picAddUser = "pic_add_user_";

	public static String picForgetPwd = "pic_forget_pwd_";

	public static String picEditDealPwd = "pic_edit_deal_pwd_";

	public static String picForgetDealPwd = "pic_forget_deal_pwd_";

	@Value("${codepic.path}")
	public String codepicPath;
	@Value("${mapping.pic.path}")
	public String mappingPicPath;

	@Autowired
	private VerifyCodeRecordMapper verifyCodeRecordMapper;
	@Autowired
	private PicVerifyCodeRecordMapper picVerifyCodeRecordMapper;

	public BaseResponse sendCode(String phone, String type, String ipAddr, ResponseParamsDto responseParamsDto) {
		try {
			Integer code = (int) ((Math.random() * 9 + 1) * 100000);

			if (responseParamsDto instanceof ResponseParamsDto.ResponseParamsKRDto){
				sendSmsKR(phone,code.toString());
			}else{
				sendSms(phone, code.toString()); // 异步-发送验证码
			}
			//添加验证码发送记录
			verifyCodeRecordMapper.add(new VerifyCodeRecord(phone, CastUtils.castString(code), CastUtils.castInt(type), ipAddr));

			return RspUtil.successMsg(responseParamsDto.SMSSEND_SUCCESS);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	public static Object getCode(String str) {
		if (ValidataUtil.isNull((String) CODE_MAP.get(str))) {
			return "";
		}
		return CODE_MAP.get(str);
	}

	public static void deleteCode(String str) {
		CODE_MAP.remove(str);
	}

	private static void sendSms(final String phone, final String code) {
		ThreadUtil.execute(new MyTaskUtil() {
			@Override
			public void run() {
				try {
					AliSmsUtil.sendMySms(phone, code);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 2, TimeUnit.MILLISECONDS);
	}

	private static void sendSmsKR(final String phone, final String code) {
		ThreadUtil.execute(new MyTaskUtil() {
			@Override
			public void run() {
				try {
					INTsms.sendRequest(code, phone);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 2, TimeUnit.MILLISECONDS);
	}

	/**
	 * 图片验证码存放到内存
	 * @param phone
	 * @param type
	 * @param picCode
	 * @param image
	 * @return
     * @throws Exception
     */
	public BaseResponse setPicCode(String phone, String type, String ipAddr) throws Exception {
		try {
			String key = getPicCodeKey(phone, type);

			String picCodeName = key + ".gif";
			String basePath = codepicPath;
			String picCodePath = basePath+ picCodeName;

			OutputStream output = new FileOutputStream(new File(picCodePath));
			Captcha captcha = new GifCaptcha(146,33,4);
			captcha.out(output);
			//int code = handlePicCode(output);
			int code = CastUtils.castInt(captcha.text());

			//添加验证码发送记录
			picVerifyCodeRecordMapper.add(new PicVerifyCodeRecord(phone, String.valueOf(code), CastUtils.castInt(type), ipAddr));

			Map<String,Object> map = new HashMap<String, Object>();
			map.put("picurl", mappingPicPath+picCodeName);
			//log.info("setPicCode方法下的PIC_CODE_MAP："+GsonUtils.toJson(MapUtil.PIC_CODE_MAP));
			return RspUtil.success(map);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	public static Object getPicCode(String str) {
		if (ValidataUtil.isNull((String) PIC_CODE_MAP.get(str))) {
			return "";
		}
		return PIC_CODE_MAP.get(str);
	}

	public static void deletePicCode(String str) {
		PIC_CODE_MAP.remove(str);
	}

	public static String getPicCodeKey(String phone, String type){
		String key = "";
		if (type.equals("1")) { // 用户注册
			key = picAddUser + phone;
		}
		if (type.equals("2")) { // 用户忘记密码
			key = picForgetPwd + phone;
		}
		if (type.equals("3")) { // 用户修改交易密码
			key = picEditDealPwd + phone;
		}
		if (type.equals("4")) { // 用户忘记交易密码
			key = picForgetDealPwd + phone;
		}
		return key;
	}


	/**
	 * 生成图片验证码
	 *
	 * @param username
	 * @param type
	 * @return
	 * @throws IOException
	 */
	private int handlePicCode(OutputStream output) throws Exception {
        // 在内存中创建图象
        int width = 120, height = 30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 获取图形上下文
        Graphics2D g = (Graphics2D) image.getGraphics();
        //生成随机类
        Random random = new Random();

        // 设定背景色
        g.setColor(getRandColor(240, 250));
        g.fillRect(0, 0, width, height);

        //这里很重要  你的环境里所拥有的汉字字体  不然汉字会乱码
        Font font = new Font("新宋体", Font.PLAIN, 25);
        //设定字体
        g.setFont(font);

        // 随机产生90条干扰线，更改getRandColor（）方法中的参数可以改变干扰线的粗细程度
        g.setColor(getRandColor(90, 230));
        for (int i = 0; i < 90; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(1200);
            int yl = random.nextInt(1200);
            g.drawLine(x, y, x + xl, y + yl);
        }

        //验证码，由2个一位数的加减乘三种运算法构成
        int num1 = (int) (Math.random() * 10) + 1;
        int num2 = (int) (Math.random() * 10) + 1;
        int funNo = random.nextInt(3); //产生[0,2]之间的随机整数
        String funMethod = "";
        int result = 0;
        switch (funNo) {
            case 0:
                funMethod = "加";
                result = num1 + num2;
                break;
            case 1:
                funMethod = "减";
                result = (num1 - num2) > 0 ? (num1 - num2) : (num2 - num1);
                break;
            case 2:
                funMethod = "乘";
                result = num1 * num2;
                break;
        }

        String calc = funMethod == "减" ? ((num1 - num2) > 0 ? (num1 + funMethod + num2 + "=?") : (num2 + funMethod + num1 + "=?")) : (num1 + funMethod + num2 + "=?");
        g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));

        FontRenderContext context = g.getFontRenderContext();
        Rectangle2D bounds = (font).getStringBounds(calc, context);
        double x = (width - bounds.getWidth()) / 2;
        double y = (height - bounds.getHeight()) / 2;
        double ascent = -bounds.getY();
        double baseY = y + ascent;

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawString(calc, (int) x, (int) baseY);

        // 图象生效
        g.dispose();
		ImageIO.write(image, "JPEG", output);

		return result;
	}

	// 给定范围获得随机颜色
	public Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}



	public BaseResponse sendYunPianCode(String areaCode,String phone, String type, String ipAddr, ResponseParamsDto responseParamsDto) {
		try {
			Integer code = (int) ((Math.random() * 9 + 1) * 100000);
//			sendSms(phone, code.toString()); // 异步-发送验证码
			String result=YunPianSmsUtils.sendSmsByTpl(areaCode,phone, String.valueOf(code));
			if("error".equalsIgnoreCase(result)){
				//验证码类短信1小时内同一手机号发送次数不能超过3次
				return RspUtil.rspError(responseParamsDto.SMS_REQUEST_FAIL);
			}else if("".equalsIgnoreCase(result)){
				return RspUtil.error();
			}
			//添加验证码发送记录
			verifyCodeRecordMapper.add(new VerifyCodeRecord(areaCode+phone, CastUtils.castString(code), CastUtils.castInt(type), ipAddr));
			return RspUtil.successMsg(responseParamsDto.SMSSEND_SUCCESS);
		} catch (Exception e) {
			log.error("错误：{}",e);
			return RspUtil.error();
		}
	}

}
