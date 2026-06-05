-- 客户模块增加两个字段 是否集团公司 所属集团
ALTER TABLE corp_commerce_info ADD group_flag TINYINT(4) DEFAULT NULL COMMENT '是否集团公司 1是，0否';
ALTER TABLE corp_commerce_info ADD belong_group_client_id BIGINT(20) DEFAULT NULL COMMENT '所属集团 法人客户id -1无 -2自己';
ALTER TABLE corp_commerce_info_lib ADD group_flag TINYINT(4) DEFAULT NULL COMMENT '是否集团公司 1是，0否';
ALTER TABLE corp_commerce_info_lib ADD belong_group_client_id BIGINT(20) DEFAULT NULL COMMENT '所属集团 法人客户id -1无 -2自己';
