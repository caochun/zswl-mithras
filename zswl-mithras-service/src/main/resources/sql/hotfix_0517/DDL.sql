-- 提前还款表增加字段
alter table contract_prepayment add COLUMN apply_derate_amount BIGINT(20) default null comment '申请减免金额';
alter table contract_prepayment_lib add COLUMN apply_derate_amount BIGINT(20) default null comment '申请减免金额';