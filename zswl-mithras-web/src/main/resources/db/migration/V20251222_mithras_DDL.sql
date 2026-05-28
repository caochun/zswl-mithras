
-- 减值上迁
CREATE TABLE `ecl_execute_client_promotion_result`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT,
    `contract_id` bigint(20) default null comment '合同id',
    `interval_month` int(11) DEFAULT NULL COMMENT '间隔',
    `conclusion` tinyint(2) DEFAULT 0 COMMENT '结果0不满足，1满足',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间-测算时间',
    `create_by`               bigint(20)          DEFAULT NULL COMMENT '创建人id',
    `update_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`               bigint(20)          DEFAULT NULL COMMENT '修改人id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='资产减值客户是否上迁记录表';

alter table ecl_execute_record add column promotion_result tinyint(2) DEFAULT 0 COMMENT '结果0不满足，1满足';
alter table ecl_execute_record_lib add column promotion_result tinyint(2) DEFAULT 0 COMMENT '结果0不满足，1满足';

alter table ecl_execute_predict_record add column promotion_result tinyint(2) DEFAULT 0 COMMENT '结果0不满足，1满足';
alter table ecl_execute_predict_record add column promotion_result_handle tinyint(2) DEFAULT 0 COMMENT '手工结果0不满足，1满足';


-- 征信解析
drop table if exists credit_report_base_info;
CREATE TABLE `credit_report_base_info`
(
    `id`                       bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_code`              varchar(50)          DEFAULT NULL COMMENT '查询编号',
    `apply_org`                varchar(255)         DEFAULT NULL COMMENT '申请部门',
    `apply_status`             varchar(255)         DEFAULT NULL COMMENT '申请状态，同流程审批状态，枚举值：未提交、审批中、审批通过、审批拒绝、已关闭',
    `apply_time`               datetime             DEFAULT NULL COMMENT '申请通过时间',
    `select_status`            varchar(30)           DEFAULT NULL COMMENT '查询状态',
    `select_time`              datetime             DEFAULT NULL COMMENT '查询完成时间',
    `select_version`           varchar(50) NOT NULL COMMENT '查询版本,默认展示为企业信用报告(授信机构版)',
    `report_format`            varchar(50) NOT NULL COMMENT '信用报告封装格式,默认展示为“html格式”',
    `proj_code`                varchar(50)          DEFAULT NULL COMMENT '关联项目编号',
    `proj_name`                varchar(255)         DEFAULT NULL COMMENT '关联项目名称',
    `proj_id`                  bigint(20) DEFAULT NULL COMMENT '关联项目id',
    `proj_id_data_type`        varchar(30) NOT NULL DEFAULT '' COMMENT 'proj_id的数据类型',
    `authorization_began_date` date                 DEFAULT NULL COMMENT '授信开始时间',
    `authorization_end_date`   date                 DEFAULT NULL COMMENT '授信结束时间',
    `update_by`                bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `create_by`                bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`              datetime             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`              datetime             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                  tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='征信报告基本信息表';

drop table if exists credit_report_base_info_lib;
CREATE TABLE `credit_report_base_info_lib`
(
    `id`                       bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_code`              varchar(50)          DEFAULT NULL COMMENT '查询编号',
    `apply_org`                varchar(255)         DEFAULT NULL COMMENT '申请部门',
    `apply_status`             varchar(255)         DEFAULT NULL COMMENT '申请状态，同流程审批状态，枚举值：未提交、审批中、审批通过、审批拒绝、已关闭',
    `apply_time`               datetime             DEFAULT NULL COMMENT '申请通过时间',
    `select_status`            varchar(30)           DEFAULT NULL COMMENT '查询状态',
    `select_time`              datetime             DEFAULT NULL COMMENT '查询完成时间',
    `select_version`           varchar(50) NOT NULL COMMENT '查询版本,默认展示为企业信用报告(授信机构版)',
    `report_format`            varchar(50) NOT NULL COMMENT '信用报告封装格式,默认展示为“html格式”',
    `proj_code`                varchar(50)          DEFAULT NULL COMMENT '关联项目编号',
    `proj_name`                varchar(255)         DEFAULT NULL COMMENT '关联项目名称',
    `proj_id`                  bigint(20) DEFAULT NULL COMMENT '关联项目id',
    `proj_id_data_type`        varchar(30) NOT NULL DEFAULT '' COMMENT 'proj_id的数据类型',
    `authorization_began_date` date                 DEFAULT NULL COMMENT '授信开始时间',
    `authorization_end_date`   date                 DEFAULT NULL COMMENT '授信结束时间',
    `update_by`                bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `create_by`                bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`              datetime             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`              datetime             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                  tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `version`          varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`        bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime    DEFAULT NULL,
    `data_create_by`   bigint(20) unsigned DEFAULT NULL,
    `data_update_time` datetime    DEFAULT NULL,
    `data_update_by`   bigint(20) DEFAULT NULL,
    `repay_rate`       varchar(20) DEFAULT NULL COMMENT '还款频率',
    `version_type`     tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='征信报告基本信息版本表';


drop table if exists credit_report_client_item;
CREATE TABLE `credit_report_client_item`
(
    `id`                         bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_report_base_info_id` bigint(20) DEFAULT NULL COMMENT '征信报告基本信息表id',
    `client_id`                  bigint(20) DEFAULT NULL COMMENT '关联客户id',
    `client_name`                varchar(50)  DEFAULT NULL COMMENT '客户名称',
    `csc_code`                   varchar(50)  DEFAULT NULL COMMENT '统一社会信用代码',
    `zhong_zheng_code`           varchar(50)  DEFAULT NULL COMMENT '中征码',
    `select_goal`                varchar(50)  DEFAULT NULL COMMENT '查询目的,枚举值：贷前（保前）审查、贷后（在保）管理、贷中操作、关联查询',
    `select_status`            varchar(30)           DEFAULT NULL COMMENT '查询状态',
    `select_time`              datetime             DEFAULT NULL COMMENT '查询完成时间',
    `select_error_code`          varchar(200) DEFAULT NULL COMMENT '失败原因编号',
    `select_error_reason`        varchar(200) DEFAULT NULL COMMENT '失败原因',
    `archive_id`                 varchar(200) DEFAULT NULL COMMENT '档案编号',
    `serial_number`              varchar(200) DEFAULT NULL COMMENT '查询交易流水号',
    `update_by`                  bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `create_by`                  bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`                datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                    tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='征信报告客户表';


drop table if exists credit_report_client_item_lib;
CREATE TABLE `credit_report_client_item_lib`
(
    `id`                         bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_report_base_info_id` bigint(20) DEFAULT NULL COMMENT '征信报告基本信息表id',
    `client_id`                  bigint(20) DEFAULT NULL COMMENT '关联客户id',
    `client_name`                varchar(50)  DEFAULT NULL COMMENT '客户名称',
    `csc_code`                   varchar(50)  DEFAULT NULL COMMENT '统一社会信用代码',
    `zhong_zheng_code`           varchar(50)  DEFAULT NULL COMMENT '中征码',
    `select_goal`                varchar(50)  DEFAULT NULL COMMENT '查询目的,枚举值：贷前（保前）审查、贷后（在保）管理、贷中操作、关联查询',
    `select_status`            varchar(30)           DEFAULT NULL COMMENT '查询状态',
    `select_time`              datetime             DEFAULT NULL COMMENT '查询完成时间',
    `select_error_code`          varchar(200) DEFAULT NULL COMMENT '失败原因编号',
    `select_error_reason`        varchar(200) DEFAULT NULL COMMENT '失败原因',
    `archive_id`                 varchar(200) DEFAULT NULL COMMENT '档案编号',
    `serial_number`              varchar(200) DEFAULT NULL COMMENT '查询交易流水号',
    `update_by`                  bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `create_by`                  bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`                datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                    tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `version`          varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`        bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime    DEFAULT NULL,
    `data_create_by`   bigint(20) unsigned DEFAULT NULL,
    `data_update_time` datetime    DEFAULT NULL,
    `data_update_by`   bigint(20) DEFAULT NULL,
    `repay_rate`       varchar(20) DEFAULT NULL COMMENT '还款频率',
    `version_type`     tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='征信报告客户版本表';

drop table if exists credit_report_summary;
CREATE TABLE `credit_report_summary`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_code`       bigint(20)                   DEFAULT NULL COMMENT '查询编号',
    `credit_report_id`       bigint(20)                   DEFAULT NULL COMMENT '征信报告基本表id',
    `first_creditYear` varchar(50) DEFAULT NULL COMMENT '首次有信贷交易年份',
    `credit_organization_number` int(11) DEFAULT NULL COMMENT '信贷交易机构数',
    `unsettled_credit_organization_number` int(11) DEFAULT NULL COMMENT '未结清信贷交易机构数',
    `first_repayment_responsibility_year` varchar(20) DEFAULT NULL COMMENT '首次有相关还款责任的年份',
    `loan_transaction_balance` decimal(20, 5) DEFAULT NULL COMMENT '借贷交易-余额',
    `loan_transaction_recovery_balance` decimal(20, 5) DEFAULT NULL COMMENT '借贷交易-被追偿余额',
    `loan_transaction_focus_balance` decimal(20, 5) DEFAULT NULL COMMENT '借贷交易-关注类余额',
    `loan_transaction_bad_balance` decimal(20, 5) DEFAULT NULL COMMENT '借贷交易-不良类余额',
    `guarantee_transaction_balance` decimal(20, 5) DEFAULT NULL COMMENT '担保交易-余额',
    `guarantee_transaction_focus_balance` decimal(20, 5) DEFAULT NULL COMMENT '担保交易-关注类余额',
    `guarantee_transaction_bad_balance` decimal(20, 5) DEFAULT NULL COMMENT '担保交易-不良类余额',
    `non_credit_transaction_number` int(11) DEFAULT NULL COMMENT '非信贷交易账户数',
    `tax_arrears_records_number` int(11) DEFAULT NULL COMMENT '欠税记录条数',
    `civil_judgment_records_number` int(11) DEFAULT NULL COMMENT '民事判决记录条数',
    `mandatory_execution_records_number` int(11) DEFAULT NULL COMMENT '强制执行记录条数',
    `administrative_penalty_records_number` int(11) DEFAULT NULL COMMENT '行政处罚记录条数',
    `create_by`         bigint(20)                   DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`       datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         bigint(20)                   DEFAULT NULL COMMENT '更新人',
    `update_time`       datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           tinyint(4)          NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='征信报告-信息概要表';


drop table if exists credit_report_unsettled_summary;

CREATE TABLE `credit_report_unsettled_summary`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_code`       bigint(20)                   DEFAULT NULL COMMENT '查询编号',
    `credit_report_id`       bigint(20)                   DEFAULT NULL COMMENT '征信报告基本表id',
    `payment_module` varchar(50) DEFAULT NULL COMMENT '款项模块',
    `payment_type` varchar(50) DEFAULT NULL COMMENT '款项类型-短期-贴现',
    `fund_classification` varchar(50) DEFAULT NULL COMMENT '款项分类-正常，关注-不良-合计',
    `account_number` int(11) DEFAULT NULL COMMENT '账户数',
    `account_amount` decimal(20, 5) DEFAULT NULL COMMENT '账户余额',
    `create_by`         bigint(20)                   DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`       datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         bigint(20)                   DEFAULT NULL COMMENT '更新人',
    `update_time`       datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           tinyint(4)          NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='征信报告-未结清信贷及授信信息表';

drop table if exists credit_report_repayment_responsibility;
CREATE TABLE `credit_report_repayment_responsibility`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_code`       bigint(20)                   DEFAULT NULL COMMENT '查询编号',
    `credit_report_id`       bigint(20)                   DEFAULT NULL COMMENT '征信报告基本表id',
    `responsibility_type` varchar(50) DEFAULT NULL COMMENT '责任类型',
    `recoverable_repayment_responsibility_amount` decimal(20, 5) DEFAULT NULL COMMENT '被追偿业务-还款责任金额',
    `recoverable_account_number` decimal(20, 5) DEFAULT NULL COMMENT '被追偿业务-账户数',
    `recoverable_balance` decimal(20, 5) DEFAULT NULL COMMENT '被追偿业务-余额',
    `other_repayment_responsibility_amount` decimal(20, 5) DEFAULT NULL COMMENT '其他借贷交易-还款责任金额',
    `other_account_number` decimal(20, 5) DEFAULT NULL COMMENT '其他借贷交易-账户数',
    `other_balance` decimal(20, 5) DEFAULT NULL COMMENT '其他借贷交易-余额',
    `other_focus_balance` decimal(20, 5) DEFAULT NULL COMMENT '其他借贷交易-关注类余额',
    `other_bad_balance` decimal(20, 5) DEFAULT NULL COMMENT '其他借贷交易-不良类余额',
    `create_by`         bigint(20)                   DEFAULT NULL COMMENT '创建人、发起人',
    `update_by`         bigint(20)                   DEFAULT NULL COMMENT '更新人',
    `create_time`       datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           tinyint(4)          NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='征信报告-相关还款责任信息概要表';


drop table if exists credit_report_repayment_responsibility;

CREATE TABLE `credit_report_repayment_responsibility`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_code`       bigint(20)                   DEFAULT NULL COMMENT '查询编号',
    `credit_report_id`       bigint(20)                   DEFAULT NULL COMMENT '征信报告基本表id',
    `responsibility_type` varchar(50) DEFAULT NULL COMMENT '责任类型',
    `recoverable_repayment_responsibility_amount` decimal(20, 5) DEFAULT NULL COMMENT '被追偿业务-还款责任金额',
    `recoverable_account_number` decimal(20, 5) DEFAULT NULL COMMENT '被追偿业务-账户数',
    `recoverable_balance` decimal(20, 5) DEFAULT NULL COMMENT '被追偿业务-余额',
    `other_repayment_responsibility_amount` decimal(20, 5) DEFAULT NULL COMMENT '其他借贷交易-还款责任金额',
    `other_account_number` decimal(20, 5) DEFAULT NULL COMMENT '其他借贷交易-账户数',
    `other_balance` decimal(20, 5) DEFAULT NULL COMMENT '其他借贷交易-余额',
    `other_focus_balance` decimal(20, 5) DEFAULT NULL COMMENT '其他借贷交易-关注类余额',
    `other_bad_balance` decimal(20, 5) DEFAULT NULL COMMENT '其他借贷交易-不良类余额',
    `create_by`         bigint(20)                   DEFAULT NULL COMMENT '创建人、发起人',
    `update_by`         bigint(20)                   DEFAULT NULL COMMENT '更新人',
    `create_time`       datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           tinyint(4)          NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='征信报告-相关还款责任信息概要表';


drop table if exists credit_report_record_details;
CREATE TABLE `credit_report_record_details`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_code`       bigint(20)                   DEFAULT NULL COMMENT '查询编号',
    `credit_report_id`       bigint(20)                   DEFAULT NULL COMMENT '征信报告基本表id',
    `account_number` varchar(50) DEFAULT NULL COMMENT '账户编号',
    `creditor_institution` varchar(50) DEFAULT NULL COMMENT '债权机构',
    `business_type` varchar(50) DEFAULT NULL COMMENT '业务种类',
    `opening_date` varchar(50) DEFAULT NULL COMMENT '开立日期',
    `expiration_date` varchar(50) DEFAULT NULL COMMENT '到期日',
    `currency` varchar(50) DEFAULT NULL COMMENT '币种',
    `loan_amount` varchar(50) DEFAULT NULL COMMENT '借款金额',
    `distribution_method` varchar(50) DEFAULT NULL COMMENT '发放形式',
    `guarantee_method` varchar(50) DEFAULT NULL COMMENT '担保方式',
    `balance` varchar(50) DEFAULT NULL COMMENT '余额',
    `five_classification` varchar(50) DEFAULT NULL COMMENT '五级分类',
    `total_overdue_amount` varchar(50) DEFAULT NULL COMMENT '逾期总额',
    `overdue_principal` varchar(50) DEFAULT NULL COMMENT '逾期本金',
    `overdue_month` int(11) DEFAULT NULL COMMENT '逾期月数',
    `last_repayment_date` varchar(50) DEFAULT NULL COMMENT '最近一次还款日期',
    `last_repayment_amount` varchar(50) DEFAULT NULL COMMENT '最近一次还款总额',
    `last_repayment_type` varchar(50) DEFAULT NULL COMMENT '最近一次还款形式',
    `specific_transaction_prompts` varchar(50) DEFAULT NULL COMMENT '特定交易提示',
    `credit_agreement_number` varchar(50) DEFAULT NULL COMMENT '授信协议编号',
    `information_report_date` varchar(50) DEFAULT NULL COMMENT '信息报告日期',
    `create_by`         bigint(20)                   DEFAULT NULL COMMENT '创建人、发起人',
    `update_by`         bigint(20)                   DEFAULT NULL COMMENT '更新人',
    `create_time`       datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           tinyint(4)          NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='征信报告-信贷记录明细表';


drop table if exists credit_report_limit;
CREATE TABLE `credit_report_limit`
(
    `id`                               bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_code`                      bigint(20) DEFAULT NULL COMMENT '查询编号',
    `credit_report_id`                 bigint(20) DEFAULT NULL COMMENT '征信报告基本表id',
    `total_amount`                     decimal(20, 5) DEFAULT NULL COMMENT '非循环-总额',
    `used_amount`                      decimal(20, 5) DEFAULT NULL COMMENT '非循环-已用额度',
    `remaining_available_amount`       decimal(20, 5) DEFAULT NULL COMMENT '非循环-剩余可用额度',
    `cycle_total_amount`               decimal(20, 5) DEFAULT NULL COMMENT '循环-已用额度',
    `cycle_used_amount`                decimal(20, 5) DEFAULT NULL COMMENT '循环-已用额度',
    `cycle_remaining_available_amount` decimal(20, 5) DEFAULT NULL COMMENT '循环-已用额度',
    `create_by`                        bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `update_by`         bigint(20)                   DEFAULT NULL COMMENT '更新人',
    `create_time`                      datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`                      datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                          tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='征信报告-信用额度表';


alter table credit_report_summary add column `credit_report_client_id`                 bigint(20) DEFAULT NULL COMMENT '征信报告客户表id';
alter table credit_report_unsettled_summary add column `credit_report_client_id`                 bigint(20)
                                      DEFAULT NULL COMMENT '征信报告客户表id';
alter table credit_report_repayment_responsibility add column `credit_report_client_id`                 bigint(20) DEFAULT NULL COMMENT '征信报告客户表id';
alter table credit_report_record_details add column `credit_report_client_id`
    bigint(20) DEFAULT NULL COMMENT '征信报告客户表id';
alter table credit_report_limit add column `credit_report_client_id`
    bigint(20) DEFAULT NULL COMMENT '征信报告客户表id';



