-- 提前还款优化开始 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
alter table contract_prepayment add column is_early_settle tinyint(1) default null comment '是否提前结清，0-否，1-是' after contract_id;
alter table contract_prepayment_lib add column is_early_settle tinyint(1) default null comment '是否提前结清，0-否，1-是' after contract_id;

alter table contract_prepayment add column penalty_derate_type varchar(20) default null comment '违约金减免方式' after penalty;
alter table contract_prepayment_lib add column penalty_derate_type varchar(20) default null comment '违约金减免方式' after penalty;
alter table contract_prepayment add column penalty_derate_percent int(10) default null comment '违约金减免百分比' after penalty_derate_type;
alter table contract_prepayment_lib add column penalty_derate_percent int(10) default null comment '违约金减免百分比' after penalty_derate_type;
alter table contract_prepayment add column penalty_derate_amount bigint(20) default null comment '违约金减免金额' after penalty_derate_percent;
alter table contract_prepayment_lib add column penalty_derate_amount bigint(20) default null comment '违约金减免金额' after penalty_derate_percent;

alter table contract_prepayment add column loss_derate_type varchar(20) default null comment '提前终止补偿金减免方式' after loss;
alter table contract_prepayment_lib add column loss_derate_type varchar(20) default null comment '提前终止补偿金减免方式' after loss;
alter table contract_prepayment add column loss_derate_percent int(10) default null comment '提前终止补偿金减免百分比' after loss_derate_type;
alter table contract_prepayment_lib add column loss_derate_percent int(10) default null comment '提前终止补偿金减免百分比' after loss_derate_type;
alter table contract_prepayment modify column apply_derate_amount bigint(20) DEFAULT NULL COMMENT '提前终止补偿金减免金额' after loss_derate_type;
alter table contract_prepayment_lib modify column apply_derate_amount bigint(20) DEFAULT NULL COMMENT '提前终止补偿金减免金额' after loss_derate_type;

alter table contract_prepayment add column is_earnest_money_deduction tinyint(1) default null comment '保证金是否抵扣，0-否，1-是' after is_early_settle;
alter table contract_prepayment_lib add column is_earnest_money_deduction tinyint(1) default null comment '保证金是否抵扣，0-否，1-是' after is_early_settle;
alter table contract_prepayment add column earnest_money_balance bigint(20) default null comment '保证金余额' after is_earnest_money_deduction;
alter table contract_prepayment_lib add column earnest_money_balance bigint(20) default null comment '保证金余额' after is_earnest_money_deduction;
alter table contract_prepayment add column earnest_money_deduction_amount bigint(20) default null comment '保证金抵扣金额' after earnest_money_balance;
alter table contract_prepayment_lib add column earnest_money_deduction_amount bigint(20) default null comment '保证金抵扣金额' after earnest_money_balance;

alter table contract_prepayment add column remark text default null comment '备注说明' after unpaid_rent;
alter table contract_prepayment_lib add column remark text default null comment '备注说明' after unpaid_rent;

alter table contract_prepayment add column before_maturity_principal bigint(20) default null comment '未到期本金' after unpaid_rent_due;
alter table contract_prepayment_lib add column before_maturity_principal bigint(20) default null comment '未到期本金' after unpaid_rent_due;

alter table contract_prepayment add column before_maturity_interest bigint(20) default null comment '未到期利息' after before_maturity_principal;
alter table contract_prepayment_lib add column before_maturity_interest bigint(20) default null comment '未到期利息' after before_maturity_principal;

alter table contract_prepayment modify column early_repayment bigint(20) default NULL COMMENT '提前归还本金';
alter table contract_prepayment_lib modify column early_repayment bigint(20) default NULL COMMENT '提前归还本金';
-- 提前还款优化结束 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



-- App开始 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

//APP拜访记录模糊搜索境内客户名称
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 10:42:13', '2024-12-10 10:42:13', 10897, 'appvisitqueryCompany', 'APP拜访记录模糊搜索境内客户名称', 0, 6, NULL, NULL, NULL, 'POST', '/app/visit/queryCompany', 2, NULL);


//统一视图-流程抄送

INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 10:49:30', '2024-12-10 10:49:30', 10898, 'myReceivecclist', '统一视图-流程抄送', 0, 748, NULL, NULL, NULL, 'POST', '/dashboard/todo/myReceive/cc/list', 2, NULL);



//租金支付通知关键信息
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 10:57:43', '2024-12-10 10:57:43', 10899, 'processpreparekeylist', '租金支付通知关键信息', 0, 10, NULL, NULL, NULL, 'POST', '/process/prepare/key/list', 2, NULL);



//appapi所有的方法

//拜访明细
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-04 17:14:59', '2024-12-04 17:14:59', 10875, 'apppcvisitlist', '拜访明细', 0, 783, NULL, NULL, NULL, 'POST', '/app/pc/visit/list', 2, NULL);

//拜访汇总
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-04 17:15:24', '2024-12-04 17:15:24', 10876, 'apppcvisitsummary', '拜访汇总', 0, 783, NULL, NULL, NULL, 'POST', '/app/pc/visit/summary', 2, NULL);

INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:09:37', '2024-12-10 14:09:37', 10926, 'appauthoritydetail', 'APP功能权限-详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/authority/detail', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:09:14', '2024-12-10 14:09:14', 10925, 'appcontractsignupdate', 'APP合同面签-更新签约', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/sign/update', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:08:44', '2024-12-10 14:08:44', 10924, 'appcontractpaysign', 'APP合同面签-是否已签约', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/pay/sign', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:08:13', '2024-12-10 14:08:13', 10923, 'appcontractsigndetail', 'APP合同面签-详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/sign/detail', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:07:16', '2024-12-10 14:07:16', 10922, 'appcontractsigncopy', 'APP合同面签-拷贝其它合同视频和图片', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/sign/copy', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:06:45', '2024-12-10 14:06:45', 10921, 'appcontractprojsigned', 'APP合同面签-同项目其它合同是否已有签约照片视频', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/proj/signed', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:06:07', '2024-12-10 14:06:07', 10920, 'appcontractexisted', 'APP合同面签-合同是否已有签约照片视频', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/existed', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:05:39', '2024-12-10 14:05:39', 10919, 'appcontractsign', 'APP合同面签-去签署', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/sign', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:05:13', '2024-12-10 14:05:13', 10918, 'appcontractsignlist', 'APP合同面签-签约列表', 0, 783, NULL, NULL, NULL, 'POST', '/app/contract/sign/list', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:04:46', '2024-12-10 14:04:46', 10917, 'appcalendarmonthlylist', 'APP还款日历-每月汇总', 0, 783, NULL, NULL, NULL, 'POST', '/app/calendar/monthly/list', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:04:18', '2024-12-10 14:04:18', 10916, 'appcalendardailydetail', 'APP还款日历-还款详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/calendar/daily/detail', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:03:43', '2024-12-10 14:03:43', 10915, 'appcalendardailylist', 'APP还款日历-我的日历/团队日历', 0, 783, NULL, NULL, NULL, 'POST', '/app/calendar/daily/list', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:03:07', '2024-12-10 14:03:07', 10914, 'appcashFlowgenerationexecute', 'APP报价试算-生产现金流', 0, 783, NULL, NULL, NULL, 'POST', '/app/cashFlow/generation/execute', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 14:02:35', '2024-12-10 14:02:35', 10913, 'appprojcontractbase', 'APP我的项目-我的合同数目', 0, 783, NULL, NULL, NULL, 'POST', '/app/proj/contract/base', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:59:21', '2024-12-10 13:59:21', 10912, 'appprojcontractdetail', 'APP我的项目-我的合同信息', 0, 783, NULL, NULL, NULL, 'POST', '/app/proj/contract/detail', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:58:40', '2024-12-10 13:58:40', 10911, 'appprojinfodetail', 'APP我的项目-我的项目信息', 0, 783, NULL, NULL, NULL, 'POST', '/app/proj/info/detail', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:58:09', '2024-12-10 13:58:09', 10910, 'appprojinfolist', 'APP我的项目-我的项目列表', 0, 783, NULL, NULL, NULL, 'POST', '/app/proj/info/list', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:57:38', '2024-12-10 13:57:38', 10909, 'appclientdetail', 'APP我的客户-拜访详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/client/detail', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:57:12', '2024-12-10 13:57:12', 10908, 'appclientcontractlist', 'APP我的客户-合同信息', 0, 783, NULL, NULL, NULL, 'POST', '/app/client/contract/list', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:56:39', '2024-12-10 13:56:39', 10907, 'appclientlist', 'APP我的客户详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/client/list', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:56:11', '2024-12-10 13:56:11', 10906, 'appvisitdetail', 'APP拜访记录详情', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/detail', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:55:46', '2024-12-10 13:55:46', 10905, 'appvisitinvalid', 'APP作废打卡记录', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/invalid', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:55:21', '2024-12-10 13:55:21', 10904, 'appvisitqueryCheckPlan', 'APP拜访记录租后检查', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/queryCheckPlan', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:54:55', '2024-12-10 13:54:55', 10903, 'appvisitqueryProjEstablish', 'APP拜访记录立项', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/queryProjEstablish', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:54:28', '2024-12-10 13:54:28', 10902, 'appvisitlist', 'APP拜访记录', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/list', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:54:04', '2024-12-10 13:54:04', 10901, 'appvisitRecheckIn', 'APP漏打卡', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/RecheckIn', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-10 13:49:41', '2024-12-10 13:49:41', 10900, 'appvisitcheckIn', 'APP拜访打卡', 0, 783, NULL, NULL, NULL, 'POST', '/app/visit/checkIn', 2, NULL);

INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-13 19:10:20', '2024-12-13 19:10:20', 10931, 'appclientimageToPdf', 'APP我的客户-照片转pdf', 0, 783, NULL, NULL, NULL, 'POST', '/app/client/imageToPdf', 2, NULL);

INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-13 16:45:15', '2024-12-13 17:06:40', 10930, 'appContractSignFileUpload', '上传文件', 0, 783, NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-13 16:44:30', '2024-12-13 17:06:43', 10929, 'appContractSignFileBatchDownload', '文件批量下载', 0, 783, NULL, NULL, NULL, 'POST', '/file/batch/download', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-13 16:43:23', '2024-12-13 17:06:45', 10928, 'appContractSignFileBatchRemove', '文件批量删除', 0, 783, NULL, NULL, NULL, 'POST', '/file/batch/remove', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-13 16:33:53', '2024-12-13 17:06:48', 10927, 'appContractSignContractFileUpload', '上传合同文件', 0, 783, NULL, NULL, NULL, 'POST', '/contract/file/upload', 2, NULL);
INSERT INTO `mithras`.`bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-12-16 09:10:11', '2024-12-16 09:10:11', 10932, 'appfilerename', '重命名文件', 0, 783, NULL, NULL, NULL, 'POST', '/file/rename', 2, NULL);



//新建数据库表
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



CREATE TABLE `contract_sign_info` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `contract_id` bigint(20) NOT NULL COMMENT '合同id',
                                      `file_id` bigint(20) NOT NULL COMMENT '文件id',
                                      `signatory` bigint(20) NOT NULL COMMENT '签约人id，如果是我方则为0，如果是对方则为对应客户id',
                                      `sign_keyword` varchar(50) NOT NULL DEFAULT '' COMMENT '签章关键字',
                                      `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                      `sign_way` varchar(32) DEFAULT NULL COMMENT '签约方式',
                                      `sign_status` varchar(32) DEFAULT NULL COMMENT '签约状态',
                                      `sign_finish_time` datetime DEFAULT NULL COMMENT '签约完成时间',
                                      `real_name_auth_status` varchar(32) DEFAULT NULL COMMENT '实名认证状态',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_contract_id` (`contract_id`,`deleted`),
                                      KEY `idx_file_id` (`file_id`,`deleted`),
                                      KEY `idx_signatory` (`signatory`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=590 DEFAULT CHARSET=utf8mb4 COMMENT='合同签约信息';




//新增字段
ALTER TABLE `contract_base_info` ADD `is_signed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否签约'
ALTER TABLE `contract_base_info_lib` ADD `is_signed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否签约'



//新增菜单-访客管理
INSERT INTO `mithras`.`bifrost_menu` (`gmt_create`, `gmt_modified`, `code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`, `id`, `en_name`, `target`, `create_by`, `update_by`) VALUES ('2024-12-04 17:03:03', '2024-12-04 17:16:18', 'qx1123code', 3, 0, '1', '/visitorManage', NULL, NULL, '访客管理', 783, NULL, NULL, NULL, NULL);

INSERT INTO `mithras`.`bifrost_custom_tree` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `flag`, `sort_no`, `parent_id`, `create_by`, `update_by`, `en_name`, `icon`, `path`, `lang_env`) VALUES ('2024-12-04 16:59:44', '2024-12-04 17:12:13', 213, 'visitorManage', '访客管理', 1, 0, NULL, NULL, NULL, NULL, 'icon-laofangke', NULL, NULL);

INSERT INTO `mithras`.`bifrost_custom_tree_menu_ref` (`id`, `gmt_create`, `gmt_modified`, `create_by`, `update_by`, `custom_tree_id`, `menu_id`, `sort_no`) VALUES (757, '2024-12-04 17:13:37', '2024-12-04 17:13:37', NULL, NULL, 213, 783, 0);

//起租，结清，新建，作废都是已签约，生效的都按未签约

update contract_base_info set is_signed = 1 WHERE contract_status in ('START_RENT', 'INVALID', 'NEW', 'SETTLE')

update contract_base_info_lib set is_signed = 1 WHERE contract_status in ('START_RENT', 'INVALID', 'NEW', 'SETTLE')

-- App结束 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<