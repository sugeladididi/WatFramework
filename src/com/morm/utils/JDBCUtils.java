package com.morm.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.morm.core.JDBCPool;
import com.util.LogUtil;

/**
 * @Description
 * @author 李福涛
 * @version 1.0
 *
 */
public final class JDBCUtils {

	private static JDBCPool pool = new JDBCPool();

	private static Connection conn;

	private static boolean autoCommit = false;

	/**
	 * @Description 从连接池中获取一个连接
	 * @return Connection数据库连接对象
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		return pool.getConnection();
	}

	/**
	 * 创建 Statement 对象
	 * 
	 * @throws SQLException
	 */
	public static Statement statement() throws SQLException {
		Statement st = null;
		conn = getConnection();

		try {
			st = conn.createStatement();
		} catch (SQLException e) {
			LogUtil.error("创建 Statement 对象失败: " + e.getMessage());
		}
		return st;
	}

	/**
	 * 根据给定的带参数占位符的SQL语句，创建 PreparedStatement 对象
	 * 
	 * @param SQL
	 *            带参数占位符的SQL语句
	 * @return 返回相应的 PreparedStatement 对象
	 * @throws SQLException
	 */
	private static PreparedStatement prepare(String SQL, boolean autoGeneratedKeys) throws SQLException {
		PreparedStatement ps = null;
		conn = getConnection();
		try {
			if (autoGeneratedKeys) {
				ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
			} else {
				ps = conn.prepareStatement(SQL);
			}
		} catch (SQLException e) {
			LogUtil.error("创建 PreparedStatement 对象失败: " + e.getMessage());
		}
		return ps;
	}

	public static ResultSet query(String SQL, List<Object> params) throws SQLException {
		if (SQL == null || SQL.trim().isEmpty()) {
			throw new RuntimeException("你的SQL语句为空或不是查询语句");
		}
		ResultSet rs = null;
		if (params.size() > 0) {
			/* 说明 有参数 传入，就需要处理参数 */
			PreparedStatement ps = prepare(SQL, false);
			try {
				for (int i = 0; i < params.size(); i++) {
					ps.setObject(i + 1, params.get(i));
				}
				rs = ps.executeQuery();
			} catch (SQLException e) {
				LogUtil.error("执行SQL失败: " + e.getMessage());
			}
		} else {
			/* 说明没有传入任何参数 */
			Statement st = statement();
			try {
				rs = st.executeQuery(SQL); // 直接执行不带参数的 SQL 语句
			} catch (SQLException e) {
				LogUtil.error("执行SQL失败: " + e.getMessage());
			}
		}
		return rs;
	}


	/**
	 * 
	 * @Description
	 * @param SQL
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static boolean execute(String SQL, List<Object> params) throws SQLException {
		if (SQL == null || SQL.trim().isEmpty() || SQL.trim().toLowerCase().startsWith("select")) {
			throw new RuntimeException("你的SQL语句为空或有错");
		}
		boolean r = false;
		/* 表示 执行 DDL 或 DML 操作是否成功的一个标识变量 */
		/* 获得 被执行的 SQL 语句的 前缀 */
		SQL = SQL.trim();
		SQL = SQL.toLowerCase();
		String prefix = SQL.substring(0, SQL.indexOf(" "));
		String operation = ""; // 用来保存操作类型的 变量
		// 根据前缀 确定操作
		switch (prefix) {
		case "create":
			operation = "create table";
			break;
		case "alter":
			operation = "update table";
			break;
		case "drop":
			operation = "drop table";
			break;
		case "truncate":
			operation = "truncate table";
			break;
		case "insert":
			operation = "insert :";
			break;
		case "update":
			operation = "update :";
			break;
		case "delete":
			operation = "delete :";
			break;
		}
		if (params != null) { // 说明有参数
			PreparedStatement ps = prepare(SQL, false);
			Connection c = null;
			try {
				c = ps.getConnection();
				c.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				for (int i = 0; i < params.size(); i++) {
					Object param = params.get(i);
					param = typeof(param);
					ps.setObject(i + 1, param);
				}
				ps.executeUpdate();
				commit(c);
				r = true;
			} catch (SQLException e) {
				LogUtil.error(operation + " 失败: " + e.getMessage());
				rollback(c);
			}
		} else { // 说明没有参数
			Statement st = statement();
			Connection c = null;
			try {
				c = st.getConnection();
				c.setAutoCommit(autoCommit);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// 执行 DDL 或 DML 语句，并返回执行结果
			try {
				st.executeUpdate(SQL);
				commit(c); // 提交事务
				r = true;
			} catch (SQLException e) {
				LogUtil.error(operation + " 失败: " + e.getMessage());
				rollback(c); // 回滚事务
			}
		}
		return r;
	}

	/**
	 * 
	 * @param SQL
	 *            需要执行的 INSERT 语句
	 * 
	 * @param autoGeneratedKeys
	 *            指示是否需要返回由数据库产生的键
	 * 
	 * @param params
	 *            将要执行的SQL语句中包含的参数占位符的 参数值
	 * 
	 * @return 如果指定 autoGeneratedKeys 为 true 则返回由数据库产生的键； 如果指定 autoGeneratedKeys
	 *         为 false 则返回受当前SQL影响的记录数目
	 */
	public static int insert(String SQL, boolean autoGeneratedKeys, List<Object> params) throws SQLException {
		int var = -1;
		if (SQL == null || SQL.trim().isEmpty()) {
			throw new RuntimeException("你没有指定SQL语句，请检查是否指定了需要执行的SQL语句");
		}
		// 如果不是 insert 开头开头的语句
		if (!SQL.trim().toLowerCase().startsWith("insert")) {
			throw new RuntimeException("你指定的SQL语句不是插入语句，请检查你的SQL语句");
		}
		// 获得 被执行的 SQL 语句的 前缀 ( 第一个单词 )
		SQL = SQL.trim();
		SQL = SQL.toLowerCase();
		if (params.size() > 0) { // 说明有参数
			PreparedStatement ps = prepare(SQL, autoGeneratedKeys);
			Connection c = null;
			try {
				c = ps.getConnection(); // 从 PreparedStatement 对象中获得 它对应的连接对象
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				for (int i = 0; i < params.size(); i++) {
					Object p = params.get(i);
					p = typeof(p);
					ps.setObject(i + 1, p);
				}
				int count = ps.executeUpdate();
				if (autoGeneratedKeys) { // 如果希望获得数据库产生的键
					ResultSet rs = ps.getGeneratedKeys(); // 获得数据库产生的键集
					if (rs.next()) { // 因为是保存的是单条记录，因此至多返回一个键
						var = rs.getInt(1); // 获得值并赋值给 var 变量
					}
				} else {
					var = count; // 如果不需要获得，则将受SQL影像的记录数赋值给 var 变量
				}
				commit(c);
			} catch (SQLException e) {
				LogUtil.error("数据保存失败: " + e.getMessage());
				rollback(c);
			}
		} else { // 说明没有参数
			Statement st = statement();
			Connection c = null;
			try {
				c = st.getConnection(); // 从 Statement 对象中获得 它对应的连接对象
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// 执行 DDL 或 DML 语句，并返回执行结果
			try {
				int count = st.executeUpdate(SQL);
				if (autoGeneratedKeys) { // 如果企望获得数据库产生的键
					ResultSet rs = st.getGeneratedKeys(); // 获得数据库产生的键集
					if (rs.next()) { // 因为是保存的是单条记录，因此至多返回一个键
						var = rs.getInt(1); // 获得值并赋值给 var 变量
					}
				} else {
					var = count; // 如果不需要获得，则将受SQL影像的记录数赋值给 var 变量
				}
				commit(c); // 提交事务
			} catch (SQLException e) {
				LogUtil.error("数据保存失败: " + e.getMessage());
				rollback(c); // 回滚事务
			}
		}
		return var;
	}

	/** 提交事务 */
	private static void commit(Connection c) {
		if (c != null) {
			try {
				c.setAutoCommit(autoCommit);
				c.commit();
			} catch (SQLException e) {
				e.printStackTrace(); // ignorance
			}
		}
	}

	/** 回滚事务 */
	private static void rollback(Connection c) {
		if (c != null && !autoCommit) {
			try {
				c.rollback();
			} catch (SQLException e) {
				LogUtil.error("", e);
			}
		}
	}

	// /**
	// * 设置是否自动提交事务
	// **/
	// public static void transaction() {
	// try {
	// conn.setAutoCommit(autoCommit);
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * 释放资源
	 **/
	public static void release(Object cloaseable) {
		if (cloaseable != null) {
			if (cloaseable instanceof ResultSet) {
				ResultSet rs = (ResultSet) cloaseable;
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (cloaseable instanceof Statement) {
				Statement st = (Statement) cloaseable;
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (cloaseable instanceof Connection) {
				Connection c = (Connection) cloaseable;
				try {
					c.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**
	 * 
	 * @Description 转换类型
	 * @param o
	 * @return
	 */
	private static Object typeof(Object o) {
		Object r = o;
		if (r != null) {
			
			if (o instanceof java.sql.Timestamp) {
				return r;
			}
			
			// 将 java.util.Date 转成 java.sql.Date
			if (o instanceof java.util.Date) {
				java.util.Date d = (java.util.Date) o;
				r = new java.sql.Date(d.getTime());
				return r;
			}
			
			// 将 Character 或 char 变成 String
			if (o instanceof Character || o.getClass() == char.class) {
				r = String.valueOf(o);
				return r;
			}
			
		}
		return r;
	}

	
	public static void main(String[] args) throws SQLException {

		// boolean b = execute("delete from tb_users where user_id=?", "66");

		// List<Object> parameValues = new ArrayList<>();
		// parameValues.add("1");
		// parameValues.add("1");
		// parameValues.add("1");
		//
		// int b = insert("insert into tb_users(user_name, user_pwd, user_tell)
		// values(?, ?, ?)", false, parameValues);

		// boolean b = execute("select user_id, user_name, user_pwd, user_tell
		// from tb_users where user_id=#{id}", 58);

		// System.out.println(b);
	}

}
