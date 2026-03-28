-- 资讯站独立域升级脚本（不改动公告 sys_notice）

CREATE TABLE IF NOT EXISTS `up_news_category` (
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

CREATE TABLE IF NOT EXISTS `up_news` (
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

CREATE TABLE IF NOT EXISTS `up_news_like` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `news_id` BIGINT(20) NOT NULL COMMENT '资讯ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_news_like` (`user_id`, `news_id`),
    KEY `idx_news_like_news` (`news_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯点赞表';

CREATE TABLE IF NOT EXISTS `up_news_favorite` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `news_id` BIGINT(20) NOT NULL COMMENT '资讯ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_news_favorite` (`user_id`, `news_id`),
    KEY `idx_news_favorite_news` (`news_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯收藏表';

CREATE TABLE IF NOT EXISTS `up_news_comment` (
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

INSERT INTO `up_news_category` (`id`, `category_name`, `parent_id`, `sort_num`, `status`, `create_by`)
SELECT 1, '干货分享', 0, 1, '0', 'admin' WHERE NOT EXISTS (SELECT 1 FROM up_news_category WHERE id = 1);
INSERT INTO `up_news_category` (`id`, `category_name`, `parent_id`, `sort_num`, `status`, `create_by`)
SELECT 2, '专题合集', 0, 2, '0', 'admin' WHERE NOT EXISTS (SELECT 1 FROM up_news_category WHERE id = 2);
INSERT INTO `up_news_category` (`id`, `category_name`, `parent_id`, `sort_num`, `status`, `create_by`)
SELECT 3, '行业快讯', 0, 3, '0', 'admin' WHERE NOT EXISTS (SELECT 1 FROM up_news_category WHERE id = 3);
INSERT INTO `up_news_category` (`id`, `category_name`, `parent_id`, `sort_num`, `status`, `create_by`)
SELECT 4, '市场观察', 0, 4, '0', 'admin' WHERE NOT EXISTS (SELECT 1 FROM up_news_category WHERE id = 4);

-- =============================================
-- 菜单补充：资讯管理（同级于工具箱）
-- =============================================
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 4, '资讯管理', 0, 4, '/news', NULL, NULL, 'M', '0', '0', NULL, 'message', '资讯管理目录', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 4);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 210, '内容管理', 4, 1, '/news/content', 'news/content', NULL, 'C', '0', '0', 'news:content:list', 'document', '资讯内容管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 210);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 220, '分类管理', 4, 2, '/news/category', 'news/category', NULL, 'C', '0', '0', 'news:category:list', 'tree', '资讯分类管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 220);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 230, '评论管理', 4, 3, '/news/comment', 'news/comment', NULL, 'C', '0', '0', 'news:comment:list', 'chat-line-round', '资讯评论管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 230);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 240, '互动管理', 4, 4, '/news/interaction', 'news/interaction', NULL, 'C', '0', '0', 'news:interaction:list', 'data-analysis', '资讯互动管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 240);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 4 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 4);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 210 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 210);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 220 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 220);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 230 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 230);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 240 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 240);
