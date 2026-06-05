-- 借据表增加字段
alter table `contract_receipt` add column `receipt_code` varchar(20) default null comment '借据编号';
alter table `contract_receipt` add column `sequence` tinyint(4) default null comment '序列号';
alter table `contract_receipt_lib` add column `receipt_code` varchar(20) default null comment '借据编号';
alter table `contract_receipt_lib` add column `sequence` tinyint(4) default null comment '序列号';
