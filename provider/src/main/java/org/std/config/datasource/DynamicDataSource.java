package org.std.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源路由类
 * 切换核心是spring底层提供的AbstractRoutingDataSource，
 * 实现determineCurrentLookupKey()，返回数据库名称
 */
public class DynamicDataSource extends AbstractRoutingDataSource{

	@Override
	protected Object determineCurrentLookupKey() {
		System.out.println(DynamicDataSourceContextHolder.getDataSourceType());
		return DynamicDataSourceContextHolder.getDataSourceType();
	}

}
