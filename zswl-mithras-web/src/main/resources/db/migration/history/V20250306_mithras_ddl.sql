-- 公开信息拷贝
ALTER TABLE payment_base_info ADD COLUMN is_init_public_info TINYINT(4) DEFAULT 0 COMMENT '是否初始化了公开信息';
ALTER TABLE payment_base_info_lib ADD COLUMN is_init_public_info TINYINT(4) DEFAULT 0 COMMENT '是否初始化了公开信息';