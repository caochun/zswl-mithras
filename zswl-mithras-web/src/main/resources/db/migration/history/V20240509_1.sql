CREATE TABLE `track_event_info` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id	',
   `biz_id` bigint(20) NOT NULL COMMENT '所属业务ID',
   `biz_source` varchar(20) NOT NULL COMMENT '业务来源',
   `task_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
   `task_type` varchar(20) DEFAULT NULL COMMENT '任务类型',
   `plan_time` datetime DEFAULT NULL COMMENT '计划日期',
   `start_rent_after_day` int(5) DEFAULT NULL COMMENT '起租后的几个自然日',
   `processor_id` bigint(20) DEFAULT NULL COMMENT '处理人id',
   `processor_dept` varchar(255) DEFAULT NULL COMMENT '处理人岗位',
   `remind_frequency` varchar(20) DEFAULT NULL COMMENT '提醒频率',
   `task_content` varchar(200) DEFAULT NULL COMMENT '任务内容',
   `task_status` tinyint(1) DEFAULT '1' COMMENT '任务状态 -1生效 0关闭',
   `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id	',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
   `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id	',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间	',
   `client_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
   `contract_code` varchar(50) DEFAULT NULL COMMENT '合同编号',
   `proj_name` varchar(200) DEFAULT NULL COMMENT '项目名称',
   `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
   `on_flow_count` int(11) DEFAULT '0' COMMENT '推送次数',
   `is_ledger` tinyint(1) DEFAULT '0' COMMENT '是否从台账新增',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `track_event_info_lib` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id	',
   `biz_id` bigint(20) NOT NULL COMMENT '所属业务ID',
   `biz_source` varchar(20) NOT NULL COMMENT '业务来源',
   `task_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
   `task_type` varchar(20) DEFAULT NULL COMMENT '任务类型',
   `plan_time` datetime DEFAULT NULL COMMENT '计划日期',
   `start_rent_after_day` int(5) DEFAULT NULL COMMENT '起租后的几个自然日',
   `processor_id` bigint(20) DEFAULT NULL COMMENT '处理人id',
   `processor_dept` varchar(255) DEFAULT NULL COMMENT '处理人岗位',
   `remind_frequency` varchar(20) DEFAULT NULL COMMENT '提醒频率',
   `task_content` varchar(200) DEFAULT NULL COMMENT '任务内容',
   `task_status` tinyint(1) DEFAULT '1' COMMENT '任务状态 -1生效 0关闭',
   `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id	',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
   `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id	',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间	',
   `client_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
   `contract_code` varchar(50) DEFAULT NULL COMMENT '合同编号',
   `proj_name` varchar(200) DEFAULT NULL COMMENT '项目名称',
   `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
   `on_flow_count` int(11) DEFAULT '0' COMMENT '推送次数',
   `is_ledger` tinyint(1) DEFAULT NULL COMMENT '是否从台账新增',
   `origin_id` bigint(20) NOT NULL COMMENT '草稿表id',
   `version` varchar(32) NOT NULL COMMENT '版本',
   `data_create_time` datetime DEFAULT NULL,
   `data_create_by` bigint(20) DEFAULT NULL,
   `data_update_time` datetime DEFAULT NULL,
   `data_update_by` bigint(20) DEFAULT NULL,
   `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

update bifrost_custom_tree set name = '运营管理' where code = 'leaseMaintain';
update bifrost_menu set name = '租赁物审核台账' where code = 'leaseMaintain';
-- 跟踪事项 菜单 id=726
INSERT INTO `bifrost_menu` (`gmt_create`, `gmt_modified`, `code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`, `id`, `en_name`, `target`, `create_by`, `update_by`) VALUES ('2024-04-19 17:15:34', '2024-04-19 17:15:34', 'leaseTracking', 3, 0, NULL, '/lease/tracking', NULL, NULL, '跟踪事项台账', 726, NULL, NULL, NULL, NULL);
-- 跟踪事项 分组菜单关联
INSERT INTO `bifrost_custom_tree_menu_ref` (`id`, `gmt_create`, `gmt_modified`, `create_by`, `update_by`, `custom_tree_id`, `menu_id`, `sort_no`) VALUES (NULL, '2024-02-29 14:28:22', '2024-04-19 17:15:43', NULL, NULL, (select id from bifrost_custom_tree where code = 'leaseMaintain'), 726, 0);
-- 跟踪事项 功能
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('2024-04-23 09:53:04', '2024-04-23 09:53:29', null, 'trackEventList', '跟踪事项列表', 0, 726, NULL, NULL, NULL, 'POST', '/trackEvent/list', 1, NULL),
    ('2024-04-23 09:54:27', '2024-04-23 09:54:27', null, 'trackEventDetail', '跟踪事项详情', 0, 726, NULL, NULL, NULL, 'GET', '/trackEvent/detail', 1, NULL),
    ('2024-04-23 09:54:51', '2024-04-23 09:54:51', null, 'trackEventContractInfo', '信息回显', 0, 726, NULL, NULL, NULL, 'POST', '/trackEvent/contractInfo', 1, NULL),
    ('2024-04-23 09:55:11', '2024-04-23 09:55:11', null, 'trackEventAdd', '跟踪事项新增', 0, 726, NULL, NULL, NULL, 'POST', '/trackEvent/add', 2, NULL),
    ('2024-04-23 09:55:30', '2024-04-23 09:55:30', null, 'trackEventUpdate', '跟踪事项编辑', 0, 726, NULL, NULL, NULL, 'POST', '/trackEvent/update', 2, NULL),
    ('2024-04-23 09:55:52', '2024-04-23 09:55:52', null, 'trackEventClose', '关闭任务', 0, 726, NULL, NULL, NULL, 'GET', '/trackEvent/close', 2, NULL),
    ('2024-04-23 09:56:18', '2024-04-23 09:56:18', null, 'trackEventQueryProcessor', '处理人下拉框', 0, 726, NULL, NULL, NULL, 'GET', '/trackEvent/queryProcessor', 1, NULL),
    ('2024-04-23 10:13:09', '2024-04-23 10:13:09', null, 'clientlist-trackevent', '选择客户', 0, 726, NULL, NULL, NULL, 'POST', '/client/list', 1, NULL),
    ('2024-04-23 10:20:40', '2024-04-23 13:08:20', null, 'contractreviewquery_trackevent', '选择项目', 0, 726, NULL, NULL, NULL, 'POST', '/contract/review/query', 1, NULL),
    ('2024-04-23 11:34:14', '2024-04-23 11:34:14', null, 'TrackEventContractCodeList', '合同编号下拉框', 0, 726, NULL, NULL, NULL, 'GET', '/trackEvent/contractCodeList', 1, NULL),
    ('2024-04-23 14:31:19', '2024-04-23 14:31:19', null, 'trackEventFileUpload', '跟踪事项文件上传', 0, 726, NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL),
    ('2024-04-23 14:31:49', '2024-04-23 14:31:49', null, 'trackEventFileDownload', '跟踪事项文件下载', 0, 726, NULL, NULL, NULL, 'GET', '/file/download', 2, NULL),
    ('2024-04-23 14:32:36', '2024-04-23 14:32:36', null, 'trackEventFileList', '跟踪事项文件列表', 0, 726, NULL, NULL, NULL, 'POST', '/file/list', 2, NULL),
    ('2024-04-23 14:33:37', '2024-04-23 14:33:37', null, 'trackEventFileBatchRemove', '跟踪事项文件删除', 0, 726, NULL, NULL, NULL, 'POST', '/file/batch/remove', 2, NULL),
    ('2024-04-26 15:06:52', '2024-04-26 15:06:52', null, 'trackEventDownload', '批量导出', 0, 726, NULL, NULL, NULL, 'POST', '/trackEvent/download', 1, NULL);
-- 保单
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES
    ('2024-05-06 16:58:55', '2024-05-06 16:58:55', NULL, 'policyInfoSubmit', '提交保单信息', 0, (select id from bifrost_menu where code = 'afterLeasepolicyManage'), NULL, NULL, NULL, 'POST', '/policy/info/submit', 2, NULL);

alter table `policy_info` add column `data_status` varchar(20) DEFAULT 'FORMAL' comment '数据状态';
alter table `policy_info_lib` add column `data_status` varchar(20) DEFAULT 'FORMAL' comment '数据状态';

ALTER TABLE `new_after_lease_check_report_base`
    MODIFY COLUMN `deadline` date NULL COMMENT '到期日' AFTER `risk_exposure`;

ALTER TABLE `new_after_lease_check_report_base_lib`
    MODIFY COLUMN `deadline` date NULL COMMENT '到期日' AFTER `risk_exposure`;

INSERT INTO `flow_query_extra` (`flow_key`, `instance_id`, `biz_id`, `client_name`, `proj_name`, `proj_name_info`, `proj_code`, `contract_code`, `create_by`, `update_by` )
VALUES
('ContractEarlySettleConfirmFlow', '2582768', 1156, '山西晋南钢铁集团有限公司', '2022山西晋南钢铁售后回租项目', NULL, 'ZTZLCHA202208029', '浙商租【2022】租字第(A-0077)号', 68, 68 );
INSERT INTO `biz_process_data` (`process_instance_id`, `client_id`) VALUES ('2582768', 2747);

ALTER TABLE `contract_constitution_file_lib`
    ADD COLUMN `file_type` varchar(40) NULL COMMENT '章程文件类型' AFTER `materials_list_id`;


