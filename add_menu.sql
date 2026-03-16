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