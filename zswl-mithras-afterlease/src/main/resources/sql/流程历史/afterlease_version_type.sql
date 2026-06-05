ALTER TABLE after_lease_check_plan_base_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE after_lease_check_plan_project_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
