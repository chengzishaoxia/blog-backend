package com.klee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 应用启动类（新名称：wing-blog）
 * 原名称：YouLaiBootApplication
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class WingBlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(WingBlogApplication.class, args);
    }
}

