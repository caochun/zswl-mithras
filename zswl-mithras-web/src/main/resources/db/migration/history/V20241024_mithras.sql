CREATE TABLE `finance_flow_tab_main_info`
(
    `id`                       bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `batch_number`             varchar(32) DEFAULT NULL COMMENT '批次号',
    `business_model`           varchar(32) DEFAULT NULL COMMENT '业务模块 WriteOffBusinessModelEnum#name',
    `account_type`             varchar(32) DEFAULT NULL COMMENT '账户类型',
    `bank_account_name`        varchar(32) DEFAULT NULL COMMENT '来款账户名称',
    `bank_account_number`      varchar(32) DEFAULT NULL COMMENT '来款账户银行账号',
    `write_off_status`         varchar(16) DEFAULT NULL COMMENT '核销状态',
    `is_perfect_match`         tinyint(2) DEFAULT NULL COMMENT '是否完全匹配',
    `supervise_account_name`   varchar(32) DEFAULT NULL COMMENT '监管账户银行账户名称',
    `supervise_account_number` varchar(32) DEFAULT NULL COMMENT '监管账户银行账号',
    `is_expired`               tinyint(2) NOT NULL DEFAULT '0' COMMENT 'tab超时过期标识',
    `is_write_off`             tinyint(2) NOT NULL DEFAULT '0' COMMENT '本次是否核销',
    `deleted`                  tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by`                bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`              datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`              datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY                        `idx_supervise_account_name_supervise_account_number_deleted` (`id`,`supervise_account_name`,`supervise_account_number`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流水核销tab信息主表';

CREATE TABLE `finance_flow_tab_record`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `main_id`              bigint(20) NOT NULL COMMENT '关联tab主表ID',
    `finance_flow_type`    varchar(16) NOT NULL COMMENT '银行流水类型，收/付',
    `finance_flow_id`      bigint(20) NOT NULL COMMENT '银行流水ID',
    `other_account_name`   varchar(32) DEFAULT NULL COMMENT '对方户名',
    `other_account_number` varchar(32) DEFAULT NULL COMMENT '对方账号',
    `biz_date`             date        DEFAULT NULL COMMENT '交易日期',
    `biz_amount`           bigint(20) NOT NULL COMMENT '交易金额，存毫厘',
    `surplus_amount`       bigint(20) DEFAULT NULL COMMENT '剩余可核销金额，存毫厘',
    `deleted`              tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by`            bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`          datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`          datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `bank_flow_no`         varchar(64) DEFAULT NULL COMMENT '银行流水编号',
    PRIMARY KEY (`id`),
    KEY                    `idx_id_main_id_finance_flow_id_deleted` (`id`,`main_id`,`finance_flow_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tab中流水详情记录';

CREATE TABLE `finance_flow_match_result`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `main_id`                 bigint(20) NOT NULL COMMENT '关联tab主表ID',
    `business_model`          varchar(32)  DEFAULT NULL COMMENT '业务模块 WriteOffBusinessModelEnum#name',
    `finance_flow_id_list`    varchar(32)  DEFAULT NULL COMMENT '列表',
    `bank_flow_no_list`       varchar(512) DEFAULT NULL COMMENT '列表',
    `source_id`               bigint(20) DEFAULT NULL COMMENT '源表数据ID，对应双端的应付应收表ID',
    `client_id`               int(11) DEFAULT NULL COMMENT '客户ID',
    `source_business_code`    varchar(32)  DEFAULT NULL COMMENT '源数据名称，资金端-机构名称；项目端-合同编号',
    `cash_flow_item`          varchar(32)  DEFAULT NULL COMMENT '现金流项目，对应各自领域的枚举',
    `cash_flow_code`          varchar(32)  DEFAULT NULL COMMENT '现金流编号，对应各自领域的编号',
    `should_write_off_time`   date         DEFAULT NULL COMMENT '应付/应收时间',
    `should_write_off_amount` bigint(20) DEFAULT NULL COMMENT '应付/应收金额，毫厘',
    `no_write_off_amount`     bigint(20) DEFAULT NULL COMMENT '未付/未收金额，毫厘',
    `this_write_off_amount`   bigint(20) DEFAULT NULL COMMENT '本次核销金额，毫厘',
    `is_system_generate`      tinyint(2) DEFAULT '1' COMMENT '是否是系统生成',
    `deleted`                 tinyint(4) DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by`               bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`             datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`               bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`             datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY                       `idx_id_main_id_finance_flow_id_deleted` (`id`,`main_id`,`finance_flow_id_list`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自动核销预核销记录表';

ALTER TABLE payment_actual_detail_unconfirmed ADD process_instance_id varchar(16) COMMENT '关联付款核销确认流程id';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ( 'collectionFlowCenterBusinessPaymentManualCashFlowList', '付款业务流水待核销现金流列表',
         0, 495, 'POST', '/collection/flow/center/business/payment/manual/cashFlowList', 2);

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('capitalWriteOffManualWriteOff', '手动核销', '495', 'POST', '/capital/write/off/manual/write/off', '2'),
       ('capitalWriteOffCheckBeforeImport', '选择流水后的校验', '495', 'POST', '/capital/write/off/check/before/import',
        '2'),
       ('capitalWriteOffFlowMatchResult', '选择流水之后的匹配结果', '495', 'POST',
        '/capital/write/off/flow/match/result', '2'),
       ('capitalWriteOffDeleteBankFlow', '删除银行流水', '495', 'POST', '/capital/write/off/delete/bank/flow', '2'),
       ('capitalWriteOffAddBankFlow', '增加银行流水', '495', 'POST', '/capital/write/off/add/bank/flow', '2'),
       ('capitalWriteOffUpdateBusinessFlow', '修改业务流水', '495', 'POST', '/capital/write/off/update/business/flow',
        '2'),
       ('capitalWriteOffAddBusinessFlow', '新增业务流水', '495', 'POST', '/capital/write/off/add/business/flow', '2'),
       ('capitalWriteOffDeleteBusinessFlow', '删除业务流水', '495', 'POST', '/capital/write/off/delete/business/flow',
        '2'),
       ('capitalWriteOffRematchTab', '重新匹配单个Tab的信息', '495', 'POST', '/capital/write/off/rematch/tab', '2'),
       ('capitalWriteOffDeleteTab', '删除单个Tab', '495', 'POST', '/capital/write/off/delete/tab', '2'),
       ('capitalWriteOffSingleTab', '获取单个Tab信息', '495', 'POST', '/capital/write/off/single/tab', '2'),
       ('capitalWriteOffReleaseBankFlow', '释放银行流水', '495', 'POST', '/capital/write/off/release/bank/flow', '2');


ALTER TABLE payment_actual_detail ADD COLUMN un_confirmed_id BIGINT(20) NULL COMMENT '关联un_confirmed表id';


-- 业务帐龄表
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values ('financeAccountAgeBaseInfoCount', '帐龄月份-统计', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/base/info/count', 2);

update finance_account_age_item set accountancy_organization_name = '浙江浙商融资租赁有限公司' where accountancy_organization_name is null;

INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`)
VALUES
('job', '岗位类型', 'assetManagementReview', '资产管理复核岗', 10);


