package com.linzhi.datasource.app;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * SecurityAutoConfiguration.class 如果不加这个，访求controller会跳转到验证登陆页面
 */
//@SpringBootApplication(scanBasePackages = {"com.linzhi.datasource.app"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class},scanBasePackages = "com.linzhi.datasource.app.*")
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},scanBasePackages = "com.linzhi.datasource.app.*")
//@MapperScan("com.linzhi.datasource.app.dao") 如果不加MapperScan，在yml文件新增增加mapper.identity=MYSQL 就不报错误了
public class App extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(App.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
