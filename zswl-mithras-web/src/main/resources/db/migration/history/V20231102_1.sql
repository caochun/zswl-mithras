-- 流动性统计，增加列表和下载接口
INSERT INTO bifrost_function (code, name, menu_id, method, path, type, group_id)
VALUES ('cashinOutstatdownload', '流动性统计下载', 481, 'POST', '/cash/inOut/stat/download', 1, 129);
INSERT INTO bifrost_function (code, name, menu_id, method, path, type, group_id)
VALUES ('cashinOutstat', '流动性统计列表', 481, 'POST', '/cash/inOut/stat', 1, 129);

-- FTP计息，列表增加下载
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('ftpInterestIndexDownload', 'FTP计息-列表-下载', 0, 482, NULL, NULL, NULL, 'POST', '/index/download', 1, NULL);

ALTER TABLE `proj_review_base_info`
    ADD COLUMN `regional_division` varchar(100) NULL COMMENT '区域划分' AFTER `regional_project_classify`;
ALTER TABLE `proj_review_base_info_lib`
    ADD COLUMN `regional_division` varchar(100) NULL COMMENT '区域划分' AFTER `regional_project_classify`;

-- 客户编号为空，新增岗位-信科岗
INSERT INTO general_dictionary (dict_key, dict_desc, code, display, sort)
VALUES ('job', '岗位类型', 'InformationTechnologyPost', '信科岗', 10);


-- 项目立项评审添加信息
ALTER TABLE `proj_establish_base_info`
    ADD COLUMN `country` varchar(20) NULL COMMENT '国家' AFTER `funds_purpose`;
ALTER TABLE `proj_establish_base_info`
    ADD COLUMN `province` varchar(20) NULL COMMENT '省份' AFTER `country`;
ALTER TABLE `proj_establish_base_info`
    ADD COLUMN `city` varchar(20) NULL COMMENT '城市' AFTER `province`;
ALTER TABLE `proj_establish_base_info`
    ADD COLUMN `district` varchar(20) NULL COMMENT '区、县' AFTER `city`;

ALTER TABLE `proj_establish_base_info_lib`
    ADD COLUMN `country` varchar(20) NULL COMMENT '国家' AFTER `funds_purpose`;
ALTER TABLE `proj_establish_base_info_lib`
    ADD COLUMN `province` varchar(20) NULL COMMENT '省份' AFTER `country`;
ALTER TABLE `proj_establish_base_info_lib`
    ADD COLUMN `city` varchar(20) NULL COMMENT '城市' AFTER `province`;
ALTER TABLE `proj_establish_base_info_lib`
    ADD COLUMN `district` varchar(20) NULL COMMENT '区、县' AFTER `city`;

ALTER TABLE `proj_review_base_info`
    ADD COLUMN `country` varchar(20) NULL COMMENT '国家' AFTER `proj_source`;
ALTER TABLE `proj_review_base_info`
    ADD COLUMN `province` varchar(20) NULL COMMENT '省份' AFTER `country`;
ALTER TABLE `proj_review_base_info`
    ADD COLUMN `city` varchar(20) NULL COMMENT '城市' AFTER `province`;
ALTER TABLE `proj_review_base_info`
    ADD COLUMN `district` varchar(20) NULL COMMENT '区、县' AFTER `city`;

ALTER TABLE `proj_review_base_info_lib`
    ADD COLUMN `country` varchar(20) NULL COMMENT '国家' AFTER `proj_source`;
ALTER TABLE `proj_review_base_info_lib`
    ADD COLUMN `province` varchar(20) NULL COMMENT '省份' AFTER `country`;
ALTER TABLE `proj_review_base_info_lib`
    ADD COLUMN `city` varchar(20) NULL COMMENT '城市' AFTER `province`;
ALTER TABLE `proj_review_base_info_lib`
    ADD COLUMN `district` varchar(20) NULL COMMENT '区、县' AFTER `city`;

-- 流程关联项目信息
ALTER TABLE `flow_query_extra`
    ADD COLUMN `proj_name_info` json NULL COMMENT '关联项目信息' AFTER `proj_name`;
-- 保单表
CREATE TABLE `policy_info_tmp`
(
    `id`                         bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `contract_id`                bigint(20) DEFAULT NULL COMMENT '合同id',
    `policy_code`                varchar(50)  DEFAULT NULL COMMENT '保单编号',
    `policy_amount`              bigint(20) DEFAULT NULL COMMENT '保单金额',
    `insurance_start_date`       datetime     DEFAULT NULL COMMENT '保险起始日',
    `insurance_end_date`         datetime     DEFAULT NULL COMMENT '保险到期日',
    `notice_flag`                tinyint(1) DEFAULT '0' COMMENT '通知标识 0未通知， 1，已通知',
    `renew_insurance_result`     bigint(1) DEFAULT '0' COMMENT '续保结果 0需要续保，1 已续保或不需续保',
    `insurance_company`          varchar(255) DEFAULT NULL COMMENT '保险公司名称',
    `contract_code`              varchar(50)  DEFAULT NULL,
    `policy_status`              varchar(20)  DEFAULT NULL COMMENT '保单状态',
    `identification_information` varchar(255) DEFAULT NULL COMMENT '标识信息',
    `automatic`                  int(11) DEFAULT NULL COMMENT '是否自动推送，0 手动 1 自动',
    `policy_type`                varchar(50)  DEFAULT NULL COMMENT '保单种类',
    `renew_insurance_flag`       varchar(50)  DEFAULT NULL COMMENT '是否续保',
    `remark`                     varchar(500) DEFAULT NULL COMMENT '备注',
    `create_by`                  bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                  bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='保单暂存表c';
alter table policy_info
    add COLUMN identification_information varchar(255) null COMMENT '标识信息' after policy_status;
alter table policy_info_lib
    add COLUMN identification_information varchar(255) null COMMENT '标识信息' after policy_status;
alter table payment_policy_info
    add COLUMN identification_information varchar(255) null COMMENT '标识信息' after policy_code;
alter table payment_policy_info_lib
    add COLUMN identification_information varchar(255) null COMMENT '标识信息' after policy_code;


INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyInfoTmpExport', '保单暂存表c导出', 0, 491, NULL, 'POST', '/policy/info/tmp/export', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyTmpFileBatchRemove', '保单临时表文件批量删除统一', 0, 491, NULL, 'POST', '/file/batch/remove', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyTmpFileUpload', '保单临时表文件上传统一', 0, 491, NULL, 'POST', '/file/upload', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyTmpFileBatchDownload', '保单临时表文件批量下载统一', 0, 491, NULL, 'GET', '/file/batch/download', 2,
        144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyTmpFileList', '保单临时表文件列表统一', 0, 491, NULL, 'POST', '/file/list', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyTmpFileUploadRecord', '保单临时表获取文件上传后信息记录统一', 0, 491, NULL, 'POST',
        '/file/upload/record', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyTmpFileUploadPresigned', '保单临时表获取文件预上传地址统一', 0, 491, NULL, 'POST',
        '/file/upload/presigned', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyInfoTmpImport', '导入保单信息', 0, 491, NULL, 'POST', '/policy/info/tmp/import', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyInfoTmpModify', '修改保单暂存表c', 0, 491, NULL, 'POST', '/policy/info/tmp/modify', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyInfoTmpList', '保单暂存表c列表', 0, 491, NULL, 'POST', '/policy/info/tmp/list', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyInfoTmpRemove', '删除保单暂存表c', 0, 491, NULL, 'POST', '/policy/info/tmp/remove', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyInfoTmpAdd', '新增保单暂存表c', 0, 491, NULL, 'POST', '/policy/info/tmp/add', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyLedgerTmpSync', '保单台账-确认保单信息', 0, 491, NULL, 'POST', '/policy/ledger/tmp/sync', 2, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyaddcontractlist', '新增合同列表', 0, 491, NULL, 'GET', '/policy/add/contract/list', 1, 144);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('policyLedgerInfoAdd', '保单台账-新增保单信息', 0, 491, NULL, 'POST', '/policy/ledger/info/add', 2, 144);

alter table payment_policy_info
    add COLUMN contract_id bigint(20) null COMMENT '合同id' after policy_code;
alter table payment_policy_info_lib
    add COLUMN contract_id bigint(20) null COMMENT '合同id' after policy_code;

alter table `new_after_lease_check_plan_client` modify column `belong_dept_id` bigint(20) DEFAULT NULL COMMENT '客户所属业务部门id';
alter table `new_after_lease_check_plan_client` modify column `belong_sponsor_id` bigint(20) DEFAULT NULL COMMENT '客户所属主办id';
alter table `new_after_lease_check_plan_client_lib` modify column `belong_dept_id` bigint(20) DEFAULT NULL COMMENT '客户所属业务部门id';
alter table `new_after_lease_check_plan_client_lib` modify column `belong_sponsor_id` bigint(20) DEFAULT NULL COMMENT '客户所属主办id';