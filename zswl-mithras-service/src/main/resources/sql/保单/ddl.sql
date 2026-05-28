ALTER TABLE policy_info ADD contract_id BIGINT(20) NULL COMMENT '合同id';
ALTER TABLE policy_info ADD payment_id BIGINT(20) NULL COMMENT '付款ID';
ALTER TABLE policy_info ADD payment_policy_id BIGINT(20) NULL COMMENT '付款保单ID';
ALTER TABLE policy_info ADD policy_type varchar(50) NULL COMMENT '保单种类';
ALTER TABLE policy_info ADD renew_insurance_flag varchar(50) NULL COMMENT '是否续保';
ALTER TABLE policy_info ADD remark varchar(500) NULL COMMENT '备注';
ALTER TABLE policy_info ADD parent_id BIGINT(20) NULL COMMENT '父id';
ALTER TABLE policy_info ADD level INT(11) NULL COMMENT '层级';

ALTER TABLE payment_policy_info ADD policy_type varchar(50) NULL COMMENT '保单种类';
ALTER TABLE payment_policy_info ADD renew_insurance_flag varchar(50) NULL COMMENT '是否续保';
ALTER TABLE payment_policy_info ADD remark varchar(500) NULL COMMENT '备注';

ALTER TABLE payment_policy_info_lib ADD policy_type varchar(50) NULL COMMENT '保单种类';
ALTER TABLE payment_policy_info_lib ADD renew_insurance_flag varchar(50) NULL COMMENT '是否续保';
ALTER TABLE payment_policy_info_lib ADD remark varchar(500) NULL COMMENT '备注';

ALTER TABLE payment_base_info ADD financial_status TINYINT(1) NULL COMMENT '撤回标识，0正常，1撤回';
ALTER TABLE payment_base_info_lib ADD financial_status TINYINT(1) NULL COMMENT '撤回标识，0正常，1撤回';


ALTER TABLE policy_info ADD expiration_reminder_flag TINYINT(1) default 0 COMMENT '保单到期标识 0， 1，已通知';
ALTER TABLE policy_info_lib ADD expiration_reminder_flag TINYINT(1) default 0 COMMENT '保单到期标识 0， 1，已通知';

ALTER TABLE policy_info ADD renewal_overdue_flag TINYINT(1) default 0 COMMENT '续保逾期标识 0未通知， 1，已通知';
ALTER TABLE policy_info_lib ADD renewal_overdue_flag TINYINT(1) default 0 COMMENT '续保逾期标识 0未通知， 1，已通知';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('policyLedgerRenewInsurance', '保单台账-保单续保信息', 0, (select id from bifrost_menu where code = 'afterLeasepolicyManage'), null, null, null, 'POST',
        '/policy/ledger/renew/insurance', 2, null);


ALTER TABLE policy_info_lib ADD contract_id BIGINT(20) NULL COMMENT '合同id';
ALTER TABLE policy_info_lib ADD payment_id BIGINT(20) NULL COMMENT '付款ID';
ALTER TABLE policy_info_lib ADD payment_policy_id BIGINT(20) NULL COMMENT '付款保单ID';
ALTER TABLE policy_info_lib ADD policy_type varchar(50) NULL COMMENT '保单种类';
ALTER TABLE policy_info_lib ADD renew_insurance_flag varchar(50) NULL COMMENT '是否续保';
ALTER TABLE policy_info_lib ADD remark varchar(500) NULL COMMENT '备注';
ALTER TABLE policy_info_lib ADD parent_id BIGINT(20) NULL COMMENT '父id';
ALTER TABLE policy_info_lib ADD level INT(11) NULL COMMENT '层级';
ALTER TABLE policy_info_lib ADD notice_flag INT(11) NULL COMMENT '通知标识 0未通知， 1，已通知';



