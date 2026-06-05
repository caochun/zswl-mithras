-- 项目评审增加两个字段 数据来源类型、集团授信评审id
ALTER TABLE proj_review_base_info ADD relation_data_type VARCHAR(30) DEFAULT NULL COMMENT '数据来源类型，区分普通立项(PROJ_ESTABLISH)和集团授信(GROUP_CREDIT_REVIEW)';
ALTER TABLE proj_review_base_info ADD group_credit_review_id BIGINT(20) DEFAULT NULL COMMENT '集团授信评审id';
ALTER TABLE proj_review_base_info_lib ADD relation_data_type VARCHAR(30) DEFAULT NULL COMMENT '数据来源类型，区分普通立项(PROJ_ESTABLISH)和集团授信(GROUP_CREDIT_REVIEW)';
ALTER TABLE proj_review_base_info_lib ADD group_credit_review_id BIGINT(20) DEFAULT NULL COMMENT '集团授信评审id';
