-- 月结管理
ALTER TABLE `contract_income_sharing` ADD COLUMN `confirm_time` DATETIME COMMENT '确认时间';
ALTER TABLE `contract_income_sharing` ADD COLUMN `confirm_batch` VARCHAR(100) COMMENT '确认批次号';
ALTER TABLE `monthly_stamp_duty` ADD COLUMN `confirm_time` DATETIME COMMENT '确认时间';
ALTER TABLE `monthly_stamp_duty` ADD COLUMN `confirm_batch` VARCHAR(100) COMMENT '确认批次号';
ALTER TABLE `funds_daily_cost` ADD COLUMN `confirm_time` DATETIME COMMENT '确认时间';
ALTER TABLE `funds_daily_cost` ADD COLUMN `confirm_batch` VARCHAR(100) COMMENT '确认批次号';

ALTER TABLE `asset_classify_client` ADD COLUMN `asset_balance` bigint(20) DEFAULT NULL COMMENT '资产余额（亿元）';
ALTER TABLE `asset_classify_client_auxiliary_lib` ADD COLUMN `asset_balance` bigint(20) DEFAULT NULL COMMENT '资产余额（亿元）';
ALTER TABLE `asset_classify_client_lib` ADD COLUMN `asset_balance` bigint(20) DEFAULT NULL COMMENT '资产余额（亿元）';

-- 财资-【资金管理-机构管理】
ALTER TABLE `fund_organization` ADD COLUMN `institution_code` VARCHAR(50) DEFAULT NULL COMMENT '机构代码';
-- 租金往来方
ALTER TABLE `contract_tenantry` ADD COLUMN `rent_concat_account_name` VARCHAR(100) DEFAULT NULL COMMENT '租金往来方';
ALTER TABLE `contract_tenantry` ADD COLUMN `rent_concat_account_id` VARCHAR(20) DEFAULT NULL COMMENT '租金往来方id';
ALTER TABLE `contract_tenantry_lib` ADD COLUMN `rent_concat_account_name` VARCHAR(100) DEFAULT NULL COMMENT '租金往来方';
ALTER TABLE `contract_tenantry_lib` ADD COLUMN `rent_concat_account_id` VARCHAR(20) DEFAULT NULL COMMENT '租金往来方id';

-- 收入分摊表
INSERT INTO `bifrost_menu` (code,level,path,name) VALUES ('budgetIncomeShareTable',3,'/budget/incomeShareTable','收入分摊表');
INSERT INTO `bifrost_custom_tree_menu_ref` (custom_tree_id,menu_id) VALUES (139,(SELECT id FROM `bifrost_menu` WHERE code = 'budgetIncomeShareTable'));

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`,`method`, `path`, `type`)
VALUES ('incomeSharingList', '列表信息', 0, (SELECT id FROM `bifrost_menu` WHERE code = 'budgetIncomeShareTable'), 'POST', '/incomeSharing/list', 1);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
VALUES ('incomeSharingDetail', '收入分摊详细列表', 0, (SELECT id FROM `bifrost_menu` WHERE code = 'budgetIncomeShareTable'), 'POST', '/incomeSharing/detail', 1);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`,`method`, `path`, `type`)
VALUES ('incomeSharingListDownload', '列表下载', 0, (SELECT id FROM `bifrost_menu` WHERE code = 'budgetIncomeShareTable'), 'POST', '/incomeSharing/list/download', 1);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
VALUES ('incomeSharingDetailDownload', '详情下载', 0, (SELECT id FROM `bifrost_menu` WHERE code = 'budgetIncomeShareTable'), 'POST', '/incomeSharing/detail/download', 2);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
VALUES ('clientListIncomeShareTable', '客户列表', 0, (SELECT id FROM `bifrost_menu` WHERE code = 'budgetIncomeShareTable'), 'POST', '/client/list', 1);

-- 李朝
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`)
VALUES ('highseascustomerlist', '获取公海客户列表', 0, 20, NULL, NULL, NULL, 'POST', '/high/seas/customers/list', 2);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`)
VALUES ('fundinstitutionCode', '获取机构代码', 0, 168, NULL, NULL, NULL, 'POST', '/fund/institutionCode', 2);

CREATE TABLE `fund_receipt_flow_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `data_source` varchar(100) NOT NULL DEFAULT '手工核销',
  `receipt_repay_id` bigint(20) DEFAULT NULL COMMENT '收付款id',
  `settle_method` varchar(30) NOT NULL DEFAULT '' COMMENT '结算方式',
  `cash_flow_code` varchar(50) NOT NULL COMMENT '现金流编号',
  `cash_flow_item` varchar(50) NOT NULL COMMENT '现金流类型',
  `cash_flow_date` date NOT NULL COMMENT '核销日期',
  `total_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '总金额',
  `principal_amount` bigint(20) DEFAULT NULL COMMENT '本金金额',
  `interest_amount` bigint(20) DEFAULT NULL COMMENT '利息金额',
  `our_account_number` varchar(50) DEFAULT NULL COMMENT '我方账户号',
  `bank_flow_no` varchar(50) DEFAULT NULL COMMENT '银行流水号',
  `finance_flow_id` bigint(20) DEFAULT NULL COMMENT '流水记录ID',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `our_account_name` varchar(128) DEFAULT NULL COMMENT '我方银行账户名',
  `our_account_bank` varchar(128) DEFAULT NULL COMMENT '我方银行开户行',
  PRIMARY KEY (`id`),
  KEY `idx_receiptrepayid_deleted` (`receipt_repay_id`,`deleted`),
  KEY `idx_bankflowno_deleted` (`bank_flow_no`,`deleted`),
  KEY `idx_cashflowcode_deleted` (`cash_flow_code`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金端核销明细';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ('bankcenterfinancesublist', '银行流水-资金端-手工核销-现金流子列表', 0, 495, 'POST',
        '/bank/center/finance/sub/list', 1),
       ('bankcenterfinancecashflowlist', '银行流水-资金端-手工核销-现金流信息', 0, 495, 'POST',
        '/bank/center/finance/cashflow/list', 1),
       ('bankcenterfinanceinfolist', '银行流水-资金端-手工核销-融资列表', 0, 495, 'POST',
        '/bank/center/finance/info/list', 1),
       ('bankcenterfinanceorglist', '银行流水-资金端-手工核销-机构/产品列表', 0, 495, 'POST',
        '/bank/center/finance/org/list', 1);