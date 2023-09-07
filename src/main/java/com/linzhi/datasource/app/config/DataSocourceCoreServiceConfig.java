package com.linzhi.datasource.app.config;

import com.linzhi.datasource.app.support.DefaultExcelServiceRegistry;
import com.linzhi.datasource.app.support.ExcelServiceRigistry;
import com.linzhi.datasource.app.support.ExcelServiceScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tk.mybatis.spring.annotation.MapperScan;

@Configuration
@ComponentScan(basePackages = { "com.linzhi.datasource.app" })
@MapperScan(value = "com.linzhi.datasource.app.dao")
public class DataSocourceCoreServiceConfig {


	@Bean
	ExcelServiceRigistry excelServiceRigistry() {
		return new DefaultExcelServiceRegistry();
	}

	@Bean
	ExcelServiceScanner excelServiceScanner() {
		ExcelServiceScanner excelServiceScanner = new ExcelServiceScanner();
		excelServiceScanner.setExcelServiceRigistry(excelServiceRigistry());
		return excelServiceScanner;
	}

	@Bean
	public TaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(4);
		executor.setMaxPoolSize(4);
		executor.setThreadNamePrefix("async-task");
		executor.initialize();
		return executor;
	}
}
