CREATE TABLE `funds_daily_cost_main` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                         `query_key` varchar(20) NOT NULL DEFAULT '' COMMENT '查询唯一key',
                                         `financing_type` varchar(10) NOT NULL DEFAULT '' COMMENT '融资类型',
                                         `financing_id` bigint(20) NOT NULL COMMENT '融资id',
                                         `financing_code` varchar(50) NOT NULL DEFAULT '' COMMENT '融资编号',
                                         `financing_channel` varchar(100) DEFAULT NULL COMMENT '融资渠道',
                                         `carry_interest_date` date NOT NULL COMMENT '起息日',
                                         `biz_type` varchar(50) DEFAULT NULL COMMENT '关联合同业务类型',
                                         `lease_type` varchar(50) DEFAULT NULL COMMENT '关联合同租赁类型',
                                         `loan_property` varchar(50) DEFAULT NULL COMMENT '借款性质',
                                         `financing_rate` int(10) DEFAULT NULL COMMENT '借款利率',
                                         `total_capital_cost_this_year` bigint(20) NOT NULL DEFAULT '0' COMMENT '当年累计计提资金成本',
                                         `total_capital_cost_this_month` bigint(20) NOT NULL DEFAULT '0' COMMENT '当月累计计提资金成本',
                                         `last_update_date` date DEFAULT NULL COMMENT '更新日期',
                                         `finish` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否结束计提',
                                         `is_same_biz` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否同业融资',
                                         `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                         `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `idx_query_key` (`query_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='资⾦每⽇成本记录主表';

alter table funds_daily_cost add column main_id bigint(20) not null comment '主表id' after id;
alter table funds_daily_cost add column financing_product_id bigint(20) DEFAULT NULL COMMENT '产品明细id（仅直融）' after financing_id;
alter table funds_daily_cost add column abbreviation varchar(100) DEFAULT NULL COMMENT '证券简称' after financing_product_id;
alter table funds_daily_cost add column financing_cost_after_tax bigint(20) DEFAULT NULL COMMENT '融资成本（税后）' after financing_cost;
alter table funds_daily_cost add column tax_rate decimal(8,6) DEFAULT NULL COMMENT '税率' after daily_rate;
alter table funds_daily_cost add column financing_cost_diff bigint(20) NOT NULL DEFAULT 0 COMMENT '钆差金额' after financing_cost;
alter table funds_daily_cost add column begin_of_period_interest_balance bigint(20) not null default 0 comment '期初应付利息余额';
alter table funds_daily_cost add column end_of_period_interest_balance bigint(20) not null default 0 comment '期末应付利息余额';

alter table funds_daily_cost add key idx_interest_date (interest_date, deleted);
alter table funds_daily_cost add key idx_main_id (main_id, deleted);

alter table funds_daily_cost modify column principle_amount bigint(20) NOT NULL DEFAULT 0 COMMENT '还款本金';
alter table funds_daily_cost modify column interest_amount bigint(20) NOT NULL DEFAULT 0 COMMENT '还款利息';

alter table monthly_management_cost_record add column financing_cost_diff bigint(20) NOT NULL DEFAULT 0 COMMENT '钆差金额' after financing_cost;
alter table monthly_management_cost_record add column begin_of_period_interest_balance bigint(20) not null default 0 comment '期初应付利息余额';
alter table monthly_management_cost_record add column end_of_period_interest_balance bigint(20) not null default 0 comment '期末应付利息余额';