package com.test.aopDemo;

import com.container.aop.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static int ans;

    public static void main(String[] args) {
        // 模拟容器初始化
        ApplicationContext applicationContext = new ApplicationContext();
        ConcurrentHashMap<String, Object> proxyBeanMap = ApplicationContext.proxyBeanMap;

        // 生成的代理对象 默认为该类名的小写
        Test test = (Test) proxyBeanMap.get("test");

        test.doSomeThing();

        System.out.println("-------------");

        test.doWtihNotProxy();

    }
}
