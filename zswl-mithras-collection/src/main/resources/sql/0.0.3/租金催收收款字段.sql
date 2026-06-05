-- email催收后 修改收款主表的催收次数 用于过滤条件
ALTER TABLE collection_base_info ADD email_notice_count int(11) DEFAULT 0 COMMENT '租金催收email发送次数';
alter table collection_base_info
    add penalty_interest_deduction_amount bigint(20) DEFAULT '0' COMMENT '罚息减免金额';

alter table collection_base_info
    add notice_financial_flag tinyint(1) DEFAULT NULL COMMENT '是否通知过苍穹（1是0否）';

alter table collection_base_info
    add overdue_collection_count int(11) DEFAULT '0' COMMENT '逾期催收次数';

alter table collection_base_info
    add plan_penalty_interest_date datetime DEFAULT NULL COMMENT '计划罚息收款日期';
