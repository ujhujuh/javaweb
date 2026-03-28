-- =============================================
-- 资料线上售卖模块升级脚本（MVP）
-- =============================================

CREATE TABLE IF NOT EXISTS `up_material_category` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '父级分类ID',
    `sort_num` INT(11) NOT NULL DEFAULT 0 COMMENT '排序值',
    `status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '状态（0正常 1禁用）',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_material_category_parent` (`parent_id`),
    KEY `idx_material_category_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料分类表';

CREATE TABLE IF NOT EXISTS `up_material` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '资料ID',
    `title` VARCHAR(200) NOT NULL COMMENT '资料标题',
    `summary` VARCHAR(1000) DEFAULT NULL COMMENT '资料简介',
    `detail_content` LONGTEXT COMMENT '资料详情',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图',
    `preview_url` VARCHAR(500) DEFAULT NULL COMMENT '试看地址',
    `file_url` VARCHAR(500) NOT NULL COMMENT '资料文件地址',
    `file_format` VARCHAR(100) DEFAULT NULL COMMENT '文件格式',
    `file_size` VARCHAR(50) DEFAULT NULL COMMENT '文件大小',
    `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '售价',
    `sales_count` INT(11) NOT NULL DEFAULT 0 COMMENT '销量',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签',
    `category_id` BIGINT(20) NOT NULL COMMENT '分类ID',
    `status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '状态（0草稿 1上架 2下架）',
    `max_download_count` INT(11) NOT NULL DEFAULT 99 COMMENT '最大下载次数',
    `is_free` CHAR(1) NOT NULL DEFAULT '0' COMMENT '是否免费（0付费 1免费）',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_material_category` (`category_id`),
    KEY `idx_material_status` (`status`),
    KEY `idx_material_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料资源表';

CREATE TABLE IF NOT EXISTS `up_material_order` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `material_id` BIGINT(20) NOT NULL COMMENT '资料ID',
    `material_title` VARCHAR(200) NOT NULL COMMENT '资料标题快照',
    `material_cover` VARCHAR(500) DEFAULT NULL COMMENT '资料封面快照',
    `pay_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '支付金额',
    `pay_type` VARCHAR(20) DEFAULT NULL COMMENT '支付方式',
    `status` VARCHAR(20) NOT NULL COMMENT '订单状态',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `deliver_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `close_time` DATETIME DEFAULT NULL COMMENT '关闭时间',
    `pay_trade_no` VARCHAR(100) DEFAULT NULL COMMENT '支付流水号',
    `download_count` INT(11) NOT NULL DEFAULT 0 COMMENT '下载次数',
    `max_download_count` INT(11) NOT NULL DEFAULT 99 COMMENT '最大下载次数',
    `create_by` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_material_order_no` (`order_no`),
    KEY `idx_material_order_user` (`user_id`),
    KEY `idx_material_order_material` (`material_id`),
    KEY `idx_material_order_status` (`status`),
    KEY `idx_material_order_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料订单表';

INSERT INTO `up_material_category` (`id`, `category_name`, `parent_id`, `sort_num`, `status`, `create_by`)
SELECT 1, '学习资料', 0, 1, '0', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `up_material_category` WHERE `id` = 1);

INSERT INTO `up_material_category` (`id`, `category_name`, `parent_id`, `sort_num`, `status`, `create_by`)
SELECT 2, '行业文档', 0, 2, '0', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `up_material_category` WHERE `id` = 2);

INSERT INTO `up_material_category` (`id`, `category_name`, `parent_id`, `sort_num`, `status`, `create_by`)
SELECT 3, '设计模板', 0, 3, '0', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `up_material_category` WHERE `id` = 3);

INSERT INTO `up_material` (`id`, `title`, `summary`, `detail_content`, `cover_image`, `preview_url`, `file_url`,
                            `file_format`, `file_size`, `price`, `sales_count`, `tags`, `category_id`, `status`,
                            `max_download_count`, `is_free`, `publish_time`, `create_by`)
SELECT 1,
       'Spring Boot 实战模板包',
       '覆盖认证、日志、权限控制等常见企业场景，开箱即用。',
       '<p>包含项目骨架、权限设计说明、部署指南。</p>',
       'https://picsum.photos/seed/material-1/1200/600',
       'https://example.com/material/preview-1',
       'https://example.com/material/file-1.zip',
       'zip',
       '38MB',
       49.90,
       0,
       'Spring Boot,后端模板',
       1,
       '1',
       99,
       '0',
       NOW(),
       'admin'
WHERE NOT EXISTS (SELECT 1 FROM `up_material` WHERE `id` = 1);
