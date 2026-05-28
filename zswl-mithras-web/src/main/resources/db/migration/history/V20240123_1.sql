ALTER TABLE contract_receipt ADD COLUMN tax BIGINT(20) COMMENT '税额(元)';

ALTER TABLE contract_receipt ADD COLUMN rent_excluding_tax BIGINT(20) COMMENT '不含税租金（元）';

ALTER TABLE contract_receipt_lib ADD COLUMN tax BIGINT(20) COMMENT '税额(元)';

ALTER TABLE contract_receipt_lib ADD COLUMN rent_excluding_tax BIGINT(20) COMMENT '不含税租金（元）';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('contractrreceiptupdateActualTax', '更新税额和不含税额', 0, 20, NULL, NULL, NULL, 'POST', '/contract/receipt/updateActualTax', 1, NULL),
	('contractreceiptqueryActualTax', '计算税额和不含税额', 0, 20, NULL, NULL, NULL, 'POST', '/contract/receipt/queryActualTax', 1, NULL);

-- 增加运营复核节点
update `general_dictionary` set `display` = '运营管理（经办）' where `code` = 'yunYingGuanLi';
update `general_dictionary` set `display` = '运营经理（经办）' where `code` = 'operationManagement';
INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`)
VALUES
	('job', '岗位类型', 'operationManagementReview', '运营经理（复核）', 10),
	('job', '岗位类型', 'yunYingGuanLiReview', '运营管理（复核）', 10);

-- 批量一键通过
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('flowexecutionbatchpass', '批量审批通过', 0, 10, NULL, NULL, NULL, 'POST', '/flow/execution/batch/pass', 2, NULL);
