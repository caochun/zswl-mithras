-- 项目跟踪
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num` )
VALUES ('项目跟踪汇总表', 'http://10.158.11.178/page/qcb7417365e1b47f6a714242', 'yunying', -15 );

-- 项目利润新增字段
alter table `finance_project_profit_detail` add column `total_gross_profit_this_year` bigint(20) DEFAULT NULL COMMENT '本年累计毛利';
alter table `finance_project_profit_detail` add column `gross_profit_this_month` bigint(20) DEFAULT NULL COMMENT '本月毛利';
alter table `finance_project_profit_detail` add column `revenue_this_month` bigint(20) DEFAULT NULL COMMENT '本月收入';
alter table `finance_project_profit_detail` add column `assess_dept_id` bigint(20) DEFAULT NULL COMMENT '考核部门id';
alter table `finance_project_profit_detail` add column `total_profit_this_year_before` bigint(20) DEFAULT NULL COMMENT '本年累计利润总额扣费前';
alter table `finance_project_profit_detail` add column `risk_balance_begin_year` bigint(20) DEFAULT NULL COMMENT '年初风险金余额';
alter table `finance_project_profit_detail` add column `total_risk_balance_this_year` bigint(20) DEFAULT NULL COMMENT '计风险金计提/冲抵';
-- FTP计息增加字段
alter table ftp_interest_base_info add column last_cash_ftp int(10) DEFAULT NULL COMMENT '最近一次现金FTP价格';

-- 统一工作台
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('dashboardfinancestatisticsList', '融资视图-分组统计', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/finance/statisticsList', 1, NULL),
	('dashboardfinancecreditinfolist', '融资视图-授信情况', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/finance/creditinfo/list', 1, NULL),
	('dashboardfinancerepaylist', '融资视图-还本付息', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/finance/repay/list', 1, NULL),
	('dashboardfinancebalancelist', '融资视图-融资余额', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/finance/balance/list', 1, NULL),
	('dashboardfinanceloaninfolist', '融资视图-融资情况', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/finance/loaninfo/list', 1, NULL),
	('dashboardpaystatisticsbydept', '项目视图-投放情况-按部门统计', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/pay/statistics/bydept', 1, NULL),
	('dashboardpaystatistics', '项目视图-投放情况-按公司统计', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/pay/statistics', 1, NULL),
	('dashboardpaylist', '项目视图-投放情况', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/pay/list', 1, NULL),
	('dashboardprojectinfostatisticsRentInfoList', '项目视图-项目情况-统计', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/project/info/statisticsRentInfoList', 1, NULL),
	('dashboardprojectinfoprovisionlist', '项目视图-项目情况-剩余本金与拨备', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/project/info/provision/list', 1, NULL),
	('dashboardprojectinfopaynosettlelist', '项目视图-项目情况-已投放未结清项目', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/project/info/paynosettle/list', 1, NULL);
-- 资产管理策略新增字段
alter table `common_process_prepare` add column `is_asset_confirm` varchar(20) DEFAULT NULL COMMENT '资产管理岗是否确认';

-- 增加接口
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('riskcontrolopinionmonitorunresolved-dashboard', '我的未处理舆情列表-统一工作台', 0, 730, NULL, NULL, NULL, 'POST', '/risk/control/opinion/monitor/unresolved', 1, null);
