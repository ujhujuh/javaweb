-- =============================================
-- 菜单补充：售卖管理（与“内容管理”同级）
-- =============================================

-- 若资讯管理目录不存在则补齐
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 4, '资讯管理', 0, 4, '/news', NULL, NULL, 'M', '0', '0', NULL, 'message', '资讯管理目录', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 4);

-- 二级目录：售卖管理（与内容管理 210 同级）
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 250, '售卖管理', 4, 5, '/sale', NULL, NULL, 'M', '0', '0', NULL, 'document', '线上售卖管理目录', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 250);

-- 子菜单：资源管理
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 251, '资源管理', 250, 1, '/sale/material', 'sale/material', NULL, 'C', '0', '0', 'sale:material:list', 'document', '售卖资源管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 251);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 254, '资源查询', 251, 1, NULL, NULL, NULL, 'F', '0', '0', 'sale:material:query', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 254);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 255, '资源新增', 251, 2, NULL, NULL, NULL, 'F', '0', '0', 'sale:material:add', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 255);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 256, '资源修改', 251, 3, NULL, NULL, NULL, 'F', '0', '0', 'sale:material:edit', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 256);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 257, '资源删除', 251, 4, NULL, NULL, NULL, 'F', '0', '0', 'sale:material:remove', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 257);

-- 子菜单：分类管理
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 252, '分类管理', 250, 2, '/sale/category', 'sale/category', NULL, 'C', '0', '0', 'sale:category:list', 'tree', '售卖分类管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 252);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 258, '分类查询', 252, 1, NULL, NULL, NULL, 'F', '0', '0', 'sale:category:query', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 258);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 259, '分类新增', 252, 2, NULL, NULL, NULL, 'F', '0', '0', 'sale:category:add', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 259);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 260, '分类修改', 252, 3, NULL, NULL, NULL, 'F', '0', '0', 'sale:category:edit', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 260);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 261, '分类删除', 252, 4, NULL, NULL, NULL, 'F', '0', '0', 'sale:category:remove', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 261);

-- 子菜单：订单管理
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 253, '订单管理', 250, 3, '/sale/order', 'sale/order', NULL, 'C', '0', '0', 'sale:order:list', 'form', '售卖订单管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 253);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 262, '订单查询', 253, 1, NULL, NULL, NULL, 'F', '0', '0', 'sale:order:query', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 262);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 263, '订单补发货', 253, 2, NULL, NULL, NULL, 'F', '0', '0', 'sale:order:resend', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 263);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 264, '订单关闭', 253, 3, NULL, NULL, NULL, 'F', '0', '0', 'sale:order:close', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 264);

-- 角色授权：超级管理员默认拥有售卖管理全部菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 250 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 250);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 251 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 251);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 252 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 252);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 253 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 253);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 254 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 254);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 255 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 255);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 256 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 256);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 257 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 257);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 258 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 258);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 259 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 259);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 260 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 260);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 261 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 261);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 262 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 262);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 263 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 263);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 264 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 264);
