-- 所有lib表加字段version_type 版本标志，0无效，1有效...业务自扩展
ALTER TABLE common_version ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
