package com.container.aop;

import java.lang.reflect.Method;

import com.util.StringUtils;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @Description 
 * @author 李福涛
 * @version 1.0  
 *
 */
public abstract class AbsMethodAdvance implements MethodInterceptor {

    /**
     * 要被代理的目标对象
     */
    private Object targetObject;

    /**
     * 被代理的方法名
     */
    private String proxyMethodName;

    /**
     * 根据被代理对象 创建代理对象
     * @param target
     * @return
     */
    public Object createProxyObject(Object target) {
        this.targetObject = target;
        // 该类用于生成代理对象
        Enhancer enhancer = new Enhancer();
        // 设置目标类为代理对象的父类
        enhancer.setSuperclass(this.targetObject.getClass());
        // 设置回调用对象为本身
        enhancer.setCallback(this);

        return enhancer.create();
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object result;
        
        // 得到被代理的方法
        String proxyMethod = getProxyMethodName();

        // 执行doBefore方法
        if (StringUtils.isNotBlank(proxyMethod) && proxyMethod.equals(method.getName())) {
            doBefore();
        }

        // 执行拦截的方法
        result = methodProxy.invokeSuper(proxy, args);

        // 执行doAfter方法
        if (StringUtils.isNotBlank(proxyMethod) && proxyMethod.equals(method.getName())) {
            doAfter();
        }

        return result;
    }

    public abstract void doBefore();

    public abstract void doAfter();

    public String getProxyMethodName() {
        return proxyMethodName;
    }

    public void setProxyMethodName(String proxyMethodName) {
        this.proxyMethodName = proxyMethodName;
    }
}
