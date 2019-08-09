package org.std.config.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 切换数据源 切面拦截 
 */
@Aspect
@Order(-10) //保证该AOP在@Transactional之前执行
@Component
public class DynamicDataSourceAspect {
	private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);
	
	/**
	 * 切换数据源
	 * @param point
	 * @param targetDataSource
	 */
	@Before("execution (public * org.std..*.service..*.*(..)) && @annotation(targetDataSource)")
	public void changeDataSource(JoinPoint point, TargetDataSource targetDataSource){
		String dataSourceId = targetDataSource.value();
		System.out.println("切换数据源 --> "+dataSourceId);
		if (!DynamicDataSourceContextHolder.containsDataSource(dataSourceId)){
			System.err.println("数据源[{}]不存在，使用默认数据源 > {}"+targetDataSource.value()+point.getSignature());
			logger.error("要切换的数据源不存在，数据源切换失败，使用默认数据源");
		}else{
			System.out.println("Use DataSource : {} > {}"+targetDataSource.value()+point.getSignature());
			DynamicDataSourceContextHolder.setDataSourceType(dataSourceId);
		}
	}
	
	/**
	 * 清除数据源
	 * @param point
	 * @param targetDataSource
	 */
	@After("execution (public * org.std..*.service..*.*(..)) && @annotation(targetDataSource)")
	public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource){
		System.out.println("Revert DataSource : {} > {}"+targetDataSource.value()+point.getSignature());
		DynamicDataSourceContextHolder.clearDataSourceType();
	}
}
