package com.internation;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description 国际化工具
 * @author 李福涛
 * @version 1.0
 *
 */
public class MyResourceBundle {

	/**
	 * 用于存放对应的语言映射表
	 */
	private Map<String, String> map = new HashMap<String, String>();

	/**
	 * 通过静态工厂初始化ResourceBundle对象
	 * 
	 * @Description
	 * @param location
	 * @param language
	 * @return
	 */
	public static MyResourceBundle getBundle(String location, String language) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(location);
			Element rootElement = document.getRootElement();
			Iterator<Element> rootElementIterator = rootElement.elementIterator();
			while (rootElementIterator.hasNext()) {
				Element localeNode = rootElementIterator.next();
				Attribute language_country = localeNode.attribute("language-country");
				if (language_country.getValue().equals(language)) {
					Iterator<Element> localeNodeIterator = localeNode.elementIterator();
					while (localeNodeIterator.hasNext()) {
						Element pairNode = localeNodeIterator.next();
						map.put(pairNode.attributeValue("key"), pairNode.attributeValue("value"));
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} finally {
			return new MyResourceBundle(map);
		}
	}

	/**
	 * 构造方法
	 * 
	 * @Description
	 * @param map
	 */
	public MyResourceBundle(Map<String, String> map) {
		this.map = map;
	}

	/**
	 * 通过key，获取map中对应的value
	 * 
	 * @Description
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return map.get(key);
	}

}
