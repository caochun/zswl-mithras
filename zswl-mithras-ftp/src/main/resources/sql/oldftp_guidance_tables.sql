DROP TABLE IF EXISTS `ftp_monthly_guidance`;
CREATE TABLE `ftp_monthly_guidance` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `year` int(11) DEFAULT NULL COMMENT '年度',
    `month` int(2) DEFAULT NULL COMMENT '月度',
    `guidance_process_status` varchar(50) DEFAULT NULL COMMENT '审批状态',
    `guidance_record_status` varchar(50) DEFAULT NULL,
    `one_year_earnings_guidance` int(11) DEFAULT NULL COMMENT '一年期ftp收益指导报价',
    `one_to_three_earnings_guidance` int(11) DEFAULT NULL COMMENT '1-3年期ftp收益指导报价',
    `more_than_three_earnings_guidance` int(11) DEFAULT NULL COMMENT '3年以上ftp收益指导报价',
    `selling_price` int(11) DEFAULT NULL COMMENT '卖出价',
    `buying_price` int(11) DEFAULT NULL COMMENT '买入价',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uniq_year_month` (`year`,`month`) COMMENT '月度唯一'
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COMMENT='月度指导';

DROP TABLE IF EXISTS `ftp_monthly_guidance_lib`;
CREATE TABLE `ftp_monthly_guidance_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `year` int(11) DEFAULT NULL COMMENT '年度',
    `month` int(2) DEFAULT NULL COMMENT '月度',
    `guidance_process_status` varchar(50) DEFAULT NULL COMMENT '审批状态',
    `guidance_record_status` varchar(50) DEFAULT NULL,
    `one_year_earnings_guidance` int(11) DEFAULT NULL COMMENT '一年期ftp收益指导报价',
    `one_to_three_earnings_guidance` int(11) DEFAULT NULL COMMENT '1-3年期ftp收益指导报价',
    `more_than_three_earnings_guidance` int(11) DEFAULT NULL COMMENT '3年以上ftp收益指导报价',
    `selling_price` int(11) DEFAULT NULL COMMENT '卖出价',
    `buying_price` int(11) DEFAULT NULL COMMENT '买入价',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=228 DEFAULT CHARSET=utf8mb4 COMMENT='月度指导版本表';

DROP TABLE IF EXISTS `ftp_monthly_pricing`;
CREATE TABLE `ftp_monthly_pricing` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
   `enterprise_type` varchar(50) NOT NULL COMMENT '企业类型',
   `credit_term` varchar(50) NOT NULL COMMENT '期限',
   `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
   `value` int(11) NOT NULL COMMENT '值',
   `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
   `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
   `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1448 DEFAULT CHARSET=utf8mb4 COMMENT='月度ftp定价指导';

DROP TABLE IF EXISTS `ftp_monthly_pricing_lib`;
CREATE TABLE `ftp_monthly_pricing_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `enterprise_type` varchar(50) NOT NULL COMMENT '企业类型',
    `credit_term` varchar(50) NOT NULL COMMENT '期限',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `value` int(11) NOT NULL COMMENT '值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5288 DEFAULT CHARSET=utf8mb4 COMMENT='月度ftp定价指导版本表';

DROP TABLE IF EXISTS `ftp_monthly_valuation`;
CREATE TABLE `ftp_monthly_valuation` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `crdit_term` varchar(50) DEFAULT NULL COMMENT '期限',
    `financing_cost` int(11) DEFAULT NULL COMMENT '融资成本',
    `guarantee_cost` int(11) DEFAULT NULL COMMENT '担保成本',
    `subtotal_cost` int(11) DEFAULT NULL COMMENT '成本小计',
    `discount_rate` int(11) DEFAULT NULL COMMENT '国股银票转贴现利率',
    `discount_rate_weight` int(11) DEFAULT NULL COMMENT '权重1',
    `shibor_rate` int(11) DEFAULT NULL COMMENT '1年期shibor利率',
    `shibor_rate_weight` int(11) DEFAULT NULL COMMENT '权重2',
    `lpr_rate` int(11) DEFAULT NULL COMMENT '同期LPR利率',
    `lpr_rate_weight` int(11) DEFAULT NULL COMMENT '权重3',
    `finance_cost_trends` int(11) DEFAULT NULL COMMENT '融资成本趋势',
    `finance_cost_trends_weight` int(11) DEFAULT NULL COMMENT '权重4',
    `subtotal_rate` int(11) DEFAULT NULL COMMENT '小计',
    `subtotal_adjustment_valuation` int(11) DEFAULT NULL COMMENT '调整后计价小计',
    `encourage_valuation` int(11) DEFAULT NULL COMMENT '鼓励介入类',
    `moderate_support_valuation` int(11) DEFAULT NULL COMMENT '适度类',
    `cautious_valuation` int(11) DEFAULT NULL COMMENT '谨慎支持类',
    `state_own_listed_valuation` int(11) DEFAULT NULL COMMENT '国有/上市公司',
    `other_valuation` int(11) DEFAULT NULL COMMENT '其他类',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=248 DEFAULT CHARSET=utf8mb4 COMMENT='月度计价指导';

DROP TABLE IF EXISTS `ftp_monthly_valuation_lib`;
CREATE TABLE `ftp_monthly_valuation_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `crdit_term` varchar(50) DEFAULT NULL COMMENT '期限',
    `financing_cost` int(11) DEFAULT NULL COMMENT '融资成本',
    `guarantee_cost` int(11) DEFAULT NULL COMMENT '担保成本',
    `subtotal_cost` int(11) DEFAULT NULL COMMENT '成本小计',
    `discount_rate` int(11) DEFAULT NULL COMMENT '国股银票转贴现利率',
    `discount_rate_weight` int(11) DEFAULT NULL COMMENT '权重1',
    `shibor_rate` int(11) DEFAULT NULL COMMENT '1年期shibor利率',
    `shibor_rate_weight` int(11) DEFAULT NULL COMMENT '权重2',
    `lpr_rate` int(11) DEFAULT NULL COMMENT '同期LPR利率',
    `lpr_rate_weight` int(11) DEFAULT NULL COMMENT '权重3',
    `finance_cost_trends` int(11) DEFAULT NULL COMMENT '融资成本趋势',
    `finance_cost_trends_weight` int(11) DEFAULT NULL COMMENT '权重4',
    `subtotal_rate` int(11) DEFAULT NULL COMMENT '小计',
    `subtotal_adjustment_valuation` int(11) DEFAULT NULL COMMENT '调整后计价小计',
    `encourage_valuation` int(11) DEFAULT NULL COMMENT '鼓励介入类',
    `moderate_support_valuation` int(11) DEFAULT NULL COMMENT '适度类',
    `cautious_valuation` int(11) DEFAULT NULL COMMENT '谨慎支持类',
    `state_own_listed_valuation` int(11) DEFAULT NULL COMMENT '国有/上市公司',
    `other_valuation` int(11) DEFAULT NULL COMMENT '其他类',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=668 DEFAULT CHARSET=utf8mb4 COMMENT='月度计价指导版本表';

DROP TABLE IF EXISTS `ftp_quarterly_base_pricing`;
CREATE TABLE `ftp_quarterly_base_pricing` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `month_type` varchar(50) NOT NULL COMMENT '孟月、仲月、季月',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `credit_term` varchar(50) NOT NULL COMMENT '1年期、1-3年、3年以上',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1628 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导基础定价';

DROP TABLE IF EXISTS `ftp_quarterly_base_pricing_lib`;
CREATE TABLE `ftp_quarterly_base_pricing_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `month_type` varchar(50) NOT NULL COMMENT '孟月、仲月、季月',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `credit_term` varchar(50) NOT NULL COMMENT '1年期、1-3年、3年以上',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1628 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导基础定价版本表';

DROP TABLE IF EXISTS `ftp_quarterly_customer_principal_pricing`;
CREATE TABLE `ftp_quarterly_customer_principal_pricing` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `credit_term` varchar(50) NOT NULL COMMENT '1年期、1-3年、3年以上',
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=368 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导客户主体计价';

DROP TABLE IF EXISTS `ftp_quarterly_customer_principal_pricing_lib`;
CREATE TABLE `ftp_quarterly_customer_principal_pricing_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `credit_term` varchar(50) NOT NULL COMMENT '1年期、1-3年、3年以上',
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=368 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导客户主体计价版本表';

DROP TABLE IF EXISTS `ftp_quarterly_enterprise_pricing`;
CREATE TABLE `ftp_quarterly_enterprise_pricing` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `credit_term` varchar(50) NOT NULL,
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1088 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导按企业类型定价';

DROP TABLE IF EXISTS `ftp_quarterly_enterprise_pricing_lib`;
CREATE TABLE `ftp_quarterly_enterprise_pricing_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `enterprise_type` varchar(50) NOT NULL COMMENT '国有、其他',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `credit_term` varchar(50) NOT NULL,
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1088 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导按企业类型定价版本表';

DROP TABLE IF EXISTS `ftp_quarterly_guidance`;
CREATE TABLE `ftp_quarterly_guidance` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `year` int(4) DEFAULT NULL COMMENT '年度',
    `quarter` tinyint(1) DEFAULT NULL COMMENT '季度',
    `guidance_process_status` varchar(50) DEFAULT NULL COMMENT '审批状态',
    `guidance_record_status` varchar(50) DEFAULT NULL,
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uniq_year_quarter` (`year`,`quarter`) USING BTREE COMMENT '季度唯一'
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导';

DROP TABLE IF EXISTS `ftp_quarterly_guidance_lib`;
CREATE TABLE `ftp_quarterly_guidance_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `year` int(4) DEFAULT NULL COMMENT '年度',
    `quarter` tinyint(1) DEFAULT NULL COMMENT '季度',
    `guidance_process_status` varchar(50) DEFAULT NULL COMMENT '审批状态',
    `guidance_record_status` varchar(50) DEFAULT NULL,
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导版本表';

DROP TABLE IF EXISTS `ftp_quarterly_month_pricing`;
CREATE TABLE `ftp_quarterly_month_pricing` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
    `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
    `month_type` varchar(50) NOT NULL COMMENT '孟月、仲月、季月、均值',
    `credit_term` varchar(50) NOT NULL,
    `percent_value` int(11) NOT NULL COMMENT '利率值',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1088 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导按月定价';

DROP TABLE IF EXISTS `ftp_quarterly_month_pricing_lib`;
CREATE TABLE `ftp_quarterly_month_pricing_lib` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `guidance_id` bigint(20) NOT NULL COMMENT '所属指引id',
   `project_classify` varchar(50) NOT NULL COMMENT '项目分类',
   `month_type` varchar(50) NOT NULL COMMENT '孟月、仲月、季月、均值',
   `credit_term` varchar(50) NOT NULL,
   `percent_value` int(11) NOT NULL COMMENT '利率值',
   `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
   `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
   `site` varchar(20) DEFAULT NULL COMMENT '在excel中的坐标',
   `version` varchar(40) NOT NULL COMMENT '版本号',
   `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
   `data_create_time` datetime DEFAULT NULL,
   `data_create_by` bigint(20) DEFAULT NULL,
   `data_update_time` datetime DEFAULT NULL,
   `data_update_by` bigint(20) DEFAULT NULL,
   `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1088 DEFAULT CHARSET=utf8mb4 COMMENT='季度指导按月定价版本表';
