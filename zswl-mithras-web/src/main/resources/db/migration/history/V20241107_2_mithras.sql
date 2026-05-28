-------- 资金sql -------------------------------------------------------------
-- 授信额度表
CREATE TABLE `credit_limit` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `biz_type` varchar(20) NOT NULL COMMENT '业务类型',
                                `granting_subject_key` varchar(20) NOT NULL COMMENT '授信主体key，能唯一标识一个授信主体',
                                `biz_source_key` varchar(20) NOT NULL COMMENT '业务源key，能唯一标识一个授信（比如资金端可以是授信id，项目端可以是评审id）',
                                `query_key` varchar(100) NOT NULL DEFAULT '' COMMENT '查询key',
                                `total_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '授信总额度',
                                `guarantee_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '担保额度',
                                `credit_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '信用额度',
                                `occupy_total_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '已占用授信总额度',
                                `occupy_guarantee_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '已占用担保额度',
                                `occupy_credit_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '已占用信用额度',
                                `effective_date_from` date DEFAULT NULL COMMENT '有效期-起',
                                `effective_date_to` date DEFAULT NULL COMMENT '有效期-止',
                                `recyclable` tinyint(1) NOT NULL COMMENT '额度是否可循环，0-否，1-是',
                                `status` varchar(20) NOT NULL DEFAULT 'WAIT_EFFECTIVE' COMMENT '状态',
                                `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                PRIMARY KEY (`id`) USING BTREE,
                                KEY `idx_biztype_grantingsubjectkey` (`biz_type`,`granting_subject_key`,`deleted`),
                                KEY `idx_biztype_bizsourcekey` (`biz_type`,`biz_source_key`,`deleted`),
                                KEY `idx_query_key` (`query_key`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授信额度表';
-- 授信额度占用明细表
CREATE TABLE `credit_limit_detail` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `biz_type` varchar(20) NOT NULL COMMENT '业务类型',
                                       `granting_subject_key` varchar(20) NOT NULL COMMENT '授信主体key，能唯一标识一个授信主体',
                                       `biz_target_key` varchar(20) NOT NULL COMMENT '业务目标key，能唯一标识一个占用授信的业务数据（比如资金端可以是融资id，项目端可以是合同id）',
                                       `query_key` varchar(100) NOT NULL DEFAULT '' COMMENT '查询key',
                                       `occupy_total_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '已占用总额度',
                                       `occupy_guarantee_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '已占用担保额度',
                                       `occupy_credit_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '已占用信用额度',
                                       `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                       `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `idx_biztype_grantingsubjectkey` (`biz_type`,`granting_subject_key`,`deleted`),
                                       KEY `idx_biztype_biztargetkey` (`biz_type`,`biz_target_key`,`deleted`),
                                       KEY `idx_query_key` (`query_key`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授信额度占用明细表';
-- 业务目标和授信关联表
CREATE TABLE `credit_business_ref` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `biz_type` varchar(20) NOT NULL COMMENT '业务类型',
                                       `granting_subject_key` varchar(20) NOT NULL COMMENT '授信主体key，能唯一标识一个授信主体',
                                       `biz_source_key` varchar(20) NOT NULL COMMENT '业务源key，能唯一标识一个授信（比如资金端可以是授信id，项目端可以是评审id）',
                                       `biz_target_key` varchar(20) NOT NULL COMMENT '业务目标key，能唯一标识一个占用授信的业务数据（比如资金端可以是融资id，项目端可以是合同id）',
                                       `effective` tinyint(4) NOT NULL DEFAULT '1' COMMENT '关联关系是否生效，0-无效，1-有效',
                                       `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                       `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       KEY `idx_biztype_bizsourcekey` (`biz_type`,`biz_source_key`,`deleted`),
                                       KEY `idx_biztype_biztargetkey` (`biz_type`,`biz_target_key`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务和授信关联表';
-- 授信额度占用变更记录表
CREATE TABLE `credit_limit_change_record` (
                                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                              `biz_type` varchar(20) NOT NULL COMMENT '业务类型',
                                              `granting_subject_key` varchar(20) NOT NULL COMMENT '授信主体key，能唯一标识一个授信主体',
                                              `biz_source_key` varchar(20) NOT NULL DEFAULT '' COMMENT '业务源key，能唯一标识一个授信（比如资金端可以是授信id，项目端可以是评审id）',
                                              `biz_target_key` varchar(20) NOT NULL COMMENT '业务目标key，能唯一标识一个占用授信的业务数据（比如资金端可以是融资id，项目端可以是合同id）',
                                              `query_key` varchar(100) NOT NULL DEFAULT '' COMMENT '查询key',
                                              `change_type` varchar(30) NOT NULL COMMENT '变更类型，occupy - 占用，release - 释放',
                                              `change_date` date NOT NULL COMMENT '变更日期',
                                              `change_total_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '变更总额度',
                                              `change_guarantee_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '变更担保额度',
                                              `change_credit_limit` bigint(20) NOT NULL DEFAULT '0' COMMENT '变更信用额度',
                                              `remark` varchar(500) DEFAULT NULL COMMENT '备注说明',
                                              `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                              `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `idx_biztype_grantingsubjectkey` (`biz_type`,`granting_subject_key`,`deleted`),
                                              KEY `idx_biztype_biztargetkey` (`biz_type`,`biz_target_key`,`deleted`),
                                              KEY `idx_query_key` (`query_key`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授信额度占用变更记录表';

-- 计划现金流统一表
CREATE TABLE `fund_receipt_flow_plan` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `receipt_repay_id` bigint(20) DEFAULT NULL COMMENT '收付款id',
                                          `cash_flow_phase` tinyint(4) NOT NULL COMMENT '现金流期项',
                                          `cash_flow_code` varchar(50) NOT NULL COMMENT '现金流编号',
                                          `cash_flow_item` varchar(50) NOT NULL COMMENT '现金流类型',
                                          `cash_flow_date` date NOT NULL COMMENT '现金流日期',
                                          `total_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '总金额',
                                          `principal_amount` bigint(20) DEFAULT NULL COMMENT '本金金额',
                                          `interest_amount` bigint(20) DEFAULT NULL COMMENT '利息金额',
                                          `write_off_state` varchar(30) NOT NULL COMMENT '核销状态',
                                          `remark` text COMMENT '备注',
                                          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                          `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                          `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_receiptrepayid_deleted` (`receipt_repay_id`,`deleted`),
                                          KEY `idx_cashflowcode_deleted` (`cash_flow_code`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金端计划现金流';

-- 现金流类型元数据
CREATE TABLE `system_cash_flow_meta` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                         `category` varchar(30) NOT NULL COMMENT '类别',
                                         `fund_flow` varchar(20) NOT NULL COMMENT '资金流向',
                                         `cash_flow_item_code` varchar(50) NOT NULL COMMENT '现金流项目code',
                                         `cash_flow_item_display` varchar(50) NOT NULL COMMENT '现金流项目display',
                                         `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                         `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='现金流类型元数据';

-- 直融
alter table fund_direct_financing_base_info add carry_interest_time DATETIME COMMENT '起息日';
alter table fund_direct_financing_base_info add duration_time DATETIME COMMENT '到期日';
alter table fund_direct_financing_base_info add financing_month INT COMMENT '融资期限（月）';
alter table fund_direct_financing_base_info add financing_status VARCHAR(50) COMMENT '融资状态';
alter table fund_direct_financing_base_info add comprehensive_financing_cost BIGINT COMMENT '综合融资成本';
alter table fund_direct_financing_base_info add repay_way VARCHAR(255) COMMENT '还款方式';
alter table fund_direct_financing_base_info add repay_frequency VARCHAR(255) COMMENT '还款频率';
alter table fund_direct_financing_base_info add calculate_day INT COMMENT '计算日';

CREATE TABLE `fund_direct_financing_collect_account` (
                                                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                                         `financing_id` bigint(20) NOT NULL COMMENT '融资id',
                                                         `account_name` varchar(200) DEFAULT NULL COMMENT '账户名称',
                                                         `account_num` varchar(30) DEFAULT NULL COMMENT '银行账号',
                                                         `account_address` varchar(200) DEFAULT NULL COMMENT '开户行',
                                                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                         `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                         `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                                         `deleted` int(11) DEFAULT '0' COMMENT '逻辑删除',
                                                         PRIMARY KEY (`id`) USING BTREE,
                                                         KEY `idx_financing_id` (`financing_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=548 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='资金管理-直融管理-对方收款账户';

alter table fund_direct_financing_fee_detail add column pay_date DATETIME default null comment '支付时间';
alter table fund_direct_financing_repay_actual add column pre_pay_difference BIGINT(20) default null comment '预付差额';

-- 间融
alter table fund_financing_base_info add column organization_info json default null comment '融资机构信息';
alter table fund_financing_base_info_lib add column organization_info json default null comment '融资机构信息';

-- 不可为空校验取消： financing_amount credit_id credit_code total_credit_limit org_id org_name 可以为空
alter table fund_financing_base_info modify financing_amount BIGINT(20) null comment '融资金额';
alter table fund_financing_base_info modify credit_id BIGINT(20) null comment '授信id';
alter table fund_financing_base_info modify credit_code VARCHAR(50) null comment '授信编号';
alter table fund_financing_base_info modify total_credit_limit BIGINT(20) null comment '总授信额度';
alter table fund_financing_base_info modify organization_id BIGINT(20) null comment '融资机构id';
alter table fund_financing_base_info modify organization_name VARCHAR(50) null comment '融资机构名称';

alter table fund_financing_base_info_lib modify financing_amount BIGINT(20) null comment '融资金额';
alter table fund_financing_base_info_lib modify credit_id BIGINT(20) null comment '授信id';
alter table fund_financing_base_info_lib modify credit_code VARCHAR(50) null comment '授信编号';
alter table fund_financing_base_info_lib modify total_credit_limit BIGINT(20) null comment '总授信额度';
alter table fund_financing_base_info_lib modify organization_id BIGINT(20) null comment '融资机构id';
alter table fund_financing_base_info_lib modify organization_name VARCHAR(50) null comment '融资机构名称';

alter table fund_financing_plan modify financing_amount BIGINT(20) null comment '融资金额';
alter table fund_financing_plan_lib modify financing_amount BIGINT(20) null comment '融资金额';

alter table fund_financing_plan add column guarantee_info json default null comment '担保信息';
alter table fund_financing_plan_lib add column guarantee_info json default null comment '担保信息';

alter table fund_financing_pay_account add column account_category VARCHAR(50) null COMMENT '账户类别';
alter table fund_financing_pay_account_lib add column account_category VARCHAR(50) null COMMENT '账户类别';

-- 间融添加费用项
CREATE TABLE `fund_financing_fee_detail` (
                                             `id` bigint(20) NOT NULL COMMENT 'id' AUTO_INCREMENT,
                                             `financing_id` bigint(20) DEFAULT NULL COMMENT '融资id',
                                             `organization_id` BIGINT DEFAULT NULL COMMENT '融资机构id',
                                             `organization_name` varchar(100) DEFAULT NULL COMMENT '融资机构名称',
                                             `expense_type` varchar(50) DEFAULT NULL COMMENT '费用类型',
                                             `amount` bigint(20) DEFAULT NULL COMMENT '金额（万元）',
                                             `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
                                             `pay_date` datetime DEFAULT NULL COMMENT '支付时间',
                                             `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' ,
                                             `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=190 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='间融-费用明细';

CREATE TABLE `fund_financing_fee_detail_lib` (
                                                 `id` bigint(20) NOT NULL COMMENT 'id' AUTO_INCREMENT,
                                                 `financing_id` bigint(20) DEFAULT NULL COMMENT '融资id',
                                                 `organization_id` BIGINT DEFAULT NULL COMMENT '融资机构id',
                                                 `organization_name` varchar(100) DEFAULT NULL COMMENT '融资机构名称',
                                                 `expense_type` varchar(50) DEFAULT NULL COMMENT '费用类型',
                                                 `amount` bigint(20) DEFAULT NULL COMMENT '金额（万元）',
                                                 `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
                                                 `pay_date` datetime DEFAULT NULL COMMENT '支付时间',
                                                 `write_off_status` varchar(100) DEFAULT NULL COMMENT '核销状态',
                                                 `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' ,
                                                 `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                                 `version` varchar(40) NOT NULL COMMENT '版本号',
                                                 `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
                                                 `data_create_time` datetime DEFAULT NULL COMMENT '原数据创建时间',
                                                 `data_create_by` bigint(20) DEFAULT NULL COMMENT '原数据创建人',
                                                 `data_update_time` datetime DEFAULT NULL COMMENT '原数据更新时间',
                                                 `data_update_by` bigint(20) DEFAULT NULL COMMENT '原数据更新人',
                                                 `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
                                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=190 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='间融-费用明细';

alter table fund_receipt_repay_expense add column fee_id BIGINT(20) DEFAULT null comment '间融费用项id';
alter table fund_receipt_repay_expense_lib add column fee_id BIGINT(20) DEFAULT null comment '间融费用项id';

-- 机构补充字段
alter table fund_organization add column current_deposit_rate BIGINT(20) DEFAULT null comment '活期存款利率';
alter table fund_organization add column agreement_deposit_rate BIGINT(20) DEFAULT null comment '协定存款利率';
alter table fund_organization add column agreement_deposit_rate_due_time DATETIME DEFAULT null comment '协定存款利率到期日';

-- 融资授信中间表
CREATE TABLE `fund_financing_credit_ref` (
                                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                             `financing_id` bigint(20) NOT NULL COMMENT '间融合同id',
                                             `credit_id` bigint(20) NOT NULL COMMENT '授信id',
                                             `organization_id` bigint(20) NOT NULL COMMENT '机构id',
                                             `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                             `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=806 DEFAULT CHARSET=utf8mb4 COMMENT='融资授信关联表';

CREATE TABLE `fund_financing_credit_ref_lib` (
                                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                 `financing_id` bigint(20) NOT NULL COMMENT '间融合同id',
                                                 `credit_id` bigint(20) NOT NULL COMMENT '授信id',
                                                 `organization_id` bigint(20) NOT NULL COMMENT '机构id',
                                                 `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                                 `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                 `version` varchar(40) NOT NULL COMMENT '版本号',
                                                 `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
                                                 `data_create_time` datetime DEFAULT NULL COMMENT '原数据创建时间',
                                                 `data_create_by` bigint(20) DEFAULT NULL COMMENT '原数据创建人',
                                                 `data_update_time` datetime DEFAULT NULL COMMENT '原数据更新时间',
                                                 `data_update_by` bigint(20) DEFAULT NULL COMMENT '原数据更新人',
                                                 `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
                                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=806 DEFAULT CHARSET=utf8mb4 COMMENT='融资授信关联表-lib';


INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES
    ('2024-10-24 18:34:09', '2024-10-24 18:34:53', null, 'fundfinancingfeeadd', '间融-新增费用项', 0, 358, NULL, NULL, NULL, 'POST', '/fund/financing/fee/add', 2, NULL),
    ('2024-10-24 18:34:43', '2024-10-24 18:34:43', null, 'fundfinancingfeemodify', '间融-修改费用项', 0, 358, NULL, NULL, NULL, 'POST', '/fund/financing/fee/modify', 2, NULL),
    ('2024-10-24 18:35:30', '2024-10-24 18:35:30', null, 'fundfinancingfeelist', '间融-费用项列表', 0, 358, NULL, NULL, NULL, 'POST', '/fund/financing/fee/list', 1, NULL),
    ('2024-10-24 18:35:57', '2024-10-24 18:35:57', null, 'fundfinancingfeeremove', '间融-删除费用项', 0, 358, NULL, NULL, NULL, 'POST', '/fund/financing/fee/remove', 2, NULL),
    ('2024-10-24 18:36:58', '2024-10-24 18:36:58', null, 'fundcreditinvalid', '授信失效', 0, 148, NULL, NULL, NULL, 'POST', '/fund/credit/invalid', 2, NULL),
    ('2024-10-24 18:37:19', '2024-10-24 18:37:19', null, 'fundcreditlimitDetail', '使用详情', 0, 148, NULL, NULL, NULL, 'POST', '/fund/credit/limitDetail', 1, NULL),
    ('2024-10-24 18:38:40', '2024-10-24 18:38:40', null, 'fundguaranteeinfolimitDetail', '担保-额度使用详情', 0, 158, NULL, NULL, NULL, 'POST', '/fund/guarantee/info/limitDetail', 1, NULL),
    ('2024-10-24 18:40:47', '2024-10-24 18:40:47', null, 'funddirectfinancingcollectaccountadd', '新增直接融资-收款账户', 0, 490, NULL, NULL, NULL, 'POST', '/fund/direct/financing/collect/account/add', 2, NULL),
    ('2024-10-24 18:41:07', '2024-10-24 18:41:07', null, 'funddirectfinancingcollectaccountmodify', '修改直接融资-收款账户', 0, 490, NULL, NULL, NULL, 'POST', '/fund/direct/financing/collect/account/modify', 2, NULL),
    ('2024-10-24 18:41:29', '2024-10-24 18:41:29', null, 'funddirectfinancingcollectaccountlist', '直接融资-收款账户列表', 0, 490, NULL, NULL, NULL, 'POST', '/fund/direct/financing/collect/account/list', 1, NULL),
    ('2024-10-24 18:41:49', '2024-10-24 18:41:49', null, 'funddirectfinancingcollectaccountremove', '删除直接融资-收款账户', 0, 490, NULL, NULL, NULL, 'POST', '/fund/direct/financing/collect/account/remove', 2, NULL),
    ('2024-10-24 18:42:49', '2024-10-24 18:42:49', null, 'funddirectfinancingrepayactuallistcalculate', '直融-现金流测算', 0, 490, NULL, NULL, NULL, 'POST', '/fund/direct/financing/repay/actual/list/calculate', 2, NULL),
    ('2023-04-06 09:41:11', '2024-10-25 10:52:34', null, 'fundCreditLimitIndexDownload', '首页列表下载-授信使用详情', 0, 148, NULL, NULL, NULL, 'POST', '/index/download', 2, NULL),
    ('2023-04-06 09:41:11', '2024-10-25 10:53:15', null, 'fundDirectFinancingIndexDownload', '首页列表下载-直融', 0, 490, NULL, NULL, NULL, 'POST', '/index/download', 2, NULL),
    ('2023-04-06 09:41:11', '2024-10-25 10:52:57', null, 'fundGuaranteeAgencyLimitIndexDownload', '首页列表下载-担保使用详情', 0, 158, NULL, NULL, NULL, 'POST', '/index/download', 2, NULL),
    ('2024-10-31 14:37:55', '2024-10-31 14:41:52', null, 'businessFlowProjPayIndexDownload', '首页列表下载-项目端付款', 0, 495, NULL, NULL, NULL, 'POST', '/index/download', 1, NULL),
    ('2024-10-31 14:38:31', '2024-10-31 14:41:57', null, 'businessFlowProjCollectIndexDownload', '首页列表下载-项目端收款', 0, 495, NULL, NULL, NULL, 'POST', '/index/download', 1, NULL),
    ('2024-10-31 14:38:55', '2024-10-31 14:42:03', null, 'businessFlowFinancialPayIndexDownload', '首页列表下载-资金端付款', 0, 495, NULL, NULL, NULL, 'POST', '/index/download', 1, NULL),
    ('2024-10-31 14:39:07', '2024-10-31 14:42:05', null, 'businessFlowFinancialCollectIndexDownload', '首页列表下载-资金端收款', 0, 495, NULL, NULL, NULL, 'POST', '/index/download', 1, NULL);


update bifrost_menu set name = '间融管理' where code = 'financiaifund';

-- -------------------------------------资金数据订正----------------------------------------------

-- 1.北京银行股份有限公司杭州分行营业部 机构id=27 有两个生效授信42和10 将42的额度转移到10上
-- 将授信10额度设为十五亿,并更改授信编号 (原编号：DK202304140008)
update fund_credit set total_credit_limit = 15000000000000,credit_code = 'DK202304141008' where id = 10;
-- 将授信42设为失效
update fund_credit set effective = 0 where id = 42;
-- 将基于授信42创建的合同全引用到授信10上
update fund_financing_base_info set credit_id = 10 where credit_id = 42;

-- 2.中国银行股份有限公司杭州钱江新城支行 授信id=62 改为非循环授信
update fund_credit set recyclable = 0 where id = 62;

-- 3.初始化中间表（在定时任务修改表数据前执行）
insert into fund_financing_credit_ref (financing_id, credit_id, organization_id)
select id ,credit_id, organization_id from fund_financing_base_info;

-- 文件处理
-- 直融
update materials_list set materials_type = 'PROSPECTUS' where business_type = 'FUND_DIRECT_FINANCING' and materials_type = 'CONTRACT';
update materials_list_lib set materials_type = 'PROSPECTUS' where business_type = 'FUND_DIRECT_FINANCING' and materials_type = 'CONTRACT';

-- 间融
update materials_list a join (
    select
    b.id as id,
    CASE a.business_type
    WHEN 'BANK_ACCEPTANCE' THEN 'BANK_ACCEPTANCE_AGREEMENT'
    WHEN 'LETTER_OF_CREDIT' THEN 'DOMESTIC_CREDIT_FINANCING_AGREEMENT'
    WHEN 'FACTORING_FINANCING' THEN 'FACTORING_CONTRACT'
    WHEN 'WORKING_CAPITAL_LOAN' THEN 'CURRENT_LOAN_CONTRACT'
    WHEN 'PROJECT_LOAN' THEN 'CURRENT_LOAN_CONTRACT'
    WHEN 'COMMERCE_ACCEPTANCE' THEN 'ACCEPTANCE_DISCOUNT_AGREEMENT'
    END as type
    from fund_financing_base_info a
    join materials_list b on a.id = b.belong_id and b.business_type = 'FUND_FINANCING' and b.materials_type = 'CONTRACT'
    )b on a.id = b.id
    set a.materials_type = b.type;

update materials_list_lib a join (
    select
    b.id as id,
    CASE a.business_type
    WHEN 'BANK_ACCEPTANCE' THEN 'BANK_ACCEPTANCE_AGREEMENT'
    WHEN 'LETTER_OF_CREDIT' THEN 'DOMESTIC_CREDIT_FINANCING_AGREEMENT'
    WHEN 'FACTORING_FINANCING' THEN 'FACTORING_CONTRACT'
    WHEN 'WORKING_CAPITAL_LOAN' THEN 'CURRENT_LOAN_CONTRACT'
    WHEN 'PROJECT_LOAN' THEN 'CURRENT_LOAN_CONTRACT'
    WHEN 'COMMERCE_ACCEPTANCE' THEN 'ACCEPTANCE_DISCOUNT_AGREEMENT'
    END as type
    from fund_financing_base_info a
    join materials_list_lib b on a.id = b.belong_id and b.business_type = 'FUND_FINANCING' and b.materials_type = 'CONTRACT'
    )b on a.id = b.id
    set a.materials_type = b.type;

-- 初始化计划现金流 - 融资款
insert into `fund_receipt_flow_plan` (`receipt_repay_id`, `cash_flow_phase`, `cash_flow_code`, `cash_flow_item`, `cash_flow_date`, `total_amount`, `principal_amount`, `interest_amount`, `write_off_state`)
select `receipt_repay_id`, 0, `cash_flow_code`, 'FINANCE_FUND', `actual_loan_date`, `principal`, null, null, ifnull(`write_off_state`,'NO_WRITE_OFF') from `fund_receipt_repay_borrowing`;
-- 初始化计划现金流 - 保证金
insert into `fund_receipt_flow_plan` (`receipt_repay_id`, `cash_flow_phase`, `cash_flow_code`, `cash_flow_item`, `cash_flow_date`, `total_amount`, `principal_amount`, `interest_amount`, `write_off_state`)
select b.`receipt_repay_id`, 0, b.`cash_flow_code`, b.`deposit_cash_flow_type`, a.`actual_loan_date`, b.`amount`, null, null, ifnull(b.`write_off_state`,'NO_WRITE_OFF')
from `fund_receipt_repay_borrowing` as a inner join `fund_receipt_repay_cash_deposit` as b on a.`receipt_repay_id` = b.`receipt_repay_id`;
insert into `fund_receipt_flow_plan` (`receipt_repay_id`, `cash_flow_phase`, `cash_flow_code`, `cash_flow_item`, `cash_flow_date`, `total_amount`, `principal_amount`, `interest_amount`, `write_off_state`, `remark`)
-- 初始化计划现金流 - 费用项
select b.`receipt_repay_id`, 0, b.`cash_flow_code`, b.`expense_type`, a.`actual_loan_date`, b.`total_amount`, null, null, ifnull(b.`write_off_state`,'NO_WRITE_OFF'), b.`remark`
from `fund_receipt_repay_borrowing` as a inner join `fund_receipt_repay_expense` as b on a.`receipt_repay_id` = b.`receipt_repay_id`;
-- 初始化计划现金流 - 还本付息
insert into `fund_receipt_flow_plan` (`receipt_repay_id`, `cash_flow_phase`, `cash_flow_code`, `cash_flow_item`, `cash_flow_date`, `total_amount`, `principal_amount`, `interest_amount`, `write_off_state`)
select `receipt_repay_id`, `phase`, `cash_flow_code`, 'REPAY', `repay_date`, ifnull(`repay_amount`,0), ifnull(`principle_amount`,0), ifnull(`interest_amount`,0), ifnull(`write_off_state`,'NO_WRITE_OFF')
from `fund_receipt_repay_cash_flow`;
-- 初始化现金流类型元数据
insert into `system_cash_flow_meta`
(`category`, `fund_flow`, `cash_flow_item_code`, `cash_flow_item_display`)
values
    ('FUND', 'IN', 'FINANCE_FUND', '融资款'),
    ('FUND', 'OUT', 'DEPOSIT_PAYMENT', '保证金付款'),
    ('FUND', 'IN', 'DEPOSIT_RETURN', '保证金退款'),
    ('FUND', 'OUT', 'REPAY', '还本付息'),
    ('FUND', 'OUT', 'AUDIT_FEE', '审计费'),
    ('FUND', 'OUT', 'LOAN_SERVICE_FEE', '贷款服务费'),
    ('FUND', 'OUT', 'CREDIT_ASSESSMENT_FEE', '信用评估费'),
    ('FUND', 'OUT', 'FINANCIAL_ADVISORY_FEE', '财务顾问费'),
    ('FUND', 'OUT', 'GUARANTEE_FEE', '担保费'),
    ('FUND', 'OUT', 'CUSTODY_FEE', '托管费'),
    ('FUND', 'OUT', 'OPEN_LICENSE_FEE', '开证许可证费'),
    ('FUND', 'OUT', 'FACTORING_FEE', '保理手续费');
----------------------------- 资金sql end ------------------------------------------------

------------------------------ BigBear SQL Start -----------------------------------------
-- =====================================项目批复金额开始===================================================
ALTER TABLE group_credit_review_base_info ADD COLUMN project_approval_amount BIGINT(20) NULL COMMENT '项目批复金额';
ALTER TABLE group_credit_review_base_info_lib ADD COLUMN project_approval_amount BIGINT(20) NULL COMMENT '项目批复金额';
ALTER TABLE proj_review_aoc_price ADD COLUMN project_approval_amount BIGINT(20) NULL COMMENT '项目批复金额';
ALTER TABLE proj_review_aoc_price_lib ADD COLUMN project_approval_amount BIGINT(20) NULL COMMENT '项目批复金额';
ALTER TABLE proj_review_lease_price ADD COLUMN project_approval_amount BIGINT(20) NULL COMMENT '项目批复金额';
ALTER TABLE proj_review_lease_price_lib ADD COLUMN project_approval_amount BIGINT(20) NULL COMMENT '项目批复金额';
ALTER TABLE proj_review_factoring_price ADD COLUMN project_approval_amount BIGINT(20) NULL COMMENT '项目批复金额';
ALTER TABLE proj_review_factoring_price_lib ADD COLUMN project_approval_amount BIGINT(20) NULL COMMENT '项目批复金额';
-- =====================================项目批复金额结束===================================================
------------------------------ BigBear SQL End -------------------------------------------
