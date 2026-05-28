-- 合同提前结清优化
alter table contract_prepayment add column is_early_settle tinyint(1) default null comment '是否提前结清，0-否，1-是' after contract_id;
alter table contract_prepayment_lib add column is_early_settle tinyint(1) default null comment '是否提前结清，0-否，1-是' after contract_id;

alter table contract_prepayment add column penalty_derate_type varchar(20) default null comment '违约金减免方式' after penalty;
alter table contract_prepayment_lib add column penalty_derate_type varchar(20) default null comment '违约金减免方式' after penalty;
alter table contract_prepayment add column penalty_derate_percent int(10) default null comment '违约金减免百分比' after penalty_derate_type;
alter table contract_prepayment_lib add column penalty_derate_percent int(10) default null comment '违约金减免百分比' after penalty_derate_type;
alter table contract_prepayment add column penalty_derate_amount bigint(20) default null comment '违约金减免金额' after penalty_derate_percent;
alter table contract_prepayment_lib add column penalty_derate_amount bigint(20) default null comment '违约金减免金额' after penalty_derate_percent;

alter table contract_prepayment add column loss_derate_type varchar(20) default null comment '提前终止补偿金减免方式' after loss;
alter table contract_prepayment_lib add column loss_derate_type varchar(20) default null comment '提前终止补偿金减免方式' after loss;
alter table contract_prepayment add column loss_derate_percent int(10) default null comment '提前终止补偿金减免百分比' after loss_derate_type;
alter table contract_prepayment_lib add column loss_derate_percent int(10) default null comment '提前终止补偿金减免百分比' after loss_derate_type;
alter table contract_prepayment modify column apply_derate_amount bigint(20) DEFAULT NULL COMMENT '提前终止补偿金减免金额' after loss_derate_type;
alter table contract_prepayment_lib modify column apply_derate_amount bigint(20) DEFAULT NULL COMMENT '提前终止补偿金减免金额' after loss_derate_type;

alter table contract_prepayment add column is_earnest_money_deduction tinyint(1) default null comment '保证金是否抵扣，0-否，1-是' after is_early_settle;
alter table contract_prepayment_lib add column is_earnest_money_deduction tinyint(1) default null comment '保证金是否抵扣，0-否，1-是' after is_early_settle;
alter table contract_prepayment add column earnest_money_balance bigint(20) default null comment '保证金余额' after is_earnest_money_deduction;
alter table contract_prepayment_lib add column earnest_money_balance bigint(20) default null comment '保证金余额' after is_earnest_money_deduction;
alter table contract_prepayment add column earnest_money_deduction_amount bigint(20) default null comment '保证金抵扣金额' after earnest_money_balance;
alter table contract_prepayment_lib add column earnest_money_deduction_amount bigint(20) default null comment '保证金抵扣金额' after earnest_money_balance;

alter table contract_prepayment add column remark text default null comment '备注说明' after unpaid_rent;
alter table contract_prepayment_lib add column remark text default null comment '备注说明' after unpaid_rent;

alter table contract_prepayment add column before_maturity_principal bigint(20) default null comment '未到期本金' after unpaid_rent_due;
alter table contract_prepayment_lib add column before_maturity_principal bigint(20) default null comment '未到期本金' after unpaid_rent_due;

alter table contract_prepayment add column before_maturity_interest bigint(20) default null comment '未到期利息' after before_maturity_principal;
alter table contract_prepayment_lib add column before_maturity_interest bigint(20) default null comment '未到期利息' after before_maturity_principal;

alter table contract_prepayment modify column early_repayment bigint(20) default NULL COMMENT '提前归还本金';
alter table contract_prepayment_lib modify column early_repayment bigint(20) default NULL COMMENT '提前归还本金';