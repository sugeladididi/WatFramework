package com.test.dao;

import java.util.List;

import com.test.entity.User;

/**
 * @Description IDao接口类
 * @author 李福涛
 *
 */
public interface IDao<T> {
	
	/**
	 * 查询所有
	 * @return
	 */
	public List<T> queryAll();
	
	/**
	 * 通过Id查找
	 * @param id
	 * @return
	 */
	public T selectById(int id);
	
	/**
	 * 通过Id删除
	 * @param id
	 * @return
	 */
	public boolean deleteById(int id);
	
	/**
	 * 添加一个bean
	 * @param t
	 * @return
	 */
	public boolean insert(T t);
	

}
