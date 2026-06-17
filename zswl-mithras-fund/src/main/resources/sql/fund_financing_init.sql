-- Create syntax for TABLE 'fund_financing_base_info'
CREATE TABLE `fund_financing_base_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `credit_id` bigint(20) NOT NULL COMMENT '授信id',
  `credit_code` varchar(50) NOT NULL COMMENT '授信编号',
  `sequence` tinyint(4) NOT NULL COMMENT '序列号',
  `financing_code` varchar(50) NOT NULL COMMENT '融资编号',
  `organization_id` bigint(20) NOT NULL COMMENT '融资机构id',
  `organization_name` varchar(50) NOT NULL DEFAULT '' COMMENT '融资机构名称',
  `total_credit_limit` bigint(20) NOT NULL COMMENT '总授信额度',
  `remaining_credit_limit` bigint(20) DEFAULT NULL COMMENT '剩余授信额度',
  `time_limit_type` varchar(50) NOT NULL DEFAULT '' COMMENT '期限类型',
  `business_type` varchar(50) NOT NULL DEFAULT '' COMMENT '业务类型',
  `guarantee_info` json DEFAULT NULL COMMENT '担保信息',
  `funds_purpose` varchar(200) NOT NULL DEFAULT '' COMMENT '资金用途',
  `remark` text COMMENT '备注',
  `fund_manager_id` bigint(20) NOT NULL COMMENT '资金经理id',
  `dept_id` bigint(20) NOT NULL COMMENT '所属部门id',
  `biz_header_id` bigint(20) unsigned NOT NULL COMMENT '部门负责人id',
  `leader_id` bigint(20) NOT NULL COMMENT '分管领导id',
  `financing_status` varchar(20) NOT NULL DEFAULT '' COMMENT '融资状态',
  `approval_status` varchar(30) NOT NULL DEFAULT '' COMMENT '审批状态',
  `change_sub_type` varchar(20) DEFAULT NULL COMMENT '审批流子类型',
  `plan_loan_date` date DEFAULT NULL COMMENT '计划贷款日期',
  `actual_loan_date` date DEFAULT NULL COMMENT '实际贷款日期',
  `actual_expire_date` date DEFAULT NULL COMMENT '实际到期日期',
  `financing_amount` bigint(20) NOT NULL COMMENT '融资金额',
  `has_pledge_info` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有质押',
  `repay_day` tinyint(4) unsigned DEFAULT NULL COMMENT '还款日',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_credit_code` (`credit_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-基本信息';

-- Create syntax for TABLE 'fund_financing_base_info_lib'
CREATE TABLE `fund_financing_base_info_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `credit_id` bigint(20) NOT NULL COMMENT '授信id',
  `credit_code` varchar(50) NOT NULL COMMENT '授信编号',
  `sequence` tinyint(4) NOT NULL COMMENT '序列号',
  `financing_code` varchar(50) NOT NULL COMMENT '融资编号',
  `organization_id` bigint(20) NOT NULL COMMENT '融资机构id',
  `organization_name` varchar(50) NOT NULL DEFAULT '' COMMENT '融资机构名称',
  `total_credit_limit` bigint(20) NOT NULL COMMENT '总授信额度',
  `remaining_credit_limit` bigint(20) DEFAULT NULL COMMENT '剩余授信额度',
  `time_limit_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '期限类型',
  `business_type` varchar(50) NOT NULL DEFAULT '' COMMENT '业务类型',
  `guarantee_info` json DEFAULT NULL COMMENT '担保信息',
  `funds_purpose` varchar(200) NOT NULL DEFAULT '' COMMENT '资金用途',
  `remark` text COMMENT '备注',
  `fund_manager_id` bigint(20) NOT NULL COMMENT '资金经理id',
  `dept_id` bigint(20) NOT NULL COMMENT '所属部门id',
  `biz_header_id` bigint(20) unsigned NOT NULL COMMENT '部门负责人id',
  `leader_id` bigint(20) NOT NULL COMMENT '分管领导id',
  `financing_status` varchar(20) NOT NULL DEFAULT '' COMMENT '融资状态',
  `approval_status` varchar(30) NOT NULL DEFAULT '' COMMENT '审批状态',
  `change_sub_type` varchar(20) DEFAULT NULL COMMENT '审批流子类型',
  `plan_loan_date` date DEFAULT NULL COMMENT '计划贷款时间',
  `actual_loan_date` date DEFAULT NULL COMMENT '实际贷款日期',
  `actual_expire_date` date DEFAULT NULL COMMENT '实际到期日期',
  `financing_amount` bigint(20) NOT NULL COMMENT '融资金额',
  `has_pledge_info` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有质押',
  `repay_day` tinyint(4) unsigned DEFAULT NULL COMMENT '还款日',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_credit_code` (`credit_code`),
  KEY `idx_origin_id` (`origin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-基本信息版本';

-- Create syntax for TABLE 'fund_financing_collect_account'
CREATE TABLE `fund_financing_collect_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `client_name` varchar(200) DEFAULT NULL COMMENT '客户名称',
  `account_name` varchar(200) DEFAULT NULL COMMENT '账户名称',
  `account_num` varchar(30) DEFAULT NULL COMMENT '银行账号',
  `account_address` varchar(200) DEFAULT NULL COMMENT '开户行',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-对方收款账户';

-- Create syntax for TABLE 'fund_financing_collect_account_lib'
CREATE TABLE `fund_financing_collect_account_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `client_name` varchar(200) DEFAULT NULL COMMENT '客户名称',
  `account_name` varchar(200) DEFAULT NULL COMMENT '账户名称',
  `account_num` varchar(30) DEFAULT NULL COMMENT '银行账号',
  `account_address` varchar(200) DEFAULT NULL COMMENT '开户行',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-对方收款账户版本';

-- Create syntax for TABLE 'fund_financing_early_settle_plan'
CREATE TABLE `fund_financing_early_settle_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `early_repay_amount` bigint(20) NOT NULL COMMENT '提前偿还金额',
  `early_principle_amount` bigint(20) NOT NULL COMMENT '提前偿还本金金额',
  `early_interest_amount` bigint(20) DEFAULT NULL COMMENT '提前偿还利息金额',
  `liquidated_damages_amount` bigint(20) DEFAULT NULL COMMENT '违约金金额',
  `other_fee_amount` bigint(20) DEFAULT NULL COMMENT '其他费用金额',
  `remission_amount` bigint(20) DEFAULT NULL COMMENT '违约全减免金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-提前结清方案';

-- Create syntax for TABLE 'fund_financing_early_settle_plan_lib'
CREATE TABLE `fund_financing_early_settle_plan_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `early_repay_amount` bigint(20) NOT NULL COMMENT '提前偿还金额',
  `early_principle_amount` bigint(20) NOT NULL COMMENT '提前偿还本金金额',
  `early_interest_amount` bigint(20) DEFAULT NULL COMMENT '提前偿还利息金额',
  `liquidated_damages_amount` bigint(20) DEFAULT NULL COMMENT '违约金金额',
  `other_fee_amount` bigint(20) DEFAULT NULL COMMENT '其他费用金额',
  `remission_amount` bigint(20) DEFAULT NULL COMMENT '违约全减免金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-提前结清方案版本';

-- Create syntax for TABLE 'fund_financing_pay_account'
CREATE TABLE `fund_financing_pay_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `bank_account_id` bigint(20) DEFAULT NULL COMMENT '基础数据-我方银行账户id',
  `account_bank` varchar(255) DEFAULT NULL COMMENT '支行名称',
  `account_number` varchar(50) DEFAULT NULL COMMENT '账号',
  `account_type` varchar(20) DEFAULT NULL COMMENT '账户类型',
  `account_opening_date` datetime DEFAULT NULL COMMENT '开户时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-我方付款账户';

-- Create syntax for TABLE 'fund_financing_pay_account_lib'
CREATE TABLE `fund_financing_pay_account_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `bank_account_id` bigint(20) DEFAULT NULL COMMENT '基础数据-我方银行账户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  `account_bank` varchar(255) DEFAULT NULL COMMENT '支行名称',
  `account_number` varchar(50) DEFAULT NULL COMMENT '账号',
  `account_type` varchar(20) DEFAULT NULL COMMENT '账户类型',
  `account_opening_date` datetime DEFAULT NULL COMMENT '开户时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-我方付款账户版本';

-- Create syntax for TABLE 'fund_financing_plan'
CREATE TABLE `fund_financing_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `financing_amount` bigint(20) NOT NULL COMMENT '融资金额',
  `interest_amount` bigint(20) DEFAULT NULL COMMENT '预计利息金额',
  `financing_month` int(10) DEFAULT NULL COMMENT '融资期限（月）',
  `service_charge_amount` bigint(20) DEFAULT NULL COMMENT '手续费',
  `repay_times` int(10) DEFAULT NULL COMMENT '还款期数',
  `earnest_money_amount` bigint(20) DEFAULT NULL COMMENT '保证金',
  `repay_frequency` varchar(20) DEFAULT NULL COMMENT '还款频率',
  `license_amount` bigint(20) DEFAULT NULL COMMENT '开证许可证费用',
  `repay_way` varchar(20) DEFAULT '' COMMENT '还款方式',
  `other_amount` bigint(20) DEFAULT NULL COMMENT '其他费用',
  `guarantee_amount_info` json DEFAULT NULL COMMENT '担保费信息',
  `interest_rate_type` varchar(20) DEFAULT '' COMMENT '借款年利率类型',
  `lpr_type` varchar(20) DEFAULT '' COMMENT 'LPR品种',
  `lpr_rate_percent` int(10) DEFAULT NULL COMMENT 'LPR利率',
  `lpr_add_percent` int(10) DEFAULT NULL COMMENT 'LPR加点',
  `comprehensive_interest_rate` int(10) DEFAULT NULL COMMENT '综合借款年利率',
  `repay_day` tinyint(4) DEFAULT NULL COMMENT '还款日',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-融资方案';

-- Create syntax for TABLE 'fund_financing_plan_lib'
CREATE TABLE `fund_financing_plan_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `financing_amount` bigint(20) NOT NULL COMMENT '融资金额',
  `interest_amount` bigint(20) DEFAULT NULL COMMENT '预计利息金额',
  `financing_month` int(10) DEFAULT NULL COMMENT '融资期限（月）',
  `service_charge_amount` bigint(20) DEFAULT NULL COMMENT '手续费',
  `repay_times` int(10) DEFAULT NULL COMMENT '还款期数',
  `earnest_money_amount` bigint(20) DEFAULT NULL COMMENT '保证金',
  `repay_frequency` varchar(20) DEFAULT NULL COMMENT '还款频率',
  `license_amount` bigint(20) DEFAULT NULL COMMENT '开证许可证费用',
  `repay_way` varchar(20) DEFAULT '' COMMENT '还款方式',
  `other_amount` bigint(20) DEFAULT NULL COMMENT '其他费用',
  `guarantee_amount_info` json DEFAULT NULL COMMENT '担保费信息',
  `interest_rate_type` varchar(20) DEFAULT '' COMMENT '借款年利率类型',
  `lpr_type` varchar(20) DEFAULT '' COMMENT 'LPR品种',
  `lpr_rate_percent` int(10) DEFAULT NULL COMMENT 'LPR利率',
  `lpr_add_percent` int(10) DEFAULT NULL COMMENT 'LPR加点',
  `comprehensive_interest_rate` int(10) DEFAULT NULL COMMENT '综合借款年利率',
  `repay_day` tinyint(4) DEFAULT NULL COMMENT '还款日',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-融资方案版本';

-- Create syntax for TABLE 'fund_financing_pledge_info'
CREATE TABLE `fund_financing_pledge_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `pledge_code` varchar(50) NOT NULL COMMENT '质押编号',
  `biz_dept_id` bigint(20) NOT NULL COMMENT '业务部门id',
  `proj_review_id` bigint(20) NOT NULL COMMENT '项目评审id',
  `proj_name` varchar(200) NOT NULL COMMENT '项目名称',
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `contract_code` varchar(50) NOT NULL COMMENT '合同编号',
  `biz_type` varchar(20) NOT NULL COMMENT '业务类型',
  `contract_amount` bigint(20) NOT NULL COMMENT '合同金额',
  `contract_start_date` date DEFAULT NULL COMMENT '合同开始日期',
  `contract_end_date` date DEFAULT NULL COMMENT '合同结束日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `remaining_unpaid_principal` bigint(20) DEFAULT NULL COMMENT '剩余未还本金',
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-质押信息';

-- Create syntax for TABLE 'fund_financing_pledge_info_lib'
CREATE TABLE `fund_financing_pledge_info_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `pledge_code` varchar(50) NOT NULL COMMENT '质押编号',
  `biz_dept_id` bigint(20) NOT NULL COMMENT '业务部门id',
  `proj_review_id` bigint(20) NOT NULL COMMENT '项目评审id',
  `proj_name` varchar(200) NOT NULL COMMENT '项目名称',
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `contract_code` varchar(50) NOT NULL COMMENT '合同编号',
  `biz_type` varchar(20) NOT NULL COMMENT '业务类型',
  `contract_amount` bigint(20) NOT NULL COMMENT '合同金额',
  `contract_start_date` date DEFAULT NULL COMMENT '合同开始日期',
  `contract_end_date` date DEFAULT NULL COMMENT '合同结束日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  `remaining_unpaid_principal` bigint(20) DEFAULT NULL COMMENT '剩余未还本金',
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-质押信息版本';

-- Create syntax for TABLE 'fund_financing_repay_actual'
CREATE TABLE `fund_financing_repay_actual` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `cash_flow_code` varchar(60) DEFAULT NULL COMMENT '现金流编号',
  `repay_date` date NOT NULL COMMENT '还款日期',
  `phase` int(10) NOT NULL COMMENT '还款期项',
  `principle_amount` bigint(20) DEFAULT NULL COMMENT '本金',
  `interest_amount` bigint(20) DEFAULT NULL COMMENT '利息',
  `repay_amount` bigint(20) DEFAULT NULL COMMENT '应还总额',
  `remaining_principle_amount` bigint(20) DEFAULT NULL COMMENT '剩余未还本金',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-还款实际表';

-- Create syntax for TABLE 'fund_financing_repay_actual_lib'
CREATE TABLE `fund_financing_repay_actual_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `cash_flow_code` varchar(60) DEFAULT NULL COMMENT '现金流编号',
  `repay_date` date NOT NULL COMMENT '还款日期',
  `phase` int(10) NOT NULL COMMENT '还款期项',
  `principle_amount` bigint(20) DEFAULT NULL COMMENT '本金',
  `interest_amount` bigint(20) DEFAULT NULL COMMENT '利息',
  `repay_amount` bigint(20) DEFAULT NULL COMMENT '应还总额',
  `remaining_principle_amount` bigint(20) DEFAULT NULL COMMENT '剩余未还本金',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-还款实际表版本';

-- Create syntax for TABLE 'fund_financing_repay_estimate'
CREATE TABLE `fund_financing_repay_estimate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `repay_date` date NOT NULL COMMENT '还款日期',
  `phase` int(10) NOT NULL COMMENT '还款期项',
  `principle_amount` bigint(20) DEFAULT NULL COMMENT '本金',
  `interest_amount` bigint(20) DEFAULT NULL COMMENT '利息',
  `repay_amount` bigint(20) DEFAULT NULL COMMENT '应还总额',
  `remaining_principle_amount` bigint(20) DEFAULT NULL COMMENT '剩余未还本金',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-还款概算表';

-- Create syntax for TABLE 'fund_financing_repay_estimate_lib'
CREATE TABLE `fund_financing_repay_estimate_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
  `repay_date` date NOT NULL COMMENT '还款日期',
  `phase` int(10) NOT NULL COMMENT '还款期项',
  `principle_amount` bigint(20) DEFAULT NULL COMMENT '本金',
  `interest_amount` bigint(20) DEFAULT NULL COMMENT '利息',
  `repay_amount` bigint(20) DEFAULT NULL COMMENT '应还总额',
  `remaining_principle_amount` bigint(20) DEFAULT NULL COMMENT '剩余未还本金',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_financing_id` (`financing_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金管理-融资管理-还款概算表版本';

ALTER TABLE fund_financing_plan ADD lpr_arrange_mode varchar(50) NULL COMMENT 'LPR调整方式';
ALTER TABLE fund_financing_plan ADD lpr_adjustment_day varchar(50) NULL COMMENT 'LPR调整日';
ALTER TABLE fund_financing_plan ADD comprehensive_interest_rate_current int(10) NULL COMMENT '综合借款年利率-当前使用';

ALTER TABLE fund_financing_plan_lib ADD lpr_arrange_mode varchar(50) NULL COMMENT 'LPR调整方式';
ALTER TABLE fund_financing_plan_lib ADD lpr_adjustment_day varchar(50) NULL COMMENT 'LPR调整日';
ALTER TABLE fund_financing_plan_lib ADD comprehensive_interest_rate_current int(10) NULL COMMENT '综合借款年利率-当前使用';

update fund_financing_plan set comprehensive_interest_rate_current = lpr_rate_percent + lpr_add_percent;
