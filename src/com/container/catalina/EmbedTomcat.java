package com.container.catalina;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import com.mvc.servlet.DispatcherServlet;
import com.util.LogUtil;



/**
 * @Description
 * @author 李福涛
 * @version 1.0
 *
 */ 
public class EmbedTomcat {

	private Properties properties = null;
	private Tomcat tomcat = null;
	
	private Map<String, Method> allMethodsMap;
	
	public EmbedTomcat() {
		super();
	}

	public EmbedTomcat(Map<String, Method> allMethodsMap) {
		super();
		this.allMethodsMap = allMethodsMap;
	}

	// 默认配置
	private int port = 8080;
	private String webappDir = "WebRoot";
	private String contextDir = "EmbedTomcat";
	private String encoding = "UTF-8";

	public void start() throws LifecycleException {

		loadConfig();

		tomcat = new Tomcat();

		// 配置tomcat访问端口和编码格式
		Connector connector = new Connector();
		connector.setURIEncoding(encoding);
		connector.setPort(port);
		tomcat.getService().addConnector(connector);

		// 配置servlet
		Context context = tomcat.addContext(contextDir, null);
		tomcat.addServlet(context, "myServlet", new DispatcherServlet(allMethodsMap));
		context.addServletMappingDecoded("*.do", "myServlet");
		

		// 配置appbase目录
		String basePath = System.getProperty("user.dir") + File.separator;
		tomcat.getHost().setAppBase(basePath + ".");
		tomcat.addWebapp("", webappDir);

		// 启动tomcat
		tomcat.start();
		tomcat.getServer().await();
		
		
		
//		// 创建tomcat服务器
//		Tomcat tomcatServer = new Tomcat();
//		// 指定端口号
//		tomcatServer.setPort(PORT);
//		// 是否设置自动部署
//		tomcatServer.getHost().setAutoDeploy(false);
//		// 创建上下文
//		StandardContext standardContex = new StandardContext();
//		standardContex.setPath(CONTEX_PATH);
//		// 监听上下文
//		standardContex.addLifecycleListener(new FixContextListener());
//		// tomcat容器添加standardContex
//		tomcatServer.getHost().addChild(standardContex);
//
//		// 创建Servlet
//		tomcatServer.addServlet(CONTEX_PATH, SERVLET_NAME, new IndexServlet());
//		// servleturl映射
//		standardContex.addServletMappingDecoded("/index", SERVLET_NAME);
//		tomcatServer.start();
//		System.out.println("tomcat服务器启动成功..");
//		// 异步进行接收请求
//		tomcatServer.getServer().await();
	}

	private void loadConfig() {
		this.properties = ParseConfiguration.parse("tomcat.properties");
		String port = properties.getProperty("tomcat.port");
		String webappDir = properties.getProperty("tomcat.webappDir");
		String contextDir = properties.getProperty("tomcat.contextDir");
		String encoding = properties.getProperty("tomcat.encoding");

		if (port != null) {
			this.port = Integer.parseInt(port);
			LogUtil.debug("tomcat.port : " + port);
		}
		if (webappDir != null) {
			this.webappDir = webappDir;
			LogUtil.debug("tomcat.webappDir : " + webappDir);
		}
		if (contextDir != null) {
			this.contextDir = contextDir;
			LogUtil.debug("tomcat.contextDir : " + contextDir);
		}
		if (encoding != null) {
			this.encoding = encoding;
			LogUtil.debug("tomcat.encoding : " + encoding);
		}
	}

}
