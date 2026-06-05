-- 付款表增加字段
alter table payment_base_info add column `retention_money_type` INT(11) default null comment '质保金标识，0 内扣，1 不内扣';

alter table payment_base_info add column `retention_money` BIGINT(20) default null comment '质保金';

alter table payment_base_info_lib add column `retention_money_type` INT(11) default null comment '质保金标识，0 内扣，1 不内扣';

alter table payment_base_info_lib add column `retention_money` BIGINT(20) default null comment '质保金';
