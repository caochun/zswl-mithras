insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('paymentCloseBeforeCheck', '付款实际核销关闭前检查', 0, 16, 'POST', '/payment/close/before/check', 2),
        ('paymentSendAdvanceApplication', '推送银企直联付款申请单', 0, 16, 'POST', '/payment/send/advance/application', 2);


-- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 客户资料清单改造开始 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
-- 表结构变更
alter table `materials_list` add column `source_business_key` varchar(50) not null default '' comment '文件来源业务key（比如客户资料可能是在立项/评审阶段上传的）';
alter table `materials_list_lib` add column `source_business_key` varchar(50) not null default '' comment '文件来源业务key（比如客户资料可能是在立项/评审阶段上传的）';
alter table `materials_list` add key `idx_source_business_key` (`source_business_key`);
alter table `materials_list_lib` add key `idx_source_business_key` (`source_business_key`);
alter table onlyoffice_key_store add column file_id bigint(20) not null default 0 comment '文件表id';
alter table onlyoffice_key_store add column file_id_type tinyint(4) not null default 0 comment '文件表id类型，1-编辑区，2-版本区';

-- 历史数据订正-自然人
update `materials_list` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'ID_CARD' where `business_type` = 'CLIENT' and `materials_type` = 'ID_CARD' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'BASIC_INFORMATION_UNGROUPED' where `business_type` = 'CLIENT' and `materials_type` = 'HOUSEHOLD' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'MARRIAGE_CERT' where `business_type` = 'CLIENT' and `materials_type` = 'MARRIAGE_CERT' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'PERSONAL_CREDIT_REPORT' where `business_type` = 'CLIENT' and `materials_type` = 'PERSONAL_CREDIT_REPORT' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'BASIC_INFORMATION_UNGROUPED' where `business_type` = 'CLIENT' and `materials_type` = 'NAMED_ASSETS' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list` set `materials_type` = 'OTHERS', `materials_sub_type` = 'OTHER_INFORMATION' where `business_type` = 'CLIENT' and `materials_type` = 'OTHERS' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list_lib` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'ID_CARD' where `business_type` = 'CLIENT' and `materials_type` = 'ID_CARD' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list_lib` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'BASIC_INFORMATION_UNGROUPED' where `business_type` = 'CLIENT' and `materials_type` = 'HOUSEHOLD' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list_lib` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'MARRIAGE_CERT' where `business_type` = 'CLIENT' and `materials_type` = 'MARRIAGE_CERT' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list_lib` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'PERSONAL_CREDIT_REPORT' where `business_type` = 'CLIENT' and `materials_type` = 'PERSONAL_CREDIT_REPORT' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list_lib` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'BASIC_INFORMATION_UNGROUPED' where `business_type` = 'CLIENT' and `materials_type` = 'NAMED_ASSETS' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
update `materials_list_lib` set `materials_type` = 'OTHERS', `materials_sub_type` = 'OTHER_INFORMATION' where `business_type` = 'CLIENT' and `materials_type` = 'OTHERS' and `belong_id` in (select `id` from `client` where `client_type` = 'NORMAL');
-- 历史数据订正-法人
update `materials_list` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'BASIC_INFORMATION_UNGROUPED' where `business_type` = 'CLIENT' and `materials_type` = 'BASIC_INFORMATION' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list` set `materials_type` = 'LEASE_APPLICATION', `materials_sub_type` = 'LEASE_APPLICATION' where `business_type` = 'CLIENT' and `materials_type` = 'LEASE_APPLICATION' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list` set `materials_type` = 'CREDIT_LETTER', `materials_sub_type` = 'CREDIT_LETTER' where `business_type` = 'CLIENT' and `materials_type` = 'CREDIT_LETTER' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list` set `materials_type` = 'FINANCIAL_INFORMATION', `materials_sub_type` = 'FINANCIAL_INFORMATION_UNGROUPED' where `business_type` = 'CLIENT' and `materials_type` = 'FINANCIAL_INFORMATION' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list` set `materials_type` = 'BUSINESS_INFORMATION', `materials_sub_type` = 'BUSINESS_INFORMATION_UNGROUPED' where `business_type` = 'CLIENT' and `materials_type` = 'BUSINESS_INFORMATION' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list` set `materials_type` = 'OTHERS', `materials_sub_type` = 'OTHER_INFORMATION' where `business_type` = 'CLIENT' and `materials_type` = 'OTHERS' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list_lib` set `materials_type` = 'BASIC_INFORMATION', `materials_sub_type` = 'BASIC_INFORMATION_UNGROUPED' where `business_type` = 'CLIENT' and `materials_type` = 'BASIC_INFORMATION' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list_lib` set `materials_type` = 'LEASE_APPLICATION', `materials_sub_type` = 'LEASE_APPLICATION' where `business_type` = 'CLIENT' and `materials_type` = 'LEASE_APPLICATION' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list_lib` set `materials_type` = 'CREDIT_LETTER', `materials_sub_type` = 'CREDIT_LETTER' where `business_type` = 'CLIENT' and `materials_type` = 'CREDIT_LETTER' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list_lib` set `materials_type` = 'FINANCIAL_INFORMATION', `materials_sub_type` = 'FINANCIAL_INFORMATION_UNGROUPED' where `business_type` = 'CLIENT' and `materials_type` = 'FINANCIAL_INFORMATION' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list_lib` set `materials_type` = 'BUSINESS_INFORMATION', `materials_sub_type` = 'BUSINESS_INFORMATION_UNGROUPED' where `business_type` = 'CLIENT' and `materials_type` = 'BUSINESS_INFORMATION' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');
update `materials_list_lib` set `materials_type` = 'OTHERS', `materials_sub_type` = 'OTHER_INFORMATION' where `business_type` = 'CLIENT' and `materials_type` = 'OTHERS' and `belong_id` in (select `id` from `client` where `client_type` = 'CORPORATION');

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('projreviewclientmaterialscheck', '项目评审-检查客户材料是否缺少', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/client/materials/check', 1, NULL),
    ('projReviewFileUpload', '项目评审-上传文件', 0, 12, NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL),
    ('projPricingFileUpload', '项目定价-上传文件', 0, 733, NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL),
    ('projEstablishFileUpload', '项目立项-上传文件', 0, 8, NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL);
-- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 客户资料清单改造结束 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

-- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> OCR识别优化开始 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
-- 增加字段
alter table lease_item_vehicle_registration_certificate add column picture_count int(10) NOT NULL DEFAULT '0' COMMENT '图片张数';
-- 数据初始化
update lease_item_vehicle_registration_certificate set picture_count = (ifnull(JSON_LENGTH(change_record),0) + if(file_id is null, 0, 1));
-- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> OCR识别优化结束 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

-- 运营工作台 观远接口注册
INSERT INTO `guanyuan_ds_info` (`id`, `business_key`, `guanyuan_ds_id`, `guanyuan_ds_remark`, `deleted`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (NULL, 'OperationYYContractApproval', 'k3ebd2fd4179145c8ae2ca7a', '运营合同审批-时效', 0, NULL, '2024-05-21 17:25:49', NULL, '2024-11-13 11:11:20');
INSERT INTO `guanyuan_ds_info` (`id`, `business_key`, `guanyuan_ds_id`, `guanyuan_ds_remark`, `deleted`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (NULL, 'OperationYYContractApprovalArrive', 'le1658aa4294a492482a3eb8', '运营合同审批-节点到达时间-时效', 0, NULL, '2024-05-21 17:25:49', NULL, '2024-11-13 19:09:56');

-- 征信文件上传-功能注册
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ('creditReportFileBatchRemove', '征信报送文件删除', 0, 248, 'POST', '/file/batch/remove', 2),
       ('creditReportFileList', '征信报送文件列表', 0, 248,  'POST', '/file/list', 2),
       ('creditReportFileDownload', '征信报送文件下载', 0, 248, 'GET', '/file/download', 2),
       ( 'creditReportFileUpload', '征信报送文件上传', 0, 248, 'POST', '/file/upload', 2);
