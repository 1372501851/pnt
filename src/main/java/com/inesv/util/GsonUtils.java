package com.inesv.util;

import com.google.gson.Gson;

/**
 * @author: xujianfeng
 * @create: 2018-05-03 17:36
 **/
public class GsonUtils {
    private final static Gson GSON = new Gson();

    public static String toJson(Object obj){
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> t){
        return GSON.fromJson(json, t);
    }
}
