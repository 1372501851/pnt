package com.inesv.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class UrlCodeUtil {
	private UrlCodeUtil() {

	}

	public static String encode(String str) {
		try {
			return URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	public static String decode(String str) {
		try {
			return URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}
	public static void main(String[] args){
		System.out.println(encode("!@#"));
	}
}
