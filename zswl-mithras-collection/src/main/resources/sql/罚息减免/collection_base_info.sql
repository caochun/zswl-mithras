ALTER TABLE `collection_base_info`
    ADD COLUMN `penalty_interest_calculate_flag` TINYINT(1) NULL DEFAULT 0 COMMENT '罚息计算状态 0 计算，1 不再计算';
