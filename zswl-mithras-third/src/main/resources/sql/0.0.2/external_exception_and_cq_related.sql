drop table if EXISTS external_exception_info;
CREATE TABLE `external_exception_info`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `biz_info`   json              DEFAULT NULL COMMENT '业务数据',
    `biz_model`  varchar(50)       DEFAULT NULL COMMENT '业务模块',
    `biz_key`    varchar(50)       DEFAULT NULL COMMENT '业务标识',
    `exception`  varchar(255)      DEFAULT NULL COMMENT '错误信息',
    `status`     int(2) unsigned zerofill DEFAULT NULL COMMENT '状态 0失败',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `retry_count`  int(11)      DEFAULT 0 COMMENT '重试次数',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外部接口异常信息表';
CREATE TABLE `cq_related_mithras`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '方案id',
    `billno`          varchar(50) DEFAULT NULL COMMENT '苍穹付款申请单单号',
    `collection_code` varchar(50) DEFAULT NULL COMMENT '收付款申请编号',
    `record_source`   varchar(30) DEFAULT NULL COMMENT '收付款类型',
    `create_by`       bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id',
    `create_time`     datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by`       bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`     datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COMMENT='苍穹-关联租赁表'
