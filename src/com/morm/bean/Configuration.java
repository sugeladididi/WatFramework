package com.morm.bean;

/**
 * @Description 管理配置信息
 * @author 李福涛
 * @version 1.0
 *
 */
public class Configuration {
	private String driver;
	private String url;
	private String username;
	private String password;
	private int jdbcPoolInitSize;

	public Configuration(String driver, String url, String username, String password, int jdbcPoolInitSize) {
		super();
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		this.jdbcPoolInitSize = jdbcPoolInitSize;
	}

	public Configuration() {
		super();
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getJdbcPoolInitSize() {
		return jdbcPoolInitSize;
	}

	public void setJdbcPoolInitSize(int jdbcPoolInitSize) {
		this.jdbcPoolInitSize = jdbcPoolInitSize;
	}

}
