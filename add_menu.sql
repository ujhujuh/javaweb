-- =============================================
-- 添加美股情绪指标监控菜单
-- =============================================

-- 插入工具箱一级菜单
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`) VALUES
(3, '工具箱', 0, 3, '/toolbox', NULL, NULL, 'M', '0', '0', NULL, 'tool', '工具箱目录', 'admin');

-- 插入美股情绪指标监控菜单
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`) VALUES
(200, '美股情绪指标监控', 3, 1, '/toolbox/us-sentiment', 'toolbox/us-sentiment', NULL, 'C', '0', '0', 'toolbox:us-sentiment:list', 'chart', '美股情绪指标监控菜单', 'admin');

-- 插入美股情绪指标监控相关按钮权限
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`) VALUES
(201, '美股情绪指标查询', 200, 1, NULL, NULL, NULL, 'F', '0', '0', 'toolbox:us-sentiment:query', '#', NULL, 'admin'),
(202, '手动收集数据', 200, 2, NULL, NULL, NULL, 'F', '0', '0', 'toolbox:us-sentiment:collect', '#', NULL, 'admin'),
(203, '美股情绪指标删除', 200, 3, NULL, NULL, NULL, 'F', '0', '0', 'toolbox:us-sentiment:remove', '#', NULL, 'admin');

-- 为超级管理员角色分配新菜单权限（假设超级管理员角色ID为1）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 3),
(1, 200),
(1, 201),
(1, 202),
(1, 203);

-- 为普通用户角色分配新菜单权限（假设普通用户角色ID为2）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 3),
(2, 200),
(2, 201);

-- =============================================
-- 新增“资讯管理”菜单（与“工具箱”同级）
-- =============================================

-- 资讯管理一级目录（同级：工具箱 parent_id=0）
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 4, '资讯管理', 0, 4, '/news', NULL, NULL, 'M', '0', '0', NULL, 'message', '资讯管理目录', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 4);

-- 资讯管理子菜单：内容管理
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 210, '内容管理', 4, 1, '/news/content', 'news/content', NULL, 'C', '0', '0', 'news:content:list', 'document', '资讯内容管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 210);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 211, '内容查询', 210, 1, NULL, NULL, NULL, 'F', '0', '0', 'news:content:query', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 211);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 212, '内容新增', 210, 2, NULL, NULL, NULL, 'F', '0', '0', 'news:content:add', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 212);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 213, '内容修改', 210, 3, NULL, NULL, NULL, 'F', '0', '0', 'news:content:edit', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 213);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 214, '内容删除', 210, 4, NULL, NULL, NULL, 'F', '0', '0', 'news:content:remove', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 214);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 215, '内容发布', 210, 5, NULL, NULL, NULL, 'F', '0', '0', 'news:content:publish', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 215);

-- 资讯管理子菜单：分类管理
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 220, '分类管理', 4, 2, '/news/category', 'news/category', NULL, 'C', '0', '0', 'news:category:list', 'tree', '资讯分类管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 220);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 221, '分类查询', 220, 1, NULL, NULL, NULL, 'F', '0', '0', 'news:category:query', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 221);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 222, '分类新增', 220, 2, NULL, NULL, NULL, 'F', '0', '0', 'news:category:add', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 222);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 223, '分类修改', 220, 3, NULL, NULL, NULL, 'F', '0', '0', 'news:category:edit', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 223);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 224, '分类删除', 220, 4, NULL, NULL, NULL, 'F', '0', '0', 'news:category:remove', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 224);

-- 资讯管理子菜单：评论管理
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 230, '评论管理', 4, 3, '/news/comment', 'news/comment', NULL, 'C', '0', '0', 'news:comment:list', 'chat-line-round', '资讯评论管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 230);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 231, '评论查询', 230, 1, NULL, NULL, NULL, 'F', '0', '0', 'news:comment:query', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 231);
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 232, '评论删除', 230, 2, NULL, NULL, NULL, 'F', '0', '0', 'news:comment:remove', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 232);

-- 资讯管理子菜单：互动数据（点赞/收藏）
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 240, '互动管理', 4, 4, '/news/interaction', 'news/interaction', NULL, 'C', '0', '0', 'news:interaction:list', 'data-analysis', '资讯互动管理菜单', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 240);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `menu_type`, `visible`, `status`, `perms`, `icon`, `remark`, `create_by`)
SELECT 241, '互动查询', 240, 1, NULL, NULL, NULL, 'F', '0', '0', 'news:interaction:query', '#', NULL, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 241);

-- 角色授权：超级管理员默认授予资讯管理全部菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 4 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 4);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 210 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 210);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 211 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 211);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 212 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 212);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 213 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 213);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 214 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 214);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 215 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 215);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 220 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 220);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 221 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 221);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 222 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 222);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 223 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 223);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 224 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 224);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 230 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 230);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 231 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 231);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 232 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 232);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 240 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 240);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, 241 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 241);
