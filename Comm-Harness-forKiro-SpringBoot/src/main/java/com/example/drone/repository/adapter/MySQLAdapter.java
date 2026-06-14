package com.example.drone.repository.adapter;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * MySQL 数据库适配器实现.
 * <p>
 * 当配置文件中 database.type=mysql 时，该适配器被激活。
 * 使用 Druid 连接池作为数据源，适用于生产环境和大规模部署。
 * </p>
 *
 * @author Drone Management System
 * @version 1.0.0
 * @since 2024-01-15
 */
public class MySQLAdapter implements DatabaseAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MySQLAdapter.class);

    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;

    public MySQLAdapter(String url, String username, String password, String driverClassName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
    }

    /**
     * 获取 MySQL 数据源.
     * <p>
     * 创建并配置 Druid 连接池，使用配置文件中的参数。
     * </p>
     *
     * @return Druid 数据源对象
     */
    @Override
    public DataSource getDataSource() {
        logger.info("初始化 MySQL 数据源，URL: {}", url);
        
        DruidDataSource dataSource = new DruidDataSource();
        
        // 基本连接配置
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        
        // 连接池配置
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);
        
        // 连接检测配置
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        
        // 初始化连接池
        try {
            dataSource.init();
            logger.info("MySQL 数据源初始化成功");
        } catch (SQLException e) {
            logger.error("MySQL 数据源初始化失败", e);
            throw new RuntimeException("无法初始化 MySQL 数据源", e);
        }
        
        return dataSource;
    }

    /**
     * 获取数据库类型标识.
     *
     * @return 返回 "mysql"
     */
    @Override
    public String getDatabaseType() {
        return "mysql";
    }
}
