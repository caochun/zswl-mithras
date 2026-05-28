alter table contract_receipt_bottom add column org_scale varchar (50) default null comment '借据创建时取自客户管理的【企业规模】';
alter table contract_receipt_bottom add column xirr double default null comment 'xirr';



