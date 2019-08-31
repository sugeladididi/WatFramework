package com.morm.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import com.morm.annotation.MyDelete;
import com.morm.annotation.MyInsert;
import com.morm.annotation.MyParam;
import com.morm.annotation.MySelect;
import com.morm.annotation.MyUpdate;
import com.morm.utils.JDBCUtils;
import com.morm.utils.SQLUtils;
import com.util.LogUtil;

/**
 * @Description 动态代理实现类
 * @author 李福涛
 * @version 1.0
 *
 */
public class MyInvocationHandlerMbatis implements InvocationHandler {

	/**
	 * 这个就是我们要代理的真实对象
	 */
	private Object subject;

	/**
	 * 构造方法，给我们要代理的真实对象赋初值
	 * 
	 * @param subject
	 */
	public MyInvocationHandlerMbatis(Object subject) {
		this.subject = subject;
	}

	/**
	 * 该方法负责集中处理动态代理类上的所有方法调用。 调用处理器根据这三个参数进行预处理或分派到 委托类实例上反射执行
	 * 
	 * @param proxy
	 *            代理类实例
	 * @param method
	 *            被调用的方法对象
	 * @param args
	 *            调用参数
	 * @return
	 * @throws Throwable
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		// 判断方法上是否有Insert注解
		MyInsert myInsert = method.getAnnotation(MyInsert.class);
		if (myInsert != null) {
			return insertSQL(myInsert, method, args);
		}

		// 判断方法上是否有Select注解
		MySelect mySelect = method.getAnnotation(MySelect.class);
		if (mySelect != null) {
			return selectSQL(mySelect, method, args);
		}

		// 判断方法上是否有Delete注解
		MyDelete myDelete = method.getAnnotation(MyDelete.class);
		if (myDelete != null) {
			return deleteSQL(myDelete, method, args);
		}

		// 判断方法上是否有Update注解
		MyUpdate myUpdate = method.getAnnotation(MyUpdate.class);
		if (myUpdate != null) {
			return updateSQL(myUpdate, method, args);
		}

		return null;
	}

	/**
	 * @Description
	 * @param myUpdate
	 * @param method
	 * @param args
	 * @return
	 * @throws SQLException 
	 */
	private Object updateSQL(MyUpdate myUpdate, Method method, Object[] args) throws SQLException {
		// 获取注解上的sql
		String updateSql = myUpdate.value();
		LogUtil.debug("sql:" + updateSql);
		// 获取method方法上所有的参数名
		Parameter[] parameters = method.getParameters();
		// 将方法上的参数名和参数值存放在Map集合中
		ConcurrentHashMap<Object, Object> parameterMap = getMyParams(parameters, args);

		// 通过SQL拆分工具类获取SQL语句上需要传递的参数数组
		List<String> sqlUpdateParameter = SQLUtils.sqlUpdateParameter(updateSql);

		// 排序参数
		List<Object> parameValues = new ArrayList<>();
		for (int i = 0; i < sqlUpdateParameter.size(); i++) {
			String parameterName = sqlUpdateParameter.get(i);
			Object object = parameterMap.get(parameterName);
			parameValues.add(object);
		}

		// 变为?号
		String newSql = SQLUtils.parameQuestion(updateSql, sqlUpdateParameter);
		LogUtil.debug("执行SQL:" + newSql + "参数信息:" + parameValues.toString());

		// 调用JDBC代码查询
		boolean b = JDBCUtils.execute(newSql, parameValues);
		return b;

	}

	/**
	 * @Description
	 * @param myDelete
	 * @param method
	 * @param args
	 * @return
	 */
	private Object deleteSQL(MyDelete myDelete, Method method, Object[] args) {
		try {
			// 获取注解上的sql
			String deleteSql = myDelete.value();
			LogUtil.debug("sql:" + deleteSql);
			// 获取method方法上所有的参数名
			Parameter[] parameters = method.getParameters();
			// 将方法上的参数名和参数值存放在Map集合中
			ConcurrentHashMap<Object, Object> parameterMap = getMyParams(parameters, args);

			// 通过SQL拆分工具类获取SQL语句上需要传递的参数数组
			List<String> sqlDeleteParameter = SQLUtils.sqlDeleteParameter(deleteSql);

			// 排序参数
			List<Object> parameValues = new ArrayList<>();
			for (int i = 0; i < sqlDeleteParameter.size(); i++) {
				String parameterName = sqlDeleteParameter.get(i);
				Object object = parameterMap.get(parameterName);
				parameValues.add(object);
			}

			// 变为?号
			String newSql = SQLUtils.parameQuestion(deleteSql, sqlDeleteParameter);
			LogUtil.debug("执行SQL:" + newSql + "参数信息:" + parameValues.toString());

			// 调用JDBC代码查询
			boolean b = JDBCUtils.execute(newSql, parameValues);
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @Description
	 * @param myInsert
	 *            注解类
	 * @param method
	 *            方法
	 * @param args
	 *            方法的参数值
	 * @return 影响的数据行数
	 * @throws SQLException
	 */
	private int insertSQL(MyInsert myInsert, Method method, Object[] args) throws SQLException {
		// 获取注解上的sql
		String insertSql = myInsert.value();
		LogUtil.debug("sql:" + insertSql);
		// 获取method方法上所有的参数名
		Parameter[] parameters = method.getParameters();
		// 将方法上的参数名和参数值存放在Map集合中
		ConcurrentHashMap<Object, Object> parameterMap = getMyParams(parameters, args);

		// 通过SQL拆分工具类获取SQL语句上需要传递的参数数组
		String[] sqlParameter = SQLUtils.sqlInsertParameter(insertSql);

		// 用于存放属性的链表
		List<Object> parameValues = new ArrayList<>();
		for (int i = 0; i < sqlParameter.length; i++) {
			String str = sqlParameter[i];
			Object object = parameterMap.get(str);
			parameValues.add(object);
		}

		// 将SQL语句替换为？号
		LogUtil.debug("insertSql:" + insertSql);
		LogUtil.debug("sqlParameter:" + sqlParameter);
		String newSql = SQLUtils.parameQuestion(insertSql, sqlParameter);

		LogUtil.debug("newSql:" + newSql);
		// 调用jdbc代码执行
		int insertResult = JDBCUtils.insert(newSql, false, parameValues);
		return insertResult;
	}

	/**
	 * 
	 * @Description
	 * @param myInsert
	 * @param method
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	private Object selectSQL(MySelect myInsert, Method method, Object[] args) throws SQLException {
		try {
			// 获取查询SQL语句
			String selectSQL = myInsert.value();
			// 将方法上的参数存放在Map集合中
			Parameter[] parameters = method.getParameters();
			// 获取方法上参数集合
			ConcurrentHashMap<Object, Object> parameterMap = getMyParams(parameters, args);

			// 获取SQL传递参数
			List<String> sqlSelectParameter = SQLUtils.sqlSelectParameter(selectSQL);
			
			// 排序参数
			List<Object> parameValues = new ArrayList<>();
			for (int i = 0; i < sqlSelectParameter.size(); i++) {
				String parameterName = sqlSelectParameter.get(i);
				Object object = parameterMap.get(parameterName);
				parameValues.add(object.toString());
			}

			// 变为?号
			String newSql = SQLUtils.parameQuestion(selectSQL, sqlSelectParameter);
			LogUtil.debug("执行SQL:" + newSql + "参数信息:" + parameValues.toString());

			// 调用JDBC代码查询
			ResultSet rs = JDBCUtils.query(newSql, parameValues);
			// 获取返回类型
			Class<?> returnType = method.getReturnType();
			if (!rs.next() || rs == null) {
				// 没有查找数据
				LogUtil.error("没有数据返回");
				return null;
			}
			// 向上移动
			rs.previous();
			// 实例化对象

			// 获得返回类型的所有成员变量
			Field[] declaredFields = returnType.getDeclaredFields();
			Object newInstance = returnType.newInstance();
			while (rs.next()) {
				for (Field declaredField : declaredFields) {
					String fieldName = declaredField.getName();
					// 获取集合中数据
					Object value = rs.getObject(fieldName);
					// 查找对应属性
					Field field = returnType.getDeclaredField(fieldName);
					// 设置允许私有访问
					field.setAccessible(true);
					// 赋值参数
					field.set(newInstance, value);
				}
			}
			return newInstance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @Description 获取方法上参数集合
	 * @param parameters
	 * @param args
	 * @return
	 */
	private ConcurrentHashMap<Object, Object> getMyParams(Parameter[] parameters, Object[] args) {
		ConcurrentHashMap<Object, Object> parameterMap = new ConcurrentHashMap<>();
		for (int i = 0; i < parameters.length; i++) {
			// 如果有参数为空，则跳过
			if (parameters[i] != null && args[i] != null) {
				// 参数信息
				Parameter parameter = parameters[i];
				// 获得参数的注解
				MyParam myParam = parameter.getDeclaredAnnotation(MyParam.class);
				// 通过注解获得参数名称
				String paramValue = myParam.value();
				// 参数值
				Object oj = args[i];
				parameterMap.put(paramValue, oj);
			}
		}
		return parameterMap;
	}
}
