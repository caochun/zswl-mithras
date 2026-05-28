ALTER TABLE proj_establish_base_info add column regional_project_classify varchar(20) COMMENT '地区分类' after evaluation_subject_id;
ALTER TABLE proj_establish_base_info_lib add column regional_project_classify varchar(20) COMMENT '地区分类' after evaluation_subject_id;

UPDATE proj_establish_base_info e, proj_review_base_info p set e.regional_project_classify = p.regional_project_classify where p.proj_establish_id = e.id;

-- 删除FTP无用的参数
DELETE FROM new_ftp_parameter_setting_config WHERE category = 'REGIONAL_CLASSIFICATION_MINIMUM_COMPENSATION_RATE';
-- 卖方账户带入接口
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('paymentsellerInfo', '付款申请-卖方账号信息', 0, 15, NULL, NULL, NULL, 'POST', '/payment/sellerInfo', 1, NULL);

-- 新增决议文件查看功能
INSERT INTO `bifrost_function` (`gmt_create`,`gmt_modified`,`id`,`code`,`name`,`sort_no`,`menu_id`,`create_by`,`update_by`,`en_name`,`method`,`path`,`type`,`group_id`) VALUES
    ('2024-04-16 19:47:52','2024-04-17 20:13:33',NULL,'resolutionFileDownLoad','决议文件查看',0,20,NULL,NULL,NULL,'GET','/materials/download/query',1,NULL);
-- 新增决议文件id字段
ALTER TABLE `contract_guarantor` ADD COLUMN `resolution_file_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '决议文件' AFTER `resolution_type`;
ALTER TABLE `contract_guarantor_lib` ADD COLUMN `resolution_file_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '决议文件' AFTER `resolution_type`;
ALTER TABLE `contract_tenantry` ADD COLUMN `resolution_file_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '决议文件' AFTER `resolution_type`;
ALTER TABLE `contract_tenantry_lib` ADD COLUMN `resolution_file_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '决议文件' AFTER `resolution_type`;