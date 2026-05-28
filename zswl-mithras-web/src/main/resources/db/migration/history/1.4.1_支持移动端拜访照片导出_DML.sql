INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('appvisitcontractlist', 'APP拜访查询合同信息', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/contract/list', 1, NULL),
    ('apppcvisitfiledownload', '客户拜访文件批量下载', 0, 783, NULL, NULL, NULL, 'POST', '/app/pc/visit/file/download', 1, NULL),
    ('visitRecordFileList', '客户拜访-文件列表', 0, 783, NULL, NULL, NULL, 'POST', '/file/list', 1, NULL),
    ('apppcvisitfiledownloadtasklist', '下载任务列表', 0, 783, NULL, NULL, NULL, 'POST', '/app/pc/visit/file/download/task/list', 1, NULL);

INSERT INTO `bifrost_system_config` (`config_key`, `config_value`, `created_by`, `updated_by`, `description`, `status`, `type`)
VALUES
    ('visit_file_download_limit', '200', 'admin', 'admin', '客户拜访批量下载的拜访记录数量限制', 1, 'String');