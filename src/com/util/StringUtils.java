package com.util;

/**
 * 
 * @Description 字符串解析类
 * @author 李福涛
 * @version 1.0  
 *
 */
public class StringUtils {

	public static boolean isNotBlank(String str) {
		if (str != null && str.trim().length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isBlank(String str) {
		return !isNotBlank(str);
	}
}
