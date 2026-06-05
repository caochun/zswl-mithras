CREATE TABLE `ecl_execute_client_promotion_result`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT,
    `contract_id` bigint(20) default null comment '合同id',
    `interval_month` int(11) DEFAULT NULL COMMENT '间隔',
    `conclusion` tinyint(2) DEFAULT 0 COMMENT '结果0不满足，1满足',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间-测算时间',
    `create_by`               bigint(20)          DEFAULT NULL COMMENT '创建人id',
    `update_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`               bigint(20)          DEFAULT NULL COMMENT '修改人id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='资产减值客户是否上迁记录表';
alter table ecl_execute_predict_record add column promotion_result tinyint(2) DEFAULT 0 COMMENT '结果0不满足，1满足';
alter table ecl_execute_predict_record add column promotion_result_handle tinyint(2) DEFAULT 0 COMMENT '手工结果0不满足，1满足';
