-- 项目立项保理/债权转让 债权人由单个变更为多个
alter table `proj_establish_base_info` add column `creditor_info` json default null comment '债权人信息';
alter table `proj_establish_base_info_lib` add column `creditor_info` json default null comment '债权人信息';

-- 项目评审保理/债权转让 债权人由单个变更为多个
alter table `proj_review_base_info` add column `creditor_info` json default null comment '债权人信息';
alter table `proj_review_base_info_lib` add column `creditor_info` json default null comment '债权人信息';

-- 合同基本信息增加保理类型
alter table `contract_base_info` add `factoring_type` varchar(100) DEFAULT NULL COMMENT '保理类型';
alter table `contract_base_info_lib` add `factoring_type` varchar(100) DEFAULT NULL COMMENT '保理类型';

-- 删除老表
drop table IF EXISTS contract_factoring_price, contract_factoring_price_lib, contract_aoc_price, contract_aoc_price_lib;

-- 合同 保理报价方案表
CREATE TABLE `contract_factoring_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id	',
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `contract_amount` bigint(20) DEFAULT NULL COMMENT '合同金额',
  `credit_amount_loop` tinyint(1) DEFAULT NULL COMMENT '额度是否可循环',
  `factoring_credit_term` int(11) DEFAULT NULL COMMENT '保理融资期限',
  `earnest_money` bigint(20) DEFAULT NULL COMMENT '保证金',
  `factoring_financing_proportion` int(11) DEFAULT NULL COMMENT '保理融资比例',
  `consulting_fee` bigint(20) DEFAULT NULL COMMENT '手续费',
  `rate_type` varchar(10) DEFAULT NULL COMMENT '保理费率类型。固定利率：fixed、浮动利率：float',
  `lpr_type` varchar(20) DEFAULT NULL COMMENT 'lpr品种 一年期，五年期',
  `lpr_percent` int(11) DEFAULT NULL COMMENT 'lpr',
  `lpr_add_percent` int(11) DEFAULT NULL COMMENT 'lpr加点',
  `factoring_rate_percent` int(11) DEFAULT NULL COMMENT '保理费率值。百分之多少',
  `irr_percent` int(11) DEFAULT NULL COMMENT '内部收益率。百分之多少',
  `repay_calc_type` varchar(40) DEFAULT NULL COMMENT '还款计算方式',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_contract_id` (`contract_id`) USING BTREE COMMENT '每个合同只能有一份报价'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保理合同报价方案表';

-- 合同 保理报价方案版本表
CREATE TABLE `contract_factoring_price_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id	',
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `contract_amount` bigint(20) DEFAULT NULL COMMENT '合同金额',
  `credit_amount_loop` tinyint(1) DEFAULT NULL COMMENT '额度是否可循环',
  `factoring_credit_term` int(11) DEFAULT NULL COMMENT '保理融资期限',
  `earnest_money` bigint(20) DEFAULT NULL COMMENT '保证金',
  `factoring_financing_proportion` int(11) DEFAULT NULL COMMENT '保理融资比例',
  `consulting_fee` bigint(20) DEFAULT NULL COMMENT '手续费',
  `rate_type` varchar(10) DEFAULT NULL COMMENT '保理费率类型。固定利率：fixed、浮动利率：float',
  `lpr_type` varchar(20) DEFAULT NULL COMMENT 'lpr品种 一年期，五年期',
  `lpr_percent` int(11) DEFAULT NULL COMMENT 'lpr',
  `lpr_add_percent` int(11) DEFAULT NULL COMMENT 'lpr加点',
  `factoring_rate_percent` int(11) DEFAULT NULL COMMENT '保理费率值。百分之多少',
  `irr_percent` int(11) DEFAULT NULL COMMENT '内部收益率。百分之多少',
  `repay_calc_type` varchar(40) DEFAULT NULL COMMENT '还款计算方式',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) unsigned DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保理合同报价方案版本表';

-- 合同 债权转让报价方案表
CREATE TABLE `contract_aoc_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id	',
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `contract_amount` bigint(20) DEFAULT NULL COMMENT '合同金额',
  `credit_amount_loop` tinyint(1) DEFAULT NULL COMMENT '额度是否可循环',
  `aoc_credit_term` int(11) DEFAULT NULL COMMENT '转让额度有效期',
  `earnest_money` bigint(20) DEFAULT NULL COMMENT '保证金',
  `consulting_fee` bigint(20) DEFAULT NULL COMMENT '手续费',
  `rate_type` varchar(10) DEFAULT NULL COMMENT '转让费率类型。固定利率：fixed、浮动利率：float',
  `lpr_type` varchar(20) DEFAULT NULL COMMENT 'lpr品种 一年期，五年期',
  `lpr_percent` int(11) DEFAULT NULL COMMENT 'lpr',
  `lpr_add_percent` int(11) DEFAULT NULL COMMENT 'lpr加点',
  `aoc_rate_percent` int(11) DEFAULT NULL COMMENT '转让费率值。百分之多少',
  `irr_percent` int(11) DEFAULT NULL COMMENT '内部收益率。百分之多少',
  `repay_calc_type` varchar(40) DEFAULT NULL COMMENT '还款计算方式',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_contract_id` (`contract_id`) USING BTREE COMMENT '每个合同只能有一份报价'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='债权转让合同报价方案表';

-- 合同 债权转让报价方案版本表
CREATE TABLE `contract_aoc_price_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id	',
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `contract_amount` bigint(20) DEFAULT NULL COMMENT '合同金额',
  `credit_amount_loop` tinyint(1) DEFAULT NULL COMMENT '额度是否可循环',
  `aoc_credit_term` int(11) DEFAULT NULL COMMENT '转让额度有效期',
  `earnest_money` bigint(20) DEFAULT NULL COMMENT '保证金',
  `consulting_fee` bigint(20) DEFAULT NULL COMMENT '手续费',
  `rate_type` varchar(10) DEFAULT NULL COMMENT '转让费率类型。固定利率：fixed、浮动利率：float',
  `lpr_type` varchar(20) DEFAULT NULL COMMENT 'lpr品种 一年期，五年期',
  `lpr_percent` int(11) DEFAULT NULL COMMENT 'lpr',
  `lpr_add_percent` int(11) DEFAULT NULL COMMENT 'lpr加点',
  `aoc_rate_percent` int(11) DEFAULT NULL COMMENT '转让费率值。百分之多少',
  `irr_percent` int(11) DEFAULT NULL COMMENT '内部收益率。百分之多少',
  `repay_calc_type` varchar(40) DEFAULT NULL COMMENT '还款计算方式',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) unsigned DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='债权转让合同报价方案表';

alter table `contract_account` add column `repay_way` varchar(20) default null comment '回款方式';

alter table `contract_account_lib` add column `repay_way` varchar(20) default null comment '回款方式';

alter table `contract_account` add column `account_use` varchar(20) default null comment '账号用途';

alter table `contract_account_lib` add column `account_use` varchar(20) default null comment '账号用途';

ALTER TABLE proj_review_base_info ADD `zr_types` varchar(100) DEFAULT NULL COMMENT '转让类型。有追、无追' AFTER `factoring_types`;
ALTER TABLE proj_review_base_info_lib ADD `zr_types` varchar(100) DEFAULT NULL COMMENT '转让类型。有追、无追' AFTER `factoring_types`;

alter table `materials_list` add column `system_generate` tinyint(4) not null default 0 comment '是否系统生成文件，0-否，1-是';

ALTER TABLE proj_establish_factoring_price ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_establish_factoring_price_lib ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_establish_aoc_price ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_establish_aoc_price_lib ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_review_factoring_price ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_review_factoring_price_lib ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_review_aoc_price ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_review_aoc_price_lib ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';

alter table `contract_base_info` add column `zr_type` varchar(20) DEFAULT NULL COMMENT '转让类型';
alter table `contract_base_info_lib` add column `zr_type` varchar(20) DEFAULT NULL COMMENT '转让类型';

alter table `proj_establish_base_info` add column `assignor` varchar(200) DEFAULT NULL COMMENT '转让方（转租赁类型时）';
alter table `proj_establish_base_info_lib` add column `assignor` varchar(200) DEFAULT NULL COMMENT '转让方（转租赁类型时）';
alter table `proj_review_base_info` add column `assignor` varchar(200) DEFAULT NULL COMMENT '转让方（转租赁类型时）';
alter table `proj_review_base_info_lib` add column `assignor` varchar(200) DEFAULT NULL COMMENT '转让方（转租赁类型时）';
alter table `contract_base_info` add column `assignor` varchar(200) DEFAULT NULL COMMENT '转让方（转租赁类型时）';
alter table `contract_base_info_lib` add column `assignor` varchar(200) DEFAULT NULL COMMENT '转让方（转租赁类型时）';

update `contract_account` set `account_use` = 'ZLSK' where account_use is null;

update `contract_account_lib` set `account_use` = 'ZLSK' where account_use is null;

update `materials_list` set `system_generate` = 1 where `materials_type` in ('LEASE_CONTRACT', 'CONSULTING_CONTRACT', 'GUARANTEE_CONTRACT', 'MORTGAGE_CONTRACT');