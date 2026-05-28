-- 流水反核销
alter table exception_request_info
    add column source varchar(50) null comment '来源，用于标识该次请求调用模块';
alter table exception_request_info
    add column business_key varchar(200) null comment '业务主建';
alter table exception_request_info
    add column business_title varchar(255) null comment '业务模块用于存放页面展示信息，用于帮助业务区分记录';
alter table exception_request_info
    add column withdraw_flag tinyint(1) null comment '接口撤回标识 0已撤回， 1 撤回失败';
alter table exception_request_info
    add column withdraw_fail_message varchar(255) null comment '业务主建';

ALTER TABLE `collection_record_info`
    ADD COLUMN `deleted` TINYINT(1) NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除，默认0';

ALTER TABLE `payment_actual_detail`
    ADD COLUMN `deleted` TINYINT(1) NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除，默认0';

ALTER TABLE `margin_record_info`
    ADD COLUMN `deleted` TINYINT(1) NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除，默认0';

ALTER TABLE `finance_flow_write_off_detail`
    ADD COLUMN `deleted` TINYINT(1) NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除，默认0';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('thirdFinancialWithdraw', '反核销数据', 0, 495, null, null, null, 'POST', '/third/financial/withdraw', 2, null);

-- ====================================大熊的SQL BEGIN===============================================
CREATE TABLE `monthly_finance_stamp_duty` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `main_id` bigint(20) DEFAULT NULL COMMENT '关联主表ID',
    `financing_id` bigint(20) DEFAULT NULL COMMENT '融资id',
    `type` varchar(255) DEFAULT NULL COMMENT '融资类型',
    `organization_name` varchar(255) DEFAULT NULL COMMENT '融资渠道',
    `financing_code` varchar(255) DEFAULT NULL COMMENT '融资编号',
    `stamp_duty` bigint(20) DEFAULT NULL COMMENT '本月计提印花税/元',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `source_id` bigint(20) DEFAULT NULL COMMENT '源数据ID',
    `is_confirmed` tinyint(4) DEFAULT NULL COMMENT '收入是否已确认',
    `is_send_cq` tinyint(4) DEFAULT '0' COMMENT '是否推送，默认0未推送',
    `is_effect` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否激活，默认1激活',
    `batch_number` varchar(255) DEFAULT NULL COMMENT '批次号',
    `new_update` tinyint(4) DEFAULT '0' COMMENT '是否已经更新',
    PRIMARY KEY (`id`),
    KEY `idx_main_id_deleted` (`main_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月结管理资金端印花税';

CREATE TABLE `monthly_management_air_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `main_id` bigint(20) NOT NULL COMMENT '月结管理主表记录ID',
    `receipt_id` bigint(20) unsigned DEFAULT NULL COMMENT '借据ID',
    `receipt_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '借据编号',
    `contract_id` bigint(20) unsigned DEFAULT NULL COMMENT '合同ID',
    `proj_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目名称',
    `contract_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '合同编号',
    `client_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客户名称',
    `lease_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租赁类型',
    `biz_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类型',
    `tax_rate` bigint(20) DEFAULT NULL COMMENT '税率',
    `income_sum` bigint(20) DEFAULT NULL COMMENT '本月收入金额（含税）',
    `income_without_tax_sum` bigint(20) DEFAULT NULL COMMENT '本月收入金额（不含税）',
    `overdue_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前是否逾期',
    `is_effect` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否激活，默认1激活',
    `actual_lease_date` datetime DEFAULT NULL COMMENT '实际起租日',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `source_id` bigint(20) DEFAULT NULL COMMENT '源数据ID',
    `is_confirmed` tinyint(4) DEFAULT '0' COMMENT '收入是否已确认',
    `is_send_cq` tinyint(4) DEFAULT '0' COMMENT '是否推送，默认0未推送',
    `batch_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '批次号',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
    `new_update` tinyint(4) DEFAULT '0' COMMENT '是否已经更新',
    PRIMARY KEY (`id`),
    KEY `idx_main_id_deleted` (`main_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实际利率法-记录表';

CREATE TABLE `monthly_management_base_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `main_id` bigint(20) DEFAULT NULL COMMENT '记录ID，配合逻辑删除唯一',
    `year` int(11) NOT NULL COMMENT '年份',
    `month` int(11) NOT NULL COMMENT '月份',
    `air_count` bigint(20) DEFAULT NULL COMMENT '实际利率法(含税)',
    `air_count_exclude_tax` bigint(20) DEFAULT NULL COMMENT '实际利率法(不含税)',
    `rp_count` bigint(20) DEFAULT NULL COMMENT '剩余本金法(含税)',
    `rp_count_exclude_tax` bigint(20) DEFAULT NULL COMMENT '剩余本金法(不含税)',
    `cost_count` bigint(20) DEFAULT NULL COMMENT '当期计提成本(含税)',
    `cost_count_exclude_tax` bigint(20) DEFAULT NULL COMMENT '当期计提成本(不含税)',
    `stamp_duty_count` bigint(20) DEFAULT NULL COMMENT '印花税',
    `confirm_date` date DEFAULT NULL COMMENT '确认日期',
    `status` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态 {@link MonthlyManagementStatusEnum}',
    `close_date` datetime DEFAULT NULL COMMENT '关账日期',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_main_id_deleted` (`main_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='月结管理主表';

CREATE TABLE `monthly_management_cost_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一标识符，主键',
    `main_id` bigint(20) NOT NULL COMMENT '月结管理主表记录ID',
    `financing_id` bigint(20) DEFAULT NULL COMMENT '融资交易的ID',
    `financing_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '融资交易的唯一编号',
    `year_and_month` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '相关月份',
    `organization_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '融资渠道或机构名称',
    `financing_amount` bigint(20) DEFAULT NULL COMMENT '融资总金额（单位：元）',
    `remaining_amount` bigint(20) DEFAULT NULL COMMENT '融资剩余金额（单位：元）',
    `business_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类型',
    `financing_rate` int(11) DEFAULT NULL COMMENT '融资年利率',
    `daily_rate` int(11) DEFAULT NULL COMMENT '日利率',
    `total_capital_cost` bigint(20) DEFAULT NULL COMMENT '累计计提的资金成本',
    `total_capital_cost_after_tax` bigint(20) DEFAULT NULL COMMENT '累计计提的资金成本（税后）',
    `term_capital_cost` bigint(20) DEFAULT NULL COMMENT '当期应付利息',
    `term_capital_cost_after_tax` bigint(20) DEFAULT NULL COMMENT '当期应付利息（税后）',
    `property_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质押资产类型',
    `financing_cost` bigint(20) DEFAULT NULL COMMENT '当日应付利息',
    `value_date` date DEFAULT NULL COMMENT '起息日',
    `property_type_display` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质押资产类型的展示名称',
    `loan_property` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '借款性质',
    `is_confirmed` int(11) DEFAULT '0' COMMENT '收入是否已确认',
    `is_effect` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否激活，默认1激活',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `source_id` bigint(20) DEFAULT NULL COMMENT '源数据ID',
    `is_send_cq` tinyint(4) DEFAULT '0' COMMENT '是否推送，默认0未推送',
    `batch_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '批次号',
    `new_update` tinyint(4) DEFAULT '0' COMMENT '是否已经更新',
    PRIMARY KEY (`id`),
    KEY `idx_main_id_deleted` (`main_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成本计提-记录表';

CREATE TABLE `monthly_management_rp_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一标识符，主键',
    `main_id` bigint(20) NOT NULL COMMENT '月结管理主表记录ID',
    `receipt_id` bigint(20) DEFAULT NULL COMMENT '关联的借据ID',
    `receipt_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '借据的唯一编号',
    `contract_id` bigint(20) DEFAULT NULL COMMENT '关联的合同ID',
    `proj_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目名称',
    `contract_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '合同的唯一编号',
    `client_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客户名称',
    `lease_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租赁类型',
    `biz_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类型',
    `tax_rate` int(11) DEFAULT NULL COMMENT '适用的税率',
    `income_sum` bigint(20) DEFAULT NULL COMMENT '本月总收入金额（含税）',
    `income_without_tax_sum` bigint(20) DEFAULT NULL COMMENT '本月总收入金额（不含税）',
    `overdue_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '当前是否处于逾期状态',
    `actual_lease_date` datetime DEFAULT NULL COMMENT '实际开始租赁的日期',
    `the_latest_full_refund_rent_period` int(11) DEFAULT NULL COMMENT '最近一次全额偿还租金的期次',
    `the_latest_full_refund_rent_date` datetime DEFAULT NULL COMMENT '最近一次全额偿还租金的应收款日期',
    `the_latest_full_refund_rent_capital` bigint(20) DEFAULT NULL COMMENT '最近一次全额偿还租金后剩余的本金',
    `contract_nominal_interest_rate` int(11) DEFAULT NULL COMMENT '合同约定的名义利率',
    `is_effect` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否激活，默认1激活',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `source_id` bigint(20) DEFAULT NULL COMMENT '源数据ID',
    `is_confirmed` tinyint(4) DEFAULT '0' COMMENT '收入是否已确认',
    `is_send_cq` tinyint(4) DEFAULT '0' COMMENT '是否推送，默认0未推送',
    `batch_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '批次号',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
    `new_update` tinyint(4) DEFAULT '0' COMMENT '是否已经更新',
    PRIMARY KEY (`id`),
    KEY `idx_main_id_deleted` (`main_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='剩余本金法-记录表';

CREATE TABLE `monthly_proj_stamp_duty` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '唯一标识符',
    `main_id` bigint(20) DEFAULT NULL COMMENT '关联主表ID',
    `client_name` varchar(255) NOT NULL COMMENT '客户名称',
    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同ID',
    `contract_code` varchar(50) DEFAULT NULL COMMENT '合同编号',
    `stamp_duty` bigint(20) DEFAULT NULL COMMENT '本月计提印花税',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `source_id` bigint(20) DEFAULT NULL COMMENT '源数据ID',
    `is_confirmed` tinyint(4) DEFAULT '0' COMMENT '收入是否已确认',
    `is_send_cq` tinyint(4) DEFAULT '0' COMMENT '是否推送，默认0未推送',
    `is_effect` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否激活，默认1激活',
    `batch_number` varchar(255) DEFAULT NULL COMMENT '批次号',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
    `new_update` tinyint(4) DEFAULT '0' COMMENT '是否已经更新',
    PRIMARY KEY (`id`),
    KEY `idx_main_id_deleted` (`main_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月结管理项目端印花税';

ALTER TABLE monthly_stamp_duty ADD COLUMN client_id bigint(20) null comment '客户ID';

-- 功能注册 生产
-- 功能注册
INSERT INTO bifrost_function (code, name, sort_no, menu_id, en_name, method, path, type, group_id)
VALUES ('monthlyUpdateSingle', '更新单条数据', 0, 739, null, 'POST', '/monthly/update/single', 2, null),
       ('monthlyPushSingle', '月结推送单条数据到苍穹', 0, 739, null, 'POST', '/monthly/push/single', 2, null),
       ('monthlyUpdateStatus', '月结管理更新单条数据状态', 0, 739, null, 'POST', '/monthly/update/status', 2, null),
       ('monthlyFresh', '月结管理刷新数据', 0, 739, null, 'POST', '/monthly/fresh', 2, null),
       ('monthlyCloseValidate', '关账校验', 0, 739, null, 'POST', '/monthly/close/validate', 2, null),
       ('monthlyClose', '月结关账', 0, 739, null, 'POST', '/monthly/close', 2, null),
       ('monthlyBaseAdd', '新增月结', 0, 739, null, 'POST', '/monthly/base/add', 2, null);

-- ====================================大熊的SQL END=================================================

