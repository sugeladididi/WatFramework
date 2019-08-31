package com.test.aopDemo;

/**
 * @Description 被代理的类
 * @author 李福涛
 * @version 1.0
 *
 */
public class Test {
	public void doSomeThing() {
		System.out.println("do some thing...");
	}

	public void doWtihNotProxy() {
		System.out.println("do some thing with not proxy");
	}
}
