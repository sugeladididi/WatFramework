package com.morm.utils;

import java.util.ArrayList;
import java.util.List;

import com.morm.annotation.MySelect;

/**
 * @Description
 * @author 李福涛
 * @version 1.0
 *
 */
public class SQLUtils {
	/**
	 * 
	 * 获取传入字符串values后面的参数
	 * ("insert into tb_users(user_name, user_pwd, user_tell) values(#{name}, #{pwd}, #{tell})")
	 * @param sql
	 * @return 参数列表
	 */
	public static String[] sqlInsertParameter(String sql) {
		int startIndex = sql.indexOf("values");
		int endIndex = sql.length();
		// 去掉除“,”外的所有符号
		String substring = sql.substring(startIndex + 6, endIndex).replace("(", "").replace(")", "").replace("#{", "")
				.replace("}", "");
		// 按“,”拆分
		String[] split = substring.split(",");
		// 去掉空格
		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].trim();
		}
		return split;
	}

	/**
	 * 
	 * 获取select 后面where语句
	 * ("select user_id, user_name, user_pwd, user_tell from tb_users where user_id=#{id} and user_name=#{name}")
	 * 
	 * @param sql
	 * @return
	 */
	public static List<String> sqlSelectParameter(String sql) {
		int startIndex = sql.indexOf("where");
		int endIndex = sql.length();
		String substring = sql.substring(startIndex + 5, endIndex);
		String[] split = substring.split("and");
		List<String> listArr = new ArrayList<>();
		for (String string : split) {
			String[] sp2 = string.split("=");
			String paramName = sp2[1].trim().replace("#{", "").replace("}", "");
			listArr.add(paramName);
		}
		return listArr;
	}
	
	/**
	 * @Description 
	 * @param deleteSql
	 * @return
	 */
	public static List<String> sqlDeleteParameter(String sql) {
		int startIndex = sql.indexOf("where");
		int endIndex = sql.length();
		String substring = sql.substring(startIndex + 5, endIndex);
		String[] split = substring.split("and");
		List<String> listArr = new ArrayList<>();
		for (String string : split) {
			String[] sp2 = string.split("=");
			String paramName = sp2[1].trim().replace("#{", "").replace("}", "");
			listArr.add(paramName);
		}
		return listArr;
	}
	
	/**
	 * 
	 * 获取select 后面where语句
	 * ("update tb_users set user_name=#{user_name},user_age=#{user_age}, user_tell=#{user_tell} where user_id=#{user_id}")
	 * 
	 * @param sql
	 * @return
	 */
	public static List<String> sqlUpdateParameter(String sql) {
		int startIndex = sql.indexOf("set");
		sql = sql.replaceAll("where", ",");
		int endIndex = sql.length();
		String substring = sql.substring(startIndex + 3, endIndex);
		String[] split = substring.split(",");
		List<String> listArr = new ArrayList<>();
		for (String string : split) {
			String[] sp2 = string.split("=");
			String paramName = sp2[1].trim().replace("#{", "").replace("}", "");
			listArr.add(paramName);
		}
		return listArr;
	}


	/**
	 * 
	 * @Description 将传入的SQL语句的参数替换变为?
	 * @param sql
	 * @param parameterName
	 * @return
	 */
	public static String parameQuestion(String sql, String[] parameterName) {
		for (int i = 0; i < parameterName.length; i++) {
			String string = parameterName[i];
			sql = sql.replace("#{" + string + "}", "?");
		}
		return sql;
	}

	/**
	 * 
	 * @Description 将传入的SQL语句的参数替换变为?<br>
	 * @param sql
	 * @param parameterName
	 * @return
	 */
	public static String parameQuestion(String sql, List<String> parameterName) {
		for (int i = 0; i < parameterName.size(); i++) {
			String string = parameterName.get(i);
			sql = sql.replace("#{" + string + "}", "?");
		}
		return sql;
	}

	public static void main(String[] args) {
		// String sql = "insert into user(userName,userAge)
		// values(#{userName},#{userAge})";
		// String[] sqlParameter = sqlInsertParameter(sql);
		// for (String string : sqlParameter) {
		// System.out.println(string);
		// }
		List<String> sqlSelectParameter = SQLUtils
				.sqlSelectParameter("select * from User where userName=#{userName} and userAge=#{userAge} ");
		for (String string : sqlSelectParameter) {
			System.out.println(string);
		}
	}



}
