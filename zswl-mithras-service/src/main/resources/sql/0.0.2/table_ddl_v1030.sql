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

CREATE TABLE `contract_prepayment_lib`
(
    `id`                       bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id	',
    `contract_id`              bigint(20) NOT NULL COMMENT '所属合同ID',
    `applay_repayment_date`    datetime DEFAULT NULL COMMENT '申请还款日期',
    `unpaid_rent_due`          bigint(20) NOT NULL COMMENT '到期未付租金',
    `penalty`                  bigint(20) DEFAULT NULL COMMENT '违约金',
    `early_repayment`          bigint(20) NOT NULL COMMENT '提前归还本金',
    `early_repayment_interest` bigint(20) DEFAULT NULL COMMENT '提前归还利息',
    `loss`                     bigint(20) DEFAULT NULL COMMENT '提前终止补偿金',
    `unpaid_rent`              bigint(20) DEFAULT NULL COMMENT '到期未付租金',
    `create_by`                bigint(20) DEFAULT NULL COMMENT '创建人id、发起人id	',
    `create_time`              datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间。默认当前时间',
    `update_by`                bigint(20) DEFAULT NULL COMMENT '最后更新人id	',
    `update_time`              datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间；每次记录变化，自动更新为当前时间	',
    `nominal_price`            bigint(20) DEFAULT NULL COMMENT '名义价款',
    `version`                  varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`                bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time`         datetime DEFAULT NULL,
    `data_create_by`           bigint(20) DEFAULT NULL,
    `data_update_time`         datetime DEFAULT NULL,
    `data_update_by`           bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COMMENT='合同-提前还款版本表';

alter table payment_actual_detail add flow_id VARCHAR(40) DEFAULT NULL COMMENT  '流水id';

alter table margin_record_info add flow_id VARCHAR(40) DEFAULT NULL COMMENT  '流水id';

alter table margin_record_info add invoice_flag TINYINT DEFAULT NULL COMMENT  '是否开票，0不开 1开';

alter table collection_record_info add flow_id VARCHAR(40) DEFAULT NULL COMMENT  '流水id';

alter table collection_record_info add invoice_flag TINYINT DEFAULT NULL COMMENT  '是否开票，0不开 1开';

alter table contract_prepayment add nominal_price  bigint(20) DEFAULT NULL COMMENT  '名义价款';

alter table contract_prepayment add nominal_price_date datetime DEFAULT NULL COMMENT  '名义货价还款时间';

alter table contract_prepayment_lib add nominal_price_date datetime DEFAULT NULL COMMENT  '名义货价还款时间';

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

