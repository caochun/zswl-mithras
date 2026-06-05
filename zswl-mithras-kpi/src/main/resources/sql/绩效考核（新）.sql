INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`) VALUE('job', '岗位类型', 'kpimanagement', '绩效考核岗', 10);




-- 菜单调整（需根据生产环境实际id修改）
-- 财务管理
INSERT INTO `bifrost_custom_tree` (`code`, `name`, `flag`, `sort_no`, `parent_id`, `create_by`, `update_by`, `en_name`, `icon`, `path`, `lang_env`)
VALUES
	('budget', '财务管理', 1, 91, NULL, NULL, NULL, NULL, 'icon-caiwuguanli', NULL, NULL);

-- 我方账户
update `bifrost_menu` set `path` = '/budget/bankAccount' where `id` = 21;
update `bifrost_custom_tree_menu_ref` set `custom_tree_id` = 148 where `menu_id` = 21;
update `bifrost_org_menu_function` set `custom_tree_id` = 148 where `menu_id` = 21;
update `bifrost_role_menu_function` set `custom_tree_id` = 148 where `menu_id` = 21;

-- LPR设置
update `bifrost_menu` set `path` = '/budget/lpr' where `id` = 22;
update `bifrost_custom_tree_menu_ref` set `custom_tree_id` = 148 where `menu_id` = 22;
update `bifrost_org_menu_function` set `custom_tree_id` = 148 where `menu_id` = 22;
update `bifrost_role_menu_function` set `custom_tree_id` = 148 where `menu_id` = 22;

-- 财务报表
update `bifrost_menu` set `path` = '/budget/financeSheet' where `id` = 458;
update `bifrost_custom_tree_menu_ref` set `custom_tree_id` = 148 where `menu_id` = 458;
update `bifrost_org_menu_function` set `custom_tree_id` = 148 where `menu_id` = 458;
update `bifrost_role_menu_function` set `custom_tree_id` = 148 where `menu_id` = 458;

-- 项目利润和拨备计提
INSERT INTO `bifrost_menu` (`code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`, `en_name`, `target`)
VALUES
	('budgetprojProfit', 3, 0, NULL, '/budget/projProfit', NULL, NULL, '项目利润', NULL, NULL),
	('budgetprovisioning', 3, 0, NULL, '/budget/provisioning', NULL, NULL, '拨备计提', NULL, NULL);

-- FTP定价管理
update `bifrost_custom_tree` set `parent_id` = 148 where id = 88;