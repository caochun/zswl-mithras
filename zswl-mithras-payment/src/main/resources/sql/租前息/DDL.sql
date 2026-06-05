-- 付款申请表增加字段
alter table `payment_base_info` add column `receipt_id` bigint(20) default null comment '借据id（预关联）';
alter table `payment_base_info` add column `receipt_code` varchar(20) default null comment '借据编号';
alter table `payment_base_info` add column `receipt_id_final` bigint(20) default null comment '借据id（最终关联）';
alter table `payment_base_info_lib` add column `receipt_id` bigint(20) default null comment '借据id（预关联）';
alter table `payment_base_info_lib` add column `receipt_code` varchar(20) default null comment '借据编号';
alter table `payment_base_info_lib` add column `receipt_id_final` bigint(20) default null comment '借据id（最终关联）';
