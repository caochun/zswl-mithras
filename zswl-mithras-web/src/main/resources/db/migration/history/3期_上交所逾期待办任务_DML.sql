INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('overdueListDraftImport', '票据逾期名单草稿数据导入', 0, 785, NULL, NULL, NULL, 'POST', '/overdueList/draft/import', 1, NULL),
    ('overdueListDraftList', '票据逾期名单草稿数据查询', 0, 785, NULL, NULL, NULL, 'POST', '/overdueList/draft/list', 1, NULL);