-- 项目立项保理/债权转让 债权人由单个变更为多个
alter table `proj_establish_base_info` add column `creditor_info` json default null comment '债权人信息';
alter table `proj_establish_base_info_lib` add column `creditor_info` json default null comment '债权人信息';

-- 项目评审保理/债权转让 债权人由单个变更为多个
alter table `proj_review_base_info` add column `creditor_info` json default null comment '债权人信息';
alter table `proj_review_base_info_lib` add column `creditor_info` json default null comment '债权人信息';
ALTER TABLE proj_review_base_info ADD `zr_types` varchar(100) DEFAULT NULL COMMENT '转让类型。有追、无追' AFTER `factoring_types`;
ALTER TABLE proj_review_base_info_lib ADD `zr_types` varchar(100) DEFAULT NULL COMMENT '转让类型。有追、无追' AFTER `factoring_types`;
ALTER TABLE proj_establish_factoring_price ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_establish_factoring_price_lib ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_establish_aoc_price ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_establish_aoc_price_lib ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_review_factoring_price ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_review_factoring_price_lib ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_review_aoc_price ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
ALTER TABLE proj_review_aoc_price_lib ADD `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率';
alter table `proj_establish_base_info` add column `assignor` varchar(200) DEFAULT NULL COMMENT '转让方（转租赁类型时）';
alter table `proj_establish_base_info_lib` add column `assignor` varchar(200) DEFAULT NULL COMMENT '转让方（转租赁类型时）';
alter table `proj_review_base_info` add column `assignor` varchar(200) DEFAULT NULL COMMENT '转让方（转租赁类型时）';
alter table `proj_review_base_info_lib` add column `assignor` varchar(200) DEFAULT NULL COMMENT '转让方（转租赁类型时）';
