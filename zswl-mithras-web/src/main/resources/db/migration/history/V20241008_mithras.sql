--  *************************************** 客户管理制度SQL开始 ***************************************

-- 老表
alter table client add column `is_released` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否被释放，0-否，1-是';
alter table client add column `latest_user_id` bigint(20) DEFAULT NULL COMMENT '最新一次更新人';

alter table corp_address_info add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_bank_account add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_bond_info add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_commerce_info add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_contact_info add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_related_enterprise add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_shareholder_info add column user_id bigint(20) default null comment '数据所属用户id';

alter table corp_address_info_lib add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_bank_account_lib add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_bond_info_lib add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_commerce_info_lib add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_contact_info_lib add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_related_enterprise_lib add column user_id bigint(20) default null comment '数据所属用户id';
alter table corp_shareholder_info_lib add column user_id bigint(20) default null comment '数据所属用户id';
alter table client_transfer add column proj_establish_ids json DEFAULT NULL COMMENT '立项id列表';
alter table client_transfer add column `description` varchar(500) DEFAULT NULL COMMENT '说明';

CREATE TABLE `client_transfer_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `batch_no` varchar(50) NOT NULL COMMENT '批次编号',
  `approval_status` varchar(20) NOT NULL DEFAULT '' COMMENT '审批状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户移交申请表';

CREATE TABLE `client_transfer_weight` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                          `risk_transfer_value` int(10) DEFAULT NULL COMMENT '风险移交比例',
                                          `income_transfer_value` int(10) DEFAULT NULL COMMENT '收益移交比例',
                                          `proj_archive_status` varchar(50) DEFAULT NULL COMMENT '项目资料归档状态',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                          `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                          `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                          `batch_no` varchar(50) DEFAULT NULL COMMENT '批次编号',
                                          `to_dept_id` bigint(20) DEFAULT NULL COMMENT '移至部门id',
                                          `to_sponsor_id` bigint(20) DEFAULT NULL COMMENT '移至用户id',
                                          `to_cosponsor_ids` json DEFAULT NULL COMMENT '协办id列表',
                                          `contract_code` varchar(50) DEFAULT NULL COMMENT '合同编号',
                                          `proj_code` varchar(50) DEFAULT NULL COMMENT '项目编号',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=210 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户移交分配比例';

-- 新表
CREATE TABLE `client_authority` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
                                    `dept_id` bigint(20) DEFAULT NULL COMMENT '部门id',
                                    `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                    `level` tinyint(4) NOT NULL DEFAULT '0' COMMENT '权限级别',
                                    `source_id` varchar(20) NOT NULL DEFAULT '-1' COMMENT '来源业务id',
                                    `source_business_type` varchar(50) NOT NULL DEFAULT '' COMMENT '来源业务类型',
                                    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                    `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_clientid` (`client_id`),
                                    KEY `idx_userid` (`user_id`),
                                    KEY `idx_sourcebusinesstype_sourceid` (`source_business_type`,`source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户权限表';

CREATE TABLE `client_authority_apply_record` (
                                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                                 `batch_no` varchar(50) NOT NULL DEFAULT '' comment '批次号',
                                                 `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                                 `dept_id` bigint(20) NOT NULL COMMENT '部门id',
                                                 `user_id` bigint(20) NOT NULL COMMENT '用户id',
                                                 `level` tinyint(4) NOT NULL COMMENT '权限级别',
                                                 `reason` varchar(500) DEFAULT NULL COMMENT '申请原因',
                                                 `process_instance_id` varchar(64) DEFAULT '' COMMENT '流程实例id',
                                                 `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                                 `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                                 `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                 PRIMARY KEY (`id`),
                                                 KEY `idx_client_id` (`client_id`),
                                                 KEY `idx_user_id` (`user_id`),
                                                 KEY `idx_process_instance_id` (`process_instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户权限申请记录表';

CREATE TABLE `client_create_record` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                        `user_id` bigint(20) NOT NULL COMMENT '用户id',
                                        `dept_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '部门id',
                                        `unique_code` varchar(30) NOT NULL COMMENT '创建记录唯一标识码',
                                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE KEY `uniq_clientid_userid` (`client_id`,`user_id`),
                                        KEY `idx_client_id` (`client_id`),
                                        KEY `idx_dept_id` (`dept_id`),
                                        KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户创建记录';

CREATE TABLE `client_user_ref` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                   `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                   `user_id` bigint(20) NOT NULL COMMENT '用户id',
                                   `dept_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '部门id',
                                   `last_operate_time` datetime NOT NULL COMMENT '最近操作时间',
                                   `last_operate_type` varchar(20) NOT NULL DEFAULT '' COMMENT '最近操作类型',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                   `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
                                   `update_by` bigint(20) DEFAULT NULL comment '更新人id',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE KEY `uniq_clientid_useerid` (`client_id`,`user_id`),
                                   KEY `idx_client_id` (`client_id`),
                                   KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='客户副本和用户的关联记录';

CREATE TABLE `client_file_info` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
                                    `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                    `file_id` bigint(20) DEFAULT NULL COMMENT '文件id',
                                    `batch_no` varchar(64) NOT NULL COMMENT '上传批次',
                                    `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程实例id',
                                    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                    `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户申办权限文档表';

-- Create syntax for TABLE 'new_corp_address_info'
CREATE TABLE `new_corp_address_info` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                         `address_type` varchar(20) DEFAULT NULL COMMENT '地址类型',
                                         `country` varchar(20) DEFAULT NULL COMMENT '国家',
                                         `province` varchar(20) DEFAULT NULL COMMENT '省份',
                                         `city` varchar(20) DEFAULT NULL COMMENT '城市',
                                         `district` varchar(20) DEFAULT NULL COMMENT '区、县',
                                         `detail` varchar(200) DEFAULT NULL COMMENT '详细地址',
                                         `region_code` varchar(50) DEFAULT NULL COMMENT '区域代码',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                         `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人地址信息';

-- Create syntax for TABLE 'new_corp_address_info_lib'
CREATE TABLE `new_corp_address_info_lib` (
                                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                             `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                             `address_type` varchar(20) DEFAULT NULL COMMENT '地址类型',
                                             `country` varchar(20) DEFAULT NULL COMMENT '国家',
                                             `province` varchar(20) DEFAULT NULL COMMENT '省份',
                                             `city` varchar(20) DEFAULT NULL COMMENT '城市',
                                             `district` varchar(20) DEFAULT NULL COMMENT '区、县',
                                             `detail` varchar(200) DEFAULT NULL COMMENT '详细地址',
                                             `region_code` varchar(50) DEFAULT NULL COMMENT '区域代码',
                                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                             `origin_id` bigint(20) NOT NULL COMMENT '草稿表id',
                                             `version` varchar(32) NOT NULL COMMENT '版本',
                                             `data_create_time` datetime DEFAULT NULL COMMENT '编辑区数据创建时间',
                                             `data_create_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据创建人id',
                                             `data_update_time` datetime DEFAULT NULL COMMENT '编辑区数据更新时间',
                                             `data_update_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据更新人id',
                                             `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
                                             `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人地址信息';

-- Create syntax for TABLE 'new_corp_bank_account'
CREATE TABLE `new_corp_bank_account` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
                                         `main_account` tinyint(1) DEFAULT NULL COMMENT '是否主账号',
                                         `account_name` varchar(50) DEFAULT NULL COMMENT '账号名称',
                                         `account_number` varchar(50) DEFAULT NULL COMMENT '银行账号',
                                         `account_bank` varchar(100) DEFAULT NULL COMMENT '开户行',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                         `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人银行账户';

-- Create syntax for TABLE 'new_corp_bank_account_lib'
CREATE TABLE `new_corp_bank_account_lib` (
                                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                             `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
                                             `main_account` tinyint(1) DEFAULT NULL COMMENT '是否主账号',
                                             `account_name` varchar(50) DEFAULT NULL COMMENT '账号名称',
                                             `account_number` varchar(50) DEFAULT NULL COMMENT '银行账号',
                                             `account_bank` varchar(100) DEFAULT NULL COMMENT '开户行',
                                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                             `origin_id` bigint(20) NOT NULL COMMENT '草稿表id',
                                             `version` varchar(32) NOT NULL COMMENT '版本',
                                             `data_create_time` datetime DEFAULT NULL COMMENT '编辑区数据创建时间',
                                             `data_create_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据创建人id',
                                             `data_update_time` datetime DEFAULT NULL COMMENT '编辑区数据更新时间',
                                             `data_update_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据更新人id',
                                             `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
                                             `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人银行账户';

-- Create syntax for TABLE 'new_corp_bond_info'
CREATE TABLE `new_corp_bond_info` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
                                      `rate_date` date DEFAULT NULL COMMENT '评级日期',
                                      `rate_company` varchar(50) DEFAULT NULL COMMENT '评级公司',
                                      `rate` varchar(20) DEFAULT NULL COMMENT '评级',
                                      `rate_future` varchar(100) DEFAULT NULL COMMENT '评级展望',
                                      `issue_total` bigint(20) DEFAULT NULL COMMENT '发行总额，单位亿元',
                                      `issue_amount` bigint(20) DEFAULT NULL COMMENT '发行只数',
                                      `stock_scale` bigint(20) DEFAULT NULL COMMENT '存量规模，单位：亿元',
                                      `stock_amount` bigint(20) DEFAULT NULL COMMENT '存量只数',
                                      `maturity_scale` bigint(20) DEFAULT NULL COMMENT '到期规模，单位：亿元',
                                      `maturity_amount` bigint(20) DEFAULT NULL COMMENT '到期只数',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                      `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人股票信息';

-- Create syntax for TABLE 'new_corp_bond_info_lib'
CREATE TABLE `new_corp_bond_info_lib` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
                                          `rate_date` date DEFAULT NULL COMMENT '评级日期',
                                          `rate_company` varchar(50) DEFAULT NULL COMMENT '评级公司',
                                          `rate` varchar(20) DEFAULT NULL COMMENT '评级',
                                          `rate_future` varchar(100) DEFAULT NULL COMMENT '评级展望',
                                          `issue_total` bigint(20) DEFAULT NULL COMMENT '发行总额，单位亿元',
                                          `issue_amount` bigint(20) DEFAULT NULL COMMENT '发行只数',
                                          `stock_scale` bigint(20) DEFAULT NULL COMMENT '存量规模，单位：亿元',
                                          `stock_amount` bigint(20) DEFAULT NULL COMMENT '存量只数',
                                          `maturity_scale` bigint(20) DEFAULT NULL COMMENT '到期规模，单位：亿元',
                                          `maturity_amount` bigint(20) DEFAULT NULL COMMENT '到期只数',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                          `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                          `origin_id` bigint(20) NOT NULL COMMENT '草稿表id',
                                          `version` varchar(32) NOT NULL COMMENT '版本',
                                          `data_create_time` datetime DEFAULT NULL COMMENT '编辑区数据创建时间',
                                          `data_create_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据创建人id',
                                          `data_update_time` datetime DEFAULT NULL COMMENT '编辑区数据更新时间',
                                          `data_update_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据更新人id',
                                          `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
                                          `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人股票信息';

-- Create syntax for TABLE 'new_corp_commerce_info'
CREATE TABLE `new_corp_commerce_info` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                          `triple_cert_in_one` tinyint(1) DEFAULT NULL COMMENT '三证合一',
                                          `zhong_zheng_code` varchar(50) DEFAULT NULL COMMENT '中征码',
                                          `domestic_or_abroad` varchar(30) DEFAULT 'DOMESTIC' COMMENT '境内or境外',
                                          `special_org_code` varchar(50) DEFAULT NULL COMMENT '境外企业特殊机构代码',
                                          `org_code` varchar(50) DEFAULT NULL COMMENT '组织机构代码',
                                          `biz_license_code` varchar(50) DEFAULT NULL COMMENT '营业执照号',
                                          `continuous_status` varchar(20) DEFAULT NULL COMMENT '存续状态',
                                          `establish_date` date DEFAULT NULL COMMENT '成立日期',
                                          `approval_date` date DEFAULT NULL COMMENT '核准日期',
                                          `biz_licence_long_term` tinyint(1) DEFAULT NULL COMMENT '营业许可证是否为长期',
                                          `biz_license_end_date` date DEFAULT NULL COMMENT '营业许可证到期日(如果许可证是非长期类型)',
                                          `biz_scope` text COMMENT '业务范围',
                                          `industry_type` varchar(50) DEFAULT NULL COMMENT '行业分类',
                                          `risk_control_industry_classify` varchar(50) DEFAULT NULL COMMENT '风控行业分类',
                                          `economy_type` varchar(50) DEFAULT NULL COMMENT '经济类型',
                                          `org_type` varchar(20) DEFAULT NULL COMMENT '组织机构类型',
                                          `org_scale` varchar(20) DEFAULT NULL COMMENT '企业规模',
                                          `register_currency_type` varchar(20) DEFAULT NULL COMMENT '注册币种',
                                          `register_capital` bigint(20) DEFAULT NULL COMMENT '注册资本',
                                          `real_currency_type` varchar(20) DEFAULT NULL COMMENT '实收币种',
                                          `real_capital` bigint(20) DEFAULT NULL COMMENT '实收资本',
                                          `register_capital_rate` bigint(20) DEFAULT NULL COMMENT '注册资本到位率',
                                          `corp_represent` varchar(20) DEFAULT NULL COMMENT '法人代表',
                                          `corp_gender` varchar(20) DEFAULT NULL COMMENT '法人性别',
                                          `corp_cert_type` varchar(20) DEFAULT NULL COMMENT '法人证件类型',
                                          `corp_cert_code` varchar(50) DEFAULT NULL COMMENT '法人证件号码',
                                          `listed_company` tinyint(1) DEFAULT NULL COMMENT '是否上市公司',
                                          `enterprise_nature` varchar(20) DEFAULT NULL COMMENT '企业性质',
                                          `ownership_type` varchar(32) DEFAULT NULL COMMENT '是否是上市公司控股',
                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                          `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                          `client_code` varchar(50) DEFAULT NULL COMMENT '客户编号（冗余）',
                                          `is_related` tinyint(1) DEFAULT '0' COMMENT '是否关联方',
                                          `group_flag` tinyint(4) DEFAULT NULL COMMENT '是否集团公司 1是，0否',
                                          `belong_group_client_id` bigint(20) DEFAULT NULL COMMENT '所属集团 法人客户id -1无 -2自己',
                                          `is_market` tinyint(1) DEFAULT '0' COMMENT '是否在沪深主板、中小板、创业板上市',
                                          `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          UNIQUE KEY `uniq_client_user_id` (`client_id`,`user_id`),
                                          KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人工商信息表';

-- Create syntax for TABLE 'new_corp_commerce_info_lib'
CREATE TABLE `new_corp_commerce_info_lib` (
                                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                              `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                              `triple_cert_in_one` tinyint(1) DEFAULT NULL COMMENT '三证合一',
                                              `zhong_zheng_code` varchar(50) DEFAULT NULL COMMENT '中征码',
                                              `domestic_or_abroad` varchar(30) DEFAULT 'DOMESTIC' COMMENT '境内or境外',
                                              `special_org_code` varchar(50) DEFAULT NULL COMMENT '境外企业特殊机构代码',
                                              `org_code` varchar(50) DEFAULT NULL COMMENT '组织机构代码',
                                              `biz_license_code` varchar(50) DEFAULT NULL COMMENT '营业执照号',
                                              `continuous_status` varchar(20) DEFAULT NULL COMMENT '存续状态',
                                              `establish_date` date DEFAULT NULL COMMENT '成立日期',
                                              `approval_date` date DEFAULT NULL COMMENT '核准日期',
                                              `biz_licence_long_term` tinyint(1) DEFAULT NULL COMMENT '营业许可证是否为长期',
                                              `biz_license_end_date` date DEFAULT NULL COMMENT '营业许可证到期日(如果许可证是非长期类型)',
                                              `biz_scope` text COMMENT '业务范围',
                                              `industry_type` varchar(50) DEFAULT NULL COMMENT '行业分类',
                                              `risk_control_industry_classify` varchar(50) DEFAULT NULL COMMENT '风控行业分类',
                                              `economy_type` varchar(50) DEFAULT NULL COMMENT '经济类型',
                                              `org_type` varchar(20) DEFAULT NULL COMMENT '组织机构类型',
                                              `org_scale` varchar(20) DEFAULT NULL COMMENT '企业规模',
                                              `register_currency_type` varchar(20) DEFAULT NULL COMMENT '注册币种',
                                              `register_capital` varchar(50) DEFAULT NULL COMMENT '注册资本',
                                              `real_currency_type` varchar(20) DEFAULT NULL COMMENT '实收币种',
                                              `real_capital` varchar(50) DEFAULT NULL COMMENT '实收资本',
                                              `register_capital_rate` varchar(20) DEFAULT NULL COMMENT '注册资本到位率',
                                              `corp_represent` varchar(20) DEFAULT NULL COMMENT '法人代表',
                                              `corp_gender` varchar(20) DEFAULT NULL COMMENT '法人性别',
                                              `corp_cert_type` varchar(20) DEFAULT NULL COMMENT '法人证件类型',
                                              `corp_cert_code` varchar(50) DEFAULT NULL COMMENT '法人证件号码',
                                              `listed_company` tinyint(1) DEFAULT NULL COMMENT '是否上市公司',
                                              `enterprise_nature` varchar(20) DEFAULT NULL COMMENT '企业性质',
                                              `ownership_type` varchar(32) DEFAULT NULL COMMENT '是否是上市公司控股',
                                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                              `origin_id` bigint(20) NOT NULL COMMENT '草稿表id',
                                              `version` varchar(32) NOT NULL COMMENT '版本',
                                              `data_create_time` datetime DEFAULT NULL COMMENT '编辑区数据创建时间',
                                              `data_create_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据创建人id',
                                              `data_update_time` datetime DEFAULT NULL COMMENT '编辑区数据更新时间',
                                              `data_update_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据更新人id',
                                              `client_code` varchar(50) DEFAULT NULL COMMENT '客户编号（冗余）',
                                              `is_related` tinyint(1) DEFAULT '0' COMMENT '是否关联方',
                                              `group_flag` tinyint(4) DEFAULT NULL COMMENT '是否集团公司 1是，0否',
                                              `belong_group_client_id` bigint(20) DEFAULT NULL COMMENT '所属集团 法人客户id -1无 -2自己',
                                              `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
                                              `is_market` tinyint(1) DEFAULT '0' COMMENT '是否在沪深主板、中小板、创业板上市',
                                              `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人工商信息表';

-- Create syntax for TABLE 'new_corp_contact_info'
CREATE TABLE `new_corp_contact_info` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                         `main` tinyint(1) DEFAULT NULL COMMENT '是否主联系人',
                                         `position` varchar(20) DEFAULT NULL COMMENT '职务',
                                         `gender` varchar(20) DEFAULT NULL COMMENT '性别',
                                         `name` varchar(50) DEFAULT NULL COMMENT '姓名',
                                         `telephone` varchar(20) DEFAULT NULL COMMENT '电话',
                                         `landline_telephone` varchar(30) DEFAULT NULL COMMENT '座机',
                                         `mail` varchar(50) DEFAULT NULL COMMENT '邮箱',
                                         `cert_type` varchar(20) DEFAULT NULL COMMENT '证件类型',
                                         `cert_number` varchar(50) DEFAULT NULL COMMENT '证件号码',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                         `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人联系人';

-- Create syntax for TABLE 'new_corp_contact_info_lib'
CREATE TABLE `new_corp_contact_info_lib` (
                                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                             `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                             `main` tinyint(1) DEFAULT NULL COMMENT '是否主联系人',
                                             `position` varchar(20) DEFAULT NULL COMMENT '职务',
                                             `gender` varchar(20) DEFAULT NULL COMMENT '性别',
                                             `name` varchar(50) DEFAULT NULL COMMENT '姓名',
                                             `telephone` varchar(20) DEFAULT NULL COMMENT '电话',
                                             `landline_telephone` varchar(30) DEFAULT NULL COMMENT '座机',
                                             `mail` varchar(50) DEFAULT NULL COMMENT '邮箱',
                                             `cert_type` varchar(20) DEFAULT NULL COMMENT '证件类型',
                                             `cert_number` varchar(50) DEFAULT NULL COMMENT '证件号码',
                                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                             `origin_id` bigint(20) NOT NULL COMMENT '草稿表id',
                                             `version` varchar(32) NOT NULL COMMENT '版本',
                                             `data_create_time` datetime DEFAULT NULL COMMENT '编辑区数据创建时间',
                                             `data_create_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据创建人id',
                                             `data_update_time` datetime DEFAULT NULL COMMENT '编辑区数据更新时间',
                                             `data_update_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据更新人id',
                                             `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
                                             `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人联系人';

-- Create syntax for TABLE 'new_corp_relate_enterprise'
CREATE TABLE `new_corp_relate_enterprise` (
                                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                               `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
                                               `enterprise_name` varchar(100) DEFAULT NULL COMMENT '关联企业名称',
                                               `relationship` varchar(100) DEFAULT NULL COMMENT '关联关系',
                                               `register_capital` bigint(20) DEFAULT NULL COMMENT '注册资本',
                                               `shareholding_ratio` bigint(20) DEFAULT NULL COMMENT '持股比例',
                                               `invest_amount` bigint(20) DEFAULT NULL COMMENT '投资金额（万元）',
                                               `continuous_status` varchar(20) DEFAULT NULL COMMENT '存续状态',
                                               `establish_date` date DEFAULT NULL COMMENT '成立日期',
                                               `industry_type` varchar(20) DEFAULT NULL COMMENT '行业',
                                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                               `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               UNIQUE KEY `uniq_clientid_enterprisename_userid` (`client_id`,`enterprise_name`,`user_id`),
                                               KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人关联企业';

-- Create syntax for TABLE 'new_corp_relate_enterprise_lib'
CREATE TABLE `new_corp_relate_enterprise_lib` (
                                                   `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                   `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
                                                   `enterprise_name` varchar(100) DEFAULT NULL COMMENT '关联企业名称',
                                                   `relationship` varchar(100) DEFAULT NULL COMMENT '关联关系',
                                                   `register_capital` bigint(20) DEFAULT NULL COMMENT '注册资本',
                                                   `shareholding_ratio` bigint(20) DEFAULT NULL COMMENT '持股比例',
                                                   `invest_amount` bigint(20) DEFAULT NULL COMMENT '投资金额（万元）',
                                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                   `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                   `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                                   `origin_id` bigint(20) NOT NULL COMMENT '草稿表id',
                                                   `version` varchar(32) NOT NULL COMMENT '版本',
                                                   `industry_type` varchar(20) DEFAULT NULL COMMENT '行业',
                                                   `establish_date` date DEFAULT NULL COMMENT '成立日期',
                                                   `data_create_time` datetime DEFAULT NULL COMMENT '编辑区数据创建时间',
                                                   `data_create_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据创建人id',
                                                   `data_update_time` datetime DEFAULT NULL COMMENT '编辑区数据更新时间',
                                                   `data_update_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据更新人id',
                                                   `continuous_status` varchar(20) DEFAULT NULL COMMENT '存续状态',
                                                   `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
                                                   `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                                   PRIMARY KEY (`id`) USING BTREE,
                                                   KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的法人关联企业';

-- Create syntax for TABLE 'new_corp_shareholder_info'
CREATE TABLE `new_corp_shareholder_info` (
                                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                             `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
                                             `shareholder_type` varchar(20) DEFAULT NULL COMMENT '股东类型',
                                             `shareholder_name` varchar(500) DEFAULT NULL COMMENT '股东姓名',
                                             `paid_total` bigint(20) DEFAULT NULL COMMENT '认缴金额（万）',
                                             `actual_paid_total` bigint(20) DEFAULT NULL COMMENT '实缴金额',
                                             `capital_way` varchar(20) DEFAULT NULL COMMENT '出资方式',
                                             `capital_percent` bigint(20) DEFAULT NULL COMMENT '出资占比',
                                             `real_controller` tinyint(1) DEFAULT NULL COMMENT '是否实际控制人',
                                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                             `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             UNIQUE KEY `uniq_clientid_shareholdername_userid` (`client_id`,`shareholder_name`,`user_id`),
                                             KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的股东信息';

-- Create syntax for TABLE 'new_corp_shareholder_info_lib'
CREATE TABLE `new_corp_shareholder_info_lib` (
                                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                 `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
                                                 `shareholder_type` varchar(20) DEFAULT NULL COMMENT '股东类型',
                                                 `shareholder_name` varchar(500) DEFAULT NULL COMMENT '股东姓名',
                                                 `paid_total` bigint(20) DEFAULT NULL COMMENT '认缴金额（万）',
                                                 `actual_paid_total` bigint(20) DEFAULT NULL COMMENT '实缴金额',
                                                 `capital_way` varchar(20) DEFAULT NULL COMMENT '出资方式',
                                                 `capital_percent` bigint(20) DEFAULT NULL COMMENT '出资占比',
                                                 `real_controller` tinyint(1) DEFAULT NULL COMMENT '是否实际控制人',
                                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
                                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                 `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
                                                 `origin_id` bigint(20) NOT NULL COMMENT '草稿表id',
                                                 `version` varchar(32) NOT NULL COMMENT '版本',
                                                 `data_create_time` datetime DEFAULT NULL COMMENT '编辑区数据创建时间',
                                                 `data_create_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据创建人id',
                                                 `data_update_time` datetime DEFAULT NULL COMMENT '编辑区数据更新时间',
                                                 `data_update_by` bigint(20) DEFAULT NULL COMMENT '编辑区数据更新人id',
                                                 `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
                                                 `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
                                                 PRIMARY KEY (`id`) USING BTREE,
                                                 KEY `idx_clientid_userid` (`client_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='新的股东信息';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('fileremove-clienttransfer', '客户移交-文件单个删除', 0, 6, NULL, NULL, NULL, 'POST', '/file/remove', 2, NULL),
	('filebatchremove-clienttransfer', '客户移交-文件批量删除', 0, 6, NULL, NULL, NULL, 'POST', '/file/batch/remove', 2, NULL),
	('filedownload-clienttransfer', '客户移交-下载文件', 0, 6, NULL, NULL, NULL, 'GET', '/file/download', 1, NULL),
	('fileupload-clienttransfer', '客户移交-上传资料', 0, 6, NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL),
	('clienttransferapplyqueryByBatchNo', '客户移交-批次号查询移交申请信息', 0, 6, NULL, NULL, NULL, 'POST', '/client/transfer/apply/queryByBatchNo', 1, NULL),
	('clienttransferapplycreate', '客户移交-创建移交申请', 0, 6, NULL, NULL, NULL, 'POST', '/client/transfer/apply/create', 2, NULL),
	('clientbatchnumber', '客户当前批次版本', 0, 6, NULL, NULL, NULL, 'POST', '/client/batch/number', 2, NULL),
	('clientapifilelist', '客户管理获取文件信息', 0, 6, NULL, NULL, NULL, 'POST', '/client/file/list', 2, NULL),
	('clientfileupload', '客户管理上传文件', 0, 6, NULL, NULL, NULL, 'POST', '/client/file/upload', 2, NULL),
	('clientapplyoccupy', '检查客户是否被占有', 0, 6, NULL, NULL, NULL, 'POST', '/client/apply/occupy', 2, NULL),
	('clientapplystatus', '得到客户状态和管控权限', 0, 6, NULL, NULL, NULL, 'POST', '/client/apply/status', 2, NULL),
	('clientapplyown', '客户详情页面是否有编辑查看权限', 0, 6, NULL, NULL, NULL, 'POST', '/client/apply/own', 2, NULL),
	('clienttransferexport', '导出客户移交信息模版', 0, 6, NULL, NULL, NULL, 'POST', '/client/transfer/export', 2, NULL),
	('clientapplyvalidate', '校验客户申办权限申请客户信息', 0, 6, NULL, NULL, NULL, 'POST', '/client/apply/validate', 2, NULL),
	('clientapplydetail', '客户申办权限申请客户信息', 0, 6, NULL, NULL, NULL, 'POST', '/client/apply/detail', 2, NULL),
	('clientapplymodify', '客户申办权限信息保存', 0, 6, NULL, NULL, NULL, 'POST', '/client/apply/modify', 2, NULL),
	('clientapplyeffect', '客户申办权限申请（或提交审批）', 0, 6, NULL, NULL, NULL, 'POST', '/client/apply/effect', 2, NULL),
	('clientauthorityeffect', '客户权限生效（或提交审批）', 0, 6, NULL, NULL, NULL, 'POST', '/client/authority/effect', 2, NULL),
	('clientnewTransferdetail', '新的客户移交审批详情', 0, 6, NULL, NULL, NULL, 'POST', '/client/newTransfer/detail', 2, NULL),
	('clientnewTransferremove', '取消操作新的提交转移编辑用户负责的客户', 0, 6, NULL, NULL, NULL, 'POST', '/client/newTransfer/remove', 2, NULL),
	('clientnewTransfermodify', '新的提交转移编辑用户负责的客户', 0, 6, NULL, NULL, NULL, 'POST', '/client/newTransfer/modify', 2, NULL),
	('clientnewTransfersubmit', '新的提交转移指定用户负责的客户', 0, 6, NULL, NULL, NULL, 'POST', '/client/newTransfer/submit', 2, NULL),
	('clientnewListBySponsors', '新的获取指定用户负责的客户列表', 0, 6, NULL, NULL, NULL, 'POST', '/client/newList/BySponsors', 2, NULL),
	('clientnewlist', '新的客户列表', 0, 6, NULL, NULL, NULL, 'POST', '/client/new/list', 2, NULL),
    ('selectneworgs', '获取新的创建部门列表', 0, 6, NULL, NULL, NULL, 'GET', '/select/new/orgs', 2, NULL),
    ('filelist-clienttransform', '客户移交-资料清单', 0, 6, NULL, NULL, NULL, 'POST', '/file/list', 1, NULL),
    ('clientnewTransferdetailmodify', '新的客户移交审批详情编辑信息', 0, 6, NULL, NULL, NULL, 'POST', '/client/newTransfer/detail/modify', 2, NULL),
    ('clientnoauthoritylist', '获取可申请申办权的客户', 0, 6, NULL, NULL, NULL, 'POST', '/client/noauthority/list', 1, NULL);

INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`) VALUES ('job', '岗位类型', 'humanresourcesleader', '人力资源部分管领导', 10);
INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`) VALUES ('job', '岗位类型', 'yyglbleader', '运营管理部分管领导', 10);

-- 初始化用户自己的客户副本数据（sql初始化完成后需要通过脚本填充user_id字段）
insert into new_corp_address_info (id, client_id, address_type, country, province, city, district, detail, region_code)
select id, client_id, address_type, country, province, city, district, detail, region_code from corp_address_info;

insert into new_corp_address_info_lib (id, client_id, address_type, country, province, city, district, detail, region_code, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by)
select id, client_id, address_type, country, province, city, district, detail, region_code, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by from corp_address_info_lib;

insert into new_corp_bank_account (id, client_id, main_account, account_name, account_number, account_bank)
select id, client_id, main_account, account_name, account_number, account_bank from corp_bank_account;

insert into new_corp_bank_account_lib (id, client_id, main_account, account_name, account_number, account_bank, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by)
select id, client_id, main_account, account_name, account_number, account_bank, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by from corp_bank_account_lib;

insert into new_corp_bond_info (id, client_id, rate_date, rate_company, rate, rate_future, issue_total, issue_amount, stock_scale, stock_amount, maturity_scale, maturity_amount)
select id, client_id, rate_date, rate_company, rate, rate_future, issue_total, issue_amount, stock_scale, stock_amount, maturity_scale, maturity_amount from corp_bond_info;

insert into new_corp_bond_info_lib (id, client_id, rate_date, rate_company, rate, rate_future, issue_total, issue_amount, stock_scale, stock_amount, maturity_scale, maturity_amount, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by)
select id, client_id, rate_date, rate_company, rate, rate_future, issue_total, issue_amount, stock_scale, stock_amount, maturity_scale, maturity_amount, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by from corp_bond_info_lib;

insert into new_corp_commerce_info (id, client_id, triple_cert_in_one, zhong_zheng_code, org_code, biz_license_code, continuous_status, establish_date, approval_date, biz_licence_long_term, biz_license_end_date, biz_scope, industry_type, economy_type, org_type, org_scale, register_currency_type, register_capital, real_currency_type, real_capital, register_capital_rate, corp_represent, corp_gender, corp_cert_type, corp_cert_code, listed_company, enterprise_nature, ownership_type, client_code, is_related, group_flag, belong_group_client_id, domestic_or_abroad, special_org_code, risk_control_industry_classify, is_market)
select id, client_id, triple_cert_in_one, zhong_zheng_code, org_code, biz_license_code, continuous_status, establish_date, approval_date, biz_licence_long_term, biz_license_end_date, biz_scope, industry_type, economy_type, org_type, org_scale, register_currency_type, register_capital, real_currency_type, real_capital, register_capital_rate, corp_represent, corp_gender, corp_cert_type, corp_cert_code, listed_company, enterprise_nature, ownership_type, client_code, is_related, group_flag, belong_group_client_id, domestic_or_abroad, special_org_code, risk_control_industry_classify, is_market from corp_commerce_info;

insert into new_corp_commerce_info_lib (id, client_id, triple_cert_in_one, zhong_zheng_code, org_code, biz_license_code, continuous_status, establish_date, approval_date, biz_licence_long_term, biz_license_end_date, biz_scope, industry_type, economy_type, org_type, org_scale, register_currency_type, register_capital, real_currency_type, real_capital, register_capital_rate, corp_represent, corp_gender, corp_cert_type, corp_cert_code, listed_company, enterprise_nature, ownership_type, client_code, is_related, group_flag, belong_group_client_id, domestic_or_abroad, special_org_code, risk_control_industry_classify, is_market, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by)
select id, client_id, triple_cert_in_one, zhong_zheng_code, org_code, biz_license_code, continuous_status, establish_date, approval_date, biz_licence_long_term, biz_license_end_date, biz_scope, industry_type, economy_type, org_type, org_scale, register_currency_type, register_capital, real_currency_type, real_capital, register_capital_rate, corp_represent, corp_gender, corp_cert_type, corp_cert_code, listed_company, enterprise_nature, ownership_type, client_code, is_related, group_flag, belong_group_client_id, domestic_or_abroad, special_org_code, risk_control_industry_classify, is_market, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by from corp_commerce_info_lib;

insert into new_corp_contact_info (id, client_id, main, position, gender, name, telephone, landline_telephone, mail, cert_type, cert_number)
select id, client_id, main, position, gender, name, telephone, landline_telephone, mail, cert_type, cert_number from corp_contact_info;

insert into new_corp_contact_info_lib (id, client_id, main, position, gender, name, telephone, landline_telephone, mail, cert_type, cert_number, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by)
select id, client_id, main, position, gender, name, telephone, landline_telephone, mail, cert_type, cert_number, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by from corp_contact_info_lib;

insert into new_corp_relate_enterprise (id, client_id, enterprise_name, relationship, register_capital, shareholding_ratio, invest_amount, continuous_status, establish_date, industry_type)
select id, client_id, enterprise_name, relationship, register_capital, shareholding_ratio, invest_amount, continuous_status, establish_date, industry_type from corp_related_enterprise;

insert into new_corp_relate_enterprise_lib (id, client_id, enterprise_name, relationship, register_capital, shareholding_ratio, invest_amount, continuous_status, establish_date, industry_type, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by)
select id, client_id, enterprise_name, relationship, register_capital, shareholding_ratio, invest_amount, continuous_status, establish_date, industry_type, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by from corp_related_enterprise_lib;

insert into new_corp_shareholder_info (id, client_id, shareholder_type, shareholder_name, paid_total, actual_paid_total, capital_way, capital_percent, real_controller)
select id, client_id, shareholder_type, shareholder_name, paid_total, actual_paid_total, capital_way, capital_percent, real_controller from corp_shareholder_info;

insert into new_corp_shareholder_info_lib (id, client_id, shareholder_type, shareholder_name, paid_total, actual_paid_total, capital_way, capital_percent, real_controller, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by)
select id, client_id, shareholder_type, shareholder_name, paid_total, actual_paid_total, capital_way, capital_percent, real_controller, origin_id, version, version_type, data_create_time, data_create_by, data_update_time, data_update_by from corp_shareholder_info_lib;

-- 更新归属用户
update new_corp_address_info as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_address_info as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;
update new_corp_address_info_lib as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_address_info_lib as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;

update new_corp_bank_account as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_bank_account as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;
update new_corp_bank_account_lib as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_bank_account_lib as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;

update new_corp_bond_info as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_bond_info as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;
update new_corp_bond_info_lib as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_bond_info_lib as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;

update new_corp_commerce_info as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_commerce_info as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;
update new_corp_commerce_info_lib as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_commerce_info_lib as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;

update new_corp_contact_info as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_contact_info as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;
update new_corp_contact_info_lib as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_contact_info_lib as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;

update new_corp_relate_enterprise as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_relate_enterprise as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;
update new_corp_relate_enterprise_lib as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_relate_enterprise_lib as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;

update new_corp_shareholder_info as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_shareholder_info as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;
update new_corp_shareholder_info_lib as a, client as b set a.user_id = b.belong_sponsor_id where a.client_id = b.id and b.belong_sponsor_id is not null;
update new_corp_shareholder_info_lib as a, client as b set a.user_id = b.create_by where a.client_id = b.id and b.belong_sponsor_id is null;

update client set latest_user_id = belong_sponsor_id, update_time = update_time where client_type = 'CORPORATION' and belong_sponsor_id is not null;
update client set latest_user_id = create_by, update_time = update_time where client_type = 'CORPORATION' and belong_sponsor_id is null;

insert into client_transfer_apply (id, batch_no, approval_status)
select id, batch_no, '' from client_transfer group by batch_no;

--  *************************************** 客户管理制度SQL结束 ***************************************