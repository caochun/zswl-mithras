-- 绩效考核-项目分配表
alter table `kpi_project_distribution_base_info` add column `team_leader_id` bigint(20) default null comment '团队长用户id' after `profit_belong_dept_id`;
alter table `kpi_project_distribution_base_info_lib` add column `team_leader_id` bigint(20) default null comment '团队长用户id' after `profit_belong_dept_id`;
alter table `kpi_project_distribution_base_info` add column `remark` text default null comment '项目交接备注' after `effect_month`;
alter table `kpi_project_distribution_base_info_lib` add column `remark` text default null comment '项目交接备注' after `effect_month`;

INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`)
VALUES
	('job', '岗位类型', 'teamleader', '团队长', 10);

-- 付款申请增加中登网查重日期
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('paymentleaseitemcheckrepeatget', '根据付款申请找到对应的租赁物审核管理的中登网查重日日期', 0, 15, NULL, NULL, NULL, 'POST', '/payment/leaseitem/checkrepeat/get', 1, 12);
