package com.inesv.controller;

import com.alibaba.fastjson.JSONObject;
import com.inesv.annotation.UnLogin;
import com.inesv.model.Params;
import com.inesv.service.UserService;
import com.inesv.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Resource
	private UserService userService;

	private final static Integer MOBILE_DAILY_LIMIT = 15;//手机号码短信验证请求日限制
	public static final Map<String, Integer> MOBILE_MAP = new ConcurrentHashMap<>();

	static Lock addUserLock = new ReentrantLock();// 锁对象

	static Lock bindPhoneLock = new ReentrantLock();// 锁对象

	@PostMapping("/memoLogin")
	@UnLogin
	public BaseResponse memoLogin(@RequestParam("data") String data) {
		log.info("助记词登陆"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("助记词参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.memoLogin(data);
		} catch (Exception e) {
			log.error("异常！", e);
			return RspUtil.error();
		}
	}


	/**
	 * 用户注册
	 * 
	 * @param data
	 * @return
	 */
	@PostMapping("/addUser")
	@UnLogin
	public BaseResponse addUser(@RequestParam("data") String data) {
		log.info("用户注册"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("用户注册参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");

//		JSONObject json = JSONObject.parseObject(data);
//		String username = json.getString("username");
//		ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
//				.getString("language"));

		addUserLock.lock();
		try {
//			//内存限制手机号码验证访问次数
//			Integer count = MOBILE_MAP.get(username);
//			if(count == null){
//				count = 0;
//				MOBILE_MAP.put(username, count);
//			}
//			if(count >= MOBILE_DAILY_LIMIT) return RspUtil.rspError(responseParamsDto.ILLEGAL_REQUEST);

			BaseResponse baseResponse = userService.addUser(data);
			log.info("用户注册返回:" + GsonUtils.toJson(baseResponse));

//			//增加验证访问次数
//			count = MOBILE_MAP.get(username) + 1;
//			MOBILE_MAP.put(username, count);

			return baseResponse;
		} catch (Exception e) {
			e.printStackTrace();
			return RspUtil.error();
		} finally {
			addUserLock.unlock();
		}
	}

	/**
	 * 用户登陆
	 * 
	 * @return
	 */
	@PostMapping("/login")
	@UnLogin
	public BaseResponse login(@RequestParam("data") String data) {
		log.info("用户登陆"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("用户登陆参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.login(data);
		} catch (Exception e) {
			e.printStackTrace();
			return RspUtil.error();
		}
	}

	@PostMapping("/userIsExistence")
	@UnLogin
	public BaseResponse userIsExistence(@RequestParam("data") String data) {
		log.info("用户注销"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("用户注销参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.userIsExistence(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 用户注销
	 * 
	 * @return
	 */
	@PostMapping("/logout")
	@UnLogin
	public BaseResponse logout(@RequestParam("data") String data) {
		log.info("用户注销"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("用户注销参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.logout(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 设置昵称
	 * 
	 * @return
	 */
	@PostMapping("/setNickName")
	public BaseResponse setNickName(@RequestParam("data") String data) {
		log.info("设置昵称"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("设置昵称参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.setNickName(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 设置用户头像
	 * 
	 * @param data
	 * @param photo
	 * @return
	 */
	@PostMapping("/setPhoto")
	public BaseResponse setPhoto(@RequestParam("data") String data,
			MultipartFile photo) {
		log.info("设置用户头像"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("设置用户头像参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.setPhoto(data, photo);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 设置交易密码（暂不使用）
	 * 
	 * @return
	 */
	@PostMapping("/setDealPwd")
	public BaseResponse setDealPwd(@RequestParam("data") String data) {
		log.info("设置交易密码"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("设置交易密码参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.setDealPwd(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 修改交易密码
	 * 
	 * @return
	 */
	@PostMapping("/editDealPwd")
	@UnLogin
	public BaseResponse editDealPwd(@RequestParam("data") String data) {
		log.info("修改交易密码"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("修改交易密码参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.editDealPwd(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 找回交易密码
	 * 
	 * @return
	 */
	@PostMapping("/forgetDealPwd")
	@UnLogin
	public BaseResponse forgetDealPwd(@RequestParam("data") String data) {
		log.info("找回交易密码"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("找回交易密码参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.forgetDealPwd(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 获取交易密码状态
	 * 
	 * @return
	 */
	@PostMapping("/getDealPwdState")
	public BaseResponse getDealPwdState(@RequestParam("data") String data) {
		log.info("获取交易密码状态"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("获取交易密码状态参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.getDealPwdState(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 修改密码
	 * 
	 * @return
	 */
	@PostMapping("/editPwd")
	public BaseResponse editPwd(@RequestParam("data") String data) {
		log.info("修改密码"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("修改密码参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.editPwd(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 忘记密码
	 * 
	 * @return
	 */
	@PostMapping("/forgetPwd")
	@UnLogin
	public BaseResponse forgetPwd(@RequestParam("data") String data) {
		log.info("忘记密码"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("忘记密码参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.forgetPwd(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 查看用户信息
	 * 
	 * @return
	 */
	@PostMapping("/getUserInfo")
	public BaseResponse getUserInfo(@RequestParam("data") String data) {
		log.info("查看用户信息"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("查看用户信息参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.getUserInfo(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 导入地址 同步代码块，不然可能出现多用户用同一个
	 * 
	 * @param data
	 * @return
	 */
	@UnLogin
	@PostMapping("/importAddress")
	public BaseResponse importAddress(String data) {
		log.info("导入钱包"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("导入钱包参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.importAddress(data);
		} catch (Exception e) {
			e.printStackTrace();
			return RspUtil.error();
		}
	}

	/**
	 * 导出地址
	 * 
	 * @param data
	 * @return
	 */
	@PostMapping("/exportAddress")
	public BaseResponse exportAddress(String data) {
		log.info("导出钱包"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("导出钱包参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.exportAddress(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 绑定微信
	 * 
	 * @param data
	 * @return
	 */
	@PostMapping("/bindWeChat")
	public BaseResponse bindWeChat(String data) {
		log.info("绑定微信接口"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("绑定微信接口参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.bindWeChat(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 绑定支付宝
	 * 
	 * @param data
	 * @return
	 */
	@PostMapping("/bindApay")
	public BaseResponse bindApay(String data) {
		log.info("绑定支付宝接口"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("绑定支付宝接口参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.bindApay(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

	/**
	 * 邀请页面-个人信息
	 * 
	 * @param data
	 * @return
	 */
	@PostMapping("/getUserByInvitation")
	public BaseResponse getUserByInvitation(String data) {
		log.info("邀请页面-个人信息接口"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("邀请页面-个人信息接口参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.getUserByInvitation(data);
		} catch (Exception e) {
			e.printStackTrace();
			return RspUtil.error();
		}
	}

	/**
	 * 绑定手机号
	 *
	 * @param data
	 * @return
	 */
	@PostMapping("/bindPhone")
	public BaseResponse bindPhone(String data) {
		log.info("绑定手机号接口"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("绑定手机号接口参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		JSONObject json = JSONObject.parseObject(data);
		String phone = json.getString("phone");
		//区号
		String areaCode = json.getString("areaCode");
		areaCode=areaCode.replaceAll(" ","+");
		ResponseParamsDto responseParamsDto = LanguageUtil.proving(json
				.getString("language"));
		bindPhoneLock.lock();
		try {
			//内存限制手机号码验证访问次数
			Integer count = MOBILE_MAP.get(areaCode+phone);
			if(count == null){
				count = 0;
				MOBILE_MAP.put(areaCode+phone, count);
			}
			if(count >= MOBILE_DAILY_LIMIT) return RspUtil.rspError(responseParamsDto.ILLEGAL_REQUEST);
			BaseResponse baseResponse= userService.bindPhone(data);
			log.info("用户绑定手机号返回:" + GsonUtils.toJson(baseResponse));

			//增加验证访问次数
			count = MOBILE_MAP.get(areaCode+phone) + 1;
			MOBILE_MAP.put(areaCode+phone, count);
			return baseResponse;
		} catch (Exception e) {
			return RspUtil.error();
		} finally {
			bindPhoneLock.unlock();
		}

	}

	/**
	 * 绑定ImToken
	 *
	 * @param data
	 * @return
	 */
	@PostMapping("/bindImToken")
	public BaseResponse bindImToken(String data) {
		log.info("绑定ImToken接口"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("绑定ImToken接口参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.bindImToken(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}


	/**
	 * 判断是否存在手机号（存在则返回手机号，不存在返回空字符串）
	 *
	 * @param data
	 * @return
	 */
	@PostMapping("/isExistPhone")
	public BaseResponse isExistPhone(String data) {
		log.info("判断是否存在手机号接口"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("判断是否存在手机号接口参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.isExistPhone(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}


	/**
	 * 判断是否存在ImToken（存在则返回ImToken，不存在返回空字符串）
	 *
	 * @param data
	 * @return
	 */
	@PostMapping("/isExistImToken")
	public BaseResponse isExistImToken(String data) {
		log.info("判断是否存在ImToken接口"
				+ Thread.currentThread().getStackTrace()[1].getMethodName());
		log.info("判断是否存在ImToken接口参数:" + data);
		if (ValidataUtil.isNull(data))
			return RspUtil.rspError("参数不能为空");
		try {
			return userService.isExistImToken(data);
		} catch (Exception e) {
			return RspUtil.error();
		}
	}

}
