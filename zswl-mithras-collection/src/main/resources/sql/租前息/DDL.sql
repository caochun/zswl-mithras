-- 收款表增加字段
alter table `collection_base_info` add column `receipt_id` bigint(20) default null comment '借据id';
alter table `collection_base_info` add column `receipt_remaining_principal` bigint(20) default null comment '借据剩余本金';
alter table `collection_base_info` add `receipt_code` varchar(20) default null comment '借据编号';
