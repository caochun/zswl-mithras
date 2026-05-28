-- 排序表
CREATE TABLE `dict_sort` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `module` varchar(32) DEFAULT NULL COMMENT '所属模块,隔离重名',
    `dict_type` varchar(64) DEFAULT NULL COMMENT '字典项类型',
    `dict_name` varchar(64) DEFAULT NULL COMMENT '字典项名称',
    `sort_no` int(11) DEFAULT NULL COMMENT '排序优先级',
    `gmt_create` timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `gmt_update` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_module` (`module`) USING BTREE,
    KEY `idx_dict_name` (`dict_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COMMENT='字典项排序优先级表，用于记录不同字典枚举项在列表排序时的优先级';

INSERT INTO `dict_sort` (`id`, `module`, `dict_type`, `dict_name`, `sort_no`, `gmt_create`, `gmt_update`) VALUES
    (null, 'dashborad', 'bizDeptSort', '浙江业务部', 0, NULL, NULL),
    (null, 'dashborad', 'bizDeptSort', '公用事业业务部', 10, NULL, NULL),
    (null, 'dashborad', 'bizDeptSort', '高端装备业务部', 20, NULL, NULL),
    (null, 'dashborad', 'bizDeptSort', '智能制造业务部', 30, NULL, NULL),
    (null, 'dashborad', 'bizDeptSort', '交通物流业务部', 40, NULL, NULL),
    (null, 'dashborad', 'bizDeptSort', '航运业务部', 50, NULL, NULL),
    (null, 'dashborad', 'bizDeptSort', '工程建设业务部', 60, NULL, NULL),
    (null, 'dashborad', 'bizDeptSort', '新能源业务部', 70, NULL, NULL),
    (null, 'dashborad', 'bizDeptSort', '化工建材业务部', 80, NULL, NULL),
    (null, 'dashborad', 'bizDeptSort', '机械和加工业务部', 90, NULL, NULL),
    (null, 'dashborad', 'bizDeptSort', '冷链物流团队', 100, NULL, NULL);

-- 运营工作台第二部分接口
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES
    ('2024-07-30 09:11:04', '2024-07-30 09:11:04', null, 'dashboardOperationCapacityList', '业务工作台-运营视角-产能分析-详情', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/capacity/list', 1, NULL),
    ('2024-07-30 09:11:27', '2024-07-30 09:11:27', null, 'dashboardOperationCapacityStatistics', '业务工作台-运营视角-产能分析-部门产能分析', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/capacity/statistics', 1, NULL),
    ('2024-07-30 09:12:26', '2024-07-30 09:12:26', null, 'dashboardOperationPayStatistics', '业务工作台-运营视角-投放完成情况-投放情况', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/pay/statistics', 1, NULL),
    ('2024-07-30 09:12:51', '2024-07-30 09:12:51', null, 'dashboardOperationPayList', '业务工作台-运营视角-投放完成情况-详情', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/pay/list', 1, NULL),
    ('2024-07-30 17:19:50', '2024-07-30 17:19:50', null, 'dashboardOperationPayImport', '业务工作台-导入', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/pay/import', 2, NULL),
    ('2024-07-31 15:47:06', '2024-07-31 15:47:06', null, 'dashboardOperationTimeStatistics', '业务工作台-运营部-时效统计-平均耗时(工作日)', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/time/statistics', 2, NULL),
    ('2024-07-31 15:47:06', '2024-07-31 15:47:06', null, 'dashboardOperationConversionList', '业务工作台-运营视角-转化率-详情', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/conversion/list', 2, NULL),
    ('2024-07-31 15:47:06', '2024-07-31 15:47:06', null, 'dashboardOperationConversionTerm', '业务工作台-运营部-转化率-时间周期', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/conversion/term', 2, NULL),
    ('2024-07-31 15:47:06', '2024-07-31 15:47:06', null, 'dashboardOperationConversionStatistics', '业务工作台-运营部-转化率-平均值', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/conversion/statistics', 2, NULL),
    ('2024-07-31 15:47:06', '2024-07-31 15:47:06', null, 'dashboardOperationTimeList', '业务工作台-运营视角-时效统计-详情', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/time/list', 2, NULL),
    ('2024-07-31 15:47:06', '2024-07-31 15:47:06', null, 'dashboardOperationTimeTerm', '业务工作台-运营部-时效统计-时间周期', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/operation/time/term', 2, NULL);

-- 工作台导入表
CREATE TABLE `dashboard_adjust_person_info` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `dept_id` bigint(20) DEFAULT NULL COMMENT '部门id',
     `dept_name` varchar(255) DEFAULT NULL,
     `position` varchar(255) DEFAULT NULL COMMENT '职位',
     `proj_manager_id` bigint(20) DEFAULT NULL COMMENT '项目经理id',
     `proj_manager_name` varchar(255) DEFAULT NULL COMMENT '项目经理名称',
     `business_group` varchar(255) DEFAULT NULL COMMENT '业务组分类',
     `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
     `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     `batch_number` bigint(20) NOT NULL COMMENT '批次号',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=572 DEFAULT CHARSET=utf8mb4 COMMENT='工作台-人力调整明细表';

CREATE TABLE `dashboard_due_diligence_info` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
     `dept_id` bigint(20) NOT NULL COMMENT '部门id',
     `dept_name` varchar(255) DEFAULT NULL,
     `proj_review_id` bigint(20) DEFAULT NULL COMMENT '评审id，可能为空',
     `proj_name` varchar(255) DEFAULT NULL COMMENT '项目名称',
     `proj_code` varchar(255) DEFAULT NULL COMMENT '项目编号',
     `amount` bigint(20) DEFAULT NULL COMMENT '金额',
     `proj_manager_id` int(11) DEFAULT NULL COMMENT '项目主办',
     `proj_manager_name` varchar(255) DEFAULT NULL COMMENT '项目主办名称',
     `risk_manager_id` bigint(20) DEFAULT NULL COMMENT '风控经理id',
     `risk_manager_name` varchar(255) DEFAULT NULL COMMENT '风控经理名称',
     `business_group` varchar(255) DEFAULT NULL COMMENT '业务组分类',
     `due_diligence_time` datetime DEFAULT NULL COMMENT '尽调时间',
     `due_diligence_report_time` datetime DEFAULT NULL COMMENT '尽调报告出具时间',
     `comment` varchar(255) DEFAULT NULL COMMENT '备注',
     `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
     `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3014 DEFAULT CHARSET=utf8mb4 COMMENT='工作台-尽调明细表';


CREATE TABLE `dashboard_review_info` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
      `dept_id` bigint(20) NOT NULL COMMENT '部门id',
      `dept_name` varchar(255) DEFAULT NULL,
      `proj_name` varchar(255) DEFAULT NULL COMMENT '项目名称',
      `proj_amount` bigint(20) DEFAULT NULL COMMENT '项目金额',
      `approval_amount` bigint(20) DEFAULT NULL COMMENT '批复金额',
      `business_group` varchar(255) DEFAULT NULL COMMENT '业务组分类',
      `convoke_time` datetime DEFAULT NULL COMMENT '召开时间',
      `review_result` varchar(255) DEFAULT NULL COMMENT '评审结果',
      `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
      `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=531 DEFAULT CHARSET=utf8mb4 COMMENT='工作台-评审明细表';



CREATE TABLE `dashboard_visit_info` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
      `dept_id` bigint(20) NOT NULL COMMENT '部门id',
      `dept_name` varchar(255) DEFAULT NULL,
      `proj_manager_id` bigint(20) NOT NULL COMMENT '项目经理id',
      `proj_manager_name` varchar(255) DEFAULT NULL COMMENT '项目经理名称',
      `business_group` varchar(255) DEFAULT NULL COMMENT '业务组分类',
      `visit_time` datetime DEFAULT NULL COMMENT '拜访日期',
      `visit_client_count` int(11) DEFAULT NULL COMMENT '拜访客户数',
      `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
      `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
      `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2757 DEFAULT CHARSET=utf8mb4 COMMENT='工作台-拜访明细表';

-- 合同文本类型
CREATE TABLE `contract_text_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `text_type` varchar(200) NOT NULL DEFAULT '' COMMENT '合同文本类型',
  `is_confirmed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已确认，0-未确认，1-已确认',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同文本信息';

CREATE TABLE `contract_text_info_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `text_type` varchar(200) NOT NULL DEFAULT '' COMMENT '合同文本类型',
  `is_confirmed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已确认，0-未确认，1-已确认',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_origin_id` (`origin_id`),
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同文本信息-版本表';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('contracttextinfoget', '合同文本类型-详情', 0, 20, NULL, NULL, NULL, 'POST', '/contract/text/info/get', 1, NULL),
	('contracttextinfosave', '合同文本类型-保存', 0, 20, NULL, NULL, NULL, 'POST', '/contract/text/info/save', 2, NULL);

-- 待维护保单菜单修改
delete from bifrost_function where code = 'maintenancepolicyprojlist';
delete from bifrost_function where code = 'maintenancePolicyProjExport';

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-08-06 10:18:40', '2024-08-06 10:18:40', NULL, 'DashBoardmaintenancepolicyprojlist', '统一工作台-待维护保单项目列表', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'POST', '/maintenance/policy/proj/list', 1, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-08-06 10:19:07', '2024-08-06 10:19:07', NULL, 'DashBoardmaintenancePolicyProjExport', '统一工作台-待维护保单导出', 0, (select id from bifrost_menu where code = 'dashboard'), NULL, NULL, NULL, 'GET', '/maintenance/policy/proj/export', 1, NULL);

-- 客户管理-联系人增加字段
alter table `corp_contact_info` add column `landline_telephone` varchar(30) default null comment '座机' after `telephone`;
alter table `corp_contact_info_lib` add column `landline_telephone` varchar(30) default null comment '座机' after `telephone`;

-- 租金支付通知书允许资金经理修改回款账户信息
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('processpreparebankinfomodify', '修改租金支付通知书的银行账户信息', 0, 10, NULL, NULL, NULL, 'POST', '/process/prepare/bankinfo/modify', 2, NULL);

alter table `rent_collection_month_detail` add column `year` int(11) default null comment '年份' after `dept_id`;

update `rent_collection_month_detail` set `year` = 2024;
