alter table contract_settle_plan MODIFY column is_earnest_deduction tinyint(4) default 0 COMMENT '保证金是否内扣 0-否 1-是';
alter table contract_settle_plan_lib MODIFY column is_earnest_deduction tinyint(4) default 0 COMMENT '保证金是否内扣 0-否 1-是';
