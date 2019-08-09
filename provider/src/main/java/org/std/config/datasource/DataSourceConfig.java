package org.std.config.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

@Configuration
@EnableTransactionManagement
@MapperScan(value={"org.std.order.dao"})
public class DataSourceConfig {
	@Bean
	@ConfigurationProperties(prefix="spring.datasource.druid.main")
	public DataSource mainDataSource(){
		return DruidDataSourceBuilder.create().build();
	}
	
	@Bean
	@ConfigurationProperties(prefix="spring.datasource.druid.slave")
	public DataSource slaveDataSource(){
		return DruidDataSourceBuilder.create().build();
	}
	
	@Bean
	@ConfigurationProperties(prefix="spring.datasource.druid.leader")
	public DataSource leaderDataSource(){
		return DruidDataSourceBuilder.create().build();
	}
	
	@Bean
	public DataSource dynamicDataSource(){
		DynamicDataSource dynamicDataSource = new DynamicDataSource();
		dynamicDataSource.setDefaultTargetDataSource(mainDataSource());
		Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
		dataSourceMap.put("main", mainDataSource());
		DynamicDataSourceContextHolder.getDataSourceId().add("main");
		dataSourceMap.put("slave", slaveDataSource());
		DynamicDataSourceContextHolder.getDataSourceId().add("slave");
		dataSourceMap.put("leader", leaderDataSource());
		DynamicDataSourceContextHolder.getDataSourceId().add("leader");
		dynamicDataSource.setTargetDataSources(dataSourceMap);
		return dynamicDataSource;
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception{
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dynamicDataSource());
		sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mybatis/*.xml"));
		return sqlSessionFactory.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sqlSessionTemplate() throws Exception{
		return new SqlSessionTemplate(sqlSessionFactory());
	}
	
	@Bean
	public PlatformTransactionManager platformTransactionManager(){
		return new DataSourceTransactionManager(dynamicDataSource());
	}
}
