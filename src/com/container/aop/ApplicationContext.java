package com.container.aop;

import com.mvc.annotation.Aspect;
import com.mvc.annotation.PointCut;
import com.util.ClassScanner;
import com.util.ReflectionUtil;
import org.apache.naming.factory.BeanFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 
 * @author 李福涛
 * @version 1.0  
 *
 */
public class ApplicationContext extends BeanFactory{

    /**
     * 存放代理类的集合
     */
    public static ConcurrentHashMap<String, Object> proxyBeanMap = new ConcurrentHashMap<String, Object>();

 	/**
 	 * 存放配置文件信息
 	 */
 	public static Properties properties = new Properties();
    
    static {

//      properties = ParseConfiguration.parse(location);

        initAopBeanMap("com.test.aopDemo");
    }

    /**
     * 初始化 aop 容器
     */
    public static void initAopBeanMap(String basePath) {
        try {
//            Set<Class<?>> classSet = ClassUtil.getClassSet(basePath);
        	
        	Map<String, Class<?>> classMap = ClassScanner.scannerClass(basePath);

        	for (Entry<String, Class<?>> entry : classMap.entrySet()) {
				Class clazz = entry.getValue();
				
				if (clazz.isAnnotationPresent(Aspect.class)) {
                    //找到切面
                    Method[] methods = clazz.getMethods();

                    for(Method method : methods) {

                        if (method.isAnnotationPresent(PointCut.class)) {
                            // 找到切点
                            PointCut pointCut = (PointCut) method.getAnnotations()[0];
                            // 获得切点的值
                            String pointCutStr = pointCut.value();

                            int lastIndexOf = pointCutStr.lastIndexOf(".");
                            // 被代理的类名
                            String className = pointCutStr.substring(0,lastIndexOf);
                            // 被代理的方法名
                            String methodName = pointCutStr.substring(lastIndexOf+1);

                            // 根据切点创建被代理对象
                            Object targetObj = ReflectionUtil.newInstance(className);
                            // 根据切面类创建代理者
                            AbsMethodAdvance proxyer = (AbsMethodAdvance) ReflectionUtil.newInstance(clazz);
                            // 设置代理的方法
                            proxyer.setProxyMethodName(methodName);

                            Object object = proxyer.createProxyObject(targetObj);

                            if (object != null) {
                                proxyBeanMap.put(targetObj.getClass().getSimpleName().toLowerCase(), object);
                            }
                        }
                    }
                }
			}
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
