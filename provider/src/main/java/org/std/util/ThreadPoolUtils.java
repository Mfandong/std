package org.std.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {
	
	private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	
	private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private static ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
	
	private static ScheduledExecutorService  scheduledThreadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	
	public static void executeByCachedThreadExecutors(Runnable runnable) {
		cachedThreadPool.execute(runnable);
	}
	
	public static Future<?> submitByCachedThreadExecutors(Callable<?> callable) {
		return cachedThreadPool.submit(callable);
	}
	
	public static void executeByFixedThreadExecutors(Runnable runnable) {
		fixedThreadPool.execute(runnable);
	}
	
	public static Future<?> submitByFixedThreadExecutors(Callable<?> callable){
		return fixedThreadPool.submit(callable);
	}
	
	public static void executeBySingleThreadExecutors(Runnable runnable) {
		singleThreadPool.execute(runnable);
	}
	
	public static Future<?> submitBySingleThreadExecutors(Callable<?> callable){
		return singleThreadPool.submit(callable);
	}
	
	public static void executeByScheduledThreadExecutors(Runnable runnable) {
		scheduledThreadPool.execute(runnable);
	}
	
	public static Future<?> scheduleByScheduledThreadExecutors(Callable<?> callable, long delay){
		return scheduledThreadPool.schedule(callable, delay, TimeUnit.SECONDS);
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
//		Runnable runnable = new TestThreadRunnable();
//		for (int i = 0; i < 10; i++) {
//			executeByCachedThreadExecutors(runnable);
//			executeByFixedThreadExecutors(runnable);
//			executeBySingleThreadExecutors(runnable);
//			executeByScheduledThreadExecutors(runnable);
//		}
		
		Callable<String> testCallable = new TestThreadCallable();
		Future<?> future = submitByCachedThreadExecutors(testCallable);
		System.out.println("异步执行等待回调");
		System.out.println(future.get());
	}
	
}
class TestThreadRunnable implements Runnable{
	
	@Override
	public void run() {
		System.out.println("当前执行的线程：" + Thread.currentThread().getName());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + "-->执行子线程");
	}
	
}

class TestThreadCallable implements Callable<String>{

	@Override
	public String call() throws Exception {
		System.out.println("当前执行的线程：" + Thread.currentThread().getName());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + "-->执行子线程");
		return "回调结果";
	}
	
}
