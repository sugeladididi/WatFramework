package com.test.service.Impl;

import java.io.Serializable;

import com.morm.core.SqlSession;
import com.mvc.annotation.Autowired;
import com.mvc.annotation.Service;
import com.test.dao.IUserDao;
import com.test.entity.User;
import com.test.service.UserService;
import com.util.LogUtil;

/**
 * @Description
 * @author 李福涛
 *
 */
@Service
public class UserServiceImpl implements UserService, Serializable {

	@Autowired
	IUserDao mapper;

	@Override
	public User selectUser(int id) {

		User user = null;
		try {
			mapper = SqlSession.getMapper(IUserDao.class);
			user = mapper.selectUser(id);
			LogUtil.info("查询到的user:" + user);
		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException e) {
			LogUtil.error("查询失败！", e);
		}
		return user;
	}

	@Override
	public boolean addUser(User user) {

		int inflLine = 0;
		try {
			mapper = SqlSession.getMapper(IUserDao.class);
			inflLine = mapper.insertUser(user.getUser_name(), user.getUser_pwd(), user.getUser_tell());
			LogUtil.info("插入行数:" + inflLine);
		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException e) {
			LogUtil.error("添加用户信息失败！", e);
		}

		return inflLine > 0 ? true : false;

	}

	@Override
	public boolean deleteUser(int id) {
		boolean b = false;
		try {
			mapper = SqlSession.getMapper(IUserDao.class);
			b = mapper.deleteUser(id);
			LogUtil.info("删除成功！");
		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException e) {
			LogUtil.error("删除用户信息失败！", e);
		}

		return b;
	}

	@Override
	public boolean updateUser(User user) {
		boolean b = false;
		try {
			mapper = SqlSession.getMapper(IUserDao.class);
			b = mapper.updateUser(user.getUser_id(), user.getUser_name(), user.getUser_pwd(), user.getUser_tell());
			LogUtil.info("更新行数:" + b);
		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException e) {
			LogUtil.error("更新用户信息失败！", e);
		}

		return b;
	}

}
