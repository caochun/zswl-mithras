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
alter table contract_prepayment add nominal_price  bigint(20) DEFAULT NULL COMMENT  '名义价款';

alter table contract_prepayment add nominal_price_date datetime DEFAULT NULL COMMENT  '名义货价还款时间';

alter table contract_prepayment_lib add nominal_price_date datetime DEFAULT NULL COMMENT  '名义货价还款时间';
