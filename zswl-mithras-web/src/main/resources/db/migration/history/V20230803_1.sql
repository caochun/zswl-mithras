INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('contractrentactualirrcalculate', '实际IRR测算', 0, 20, NULL, 'POST', '/contract/rent/actual/irr/calculate', 1, NULL);

ALTER TABLE policy_info ADD contract_id BIGINT(20) NULL COMMENT '合同id';
ALTER TABLE policy_info ADD payment_id BIGINT(20) NULL COMMENT '付款ID';
ALTER TABLE policy_info ADD payment_policy_id BIGINT(20) NULL COMMENT '付款保单ID';
ALTER TABLE policy_info ADD policy_type varchar(50) NULL COMMENT '保单种类';
ALTER TABLE policy_info ADD renew_insurance_flag varchar(50) NULL COMMENT '是否续保';
ALTER TABLE policy_info ADD remark varchar(500) NULL COMMENT '备注';
ALTER TABLE policy_info ADD parent_id BIGINT(20) NULL COMMENT '父id';
ALTER TABLE policy_info ADD level INT(11) NULL COMMENT '层级';
ALTER TABLE policy_info ADD policy_status varchar(50) NULL COMMENT '保单状态';
ALTER TABLE policy_info ADD notice_flag INT(11) NULL COMMENT '通知标识 0未通知， 1，已通知';

ALTER TABLE payment_policy_info ADD policy_type varchar(50) NULL COMMENT '保单种类';
ALTER TABLE payment_policy_info ADD renew_insurance_flag varchar(50) NULL COMMENT '是否续保';
ALTER TABLE payment_policy_info ADD remark varchar(500) NULL COMMENT '备注';

ALTER TABLE payment_policy_info_lib ADD policy_type varchar(50) NULL COMMENT '保单种类';
ALTER TABLE payment_policy_info_lib ADD renew_insurance_flag varchar(50) NULL COMMENT '是否续保';
ALTER TABLE payment_policy_info_lib ADD remark varchar(500) NULL COMMENT '备注';

INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`, `group_id` )
VALUES
( 'policyImport', '保单导入', 0, 491, 'POST', '/policy/import', 2, 144 ),
( 'policyClientList', '客户信息', 0, 491, 'POST', '/client/list', 1, 144 ),
( 'policyLedgerContractPolicyExport', '保单台账-合同保单信息导出', 0, 491, 'POST', '/policy/ledger/contract/policy/export', 2, 144 ),
( 'policyLedgerContractPolicy', '保单台账-合同保单信息', 0, 491, 'POST', '/policy/ledger/contract/policy', 2, 144 ),
( 'policyLedgerContractDetail', '保单台账-合同信息', 0, 491, 'POST', '/policy/ledger/contract/detail', 1, 144 );


