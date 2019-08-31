package com.test.dao;

import java.util.List;

import org.omg.CORBA.portable.Delegate;

import com.morm.annotation.MyDelete;
import com.morm.annotation.MyInsert;
import com.morm.annotation.MyParam;
import com.morm.annotation.MySelect;
import com.morm.annotation.MyUpdate;
import com.test.entity.User;

/**
 * @Description 针对User表定制的Dao接口
 * @author 李福涛
 * @version 1.0  
 *
 */
public interface IUserDao extends IDao{
	
	@MyInsert("insert into tb_users(user_name, user_pwd, user_tell) values(#{name}, #{pwd}, #{tell})")
	public int insertUser(@MyParam("name") String name,
                          @MyParam("pwd") String pwd,
                          @MyParam("tell") Integer tell);
	
	@MyDelete("delete from tb_users where user_id=#{id}")
	public boolean deleteUser(@MyParam("id") Integer id);
	
	@MyUpdate("update tb_users set user_name=#{name},user_pwd=#{pwd}, user_tell=#{tell} where user_id=#{id}")
	public boolean updateUser(@MyParam("id") Integer id,
                              @MyParam("name") String name,
                              @MyParam("pwd") String pwd,
                              @MyParam("tell") Integer tell);
	
	@MySelect("select user_id, user_name, user_pwd, user_tell from tb_users where user_id=#{id}")
	public User selectUser(@MyParam("id") Integer id);
	
}
