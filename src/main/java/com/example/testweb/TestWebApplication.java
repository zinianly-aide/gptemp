package com.example.testweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 测试Web应用入口
 */
@SpringBootApplication
@ComponentScan(
        basePackages = {
                "com.example.employeesnapshot.entity",
                "com.example.testweb"
        },
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                TestWebController.class,
                                MockEmployeeSnapshotService.class
                        }
                )
        }
)
public class TestWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestWebApplication.class, args);
    }
}