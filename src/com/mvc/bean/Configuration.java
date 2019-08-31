package com.mvc.bean;

/**
 * @Description 管理配置信息
 * @author 李福涛
 * @version 1.0
 *
 */
public class Configuration {
	private String basePackage;

	public Configuration() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Configuration(String basePackage) {
		super();
		this.basePackage = basePackage;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
	
	

}
