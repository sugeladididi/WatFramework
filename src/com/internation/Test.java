package com.internation;


/**
 * @Description
 * @author 李福涛
 * @version 1.0
 *
 */
public class Test {
	public static void main(String[] args) {
//		Locale locale = new Locale("en", "US");
		MyResourceBundle bundle = MyResourceBundle.getBundle("src/locale.xml", "zh_CN");
		String hello = bundle.getString("hello");
		String world = bundle.getString("world");
		System.out.println("zh_CN is:" + hello + world);
		
		bundle = MyResourceBundle.getBundle("src/locale.xml", "en_US");
		String enhello = bundle.getString("hello");
		String enworld = bundle.getString("world");
		System.out.println("en_US is:" + enhello + enworld);
		
	}

}
