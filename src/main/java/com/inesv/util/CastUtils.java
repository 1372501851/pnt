package com.inesv.util;

import org.apache.commons.lang.StringUtils;

/**
 * 转换工具
 * @author 作者xujianfeng 
 * @date 创建时间：2016年11月25日 下午3:41:16
 */
public class CastUtils {
	public static String castString(Object obj){
		return CastUtils.castString(obj, "");
	}
	
	public static String castString(Object obj, String defaultValue){
		return obj != null ? String.valueOf(obj) :defaultValue;
	}
	
	public static double castDouble(Object obj){
		return CastUtils.castDouble(obj, 0);
	}
	
	public static double castDouble(Object obj, double defaultValue){
		double doubleValue = defaultValue;
		if(obj != null){
			String strValue = castString(obj);
			if(StringUtils.isNotEmpty(strValue)){
				try {
					doubleValue = Double.parseDouble(strValue);
				} catch (NumberFormatException e) {
					doubleValue = defaultValue;
				}
			}
		}
		
		return doubleValue;
	}
	
	public static long castLong(Object obj){
		return CastUtils.castLong(obj, 0);
	}
	
	public static long castLong(Object obj, long defaultValue){
		long longValue = defaultValue;
		if(obj != null){
			String strValue = castString(obj);
			if(StringUtils.isNotEmpty(strValue)){
				try {
					longValue = Long.parseLong(strValue);
				} catch (NumberFormatException e) {
					longValue = defaultValue;
				}
			}
		}
		
		return longValue;
	}
	
	public static int castInt(Object obj){
		return CastUtils.castInt(obj, 0);
	}
	
	public static int castInt(Object obj, int defaultValue){
		int intValue = defaultValue;
		if(obj != null){
			String strValue = castString(obj);
			if(StringUtils.isNotEmpty(strValue)){
				try {
					intValue = Integer.parseInt(strValue);
				} catch (NumberFormatException e) {
					intValue = defaultValue;
				}
			}
		}
		
		return intValue;
	}
	
	public static boolean castBoolean(Object obj){
		return CastUtils.castBoolean(obj, false);
	}
	
	public static boolean castBoolean(Object obj, boolean defaultValue){
		boolean booleanValue = defaultValue;
		if(obj != null){
			String strValue = castString(obj);
			if(StringUtils.isNotEmpty(strValue)){
				try {
					booleanValue = Boolean.parseBoolean(strValue);
				} catch (NumberFormatException e) {
					booleanValue = defaultValue;
				}
			}
		}
		
		return booleanValue;
	}
}
