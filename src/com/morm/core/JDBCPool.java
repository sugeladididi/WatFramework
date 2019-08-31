package com.morm.core;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.morm.bean.Configuration;
import com.util.LogUtil;

/**
 * 
 * @Description JDBC连接池
 * @author 李福涛
 * @version 1.0  
 *
 */
public class JDBCPool implements DataSource {

	/**
	 * @Field: listConnections 使用LinkedList集合来存放数据库链接，
	 *         由于要频繁读写List集合，所以这里使用LinkedList存储数据库连接比较合适
	 */
	private static LinkedList<Connection> listConnections = new LinkedList<Connection>();

	private static Configuration conf;
	
	static {
		// 在静态代码块中加载database.properties数据库配置文件
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("database.properties");
		Properties prop = new Properties();
		try {
			prop.load(in);
			conf = new Configuration();
			conf.setDriver(prop.getProperty("driver"));
			conf.setUrl(prop.getProperty("url"));
			conf.setUsername(prop.getProperty("username"));
			conf.setPassword(prop.getProperty("password"));
			conf.setJdbcPoolInitSize(Integer.parseInt(prop.getProperty("jdbcPoolInitSize")));
			// 加载数据库驱动
			Class.forName(conf.getDriver());
			for (int i = 0; i < conf.getJdbcPoolInitSize(); i++) {
				Connection conn = DriverManager.getConnection(conf.getUrl(), conf.getUsername(), conf.getPassword());
				LogUtil.debug("获取到了链接:" + conn);
				// 将获取到的数据库连接加入到listConnections集合中，listConnections集合此时就是一个存放了数据库连接的连接池
				listConnections.add(conn);
			}

		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 获取数据库连接
	 * 
	 * @see DataSource#getConnection()
	 */
	@Override
	public Connection getConnection() throws SQLException {
		// 如果数据库连接池中的连接对象的个数大于0
		if (listConnections.size() > 0) {
			// 从listConnections集合中获取一个数据库连接
			final Connection conn = listConnections.removeFirst();
			LogUtil.debug("listConnections数据库连接池大小是" + listConnections.size());
			// 返回Connection对象的代理对象
			return (Connection) Proxy.newProxyInstance(JDBCPool.class.getClassLoader(), conn.getClass().getInterfaces(),
					new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							if (!method.getName().equals("close")) {
								return method.invoke(conn, args);
							} else {
								// 如果调用的是Connection对象的close方法，就把conn还给数据库连接池
								listConnections.add(conn);
								LogUtil.debug(conn + "被还给listConnections数据库连接池了！！");
								LogUtil.debug("listConnections数据库连接池大小为" + listConnections.size());
								return null;
							}
						}
					});
		} else {
			throw new RuntimeException("对不起，数据库忙");
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return null;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}