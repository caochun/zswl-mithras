ALTER TABLE `margin_record_info`
    ADD COLUMN `deleted` TINYINT(1) NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除，默认0';
