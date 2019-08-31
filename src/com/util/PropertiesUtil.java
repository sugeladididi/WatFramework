package com.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @Description
 * @author 李福涛
 * @version 1.0
 *
 */
public class PropertiesUtil {

	private static Properties properties = null;

	public static String getProperty(String name) {
		return properties.getProperty(name);
	}

	/**
	 * @Description 加载配置文件到系统
	 */
	private static void loadProperites(String fileName) {

		InputStream is = null;

		if (is == null) {
			try {
				is = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (is != null) {
			try {
				properties = new Properties();
				properties.load(is);
			} catch (IOException e) {
				LogUtil.error("" + e);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					LogUtil.warn("无法关闭" + fileName + "文件流!");
				}
			}
		}

		if ((is == null)) {
			LogUtil.warn("配置文件" + fileName + "加载失败！");
			properties = new Properties();
		}

		Enumeration<?> enumeration = properties.propertyNames();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			String value = properties.getProperty(name);
			if (value != null) {
				System.setProperty(name, value);
			}
		}

	}
}
