package com.morm.core;

import java.lang.reflect.Proxy;

/**
 * @Description 动态代理模式
 * @author 李福涛
 * @version 1.0  
 *
 */
public class SqlSession {
 
	// 获取getMapper
	public static <T> T getMapper(Class<T> clazz)
		throws IllegalArgumentException, InstantiationException, IllegalAccessException {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz },
				new MyInvocationHandlerMbatis(clazz));
	}
}
