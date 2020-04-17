package com.inesv.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JSONFormat {
	public static JSONObject json=new JSONObject();	//定义一个描述json的数据
	public static Object getStr(Object str){
		return json.parse(json.toJSONString(str,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteNullNumberAsZero,
				SerializerFeature.WriteDateUseDateFormat));
	}
}
