-- 计价标准字段调整
alter table new_ftp_monthly_deduction_draft modify column `asset_encourage` int(11) DEFAULT NULL COMMENT '资产行业计价-其他产业类-鼓励介入类';
alter table new_ftp_monthly_deduction_draft modify column `asset_moderate` int(11) DEFAULT NULL COMMENT '资产行业计价-其他产业类-适度支持类';
alter table new_ftp_monthly_deduction_draft modify column `asset_cautious` int(11) DEFAULT NULL COMMENT '资产行业计价-其他产业类-谨慎支持类';
alter table new_ftp_monthly_deduction_draft add column `asset_public` int(11) DEFAULT NULL COMMENT '资产行业计价-公共事业类' after `asset_cautious`;
alter table new_ftp_monthly_deduction_draft add column `asset_civil` int(11) DEFAULT NULL COMMENT '资产行业计价-民生消费类' after `asset_public`;
alter table new_ftp_monthly_deduction_draft add column `asset_state_owned` int(11) DEFAULT NULL COMMENT '资产行业计价-国有产业类' after `asset_civil`;
alter table new_ftp_monthly_deduction_lib modify column `asset_encourage` int(11) DEFAULT NULL COMMENT '资产行业计价-其他产业类-鼓励介入类';
alter table new_ftp_monthly_deduction_lib modify column `asset_moderate` int(11) DEFAULT NULL COMMENT '资产行业计价-其他产业类-适度支持类';
alter table new_ftp_monthly_deduction_lib modify column `asset_cautious` int(11) DEFAULT NULL COMMENT '资产行业计价-其他产业类-谨慎支持类';
alter table new_ftp_monthly_deduction_lib add column `asset_public` int(11) DEFAULT NULL COMMENT '资产行业计价-公共事业类' after `asset_cautious`;
alter table new_ftp_monthly_deduction_lib add column `asset_civil` int(11) DEFAULT NULL COMMENT '资产行业计价-民生消费类' after `asset_public`;
alter table new_ftp_monthly_deduction_lib add column `asset_state_owned` int(11) DEFAULT NULL COMMENT '资产行业计价-国有产业类' after `asset_civil`;

-- 集团控股公司季度最低收益率表-新增
CREATE TABLE `new_ftp_quarterly_base_pricing_ext_draft` (
                                                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                            `ftp_id` bigint(20) DEFAULT NULL COMMENT '所属的主数据id',
                                                            `three_year` int(10) DEFAULT NULL COMMENT '3年内（含）',
                                                            `three_to_five_year` int(10) DEFAULT NULL COMMENT '3-5年（含）',
                                                            `more_than_five_year` int(10) DEFAULT NULL COMMENT '5年以上',
                                                            `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
                                                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
                                                            `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
                                                            PRIMARY KEY (`id`) USING BTREE,
                                                            KEY `idx_ftp_id` (`ftp_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='季度指导基础定价编辑区表（下半部分）';
-- 集团控股公司季度最低收益率版本表-新增
CREATE TABLE `new_ftp_quarterly_base_pricing_ext_lib` (
                                                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                          `ftp_id` bigint(20) DEFAULT NULL COMMENT '所属的主数据id',
                                                          `three_year` int(10) DEFAULT NULL COMMENT '3年内（含）',
                                                          `three_to_five_year` int(10) DEFAULT NULL COMMENT '3-5年（含）',
                                                          `more_than_five_year` int(10) DEFAULT NULL COMMENT '5年以上',
                                                          `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
                                                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
                                                          `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                                          `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
                                                          `version` varchar(40) NOT NULL COMMENT '版本号',
                                                          `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
                                                          `data_create_time` datetime DEFAULT NULL COMMENT '历史数据创建时间',
                                                          `data_create_by` bigint(20) DEFAULT NULL COMMENT '历史数据创建人ID',
                                                          `data_update_time` datetime DEFAULT NULL COMMENT '历史数据更新时间',
                                                          `data_update_by` bigint(20) DEFAULT NULL COMMENT '历史数据更新人ID',
                                                          `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
                                                          PRIMARY KEY (`id`) USING BTREE,
                                                          KEY `idx_ftp_id` (`ftp_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='季度指导基础定价编辑区表（下半部分）-版本表';

CREATE TABLE `proj_establish_trade_structure` (
                                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                                  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                                  `proj_establish_id` bigint(20) NOT NULL COMMENT '立项id',
                                                  `role` varchar(20) NOT NULL COMMENT '交易结构中的角色',
                                                  `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                                  PRIMARY KEY (`id`) USING BTREE,
                                                  KEY `idx_client_id` (`client_id`,`deleted`),
                                                  KEY `idx_proj_establish_id` (`proj_establish_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目立项交易结构辅助表';

CREATE TABLE `proj_review_trade_structure` (
                                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                               `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                               `proj_review_id` bigint(20) NOT NULL COMMENT '评审id',
                                               `role` varchar(20) NOT NULL COMMENT '交易结构中的角色',
                                               `client_id` bigint(20) NOT NULL COMMENT '客户id',
                                               PRIMARY KEY (`id`) USING BTREE,
                                               KEY `idx_client_id` (`client_id`,`deleted`),
                                               KEY `idx_proj_review_id` (`proj_review_id`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目评审交易结构辅助表';