alter table contract_receipt add column org_scale varchar (50) default null comment '借据创建时取自客户管理的【企业规模】';
alter table contract_receipt_lib add column org_scale varchar (50) default null comment '借据创建时取自客户管理的【企业规模】';

alter table contract_receipt add column xirr double default null comment 'xirr';
alter table contract_receipt_lib add column xirr double default null comment 'xirr';

update contract_receipt ct
set ct.org_scale = (select org_scale
                    from corp_commerce_info
                    where client_id = (select client_id
                                       from contract_base_info
                                       where id = ct.contract_id));
