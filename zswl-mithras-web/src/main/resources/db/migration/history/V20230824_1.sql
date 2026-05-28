/*指标明细*/
alter table financial_cloud_metric_value
    add contract_detail text charset utf8mb4 null comment '参与计算合同信息';

alter table risk_control_strategy
    add client_detail text charset utf8mb4 null comment '参与计算客户信息';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
VALUES ('financialcloudmetricdetail', '金融云指标详情', 0, 480, 'POST', '/financial/cloud/metric/detail', 1);


/**/
alter table risk_control_concentration_client
    add remaining_margin bigint null comment '剩余保证金' after remaining_principal;
alter table risk_control_concentration_group
    add remaining_margin bigint null comment '剩余保证金' after remaining_principal;

alter table risk_control_concentration_client
    add client_sponsor_id bigint null comment '客户主办id' after assert_classify_result;

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('kpiProjectDistributionExport', '项目分配表导出', 0, 288, NULL, 'POST', '/kpi/projectdistribution/export', 2, NULL);

alter table `payment_actual_detail` add key `idx_payment_id` (`payment_id`);


