CREATE TABLE `rzy_dm_calculate_indicator` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `area_uni_code` bigint(20) unsigned DEFAULT NULL COMMENT '区域编码',
    `year` varchar(10) DEFAULT NULL COMMENT '数据年份',
    `area_name` varchar(255) DEFAULT NULL COMMENT '区域编码',
    `indicator_code` varchar(50) DEFAULT NULL COMMENT '指标code',
    `indicator_value` decimal(30,4) DEFAULT NULL COMMENT '指标值',
    `indicator_name` varchar(255) DEFAULT NULL COMMENT '指标中文名',
    `dt` datetime DEFAULT NULL COMMENT '指标计算日期时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5535491 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='融租易内评区域模型指标值';