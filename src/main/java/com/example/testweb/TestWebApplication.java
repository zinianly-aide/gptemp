package com.example.testweb;

import com.example.employeesnapshot.controller.RootController;
import com.example.employeesnapshot.controller.WebController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 最小化测试Web应用程序 - 仅用于测试根路径重定向
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@ComponentScan(basePackages = {
    "com.example.employeesnapshot.controller",
    "com.example.testweb"
}, useDefaultFilters = false, includeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        RootController.class,
        TestWebController.class,
        ApiController.class,
        MockEmployeeSnapshotService.class
    })
})
public class TestWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestWebApplication.class, args);
    }
}