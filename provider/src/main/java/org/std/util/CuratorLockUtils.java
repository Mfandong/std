package org.std.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CuratorLockUtils implements InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(CuratorLockUtils.class);
	@Autowired
	private CuratorFramework curatorFramework;
	
	private static final String ROOT_PATH = "curatorlock";
	
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	
	public void acquireLock(String path) {
		String keypath = "/" + ROOT_PATH + "/" + path;
		while(true){
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println("时间：" + sdf.format(new Date()) + "-->" + Thread.currentThread().getName() + "-->开始获取锁。。。");
				curatorFramework
				        .create()
				        .creatingParentContainersIfNeeded()
				        .withMode(CreateMode.EPHEMERAL)
				        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
				        .forPath(keypath);
				logger.info("sucess to acquire lock for path:{}",keypath);
				System.out.println("时间：" + sdf.format(new Date()) + "-->" + Thread.currentThread().getName() + "-->成功获取锁。。。");
				break;
			}catch (Exception e) {
				//节点创建失败，锁获取失败
				logger.info("failed to acquire lock for path:{}",keypath);
				logger.info("while try again");
				try {
					if(countDownLatch.getCount()<=0) {
						countDownLatch = new CountDownLatch(1);
					}
					countDownLatch.await();
				}catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public boolean releaseLock(String path) {
		try {
			String keypath = "/" + ROOT_PATH + "/" + path;
			if(curatorFramework.checkExists().forPath(keypath) != null) {
				curatorFramework.delete().forPath(keypath);
			}
		}catch (Exception e) {
			logger.error("failed to release lock");
			return false;
		}
		return true;
	}
	
	private void addWatcher(String path) throws Exception {
		String keypath;
		if(path.equals(ROOT_PATH)) {
			keypath = "/"+path;
		}else {
			keypath = "/"+ROOT_PATH+"/"+path;
		}
		final PathChildrenCache cache = new PathChildrenCache(curatorFramework, keypath, false);
		cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
		cache.getListenable().addListener((client,event) ->{
			if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
				String oldPath = event.getData().getPath();
				logger.info("success to release lock for path:{}",oldPath);
				if(oldPath.contains(path)) {
					logger.error("\nlisten change {}",oldPath);
					logger.error("\npath {}",path);
					countDownLatch.countDown();
				}
			}
		});
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		curatorFramework = curatorFramework.usingNamespace("lock-namespace");
		String path = "/"+ROOT_PATH;
		try {
			if(curatorFramework.checkExists().forPath(path) == null) {
				curatorFramework
				        .create()
				        .creatingParentsIfNeeded()
				        .withMode(CreateMode.PERSISTENT)
				        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
				        .forPath(path);
			}
			addWatcher(ROOT_PATH);
			logger.info("root path 的watch事件创建成功");
		}catch (Exception e) {
			logger.error("connect zookpeeper fail,please check the log >>{}",e.getMessage());
		}
	}
	
	public void curatorLock(String path){
		System.out.println("curator状态：" + curatorFramework.getState());
		InterProcessLock lock = new InterProcessMutex(curatorFramework, "/" + ROOT_PATH + "/" + path);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println("时间：" + sdf.format(new Date()) + "-->" + Thread.currentThread().getName() + "-->开始获取锁。。。");
			lock.acquire();
			System.out.println("时间：" + sdf.format(new Date()) + "-->" + Thread.currentThread().getName() + "-->成功获取锁。。。");
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				lock.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
