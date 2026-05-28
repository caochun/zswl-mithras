CREATE TABLE `credit_report_summary`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_code`       bigint(20)                   DEFAULT NULL COMMENT '查询编号',
    `credit_report_id`       bigint(20)                   DEFAULT NULL COMMENT '征信报告基本表id',
    `first_creditYear` int(11) DEFAULT NULL COMMENT '首次有信贷交易年份',
    `credit_organization_number` int(11) DEFAULT NULL COMMENT '信贷交易机构数',
    `unsettled_credit_organization_number` int(11) DEFAULT NULL COMMENT '未结清信贷交易机构数',
    `first_repayment_responsibility_year` int(11) DEFAULT NULL COMMENT '首次有相关还款责任的年份',
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
    `update_time`       datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           tinyint(4)          NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='征信报告-信息概要表';



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
    `update_time`       datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           tinyint(4)          NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='征信报告-未结清信贷及授信信息表';



CREATE TABLE `credit_report_limit`
(
    `id`                bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `credit_code`       bigint(20)                   DEFAULT NULL COMMENT '查询编号',
    `credit_report_id`       bigint(20)                   DEFAULT NULL COMMENT '征信报告基本表id',
    `total_amount` decimal(20, 5) DEFAULT NULL COMMENT '非循环-总额',
    `used_amount` decimal(20, 5) DEFAULT NULL COMMENT '非循环-已用额度',
    `remaining_available_amount` decimal(20, 5) DEFAULT NULL COMMENT '非循环-剩余可用额度',
    `cycle_total_amount` decimal(20, 5) DEFAULT NULL COMMENT '循环-已用额度',
    `cycle_used_amount` decimal(20, 5) DEFAULT NULL COMMENT '循环-已用额度',
    `cycle_remaining_available_amount` decimal(20, 5) DEFAULT NULL COMMENT '循环-已用额度',
    `create_by`         bigint(20)                   DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`       datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           tinyint(4)          NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='征信报告-信用额度表';

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
    `create_time`       datetime                     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           tinyint(4)          NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='征信报告-相关还款责任信息概要表';






