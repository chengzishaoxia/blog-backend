-- 实时连接 / 聊天室连接 最终版菜单与权限脚本
-- 适用库：klee-blog / blog-backend
-- 说明：
-- 1. 管理端连接页展示全部连接，不提供操作
-- 2. 聊天室连接页仅展示聊天室连接，提供封禁/解禁
-- 3. 本脚本可重复执行，兼容旧版“实时连接”菜单结构升级

-- 先清理旧版无用的“断开连接”权限节点
DELETE FROM `sys_role_menu` WHERE `menu_id` = 187;
DELETE FROM `sys_menu` WHERE `id` = 187;

-- 管理端连接页：展示全部连接
INSERT INTO `sys_menu` (`id`,`parent_id`,`tree_path`,`name`,`type`,`route_name`,`route_path`,`component`,`perm`,`always_show`,`keep_alive`,`visible`,`sort`,`icon`,`redirect`,`is_deleted`,`create_time`,`update_time`,`params`)
SELECT 185,1,'0,1','管理端连接',1,'AdminRealtimeManagement','realtime-admin','system/realtime/index',NULL,0,1,1,10,'monitor',NULL,0,NOW(),NOW(),NULL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 185);

UPDATE `sys_menu`
SET `parent_id` = 1,
    `tree_path` = '0,1',
    `name` = '管理端连接',
    `type` = 1,
    `route_name` = 'AdminRealtimeManagement',
    `route_path` = 'realtime-admin',
    `component` = 'system/realtime/index',
    `perm` = NULL,
    `always_show` = 0,
    `keep_alive` = 1,
    `visible` = 1,
    `sort` = 10,
    `icon` = 'monitor',
    `redirect` = NULL,
    `is_deleted` = 0,
    `update_time` = NOW()
WHERE `id` = 185;

-- 聊天室连接页：仅展示聊天室连接，并提供封禁/解禁
INSERT INTO `sys_menu` (`id`,`parent_id`,`tree_path`,`name`,`type`,`route_name`,`route_path`,`component`,`perm`,`always_show`,`keep_alive`,`visible`,`sort`,`icon`,`redirect`,`is_deleted`,`create_time`,`update_time`,`params`)
SELECT 190,1,'0,1','聊天室连接',1,'ChatRealtimeManagement','realtime-chat','system/realtime/chat/index',NULL,0,1,1,11,'chat-dot-round',NULL,0,NOW(),NOW(),NULL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 190);

UPDATE `sys_menu`
SET `parent_id` = 1,
    `tree_path` = '0,1',
    `name` = '聊天室连接',
    `type` = 1,
    `route_name` = 'ChatRealtimeManagement',
    `route_path` = 'realtime-chat',
    `component` = 'system/realtime/chat/index',
    `perm` = NULL,
    `always_show` = 0,
    `keep_alive` = 1,
    `visible` = 1,
    `sort` = 11,
    `icon` = 'chat-dot-round',
    `redirect` = NULL,
    `is_deleted` = 0,
    `update_time` = NOW()
WHERE `id` = 190;

-- 查询权限：两个页面共用
INSERT INTO `sys_menu` (`id`,`parent_id`,`tree_path`,`name`,`type`,`route_name`,`route_path`,`component`,`perm`,`always_show`,`keep_alive`,`visible`,`sort`,`icon`,`redirect`,`is_deleted`,`create_time`,`update_time`,`params`)
SELECT 186,185,'0,1,185','实时连接查询',4,NULL,'',NULL,'sys:ws:query',0,0,1,1,'',NULL,0,NOW(),NOW(),NULL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 186);

UPDATE `sys_menu`
SET `parent_id` = 185,
    `tree_path` = '0,1,185',
    `name` = '实时连接查询',
    `type` = 4,
    `route_name` = NULL,
    `route_path` = '',
    `component` = NULL,
    `perm` = 'sys:ws:query',
    `always_show` = 0,
    `keep_alive` = 0,
    `visible` = 1,
    `sort` = 1,
    `icon` = '',
    `redirect` = NULL,
    `is_deleted` = 0,
    `update_time` = NOW()
WHERE `id` = 186;

-- 聊天室封禁权限
INSERT INTO `sys_menu` (`id`,`parent_id`,`tree_path`,`name`,`type`,`route_name`,`route_path`,`component`,`perm`,`always_show`,`keep_alive`,`visible`,`sort`,`icon`,`redirect`,`is_deleted`,`create_time`,`update_time`,`params`)
SELECT 188,190,'0,1,190','聊天室封禁',4,NULL,'',NULL,'sys:chat:ban',0,0,1,2,'',NULL,0,NOW(),NOW(),NULL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 188);

UPDATE `sys_menu`
SET `parent_id` = 190,
    `tree_path` = '0,1,190',
    `name` = '聊天室封禁',
    `type` = 4,
    `route_name` = NULL,
    `route_path` = '',
    `component` = NULL,
    `perm` = 'sys:chat:ban',
    `always_show` = 0,
    `keep_alive` = 0,
    `visible` = 1,
    `sort` = 2,
    `icon` = '',
    `redirect` = NULL,
    `is_deleted` = 0,
    `update_time` = NOW()
WHERE `id` = 188;

-- 聊天室发言权限（业务权限节点）
INSERT INTO `sys_menu` (`id`,`parent_id`,`tree_path`,`name`,`type`,`route_name`,`route_path`,`component`,`perm`,`always_show`,`keep_alive`,`visible`,`sort`,`icon`,`redirect`,`is_deleted`,`create_time`,`update_time`,`params`)
SELECT 189,190,'0,1,190','聊天室发言',4,NULL,'',NULL,'chat:send',0,0,1,3,'',NULL,0,NOW(),NOW(),NULL
WHERE NOT EXISTS (SELECT 1 FROM `sys_menu` WHERE `id` = 189);

UPDATE `sys_menu`
SET `parent_id` = 190,
    `tree_path` = '0,1,190',
    `name` = '聊天室发言',
    `type` = 4,
    `route_name` = NULL,
    `route_path` = '',
    `component` = NULL,
    `perm` = 'chat:send',
    `always_show` = 0,
    `keep_alive` = 0,
    `visible` = 1,
    `sort` = 3,
    `icon` = '',
    `redirect` = NULL,
    `is_deleted` = 0,
    `update_time` = NOW()
WHERE `id` = 189;

-- 角色菜单授权
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT 1,185 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 185);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT 1,190 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 190);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT 1,186 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 186);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT 1,188 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 188);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT 1,189 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 1 AND `menu_id` = 189);

INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT 2,185 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 2 AND `menu_id` = 185);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT 2,190 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 2 AND `menu_id` = 190);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT 2,186 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 2 AND `menu_id` = 186);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT 2,188 WHERE NOT EXISTS (SELECT 1 FROM `sys_role_menu` WHERE `role_id` = 2 AND `menu_id` = 188);
