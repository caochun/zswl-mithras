-- 请求重试表
CREATE TABLE `exception_request_info`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `platform`         varchar(30) DEFAULT NULL COMMENT '平台',
    `business_id`      varchar(30) DEFAULT NULL COMMENT '业务唯一ID',
    `req_data_md5`     varchar(32) DEFAULT NULL COMMENT '请求md5值',
    `max_retry_amount` int(11) DEFAULT '3' COMMENT '最大重试次数',
    `retry_amount`     int(11) DEFAULT '0' COMMENT '重试次数',
    `req_data`         json        DEFAULT NULL COMMENT '请求参数',
    `response`         json        DEFAULT NULL COMMENT '响应值',
    `retry_flag`       int(11) DEFAULT '0' COMMENT '重试标识 0 失败，1成功',
    `create_time`      datetime    DEFAULT CURRENT_TIMESTAMP,
    `update_by`        bigint(20) DEFAULT NULL,
    `create_by`        bigint(20) DEFAULT NULL,
    `update_time`      datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `exception_request_info_business_unique_key` (`platform`,`business_id`,`req_data_md5`) USING BTREE COMMENT '业务唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=2028 DEFAULT CHARSET=utf8mb4 COMMENT='异常请求记录表';

-- 苍穹请求记录表 --已经执行
CREATE TABLE `sync_cq_record`
(
    `id`               BIGINT (20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `record_id`        VARCHAR(50) DEFAULT NULL COMMENT '请求数据唯一编号',
    `contract_code`    VARCHAR(50) DEFAULT NULL COMMENT '合同编号',
    `sourcebillno`     VARCHAR(50) DEFAULT NULL COMMENT '来源系统单号，租金为去除后四位现金流编号',
    `rent_actual_code` VARCHAR(50) DEFAULT NULL COMMENT '租金编号',
    `lease_rate`       VARCHAR(50) DEFAULT NULL COMMENT '应收金额',
    `date`             VARCHAR(50) DEFAULT NULL COMMENT '日期',
    `phase`            VARCHAR(50) DEFAULT NULL COMMENT '期项',
    `rent`             VARCHAR(50) DEFAULT NULL COMMENT '租金',
    `principal`        VARCHAR(50) DEFAULT NULL COMMENT '本金',
    `interest`         VARCHAR(50) DEFAULT NULL COMMENT '利息',
    `lastAmount`       VARCHAR(50) DEFAULT NULL COMMENT '剩余本金',
    `change_state`     VARCHAR(50) DEFAULT NULL COMMENT '变更类型',
    `cico_isinvoice`   VARCHAR(50) DEFAULT NULL COMMENT '是否开票',
    `create_time`      datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_time`      datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间	',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '苍穹-请求记录表';
