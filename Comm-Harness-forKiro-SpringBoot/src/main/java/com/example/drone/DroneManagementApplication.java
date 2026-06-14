package com.example.drone;

import org.apache.shiro.spring.boot.autoconfigure.ShiroAnnotationProcessorAutoConfiguration;
import org.apache.shiro.spring.boot.autoconfigure.ShiroAutoConfiguration;
import org.apache.shiro.spring.boot.autoconfigure.ShiroBeanAutoConfiguration;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 无人机信息管理系统启动类
 * 
 * <p>注意：暂时排除 Shiro 自动配置，将在任务 9 中实现 Shiro 配置
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
@SpringBootApplication(exclude = {
    ShiroAutoConfiguration.class,
    ShiroBeanAutoConfiguration.class,
    ShiroWebAutoConfiguration.class,
    ShiroAnnotationProcessorAutoConfiguration.class
})
public class DroneManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(DroneManagementApplication.class, args);
    }
}
