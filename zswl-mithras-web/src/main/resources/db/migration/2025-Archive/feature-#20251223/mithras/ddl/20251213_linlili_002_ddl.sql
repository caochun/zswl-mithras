CREATE TABLE `finance_project_distribution` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `approval_status` varchar(20) NOT NULL DEFAULT '' COMMENT '审批状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='财务-项目分润分配';


CREATE TABLE `finance_project_distribution_base_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_distribution_id` bigint(20) NOT NULL COMMENT '项目分配id',
  `contract_code` varchar(50) NOT NULL COMMENT '合同编号',
  `proj_name` varchar(200) NOT NULL COMMENT '项目名称',
  `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
  `remain_available_quota` bigint(20) DEFAULT NULL COMMENT '剩余可用额度(元)',
  `proj_item` varchar(30) DEFAULT NULL COMMENT '项目类型',
  `biz_type` varchar(20) DEFAULT NULL COMMENT '业务类型。租赁、保理、转租赁',
  `lease_type` varchar(100) DEFAULT NULL COMMENT '租赁类型。直租、回租、经营性租赁',
  `risk_control_industry_classify` varchar(50) DEFAULT NULL COMMENT '风控行业分类',
  `proj_source` varchar(20) DEFAULT NULL COMMENT '项目来源：存量翻单、渠道介绍、自主开发',
  `funds_purpose` text COMMENT '资金用途',
  `proj_background` text COMMENT '项目背景',
  `remark` text COMMENT '项目交接备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `supple_describe` varchar(500) DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='财务-项目分配表（新）-基本信息';

CREATE TABLE `finance_project_distribution_dept_weight` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `project_distribution_id` bigint(20) NOT NULL COMMENT '项目分配表ID',
  `weight_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分配比重类型',
  `weight_target` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分配比重归属目标',
  `wight_value` int(11) DEFAULT NULL COMMENT '分配比重数值',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_distribution` (`project_distribution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='财务-部门-项目分配比重表';


CREATE TABLE `finance_project_distribution_dept_launch_weight` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `project_distribution_id` bigint(20) NOT NULL COMMENT '项目分配表ID',
  `weight_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分配比重类型',
  `weight_target` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分配比重归属目标',
  `wight_value` int(11) DEFAULT NULL COMMENT '分配比重数值',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_distribution` (`project_distribution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='财务-部门-项目投放分配比重表';


CREATE TABLE `finance_project_distribution_dept_launch_weight_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `project_distribution_id` bigint(20) NOT NULL COMMENT '项目分配表ID',
  `weight_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分配比重类型',
  `weight_target` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分配比重归属目标',
  `wight_value` int(11) DEFAULT NULL COMMENT '分配比重数值',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL COMMENT '原始数据创建时间',
  `data_create_by` bigint(20) DEFAULT NULL COMMENT '原始数据创建人id',
  `data_update_time` datetime DEFAULT NULL COMMENT '原始数据更新时间',
  `data_update_by` bigint(20) DEFAULT NULL COMMENT '原始数据更新人id',
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_project_distribution` (`project_distribution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='财务-部门-项目投放分配比重版本表';

CREATE TABLE `finance_project_distribution_dept_weight_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `project_distribution_id` bigint(20) NOT NULL COMMENT '项目分配表ID',
  `weight_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分配比重类型',
  `weight_target` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分配比重归属目标',
  `wight_value` int(11) DEFAULT NULL COMMENT '分配比重数值',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL COMMENT '原始数据创建时间',
  `data_create_by` bigint(20) DEFAULT NULL COMMENT '原始数据创建人id',
  `data_update_time` datetime DEFAULT NULL COMMENT '原始数据更新时间',
  `data_update_by` bigint(20) DEFAULT NULL COMMENT '原始数据更新人id',
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`),
  KEY `idx_project_distribution` (`project_distribution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='财务-部门-项目分配比重版本表';