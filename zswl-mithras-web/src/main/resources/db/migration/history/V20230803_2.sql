ALTER TABLE policy_info ADD contract_code varchar(50) NULL COMMENT '合同code';
ALTER TABLE policy_info ADD renew_insurance_result INT(11) NULL COMMENT '续保结果';
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`, `group_id` )
VALUES
('paymentpolicyimport', '导入保单信息', 0, 15, 'POST', '/payment/policy/import', 2, 75 );
