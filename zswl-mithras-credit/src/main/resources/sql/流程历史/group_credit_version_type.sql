ALTER TABLE group_credit_establish_base_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE group_credit_review_base_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
