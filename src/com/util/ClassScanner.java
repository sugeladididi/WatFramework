package com.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 工具类，用于扫描指定包下所有.class文件
 * @author 李福涛
 *
 */
public class ClassScanner {

	public static Map<String, Class<?>> scannerClass(String basePackage) {
		
		// 用于保存解析后的结果
		Map<String, Class<?>> result = new HashMap<>();
		// 将com.mvc转换成 com/mvc
		String basePath = basePackage.replaceAll("\\.", "/");
		try {
			// 返回com/mvc的绝对地址 /D:/MyWorkspace/everyDay/Day2_MVC/com/mvc
			String rootPath = ClassScanner.class.getClassLoader().getResource(basePath).getPath();
			// 只保留com/mvc 
			if (rootPath != null)
				rootPath = rootPath.substring(rootPath.indexOf(basePath));
			
			Enumeration<URL> enumeration = ClassScanner.class.getClassLoader().getResources(basePath);
			
			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();
				if (url.getProtocol().equals("file")) {
					File file = new File(url.getPath().substring(1));
					// file:D:\MyWorkspace\everyDay\Day4_WatFramework\bin\com\test
					// rootPath:rootPath
					// result:{}
					scannerFile(file, rootPath, result);
				}
			}
		} catch (IOException e) {
			LogUtil.error(" ", e);
		}
		return result;
	}

	/**
	 * 
	 * @Description 
	 * @param folder
	 * @param rootPath
	 * @param classes
	 */
	private static void scannerFile(File folder, String rootPath, Map<String, Class<?>> classes) {
		try {
			// 获取folder下的所有文件
			File[] files = folder.listFiles();
			for (int i = 0; files != null && i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					String newrootPath = rootPath + "/" + file.getName() + "/";
					scannerFile(file, newrootPath, classes);
				} else {
					if (file.getName().endsWith(".class")) {
						String fileName = file.getName();
						String className = (rootPath + "/" + fileName).replaceAll("//", ".").replaceAll("/", ".");
						className = className.substring(0, className.indexOf(".class"));
						classes.put(className, Class.forName(className));
					}
				}
			}
		} catch (Exception e) {
			LogUtil.error(" ", e);
		}
	}

	
	/**
	 * 获取类加载器
	 *
	 * @param 设定文件
	 * @return ClassLoader 返回类型
	 * @throws @Title:
	 *             getClassLoader
	 */
	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	/**
	 * 加载类（默认将初始化类）
	 */
	public static Class<?> loadClass(String className) {
		return loadClass(className, true);
	}
	
	/**
	 * 加载类 需要提供类名与是否初始化的标志， 初始化是指是否执行静态代码块
	 *
	 * @param className
	 * @param isInitialized
	 *            为提高性能设置为false
	 * @param 设定文件
	 * @return Class<?> 返回类型
	 * @throws @Title:
	 *             loadClass
	 * @Description:
	 */
	public static Class<?> loadClass(String className, boolean isInitialized) {
		
		Class<?> cls;
		try {
			cls = Class.forName(className, isInitialized, getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		return cls;
	}
}
