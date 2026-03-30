-- 多语言功能数据库迁移脚本
-- 创建日期: 2026-03-30

-- 1. 为菜单表增加多语言字段
ALTER TABLE `sys_menu`
ADD COLUMN `menu_name_en` VARCHAR(50) DEFAULT NULL COMMENT '菜单名称（英文）' AFTER `menu_name`,
ADD COLUMN `menu_name_zh` VARCHAR(50) DEFAULT NULL COMMENT '菜单名称（中文）' AFTER `menu_name_en`;

-- 2. 迁移现有菜单名称到中文字段
UPDATE `sys_menu` SET `menu_name_zh` = `menu_name` WHERE `menu_name_zh` IS NULL;

-- 3. 为常用菜单添加英文名称（示例）
UPDATE `sys_menu` SET `menu_name_en` = 'System Management' WHERE `menu_name` = '系统管理';
UPDATE `sys_menu` SET `menu_name_en` = 'User Management' WHERE `menu_name` = '用户管理';
UPDATE `sys_menu` SET `menu_name_en` = 'Role Management' WHERE `menu_name` = '角色管理';
UPDATE `sys_menu` SET `menu_name_en` = 'Menu Management' WHERE `menu_name` = '菜单管理';
UPDATE `sys_menu` SET `menu_name_en` = 'Dictionary Management' WHERE `menu_name` = '字典管理';
UPDATE `sys_menu` SET `menu_name_en` = 'Parameter Settings' WHERE `menu_name` = '参数设置';
UPDATE `sys_menu` SET `menu_name_en` = 'Notice Management' WHERE `menu_name` = '通知公告';
UPDATE `sys_menu` SET `menu_name_en` = 'Operation Log' WHERE `menu_name` = '操作日志';
UPDATE `sys_menu` SET `menu_name_en` = 'Login Log' WHERE `menu_name` = '登录日志';
UPDATE `sys_menu` SET `menu_name_en` = 'Online Users' WHERE `menu_name` = '在线用户';
UPDATE `sys_menu` SET `menu_name_en` = 'News Management' WHERE `menu_name` = '资讯管理';
UPDATE `sys_menu` SET `menu_name_en` = 'Sale Management' WHERE `menu_name` = '售卖管理';

-- 4. 为用户表增加语言偏好字段
ALTER TABLE `sys_user`
ADD COLUMN `language` VARCHAR(10) DEFAULT 'zh' COMMENT '语言偏好（zh中文 en英文）' AFTER `avatar`;