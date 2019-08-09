package org.std.config.datasource;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态数据源上下文 
 */
public class DynamicDataSourceContextHolder {
	/**
	 * ThreadLocal为每个使用该变量的线程提供独立的变量副本
	 * 每个线程可以独立改变自己的副本，而不会影响其他线程所对应的副本
	 */
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
	
	/**
	 * 管理所有的数据源，判断数据源是否存
	 */
	private static List<String> dataSourceIds = new ArrayList<String>();
	
	/**
	 * 设置当前的数据源
	 * @param dataSourceType
	 */
	public static void setDataSourceType(String dataSourceType){
		contextHolder.set(dataSourceType);
	}
	
	/**
	 * 获取当前的数据源
	 * @return
	 */
	public static String getDataSourceType(){
		return contextHolder.get();
	}
	
	/**
	 * 清除当前数据源
	 */
	public static void clearDataSourceType(){
		contextHolder.remove();
	}
	
	public static boolean containsDataSource(String dataSourceId){
		return dataSourceIds.contains(dataSourceId);
	}
	
	public static List<String> getDataSourceId(){
		return dataSourceIds;
	}
}
