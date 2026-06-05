CREATE TABLE `kpi_parameter_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `config_code` varchar(50) NOT NULL DEFAULT '' COMMENT '参数code',
  `config_desc` varchar(50) NOT NULL DEFAULT '' COMMENT '参数描述',
  `config_value` text NOT NULL COMMENT '参数值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绩效考核-参数设置';

CREATE TABLE `kpi_project_allocation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proj_review_id` bigint(20) NOT NULL COMMENT '项目评审id',
  `proj_name` varchar(200) NOT NULL COMMENT '项目名称',
  `proj_classify` varchar(50) DEFAULT NULL COMMENT '项目类别',
  `proj_source` varchar(20) DEFAULT NULL COMMENT '项目来源',
  `start_date` date NOT NULL COMMENT '投放日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `dept_id` bigint(20) NOT NULL COMMENT '所属部门id',
  `sponsor_user_id` bigint(20) NOT NULL COMMENT '项目主办id',
  `sponsor_user_allocation_radio` varchar(10) DEFAULT NULL COMMENT '项目主办分配占比',
  `cosponsor_user_ids` json DEFAULT NULL COMMENT '项目协办id列表',
  `cosponsor_user_allocation_radio` json DEFAULT NULL COMMENT '项目协办分配占比',
  `adjust_pool` varchar(10) DEFAULT NULL COMMENT '调节池',
  `approval_status` varchar(50) NOT NULL COMMENT '审批状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绩效考核-项目分配表';

CREATE TABLE `kpi_project_allocation_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proj_review_id` bigint(20) NOT NULL COMMENT '项目评审id',
  `proj_name` varchar(200) NOT NULL COMMENT '项目名称',
  `proj_classify` varchar(50) DEFAULT NULL COMMENT '项目类别',
  `proj_source` varchar(20) DEFAULT NULL COMMENT '项目来源',
  `start_date` date NOT NULL COMMENT '投放日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `dept_id` bigint(20) NOT NULL COMMENT '所属部门id',
  `sponsor_user_id` bigint(20) NOT NULL COMMENT '项目主办id',
  `sponsor_user_allocation_radio` varchar(10) DEFAULT NULL COMMENT '项目主办分配占比',
  `cosponsor_user_ids` json DEFAULT NULL COMMENT '项目协办id列表',
  `cosponsor_user_allocation_radio` json DEFAULT NULL COMMENT '项目协办分配占比',
  `adjust_pool` varchar(10) DEFAULT NULL COMMENT '调节池',
  `approval_status` varchar(50) NOT NULL COMMENT '审批状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_origin_id` (`origin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绩效考核-项目分配版本表';

CREATE TABLE `kpi_project_manager_assessment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `year` int(10) NOT NULL COMMENT '年份',
  `quarter` tinyint(4) NOT NULL COMMENT '季度',
  `dept_id` bigint(20) NOT NULL COMMENT '部门',
  `approval_status` varchar(50) NOT NULL COMMENT '审批状态',
  `is_notify` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已通知业务负责人，0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绩效考核-项目经理考评表';

CREATE TABLE `kpi_project_manager_assessment_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `year` int(10) NOT NULL COMMENT '年份',
  `quarter` tinyint(4) NOT NULL COMMENT '季度',
  `dept_id` bigint(20) NOT NULL COMMENT '部门',
  `approval_status` varchar(50) NOT NULL COMMENT '审批状态',
  `is_notify` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已通知业务负责人，0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_origin_id` (`origin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绩效考核-项目经理考评表版本表';

CREATE TABLE `kpi_project_manager_assessment_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `assessment_id` bigint(20) NOT NULL COMMENT '项目经理考评表id',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '项目经理所在部门id',
  `user_id` bigint(20) NOT NULL COMMENT '项目经理id',
  `dept_performance_score` varchar(10) DEFAULT NULL COMMENT '部门业绩完成率分数',
  `competent_score` varchar(10) DEFAULT NULL COMMENT '称职条件分数',
  `marketing_channel_score` varchar(10) DEFAULT NULL COMMENT '营销渠道建设分数',
  `defect_rate_score` varchar(10) DEFAULT NULL COMMENT '不良率分数',
  `overdue_rate_score` varchar(10) DEFAULT NULL COMMENT '逾期率分数',
  `focus_radio_score` varchar(10) DEFAULT NULL COMMENT '关注类业务占比（期末）分数',
  `after_lease_score` varchar(10) DEFAULT NULL COMMENT '租后管理分数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_assessment_id` (`assessment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绩效考核-项目经理考评明细表';

CREATE TABLE `kpi_project_manager_assessment_detail_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `assessment_id` bigint(20) NOT NULL COMMENT '项目经理考评表id',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '项目经理所在部门id',
  `user_id` bigint(20) NOT NULL COMMENT '项目经理id',
  `dept_performance_score` varchar(10) DEFAULT NULL COMMENT '部门业绩完成率分数',
  `competent_score` varchar(10) DEFAULT NULL COMMENT '称职条件分数',
  `marketing_channel_score` varchar(10) DEFAULT NULL COMMENT '营销渠道建设分数',
  `defect_rate_score` varchar(10) DEFAULT NULL COMMENT '不良率分数',
  `overdue_rate_score` varchar(10) DEFAULT NULL COMMENT '逾期率分数',
  `focus_radio_score` varchar(10) DEFAULT NULL COMMENT '关注类业务占比（期末）分数',
  `after_lease_score` varchar(10) DEFAULT NULL COMMENT '租后管理分数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_assessment_id` (`assessment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绩效考核-项目经理考评明细表版本';

alter table materials_list MODIFY oss_filename VARCHAR(255);
alter table materials_list_lib MODIFY oss_filename VARCHAR(255);
