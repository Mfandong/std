package org.std.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.std.config.redis.RedisService;

@Component
public class RedisLockUtils {
	@Autowired
	private RedisService redisService;
	
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	
	public void acquireLock(String key){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while (true){
			System.out.println(Thread.currentThread().getName()+"-->"+sdf.format(new Date())+"-->开始获取锁。。。");
			boolean success = redisService.setNX(key, "1", 60);
			if (success){
				System.out.println(Thread.currentThread().getName()+"-->"+sdf.format(new Date())+"-->获取锁成功。。。");
				break;
			}
			try {
				System.out.println(Thread.currentThread().getName()+"-->"+sdf.format(new Date())+"-->等待计数器的值为：" + countDownLatch.getCount());
				if(countDownLatch.getCount()<=0) {
					countDownLatch = new CountDownLatch(1);
				}
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void releaseLock(String key){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		redisService.del(key);
		countDownLatch.countDown();
		System.out.println(Thread.currentThread().getName()+"-->"+sdf.format(new Date())+"-->释放锁。。。");
	}
}
