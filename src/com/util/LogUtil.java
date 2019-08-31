package com.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description 工具类，用于日志管理
 * @author 李福涛
 */
public class LogUtil {
	
    public static StackTraceElement findCaller() {
        // 获取堆栈信息
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        if(null == callStack) return null;

        // 最原始被调用的堆栈信息
        StackTraceElement caller = null;
        // 日志类名称
        String logClassName = LogUtil.class.getName();
        // 循环遍历到日志类标识
        boolean isEachLogClass = false;
        
        // 遍历堆栈信息，获取出最原始被调用的方法信息
        for (StackTraceElement s : callStack) {
            // 遍历到日志类
            if(logClassName.equals(s.getClassName())) {
                isEachLogClass = true;
            }
            // 下一个非日志类的堆栈，就是最原始被调用的方法
            if(isEachLogClass) {
                if(!logClassName.equals(s.getClassName())) {
                    isEachLogClass = false;
                    caller = s;
                    break;
                }
            }
        }
        
        return caller;
    }


    private static Logger logger() {
        // 最原始被调用的堆栈对象
        StackTraceElement caller = findCaller();
        if(null == caller) return LoggerFactory.getLogger(LogUtil.class);
        
        Logger log = LoggerFactory.getLogger(caller.getClassName() + "." + caller.getMethodName() + "() Line: " + caller.getLineNumber());
        
        return log;
    }

    public static void trace(String msg) {
        trace(msg, null);
    }
    
    public static void trace(String msg, Throwable e) {
        logger().trace(msg, e);
    }
    
    public static void debug(String msg) {
        debug(msg, null);
    }
    
    public static void debug(String msg, Throwable e) {
        logger().debug(msg, e);
    }
    
    public static void info(String msg) {
        info(msg, null);
    }
    
    public static void info(String msg, Throwable e) {
        logger().info(msg, e);
    }
    
    public static void warn(String msg) {
        warn(msg, null);
    }
    
    public static void warn(String msg, Throwable e) {
        logger().warn(msg, e);
    }
    
    public static void error(String msg) {
        error(msg, null);
    }
    
    public static void error(String msg, Throwable e) {
        logger().error(msg, e);
    }
}
