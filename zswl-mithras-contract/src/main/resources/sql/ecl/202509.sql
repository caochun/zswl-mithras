alter table contract_base_info add column `lease_item_types` varchar(128) DEFAULT NULL COMMENT '租赁物类型';
alter table contract_base_info_lib add column `lease_item_types` varchar(128) DEFAULT NULL COMMENT '租赁物类型';
