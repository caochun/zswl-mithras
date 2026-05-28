ALTER TABLE policy_info ADD expiration_reminder_flag TINYINT(1) default 0 COMMENT '保单到期标识 0， 1，已通知';
ALTER TABLE policy_info_lib ADD expiration_reminder_flag TINYINT(1) default 0 COMMENT '保单到期标识 0， 1，已通知';

ALTER TABLE policy_info ADD renewal_overdue_flag TINYINT(1) default 0 COMMENT '续保逾期标识 0未通知， 1，已通知';
ALTER TABLE policy_info_lib ADD renewal_overdue_flag TINYINT(1) default 0 COMMENT '续保逾期标识 0未通知， 1，已通知';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('policyLedgerRenewInsurance', '保单台账-保单续保信息', 0, (select id from bifrost_menu where code = 'afterLeasepolicyManage'), null, null, null, 'POST',
        '/policy/ledger/renew/insurance', 2, null);


ALTER TABLE policy_info_lib ADD contract_id BIGINT(20) NULL COMMENT '合同id';
ALTER TABLE policy_info_lib ADD payment_id BIGINT(20) NULL COMMENT '付款ID';
ALTER TABLE policy_info_lib ADD payment_policy_id BIGINT(20) NULL COMMENT '付款保单ID';
ALTER TABLE policy_info_lib ADD policy_type varchar(50) NULL COMMENT '保单种类';
ALTER TABLE policy_info_lib ADD renew_insurance_flag varchar(50) NULL COMMENT '是否续保';
ALTER TABLE policy_info_lib ADD remark varchar(500) NULL COMMENT '备注';
ALTER TABLE policy_info_lib ADD parent_id BIGINT(20) NULL COMMENT '父id';
ALTER TABLE policy_info_lib ADD level INT(11) NULL COMMENT '层级';
ALTER TABLE policy_info_lib ADD notice_flag INT(11) NULL COMMENT '通知标识 0未通知， 1，已通知';


# App开始 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

-- APP拜访记录模糊搜索境内客户名称
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 10:42:13', '2024-12-10 10:42:13', 10897, 'appvisitqueryCompany', 'APP拜访记录模糊搜索境内客户名称', 0, 6, NULL, NULL, NULL, 'POST', '/app/visit/queryCompany', 2, NULL);


-- 统一视图-流程抄送

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 10:49:30', '2024-12-10 10:49:30', 10898, 'myReceivecclist', '统一视图-流程抄送', 0, 730, NULL, NULL, NULL, 'POST', '/dashboard/todo/myReceive/cc/list', 2, NULL);



-- 租金支付通知关键信息
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 10:57:43', '2024-12-10 10:57:43', 10899, 'processpreparekeylist', '租金支付通知关键信息', 0, 10, NULL, NULL, NULL, 'POST', '/process/prepare/key/list', 2, NULL);



-- appapi所有的方法

-- 拜访明细
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-04 17:14:59', '2024-12-04 17:14:59', 10875, 'apppcvisitlist', '拜访明细', 0, 783, NULL, NULL, NULL, 'POST', '/app/pc/visit/list', 2, NULL);

-- 拜访汇总
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-04 17:15:24', '2024-12-04 17:15:24', 10876, 'apppcvisitsummary', '拜访汇总', 0, 783, NULL, NULL, NULL, 'POST', '/app/pc/visit/summary', 2, NULL);

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:09:37', '2024-12-10 14:09:37', 10926, 'appauthoritydetail', 'APP功能权限-详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/authority/detail', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:09:14', '2024-12-10 14:09:14', 10925, 'appcontractsignupdate', 'APP合同面签-更新签约', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/sign/update', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:08:44', '2024-12-10 14:08:44', 10924, 'appcontractpaysign', 'APP合同面签-是否已签约', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/pay/sign', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:08:13', '2024-12-10 14:08:13', 10923, 'appcontractsigndetail', 'APP合同面签-详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/sign/detail', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:07:16', '2024-12-10 14:07:16', 10922, 'appcontractsigncopy', 'APP合同面签-拷贝其它合同视频和图片', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/sign/copy', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:06:45', '2024-12-10 14:06:45', 10921, 'appcontractprojsigned', 'APP合同面签-同项目其它合同是否已有签约照片视频', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/proj/signed', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:06:07', '2024-12-10 14:06:07', 10920, 'appcontractexisted', 'APP合同面签-合同是否已有签约照片视频', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/existed', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:05:39', '2024-12-10 14:05:39', 10919, 'appcontractsign', 'APP合同面签-去签署', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/sign', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:05:13', '2024-12-10 14:05:13', 10918, 'appcontractsignlist', 'APP合同面签-签约列表', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/sign/list', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:04:46', '2024-12-10 14:04:46', 10917, 'appcalendarmonthlylist', 'APP还款日历-每月汇总', 0, 783, NULL, NULL, NULL, 'POST', '/app/calendar/monthly/list', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:04:18', '2024-12-10 14:04:18', 10916, 'appcalendardailydetail', 'APP还款日历-还款详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/calendar/daily/detail', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:03:43', '2024-12-10 14:03:43', 10915, 'appcalendardailylist', 'APP还款日历-我的日历/团队日历', 0, 783, NULL, NULL, NULL, 'POST', '/app/calendar/daily/list', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:03:07', '2024-12-10 14:03:07', 10914, 'appcashFlowgenerationexecute', 'APP报价试算-生产现金流', 0, 783, NULL, NULL, NULL, 'POST', '/app/cashFlow/generation/execute', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:02:35', '2024-12-10 14:02:35', 10913, 'appprojcontractbase', 'APP我的项目-我的合同数目', 0, 783, NULL, NULL, NULL, 'POST', '/app/proj/contract/base', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:59:21', '2024-12-10 13:59:21', 10912, 'appprojcontractdetail', 'APP我的项目-我的合同信息', 0, 783, NULL, NULL, NULL, 'POST', '/app/proj/contract/detail', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:58:40', '2024-12-10 13:58:40', 10911, 'appprojinfodetail', 'APP我的项目-我的项目信息', 0, 783, NULL, NULL, NULL, 'POST', '/app/proj/info/detail', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:58:09', '2024-12-10 13:58:09', 10910, 'appprojinfolist', 'APP我的项目-我的项目列表', 0, 783, NULL, NULL, NULL, 'POST', '/app/proj/info/list', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:57:38', '2024-12-10 13:57:38', 10909, 'appclientdetail', 'APP我的客户-拜访详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/client/detail', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:57:12', '2024-12-10 13:57:12', 10908, 'appclientcontractlist', 'APP我的客户-合同信息', 0, 783, NULL, NULL, NULL, 'POST', '/app/client/contract/list', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:56:39', '2024-12-10 13:56:39', 10907, 'appclientlist', 'APP我的客户详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/client/list', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:56:11', '2024-12-10 13:56:11', 10906, 'appvisitdetail', 'APP拜访记录详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/detail', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:55:46', '2024-12-10 13:55:46', 10905, 'appvisitinvalid', 'APP作废打卡记录', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/invalid', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:55:21', '2024-12-10 13:55:21', 10904, 'appvisitqueryCheckPlan', 'APP拜访记录租后检查', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/queryCheckPlan', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:54:55', '2024-12-10 13:54:55', 10903, 'appvisitqueryProjEstablish', 'APP拜访记录立项', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/queryProjEstablish', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:54:28', '2024-12-10 13:54:28', 10902, 'appvisitlist', 'APP拜访记录', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/list', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:54:04', '2024-12-10 13:54:04', 10901, 'appvisitRecheckIn', 'APP漏打卡', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/RecheckIn', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:49:41', '2024-12-10 13:49:41', 10900, 'appvisitcheckIn', 'APP拜访打卡', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/checkIn', 2, NULL);

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-13 19:10:20', '2024-12-13 19:10:20', 10931, 'appclientimageToPdf', 'APP我的客户-照片转pdf', 0, 783, NULL, NULL, NULL, 'POST', '/app/client/imageToPdf', 2, NULL);

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-13 16:45:15', '2024-12-13 17:06:40', 10930, 'appContractSignFileUpload', '上传文件', 0, 783, NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-13 16:44:30', '2024-12-13 17:06:43', 10929, 'appContractSignFileBatchDownload', '文件批量下载', 0, 783, NULL, NULL, NULL, 'POST', '/file/batch/download', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-13 16:43:23', '2024-12-13 17:06:45', 10928, 'appContractSignFileBatchRemove', '文件批量删除', 0, 783, NULL, NULL, NULL, 'POST', '/file/batch/remove', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-13 16:33:53', '2024-12-13 17:06:48', 10927, 'appContractSignContractFileUpload', '上传合同文件', 0, 783, NULL, NULL, NULL, 'POST', '/contract/file/upload', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-16 09:10:11', '2024-12-16 09:10:11', 10932, 'appfilerename', '重命名文件', 0, 783, NULL, NULL, NULL, 'POST', '/file/rename', 2, NULL);



-- 新建数据库表
CREATE TABLE `visit_record` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                `client_name` varchar(100) DEFAULT NULL COMMENT '客户名称',
                                `visit_way` varchar(50) DEFAULT NULL COMMENT '拜访方式',
                                `visit_type` varchar(50) DEFAULT NULL COMMENT '拜访类型',
                                `visit_phase` varchar(50) DEFAULT NULL COMMENT '拜访阶段',
                                `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
                                `check_plan_id` bigint(20) DEFAULT NULL COMMENT '租后检查计划id',
                                `check_in_date` datetime DEFAULT NULL COMMENT '打卡日期/补卡日期',
                                `check_in_location` varchar(200) DEFAULT NULL COMMENT '打卡地点/补卡地点',
                                `user_id` bigint(20) NOT NULL COMMENT '客户经理id',
                                `dept_id` bigint(20) NOT NULL COMMENT '部门id',
                                `status` varchar(20) DEFAULT NULL COMMENT '记录状态',
                                `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='拜访记录信息';



CREATE TABLE `app_contract_sign` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                     `contract_id` bigint(20) NOT NULL COMMENT '合同id',
                                     `user_id` bigint(20) DEFAULT NULL COMMENT '客户经理id',
                                     `file_id` bigint(20) NOT NULL COMMENT '文件id',
                                     `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除:是否已作废',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='合同面签信息';






-- 新增字段
ALTER TABLE `contract_base_info` ADD `is_signed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否签约';
ALTER TABLE `contract_base_info_lib` ADD `is_signed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否签约';



-- 起租，结清，新建，作废都是已签约，生效的都按未签约

update contract_base_info set is_signed = 1 WHERE contract_status in ('START_RENT', 'INVALID', 'NEW', 'SETTLE');

update contract_base_info_lib set is_signed = 1 WHERE contract_status in ('START_RENT', 'INVALID', 'NEW', 'SETTLE');

# App结束 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

# ====================================合同网签开始==================================
# 这里的菜单先执行,后续接口注册需要使用
/*INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name)
VALUES ('QX0119', 3, 8, '0', '/contract/sign', null, 'org', '合同文本管理', 'contract');

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
VALUES (9, 784, 4);*/

CREATE TABLE `contract_text_manage` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                        `contract_id` bigint(20) NOT NULL COMMENT '关联合同id',
                                        `sign_way` varchar(32) DEFAULT NULL COMMENT '签约方式',
                                        `push_time` datetime DEFAULT NULL COMMENT '推送时间',
                                        `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                        `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                        `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_contract_id_deleted` (`contract_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同文本管理表';

CREATE TABLE `contract_text_sign_info` (
                                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                           `main_id` bigint(20) NOT NULL COMMENT '关联contract_text_manage主键id',
                                           `source_file_id` bigint(20) NOT NULL COMMENT '关联materials_list主键id',
                                           `text_sign_way` varchar(32) DEFAULT NULL COMMENT '文本签约方式',
                                           `text_sign_status` varchar(32) DEFAULT NULL COMMENT '文本签约状态',
                                           `push_time` datetime DEFAULT NULL COMMENT '推送时间',
                                           `signed_file_id` bigint(20) DEFAULT NULL COMMENT '签署完成的文件id',
                                           `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                           `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                           `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           `converted_file_id` bigint(20) DEFAULT NULL COMMENT '转换完成的文件id',
                                           `qys_document_id` bigint(20) DEFAULT NULL COMMENT '上传契约锁平台的文档id',
                                           `qys_contract_id` bigint(20) DEFAULT NULL COMMENT '契约锁平台的合同id',
                                           PRIMARY KEY (`id`),
                                           KEY `idx_main_id_file_id_deleted` (`main_id`,`source_file_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同文件签约信息表';

CREATE TABLE `qiyuesuo_invoke_log` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                       `method` varchar(512) NOT NULL COMMENT '调用方法',
                                       `param` text NOT NULL COMMENT '调用参数',
                                       `result` text NOT NULL COMMENT '调用结果',
                                       `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                       `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='契约锁调用日志记录表';

ALTER TABLE contract_sign_info ADD sign_way              varchar(32) NULL COMMENT '签约方式';
ALTER TABLE contract_sign_info ADD sign_status           varchar(32) NULL COMMENT '签约状态';
ALTER TABLE contract_sign_info ADD sign_finish_time      datetime    NULL COMMENT '签约完成时间';
ALTER TABLE contract_sign_info ADD real_name_auth_status varchar(32) NULL COMMENT '实名认证状态';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
VALUES ('contractTextManageList', '合同文本管理-台账列表', 0, 784, 'POST', '/contract/text/manage/list', 2, null),
       ('contractTextManageSignPhotosAndVideos', '合同文本管理-合同签署照片和视频', 0, 784, 'POST',
        '/contract/text/manage/sign/photos/and/videos', 2, null),
       ('contractTextManageDownloadWaitSign', '合同文本管理-待签约批量下载', 0, 784, 'POST',
        '/contract/text/manage/download/wait/sign', 2, null),
       ('contractTextManageSingleSign', '合同文本管理-单个客户用印', 0, 784, 'POST',
        '/contract/text/manage/single/sign', 2, null),
       ('contractTextManageDownloadAll', '合同文本管理-合同文本下载', 0, 784, 'POST',
        '/contract/text/manage/download/all', 2, null),
       ('contractTextManageUpdateSigningWayDefault', '合同文本管理-修改默认的签约方式', 0, 784, 'POST',
        '/contract/text/manage/update/signing/way/default', 2, null),
       ('contractTextManageUpdateSigningWaySingle', '合同文本管理-修改单个文件的签约方式', 0, 784, 'POST',
        '/contract/text/manage/update/signing/way/single', 2, null),
       ('contractTextManageBatchSign', '合同文本管理-批量用印', 0, 784, 'POST', '/contract/text/manage/batch/sign', 2,
        null),
       ('contractTextManageSignedDetail', '合同文本管理-已签约详情', 0, 784, 'POST',
        '/contract/text/manage/signed/detail', 2, null),
       ('contractTextManageUnSignedDetail', '合同文本管理-未签约详情', 0, 784, 'POST',
        '/contract/text/manage/unSigned/detail', 2, null),
       ('contractTextManageFileBatchRemove', '合同文本管理-已签约文件删除', 0, 784, 'POST',
        '/file/batch/remove', 2, null),
       ('contractTextManageFileUpload', '合同文本管理-已签约文件上传', 0, 784, 'POST',
        '/file/upload', 2, null),
       ('contractTextManageFileDownload', '合同文本管理-文件下载', 0, 784, 'GET', '/file/download', 2, null);

# ===================================合同网签结束===================================================================

ALTER TABLE file_template ADD COLUMN file_template_key VARCHAR(64) NULL COMMENT '文件模版Key,新建模版必须有';
ALTER TABLE file_template ADD COLUMN face_sign_show_flag VARCHAR(64) NULL COMMENT '合同面签是否需要展示';
ALTER TABLE contract_sign_info ADD COLUMN file_template_key VARCHAR(64) NULL COMMENT '文件模版Key,新建模版必须有';
ALTER TABLE contract_sign_info ADD COLUMN face_sign_show_flag VARCHAR(64) NULL COMMENT '合同面签是否需要展示';


-- 直融费用项数据订正（不可重复执行）
update fund_direct_financing_fee_detail set amount = amount * 10000;