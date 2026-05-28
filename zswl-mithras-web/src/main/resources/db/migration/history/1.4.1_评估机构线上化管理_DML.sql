INSERT INTO `bifrost_custom_tree` (`code`, `name`, `flag`, `sort_no`, `parent_id`, `create_by`, `update_by`, `en_name`, `icon`, `path`, `lang_env`)
VALUES
    ('appraisalCompanyWhitelist', '评估机构白名单', 1, 50, NULL, NULL, NULL, NULL, 'icon-baimingdan', NULL, NULL);

INSERT INTO `bifrost_menu` (`code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`, `en_name`, `target`, `create_by`, `update_by`)
VALUES
    ('appraisalCompanyWhitelist', 3, 0, 1, '/whiteList', NULL, NULL, '评估机构白名单', NULL, NULL, NULL, NULL);

INSERT INTO `bifrost_custom_tree_menu_ref` (`custom_tree_id`, `menu_id`, `sort_no`)
VALUES
    (222, 826, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('appraisalcompanywhitelistoutsubmit', '评估机构白名单-提交出库申请', 0, 826, NULL, NULL, NULL, 'POST', '/appraisalcompany/whitelist/out/submit', 2, NULL),
    ('appraisalcompanywhitelistsubmit', '评估机构白名单-提交审批', 0, 826, NULL, NULL, NULL, 'POST', '/appraisalcompany/whitelist/submit', 2, NULL),
    ('appraisalcompanywhitelistcommercerefresh', '评估机构白名单-更新工商信息', 0, 826, NULL, NULL, NULL, 'POST', '/appraisalcompany/whitelist/commerce/refresh', 2, NULL),
    ('appraisalcompanywhitelistdetail', '评估机构白名单-评估机构详情', 0, 826, NULL, NULL, NULL, 'POST', '/appraisalcompany/whitelist/detail', 1, NULL),
    ('appraisalcompanywhitelistadd', '评估机构白名单-新增', 0, 826, NULL, NULL, NULL, 'POST', '/appraisalcompany/whitelist/add', 2, NULL),
    ('appraisalcompanywhitelistpagelist', '评估机构白名单-分页列表', 0, 826, NULL, NULL, NULL, 'POST', '/appraisalcompany/whitelist/pagelist', 1, NULL),
    ('appraisalCompanyWhitelistFileBatchRemove', '评估机构白名单-文件批量删除', 0, 826, NULL, NULL, NULL, 'POST', '/file/batch/remove', 2, NULL),
    ('appraisalCompanyWhitelistFileDownload', '评估机构白名单-文件下载', 0, 826, NULL, NULL, NULL, 'GET', '/file/download', 1, NULL),
    ('appraisalCompanyWhitelistFileList', '评估机构白名单-文件列表', 0, 826, NULL, NULL, NULL, 'POST', '/file/list', 1, NULL),
    ('appraisalCompanyWhitelistFileUpload', '评估机构白名单-文件上传', 0, 826, NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL),
    ('appraisalcompanywhitelistdelete', '评估机构白名单-删除', 0, 826, NULL, NULL, NULL, 'POST', '/appraisalcompany/whitelist/delete', 2, NULL),
    ('appraisalcompanywhitelistcancel', '评估机构白名单-取消操作', 0, 826, NULL, NULL, NULL, 'POST', '/appraisalcompany/whitelist/cancel', 2, NULL),
    ('appraisalcompanywhitelistoutreasonsave', '评估机构白名单-保存出库原因', 0, 826, NULL, NULL, NULL, 'POST', '/appraisalcompany/whitelist/out/reason/save', 2, NULL);