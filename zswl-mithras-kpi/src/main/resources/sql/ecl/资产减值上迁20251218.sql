alter table ecl_execute_record add column promotion_result tinyint(2) DEFAULT 0 COMMENT '结果0不满足，1满足';
alter table ecl_execute_record_lib add column promotion_result tinyint(2) DEFAULT 0 COMMENT '结果0不满足，1满足';
