-- 付款表增加字段
alter table payment_base_info add column `retention_money_type` INT(11) default null comment '质保金标识，0 内扣，1 不内扣';

alter table payment_base_info add column `retention_money` BIGINT(20) default null comment '质保金';

alter table payment_base_info_lib add column `retention_money_type` INT(11) default null comment '质保金标识，0 内扣，1 不内扣';

alter table payment_base_info_lib add column `retention_money` BIGINT(20) default null comment '质保金';

alter table contract_remind_record add column `intervals` BIGINT(20) default null comment '通知间隔 s';



alter table margin_base_info add column total_receivable_amount BIGINT(20) default null comment '累加应收金额';

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('collectionFlowCenterRecycleMarginPlan', '获取保证金回收计划', (select id from bifrost_menu where code = 'budgetFlowCenter'), 'POST',
        '/collection/flow/center/recycle/margin/plan', '2');

