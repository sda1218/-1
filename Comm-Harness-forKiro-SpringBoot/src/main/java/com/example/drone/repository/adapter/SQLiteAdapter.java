package com.example.drone.repository.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

/**
 * SQLite 数据库适配器实现.
 * <p>
 * 当配置文件中 database.type=sqlite 时，该适配器被激活。
 * 使用 SQLiteDataSource 作为数据源，适用于小规模部署和开发环境。
 * </p>
 *
 * @author Drone Management System
 * @version 1.0.0
 * @since 2024-01-15
 */
public class SQLiteAdapter implements DatabaseAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SQLiteAdapter.class);

    /**
     * SQLite 数据库文件路径.
     * <p>
     * 从配置文件中读取 database.sqlite.path 属性。
     * 示例：./data/drone.db
     * </p>
     */
    private final String dbPath;

    /**
     * 构造器.
     *
     * @param dbPath SQLite 数据库文件路径
     */
    public SQLiteAdapter(String dbPath) {
        this.dbPath = dbPath;
    }

    /**
     * 获取 SQLite 数据源.
     * <p>
     * 创建并配置 SQLiteDataSource 对象，设置 JDBC URL。
     * URL 格式：jdbc:sqlite:数据库文件路径
     * </p>
     *
     * @return SQLite 数据源对象
     */
    @Override
    public DataSource getDataSource() {
        logger.info("初始化 SQLite 数据源，数据库路径: {}", dbPath);
        
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + dbPath);
        
        // 初始化数据库表结构
        initializeDatabase(dataSource);
        
        logger.info("SQLite 数据源初始化成功");
        return dataSource;
    }
    
    /**
     * 初始化数据库表结构.
     * 
     * @param dataSource 数据源
     */
    private void initializeDatabase(SQLiteDataSource dataSource) {
        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            // 删除已存在的表
            stmt.execute("DROP TABLE IF EXISTS t_drone");
            
            // 创建无人机信息表
            stmt.execute("CREATE TABLE t_drone (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "serial_number TEXT NOT NULL UNIQUE," +
                "model_name TEXT NOT NULL," +
                "manufacturer TEXT NOT NULL," +
                "purchase_date TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "max_flight_time INTEGER NOT NULL," +
                "max_flight_distance INTEGER NOT NULL," +
                "weight INTEGER NOT NULL," +
                "remarks TEXT," +
                "created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))," +
                "updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))" +
                ")");
            
            // 创建索引
            stmt.execute("CREATE INDEX idx_serial_number ON t_drone(serial_number)");
            stmt.execute("CREATE INDEX idx_model_name ON t_drone(model_name)");
            stmt.execute("CREATE INDEX idx_manufacturer ON t_drone(manufacturer)");
            stmt.execute("CREATE INDEX idx_status ON t_drone(status)");
            
            // 删除已存在的触发器
            stmt.execute("DROP TRIGGER IF EXISTS update_drone_timestamp");
            
            // 创建触发器
            stmt.execute("CREATE TRIGGER update_drone_timestamp " +
                "AFTER UPDATE ON t_drone " +
                "FOR EACH ROW " +
                "BEGIN " +
                "UPDATE t_drone SET updated_at = datetime('now', 'localtime') WHERE id = NEW.id; " +
                "END");
            
            logger.info("SQLite 数据库表结构初始化成功");
        } catch (Exception e) {
            logger.warn("SQLite 数据库初始化失败（可能已存在）: {}", e.getMessage());
        }
    }

    /**
     * 获取数据库类型标识.
     *
     * @return 返回 "sqlite"
     */
    @Override
    public String getDatabaseType() {
        return "sqlite";
    }
}
