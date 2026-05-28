-- 项目评审增加两个字段 数据来源类型、集团授信评审id
ALTER TABLE proj_review_base_info ADD relation_data_type VARCHAR(30) DEFAULT NULL COMMENT '数据来源类型，区分普通立项(PROJ_ESTABLISH)和集团授信(GROUP_CREDIT_REVIEW)';
ALTER TABLE proj_review_base_info ADD group_credit_review_id BIGINT(20) DEFAULT NULL COMMENT '集团授信评审id';
ALTER TABLE proj_review_base_info_lib ADD relation_data_type VARCHAR(30) DEFAULT NULL COMMENT '数据来源类型，区分普通立项(PROJ_ESTABLISH)和集团授信(GROUP_CREDIT_REVIEW)';
ALTER TABLE proj_review_base_info_lib ADD group_credit_review_id BIGINT(20) DEFAULT NULL COMMENT '集团授信评审id';

-- 客户模块增加两个字段 是否集团公司 所属集团
ALTER TABLE corp_commerce_info ADD group_flag TINYINT(4) DEFAULT NULL COMMENT '是否集团公司 1是，0否';
ALTER TABLE corp_commerce_info ADD belong_group_client_id BIGINT(20) DEFAULT NULL COMMENT '所属集团 法人客户id -1无 -2自己';
ALTER TABLE corp_commerce_info_lib ADD group_flag TINYINT(4) DEFAULT NULL COMMENT '是否集团公司 1是，0否';
ALTER TABLE corp_commerce_info_lib ADD belong_group_client_id BIGINT(20) DEFAULT NULL COMMENT '所属集团 法人客户id -1无 -2自己';

-- 集团授信立项表、评审表
CREATE TABLE `group_credit_establish_base_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `client_id` bigint(20) DEFAULT NULL COMMENT '授信主体客户id',
    `client_risk_exposure` bigint(20) DEFAULT NULL COMMENT '授信主体存量风险敞口',
    `proj_name` varchar(200) DEFAULT NULL COMMENT '授信名称',
    `approval_type` varchar(20) DEFAULT NULL COMMENT '审批类型',
    `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
    `proj_background` text COMMENT '授信说明',
    `apply_credit_amount` bigint(20) DEFAULT NULL COMMENT '申报授信金额',
    `valid_month_count` int(11) DEFAULT NULL COMMENT '额度有效期限月数',
    `credit_amount_loop` tinyint(1) DEFAULT NULL COMMENT '额度是否可循环',
    `proj_sponsor_user_id` bigint(20) DEFAULT NULL COMMENT '项目主办用户id',
    `proj_cosponsor_user_ids` json DEFAULT NULL COMMENT '项目协办方用户id列表',
    `biz_dept_id` bigint(20) DEFAULT NULL COMMENT '业务部门id',
    `biz_dept_leader_id` bigint(20) DEFAULT NULL COMMENT '业务部门负责人id',
    `biz_division_leader_id` bigint(20) DEFAULT NULL COMMENT '业务分管领导id',
    `risk_control_manager_id` bigint(20) DEFAULT NULL COMMENT '风控经理id',
    `group_credit_establish_status` varchar(50) DEFAULT NULL COMMENT '立项状态',
    `group_credit_establish_process_status` varchar(50) DEFAULT NULL COMMENT '流程状态',
    `type_seq_id` bigint(20) DEFAULT NULL COMMENT '不同类型，不同的自增序列id',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_proj_code` (`proj_code`),
    UNIQUE KEY `seqid_unique` (`type_seq_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='集团授信立项基本信息表';

CREATE TABLE `group_credit_review_base_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `group_credit_establish_id` bigint(20) DEFAULT NULL COMMENT '集团授信立项基本信息表id',
    `client_id` bigint(20) DEFAULT NULL COMMENT '授信主体客户id',
    `client_risk_exposure` bigint(20) DEFAULT NULL COMMENT '授信主体存量风险敞口',
    `proj_name` varchar(200) DEFAULT NULL COMMENT '授信名称',
    `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
    `proj_background` text COMMENT '授信说明',
    `apply_credit_amount` bigint(20) DEFAULT NULL COMMENT '申报授信金额',
    `valid_month_count` int(11) DEFAULT NULL COMMENT '额度有效期限月数',
    `credit_amount_loop` tinyint(1) DEFAULT NULL COMMENT '额度是否可循环',
    `proj_sponsor_user_id` bigint(20) DEFAULT NULL COMMENT '项目主办用户id',
    `proj_cosponsor_user_ids` json DEFAULT NULL COMMENT '项目协办方用户id列表',
    `biz_dept_id` bigint(20) DEFAULT NULL COMMENT '业务部门id',
    `biz_dept_leader_id` bigint(20) DEFAULT NULL COMMENT '业务部门负责人id',
    `biz_division_leader_id` bigint(20) DEFAULT NULL COMMENT '业务分管领导id',
    `risk_control_manager_id` bigint(20) DEFAULT NULL COMMENT '风控经理id',
    `legal_manager_user_id` bigint(20) DEFAULT NULL COMMENT '法务经理id',
    `group_credit_review_status` varchar(50) DEFAULT NULL COMMENT '评审状态',
    `group_credit_review_process_status` varchar(50) DEFAULT NULL COMMENT '流程状态',
    `project_type` varchar(30) DEFAULT NULL COMMENT '项目类型：公共事业类、省内国（央）企、其他。',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    INDEX `idx_group_credit_establish_id` (`group_credit_establish_id`),
    UNIQUE KEY `uniq_proj_code` (`proj_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='集团授信评审基本信息表';

CREATE TABLE `group_credit_establish_base_info_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `client_id` bigint(20) DEFAULT NULL COMMENT '授信主体客户id',
    `client_risk_exposure` bigint(20) DEFAULT NULL COMMENT '授信主体存量风险敞口',
    `proj_name` varchar(200) DEFAULT NULL COMMENT '授信名称',
    `approval_type` varchar(20) DEFAULT NULL COMMENT '审批类型',
    `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
    `proj_background` text COMMENT '授信说明',
    `apply_credit_amount` bigint(20) DEFAULT NULL COMMENT '申报授信金额',
    `valid_month_count` int(11) DEFAULT NULL COMMENT '额度有效期限月数',
    `credit_amount_loop` tinyint(1) DEFAULT NULL COMMENT '额度是否可循环',
    `proj_sponsor_user_id` bigint(20) DEFAULT NULL COMMENT '项目主办用户id',
    `proj_cosponsor_user_ids` json DEFAULT NULL COMMENT '项目协办方用户id列表',
    `biz_dept_id` bigint(20) DEFAULT NULL COMMENT '业务部门id',
    `biz_dept_leader_id` bigint(20) DEFAULT NULL COMMENT '业务部门负责人id',
    `biz_division_leader_id` bigint(20) DEFAULT NULL COMMENT '业务分管领导id',
    `risk_control_manager_id` bigint(20) DEFAULT NULL COMMENT '风控经理id',
    `group_credit_establish_status` varchar(50) DEFAULT NULL COMMENT '立项状态',
    `group_credit_establish_process_status` varchar(50) DEFAULT NULL COMMENT '流程状态',
    `type_seq_id` bigint(20) DEFAULT NULL COMMENT '不同类型，不同的自增序列id',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,

    PRIMARY KEY (`id`),
    INDEX `idx_origin_id` (`origin_id`),
    KEY `idx_proj_code` (`proj_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='集团授信立项基本信息版本表';

CREATE TABLE `group_credit_review_base_info_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `group_credit_establish_id` bigint(20) DEFAULT NULL COMMENT '集团授信立项基本信息表id',
    `client_id` bigint(20) DEFAULT NULL COMMENT '授信主体客户id',
    `client_risk_exposure` bigint(20) DEFAULT NULL COMMENT '授信主体存量风险敞口',
    `proj_name` varchar(200) DEFAULT NULL COMMENT '授信名称',
    `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
    `proj_background` text COMMENT '授信说明',
    `apply_credit_amount` bigint(20) DEFAULT NULL COMMENT '申报授信金额',
    `valid_month_count` int(11) DEFAULT NULL COMMENT '额度有效期限月数',
    `credit_amount_loop` tinyint(1) DEFAULT NULL COMMENT '额度是否可循环',
    `proj_sponsor_user_id` bigint(20) DEFAULT NULL COMMENT '项目主办用户id',
    `proj_cosponsor_user_ids` json DEFAULT NULL COMMENT '项目协办方用户id列表',
    `biz_dept_id` bigint(20) DEFAULT NULL COMMENT '业务部门id',
    `biz_dept_leader_id` bigint(20) DEFAULT NULL COMMENT '业务部门负责人id',
    `biz_division_leader_id` bigint(20) DEFAULT NULL COMMENT '业务分管领导id',
    `risk_control_manager_id` bigint(20) DEFAULT NULL COMMENT '风控经理id',
    `legal_manager_user_id` bigint(20) DEFAULT NULL COMMENT '法务经理id',
    `group_credit_review_status` varchar(50) DEFAULT NULL COMMENT '立项状态',
    `group_credit_review_process_status` varchar(50) DEFAULT NULL COMMENT '流程状态',
    `project_type` varchar(30) DEFAULT NULL COMMENT '项目类型：公共事业类、省内国（央）企、其他。',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,

    PRIMARY KEY (`id`),
    INDEX `idx_group_credit_establish_id` (`group_credit_establish_id`),
    INDEX `idx_origin_id` (`origin_id`),
    KEY `idx_proj_code` (`proj_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='集团授信评审基本信息版本表';

-- 项目编号拆表 因为项目评审、项目立项都要生成项目编号了，项目编号又需要唯一，所以需要单独拎出来存
CREATE TABLE `proj_code_store` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
    `biz_type` varchar(20) DEFAULT NULL COMMENT '业务类型。租赁、保理、转租赁',
    `type_seq_id` bigint(20) DEFAULT NULL COMMENT '不同类型，不同的自增序列id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_proj_code` (`proj_code`),
    UNIQUE KEY `seqid_unique` (`biz_type`,`type_seq_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1208 DEFAULT CHARSET=utf8mb4 COMMENT='项目编号单存表';