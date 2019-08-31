package com.startup;

import com.container.catalina.EmbedTomcat;
import com.mvc.annotation.*;
import com.util.*;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import org.apache.catalina.LifecycleException;

import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description
 * @author 李福涛
 * @version 1.0
 *
 */
public class Startup {

	private EmbedTomcat embedTomcat;

	//
	private static final String IOCCACHE = Globals.IOCCACHE;
//	private static final String METHODCACHE = Globals.METHODCACHE;

	// 存放所有方法的map
	private static Map<String, Method> allMethodsMap = new HashMap<>();

	// 存放字节码文件的map
	private Map<String, Class<?>> classMap = new HashMap<>();

	/**
	 * 加载catalina容器
	 * 
	 * @Description
	 * @throws LifecycleException
	 */
	public void doLoadCatalina(Map<String, Method> allMethodsMap) throws LifecycleException {
		if (embedTomcat == null) {
			embedTomcat = new EmbedTomcat(allMethodsMap);
			embedTomcat.start();
		}
	}

	
	/**
	 * 并发扫描包
	 * 
	 * @Description
	 */
	public void doScannerClass() {

		LogUtil.info("doScannerClass()");
		// 获取线程池
		ThreadPoolExecutor executor = ExecutorUtil.getExecutor();
		ArrayList<Future<Map<String, Class<?>>>> arraySubmit = new ArrayList<Future<Map<String, Class<?>>>>();

		// 获取配置信息
		Properties properties = new Properties();
		properties = ParseConfiguration.parse("simplemvc.properties");

		// 通过properties获得basePackage的值：com.controller
		String propertie = properties.getProperty("basePackage");
		// 按“,”拆分
		String[] basePackages = propertie.split(",");

		// 并发扫包
		for (int i = 0; i < basePackages.length; i++) {
			// 去掉空格
			String basePackage = basePackages[i].trim();
			Callable<Map<String, Class<?>>> callable = new Callable<Map<String, Class<?>>>() {
				public Map<String, Class<?>> call() throws Exception {
					Map<String, Class<?>> classMap = ClassScanner.scannerClass(basePackage);
					return classMap;
				};
			};
			Future<Map<String, Class<?>>> submit = executor.submit(callable);
			arraySubmit.add(submit);
		}

		// 获取并发结果
		for (Future<Map<String, Class<?>>> submit : arraySubmit) {
			Map<String, Class<?>> map;
			try {
				map = submit.get();
				classMap.putAll(map);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 加载Ioc容器
	 * 
	 * @Description
	 */
	public void doLoadIoc() {

		LogUtil.info("DispatcherServlet.initHandlerMapping()");

		// 将结果转换为 map.entry
		Set<Map.Entry<String, Class<?>>> entries = classMap.entrySet();

		for (Map.Entry<String, Class<?>> entry : entries) {
			String className = entry.getKey();
			String beanName = className;
			Class<?> clazz = entry.getValue();
			String path = "";
			try {
				// 判断这个类是否标记了Controller注解
				if (clazz.isAnnotationPresent(Controller.class)) {
					// 判断这个类是否标记了RequestMapping注解
					if (clazz.isAnnotationPresent(RequestMapping.class)) {
						// 获得该注解
						RequestMapping reqAnno = clazz.getAnnotation(RequestMapping.class);
						// 获得该注解的属性值，即路径
						path = reqAnno.value();
					}

					// 将这个类存入map中，key为类名，value为实例化后的类
					CacheUtil.insert(IOCCACHE, className, clazz.newInstance());
					// allClassMap.put(className, clazz.newInstance());

					// 获得这个类的所有方法
					Method[] ms = clazz.getMethods();
					for (Method method : ms) {
						// 判断这个方法是否标记了RequestMapping注解
						if (method.isAnnotationPresent(RequestMapping.class)) {
							// 直接将其拼接为完整的uri
							String requestMappingPath = path + method.getAnnotation(RequestMapping.class).value();
							// 将这个完整的uri存入map中，key为完整uri，value为该映射后的方法
							
//							SerializableObject serializableObject = new SerializableObject(method);
//							CacheUtil.insert(METHODCACHE, requestMappingPath, serializableObject);
							allMethodsMap.put(requestMappingPath, method);
						}
					}
				}

				// 判断这个类是否标记了Service注解
				if (clazz.isAnnotationPresent(Service.class)) {
					Service myService = clazz.getAnnotation(Service.class);
					String myServiceValue = myService.value();
					// 如果注解的value有值，则取其值命名，即byName注入
					// String beanName = toLowerFirstWord(className);
					if (!"".equals(myServiceValue.trim())) {
						beanName = myServiceValue;
					}
					Object instance = clazz.newInstance();
					beanName = toLowerFirstWord(beanName);
					CacheUtil.insert(IOCCACHE, beanName, instance);
					// allClassMap.put(beanName, instance);
					// 如果有接口，则同时以接口名为key存入
					Class[] interfaces = clazz.getInterfaces();
					for (Class<?> i : interfaces) {
						CacheUtil.insert(IOCCACHE, toLowerFirstWord(i.getName()), instance);
						// allClassMap.put(i.getName(), instance);
					}
				}

				// 判断这个类是否标记了Repository注解
				if (clazz.isAnnotationPresent(Repository.class)) {
					Repository myRepository = clazz.getAnnotation(Repository.class);
					String myRepositoryValue = myRepository.value();
					if (!"".equals(myRepositoryValue.trim())) {
						beanName = myRepositoryValue;
					}
					Object instance = clazz.newInstance();
					CacheUtil.insert(IOCCACHE, beanName, instance);
					// allClassMap.put(beanName, instance);
					Class[] interfaces = clazz.getInterfaces();
					for (Class<?> i : interfaces) {
						CacheUtil.insert(IOCCACHE, i.getName(), instance);
						// allClassMap.put(i.getName(), instance);
					}
				}

			} catch (InstantiationException e) {
				LogUtil.error(className + " InstantiationException!");
			} catch (IllegalAccessException e) {
				LogUtil.error(className + " IllegalAccessException!");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 向Ioc容器中注入bean
	 */
	private void doAutowired() {

		LogUtil.info("DispatcherServlet.doAutowired()");

		Tuple tuple = new Tuple();
		TupleBrowser browser;
		try {
			browser = CacheUtil.getTupleBrowser(IOCCACHE);
			while (browser.getNext(tuple)) {
				// 包括私有的方法
				Field[] fields = tuple.getValue().getClass().getDeclaredFields();
				for (Field field : fields) {

					// 保证有AutoWired注解
					if (!field.isAnnotationPresent(Autowired.class)) {
						continue;
					}
					// 获得这个注解
					Autowired autowired = field.getAnnotation(Autowired.class);
					// 获得bean名称
					String beanName = autowired.value().trim();
					// 如果bean值为空，则为byType注入
					if ("".equals(beanName)) {
						beanName = field.getType().getName();
						LogUtil.debug("beanName is " + beanName);
					}
					// 设置为可获取private变量
					field.setAccessible(true);
					try {
						Object value = CacheUtil.getValue(IOCCACHE, beanName);
						if (value != null) {
							// 将bean注入到类中
							Object value2 = tuple.getValue();
							field.set(value2, value);
							CacheUtil.update(IOCCACHE, (String)tuple.getKey(), value2);
						} else {
							LogUtil.error(beanName + "在注入时为空！");
						}

					} catch (Exception e) {
						LogUtil.error(" ", e);
						continue;
					}
				}

				System.out.println(tuple.getKey() + " " + tuple.getValue().toString());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * 将字符串的首字母改为小写
	 * 
	 * @param name
	 *            将要改写的字符串
	 * @return 首字母改为小写后的字符串
	 */
	private String toLowerFirstWord(String name) {
		int lastIndexOf = name.lastIndexOf(".");
		char[] charArray = name.toCharArray();
		charArray[lastIndexOf+1] += 32;
		return String.valueOf(charArray);
	}

	public static void main(String[] args) throws LifecycleException, ServletException {

		Startup startup = new Startup();
		// 并发扫描类包
		startup.doScannerClass();
		// 加载Ioc容器到缓存
		startup.doLoadIoc();
		// 向Ioc缓存注入
		startup.doAutowired();

//		Tuple tuple = new Tuple();
//		TupleBrowser browser;
//		try {
//			browser = CacheUtil.getTupleBrowser(IOCCACHE);
//			while (browser.getNext(tuple)) {
//				System.out.println(tuple.getKey() + "===========" + tuple.getValue());
//			}
//		} catch (Exception e) {
//
//		}
		
		// 启动Catalina容器
		startup.doLoadCatalina(allMethodsMap);
	}
}
