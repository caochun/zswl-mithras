# 加字段 干掉cr_pm_protocol表
ALTER TABLE cr_mortgage ADD `payment_apply_code` varchar(20) DEFAULT NULL COMMENT '借据编号';
ALTER TABLE cr_mortgage ADD `apply_payment_amount` bigint(20) DEFAULT NULL COMMENT '借据本金(单位：0.0001元)';
ALTER TABLE cr_pledge ADD `payment_apply_code` varchar(20) DEFAULT NULL COMMENT '借据编号';
ALTER TABLE cr_pledge ADD `apply_payment_amount` bigint(20) DEFAULT NULL COMMENT '借据本金(单位：0.0001元)';