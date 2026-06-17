ALTER TABLE proj_establish_base_info ADD INDEX `proj_establish_base_info_proj_establish_status_IDX` (`proj_establish_status`) USING BTREE;

ALTER TABLE proj_review_base_info ADD INDEX `proj_review_ proj_sponsor_user_id` (`proj_sponsor_user_id`) USING BTREE COMMENT '主办索引';
ALTER TABLE proj_review_base_info ADD INDEX `proj_review_base_info_relation_data_type_IDX` (`relation_data_type`,`proj_review_status`) USING BTREE;
ALTER TABLE proj_review_base_info ADD INDEX `idx_proj_establish_id` (`proj_establish_id`) USING BTREE;
