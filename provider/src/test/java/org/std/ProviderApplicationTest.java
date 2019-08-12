package org.std;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.std.config.redis.RedisService;
import org.std.model.OrderInfo;
import org.std.order.service.IAsyncService;
import org.std.order.service.IOrderbaseService;
import org.std.order.service.IRedisCacheService;
import org.std.task.QuartzService;
import org.std.task.ScheduleJob;
import org.std.util.CuratorLockUtils;
import org.std.util.RedisLockUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=ProviderApplication.class)
public class ProviderApplicationTest {
	@Autowired
	private IOrderbaseService orderbaseService;
	
	@Autowired
	private RedisService redisService;
	@Autowired
	private IRedisCacheService redisCacheService;
	@Autowired
	private CuratorLockUtils curator;
	@Autowired
	private RedisLockUtils redisLock;
	@Autowired
	private IAsyncService asyncService;
	@Autowired
	private QuartzService quartzService;
	
	@Test
	public void getOrderInfoTest(){
		OrderInfo orderInfo = orderbaseService.getOrderInfoByOrderid("110000001-190724102342119121");
		System.out.println(orderInfo.toString());
	}
	
	@Test
	public void dynamicDataSourceTest(){
		OrderInfo mainOrderInfo = orderbaseService.getOrderInfoByIdFromMain("110000001-190709182919273571");
		System.out.println("main-->"+(mainOrderInfo == null ? "" : mainOrderInfo.toString()));
		//slave  -->  110000002-190620134041109679
		OrderInfo slaveOrderInfo = orderbaseService.getOrderInfoByIdFromSlave("110000001-190709182919273571");
		System.out.println("slave-->"+(slaveOrderInfo == null ? "" : slaveOrderInfo.toString()));
		OrderInfo headerOrderInfo = orderbaseService.getOrderInfoByIdFromHeader("110000001-190709182919273571");
		System.out.println("header-->"+(headerOrderInfo == null ? "" : headerOrderInfo.toString()));
	}
	
	@Test
	public void redisConnectTest(){
		redisService.set("test:redis:connect:aa", "aa", 60);
		
		String cache = redisService.get("test:redis:connect:aa");
		System.out.println(cache);
	}
	
	@Test
	public void redisCacheTest(){
		OrderInfo orderInfo = redisCacheService.getCacheById("110000001-190709182919273571");
		System.out.println(orderInfo.toString());
		OrderInfo orderInfo2 = redisCacheService.getCacheById("110000002-190620134041109679");
		System.out.println(orderInfo2.toString());
		OrderInfo orderInfo3 = redisCacheService.getCacheByName("110000001-190709182919273571");
		System.out.println(orderInfo3.toString());
		OrderInfo orderInfo4 = redisCacheService.getCacheByName("110000002-190620134041109679");
		System.out.println(orderInfo4.toString());
	}
	
	@Test
	public void clearRedisCacheTest(){
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setOrderid("110000002-190620134041109679");
//		redisCacheService.updateOrderInfo(orderInfo);
		
//		redisCacheService.clearValueAllCache();
		
		redisCacheService.clearManyValueCache();
	}
	
	@Test
	public void curatorLockTest() throws InterruptedException{
		String path = "curatortest";
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				curator.acquireLock(path);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				curator.releaseLock(path);
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				curator.acquireLock(path);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				curator.releaseLock(path);
			}
		}).start();
		
		Thread.sleep(20000);
	}
	
	@Test
	public void redisLockTest() throws InterruptedException{
		String key = "redislock";
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				redisLock.acquireLock(key);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				redisLock.releaseLock(key);
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				redisLock.acquireLock(key);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				redisLock.releaseLock(key);
			}
		}).start();
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				redisLock.acquireLock(key);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				redisLock.releaseLock(key);
			}
		}).start();
        new Thread(new Runnable() {
			
			@Override
			public void run() {
				redisLock.acquireLock(key);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				redisLock.releaseLock(key);
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				redisLock.acquireLock(key);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				redisLock.releaseLock(key);
			}
		}).start();
		
		Thread.sleep(60000);
	}
	
	@Test
	public void asyncServiceTest() throws InterruptedException {
		System.out.println("当前线程：" + Thread.currentThread().getName());
		asyncService.asyncGetOrderInfoById("110000001-190724102342119121");
		System.out.println("主线程继续执行，业务逻辑交由子线程执行");
		Thread.sleep(5000);
	}
	
	@Test
	public void quarzTest() throws Exception {
		ScheduleJob job2 = new ScheduleJob("job2", "2", "org.std.task.MyJobService", "0 0/1 * * * ? ");
		ScheduleJob job3 = new ScheduleJob("job3", "3", "org.std.task.MyJobService", "0 0/1 * * * ? ");
		quartzService.addJob(job2);
		quartzService.addJob(job3);
		Thread.sleep(60000*3);
		quartzService.removeJob("job2");
		Thread.sleep(60000*3);
		ScheduleJob job4 = new ScheduleJob("job3", "3", "org.std.task.MyJobService", "0 0/2 * * * ? ");
		quartzService.updateJob(job4);
		
		Thread.sleep(60000*20);
	}
}
