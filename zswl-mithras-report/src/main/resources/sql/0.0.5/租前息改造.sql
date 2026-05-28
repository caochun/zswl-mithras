CREATE TABLE `account_report_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',
    `receipt_id` bigint(20) DEFAULT NULL COMMENT '借据id',
    `payment_id` bigint(20) DEFAULT NULL COMMENT '付款id',
    PRIMARY KEY (`id`),
    KEY idx_receipt_id(`receipt_id`),
    KEY idx_payment_id(`payment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='征信报送-账户表报送辅助';