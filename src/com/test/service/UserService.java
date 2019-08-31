package com.test.service;

import java.io.Serializable;

import com.test.entity.User;

/**
 * @Description 自定义Service类
 * @author 李福涛
 *
 */
public interface UserService{
	
	/**
	 * 插入用户信息方法
	 * @param user 待插入的User信息类
	 * @return boolean值
	 */
	boolean addUser(User user);
	
	boolean deleteUser(int id);
	
	boolean updateUser(User user);
	
	User selectUser(int id);
	
	
	
}
