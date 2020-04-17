package com.inesv.util;

import com.alibaba.fastjson.JSONObject;

public class RspUtil {
	private final static int CODE = 400;
	public final static int DEALPWD_NULL = 300;
	public final static int QUICK_ACCESS_FAIL = 1011;
	public final static int USER_EXIST_FAIL = 1044;

	public static <T> BaseResponse<T> success(T data) {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(data);
		rsp.setMessage("success");
		rsp.setCode(200);
		return rsp;
	}

	public static <T> BaseResponse<T> successMsg(String msg) {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(null);
		rsp.setMessage(msg);
		rsp.setCode(200);
		return rsp;
	}

	public static <T> BaseResponse<T> success(Integer code, String msg) {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(null);
		rsp.setMessage(msg);
		rsp.setCode(code);
		return rsp;
	}

	public static <T> BaseResponse<T> success(Integer code, String msg, T data) {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(data);
		rsp.setMessage(msg);
		rsp.setCode(code);
		return rsp;
	}

	public static <T> BaseResponse<T> success() {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(null);
		rsp.setMessage("success");
		rsp.setCode(200);
		rsp.setPage(null);
		return rsp;
	}

	public static <T> BaseResponse<T> error() {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(null);
		rsp.setMessage("失败");
		rsp.setCode(500);
		rsp.setPage(null);
		return rsp;
	}

	public static <T> BaseResponse<T> error(String msg, Integer returncode) {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(null);
		rsp.setMessage(msg);
		rsp.setCode(returncode);
		return rsp;
	}

	public static <T> BaseResponse<T> error(String msg, int code) {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(null);
		rsp.setMessage(msg);
		rsp.setCode(code);
		return rsp;
	}

	public static <T> BaseResponse<T> rspError(String msg) {
		BaseResponse<T> rsp = new BaseResponse<T>();
		rsp.setData(null);
		rsp.setMessage(msg);
		rsp.setCode(CODE);
		return rsp;
	}

	public static String errorStr(String msg) {
		JSONObject json = new JSONObject();
		json.put("code", "-2");
		json.put("message", msg);
		return json.toJSONString();
	}
}
