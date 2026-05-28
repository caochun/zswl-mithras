alter table contract_receipt add column org_scale varchar (50) default null comment '借据创建时取自客户管理的【企业规模】';
alter table contract_receipt_lib add column org_scale varchar (50) default null comment '借据创建时取自客户管理的【企业规模】';

alter table contract_receipt add column xirr double default null comment 'xirr';
alter table contract_receipt_lib add column xirr double default null comment 'xirr';

-- 付款核销运营提前审核 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
alter table payment_base_info add column yunying_review_state tinyint(4) default null comment '运营提前审核状态，0-未审核，1-已审核';
alter table payment_base_info add column yunying_review_date date default null comment '运营审核通过日期';

alter table payment_base_info_lib add column yunying_review_state tinyint(4) default null comment '运营提前审核状态，0-未审核，1-已审核';
alter table payment_base_info_lib add column yunying_review_date date default null comment '运营审核通过日期';
-- 付款核销运营提前审核 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

ALTER table financing_repay_actual_process_detail add COLUMN `fund_financing_account_type` varchar(50) default null COMMENT '账户类别';
