INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('dashboardOperationTodoStatistics', '运营视图-待办统计', 0, 730, null, null, null, 'POST', '/dashboard/operation/todo/statistics', 1, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('dashboardOperationApprovalStatistics', '业务工作台-项目视图-计划执行情况-运营审批时效统计', 0, 730, null, null, null, 'POST', '/dashboard/operation/approval/statistics', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('dashboardOperationApprovalList', '业务工作台-合同审批时效及退回情况-运营审批时效', 0, 730, null, null, null, 'POST', '/dashboard/operation/approval/list', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('dashboardOperationContractReturnList', '业务工作台-合同审批时效及退回情况-合同退回列表', 0, 730, null, null, null, 'POST', '/dashboard/operation/contract/return/list', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('dashboardOperationContractReturnStatistics', '业务工作台-项目视图-计划执行情况-合同退回统计', 0, 730, null, null, null, 'POST', '/dashboard/operation/contract/return/statistics', 2, null);
CREATE TABLE `netting_refund` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `bank_detail_no` varchar(64) DEFAULT NULL COMMENT '银行流水编号,由于历史原因,不指向finance_flow_record的该字段,而是billno',
                                  `finance_flow_id` bigint(20) DEFAULT NULL COMMENT '银行流水记录的ID',
                                  `netting_amount` bigint(20) DEFAULT NULL COMMENT '轧差退款金额',
                                  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `deleted` tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='资金流水轧差退款表';


INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('bankCenterReceiptCode', '合同借据编号', 0, 495, NULL, NULL, NULL, 'POST', '/bank/center/receiptCode', 2, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('bankCenterConfirmIncome', '确认收入', 0, 495, NULL, NULL, NULL, 'POST', '/bank/center/confirm/income', 2, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('bankCenterNettingRefund', '轧差退款', 0, 495, NULL, NULL, NULL, 'POST', '/bank/center/netting/refund', 2, NULL);