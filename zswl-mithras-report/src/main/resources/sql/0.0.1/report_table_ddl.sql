CREATE TABLE `cr_client` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) NOT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户表id',

    `client_code` VARCHAR(50) DEFAULT NULL COMMENT '客户编号',
    `client_name` VARCHAR(50) DEFAULT NULL COMMENT '客户名称',
    `zhong_zheng_code` VARCHAR(50) DEFAULT NULL COMMENT '中征码',
    `continuous_status` VARCHAR(20) DEFAULT NULL COMMENT '存续状态',
    `org_type` VARCHAR(20) DEFAULT NULL COMMENT '组织机构类型',
    `register_address` VARCHAR(256) DEFAULT NULL COMMENT '注册地址',
    `region_code` VARCHAR(50) DEFAULT NULL COMMENT '行政区划',
    `establish_date` DATE DEFAULT NULL COMMENT '成立日期',
    `biz_license_end_date` DATE DEFAULT NULL COMMENT '营业许可证到期日',
    `biz_scope` text COMMENT '业务范围',
    `industry_type` varchar(50) DEFAULT NULL COMMENT '行业分类',
    `economy_type` varchar(50) DEFAULT NULL COMMENT '经济类型',
    `org_scale` varchar(20) DEFAULT NULL COMMENT '企业规模',
    `register_currency_type` varchar(20) DEFAULT NULL COMMENT '注册资本币种',
    `register_capital` bigint(20) DEFAULT NULL COMMENT '注册资本（单位：0.0001元）',
    `corp_represent` varchar(20) DEFAULT NULL COMMENT '法人代表',
    `corp_cert_type` varchar(20) DEFAULT NULL COMMENT '法人证件类型',
    `corp_cert_code` varchar(50) DEFAULT NULL COMMENT '法人证件号码',
    `effect_date` date DEFAULT NULL COMMENT '数据更新日期',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_contract_id(`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-客户表';

CREATE TABLE `cr_mortgage` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) NOT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',

    `mortgage_contract_code` varchar(50) DEFAULT NULL COMMENT '抵押合同编号',
    `max_flag` tinyint(4) DEFAULT NULL COMMENT '最高额担保标识（0否，1是）',
    `sequence` varchar(10) DEFAULT NULL COMMENT '序号',
    `type` varchar(20) DEFAULT NULL COMMENT '抵押物种类',
    `model_type` varchar(50) DEFAULT NULL COMMENT '抵押物识别号类型',
    `model` varchar(50) DEFAULT NULL COMMENT '抵押物唯一识别号',
    `assessed_value` bigint(20) DEFAULT NULL COMMENT '评估价值（单位：0.0001元）',
    `appraisal_company_type` varchar(20) DEFAULT NULL COMMENT '评估机构类型',
    `assessed_date` date DEFAULT NULL COMMENT '评估日期',
    `mortgage_type` varchar(30) DEFAULT NULL COMMENT '抵押人类型',
    `mortgage_name` varchar(30) DEFAULT NULL COMMENT '抵押人名称',
    `mortgage_id_type` varchar(20) DEFAULT NULL COMMENT '抵押人身份标识类型',
    `mortgage_id` varchar(64) DEFAULT NULL COMMENT '抵押人身份标识号码',
    `mortgage_describe` varchar(200) DEFAULT NULL COMMENT '抵押物描述',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_contract_id (`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-抵押表';

CREATE TABLE `cr_pledge` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) NOT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',

    `pledge_contract_code` varchar(50) DEFAULT NULL COMMENT '质押合同编号',
    `max_flag` tinyint(4) DEFAULT NULL COMMENT '最高额担保标识（0否，1是）',
    `sequence` varchar(10) DEFAULT NULL COMMENT '序号',
    `type` varchar(20) DEFAULT NULL COMMENT '质押物种类',
    `assessed_value` bigint(20) DEFAULT NULL COMMENT '质物价值',
    `pledge_type` varchar(30) DEFAULT NULL COMMENT '出质人身份类别',
    `pledge_name` varchar(30) DEFAULT NULL COMMENT '出质人名称',
    `pledge_id_type` varchar(20) DEFAULT NULL COMMENT '出质人身份标识类型',
    `pledge_id` varchar(64) DEFAULT NULL COMMENT '出质人身份标识号码',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_contract_id (`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-质押表';

CREATE TABLE `cr_guarantor` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) NOT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',

    `payment_apply_code` varchar(20) DEFAULT NULL COMMENT '借据编号',
    `client_type` varchar(20) DEFAULT NULL COMMENT '客户分类',
    `client_name` VARCHAR(50) DEFAULT NULL COMMENT '客户名称',
    `guarantor_id_type` varchar(20) DEFAULT NULL COMMENT '身份标识类型',
    `guarantor_id` varchar(64) DEFAULT NULL COMMENT '身份标识号码',
    `client_class` varchar(20) DEFAULT NULL COMMENT '客户类型',
    `repay_liability_amount` bigint(20) DEFAULT NULL COMMENT '还款责任金额（单位：0.0001元）',
    `joint_guarantor_flag` varchar(20) DEFAULT NULL COMMENT '联保标志',
    `guarante_contract_code` varchar(50) DEFAULT NULL COMMENT '保证合同编号',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_contract_id (`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-保证表';

CREATE TABLE `cr_account` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) NOT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',

    `payment_apply_code` varchar(20) DEFAULT NULL COMMENT '借据编号',
    `lending_date` date DEFAULT NULL COMMENT '最后一笔付款明细的实付日期',
    `biz_type` varchar(20) DEFAULT NULL COMMENT '业务类型',
    `rental_calc_type` varchar(40) DEFAULT NULL COMMENT '租金计算方式',
    `repay_rate` varchar(20) DEFAULT NULL COMMENT '还款频率',
    `earnest_money` bigint(20) DEFAULT NULL COMMENT '保证金（单位：0.0001元）',
    `proj_lease_month_count` int(11) DEFAULT NULL COMMENT '借款期限（单位：月）',
    `payment_amount` bigint(20) DEFAULT NULL COMMENT '借款金额（单位：0.0001元）',
    `client_code` VARCHAR(50) DEFAULT NULL COMMENT '客户编号',
    `closed_date` date DEFAULT NULL COMMENT '结清日期',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_contract_id(`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-账户表';

CREATE TABLE `cr_repay_plan` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) NOT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',

    `payment_apply_code` varchar(20) DEFAULT NULL COMMENT '借据编号',
    `phase` int(11) DEFAULT NULL COMMENT '期项',
    `cash_flow_date` date DEFAULT NULL COMMENT '日期',
    `grace_period` varchar(32) DEFAULT NULL COMMENT '宽限期',
    `rent` bigint(20) DEFAULT NULL COMMENT '租金（单位：0.0001元）',
    `principal` bigint(20) DEFAULT NULL COMMENT '本金（单位：0.0001元）',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_contract_id (`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-还款计划表';

CREATE TABLE `cr_actual_repay` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) NOT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',

    `payment_apply_code` varchar(20) DEFAULT NULL COMMENT '借据编号',
    `phase` int(11) DEFAULT NULL COMMENT '期项',
    `pay_date` date DEFAULT NULL COMMENT '租金核销对应的实收日期',
    `collection_amount` bigint(20) DEFAULT NULL COMMENT '实收金额（单位：0.0001元）',
    `collection_principal` bigint(20) DEFAULT NULL COMMENT '实收本金（单位：0.0001元）',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_contract_id(`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-实际还款表';

CREATE TABLE `cr_special_trade` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) NOT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',

    `payment_apply_code` varchar(20) DEFAULT NULL COMMENT '借据编号',
    `trade_type` varchar(20) DEFAULT NULL COMMENT '交易类型',
    `trade_date` date DEFAULT NULL COMMENT '交易日期',
    `trade_amount` bigint(20) DEFAULT NULL COMMENT '交易金额（单位：0.0001元）',
    `change_month_count` int(11) DEFAULT NULL COMMENT '到期日变更月数',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_contract_id(`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-特定交易表';

CREATE TABLE `cr_overdue_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) NOT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',
    `payment_id` bigint(20) DEFAULT NULL COMMENT '付款申请id',
    `gen_date` date DEFAULT NULL COMMENT '生成日期',

    `payment_apply_code` varchar(20) DEFAULT NULL COMMENT '借据编号',
    `overdue_principal` bigint(20) DEFAULT NULL COMMENT '逾期本金（单位：0.0001元）',
    `overdue_day` int(11) DEFAULT NULL COMMENT '逾期天数',
    `overdue_total` bigint(20) DEFAULT NULL COMMENT '逾期总额（单位：0.0001元）',
    `overdue_change_date` date DEFAULT NULL COMMENT '逾期改变日期',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_gen_date (`gen_date`),
    INDEX idx_contract_id(`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-逾期信息表';

CREATE TABLE `cr_five_class` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) DEFAULT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',

    `payment_apply_code` varchar(20) DEFAULT NULL COMMENT '借据编号',
    `five_class` varchar(20) DEFAULT NULL COMMENT '五级分类',
    `identification_date` date DEFAULT NULL COMMENT '五级分类认定日期',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_contract_id(`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-五级分类表';

CREATE TABLE `cr_pm_protocol` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `business_key` varchar(64) DEFAULT NULL COMMENT '业务主键',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',

    `payment_apply_code` varchar(20) DEFAULT NULL COMMENT '借据编号',
    `apply_payment_amount` bigint(20) DEFAULT NULL COMMENT '借据本金(单位：0.0001元)',
    `pm_contract_code` varchar(50) DEFAULT NULL COMMENT '抵押合同编号/ 质押合同编号',

    PRIMARY KEY (`id`),
    UNIQUE KEY uniq_business_key(`business_key`),
    INDEX idx_contract_id(`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-抵质押协议表';

CREATE TABLE `handle_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `module` varchar(32) DEFAULT NULL COMMENT '模块枚举',
    `deal_time` datetime DEFAULT NULL COMMENT '处理时间',

    PRIMARY KEY (`id`),
    unique index uniq_module (`module`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-模块处理记录';