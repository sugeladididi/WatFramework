package com.container.catalina;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Description 
 * @author 李福涛
 * @version 1.0  
 *
 */
public class ParseConfiguration {
	

	public static Properties parse(String location) {
		
		Properties propertie = null;

		// 把web.xml中的contextConfigLocation对应value值的文件加载到流里面
		InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);
		try {
			// 用Properties文件加载文件里的内容
			propertie = new Properties();
			propertie.load(resourceAsStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关流
			if (null != resourceAsStream) {
				try {
					resourceAsStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return propertie;
	}

}
