-- 资产五级分类增加下载按钮
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('assetclassifysummaryfiledownload', '下载汇总审批表', 0, 208, NULL, NULL, NULL, 'POST', '/assetclassify/summaryfile/download', 1, NULL);

-- 银行流水从无需处理还原
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('bankcenterrestore', '银行流水还原至处理中心', 0, 495, NULL, NULL, NULL, 'POST', '/bank/center/restore', 2, NULL);

-------------------------------------------------------uat环境无需执行------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- 评级
INSERT INTO `bifrost_menu` (`gmt_create`, `gmt_modified`, `code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`, `id`, `en_name`, `target`, `create_by`, `update_by`) VALUES
    ('2024-06-12 16:05:44', '2024-06-12 16:14:20', 'customerCustomerRat', 3, 5, NULL, '/customer/customerRat', NULL, NULL, '评级管理', NULL, NULL, NULL, NULL, NULL);

INSERT INTO `bifrost_custom_tree_menu_ref` (`id`, `gmt_create`, `gmt_modified`, `create_by`, `update_by`, `custom_tree_id`, `menu_id`, `sort_no`) VALUES
    (NULL, '2024-06-12 16:07:12', '2024-06-12 16:10:23', NULL, NULL, (select id from bifrost_custom_tree where code = 'client'), (select id from bifrost_menu where code = 'customerCustomerRat'), 1);

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES
    ('2024-06-12 16:11:18', '2024-06-20 16:04:12', null, 'ratingClientAdd', '客户评级新增', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/add', 2, NULL),
    ('2024-06-20 16:04:39', '2024-06-20 16:04:39', null, 'ratingClientAccessCheck', '客户评级准入校验', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/accessCheck', 1, NULL),
    ('2024-06-20 16:05:03', '2024-06-20 16:05:03', null, 'ratingClientUpdate', '客户评级修改', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/update', 2, NULL),
    ('2024-06-20 16:05:23', '2024-06-20 16:05:23', null, 'ratingClientDelete', '客户评级删除', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/delete', 2, NULL),
    ('2024-06-20 16:05:38', '2024-06-20 16:05:41', null, 'ratingClientInfo', '客户信息', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/info', 1, NULL),
    ('2024-06-20 16:06:01', '2024-06-20 16:06:01', null, 'ratingModelQuery', '模型获取', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/modelQuery', 1, NULL),
    ('2024-06-20 16:06:29', '2024-06-20 16:06:29', null, 'ratingClientParamInfo', '问卷获取', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/paramInfo', 1, NULL),
    ('2024-06-20 16:06:46', '2024-06-20 16:06:46', null, 'ratingClientExecute', '试算', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/execute', 2, NULL),
    ('2024-06-20 16:07:01', '2024-06-20 16:07:01', null, 'ratingClientFinish', '确认完成评级/保存', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/finish', 2, NULL),
    ('2024-06-20 16:07:18', '2024-06-20 16:07:18', null, 'ratingClientPage', '客户评级列表', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/page', 1, NULL),
    ('2024-06-20 16:07:35', '2024-06-20 16:07:35', null, 'ratingClientDetail', '客户评级详情', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/detail', 1, NULL),
    ('2024-06-20 16:07:50', '2024-06-20 16:07:50', null, 'ratingClientEffect', '提交审批', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/effect', 2, NULL),
    ('2024-06-20 16:08:10', '2024-06-20 16:08:10', null, 'ratingClientIndexApproval', '指标审批', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/indexApproval', 2, NULL),
    ('2024-06-20 16:08:34', '2024-06-20 16:08:34', null, 'ratingClientReport', '评级报告', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/report', 1, NULL),
    ('2024-06-20 16:08:57', '2024-06-20 16:08:57', null, 'ratingClientOverturn', '评级推翻', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/overturn', 2, NULL),
    ('2024-06-20 16:09:13', '2024-06-20 16:09:13', null, 'ratingClientAbstract', '摘要信息', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/abstract', 1, NULL),
    ('2024-06-20 16:09:36', '2024-06-20 16:09:36', null, 'ratingClientOverturnRecord', '推翻记录', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/overturnRecord', 1, NULL),
    ('2024-06-20 16:11:05', '2024-06-20 16:11:05', null, 'ratingAmountPage', '债项-债项评级列表', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/page', 1, NULL),
    ('2024-06-20 16:11:25', '2024-06-20 16:11:25', null, 'ratingAmountDetail', '债项-债项评级详情', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/detail', 1, NULL),
    ('2024-06-20 16:11:44', '2024-06-20 16:11:44', null, 'ratingAmountLesseeInfo', '债项-债项评级评估主体下拉框', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/lesseeInfo', 1, NULL),
    ('2024-06-20 16:12:04', '2024-06-20 16:12:04', null, 'ratingAmountModelMatch', '债项-自动匹配模型', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/modelMatch', 1, NULL),
    ('2024-06-20 16:12:25', '2024-06-20 16:12:25', null, 'ratingAmountAdd', '债项-债项评级新增', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/add', 2, NULL),
    ('2024-06-20 16:12:55', '2024-06-20 16:12:55', null, 'ratingAmountAccessCheck', '债项-债项评级准入校验', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/accessCheck', 2, NULL),
    ('2024-06-20 16:13:13', '2024-06-20 16:13:13', null, 'ratingAmountUpdate', '债项-债项评级修改', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/update', 2, NULL),
    ('2024-06-20 16:13:31', '2024-06-20 16:13:31', null, 'ratingAmountDelete', '债项-债项评级删除', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/delete', 2, NULL),
    ('2024-06-20 16:13:52', '2024-06-20 16:13:52', null, 'ratingAmountProjInfo', '债项-债项评级项目信息', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/projInfo', 1, NULL),
    ('2024-06-20 16:14:07', '2024-06-20 16:14:07', null, 'ratingAmountParamInfo', '债项-问卷获取', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/paramInfo', 1, NULL),
    ('2024-06-20 16:14:25', '2024-06-20 16:14:25', null, 'ratingAmountExecute', '债项-试算', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/execute', 2, NULL),
    ('2024-06-20 16:14:48', '2024-06-20 16:14:48', null, 'ratingAmountFinish', '债项-确认完成评级/保存', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/finish', 2, NULL),
    ('2024-06-20 16:15:11', '2024-06-20 16:15:11', null, 'ratingAmountEffect', '债项-提交审批', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/effect', 2, NULL),
    ('2024-06-20 16:15:29', '2024-06-20 16:15:29', null, 'ratingAmountIndexApproval', '债项-指标审批', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/indexApproval', 2, NULL),
    ('2024-06-20 16:15:49', '2024-06-20 16:15:49', null, 'ratingAmountReport', '债项-评级报告', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/amount/report', 1, NULL),
    ('2024-06-26 20:07:58', '2024-06-26 20:07:58', NULL, 'lookSingeView', '查看视图', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', 'look/singeView', 1, NULL),
    ('2024-09-05 16:16:33', '2024-09-05 16:16:33', NULL, 'ratingClientIndexCheck', '指标校验', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/indexCheck', 1, NULL),
    ('2024-04-23 14:31:19', '2024-05-09 21:03:16', null, 'ratingClientFileUpload', '客户评级文件上传', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL),
    ('2024-04-23 14:31:49', '2024-05-09 21:03:33', null, 'ratingClientFileDownload', '客户评级文件下载', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'GET', '/file/download', 2, NULL),
    ('2024-04-23 14:32:36', '2024-05-09 21:03:33', null, 'ratingClientFileList', '客户评级文件列表', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/file/list', 2, NULL),
    ('2024-04-23 14:33:37', '2024-05-09 21:03:16', null, 'ratingClientFileBatchRemove', '客户评级文件删除', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/file/batch/remove', 2, NULL);

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-08-06 18:34:07', '2024-08-06 18:34:21', null, 'ratingClientAdjust', '客户评级调整', 0, (select id from bifrost_menu where code = 'customerCustomerRat'), NULL, NULL, NULL, 'POST', '/rating/client/adjust', 2, NULL);

ALTER TABLE `proj_establish_base_info` ADD COLUMN `rating_update_time` DATETIME COMMENT '评级更新时间';
ALTER TABLE `proj_establish_base_info_lib` ADD COLUMN `rating_update_time` DATETIME COMMENT '评级更新时间';
ALTER TABLE `proj_review_base_info` ADD COLUMN `rating_update_time` DATETIME COMMENT '评级更新时间';
ALTER TABLE `proj_review_base_info_lib` ADD COLUMN `rating_update_time` DATETIME COMMENT '评级更新时间';

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-08-26 11:38:00', '2024-08-26 11:38:00', null, 'projEstablishBaseInfoUpdateRating', '更新评级信息', 0, 8, NULL, NULL, NULL, 'POST', '/proj/establish/base/info/updateRating', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-08-26 11:38:28', '2024-08-26 11:38:28', null, 'projReviewBaseInfoUpdateRating', '更新评级信息', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/base/info/updateRating', 2, NULL);

-------------------------------------------------------uat环境无需执行 end------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
