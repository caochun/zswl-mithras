INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`, `group_id` )
VALUES
('maintenancePolicyProjExport', '待维护保单导出', 0, 478, 'GET', '/maintenance/policy/proj/export', 2, 113);

-- FTP计息逾期资金占用
alter table `ftp_interest_detail_record` add column `total_overdue` bigint(20) not null default 0 comment '累计逾期金额';
alter table `ftp_interest_detail_record` add column `financing_repay` bigint(20) not null default 0 comment '融资实际还款金额';
alter table `ftp_interest_detail_record` add column `own_occupy_balance` bigint(20) not null default 0 comment '自有资金占用余额';

-- 付款关闭
alter table `payment_base_info` add column `financial_status` tinyint(1) not null default 0 comment '撤回标识，0正常，1撤回';
alter table `payment_base_info_lib` add column `financial_status` tinyint(1) not null default 0 comment '撤回标识，0正常，1撤回';