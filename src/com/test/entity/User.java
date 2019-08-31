package com.test.entity;

/**
 * @Description实体类，封装User实体，成员变量名必须和表字段名相同
 * @author 李福涛
 *
 */
public class User {

	private Integer user_id;
	private String user_name;
	private String user_pwd;
	private Integer user_tell;

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(Integer user_id, String user_name, String user_pwd, Integer user_tell) {
		super();
		this.user_id = user_id;
		this.user_name = user_name;
		this.user_pwd = user_pwd;
		this.user_tell = user_tell;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_pwd() {
		return user_pwd;
	}

	public void setUser_pwd(String user_pwd) {
		this.user_pwd = user_pwd;
	}

	public Integer getUser_tell() {
		return user_tell;
	}

	public void setUser_tell(Integer user_tell) {
		this.user_tell = user_tell;
	}

	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", user_name=" + user_name + ", user_pwd=" + user_pwd + ", user_tell="
				+ user_tell + "]";
	}

}
