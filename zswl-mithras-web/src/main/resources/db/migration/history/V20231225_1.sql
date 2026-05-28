CREATE TABLE `payment_actual_detail_unconfirmed` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `payment_id` bigint(20) DEFAULT NULL COMMENT '所属支付申请id',
  `seq_code` varchar(20) DEFAULT NULL COMMENT '序号',
  `contract_id` bigint(20) DEFAULT NULL COMMENT '所属合同id',
  `info_source` varchar(50) DEFAULT NULL COMMENT '同步还是录入，显示‘财务系统’或者录入者名字',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '付款方式',
  `paid_in_date` datetime DEFAULT NULL COMMENT '实付日期',
  `paid_in_amount` bigint(20) DEFAULT NULL COMMENT '实付金额',
  `postscript` varchar(1024) DEFAULT NULL COMMENT '附言',
  `our_account_id` bigint(20) DEFAULT NULL COMMENT '我方账户',
  `our_account_number` varchar(255) DEFAULT NULL COMMENT '我方账号',
  `our_account_name` varchar(255) DEFAULT NULL COMMENT '我方账号名',
  `our_account_bank` varchar(255) DEFAULT NULL COMMENT '我方账号开户行',
  `opposite_account_id` bigint(20) DEFAULT NULL COMMENT '对方账户',
  `opposite_account_number` varchar(255) DEFAULT NULL COMMENT '对方账号',
  `opposite_account_name` varchar(255) DEFAULT NULL COMMENT '对方账号名',
  `opposite_account_bank` varchar(255) DEFAULT NULL COMMENT '对方账号开户行',
  `write_off_status` varchar(20) DEFAULT NULL COMMENT '核销状态',
  `cancel_write_off_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '反核销标识 0正常，1反核销',
  `cancel_write_off_id` bigint(20) DEFAULT NULL COMMENT '反核销对应ID',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `flow_id` varchar(40) DEFAULT NULL COMMENT '流水id',
  `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
  PRIMARY KEY (`id`),
  KEY `idx_payment_id` (`payment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实际付款记录表（未确认）';

alter table `payment_actual_detail` add column `operation_date` date DEFAULT NULL COMMENT '操作日期' after `paid_in_amount`;

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('paymentactualdetailmodify', '编辑未确认付款核销', 0, 16, NULL, NULL, NULL, 'POST', '/payment/actualdetail/modify', 2, null),
	('paymentfinish', '付款申请-结束投放', 0, 15, NULL, NULL, NULL, 'POST', '/payment/finish', 2, null),
	('paymentactualdetailsubmit', '未确认付款核销提交审批', 0, 16, NULL, NULL, NULL, 'POST', '/payment/actualdetail/submit', 2, null),
	('paymentactualdetailadd', '新增未确认付款核销', 0, 16, NULL, NULL, NULL, 'POST', '/payment/actualdetail/add', 2, null);
