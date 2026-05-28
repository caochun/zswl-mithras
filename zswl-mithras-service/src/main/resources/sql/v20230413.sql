CREATE TABLE `proc_attention_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id ',
    `process_instance_id` varchar(64) NOT NULL COMMENT '流程id',
    `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
    `attention_type` tinyint(4) COMMENT '0不关注，1关注',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_process_instance_id_user_id` (`process_instance_id`, `user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='流程关注记录';

CREATE TABLE `sync_cq_record`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `record_id`        varchar(100) DEFAULT NULL COMMENT '请求数据唯一编号',
    `contract_code`    varchar(50)  DEFAULT NULL COMMENT '合同编号',
    `sourcebillno`     varchar(50)  DEFAULT NULL COMMENT '来源系统单号，租金为去除后四位现金流编号',
    `rent_actual_code` varchar(50)  DEFAULT NULL COMMENT '租金编号',
    `lease_rate`       varchar(50)  DEFAULT NULL COMMENT '应收金额',
    `date`             varchar(50)  DEFAULT NULL COMMENT '日期',
    `phase`            varchar(50)  DEFAULT NULL COMMENT '期项',
    `rent`             varchar(50)  DEFAULT NULL COMMENT '租金',
    `principal`        varchar(50)  DEFAULT NULL COMMENT '本金',
    `interest`         varchar(50)  DEFAULT NULL COMMENT '利息',
    `lastAmount`       varchar(50)  DEFAULT NULL COMMENT '剩余本金',
    `change_state`     varchar(50)  DEFAULT NULL COMMENT '变更类型',
    `cico_isinvoice`   varchar(50)  DEFAULT NULL COMMENT '是否开票',
    `create_time`      datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_time`      datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间	',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                `sync_cq_record_rent_actual_code` (`rent_actual_code`) USING BTREE COMMENT '现金流编号索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='苍穹-请求记录表';