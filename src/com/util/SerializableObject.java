package com.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @Description 
 * @author 李福涛
 * @version 1.0  
 *
 */
public class SerializableObject implements Serializable{
	
	/**
	 * @Description 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @Description 
	 * 
	 */
	private byte[] dataArray = null;
	
	public SerializableObject() {
		super();
	}

	public SerializableObject(Object data) {
		try {
            //1、创建OutputStream对象
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //2、创建OutputStream的包装对象ObjectOutputStream，PS：对象将写到OutputStream流中
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            //3、将对象写到OutputStream流中
            objectOutputStream.writeObject(data);
            //4、将OutputStream流转换成字节数组
            dataArray = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public Object deserialize() {
        Object object = null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(dataArray);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            object = objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
