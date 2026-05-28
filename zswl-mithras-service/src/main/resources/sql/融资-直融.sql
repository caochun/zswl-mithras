INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`)
VALUES ('job', '岗位类型', 'moneymanagerhead', '资金业务负责人', 10);

alter table fund_receipt_repay_base_info
    add financing_type varchar(50) null comment '融资类型 为null则是间接融资';
alter table fund_receipt_repay_base_info_lib
    add financing_type varchar(50) null comment '融资类型 为null则是间接融资';
alter table fund_receipt_repay_base_info
    add financing_biz_type varchar(50) null comment '融资业务类型';
alter table fund_receipt_repay_base_info_lib
    add financing_biz_type varchar(50) null comment '融资业务类型';

alter table fund_receipt_repay_expense
    add direct_fee_id bigint(20) null comment '直融费用id';
alter table fund_receipt_repay_expense_lib
    add direct_fee_id bigint(20) null comment '直融费用id';

UPDATE fund_receipt_repay_base_info r
SET r.financing_biz_type = (SELECT f.business_type FROM fund_financing_base_info f WHERE f.id = r.financing_id)
WHERE financing_type IS NULL;

UPDATE fund_receipt_repay_base_info_lib r
SET r.financing_biz_type = (SELECT f.business_type FROM fund_financing_base_info f WHERE f.id = r.financing_id)
WHERE financing_type IS NULL;

alter table collection_overdue_history
    add client_id bigint(20) null comment '冗余客户id';
UPDATE collection_overdue_history c
SET c.client_id = (SELECT b.client_id FROM collection_base_info b WHERE b.id = c.collection_id);

alter table fund_receipt_repay_base_info
    add financing_channel varchar(100) null comment '融资渠道';

alter table fund_receipt_repay_base_info_lib
    add financing_channel varchar(100) null comment '融资渠道';

alter table fund_credit
    add effective bit(1) default b'1' comment '生效';