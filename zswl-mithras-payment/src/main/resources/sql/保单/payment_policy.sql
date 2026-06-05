ALTER TABLE payment_policy_info ADD policy_type varchar(50) NULL COMMENT '保单种类';
ALTER TABLE payment_policy_info ADD renew_insurance_flag varchar(50) NULL COMMENT '是否续保';
ALTER TABLE payment_policy_info ADD remark varchar(500) NULL COMMENT '备注';

ALTER TABLE payment_policy_info_lib ADD policy_type varchar(50) NULL COMMENT '保单种类';
ALTER TABLE payment_policy_info_lib ADD renew_insurance_flag varchar(50) NULL COMMENT '是否续保';
ALTER TABLE payment_policy_info_lib ADD remark varchar(500) NULL COMMENT '备注';

ALTER TABLE payment_base_info ADD financial_status TINYINT(1) NULL COMMENT '撤回标识，0正常，1撤回';
ALTER TABLE payment_base_info_lib ADD financial_status TINYINT(1) NULL COMMENT '撤回标识，0正常，1撤回';
