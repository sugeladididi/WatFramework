package com.util;

import java.util.concurrent.*;

/**
 * 自定义线程池
 * 
 * @Description
 * @author 李福涛
 * @version 1.0
 *
 */
public class ExecutorUtil {

	private volatile static ThreadPoolExecutor pool;

	/**
	 * 创建线程池，池中保存的线程数为2，允许的最大线程数为4
	 * @return
	 */
	public static ThreadPoolExecutor getExecutor() {
		if(pool == null){
			synchronized (ExecutorUtil.class){
				if(pool == null)
					return new ThreadPoolExecutor(2, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10));
			}
		}
		return pool;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		ThreadPoolExecutor threadPoolExecutor = ExecutorUtil.getExecutor();
		Callable<String> callable = new Callable<String>(){
			public String call() throws Exception {
				System.out.println("子线程1开始~");
				Thread.sleep(3000);
				return "子线程1结束";
			};
		};
		Future<String> submit = threadPoolExecutor.submit(callable);
		String result = submit.get(); // 获得提交任务后的返回结果
		System.out.println("result:" + result);

	}
}
