package com.mvc.servlet;

import com.mvc.annotation.RequestParam;
import com.mvc.annotation.Scope;
import com.mvc.controller.BaseController;
import com.startup.Globals;
import com.util.CacheUtil;
import com.util.LogUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 请求转发处理器
 * @author 李福涛
 * 
 */
public class DispatcherServlet extends HttpServlet {

	/**
	 * @Description
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// private static final String METHODCACHE = Globals.METHODCACHE;
	private static final String IOCCACHE = Globals.IOCCACHE;
	private Map<String, Method> allMethodsMap = new HashMap<>();

	public DispatcherServlet(Map<String, Method> allMethodsMap) {
		super();
		this.allMethodsMap = allMethodsMap;


	}


	public DispatcherServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// 及析出请求中的方法
			Method method = handMethod(request, response);

			// 通过方法名获得方法所在的控制器实例
			BaseController controller = handController(method);

			// 控制器初始化
			controller.init(request, response);

			// 获得方法参数
			Object[] paramValues = handParams(request, response, method);

			// 通过参数和所在控制器调用该方法
			method.invoke(controller, paramValues);

		} catch (InstantiationException | IllegalAccessException e) {
			LogUtil.error("method.getDeclaringClass().newInstance() was Error", e);
		} catch (IllegalArgumentException e) {
			LogUtil.error("method.invoke was Error", e);
		} catch (InvocationTargetException e) {
			LogUtil.error("method.invoke was Error", e);
		}
	}

	/**
	 * 处理传入方法的参数
	 * 
	 * @param request
	 *            请求
	 * @param response
	 *            应答
	 * @param method
	 *            待处理的方法
	 * @return 处理后的参数集合
	 */
	private Object[] handParams(HttpServletRequest request, HttpServletResponse response, Method method) {
		// 获得方法所有参数的类型
		Class<?>[] paramClazzs = method.getParameterTypes();

		// 按照参数个数新建Object集合用于存放值
		Object[] args = new Object[paramClazzs.length];

		int args_i = 0;
		int index = 0;
		for (Class<?> paramClazz : paramClazzs) {
			// 判断paramClazz是否是ServlerRequest的子对象或子接口
			if (ServletRequest.class.isAssignableFrom(paramClazz)) {
				// 将request参数存入args参数列表后，计数器加一
				args[args_i++] = request;
			}
			if (ServletResponse.class.isAssignableFrom(paramClazz)) {
				args[args_i++] = response;
			}
			// 获得该参数的注解
			Annotation[] paramAns = method.getParameterAnnotations()[index];
			// 如果标记了RequestParam注解
			if (paramAns.length > 0) {
				for (Annotation paramAn : paramAns) {
					// 如果是RequestParam注解
					if (RequestParam.class.isAssignableFrom(paramAn.getClass())) {
						// 则获得该注解的值，并存入参数列表中，计数器加一
						RequestParam rp = (RequestParam) paramAn;
						// 获得参数名
						String paramName = rp.value();
						// 获得参数值
						String paramValue = request.getParameter(paramName);
						// 获得参数类型名
						String paramType = paramClazz.getName();
						// 根据类型名进行强转型
						if ("int".equals(paramType) || paramType.endsWith("Integer")) {
							if (!"".equals(paramValue)) {
								args[args_i++] = Integer.parseInt(paramValue);
							} else {
								args[args_i++] = null;
							}
						} else {
							args[args_i++] = paramValue;
						}
					}
				}
			}
			index++;
		}
		return args;
	}

	/**
	 * 解析出请求的方法
	 * 
	 * @param request
	 *            请求参数
	 * @param response
	 *            应答参数
	 * @return method
	 * @throws IOException
	 */
	private Method handMethod(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// 获得包含web应用名的资源名:/Day2_MVC/index.action
		String uri = request.getRequestURI();
		// 获得web应用名：/Day2_MVC
		String contextPath = request.getContextPath();
		// 获得:/index
		String requestMappingPath = uri.substring(contextPath.length(), uri.indexOf("."));
		// 获得:/index 路径的方法

		// SerializableObject serializableObject = (SerializableObject)
		// CacheUtil.getValue(METHODCACHE, requestMappingPath);
		// Method method = (Method) serializableObject.deserialize();

		Method method = allMethodsMap.get(requestMappingPath);
		if (method == null) {
			response.sendError(404);
		}
		return method;
	}

	/**
	 * 解析出方法所在的控制器
	 * 
	 * @param method
	 *            将要解析的方法
	 * @return 从方法中解析出的控制器
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private BaseController handController(Method method) throws InstantiationException, IllegalAccessException {
		BaseController controller = null;
		// 返回表示声明由此Method对象表示的方法的类的Class对象
		Class<?> requestClass = method.getDeclaringClass();
		// 判断该类是否标记了scope注解,默认为单例
		if (requestClass.isAnnotationPresent(Scope.class)
				&& requestClass.getAnnotation(Scope.class).value().equals("prototype")) {
			controller = (BaseController) method.getDeclaringClass().newInstance();
		} else {
			 try {
				controller = (BaseController)
				 CacheUtil.getValue(IOCCACHE, method.getDeclaringClass().getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return controller;
	}

}
