-- =============================================
-- JavaWeb 应用框架数据库设计
-- =============================================
-- 版本: 1.0.0
-- 作者: iFlow CLI
-- 日期: 2026-03-14
-- 描述: 企业级JavaWeb应用框架数据库设计
-- =============================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 用户表
-- =============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户账号',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '用户昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '用户邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号码',
    `sex` CHAR(1) DEFAULT '0' COMMENT '用户性别（0未知 1男 2女）',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    `password` VARCHAR(100) DEFAULT NULL COMMENT '密码',
    `status` CHAR(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    `login_ip` VARCHAR(128) DEFAULT NULL COMMENT '最后登录IP',
    `login_date` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 角色表
-- =============================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_key` VARCHAR(50) NOT NULL COMMENT '角色权限字符串',
    `role_sort` INT(4) NOT NULL COMMENT '显示顺序',
    `status` CHAR(1) DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- =============================================
-- 菜单表
-- =============================================
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父菜单ID',
    `order_num` INT(4) DEFAULT 0 COMMENT '显示顺序',
    `path` VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `query` VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
    `menu_type` CHAR(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `visible` CHAR(1) DEFAULT '0' COMMENT '显示状态（0显示 1隐藏）',
    `status` CHAR(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    `perms` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `icon` VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- =============================================
-- 用户角色关联表
-- =============================================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `role_id` BIGINT(20) NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- =============================================
-- 角色菜单关联表
-- =============================================
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT(20) NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT(20) NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- =============================================
-- 字典类型表
-- =============================================
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `dict_name` VARCHAR(100) DEFAULT '' COMMENT '字典名称',
    `dict_type` VARCHAR(100) DEFAULT '' COMMENT '字典类型',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- =============================================
-- 字典数据表
-- =============================================
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    `dict_sort` INT(4) DEFAULT 0 COMMENT '字典排序',
    `dict_label` VARCHAR(100) DEFAULT '' COMMENT '字典标签',
    `dict_value` VARCHAR(100) DEFAULT '' COMMENT '字典键值',
    `dict_type` VARCHAR(100) DEFAULT '' COMMENT '字典类型',
    `is_default` CHAR(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- =============================================
-- 参数配置表
-- =============================================
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    `config_name` VARCHAR(100) DEFAULT '' COMMENT '参数名称',
    `config_key` VARCHAR(100) DEFAULT '' COMMENT '参数键名',
    `config_value` VARCHAR(500) DEFAULT '' COMMENT '参数键值',
    `config_type` CHAR(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

-- =============================================
-- 通知公告表
-- =============================================
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `notice_title` VARCHAR(50) NOT NULL COMMENT '公告标题',
    `notice_type` CHAR(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
    `notice_content` LONGTEXT COMMENT '公告内容',
    `status` CHAR(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(255) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- =============================================
-- 用户公告关联表
-- =============================================
DROP TABLE IF EXISTS `sys_user_notice`;
CREATE TABLE `sys_user_notice` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `notice_id` BIGINT(20) NOT NULL COMMENT '公告ID',
    `read_status` CHAR(1) DEFAULT '0' COMMENT '阅读状态（0未读 1已读）',
    `read_time` VARCHAR(20) DEFAULT NULL COMMENT '阅读时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_notice` (`user_id`, `notice_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_notice_id` (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户公告关联表';

-- =============================================
-- 资讯分类表
-- =============================================
DROP TABLE IF EXISTS `up_news_category`;
CREATE TABLE `up_news_category` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父分类ID',
    `sort_num` INT(11) DEFAULT 0 COMMENT '排序',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1禁用）',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_news_category_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯分类表';

-- =============================================
-- 资讯内容表
-- =============================================
DROP TABLE IF EXISTS `up_news`;
CREATE TABLE `up_news` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `summary` VARCHAR(1000) DEFAULT NULL COMMENT '摘要',
    `content` LONGTEXT COMMENT '正文',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签（逗号分隔）',
    `category_id` BIGINT(20) DEFAULT NULL COMMENT '分类ID',
    `visible_scope` CHAR(1) DEFAULT '0' COMMENT '可见范围（0公开 1登录可见）',
    `is_top` CHAR(1) DEFAULT '0' COMMENT '置顶（0否 1是）',
    `status` CHAR(1) DEFAULT '1' COMMENT '状态（0草稿 1发布 2下线）',
    `view_count` INT(11) DEFAULT 0 COMMENT '阅读量',
    `like_count` INT(11) DEFAULT 0 COMMENT '点赞量',
    `favorite_count` INT(11) DEFAULT 0 COMMENT '收藏量',
    `comment_count` INT(11) DEFAULT 0 COMMENT '评论量',
    `publish_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_news_category_id` (`category_id`),
    KEY `idx_news_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯内容表';

-- =============================================
-- 资讯点赞表
-- =============================================
DROP TABLE IF EXISTS `up_news_like`;
CREATE TABLE `up_news_like` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `news_id` BIGINT(20) NOT NULL COMMENT '资讯ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_news_like` (`user_id`, `news_id`),
    KEY `idx_news_like_news` (`news_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯点赞表';

-- =============================================
-- 资讯收藏表
-- =============================================
DROP TABLE IF EXISTS `up_news_favorite`;
CREATE TABLE `up_news_favorite` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `news_id` BIGINT(20) NOT NULL COMMENT '资讯ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_news_favorite` (`user_id`, `news_id`),
    KEY `idx_news_favorite_news` (`news_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯收藏表';

-- =============================================
-- 资讯评论表
-- =============================================
DROP TABLE IF EXISTS `up_news_comment`;
CREATE TABLE `up_news_comment` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `news_id` BIGINT(20) NOT NULL COMMENT '资讯ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '评论用户ID',
    `content` VARCHAR(1000) NOT NULL COMMENT '评论内容',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1删除）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_news_comment_news` (`news_id`),
    KEY `idx_news_comment_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯评论表';

-- =============================================
-- 操作日志表
-- =============================================
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `title` VARCHAR(50) DEFAULT '' COMMENT '模块标题',
    `business_type` INT(2) DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    `method` VARCHAR(100) DEFAULT '' COMMENT '方法名称',
    `request_method` VARCHAR(10) DEFAULT '' COMMENT '请求方式',
    `operator_type` INT(1) DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
    `oper_name` VARCHAR(50) DEFAULT '' COMMENT '操作人员',
    `oper_url` VARCHAR(255) DEFAULT '' COMMENT '请求URL',
    `oper_ip` VARCHAR(128) DEFAULT '' COMMENT '主机地址',
    `oper_location` VARCHAR(255) DEFAULT '' COMMENT '操作地点',
    `oper_param` VARCHAR(2000) DEFAULT '' COMMENT '请求参数',
    `json_result` VARCHAR(2000) DEFAULT '' COMMENT '返回参数',
    `status` INT(1) DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    `error_msg` VARCHAR(2000) DEFAULT '' COMMENT '错误消息',
    `oper_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `cost_time` BIGINT(20) DEFAULT 0 COMMENT '消耗时间',
    PRIMARY KEY (`id`),
    KEY `idx_oper_time` (`oper_time`),
    KEY `idx_oper_name` (`oper_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';

-- =============================================
-- 登录日志表
-- =============================================
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `username` VARCHAR(50) DEFAULT '' COMMENT '用户账号',
    `ipaddr` VARCHAR(128) DEFAULT '' COMMENT '登录IP地址',
    `login_location` VARCHAR(255) DEFAULT '' COMMENT '登录地点',
    `browser` VARCHAR(50) DEFAULT '' COMMENT '浏览器类型',
    `os` VARCHAR(50) DEFAULT '' COMMENT '操作系统',
    `status` CHAR(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
    `msg` VARCHAR(255) DEFAULT '' COMMENT '提示消息',
    `login_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    PRIMARY KEY (`id`),
    KEY `idx_login_time` (`login_time`),
    KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录';

-- =============================================
-- 在线用户表
-- =============================================
DROP TABLE IF EXISTS `sys_user_online`;
CREATE TABLE `sys_user_online` (
    `session_id` VARCHAR(50) NOT NULL COMMENT '用户会话ID',
    `login_name` VARCHAR(50) DEFAULT '' COMMENT '登录账号',
    `ipaddr` VARCHAR(128) DEFAULT '' COMMENT '登录IP地址',
    `login_location` VARCHAR(255) DEFAULT '' COMMENT '登录地点',
    `browser` VARCHAR(50) DEFAULT '' COMMENT '浏览器类型',
    `os` VARCHAR(50) DEFAULT '' COMMENT '操作系统',
    `status` VARCHAR(10) DEFAULT '' COMMENT '在线状态（on_line在线 off_line离线）',
    `start_timestamp` DATETIME DEFAULT NULL COMMENT 'session创建时间',
    `last_access_time` DATETIME DEFAULT NULL COMMENT 'session最后访问时间',
    `expire_time` INT(5) DEFAULT NULL COMMENT '超时时间，单位为分钟',
    PRIMARY KEY (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='在线用户表';

-- =============================================
-- 初始化数据 - 用户
-- =============================================
INSERT INTO `sys_user` (`id`, `username`, `nickname`, `email`, `phone`, `sex`, `avatar`, `password`, `status`, `del_flag`, `create_by`) VALUES
(1, 'admin', '超级管理员', 'admin@javaweb.com', '15888888888', '1', '/profile/avatar/2024/01/01/admin.jpg', '$2a$10$HEKFo3Iob.o23M1bxSHXz.dtMzSXlkIy4Bw1gJuqZG4Pt/xVeLnUO', '0', '0', 'admin'),
(2, 'zhangsan', '张三', 'zhangsan@javaweb.com', '15888888889', '1', '/profile/avatar/2024/01/01/zhangsan.jpg', '$2a$10$HEKFo3Iob.o23M1bxSHXz.dtMzSXlkIy4Bw1gJuqZG4Pt/xVeLnUO', '0', '0', 'admin'),
(3, 'lisi', '李四', 'lisi@javaweb.com', '15888888890', '2', '/profile/avatar/2024/01/01/lisi.jpg', '$2a$10$HEKFo3Iob.o23M1bxSHXz.dtMzSXlkIy4Bw1gJuqZG4Pt/xVeLnUO', '0', '0', 'admin'),
(4, 'wangwu', '王五', 'wangwu@javaweb.com', '15888888891', '1', '/profile/avatar/2024/01/01/wangwu.jpg', '$2a$10$HEKFo3Iob.o23M1bxSHXz.dtMzSXlkIy4Bw1gJuqZG4Pt/xVeLnUO', '0', '0', 'admin'),
(5, 'zhaoliu', '赵六', 'zhaoliu@javaweb.com', '15888888892', '2', '/profile/avatar/2024/01/01/zhaoliu.jpg', '$2a$10$HEKFo3Iob.o23M1bxSHXz.dtMzSXlkIy4Bw1gJuqZG4Pt/xVeLnUO', '0', '0', 'admin'),
(6, 'sunqi', '孙七', 'sunqi@javaweb.com', '15888888893', '1', '/profile/avatar/2024/01/01/sunqi.jpg', '$2a$10$HEKFo3Iob.o23M1bxSHXz.dtMzSXlkIy4Bw1gJuqZG4Pt/xVeLnUO', '0', '0', 'admin'),
(7, 'zhouba', '周八', 'zhouba@javaweb.com', '15888888894', '1', '/profile/avatar/2024/01/01/zhouba.jpg', '$2a$10$HEKFo3Iob.o23M1bxSHXz.dtMzSXlkIy4Bw1gJuqZG4Pt/xVeLnUO', '0', '0', 'admin');

-- =============================================
-- 初始化数据 - 角色
-- =============================================
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `role_sort`, `status`, `del_flag`, `remark`, `create_by`) VALUES
(1, '超级管理员', 'admin', 1, '0', '0', '拥有所有权限', 'admin'),
(2, '普通用户', 'user', 2, '0', '0', '普通用户权限', 'admin'),
(3, '访客', 'guest', 3, '0', '0', '仅查看权限', 'admin');

-- =============================================
-- 初始化数据 - 用户角色关联
-- =============================================
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(2, 2),
(3, 2),
(4, 2),
(5, 2),
(6, 3),
(7, 3);

-- =============================================
-- 初始化数据 - 菜单
-- =============================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`) VALUES
-- 一级菜单
(1, '系统管理', 0, 1, 'system', NULL, NULL, 'M', '0', '0', NULL, 'system', '系统管理目录', 'admin'),
(2, '系统监控', 0, 2, 'monitor', NULL, NULL, 'M', '0', '0', NULL, 'monitor', '系统监控目录', 'admin'),

-- 系统管理 - 用户管理
(100, '用户管理', 1, 1, 'user', 'system/user/index', NULL, 'C', '0', '0', 'system:user:list', 'user', '用户管理菜单', 'admin'),
(101, '用户查询', 100, 1, NULL, NULL, NULL, 'F', '0', '0', 'system:user:query', '#', NULL, 'admin'),
(102, '用户新增', 100, 2, NULL, NULL, NULL, 'F', '0', '0', 'system:user:add', '#', NULL, 'admin'),
(103, '用户修改', 100, 3, NULL, NULL, NULL, 'F', '0', '0', 'system:user:edit', '#', NULL, 'admin'),
(104, '用户删除', 100, 4, NULL, NULL, NULL, 'F', '0', '0', 'system:user:remove', '#', NULL, 'admin'),
(105, '用户重置密码', 100, 5, NULL, NULL, NULL, 'F', '0', '0', 'system:user:resetPwd', '#', NULL, 'admin'),
(106, '用户导入', 100, 6, NULL, NULL, NULL, 'F', '0', '0', 'system:user:import', '#', NULL, 'admin'),
(107, '用户导出', 100, 7, NULL, NULL, NULL, 'F', '0', '0', 'system:user:export', '#', NULL, 'admin'),

-- 系统管理 - 角色管理
(108, '角色管理', 1, 2, 'role', 'system/role/index', NULL, 'C', '0', '0', 'system:role:list', 'peoples', '角色管理菜单', 'admin'),
(109, '角色查询', 108, 1, NULL, NULL, NULL, 'F', '0', '0', 'system:role:query', '#', NULL, 'admin'),
(110, '角色新增', 108, 2, NULL, NULL, NULL, 'F', '0', '0', 'system:role:add', '#', NULL, 'admin'),
(111, '角色修改', 108, 3, NULL, NULL, NULL, 'F', '0', '0', 'system:role:edit', '#', NULL, 'admin'),
(112, '角色删除', 108, 4, NULL, NULL, NULL, 'F', '0', '0', 'system:role:remove', '#', NULL, 'admin'),
(113, '角色导出', 108, 5, NULL, NULL, NULL, 'F', '0', '0', 'system:role:export', '#', NULL, 'admin'),

-- 系统管理 - 菜单管理
(114, '菜单管理', 1, 3, 'menu', 'system/menu/index', NULL, 'C', '0', '0', 'system:menu:list', 'tree-table', '菜单管理菜单', 'admin'),
(115, '菜单查询', 114, 1, NULL, NULL, NULL, 'F', '0', '0', 'system:menu:query', '#', NULL, 'admin'),
(116, '菜单新增', 114, 2, NULL, NULL, NULL, 'F', '0', '0', 'system:menu:add', '#', NULL, 'admin'),
(117, '菜单修改', 114, 3, NULL, NULL, NULL, 'F', '0', '0', 'system:menu:edit', '#', NULL, 'admin'),
(118, '菜单删除', 114, 4, NULL, NULL, NULL, 'F', '0', '0', 'system:menu:remove', '#', NULL, 'admin'),

-- 系统管理 - 字典管理
(119, '字典管理', 1, 4, 'dict', 'system/dict/index', NULL, 'C', '0', '0', 'system:dict:list', 'dict', '字典管理菜单', 'admin'),
(120, '字典查询', 119, 1, NULL, NULL, NULL, 'F', '0', '0', 'system:dict:query', '#', NULL, 'admin'),
(121, '字典新增', 119, 2, NULL, NULL, NULL, 'F', '0', '0', 'system:dict:add', '#', NULL, 'admin'),
(122, '字典修改', 119, 3, NULL, NULL, NULL, 'F', '0', '0', 'system:dict:edit', '#', NULL, 'admin'),
(123, '字典删除', 119, 4, NULL, NULL, NULL, 'F', '0', '0', 'system:dict:remove', '#', NULL, 'admin'),
(124, '字典导出', 119, 5, NULL, NULL, NULL, 'F', '0', '0', 'system:dict:export', '#', NULL, 'admin'),

-- 系统管理 - 参数设置
(125, '参数设置', 1, 5, 'config', 'system/config/index', NULL, 'C', '0', '0', 'system:config:list', 'edit', '参数设置菜单', 'admin'),
(126, '参数查询', 125, 1, NULL, NULL, NULL, 'F', '0', '0', 'system:config:query', '#', NULL, 'admin'),
(127, '参数新增', 125, 2, NULL, NULL, NULL, 'F', '0', '0', 'system:config:add', '#', NULL, 'admin'),
(128, '参数修改', 125, 3, NULL, NULL, NULL, 'F', '0', '0', 'system:config:edit', '#', NULL, 'admin'),
(129, '参数删除', 125, 4, NULL, NULL, NULL, 'F', '0', '0', 'system:config:remove', '#', NULL, 'admin'),
(130, '参数刷新', 125, 5, NULL, NULL, NULL, 'F', '0', '0', 'system:config:refresh', '#', NULL, 'admin'),

-- 系统管理 - 通知公告
(131, '通知公告', 1, 6, 'notice', 'system/notice/index', NULL, 'C', '0', '0', 'system:notice:list', 'message', '通知公告菜单', 'admin'),
(132, '公告查询', 131, 1, NULL, NULL, NULL, 'F', '0', '0', 'system:notice:query', '#', NULL, 'admin'),
(133, '公告新增', 131, 2, NULL, NULL, NULL, 'F', '0', '0', 'system:notice:add', '#', NULL, 'admin'),
(134, '公告修改', 131, 3, NULL, NULL, NULL, 'F', '0', '0', 'system:notice:edit', '#', NULL, 'admin'),
(135, '公告删除', 131, 4, NULL, NULL, NULL, 'F', '0', '0', 'system:notice:remove', '#', NULL, 'admin'),

-- 系统管理 - 日志管理
(136, '日志管理', 1, 7, 'log', NULL, NULL, 'M', '0', '0', NULL, 'log', '日志管理目录', 'admin'),
(137, '操作日志', 136, 1, 'operlog', 'system/operlog/index', NULL, 'C', '0', '0', 'system:operlog:list', 'form', '操作日志菜单', 'admin'),
(138, '操作查询', 137, 1, NULL, NULL, NULL, 'F', '0', '0', 'system:operlog:query', '#', NULL, 'admin'),
(140, '登录日志', 136, 2, 'loginlog', 'system/loginlog/index', NULL, 'C', '0', '0', 'system:loginlog:list', 'logininfor', '登录日志菜单', 'admin'),
(141, '登录查询', 140, 1, NULL, NULL, NULL, 'F', '0', '0', 'system:loginlog:query', '#', NULL, 'admin'),
(143, '在线用户', 136, 3, 'online', 'system/online/index', NULL, 'C', '0', '0', 'system:online:list', 'online', '在线用户菜单', 'admin'),
(144, '用户强退', 143, 1, NULL, NULL, NULL, 'F', '0', '0', 'system:online:forceLogout', '#', NULL, 'admin');


-- =============================================
-- 初始化数据 - 角色菜单关联
-- =============================================
-- 超级管理员拥有所有权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105), (1, 106), (1, 107),
(1, 108), (1, 109), (1, 110), (1, 111), (1, 112), (1, 113),
(1, 114), (1, 115), (1, 116), (1, 117), (1, 118),
(1, 119), (1, 120), (1, 121), (1, 122), (1, 123), (1, 124),
(1, 125), (1, 126), (1, 127), (1, 128), (1, 129), (1, 130),
(1, 131), (1, 132), (1, 133), (1, 134), (1, 135),
(1, 136), (1, 137), (1, 138), (1, 139),
(1, 140), (1, 141), (1, 142),
(1, 143), (1, 144),
(1, 2), (1, 145), (1, 146), (1, 147);

-- 普通用户权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 1), (2, 131), (2, 132), (2, 136), (2, 140), (2, 141), (2, 2);

-- 访客权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3, 1), (3, 131), (3, 132);

-- =============================================
-- 初始化数据 - 字典类型
-- =============================================
INSERT INTO `sys_dict_type` (`id`, `dict_name`, `dict_type`, `status`, `create_by`) VALUES
(1, '用户性别', 'sys_user_sex', '0', 'admin'),
(2, '菜单状态', 'sys_show_hide', '0', 'admin'),
(3, '系统开关', 'sys_normal_disable', '0', 'admin'),
(4, '任务状态', 'sys_job_status', '0', 'admin'),
(5, '任务分组', 'sys_job_group', '0', 'admin'),
(6, '是否', 'sys_yes_no', '0', 'admin'),
(7, '通知类型', 'sys_notice_type', '0', 'admin'),
(8, '通知状态', 'sys_notice_status', '0', 'admin'),
(9, '操作类型', 'sys_oper_type', '0', 'admin'),
(10, '系统状态', 'sys_common_status', '0', 'admin'),
(11, '用户状态', 'sys_user_status', '0', 'admin'),
(12, '菜单类型', 'sys_menu_type', '0', 'admin');

-- =============================================
-- 初始化数据 - 字典数据
-- =============================================
INSERT INTO `sys_dict_data` (`id`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `is_default`, `status`, `create_by`) VALUES
-- 用户性别
(1, 1, '男', '1', 'sys_user_sex', 'Y', '0', 'admin'),
(2, 2, '女', '2', 'sys_user_sex', 'N', '0', 'admin'),
(3, 3, '未知', '0', 'sys_user_sex', 'N', '0', 'admin'),
-- 菜单状态
(4, 1, '显示', '0', 'sys_show_hide', 'Y', '0', 'admin'),
(5, 2, '隐藏', '1', 'sys_show_hide', 'N', '0', 'admin'),
-- 系统开关
(6, 1, '正常', '0', 'sys_normal_disable', 'Y', '0', 'admin'),
(7, 2, '停用', '1', 'sys_normal_disable', 'N', '0', 'admin'),
-- 是否
(8, 1, '是', 'Y', 'sys_yes_no', 'Y', '0', 'admin'),
(9, 2, '否', 'N', 'sys_yes_no', 'N', '0', 'admin'),
-- 通知类型
(10, 1, '通知', '1', 'sys_notice_type', 'Y', '0', 'admin'),
(11, 2, '公告', '2', 'sys_notice_type', 'N', '0', 'admin'),
-- 通知状态
(12, 1, '正常', '0', 'sys_notice_status', 'Y', '0', 'admin'),
(13, 2, '关闭', '1', 'sys_notice_status', 'N', '0', 'admin'),
-- 操作类型
(14, 1, '新增', '1', 'sys_oper_type', 'N', '0', 'admin'),
(15, 2, '修改', '2', 'sys_oper_type', 'N', '0', 'admin'),
(16, 3, '删除', '3', 'sys_oper_type', 'N', '0', 'admin'),
(17, 4, '授权', '4', 'sys_oper_type', 'N', '0', 'admin'),
(18, 5, '导出', '5', 'sys_oper_type', 'N', '0', 'admin'),
(19, 6, '导入', '6', 'sys_oper_type', 'N', '0', 'admin'),
(20, 7, '强退', '7', 'sys_oper_type', 'N', '0', 'admin'),
(21, 8, '生成代码', '8', 'sys_oper_type', 'N', '0', 'admin'),
(22, 9, '清空数据', '9', 'sys_oper_type', 'N', '0', 'admin'),
-- 系统状态
(23, 1, '正常', '0', 'sys_common_status', 'Y', '0', 'admin'),
(24, 2, '停用', '1', 'sys_common_status', 'N', '0', 'admin'),
-- 菜单类型
(25, 1, '目录', 'M', 'sys_menu_type', 'N', '0', 'admin'),
(26, 2, '菜单', 'C', 'sys_menu_type', 'Y', '0', 'admin'),
(27, 3, '按钮', 'F', 'sys_menu_type', 'N', '0', 'admin');

-- =============================================
-- 初始化数据 - 参数配置
-- =============================================
INSERT INTO `sys_config` (`id`, `config_name`, `config_key`, `config_value`, `config_type`, `remark`, `create_by`) VALUES
(1, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', '初始化密码 123456', 'admin'),
(2, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', '默认 skin-blue skin-green skin-purple skin-red skin-yellow', 'admin'),
(3, '用户自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', '是否开启验证码功能（true开启，false关闭）', 'admin'),
(4, '用户自助-注册用户开关', 'sys.account.registerUser', 'true', 'Y', '是否开启新用户注册功能（true开启，false关闭）', 'admin'),
(5, '账号自助-找回密码开关', 'sys.account.resetPassword', 'true', 'Y', '是否开启找回密码功能（true开启，false关闭）', 'admin'),
(6, '用户登录-单点登录开关', 'sys.account.ssoLogin', 'true', 'Y', '是否开启单点登录功能（true开启，false关闭）', 'admin'),
(7, '用户登录-记住密码开关', 'sys.account.rememberMe', 'true', 'Y', '是否开启记住密码功能（true开启，false关闭）', 'admin'),
(8, '系统登录-验证码开关', 'sys.account.captchaOnOff', 'true', 'Y', '是否开启验证码功能（true开启，false关闭）', 'admin'),
(9, '用户管理-密码字符范围', 'sys.account.chrtype', '0', 'Y', '默认任意字符；0任意，1数字，2英文字母，3英文字母+数字，4英文字母+数字+特殊字符', 'admin'),
(10, '用户管理-初始密码修改策略', 'sys.account.editInitPassword', 'true', 'Y', '是否开启初始密码修改策略（true开启，false关闭）', 'admin'),
(11, '用户管理-账号自助注册', 'sys.account.register', 'true', 'Y', '是否开启用户自助注册功能（true开启，false关闭）', 'admin'),
(12, '系统设置-是否开启上传文件', 'sys.account.uploadFile', 'true', 'Y', '是否开启上传文件功能（true开启，false关闭）', 'admin'),
(13, '系统设置-上传文件路径', 'sys.profile.uploadPath', '/profile/upload', 'Y', '文件上传路径', 'admin'),
(14, '系统设置-上传文件大小限制', 'sys.profile.uploadMaxSize', '10', 'Y', '上传文件大小限制，单位MB', 'admin'),
(15, '系统设置-允许上传文件类型', 'sys.profile.uploadFileType', 'doc,docx,zip,xls,xlsx,ppt,pptx,txt,pdf', 'Y', '允许上传的文件类型', 'admin');

-- =============================================
-- 初始化数据 - 通知公告
-- =============================================
INSERT INTO `sys_notice` (`id`, `notice_title`, `notice_type`, `notice_content`, `status`, `create_by`, `remark`) VALUES
(1, '欢迎使用JavaWeb', '1', '<p>欢迎使用JavaWeb，这是一个企业级的Web应用框架。</p><p>系统特点：</p><ul><li>基于Spring Boot + MyBatis-Plus开发</li><li>完善的RBAC权限管理</li><li>丰富的系统功能模块</li><li>灵活的配置管理</li></ul>', '0', 'admin', '欢迎公告'),
(2, '系统升级公告', '2', '<p>系统将于本周六凌晨2:00-4:00进行升级维护。</p><p>升级内容：</p><ul><li>优化系统性能</li><li>修复已知问题</li><li>新增功能模块</li></ul><p>请提前做好数据备份，给您带来的不便敬请谅解。</p>', '0', 'admin', '系统维护'),
(3, '年度总结会议通知', '1', '<p>各位同事：</p><p>公司将于2024年12月31日下午14:00在第一会议室召开年度总结会议。</p><p>会议议程：</p><ul><li>年度工作总结</li><li>优秀员工表彰</li><li>下年度工作计划</li></ul><p>请各部门提前准备好相关材料，准时参会。</p>', '0', 'admin', '会议通知'),
(4, '假期放假通知', '2', '<p>根据国家法定节假日安排，现将2025年春节放假安排通知如下：</p><p><strong>放假时间：</strong>2025年1月28日至2月3日，共7天。</p><p><strong>复工时间：</strong>2025年2月4日（星期二）。</p><p>请各部门提前做好工作安排，确保节前节后工作顺利衔接。</p><p>祝大家春节快乐，阖家幸福！</p>', '0', 'admin', '放假通知'),
(5, '系统安全提醒', '1', '<p>为保障系统安全，请各位用户注意以下几点：</p><ul><li>定期修改密码，建议使用强密码</li><li>不要将账号密码告知他人</li><li>离开时请及时退出系统</li><li>发现异常情况及时联系管理员</li></ul><p>感谢您的配合！</p>', '0', 'admin', '安全提醒');

-- =============================================
-- 初始化数据 - 资讯分类
-- =============================================
INSERT INTO `up_news_category` (`id`, `category_name`, `parent_id`, `sort_num`, `status`, `create_by`) VALUES
(1, '干货分享', 0, 1, '0', 'admin'),
(2, '专题合集', 0, 2, '0', 'admin'),
(3, '行业快讯', 0, 3, '0', 'admin'),
(4, '市场观察', 0, 4, '0', 'admin');

-- =============================================
-- 初始化数据 - 资讯内容
-- =============================================
INSERT INTO `up_news` (`id`, `title`, `summary`, `content`, `cover_image`, `tags`, `category_id`, `visible_scope`, `is_top`, `status`, `view_count`, `like_count`, `favorite_count`, `comment_count`, `publish_time`, `create_by`) VALUES
(1, '2026行业数字化趋势观察', '解读行业数字化转型中的关键策略与组织变化。', '<p>这是公开资讯正文示例，访客可查看全文。</p><p>内容包含趋势、案例与实践建议。</p>', 'https://picsum.photos/seed/news-1/1200/600', '数字化,行业', 4, '0', '1', '1', 128, 18, 11, 2, DATE_SUB(NOW(), INTERVAL 1 DAY), 'admin'),
(2, '专题：增长模型拆解与落地', '登录后可查看完整章节和数据图表。', '<p>该内容为登录可见示例，包含深度图表和落地清单。</p><p>请登录后查看完整内容。</p>', 'https://picsum.photos/seed/news-2/1200/600', '增长,专题', 2, '1', '1', '1', 96, 14, 9, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), 'admin'),
(3, '每周快讯：重点资讯汇总', '汇总本周核心动态与要点。', '<p>每周快讯内容示例。</p>', 'https://picsum.photos/seed/news-3/1200/600', '快讯,周报', 3, '0', '0', '1', 73, 8, 6, 0, DATE_SUB(NOW(), INTERVAL 3 DAY), 'admin');

-- 用户配置表
CREATE TABLE IF NOT EXISTS `sys_user_config` (
                                                 `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(500) DEFAULT NULL COMMENT '配置值',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_config` (`user_id`, `config_key`),
    KEY `idx_user_id` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户配置表';

-- =============================================
-- 美股情绪指标表
-- =============================================
DROP TABLE IF EXISTS `us_sentiment`;
CREATE TABLE `us_sentiment` (
                                `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                `vix` DECIMAL(10, 2) DEFAULT NULL COMMENT 'VIX指数（恐慌指数）',
                                `fear_greed` DECIMAL(10, 2) DEFAULT NULL COMMENT 'Fear & Greed指数（恐惧贪婪指数）',
                                `naaim` DECIMAL(10, 2) DEFAULT NULL COMMENT 'NAAIM指数（机构仓位指数）',
                                `rsi_sp500` DECIMAL(10, 2) DEFAULT NULL COMMENT 'RSI标普500指数（相对强弱指数）',
                                `record_date` DATE NOT NULL COMMENT '记录日期（前一个工作日）',
                                `vix_condition` CHAR(1) DEFAULT '0' COMMENT 'VIX是否满足条件（0否 1是）',
                                `fear_greed_condition` CHAR(1) DEFAULT '0' COMMENT 'Fear & Greed是否满足条件（0否 1是）',
                                `naaim_condition` CHAR(1) DEFAULT '0' COMMENT 'NAAIM是否满足条件（0否 1是）',
                                `rsi_condition` CHAR(1) DEFAULT '0' COMMENT 'RSI是否满足条件（0否 1是）',
                                `satisfied_count` INT(2) DEFAULT 0 COMMENT '满足条件的数量（0-4）',
                                `notification_sent` CHAR(1) DEFAULT '0' COMMENT '是否已发送通知（0否 1是）',
                                `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_record_date` (`record_date`),
                                KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='美股情绪指标表';
