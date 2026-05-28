-- 管理报表-区域授信表
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, `report_type_name`, `report_source`, `report_key`)
VALUES
    ('区域授信表', 'http://10.158.11.178/page/aa6a784af3e0e47f8ad3128b', 'riskcontrol', 0, NULL, 'GUAN_YUAN', 'aa6a784af3e0e47f8ad3128b');

INSERT INTO `bifrost_system_config` (`config_key`, `config_value`, `created_by`, `updated_by`, `description`, `status`, `type`)
VALUES
    ('guanyuan.区域授信表', '[\"http://10.158.11.178/public-api/data-source/b208425750a004c4d93468da/refresh?token=l94099293ea524c57b127b44\",\"http://10.158.11.178/public-api/data-source/s039b83c62acc43028ef149c/refresh?token=q13ee2156c050404db60ec7b\",\"http://10.158.11.178/public-api/data-source/e7a2c32c4755d4516b6e0ea7/refresh?token=mc7a339d75d7e4b84bee47a4\"]', 'admin', 'admin', '区域授信表刷新', 1, 'Json');

update bifrost_system_config set config_value = '["项目跟踪表","业务情况表（新）","区域授信表"]' where config_key = 'guanyuanReportRefreshBtn';

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('collectionFlowCenterClientContractMargin', '查询客户下可用合同保证金', (select id from bifrost_menu where code = 'budgetFlowCenter'), 'POST',
        '/collection/flow/center/client/contract/margin', '2');
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES('dashboardAfterLeaseCheckStatistics', '业务工作台-租后管理-统计', (select id from bifrost_menu where code = 'dashboard'),
       'POST','/dashboard/after/lease/check/statistics', '2'),
      ('dashboardAfterLeaseCheckList', '业务工作台-租后管理-列表', (select id from bifrost_menu where code = 'dashboard'), 'POST',
       '/dashboard/after/lease/check/list', '2');


INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('processpreparedetailrepaylist', '还本付息计划确认', 0, 10, NULL, NULL, NULL, 'POST', '/process/prepare/detail/repay/list', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('repayActualwriteOffmodify', '还款核销修改', 0, 481, NULL, NULL, NULL, 'POST', '/repayActual/writeOff/modify', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('repayActualplanmodify', '还款计划修改', 0, 481, NULL, NULL, NULL, 'POST', '/repayActual/plan/modify', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('fundTransferbankAccountlist', '账户列表', 0, 481, NULL, NULL, NULL, 'POST', '/fundTransfer/bankAccount/list', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('fundTransfercurrentdaily', '监管户待转资金图表当日情况展示', 0, 481, NULL, NULL, NULL, 'POST', '/fundTransfer/current/daily', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('fundTransfergraphdaily', '监管户待转资金图表每日情况展示', 0, 481, NULL, NULL, NULL, 'POST', '/fundTransfer/graph/daily', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('fundTransferaccountdaily', '监管户待转资金列表每日情况展示', 0, 481, NULL, NULL, NULL, 'POST', '/fundTransfer/account/daily', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('fundTransferdetaillist', '监管户待转资金账户详情', 0, 481, NULL, NULL, NULL, 'POST', '/fundTransfer/detail/list', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('fundTransfergraphlist', '监管户待转资金图表展示', 0, 481, NULL, NULL, NULL, 'POST', '/fundTransfer/graph/list', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('fundTransferaccountlist', '监管户待转资金列表展示', 0, 481, NULL, NULL, NULL, 'POST', '/fundTransfer/account/list', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('fundTransferIndexDownload', '监管户待转资金导出', 0, 481, NULL, NULL, NULL, 'POST', '/index/download', 2, NULL);

UPDATE fund_direct_financing_repay_actual SET is_confirmed = 1 WHERE repay_date <= CURDATE();
UPDATE fund_direct_financing_repay_actual SET is_paid = 1 WHERE repay_date <= CURDATE();
UPDATE fund_financing_repay_actual SET is_confirmed = 1 WHERE repay_date <= CURDATE();
UPDATE fund_financing_repay_actual SET is_paid = 1 WHERE repay_date <= CURDATE();
UPDATE fund_financing_repay_actual_lib SET is_confirmed = 1 WHERE repay_date <= CURDATE();
UPDATE fund_financing_repay_actual_lib SET is_paid = 1 WHERE repay_date <= CURDATE();

-- 合同阶段账号增加
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('basedatacontractAccountlist', '乙方账户列表', 0, 21, NULL, NULL, NULL, 'POST', '/basedata/contractAccount/list', 2, NULL);

-- 项目评审增加评级信息校验
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('projreviewratingcheck', '项目评审-校验评级信息', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/rating/check', 1, NULL);
