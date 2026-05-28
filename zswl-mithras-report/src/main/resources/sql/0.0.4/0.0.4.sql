# 生效区表加字段方便处理
ALTER TABLE cr_account ADD `payment_id` bigint(20) DEFAULT NULL COMMENT '付款id' AFTER `contract_id`;
ALTER TABLE cr_account ADD `client_id` bigint(20) DEFAULT NULL COMMENT '客户表id' AFTER `contract_id`;
ALTER TABLE cr_account ADD `client_name` varchar(50) DEFAULT NULL COMMENT '客户名称' AFTER `contract_id`;
ALTER TABLE cr_actual_repay ADD `payment_id` bigint(20) DEFAULT NULL COMMENT '付款id' AFTER `contract_id`;
ALTER TABLE cr_five_class ADD `payment_id` bigint(20) DEFAULT NULL COMMENT '付款id' AFTER `contract_id`;
ALTER TABLE cr_guarantor ADD `payment_id` bigint(20) DEFAULT NULL COMMENT '付款id' AFTER `contract_id`;
ALTER TABLE cr_mortgage ADD `payment_id` bigint(20) DEFAULT NULL COMMENT '付款id' AFTER `contract_id`;
ALTER TABLE cr_pledge ADD `payment_id` bigint(20) DEFAULT NULL COMMENT '付款id' AFTER `contract_id`;
ALTER TABLE cr_repay_plan ADD `payment_id` bigint(20) DEFAULT NULL COMMENT '付款id' AFTER `contract_id`;
ALTER TABLE cr_special_trade ADD `payment_id` bigint(20) DEFAULT NULL COMMENT '付款id' AFTER `contract_id`;

# 生效区表加索引
ALTER TABLE cr_account ADD INDEX `idx_payment_id`(`payment_id`);
ALTER TABLE cr_account ADD INDEX `idx_client_id`(`client_id`);
ALTER TABLE cr_account ADD INDEX `idx_client_name`(`client_name`);
ALTER TABLE cr_actual_repay ADD INDEX `idx_payment_id`(`payment_id`);
ALTER TABLE cr_five_class ADD INDEX `idx_payment_id`(`payment_id`);
ALTER TABLE cr_guarantor ADD INDEX `idx_payment_id`(`payment_id`);
ALTER TABLE cr_mortgage ADD INDEX `idx_payment_id`(`payment_id`);
ALTER TABLE cr_pledge ADD INDEX `idx_payment_id`(`payment_id`);
ALTER TABLE cr_repay_plan ADD INDEX `idx_payment_id`(`payment_id`);
ALTER TABLE cr_special_trade ADD INDEX `idx_payment_id`(`payment_id`);
ALTER TABLE cr_overdue_record ADD INDEX `idx_payment_id`(`payment_id`);

# 批次表
CREATE TABLE `batch_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `batch_no` varchar(20) DEFAULT NULL COMMENT '批次号',
    `batch_seq` int(11) DEFAULT NULL COMMENT '报送序号 每日重置01开始',
    `type` varchar(20) DEFAULT NULL COMMENT '批次类型，审批批次、全量批次',
    `report_time` datetime DEFAULT NULL COMMENT '批次类型，审批批次、全量批次',
    `reportor_id` bigint(20) DEFAULT NULL COMMENT '报送员id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `process_instance_id` varchar(64) DEFAULT NULL COMMENT '流程id',

    PRIMARY KEY (`id`),
    KEY idx_batch_no(`batch_no`),
    KEY idx_report_time(`report_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-批次表';