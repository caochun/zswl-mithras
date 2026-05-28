-- ABS分层核销 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
CREATE TABLE `fund_direct_financing_repay_actual_split` (
                                                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                                            `financing_id` bigint(20) NOT NULL COMMENT '融资id',
                                                            `product_detail_id` bigint(20) NOT NULL COMMENT '产品明细id',
                                                            `repay_actual_id` bigint(20) NOT NULL COMMENT '实际还款计划id',
                                                            `cash_flow_code_parent` varchar(60) NOT NULL DEFAULT '' COMMENT '现金流编号（父）',
                                                            `cash_flow_code` varchar(60) NOT NULL COMMENT '现金流编号',
                                                            `sequence` tinyint(4) NOT NULL COMMENT '序号',
                                                            `repay_date` date NOT NULL COMMENT '还款日期',
                                                            `phase` int(10) NOT NULL COMMENT '还款期项',
                                                            `repay_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '应还总额',
                                                            `principal_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '本金',
                                                            `interest_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '利息',
                                                            `remaining_principal_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '剩余未还本金',
                                                            `write_off_status` varchar(32) NOT NULL DEFAULT 'NO_WRITE_OFF' COMMENT '核销状态',
                                                            `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                            `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                            `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                                            PRIMARY KEY (`id`) USING BTREE,
                                                            KEY `idx_financing_id` (`financing_id`,`deleted`),
                                                            KEY `idx_repay_actual_id` (`repay_actual_id`,`deleted`),
                                                            KEY `idx_product_detail_id` (`product_detail_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='直接融资-实际还款表-拆分';

CREATE TABLE `fund_direct_financing_repay_actual_split_record` (
                                                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                                   `financing_id` bigint(20) NOT NULL COMMENT '融资id',
                                                                   `product_detail_id` bigint(20) NOT NULL COMMENT '产品明细id',
                                                                   `cash_flow_code_parent` varchar(50) NOT NULL DEFAULT '' COMMENT '现金流编号（父）',
                                                                   `cash_flow_code` varchar(50) NOT NULL COMMENT '现金流编号',
                                                                   `cash_flow_item` varchar(50) NOT NULL DEFAULT '0' COMMENT '现金流类型',
                                                                   `write_off_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '核销金额',
                                                                   `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                                                   `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                                   `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                                   PRIMARY KEY (`id`),
                                                                   KEY `idx_cash_flow_code` (`cash_flow_code`,`deleted`),
                                                                   KEY `idx_financing_id` (`financing_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直接融资-实际还款计划拆分-核销明细';
-- ABS分层核销 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
ALTER table lease_item_info ADD `lease_item_categories` varchar(150) DEFAULT NULL COMMENT '租赁物中文类型'


-- 评审会线上纪要 开始
CREATE TABLE `proj_review_meet_minute_base_info`
(
    `id`                               bigint(20) NOT NULL AUTO_INCREMENT,
    `proj_review_id`                   bigint(20) DEFAULT NULL COMMENT '关联的评审ID',
    `proj_review_type`                 varchar(50) DEFAULT NULL COMMENT '关联的评审类型',
    `proj_name`                        varchar(200)  DEFAULT NULL COMMENT '项目名称',
    `proj_flow_id`                     varchar(200)  DEFAULT NULL COMMENT '关联项目流程',
    `meet_minute_code`                 varchar(200)  DEFAULT NULL COMMENT '会议纪要编号',
    `meet_minute_sequence`             varchar(50)  DEFAULT NULL COMMENT '会议纪要序号',
    `meet_minute_status`               varchar(20)   DEFAULT NULL COMMENT '会议纪要状态',
    `lease_type`                       varchar(100)  DEFAULT NULL COMMENT '租赁类型',
    `lessee_info`                      json          DEFAULT NULL COMMENT '承租人列表',
    `supplier_info`                    varchar(200)          DEFAULT NULL COMMENT '供应商列表',
    `lease_require`                    text DEFAULT NULL COMMENT '租赁物要求',
    `fixed_value_basis`                varchar(200)  DEFAULT NULL COMMENT '定值依据 ProjectFixedValueBasisEnum#name',
    `fixed_value_basis_value`          varchar(200)  DEFAULT NULL COMMENT '定值依据名称 ProjectFixedValueBasisEnum#disply',
    `insurance_purchaser`              varchar(200)  DEFAULT NULL COMMENT '保险安排-购买方 ProjectInsurancePurchaserEnum',
    `policy_type`                      varchar(50)   DEFAULT NULL COMMENT '险种 ProjectPolicyTypeEnum#name',
    `policy_type_value`                varchar(50)   DEFAULT NULL COMMENT '险种值 ProjectPolicyTypeEnum#disply',
    `policy_require`                   varchar(50)   DEFAULT NULL COMMENT '保险要求 ProjectPolicyRequireEnum#name',
    `policy_require_value`             varchar(50)   DEFAULT NULL COMMENT '保险要求值 ProjectPolicyRequireEnum#disply',
    `project_approval_amount`          BIGINT(20) DEFAULT NULL COMMENT '项目批复金额',
    `financing_ratio`                  varchar(50)   DEFAULT NULL COMMENT '融资比例 ProjectFinancingRatioEnum#name',
    `financing_ratio_value`            varchar(50)   DEFAULT NULL COMMENT '融资比例值 ProjectFinancingRatioEnum#name',
    `financing_require`                    text DEFAULT NULL COMMENT '融资要求',
    `fund_special_requirements`                    text DEFAULT NULL COMMENT '融资比例特殊要求',
    `lease_term`                       int(11) DEFAULT NULL COMMENT '租赁期限',
    `pre_lease_period_flag`            tinyint(1) DEFAULT NULL COMMENT '是否含租前期 0 不含 1 含',
    `pre_lease_period`                 int(11) DEFAULT NULL COMMENT '租前期',
    `down_payment`                     bigint(20) DEFAULT NULL COMMENT '首期租金',
    `earnest_money_flag`               tinyint(1) DEFAULT NULL COMMENT '保证金标识 0 无，1有',
    `earnest_money_ratio`              int(11) DEFAULT NULL COMMENT '保证金比例',
    `earnest_money_amount`             bigint(20) DEFAULT NULL COMMENT '保证金金额',
    `earnest_money_collect_type`       varchar(50)   DEFAULT NULL COMMENT '保证金收取方式 EarnestMoneyCollectTypeEnum#name',
    `earnest_money_collect_type_value` varchar(50)   DEFAULT NULL COMMENT '保证金收取方式名称 EarnestMoneyCollectTypeEnum#disply',
    `rent_payment_method_rate`         varchar(50)   DEFAULT NULL COMMENT '租金支付频率 RentPaymentMethodRateEnum#name',
    `rent_payment_method_type`         varchar(50)   DEFAULT NULL COMMENT '租金支付方式 RentPaymentMethodTypeEnum#name',
    `financing_fund_method_type`       varchar(50)   DEFAULT NULL COMMENT '融资款支付方式 FinancingFundMethodTypeEnum#name',
    `funds_purpose`                    varchar(200) null comment '资金用途',
    `rental_start_method`              varchar(50)   DEFAULT NULL COMMENT '起租方式 RentalStartMethodEnum#name',
    `rental_start_condition`           text DEFAULT NULL COMMENT '起租条件',
    `nominal_price`                    bigint(20) DEFAULT NULL COMMENT '名义价款',
    `resolution_info`                  json          DEFAULT NULL COMMENT '决议批准文件信息 ResolutionTypeRateEnum',
    `guarantee_measures`               json          DEFAULT NULL COMMENT '担保措施 GuaranteeMeasuresTypeEnum',
    `pledge_measures`                  json          DEFAULT NULL COMMENT '质押措施 PledgeMeasuresTypeEnum',
    `other_risk_mitigation_measures`   text DEFAULT NULL COMMENT '其他风险缓释措施',
    `special_contract_terms`           text DEFAULT NULL COMMENT '特殊合同条款',
    `conditions_before_disbursement`   text DEFAULT NULL COMMENT '放款前须落实条件',
    `management_requirement`           text DEFAULT NULL COMMENT '管理要求-其他要求',
    `report_issuance_time`             date          DEFAULT NULL COMMENT '报告出具时间',
    `report_issuance_year`             int(11) DEFAULT NULL COMMENT '报告出具年份',
    `report_number_voters`             int(11) DEFAULT NULL COMMENT '报告表决人数',
    `report_number_agree`              int(11) DEFAULT NULL COMMENT '报告同意人数',
    `report_number_conditional_agree`              int(11) DEFAULT NULL COMMENT '报告有条件同意人数',
    `report_number_against`            int(11) DEFAULT NULL COMMENT '报告反对人数',
    `voting_committee`                      json          DEFAULT NULL COMMENT '表决委员',
    `voting_result`       varchar(50)   DEFAULT NULL COMMENT '表决结果 VotingResultTypeEnum',
    `voting_period_validity`       varchar(50)   DEFAULT NULL COMMENT '表决有效期VotingPeriodValidityEnum',
    `create_by`                        bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                      datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                        bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                      datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY                                `idx_proj_review_id` (`proj_review_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='项目评审会议纪要表';



CREATE TABLE `proj_review_cash_flow_quotation_proposal`
(
    `id`                  bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '现金流量明细表id',
    `project_id`          bigint(20) NOT NULL COMMENT '所属项目评审记录ID',
    `cash_flow_date`      date     DEFAULT NULL COMMENT '日期',
    `cash_flow_phase`     int(10) DEFAULT NULL COMMENT '期项',
    `cash_flow_amount`    bigint(20) DEFAULT NULL COMMENT '现金流金额',
    `rent`                bigint(20) DEFAULT NULL COMMENT '租金',
    `principal`           bigint(20) DEFAULT NULL COMMENT '本金',
    `interest`            bigint(20) DEFAULT NULL COMMENT '利息',
    `remaining_principal` bigint(20) DEFAULT NULL COMMENT '剩余本金',
    `create_by`           bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id	',
    `create_time`         datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by`           bigint(20) DEFAULT NULL COMMENT '最后更新人id	',
    `update_time`         datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间	',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目评审-报价方案-现金流计划表';

CREATE TABLE `proj_review_cash_flow_quotation_proposal_lib`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '现金流量明细表id',
    `project_id`          bigint(20) NOT NULL COMMENT '所属项目评审记录ID',
    `cash_flow_date`      date     DEFAULT NULL COMMENT '日期',
    `cash_flow_phase`     int(10) DEFAULT NULL COMMENT '期项',
    `cash_flow_amount`    bigint(20) DEFAULT NULL COMMENT '现金流金额',
    `rent`                bigint(20) DEFAULT NULL COMMENT '租金',
    `principal`           bigint(20) DEFAULT NULL COMMENT '本金',
    `interest`            bigint(20) DEFAULT NULL COMMENT '利息',
    `remaining_principal` bigint(20) DEFAULT NULL COMMENT '剩余本金',
    `create_by`           bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id	',
    `create_time`         datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by`           bigint(20) DEFAULT NULL COMMENT '最后更新人id	',
    `update_time`         datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间	',
    `version`             varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`           bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time`    datetime DEFAULT NULL,
    `data_create_by`      bigint(20) DEFAULT NULL,
    `data_update_time`    datetime DEFAULT NULL,
    `data_update_by`      bigint(20) DEFAULT NULL,
    `version_type`        tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目评审-报价方案-现金流计划表版本表';


insert into proj_review_cash_flow_quotation_proposal select * from proj_review_cash_flow_plan;
insert into proj_review_cash_flow_quotation_proposal_lib select * from proj_review_cash_flow_plan_lib;

alter table track_event_info add column proj_review_meet_minute_id bigint(20) DEFAULT NULL COMMENT '项目评审会议纪要ID';

alter table proj_review_cash_flow_plan add column proj_review_meet_minute_id bigint(20) DEFAULT NULL COMMENT '项目评审会议纪要ID';

alter table proj_review_cash_flow_plan_lib add column proj_review_meet_minute_id bigint(20) DEFAULT NULL COMMENT '项目评审会议纪要ID';


alter table proj_pricing_base_info add column proj_review_id bigint(20) DEFAULT NULL COMMENT '项目评审ID';
alter table proj_pricing_base_info_lib add column proj_review_id bigint(20) DEFAULT NULL COMMENT '项目评审ID';

alter table proj_pricing_factoring_price add column project_approval_amount bigint(20) DEFAULT NULL COMMENT '项目批复金额';
alter table proj_pricing_factoring_price_lib add column project_approval_amount bigint(20) DEFAULT NULL COMMENT '项目批复金额';

alter table proj_pricing_lease_price add column project_approval_amount bigint(20) DEFAULT NULL COMMENT '项目批复金额';
alter table proj_pricing_lease_price_lib add column project_approval_amount bigint(20) DEFAULT NULL COMMENT '项目批复金额';

alter table proj_pricing_aoc_price add column project_approval_amount bigint(20) DEFAULT NULL COMMENT '项目批复金额';
alter table proj_pricing_aoc_price_lib add column project_approval_amount bigint(20) DEFAULT NULL COMMENT '项目批复金额';

alter table proj_review_meet_minute_base_info add column process_end_time date DEFAULT NULL COMMENT '审批通过时间';

alter table proj_review_meet_minute_base_info add column report_issuance_number int(11) DEFAULT NULL COMMENT '审批通过时间';
alter table proj_review_meet_minute_base_info add column `supple_remark`   text DEFAULT NULL COMMENT '补充说明';
alter table proj_review_meet_minute_base_info add column `financing_fund_supple_remark`   text DEFAULT NULL COMMENT '融资款支付方式补充说明';

alter table proj_review_base_info
    add column `supplier_info` varchar(250) DEFAULT NULL COMMENT '供应商列表';
alter table proj_review_base_info_lib
    add column `supplier_info` varchar(250) DEFAULT NULL COMMENT '供应商列表';
alter table proj_establish_base_info
    add column `supplier_info` varchar(250) DEFAULT NULL COMMENT '供应商列表';
alter table proj_establish_base_info_lib
    add column `supplier_info` varchar(250) DEFAULT NULL COMMENT '供应商列表';
alter table proj_pricing_base_info
    add column `supplier_info` varchar(250) DEFAULT NULL COMMENT '供应商列表';
alter table proj_pricing_base_info_lib
    add column `supplier_info` varchar(250) DEFAULT NULL COMMENT '供应商列表';

ALTER table lease_item_info ADD `lease_item_categories` varchar(150) DEFAULT NULL COMMENT '租赁物中文类型';




