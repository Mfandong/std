package org.std.programming;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerAndConsumer {
	private static Integer count = 0;
	private static final Integer FULL = 10;
	//创建锁对象
	private Lock lock = new ReentrantLock();
	//创建两个条件变量，一个为缓冲区非满，一个缓冲区非空
	private Condition notFull = lock.newCondition();
	private Condition notEmpty = lock.newCondition();
	
	public static void main(String[] args) {
		ProducerAndConsumer pc = new ProducerAndConsumer();
		for (int i = 0; i < 10; i++) {
			new Thread(pc.new Producer()).start();
		}
		for (int i = 0; i < 10; i++) {
			new Thread(pc.new Consumer()).start();
		}
	}
	
	class Producer implements Runnable{

		@Override
		public void run() {
			for (int i = 0; i < 10; i++) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.lock();
				try {
					while (count == FULL){
						notFull.await();
					}
					count++;
					System.out.println(Thread.currentThread().getName()+"生产者生产，目前总共有"+count);
					notEmpty.signal();
				} catch (Exception e) {
				}finally{
					lock.unlock();
				}
			}
		}
	}
	
	class Consumer implements Runnable{

		@Override
		public void run() {
			for (int i = 0; i < 10; i++) {
				try {
					Thread.sleep(3000);
					lock.lock();
					while (count == 0){
						notEmpty.await();
					}
					count--;
					System.out.println(Thread.currentThread().getName()+"消费者消费，目前共有"+ count);
					notFull.signal();
				} catch (Exception e) {
					
				}finally{
					lock.unlock();
				}
			}
		}
		
	}
}

