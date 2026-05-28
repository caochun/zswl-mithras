
-- 资金报表
CREATE TABLE `fund_contract_bottom`
(
    `id`                                     bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    financing_id                             bigint(20) null comment '融资id',
    financing_type                           varchar(50) null comment '融资类型 为null则是间接融资',
    financing_code                           varchar(50) not null comment '融资编号',
    organization_id                          bigint(20) null comment '融资机构id',
    organization_name                        varchar(50) null comment '融资机构名称',
    fund_manager_id                          bigint(20) not null comment '资金经理id',
    business_type                            varchar(50)          default '' not null comment '业务类型',
    time_limit_type                          varchar(50)          default '' not null comment '期限类型',
    financing_month                          int null comment '融资期限（月）',
    is_pledge                                tinyint(1) null comment '是否质押',
    is_supervise                             tinyint(1) null comment '是否监管',
    pledge_supervise_message                 varchar(50)          default '无' comment '受限情况 是否有质押,监管',
    pledge_supervise_contract_codes          varchar(1000)        default null comment '质押或监管合同编号 、分割',
    guarantee_name                           varchar(50)          default null comment '担保方名称',
    guarantee_rate                           varchar(20)          default null comment '担保比例',
    financing_amount                         bigint(20) null comment '融资金额',
    comprehensive_interest_rate              bigint(20) null comment '实际综合成本',
    lpr_rate_percent                         int(10) null comment 'LPR利率 借款利率1',
    lpr_add_percent                          int(10) null comment 'LPR加点 借款利率2',
    actual_loan_date                         date null comment '实际贷款日期',
    actual_expire_date                       date null comment '实际到期日期',
    guarantee_remaining_principal            bigint(20) default null comment '担保对应剩余本金',
    repaired_principal                       bigint(20) default null comment '全部已还本金',
    repaired_interest                        bigint(20) default null comment '全部已还利息',
    remain_principal                         bigint(20) default null comment '全部未还本金',
    remain_interest                          bigint(20) default null comment '全部未还利息',
    principal_due_within_one_year            bigint(20) default null comment '一年内到期本金',
    interest_due_within_one_year             bigint(20) default null comment '一年内到期利息',
    principal_due_within_next_month          bigint(20) default null comment '下月到期本金',
    interest_due_within_next_month           bigint(20) default null comment '下月到期利息',
    principal_interest_due_within_next_month bigint(20) default null comment '下月到期本息',
    financing_year                           int(10) null comment '融资期限 到期日-起息日）/365，单位为年',
    repay_way                                varchar(20)          default '' null comment '还款方式',
    financing_status                         varchar(30)          default null comment '融资状态',
    interest_rate_type                       varchar(20)          default '' null comment '利率类型',
    principal_account_bank                   varchar(255) null comment '还本银行名称',
    principal_account_number                 varchar(50) null comment '还本银行账号',
    interest_account_bank                    varchar(255) null comment '还息银行名称',
    interest_account_number                  varchar(50) null comment '还息银行账号',
    `create_time`                            datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`                              bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`                            datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新人ID',
    `update_by`                              bigint(20) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                                      `client_bottom_create_time_index` (`create_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='资金底表-融资合同表';

ALTER TABLE fund_contract_bottom
    ADD INDEX `idx_fund_contract_bottom_financing_code` (`financing_code`) USING BTREE;


CREATE TABLE `fund_cash_flow_item_bottom`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    financing_id          bigint(20) null comment '融资id',
    financing_type        varchar(50) null comment '融资类型 为null则是间接融资',
    financing_code        varchar(50) not null comment '融资编号',
    organization_id       bigint(20) null comment '融资机构id',
    organization_name     varchar(50) null comment '融资机构名称',
    phase                 int(10) null comment '还款期项',
    cash_flow_code        varchar(60) null comment '现金流编号',
    cash_flow_item        varchar(50) null comment '现金流科目',
    cash_flow_direction   varchar(50) null comment '现金流方向',
    plan_date             date null comment '计划日期',
    plan_amount           bigint null comment '计划金额',
    plan_principle        bigint null comment '计划本金',
    plan_interest         bigint null comment '计划利息',
    financing_status      varchar(30) null comment '融资状态',
    latest_write_off_date date null comment '最后核销日',
    write_off_amount      bigint null comment '实际核销金额',
    write_off_principal   bigint null comment '实际核销本金',
    write_off_interest    bigint null comment '实际核销利息',
    write_off_statue      varchar(50) null comment '核销状态',
    `create_time`         datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`           bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`         datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新人ID',
    `update_by`           bigint(20) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                   `client_bottom_create_time_index` (`create_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='资金底表-现金流表';

ALTER TABLE fund_cash_flow_item_bottom
    ADD INDEX `idx_fund_cash_flow_item_bottom_financing_code` (`financing_code`) USING BTREE;


CREATE TABLE `fund_write_off_bottom`
(
    `id`                          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `fund_receipt_flow_detail_id` bigint(20) null comment '核销记录id ',
    cash_flow_date                date        not null comment '核销日期',
    operate_date                  date        not null comment '操作日期',
    cash_flow_direction           varchar(50) null comment '现金流方向',
    financing_id                  bigint null comment '融资id',
    financing_code                varchar(50) null comment '融资编号',
    organization_id               bigint null comment '融资机构id',
    organization_name             varchar(50) null comment '融资机构名称',
    cash_flow_code                varchar(50) not null comment '现金流编号',
    cash_flow_item                varchar(50) not null comment '现金流类型',
    `total_amount`                bigint(20) null comment '核销金额',
    `principal_amount`            bigint(20) null comment '核销本金',
    `interest_amount`             bigint(20) null comment '核销利息',
    `create_time`                 datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`                   bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`                 datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新人ID',
    `update_by`                   bigint(20) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                           `client_bottom_create_time_index` (`create_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='资金底表-核销表';

ALTER TABLE fund_write_off_bottom
    ADD INDEX `idx_fund_write_off_bottom_cash_flow_code` (`cash_flow_code`) USING BTREE;

CREATE TABLE `fund_credit_bottom`
(
    `id`                         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    credit_code                  varchar(50) null COMMENT '授信编号',
    organization_id              bigint null COMMENT '授信机构id',
    organization_name            varchar(50) null COMMENT '授信机构名称',
    recyclable                   tinyint(1) default 0 not null COMMENT '是否非循环 0 不',
    effective                    bit               default b'1' null comment '是否生效',
    total_credit_limit           bigint null comment '授信总额',
    guarantee_amount             bigint null COMMENT '担保额度',
    last_principle               bigint null comment '剩余本金',
    guarantee_last_principle     bigint null comment '担保剩余本金',
    non_guarantee_last_principle bigint null comment '非担保剩余本金',
    `create_time`                datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`                  bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time`                datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新人ID',
    `update_by`                  bigint(20) DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                          `client_bottom_create_time_index` (`create_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='资金底表-授信表';

ALTER TABLE fund_credit_bottom
    ADD INDEX `idx_fund_credit_bottom_credit_code` (`credit_code`) USING BTREE;


