CREATE TABLE `filing_materials` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
  `contract_id` bigint(20) DEFAULT NULL COMMENT '合同Id',
  `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
  `start_date` date DEFAULT NULL COMMENT '待办推送时间',
  `first_commit_date` date DEFAULT NULL COMMENT '首岗提交时间',
  `return_date` date DEFAULT NULL COMMENT '退回首岗时间',
  `flow_id` varchar(50) DEFAULT NULL COMMENT '流程ID',
  `initiation_method` varchar(50) DEFAULT NULL COMMENT '发起方式',
  `filing_type` varchar(50) DEFAULT NULL COMMENT '归档类型',
  `approve_status` varchar(50) DEFAULT NULL COMMENT '审批状态',
  `guarantee_flag` int(11) DEFAULT NULL COMMENT '是否存在担保',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `approve_date` datetime DEFAULT NULL COMMENT '审批完成时间',
  PRIMARY KEY (`id`),
  KEY `idx_client_id` (`client_id`),
  KEY `idx_contract_id` (`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='资料归档';


CREATE TABLE `filing_materials_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `filing_type` varchar(128) DEFAULT NULL COMMENT '归档类型',
  `business_type` varchar(128) DEFAULT NULL COMMENT '业务类型',
  `enable_flag` int(11) DEFAULT NULL COMMENT '是否启用（0 = 禁用，1 = 启用），控制该类型下所有目录是否生效',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注/描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_filing_business` (`filing_type`,`business_type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='资料归档业务类型配置';

CREATE TABLE `filing_first_level_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `filing_materials_config_id` bigint(20) DEFAULT NULL COMMENT '业务类型配置ID',
  `dir_code` varchar(128) DEFAULT NULL COMMENT '目录编码',
  `dir_name` varchar(256) DEFAULT NULL COMMENT '目录名称',
  `fixed_flag` int(11) DEFAULT NULL COMMENT '生成规则（0 = 条件生成，1 = 固定生成）',
  `condition_key` varchar(128) DEFAULT NULL COMMENT '条件标识（is_fixed=0 时必填）,与filing_condition_config关联',
  `sort_code` bigint(20) DEFAULT NULL COMMENT '排序号',
  `enable_flag` int(11) DEFAULT NULL COMMENT '是否启用（0 = 禁用，1 = 启用），目录自身启用状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='资料归档目录配置';


CREATE TABLE `filing_condition_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `condition_key` varchar(128) DEFAULT NULL COMMENT '条件编码（与枚举类 DirConditionKeyEnum 的 code 严格一致，是目录与条件关联的核心）',
  `condition_desc` varchar(512) DEFAULT NULL COMMENT '描述',
  `enable_flag` int(11) DEFAULT NULL COMMENT '是否启用（0 = 禁用，1 = 启用），条件判断是否启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_condition_key` (`condition_key`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='资料归档条件配置表';