CREATE TABLE `finance_overdue_report_base`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `plan_date`     date COMMENT '计划月份',
    `report_status` varchar(50) COMMENT '报送状态',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除',
    `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`     bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`     bigint(20) DEFAULT NULL COMMENT '更新人id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='逾期报送计划表';


CREATE TABLE `finance_overdue_integration`
(
    `id`                   bigint(20) NOT NULL COMMENT '主键id',
    `collection_id`        bigint(20) COMMENT '收款明细id',
    `overdue_report_id`    bigint(20) COMMENT '逾期报送计划id',
    `collection_code`      varchar(100)      DEFAULT NULL COMMENT '收款编号',
    `cash_flow_item`       varchar(20)       DEFAULT NULL COMMENT '收款类型',
    `billno`               varchar(50)       DEFAULT NULL COMMENT '收款类型',
    `record_status`        varchar(20)       DEFAULT NULL COMMENT '单据状态',
    `contract_id`          bigint(20) DEFAULT NULL COMMENT '合同id',
    `contract_code`        varchar(100)      DEFAULT NULL COMMENT '合同编号',
    `proj_name`            varchar(200)      DEFAULT NULL COMMENT '项目名称',
    `client_id`            bigint(20) DEFAULT NULL COMMENT '客户ID',
    `client_name`          varchar(100)      DEFAULT NULL COMMENT '客户名称',
    `owned_type`           varchar(100)      DEFAULT NULL COMMENT '国有类型',
    `actual_controller`    varchar(100)      DEFAULT NULL COMMENT '实控人',
    `payment_number`       varchar(100)      DEFAULT NULL COMMENT '款项内容.款项内容编码',
    `record_start_date`    date              DEFAULT NULL COMMENT '单据账龄起算日',
    `record_bill_date`     date              DEFAULT NULL COMMENT '单据日期',
    `accounttype_number`   varchar(100)      DEFAULT NULL COMMENT '科目',
    `record_due_date`      date              DEFAULT NULL COMMENT '约定收款日期',
    `record_payment_terms` text              DEFAULT NULL COMMENT '约定收款条件',
    `rece_amount`          Decimal           DEFAULT NULL COMMENT '应收金额（元）',
    `collection_cycle`     int(1) DEFAULT 1 COMMENT '行业正常收款周期',
    `approval_status`      varchar(50)       DEFAULT NULL COMMENT '审批状态',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除',
    `create_time`          datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`            bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`          datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`            bigint(20) DEFAULT NULL COMMENT '更新人id',
    KEY                    `idx_integration_client_id` (`client_id`),
    KEY                    `idx_integration_collection_id` (`collection_id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='应收逾期集成表';


CREATE TABLE `finance_overdue_settlement`
(
    `id`                   bigint(20) NOT NULL COMMENT '主键id',
    `collection_id`        bigint(20) COMMENT '收款明细id',
    `overdue_report_id`    bigint(20) COMMENT '逾期报送计划id',
    `collection_code`      varchar(100)      DEFAULT NULL COMMENT '收款编号',
    `collection_record_id`    bigint(20) COMMENT '收款核销明细id',
    `cash_flow_item`       varchar(20)       DEFAULT NULL COMMENT '收款类型',
    `billno`               varchar(50)       DEFAULT NULL COMMENT '收款类型',
    `record_status`        varchar(20)       DEFAULT NULL COMMENT '单据状态',
    `contract_id`          bigint(20) DEFAULT NULL COMMENT '合同id',
    `contract_code`        varchar(100)      DEFAULT NULL COMMENT '合同编号',
    `proj_name`            varchar(200)      DEFAULT NULL COMMENT '项目名称',
    `client_id`            bigint(20) DEFAULT NULL COMMENT '客户ID',
    `client_name`          varchar(100)      DEFAULT NULL COMMENT '客户名称',
    `record_bill_date`     date              DEFAULT NULL COMMENT '单据日期',
    `settlement_date`      date              DEFAULT NULL COMMENT '结算日期',
    `voucher_account_date` date              DEFAULT NULL COMMENT '结算记录的凭证记账日期',
    `settlement_relation`  varchar(100)      DEFAULT NULL COMMENT '结算关系',
    `settlement_amount`    Decimal           DEFAULT NULL COMMENT '结算金额（元）',
    `approval_status`      varchar(50)       DEFAULT NULL COMMENT '审批状态',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除',
    `create_time`          datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`            bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`          datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`            bigint(20) DEFAULT NULL COMMENT '更新人id',
    PRIMARY KEY (`id`),
    KEY                    `idx_settlement_client_id` (`client_id`),
    KEY                    `idx_settlement_collection_id` (`collection_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='应收逾期结算表';



CREATE TABLE `finance_overdue_report_base_lib`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `plan_date`        date COMMENT '计划月份',
    `report_status`    varchar(50) COMMENT '报送状态',
    `create_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`        bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`        bigint(20) DEFAULT NULL COMMENT '更新人id',
    `version`          varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`        bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime             DEFAULT NULL COMMENT '原数据创建时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除',
    `data_create_by`   bigint(20) DEFAULT NULL COMMENT '原数据创建人',
    `data_update_time` datetime             DEFAULT NULL COMMENT '原数据更新时间',
    `data_update_by`   bigint(20) DEFAULT NULL COMMENT '原数据更新人',
    `version_type`     tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='逾期报送计划表版本表';


CREATE TABLE `finance_overdue_integration_lib`
(
    `id`                   bigint(20) NOT NULL COMMENT '主键id',
    `collection_id`        bigint(20) COMMENT '收款明细id',
    `overdue_report_id`    bigint(20) COMMENT '逾期报送计划id',
    `collection_code`      varchar(100)         DEFAULT NULL COMMENT '收款编号',
    `cash_flow_item`       varchar(20)          DEFAULT NULL COMMENT '收款类型',
    `billno`               varchar(50)          DEFAULT NULL COMMENT '收款类型',
    `record_status`        varchar(20)          DEFAULT NULL COMMENT '单据状态',
    `contract_id`          bigint(20) DEFAULT NULL COMMENT '合同id',
    `contract_code`        varchar(100)         DEFAULT NULL COMMENT '合同编号',
    `proj_name`            varchar(200)         DEFAULT NULL COMMENT '项目名称',
    `client_id`            bigint(20) DEFAULT NULL COMMENT '客户ID',
    `client_name`          varchar(100)         DEFAULT NULL COMMENT '客户名称',
    `owned_type`           varchar(100)         DEFAULT NULL COMMENT '国有类型',
    `actual_controller`    varchar(100)         DEFAULT NULL COMMENT '实控人',
    `payment_number`       varchar(100)         DEFAULT NULL COMMENT '款项内容.款项内容编码',
    `record_start_date`    date                 DEFAULT NULL COMMENT '单据账龄起算日',
    `record_bill_date`     date                 DEFAULT NULL COMMENT '单据日期',
    `accounttype_number`   varchar(100)         DEFAULT NULL COMMENT '科目',
    `record_due_date`      date                 DEFAULT NULL COMMENT '约定收款日期',
    `record_payment_terms` text                 DEFAULT NULL COMMENT '约定收款条件',
    `rece_amount`          Decimal              DEFAULT NULL COMMENT '应收金额（元）',
    `collection_cycle`     int(1) DEFAULT 1 COMMENT '行业正常收款周期',
    `approval_status`      varchar(50)          DEFAULT NULL COMMENT '审批状态',
    `create_time`          datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`            bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`          datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`            bigint(20) DEFAULT NULL COMMENT '更新人id',
    `version`              varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`            bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time`     datetime             DEFAULT NULL COMMENT '原数据创建时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除',
    `data_create_by`       bigint(20) DEFAULT NULL COMMENT '原数据创建人',
    `data_update_time`     datetime             DEFAULT NULL COMMENT '原数据更新时间',
    `data_update_by`       bigint(20) DEFAULT NULL COMMENT '原数据更新人',
    `version_type`         tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    KEY                    `idx_integration_client_id` (`client_id`),
    KEY                    `idx_integration_collection_id` (`collection_id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='应收逾期集成表版本表';


CREATE TABLE `finance_overdue_settlement_lib`
(
    `id`                   bigint(20) NOT NULL COMMENT '主键id',
    `collection_id`        bigint(20) COMMENT '收款明细id',
    `overdue_report_id`    bigint(20) COMMENT '逾期报送计划id',
    `collection_code`      varchar(100)         DEFAULT NULL COMMENT '收款编号',
    `collection_record_id`    bigint(20) COMMENT '收款核销明细id',
    `cash_flow_item`       varchar(20)          DEFAULT NULL COMMENT '收款类型',
    `billno`               varchar(50)          DEFAULT NULL COMMENT '收款类型',
    `record_status`        varchar(20)          DEFAULT NULL COMMENT '单据状态',
    `contract_id`          bigint(20) DEFAULT NULL COMMENT '合同id',
    `contract_code`        varchar(100)         DEFAULT NULL COMMENT '合同编号',
    `proj_name`            varchar(200)         DEFAULT NULL COMMENT '项目名称',
    `client_id`            bigint(20) DEFAULT NULL COMMENT '客户ID',
    `client_name`          varchar(100)         DEFAULT NULL COMMENT '客户名称',
    `record_bill_date`     date                 DEFAULT NULL COMMENT '单据日期',
    `settlement_date`      date                 DEFAULT NULL COMMENT '结算日期',
    `voucher_account_date` date                 DEFAULT NULL COMMENT '结算记录的凭证记账日期',
    `settlement_relation`  varchar(100)         DEFAULT NULL COMMENT '结算关系',
    `settlement_amount`    Decimal              DEFAULT NULL COMMENT '结算金额（元）',
    `approval_status`      varchar(50)          DEFAULT NULL COMMENT '审批状态',
    `create_time`          datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`            bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`          datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`            bigint(20) DEFAULT NULL COMMENT '更新人id',
    `version`              varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`            bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除',
    `data_create_time`     datetime             DEFAULT NULL COMMENT '原数据创建时间',
    `data_create_by`       bigint(20) DEFAULT NULL COMMENT '原数据创建人',
    `data_update_time`     datetime             DEFAULT NULL COMMENT '原数据更新时间',
    `data_update_by`       bigint(20) DEFAULT NULL COMMENT '原数据更新人',
    `version_type`         tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`),
    KEY                    `idx_settlement_client_id` (`client_id`),
    KEY                    `idx_settlement_collection_id` (`collection_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='应收逾期结算表版本表';


CREATE TABLE `finance_overdue_version_relation`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `process_instance_id` varchar(50) COMMENT '流程id',
    `record_type` varchar(20) COMMENT '单据类型',
    `record_id` bigint(20) COMMENT '记录id',
    `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`     bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除',
    `update_by`     bigint(20) DEFAULT NULL COMMENT '更新人id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='逾期报送关联表表';


INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by,
                          update_by)
VALUES ('budgetAccountReceivable', 3, 90, null, '/budget/accountsReceivable', null, null, '应收逾期报送', null, '_self',
        null, null);

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
VALUES (139, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 0);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('financeOverdueSettlementList', '资金管理-逾期报送-应收逾期结算表列表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/list', 2),
        ('financeOverdueSettlementRemove', '资金管理-逾期报送-删除应收逾期结算表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/remove', 2),
        ('financeOverdueSettlementModify', '资金管理-逾期报送-修改应收逾期结算表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/modify', 2),
        ('financeOverdueSettlementAdd', '资金管理-逾期报送-新增应收逾期结算表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/add', 2),
        ('financeOverdueSettlementContractRelation', '资金管理-逾期报送-逾期查询客户下合同信息', 0, (select id from bifrost_menu where code =                                                                        'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/contract/relation', 2),
        ('financeOverdueSettlementPush', '资金管理-逾期报送-推送应收逾期结算', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/push', 2);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('financeOverdueIntegrationList', '资金管理-逾期报送-应收逾期集成表列表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/integration/list', 2),
        ('financeOverdueIntegrationRemove', '资金管理-逾期报送-删除应收逾期集成表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/integration/remove', 2),
        ('financeOverdueIntegrationModify', '资金管理-逾期报送-修改应收逾期集成表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/integration/modify', 2),
        ('financeOverdueIntegrationPush', '资金管理-逾期报送-推送应收逾期集成表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/integration/push', 2);

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('financeOverdueReportClose', '资金管理-逾期报送-关闭逾期报送计划表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/report/base/close', 2),
        ('financeOverdueReportAdd', '资金管理-逾期报送-新增逾期报送计划表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/report/base/add', 2),
        ('financeOverdueReportList', '资金管理-逾期报送-逾期报送计划表列表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/report/base/list', 2),
        ('financeOverdueSubmit', '资金管理-逾期报送-提交应收逾期集成单', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/submit', 2);


