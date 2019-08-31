package com.util;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.LongComparator;
import jdbm.helper.StringComparator;
import jdbm.helper.TupleBrowser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @Description JDBM缓存工具类
 * @author 李福涛
 * @version 1.0
 *
 */
public class CacheUtil {

	// cache的名字，不可更改
	private static final String MYCACHE = "mycache";

	// JDBM记录器
	private static RecordManager recman ;

	/**
	 * 通过传入的BTree别名获取bTree实例
	 * 
	 * @Description
	 * @param bTreeAlias
	 * @return
	 * @throws IOException
	 */
	public static BTree createBTreeInstance(String bTreeAlias) throws IOException {

		BTree bTree;
		// 根据别名获取对应RecordManager，这里别名为静态变量
		recman = RecordManagerFactory.createRecordManager(MYCACHE);
		// 通过这个别名获取bTreeId
		long bTreeId = recman.getNamedObject(bTreeAlias);

		// 如果 id 为 0 ， 则创建一个bTree
		if (bTreeId == 0) {
			bTree = BTree.createInstance(recman, new LongComparator());
			recman.setNamedObject(bTreeAlias, bTree.getRecid());
			recman.commit();
		} else {
			bTree = BTree.load(recman, bTreeId);
		}

		return bTree;
	}
	
	/**
	 * 向缓存插入数据
	 * 
	 * @Description 
	 * @param bTree
	 * @param key
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public static boolean insert(String bTreeAlias, String key, Object value) throws IOException{
		recman = RecordManagerFactory.createRecordManager(MYCACHE);
		BTree bTree ;
        // 拿到bTree
        long recid = recman.getNamedObject( bTreeAlias );
        if ( recid != 0 ) {
            bTree = BTree.load( recman, recid );
            LogUtil.debug( "Reloaded existing BTree was " + bTree.size());
        } else {
        	bTree = BTree.createInstance( recman, new StringComparator() );
            recman.setNamedObject( bTreeAlias, bTree.getRecid() );
            LogUtil.debug( "Created a new empty BTree" );
        }
		
		bTree.insert(key, value, true);
		recman.commit();
//		recman.close();
		return true;
	} 
	
	/**
	 * 向缓存插入数据
	 * 
	 * @Description 
	 * @param bTree
	 * @param key
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public static boolean update(String bTreeAlias, String key, Object value) throws IOException{
		recman = RecordManagerFactory.createRecordManager(MYCACHE);
		BTree bTree ;
        // 拿到bTree
        long recid = recman.getNamedObject( bTreeAlias );
        if ( recid == 0 )
        	return false;

        bTree = BTree.load( recman, recid );
        bTree.remove(key);
        bTree.insert(key, value, true);
        recman.commit();
//		recman.close();
		return true;
	} 
	

	/**
	 * 获取遍历器
	 * 
	 * @Description 
	 * @param bTreeAlias
	 * @return
	 * @throws IOException
	 */
	public static TupleBrowser getTupleBrowser(String bTreeAlias) throws IOException{
		recman = RecordManagerFactory.createRecordManager(MYCACHE);
        TupleBrowser browser = null;
        
        // 尝试重新加载现有的BTree
        long recid = recman.getNamedObject( bTreeAlias );
        if ( recid != 0 ) {
        	browser = BTree.load( recman, recid ).browse();
        }
		return browser;
	}

	/**
	 * 获取值
	 *
	 * @param bTreeAlias bTree别名
	 * @param key key
	 * @return value
	 * @throws IOException
	 */
	public static Object getValue(String bTreeAlias, String key) throws IOException{
		recman = RecordManagerFactory.createRecordManager(MYCACHE);
		BTree bTree;
		Object value = null;
        // 尝试重新加载现有的BTree
        long recid = recman.getNamedObject( bTreeAlias );
        if ( recid != 0 ) {
            bTree = BTree.load( recman, recid );
            value = bTree.find(key);
        }
		return value;
	}
	
	/**
	 * 用于测试
	 * 
	 * @Description 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {

		// 封装后的工具类
		CacheUtil.insert("iocCache", "2", "1");

		// 1.
//		BTree iocCache = CacheUtil.createBTreeInstance("iocCache");

//		CacheUtil.insert(iocCache, "2", newInstance2);
		
//		Tuple tuple = new Tuple();
//		TupleBrowser browser;
//		browser = iocCache.browse();
//		while (browser.getNext(tuple)) {
//			System.out.println(tuple.getKey() + " " + tuple.getValue().toString());
//		}
	}

}


