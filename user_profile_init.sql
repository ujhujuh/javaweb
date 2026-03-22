-- =============================================
-- 用户自我画像功能数据库设计
-- =============================================
-- 版本: 1.0.0
-- 作者: iFlow CLI
-- 日期: 2026-03-22
-- 描述: 用户自我画像功能数据采集与分析
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 软件使用数据表
-- =============================================
DROP TABLE IF EXISTS `up_software_usage`;
CREATE TABLE `up_software_usage` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `software_name` VARCHAR(100) NOT NULL COMMENT '软件名称',
    `start_time` DATETIME NOT NULL COMMENT '启动时间',
    `duration` INT(11) NOT NULL DEFAULT 0 COMMENT '运行时长（秒）',
    `window_title` VARCHAR(255) DEFAULT NULL COMMENT '窗口标题',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_software_name` (`software_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='软件使用数据表';

-- =============================================
-- 文件操作数据表
-- =============================================
DROP TABLE IF EXISTS `up_file_operation`;
CREATE TABLE `up_file_operation` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型（create/open/modify/delete/rename/copy/move）',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型（如.doc, .pdf, .jpg等）',
    `operation_time` DATETIME NOT NULL COMMENT '操作时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operation_time` (`operation_time`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_file_type` (`file_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件操作数据表';

-- =============================================
-- 浏览器行为数据表
-- =============================================
DROP TABLE IF EXISTS `up_browser_behavior`;
CREATE TABLE `up_browser_behavior` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `url` VARCHAR(1000) NOT NULL COMMENT '访问URL',
    `page_title` VARCHAR(255) DEFAULT NULL COMMENT '页面标题',
    `visit_time` DATETIME NOT NULL COMMENT '访问时间',
    `duration` INT(11) NOT NULL DEFAULT 0 COMMENT '停留时长（秒）',
    `browser_type` VARCHAR(50) DEFAULT NULL COMMENT '浏览器类型（Chrome/Firefox/Edge等）',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_visit_time` (`visit_time`),
    KEY `idx_browser_type` (`browser_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='浏览器行为数据表';

-- =============================================
-- 用户画像表
-- =============================================
DROP TABLE IF EXISTS `up_user_profile`;
CREATE TABLE `up_user_profile` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `profile_date` DATE NOT NULL COMMENT '画像日期',
    
    -- 基础属性画像
    `activity_level` VARCHAR(20) DEFAULT NULL COMMENT '活跃度（high/medium/low）',
    `usage_period` VARCHAR(50) DEFAULT NULL COMMENT '使用时段（morning/afternoon/evening）',
    `device_dependency` VARCHAR(20) DEFAULT NULL COMMENT '设备依赖度（high/medium/low）',
    `work_rhythm` VARCHAR(255) DEFAULT NULL COMMENT '工作节奏描述',
    
    -- 软件使用画像
    `software_preference` TEXT DEFAULT NULL COMMENT '软件偏好（JSON格式存储）',
    `software_diversity` INT(11) DEFAULT 0 COMMENT '软件多样性（使用不同类别软件的数量）',
    `professional_level` VARCHAR(20) DEFAULT NULL COMMENT '专业程度（high/medium/low）',
    
    -- 网络行为画像
    `browser_preference` VARCHAR(50) DEFAULT NULL COMMENT '浏览器偏好',
    `visit_category` TEXT DEFAULT NULL COMMENT '访问类别（JSON格式存储）',
    `online_duration` INT(11) DEFAULT 0 COMMENT '在线时长（秒）',
    
    -- 统计数据
    `software_start_count` INT(11) DEFAULT 0 COMMENT '软件启动次数',
    `file_operation_count` INT(11) DEFAULT 0 COMMENT '文件操作次数',
    `browser_visit_count` INT(11) DEFAULT 0 COMMENT '浏览器访问次数',
    `total_usage_duration` INT(11) DEFAULT 0 COMMENT '总使用时长（秒）',
    
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `profile_date`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_profile_date` (`profile_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表';

-- =============================================
-- 初始化菜单数据
-- =============================================
-- 插入用户画像管理菜单（放在工具箱下，工具箱ID为3）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, remark) VALUES
('用户画像', 3, 2, '/userprofile', 'userprofile/UserProfile', 'C', '0', '0', 'userprofile:list', 'user', 'admin', '用户画像管理菜单');

SET @userprofile_menu_id = LAST_INSERT_ID();

-- 插入用户画像按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by) VALUES
('用户画像查询', @userprofile_menu_id, 1, 'F', '0', '0', 'userprofile:query', 'admin'),
('用户画像新增', @userprofile_menu_id, 2, 'F', '0', '0', 'userprofile:add', 'admin'),
('用户画像修改', @userprofile_menu_id, 3, 'F', '0', '0', 'userprofile:edit', 'admin'),
('用户画像删除', @userprofile_menu_id, 4, 'F', '0', '0', 'userprofile:remove', 'admin'),
('用户画像导出', @userprofile_menu_id, 5, 'F', '0', '0', 'userprofile:export', 'admin');

-- 为超级管理员角色分配菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, @userprofile_menu_id);

-- 为普通用户角色分配菜单权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, @userprofile_menu_id);

SET FOREIGN_KEY_CHECKS = 1;