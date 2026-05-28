-- ocr
ALTER TABLE lease_item_vehicle_registration_certificate ADD registration_page_no VARCHAR(50) COMMENT '机动车登记证书编号';
ALTER TABLE lease_item_vehicle_registration_certificate ADD file_name VARCHAR(100) COMMENT '文件名称';
ALTER TABLE lease_item_vehicle_registration_certificate ADD change_record JSON DEFAULT NULL COMMENT '变更记录';
ALTER TABLE lease_item_vehicle_registration_certificate ADD file_id BIGINT(20) COMMENT '文件id';
ALTER TABLE lease_item_vehicle_registration_certificate ADD is_present_home_page TINYINT(4) DEFAULT 0 COMMENT '是否是首页';
-- ocr数据订正
UPDATE lease_item_vehicle_registration_certificate AS rc INNER JOIN materials_list AS m ON rc.id = m.belong_id SET rc.file_id = m.id, rc.file_name = m.filename WHERE rc.deleted = 0;
UPDATE lease_item_vehicle_registration_certificate SET is_present_home_page = 1 WHERE (change_record IS NULL OR change_record = '') AND deleted = 0;

-- 统一工作台，计划执行情况
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-07-09 20:09:09', '2024-07-09 20:09:09', null, 'dashboardPlanList', '项目视图-计划执行情况', 0, (select id from bifrost_menu where `code` = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/plan/list', 1, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-07-09 20:09:27', '2024-07-09 20:09:27', null, 'dashboardPlanStatistics', '项目视图-计划执行情况-按公司统计', 0, (select id from bifrost_menu where `code` = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/plan/statistics', 1, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-07-09 20:09:50', '2024-07-09 20:09:50', null, 'dashboardPlanStatisticsBydept', '项目视图-计划执行情况-按部门统计', 0, (select id from bifrost_menu where `code` = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/plan/statistics/bydept', 1, NULL);

CREATE TABLE `contract_org_plan` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `year` int(10) NOT NULL,
    `month` int(10) NOT NULL,
    `dept_id` bigint(20) NOT NULL COMMENT '部门id',
    `dept_name` varchar(255) NOT NULL COMMENT '部门名称',
    `plan_amount` bigint(20) NOT NULL COMMENT '计划投放金额',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` bigint(20) DEFAULT NULL,
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;

-- 导入七月份部门计划投放金额
INSERT INTO `contract_org_plan` (`id`, `year`, `month`, `dept_id`, `dept_name`, `plan_amount`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`)
VALUES (NULL, 2024, 7, (select id from bifrost_org where name = '浙江业务部'), '浙江业务部', 260700*100000000, '2024-07-09 15:41:19', NULL, '2024-07-09 15:41:19', NULL, 0);
INSERT INTO `contract_org_plan` (`id`, `year`, `month`, `dept_id`, `dept_name`, `plan_amount`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`)
VALUES (NULL, 2024, 7, (select id from bifrost_org where name = '公用事业业务部'), '公用事业部', 40000*100000000, '2024-07-09 15:41:19', NULL, '2024-07-09 15:41:19', NULL, 0);
INSERT INTO `contract_org_plan` (`id`, `year`, `month`, `dept_id`, `dept_name`, `plan_amount`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`)
VALUES (NULL, 2024, 7, (select id from bifrost_org where name = '高端装备业务部'), '高端装备业务部', 0*100000000, '2024-07-09 15:41:19', NULL, '2024-07-09 15:41:19', NULL, 0);
INSERT INTO `contract_org_plan` (`id`, `year`, `month`, `dept_id`, `dept_name`, `plan_amount`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`)
VALUES (NULL, 2024, 7, (select id from bifrost_org where name = '交通物流业务部'), '交通物流业务部', 7750*100000000, '2024-07-09 15:41:19', NULL, '2024-07-09 15:41:19', NULL, 0);
INSERT INTO `contract_org_plan` (`id`, `year`, `month`, `dept_id`, `dept_name`, `plan_amount`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`)
VALUES (NULL, 2024, 7, (select id from bifrost_org where name = '智能制造业务部'), '智能制造业务部', 7000*100000000, '2024-07-09 15:41:19', NULL, '2024-07-09 15:41:19', NULL, 0);
INSERT INTO `contract_org_plan` (`id`, `year`, `month`, `dept_id`, `dept_name`, `plan_amount`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`)
VALUES (NULL, 2024, 7, (select id from bifrost_org where name = '航运业务部'), '航运业务部', 0*100000000, '2024-07-09 15:41:19', NULL, '2024-07-09 15:41:19', NULL, 0);
INSERT INTO `contract_org_plan` (`id`, `year`, `month`, `dept_id`, `dept_name`, `plan_amount`, `create_time`, `create_by`, `update_time`, `update_by`, `deleted`)
VALUES (NULL, 2024, 7, (select id from bifrost_org where name = '工程建设业务部'), '工程建设业务部', 13000*100000000, '2024-07-09 15:41:19', NULL, '2024-07-09 15:41:19', NULL, 0);

-- 流水中心-业务流水资金端-变更状态并推送单据
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('businessflowfinancemanualpush', '业务流水-资金端-变更状态并推送单据', 0, 495, NULL, NULL, NULL, 'POST', '/business/flow/finance/manual/push', 2, NULL);

-- 大熊的SQL
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('bankCenterFinancePaymentWriteOff', '资金端付款核销-核销流水', 495, 'POST',
        '/bank/center/finance/payment/writeoff', 2),
       ('bankCenterFinancePaymentCashflowList', '资金端付款核销-现金流列表', 495, 'POST',
        '/bank/center/finance/payment/cashflow/list', 1);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id` )
VALUES
('dashboardFileExport', '统一工作台导出文件', 0, 730, NULL, NULL, NULL, 'POST', '/file/export', 2, NULL );