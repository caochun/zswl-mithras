-- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 区域评级优化
CREATE TABLE `rating_client_area_indicator_config` (
                                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                                       `category_code` varchar(20) DEFAULT NULL COMMENT '类别编码',
                                                       `category_name` varchar(50) NOT NULL DEFAULT '' COMMENT '类别名称',
                                                       `indicator_code` varchar(50) NOT NULL DEFAULT '' COMMENT '指标code',
                                                       `indicator_name` varchar(255) NOT NULL DEFAULT '' COMMENT '指标中文名',
                                                       `indicator_unit` varchar(20) NOT NULL DEFAULT '' COMMENT '指标数值单位',
                                                       `indicator_data_type` varchar(20) NOT NULL DEFAULT '' COMMENT '指标数值数据类型',
                                                       `indicator_sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序字段',
                                                       `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                                                       `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
                                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
                                                       `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id	',
                                                       `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
                                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COMMENT='融租易内评区域指标配置表';

CREATE TABLE `rating_client_area_indicator` (
                                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                `rating_client_id` bigint(20) NOT NULL COMMENT '客户评级记录id',
                                                `area_uni_code` bigint(20) unsigned DEFAULT NULL COMMENT '区域编码',
                                                `year` varchar(10) DEFAULT NULL COMMENT '数据年份',
                                                `area_name` varchar(255) DEFAULT NULL COMMENT '区域编码',
                                                `category_code` varchar(20) NOT NULL DEFAULT '' COMMENT '类别编码',
                                                `category_name` varchar(50) NOT NULL DEFAULT '' COMMENT '类别名称',
                                                `indicator_code` varchar(50) DEFAULT NULL COMMENT '指标code',
                                                `indicator_value` decimal(30,4) DEFAULT NULL COMMENT '指标值',
                                                `indicator_value_system` decimal(30,4) DEFAULT NULL COMMENT '指标值（系统初始值）',
                                                `indicator_name` varchar(255) DEFAULT NULL COMMENT '指标中文名',
                                                `indicator_unit` varchar(20) DEFAULT NULL COMMENT '指标数值单位',
                                                `indicator_data_type` varchar(20) DEFAULT NULL COMMENT '指标数值数据类型',
                                                `indicator_sort` int(10) NOT NULL DEFAULT '0' COMMENT '排序字段',
                                                `mode` varchar(20) NOT NULL DEFAULT 'SYSTEM' COMMENT '取数方式，system-系统取数，import-人工',
                                                `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
                                                `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
                                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
                                                `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id	',
                                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
                                                PRIMARY KEY (`id`) USING BTREE,
                                                KEY `idx_rating_client_id` (`rating_client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='融租易内评区域指标';
-- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 区域评级优化