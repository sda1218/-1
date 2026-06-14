-- 创建数据库
CREATE DATABASE IF NOT EXISTS drone_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE drone_management;

-- 创建表
CREATE TABLE IF NOT EXISTS t_drone (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    serial_number VARCHAR(100) NOT NULL UNIQUE COMMENT '序列号',
    model_name VARCHAR(100) NOT NULL COMMENT '型号名称',
    manufacturer VARCHAR(100) NOT NULL COMMENT '制造商',
    purchase_date DATE NOT NULL COMMENT '购买日期',
    status VARCHAR(50) NOT NULL COMMENT '状态：AVAILABLE/UNDER_MAINTENANCE/SCRAPPED',
    max_flight_time INT COMMENT '最大飞行时间(分钟)',
    max_flight_distance INT COMMENT '最大飞行距离(米)',
    weight INT COMMENT '重量(克)',
    remarks TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_manufacturer (manufacturer)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='无人机信息表';

-- 插入测试数据
INSERT INTO t_drone (serial_number, model_name, manufacturer, purchase_date, status, max_flight_time, max_flight_distance, weight, remarks)
VALUES 
('DJI-001', 'Mavic 3', 'DJI', '2024-01-15', 'AVAILABLE', 46, 30000, 895, '高性能航拍无人机'),
('DJI-002', 'Mini 3 Pro', 'DJI', '2024-02-20', 'AVAILABLE', 34, 18000, 249, '轻量级便携无人机'),
('PARROT-001', 'Anafi', 'Parrot', '2023-12-10', 'UNDER_MAINTENANCE', 25, 4000, 320, '维修中');
