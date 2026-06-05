CREATE TABLE `collection_overdue_history`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id	',
    `contract_id`     bigint(20) DEFAULT NULL COMMENT '合同id',
    `contract_code`   varchar(100) DEFAULT NULL COMMENT '合同编号',
    `receipt_id`      bigint(20) DEFAULT NULL COMMENT '借据ID',
    `collection_id`   bigint(20) DEFAULT NULL COMMENT '收款明细id	',
    `collection_code` varchar(100) DEFAULT NULL COMMENT '收款编号',
    `phase`           int(11) DEFAULT NULL COMMENT '期项',
    `overdue_amount`  bigint(20) DEFAULT NULL COMMENT '逾期金额',
    `overdue_days`    int(11) DEFAULT NULL COMMENT '逾期天数',
    `create_by`       bigint(20) DEFAULT NULL,
    `create_time`     datetime     DEFAULT CURRENT_TIMESTAMP,
    `update_by`       bigint(20) DEFAULT NULL,
    `update_time`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX             `collection_overdue_contract_id` (`contract_id`),
    INDEX             `collection_overdue_receipt_id` (`receipt_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='逾期历史表';
