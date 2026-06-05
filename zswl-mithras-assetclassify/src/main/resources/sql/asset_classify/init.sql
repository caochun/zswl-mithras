CREATE TABLE `asset_classify` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `year` int(11) NOT NULL COMMENT '年份',
  `quarter` tinyint(4) NOT NULL COMMENT '季度',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `classify_amount` json DEFAULT NULL COMMENT '五级分类数据',
  `finish` tinyint(4) DEFAULT NULL COMMENT '是否结束，0-否，1-是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产五级分类';

CREATE TABLE `asset_classify_check_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `asset_classify_id` bigint(20) NOT NULL COMMENT '资产五级分类id',
  `asset_classify_client_id` bigint(20) NOT NULL COMMENT '资产检查客户表主键id',
  `template_id` bigint(20) DEFAULT NULL COMMENT '检查内容模板id',
  `template_group_name` varchar(50) DEFAULT NULL COMMENT '检查内容模板分组名称',
  `template_code` varchar(50) DEFAULT NULL COMMENT '检查内容模板条目code',
  `template_title` varchar(100) NOT NULL COMMENT '检查内容模板条目',
  `template_content_input_type` varchar(20) NOT NULL COMMENT '检查内容模板输入框类型',
  `template_content_input_option` varchar(100) DEFAULT NULL COMMENT '检查内容模板输入框枚举类型（如果是下拉框等才会有值）',
  `template_order_num` int(11) NOT NULL DEFAULT '0' COMMENT '检查模板条目排序',
  `content` varchar(2000) DEFAULT NULL COMMENT '填写的检查内容',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `after_lease_check_plan_project_id` bigint(20) DEFAULT NULL COMMENT '租后检查计划ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_asset_classify_id` (`asset_classify_id`),
  KEY `idx_asset_classify_client_id` (`asset_classify_client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='五级分类检查报告检查内容表';

CREATE TABLE `asset_classify_check_content_auxiliary_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `asset_classify_id` bigint(20) NOT NULL COMMENT '资产五级分类id',
  `asset_classify_client_id` bigint(20) NOT NULL COMMENT '检查计划项目表主键id',
  `template_id` bigint(20) DEFAULT NULL COMMENT '检查内容模板id',
  `template_group_name` varchar(50) DEFAULT NULL COMMENT '检查内容模板分组名称',
  `template_code` varchar(50) DEFAULT NULL COMMENT '检查内容模板条目code',
  `template_title` varchar(100) NOT NULL COMMENT '检查内容模板条目',
  `template_content_input_type` varchar(20) NOT NULL COMMENT '检查内容模板输入框类型',
  `template_content_input_option` varchar(100) DEFAULT NULL COMMENT '检查内容模板输入框枚举类型（如果是下拉框等才会有值）',
  `template_order_num` int(11) NOT NULL DEFAULT '0' COMMENT '检查模板条目排序',
  `content` varchar(2000) DEFAULT NULL COMMENT '填写的检查内容',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  `after_lease_check_plan_project_id` bigint(20) DEFAULT NULL COMMENT '租后检查计划ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_asset_classify_id` (`asset_classify_id`),
  KEY `idx_asset_classify_client_id` (`asset_classify_client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='五级分类检查报告检查内容表_版本表（作为大流程子表）';

CREATE TABLE `asset_classify_check_content_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `asset_classify_id` bigint(20) NOT NULL COMMENT '资产五级分类id',
  `asset_classify_client_id` bigint(20) NOT NULL COMMENT '检查计划项目表主键id',
  `template_id` bigint(20) DEFAULT NULL COMMENT '检查内容模板id',
  `template_group_name` varchar(50) DEFAULT NULL COMMENT '检查内容模板分组名称',
  `template_code` varchar(50) DEFAULT NULL COMMENT '检查内容模板条目code',
  `template_title` varchar(100) NOT NULL COMMENT '检查内容模板条目',
  `template_content_input_type` varchar(20) NOT NULL COMMENT '检查内容模板输入框类型',
  `template_content_input_option` varchar(100) DEFAULT NULL COMMENT '检查内容模板输入框枚举类型（如果是下拉框等才会有值）',
  `template_order_num` int(11) NOT NULL DEFAULT '0' COMMENT '检查模板条目排序',
  `content` varchar(2000) DEFAULT NULL COMMENT '填写的检查内容',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  `after_lease_check_plan_project_id` bigint(20) DEFAULT NULL COMMENT '租后检查计划ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_asset_classify_id` (`asset_classify_id`),
  KEY `idx_asset_classify_client_id` (`asset_classify_client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='五级分类检查报告检查内容表_版本表';

CREATE TABLE `asset_classify_client` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_classify_id` bigint(20) NOT NULL COMMENT '资产五级分类主表id',
  `client_id` bigint(20) NOT NULL COMMENT '客户id',
  `client_name` varchar(100) NOT NULL DEFAULT '' COMMENT '客户名称',
  `amount` bigint(20) NOT NULL COMMENT '投放金额',
  `remaining_term` int(11) NOT NULL COMMENT '剩余租期',
  `qualitative_adjust` tinyint(4) DEFAULT NULL COMMENT '定性调整',
  `last_classify_result` varchar(20) DEFAULT NULL COMMENT '上次分类结果',
  `init_classify_result` varchar(20) DEFAULT NULL COMMENT '初分结果',
  `classify_result` varchar(20) DEFAULT NULL COMMENT '本次分类结果',
  `review_status` varchar(20) NOT NULL COMMENT '复核状态',
  `review_pass_time` datetime DEFAULT NULL COMMENT '复核通过时间',
  `belong_dept_id` bigint(20) NOT NULL COMMENT '归属部门id',
  `belong_sponsor_id` bigint(20) NOT NULL COMMENT '归属主办id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `suggest_result` varchar(50) DEFAULT NULL COMMENT '建议分类结果',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `overdue_days` int(11) DEFAULT NULL COMMENT '逾期天数',
  `overdue_amount` bigint(20) DEFAULT NULL COMMENT '逾期金额 该季度该客户的逾期金额',
  `stock_risk_exposure` bigint(20) DEFAULT NULL COMMENT '存量风险敞口',
  `contract_expiration_date` datetime DEFAULT NULL COMMENT '合同到期日',
  `start_rent_contract_codes` json DEFAULT NULL COMMENT '在租合同编号',
  `start_rent_contractRemainingPrincipal` json DEFAULT NULL COMMENT '在租合同剩余本金',
  `board_meeting` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否进入董事会流程，0-否，1-是',
  PRIMARY KEY (`id`),
  KEY `idx_asset_classify_id` (`asset_classify_id`),
  KEY `idx_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产五级分类客户信息';

CREATE TABLE `asset_classify_client_auxiliary_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_classify_id` bigint(20) NOT NULL COMMENT '资产五级分类主表id',
  `client_id` bigint(20) NOT NULL COMMENT '客户id',
  `client_name` varchar(100) NOT NULL DEFAULT '' COMMENT '客户名称',
  `amount` bigint(20) NOT NULL COMMENT '投放金额',
  `remaining_term` int(11) NOT NULL COMMENT '剩余租期',
  `qualitative_adjust` tinyint(4) DEFAULT NULL COMMENT '定性调整',
  `last_classify_result` varchar(20) DEFAULT NULL COMMENT '上次分类结果',
  `init_classify_result` varchar(20) DEFAULT NULL COMMENT '初分结果',
  `classify_result` varchar(20) DEFAULT NULL COMMENT '本次分类结果',
  `review_status` varchar(20) NOT NULL COMMENT '复核状态',
  `review_pass_time` datetime DEFAULT NULL COMMENT '复核通过时间',
  `belong_dept_id` bigint(20) NOT NULL COMMENT '归属部门id',
  `belong_sponsor_id` bigint(20) NOT NULL COMMENT '归属主办id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `suggest_result` varchar(50) DEFAULT NULL COMMENT '建议分类结果',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `overdue_days` int(11) DEFAULT NULL COMMENT '逾期天数',
  `overdue_amount` bigint(20) DEFAULT NULL COMMENT '逾期金额 该季度该客户的逾期金额',
  `stock_risk_exposure` bigint(20) DEFAULT NULL COMMENT '存量风险敞口',
  `contract_expiration_date` datetime DEFAULT NULL COMMENT '合同到期日',
  `start_rent_contract_codes` json DEFAULT NULL COMMENT '在租合同编号',
  `start_rent_contractRemainingPrincipal` json DEFAULT NULL COMMENT '在租合同剩余本金',
  `board_meeting` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否进入董事会流程，0-否，1-是',
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_asset_classify_id` (`asset_classify_id`),
  KEY `idx_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产五级分类客户信息辅助版本表(作为子表版本)';

CREATE TABLE `asset_classify_client_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_classify_id` bigint(20) NOT NULL COMMENT '资产五级分类主表id',
  `client_id` bigint(20) NOT NULL COMMENT '客户id',
  `client_name` varchar(100) NOT NULL DEFAULT '' COMMENT '客户名称',
  `amount` bigint(20) NOT NULL COMMENT '投放金额',
  `remaining_term` int(11) NOT NULL COMMENT '剩余租期',
  `qualitative_adjust` tinyint(4) DEFAULT NULL COMMENT '定性调整',
  `last_classify_result` varchar(20) DEFAULT NULL COMMENT '上次分类结果',
  `init_classify_result` varchar(20) DEFAULT NULL COMMENT '初分结果',
  `classify_result` varchar(20) DEFAULT NULL COMMENT '本次分类结果',
  `review_status` varchar(20) NOT NULL COMMENT '复核状态',
  `review_pass_time` datetime DEFAULT NULL COMMENT '复核通过时间',
  `belong_dept_id` bigint(20) NOT NULL COMMENT '归属部门id',
  `belong_sponsor_id` bigint(20) NOT NULL COMMENT '归属主办id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `suggest_result` varchar(50) DEFAULT NULL COMMENT '建议分类结果',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `overdue_days` int(11) DEFAULT NULL COMMENT '逾期天数',
  `overdue_amount` bigint(20) DEFAULT NULL COMMENT '逾期金额 该季度该客户的逾期金额',
  `stock_risk_exposure` bigint(20) DEFAULT NULL COMMENT '存量风险敞口',
  `contract_expiration_date` datetime DEFAULT NULL COMMENT '合同到期日',
  `start_rent_contract_codes` json DEFAULT NULL COMMENT '在租合同编号',
  `start_rent_contractRemainingPrincipal` json DEFAULT NULL COMMENT '在租合同剩余本金',
  `board_meeting` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否经过董事会流程，0-否，1-是',
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_asset_classify_id` (`asset_classify_id`),
  KEY `idx_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产五级分类客户信息版本表';

CREATE TABLE `asset_classify_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `year` int(11) NOT NULL COMMENT '年份',
  `quarter` tinyint(4) NOT NULL COMMENT '季度',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `classify_amount` json DEFAULT NULL COMMENT '五级分类数据',
  `finish` tinyint(4) DEFAULT NULL COMMENT '是否结束，0-否，1-是',
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产五级分类版本表';

CREATE TABLE `asset_classify_node_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_classify_id` bigint(20) NOT NULL COMMENT '资产五级分类主表id',
  `node_name` varchar(20) NOT NULL COMMENT '节点名称',
  `start_time` datetime DEFAULT NULL COMMENT '节点开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '节点结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `node_statue` varchar(50) DEFAULT NULL COMMENT '节点所处状态',
  PRIMARY KEY (`id`),
  KEY `idx_asset_classify_id` (`asset_classify_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产五级分类流程节点记录';

CREATE TABLE `asset_classify_node_record_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_classify_id` bigint(20) NOT NULL COMMENT '资产五级分类主表id',
  `node_name` varchar(20) NOT NULL COMMENT '节点名称',
  `start_time` datetime DEFAULT NULL COMMENT '节点开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '节点结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `node_statue` varchar(50) DEFAULT NULL COMMENT '节点所处状态',
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_asset_classify_id` (`asset_classify_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产五级分类流程节点记录版本表';


ALTER table after_lease_check_project_report_template MODIFY report_type VARCHAR(50);
INSERT INTO `after_lease_check_project_report_template` (`report_type`, `area_type`, `group_name`, `code`, `title`, `content_input_type`, `content_input_option`, `order_num`)
VALUES
('ASSET_CLASSIFY_NON_PUBLIC', 'SUMMARY', '检查总结', 'ACNP_S_1_01', '风险信号及重大事项、风险防范措施', 'textArea', NULL, 300),
('ASSET_CLASSIFY_NON_PUBLIC', 'SUMMARY', '检查总结', 'ACNP_S_1_02', '重要公开信息分析', 'textArea', NULL, 310),
('ASSET_CLASSIFY_NON_PUBLIC', 'SUMMARY', '检查总结', 'ACNP_S_1_03', '其他重大事项分析', 'textArea', NULL, 320);

alter table payment_base_info add column remark text default null comment '备注说明';
alter table payment_base_info_lib add column remark text default null comment '备注说明';
alter table general_dictionary modify column code varchar(50);
INSERT INTO `mithras`.`general_dictionary` (`id`, `dict_key`, `dict_desc`, `code`, `display`, `sort`) VALUES (943, 'job', '岗位类型', 'moneymanagerleader', '资金分管领导', 10);
INSERT INTO `mithras`.`general_dictionary` (`id`, `dict_key`, `dict_desc`, `code`, `display`, `sort`) VALUES (944, 'job', '岗位类型', 'pricingcommitteesecretary', '定价委员会秘书', 10);
INSERT INTO `mithras`.`general_dictionary` (`id`, `dict_key`, `dict_desc`, `code`, `display`, `sort`) VALUES (945, 'job', '岗位类型', 'pricingcommitteemember', '定价委员会委员', 10);
INSERT INTO `mithras`.`general_dictionary` (`id`, `dict_key`, `dict_desc`, `code`, `display`, `sort`) VALUES (946, 'job', '岗位类型', 'pricingcommitteedirector', '定价委员会主任', 10);

DROP TABLE IF EXISTS `ftp_monthly_guidance`;
CREATE TABLE `ftp_monthly_guidance` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `year` int(11) DEFAULT NULL COMMENT '年度',
    `month` int(2) DEFAULT NULL COMMENT '月度',
    `guidance_process_status` varchar(50) DEFAULT NULL COMMENT '审批状态',
    `guidance_record_status` varchar(50) DEFAULT NULL,
    `one_year_earnings_guidance` int(11) DEFAULT NULL COMMENT '一年期ftp收益指导报价',
    `one_to_three_earnings_guidance` int(11) DEFAULT NULL COMMENT '1-3年期ftp收益指导报价',
    `more_than_three_earnings_guidance` int(11) DEFAULT NULL COMMENT '3年以上ftp收益指导报价',
    `selling_price` int(11) DEFAULT NULL COMMENT '卖出价',
    `buying_price` int(11) DEFAULT NULL COMMENT '买入价',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uniq_year_month` (`year`,`month`) COMMENT '月度唯一'
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COMMENT='月度指导';

DROP TABLE IF EXISTS `ftp_monthly_guidance_lib`;
CREATE TABLE `ftp_monthly_guidance_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `year` int(11) DEFAULT NULL COMMENT '年度',
    `month` int(2) DEFAULT NULL COMMENT '月度',
    `guidance_process_status` varchar(50) DEFAULT NULL COMMENT '审批状态',
    `guidance_record_status` varchar(50) DEFAULT NULL,
    `one_year_earnings_guidance` int(11) DEFAULT NULL COMMENT '一年期ftp收益指导报价',
    `one_to_three_earnings_guidance` int(11) DEFAULT NULL COMMENT '1-3年期ftp收益指导报价',
    `more_than_three_earnings_guidance` int(11) DEFAULT NULL COMMENT '3年以上ftp收益指导报价',
    `selling_price` int(11) DEFAULT NULL COMMENT '卖出价',
    `buying_price` int(11) DEFAULT NULL COMMENT '买入价',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=228 DEFAULT CHARSET=utf8mb4 COMMENT='月度指导版本表';

DROP TABLE IF EXISTS `ftp_monthly_pricing`;
CREATE TABLE `ftp_monthly_pricing` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
   `enterprise_type` varchar(50) NOT NULL COMMENT '企业类型',
   `credit_term` varchar(50) NOT NULL COMMENT '期限',
   `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
   `value` int(11) NOT NULL COMMENT '值',
   `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
   `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
   `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1448 DEFAULT CHARSET=utf8mb4 COMMENT='月度ftp定价指导';

DROP TABLE IF EXISTS `ftp_monthly_pricing_lib`;
CREATE TABLE `ftp_monthly_pricing_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `enterprise_type` varchar(50) NOT NULL COMMENT '企业类型',
    `credit_term` varchar(50) NOT NULL COMMENT '期限',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `value` int(11) NOT NULL COMMENT '值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5288 DEFAULT CHARSET=utf8mb4 COMMENT='月度ftp定价指导版本表';

DROP TABLE IF EXISTS `ftp_monthly_valuation`;
CREATE TABLE `ftp_monthly_valuation` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `crdit_term` varchar(50) DEFAULT NULL COMMENT '期限',
    `financing_cost` int(11) DEFAULT NULL COMMENT '融资成本',
    `guarantee_cost` int(11) DEFAULT NULL COMMENT '担保成本',
    `subtotal_cost` int(11) DEFAULT NULL COMMENT '成本小计',
    `discount_rate` int(11) DEFAULT NULL COMMENT '国股银票转贴现利率',
    `discount_rate_weight` int(11) DEFAULT NULL COMMENT '权重1',
    `shibor_rate` int(11) DEFAULT NULL COMMENT '1年期shibor利率',
    `shibor_rate_weight` int(11) DEFAULT NULL COMMENT '权重2',
    `lpr_rate` int(11) DEFAULT NULL COMMENT '同期LPR利率',
    `lpr_rate_weight` int(11) DEFAULT NULL COMMENT '权重3',
    `finance_cost_trends` int(11) DEFAULT NULL COMMENT '融资成本趋势',
    `finance_cost_trends_weight` int(11) DEFAULT NULL COMMENT '权重4',
    `subtotal_rate` int(11) DEFAULT NULL COMMENT '小计',
    `subtotal_adjustment_valuation` int(11) DEFAULT NULL COMMENT '调整后计价小计',
    `encourage_valuation` int(11) DEFAULT NULL COMMENT '鼓励介入类',
    `moderate_support_valuation` int(11) DEFAULT NULL COMMENT '适度类',
    `cautious_valuation` int(11) DEFAULT NULL COMMENT '谨慎支持类',
    `state_own_listed_valuation` int(11) DEFAULT NULL COMMENT '国有/上市公司',
    `other_valuation` int(11) DEFAULT NULL COMMENT '其他类',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=248 DEFAULT CHARSET=utf8mb4 COMMENT='月度计价指导';

DROP TABLE IF EXISTS `ftp_monthly_valuation_lib`;
CREATE TABLE `ftp_monthly_valuation_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `crdit_term` varchar(50) DEFAULT NULL COMMENT '期限',
    `financing_cost` int(11) DEFAULT NULL COMMENT '融资成本',
    `guarantee_cost` int(11) DEFAULT NULL COMMENT '担保成本',
    `subtotal_cost` int(11) DEFAULT NULL COMMENT '成本小计',
    `discount_rate` int(11) DEFAULT NULL COMMENT '国股银票转贴现利率',
    `discount_rate_weight` int(11) DEFAULT NULL COMMENT '权重1',
    `shibor_rate` int(11) DEFAULT NULL COMMENT '1年期shibor利率',
    `shibor_rate_weight` int(11) DEFAULT NULL COMMENT '权重2',
    `lpr_rate` int(11) DEFAULT NULL COMMENT '同期LPR利率',
    `lpr_rate_weight` int(11) DEFAULT NULL COMMENT '权重3',
    `finance_cost_trends` int(11) DEFAULT NULL COMMENT '融资成本趋势',
    `finance_cost_trends_weight` int(11) DEFAULT NULL COMMENT '权重4',
    `subtotal_rate` int(11) DEFAULT NULL COMMENT '小计',
    `subtotal_adjustment_valuation` int(11) DEFAULT NULL COMMENT '调整后计价小计',
    `encourage_valuation` int(11) DEFAULT NULL COMMENT '鼓励介入类',
    `moderate_support_valuation` int(11) DEFAULT NULL COMMENT '适度类',
    `cautious_valuation` int(11) DEFAULT NULL COMMENT '谨慎支持类',
    `state_own_listed_valuation` int(11) DEFAULT NULL COMMENT '国有/上市公司',
    `other_valuation` int(11) DEFAULT NULL COMMENT '其他类',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=668 DEFAULT CHARSET=utf8mb4 COMMENT='月度计价指导版本表';

DROP TABLE IF EXISTS `ftp_quarterly_base_pricing`;
CREATE TABLE `ftp_quarterly_base_pricing` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `month_type` varchar(50) NOT NULL COMMENT '孟月、仲月、季月',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `credit_term` varchar(50) NOT NULL COMMENT '1年期、1-3年、3年以上',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1628 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导基础定价';

DROP TABLE IF EXISTS `ftp_quarterly_base_pricing_lib`;
CREATE TABLE `ftp_quarterly_base_pricing_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `month_type` varchar(50) NOT NULL COMMENT '孟月、仲月、季月',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `credit_term` varchar(50) NOT NULL COMMENT '1年期、1-3年、3年以上',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1628 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导基础定价版本表';

DROP TABLE IF EXISTS `ftp_quarterly_customer_principal_pricing`;
CREATE TABLE `ftp_quarterly_customer_principal_pricing` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `credit_term` varchar(50) NOT NULL COMMENT '1年期、1-3年、3年以上',
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=368 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导客户主体计价';

DROP TABLE IF EXISTS `ftp_quarterly_customer_principal_pricing_lib`;
CREATE TABLE `ftp_quarterly_customer_principal_pricing_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `credit_term` varchar(50) NOT NULL COMMENT '1年期、1-3年、3年以上',
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=368 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导客户主体计价版本表';

DROP TABLE IF EXISTS `ftp_quarterly_enterprise_pricing`;
CREATE TABLE `ftp_quarterly_enterprise_pricing` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `credit_term` varchar(50) NOT NULL,
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1088 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导按企业类型定价';

DROP TABLE IF EXISTS `ftp_quarterly_enterprise_pricing_lib`;
CREATE TABLE `ftp_quarterly_enterprise_pricing_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `credit_term` varchar(50) NOT NULL,
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1088 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导按企业类型定价版本表';

DROP TABLE IF EXISTS `ftp_quarterly_guidance`;
CREATE TABLE `ftp_quarterly_guidance` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `year` int(4) DEFAULT NULL COMMENT '年度',
    `quarter` tinyint(1) DEFAULT NULL COMMENT '季度',
    `guidance_process_status` varchar(50) DEFAULT NULL COMMENT '审批状态',
    `guidance_record_status` varchar(50) DEFAULT NULL,
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uniq_year_quarter` (`year`,`quarter`) USING BTREE COMMENT '季度唯一'
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导';

DROP TABLE IF EXISTS `ftp_quarterly_guidance_lib`;
CREATE TABLE `ftp_quarterly_guidance_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `year` int(4) DEFAULT NULL COMMENT '年度',
    `quarter` tinyint(1) DEFAULT NULL COMMENT '季度',
    `guidance_process_status` varchar(50) DEFAULT NULL COMMENT '审批状态',
    `guidance_record_status` varchar(50) DEFAULT NULL,
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导版本表';

DROP TABLE IF EXISTS `ftp_quarterly_month_pricing`;
CREATE TABLE `ftp_quarterly_month_pricing` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `month_type` varchar(50) NOT NULL COMMENT '孟月、仲月、季月、均值',
    `credit_term` varchar(50) NOT NULL,
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1088 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导按月定价';

DROP TABLE IF EXISTS `ftp_quarterly_month_pricing_lib`;
CREATE TABLE `ftp_quarterly_month_pricing_lib` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
   `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
   `month_type` varchar(50) NOT NULL COMMENT '孟月、仲月、季月、均值',
   `credit_term` varchar(50) NOT NULL,
   `percent_value` int(11) NOT NULL COMMENT '利率值',
   `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
   `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
   `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
   `version` varchar(40) NOT NULL COMMENT '版本号',
   `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
   `data_create_time` datetime DEFAULT NULL,
   `data_create_by` bigint(20) DEFAULT NULL,
   `data_update_time` datetime DEFAULT NULL,
   `data_update_by` bigint(20) DEFAULT NULL,
   `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1088 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导按月定价版本表';

# 加索引
ALTER TABLE proj_establish_base_info ADD INDEX `proj_establish_base_info_proj_establish_status_IDX` (`proj_establish_status`) USING BTREE;

ALTER TABLE proj_review_base_info ADD INDEX `proj_review_ proj_sponsor_user_id` (`proj_sponsor_user_id`) USING BTREE COMMENT '主办索引';
ALTER TABLE proj_review_base_info ADD INDEX `proj_review_base_info_relation_data_type_IDX` (`relation_data_type`,`proj_review_status`) USING BTREE;
ALTER TABLE proj_review_base_info ADD INDEX `idx_proj_establish_id` (`proj_establish_id`) USING BTREE;

ALTER TABLE contract_base_info ADD INDEX `idx_proj_review_id` (`proj_review_id`) USING BTREE;