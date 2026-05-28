alter table payment_base_info add column leased_price bigint(20) DEFAULT NULL COMMENT '租赁财产价值';
alter table payment_base_info add column leased_currency varchar(20) DEFAULT NULL COMMENT '币种';
alter table payment_base_info_lib add column leased_price bigint(20) DEFAULT NULL COMMENT '租赁财产价值';
alter table payment_base_info_lib add column leased_currency varchar(20) DEFAULT NULL COMMENT '币种';

ALTER TABLE proj_establish_base_info ADD COLUMN risk_control_industry_classify VARCHAR(64) NULL COMMENT '风控行业分类' AFTER lease_types;
ALTER TABLE proj_establish_base_info_lib ADD COLUMN risk_control_industry_classify VARCHAR(64) NULL COMMENT '风控行业分类' AFTER lease_types;