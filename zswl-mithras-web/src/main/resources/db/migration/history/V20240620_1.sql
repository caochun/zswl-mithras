alter table fund_financing_early_settle_plan
    add reason varchar(1024) null comment '融资结清原因';
alter table fund_financing_early_settle_plan_lib
    add reason varchar(1024) null comment '融资结清原因';

alter table fund_financing_early_settle_plan
    add last_principal bigint null comment '剩余本金';
alter table fund_financing_early_settle_plan_lib
    add last_principal bigint null comment '剩余本金';

CREATE TABLE `br_flow_record`
(
    `id`                    bigint(11) NOT NULL AUTO_INCREMENT,
    `rn`                    int(11) DEFAULT NULL COMMENT '序号',
    `bruid`                 varchar(255) DEFAULT NULL COMMENT '保融流水ID',
    `org_name`              varchar(255) DEFAULT NULL COMMENT '客户名称',
    `accountnumber`         varchar(255) DEFAULT NULL COMMENT '账户编码',
    `transseq`              varchar(255) DEFAULT NULL COMMENT '唯一标识',
    `tradedatetime`         date         DEFAULT NULL COMMENT '交易日期时间',
    `tradedate`             date         DEFAULT NULL COMMENT '交易日期',
    `tradetime`             date         DEFAULT NULL COMMENT '交易时间',
    `qixiriqi`              date         DEFAULT NULL COMMENT '起息日期',
    `moneyway`              varchar(50)  DEFAULT NULL COMMENT '交易方向1支出，2收入',
    `amount`                bigint(11) DEFAULT NULL COMMENT '交易金额(毫厘)',
    `currentbalance`        bigint(11) DEFAULT NULL COMMENT '当前余额（毫厘）',
    `lastmodifiedon`        varchar(255) DEFAULT NULL COMMENT '更新日期时间',
    `checkcode`             varchar(255) DEFAULT NULL COMMENT '对账码',
    `purpose`               varchar(255) DEFAULT NULL COMMENT '用途',
    `comments`              text COMMENT '备注',
    `oppositeaccountnumber` varchar(255) DEFAULT NULL COMMENT '对方账号',
    `oppositeaccountname`   varchar(255) DEFAULT NULL COMMENT '对方户名',
    `oppositebank`          varchar(255) DEFAULT NULL COMMENT '对方银行',
    `billcode`              varchar(50)  DEFAULT NULL COMMENT '票据号',
    `billtype`              varchar(50)  DEFAULT NULL COMMENT '票据类型',
    `checkbatchno`          varchar(255) DEFAULT NULL COMMENT '核对批号',
    `bankserialnumber`      varchar(255) DEFAULT NULL COMMENT '银行流水号',
    `notecode`              varchar(255) DEFAULT NULL COMMENT '资金系统单据号',
    `bankbusref`            varchar(255) DEFAULT NULL COMMENT '银行业务参考号',
    `receiptcode`           varchar(255) DEFAULT NULL COMMENT '电子回单编号',
    `receiptbustypno`       varchar(255) DEFAULT NULL COMMENT '业务回单类型',
    `receiptinfo`           text COMMENT '回单个性化信息',
    `busref`                varchar(255) DEFAULT NULL COMMENT '企业业务参考号',
    `deleted`               int(11) DEFAULT '0' COMMENT '0：未删除，1：已删除，默认0',
    `ignore_flag`           int(11) DEFAULT '0' COMMENT '是否删除，0：未忽略，1：已忽略，默认0',
    `create_by`             bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`           datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`             bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`           datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    KEY                     `br_flow_record_bruid` (`bruid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='保融流水表';



INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`) VALUES ('brFlowRecordCount', '保融流水表统计', 0, 495, NULL, NULL, NULL, 'POST', '/br/flow/record/count', 2);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`) VALUES ('brFlowRecordIgnore', '忽略保融流水表', 0, 495, NULL, NULL, NULL, 'POST', '/br/flow/record/ignore', 2);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`) VALUES ('brFlowRecordRemove', '删除保融流水表', 0, 495, NULL, NULL, NULL, 'POST', '/br/flow/record/remove', 2);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`) VALUES ('brFlowRecordList', '保融流水表列表', 0, 495, NULL, NULL, NULL, 'POST', '/br/flow/record/list', 2);


alter table corp_commerce_info
    add `is_market` tinyint(1) DEFAULT '0' COMMENT '是否在沪深主板、中小板、创业板上市';
alter table corp_commerce_info_lib
    add `is_market` tinyint(1) DEFAULT '0' COMMENT '是否在沪深主板、中小板、创业板上市';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `en_name`, `method`, `path`, `type`)
VALUES
	('dashboardprojectstagestatistics', '项目视图-项目阶段-统计', 0, 730, NULL, 'POST', '/dashboard/project/stage/statistics', 1),
	('dashboardprojectstageprojestablishlist', '项目视图-项目阶段-立项阶段', 0, 730, NULL, 'POST', '/dashboard/project/stage/projestablish/list', 1),
	('dashboardprojectstageprojreviewlist', '项目视图-项目阶段-评审阶段', 0, 730, NULL, 'POST', '/dashboard/project/stage/projreview/list', 1),
	('dashboardprojectstageprojreviewnocontractlist', '项目视图-项目阶段-评审通过未创建合同阶段', 0, 730, NULL, 'POST', '/dashboard/project/stage/projreview/nocontract/list', 1),
	('dashboardprojectstageprojreviewlegalreportlist', '项目视图-项目阶段-待出具合规意见', 0, 730, NULL, 'POST', '/dashboard/project/stage/projreview/legalreport/list', 1),
	('dashboardprojectstagecontractlist', '项目视图-项目阶段-签约阶段', 0, 730, NULL, 'POST', '/dashboard/project/stage/contract/list', 1),
	('dashboardprojectstagepreparepaymentlist', '项目视图-项目阶段-付款阶段', 0, 730, NULL, 'POST', '/dashboard/project/stage/preparepayment/list', 1),
	('dashboardprojectstagepaymentlist', '项目视图-项目阶段-投放阶段', 0, 730, NULL, 'POST', '/dashboard/project/stage/payment/list', 1),
	('dashboardprojectstagerepaymentlist', '项目视图-项目阶段-还款阶段', 0, 730, NULL, 'POST', '/dashboard/project/stage/repayment/list', 1),
	('clientAfterleaseStatistics', '客户视图-租后管理-统计', 0, 730, NULL, 'POST', '/dashboard/client/afterlease/statistics', 2),
	('clientAfterleaseCheckList', '客户视图-租后管理-租后检查', 0, 730, NULL, 'POST', '/dashboard/client/afterlease/check/list', 2),
	('dashboardprojectinfosettleinthreemonthlist', '项目视图-项目信息-3个月内结清项目', 0, 730, NULL, 'POST', '/dashboard/project/info/settleinthreemonth/list', 1),
	('dashboardprojectinfooverduelist', '项目视图-项目信息-存在逾期项目', 0, 730, NULL, 'POST', '/dashboard/project/info/overdue/list', 1),
	('dashboardprojectinforentthismonthlist', '项目视图-项目信息-本月应收租金', 0, 730, NULL, 'POST', '/dashboard/project/info/rentthismonth/list', 1),
	('dashboardprojectinfostatistics', '项目视图-项目信息-统计', 0, 730, NULL, 'POST', '/dashboard/project/info/statistics', 1),
	('dashboardWorkbenchClientList', '通用查询-客户信息', 0, 730, NULL, 'POST', '/client/list', 1),
	('dashboardWorkbenchSelectOrgs', '通用查询-部门信息', 0, 730, NULL, 'GET', '/select/orgs', 1),
	('dashboardWorkbenchSelectFounder', '通用查询-用户信息', 0, 730, NULL, 'POST', '/select/founder', 1),
	('dashboardClientOverviewOverdueList', '客户视图-客户一览-逾期客户明细', 0, 730, NULL, 'POST', '/dashboard/client/overview/overdue/list', 2),
	('dashboardClientOverviewSettledList', '客户视图-客户一览-已结清客户明细', 0, 730, NULL, 'POST', '/dashboard/client/overview/settled/list', 2),
	('dashboardClientOverviewAllList', '客户视图-客户一览-所有客户明细', 0, 730, NULL, 'POST', '/dashboard/client/overview/all/list', 2),
	('dashboardClientOverviewSurvivalList', '客户视图-客户一览-存续客户明细', 0, 730, NULL, 'POST', '/dashboard/client/overview/survival/list', 2),
	('dashboardClientOverviewSettleinthreemonthList', '客户视图-客户一览-3个月内结清客户明细', 0, 730, NULL, 'POST', '/dashboard/client/overview/settleinthreemonth/list', 2),
	('dashboardClientOverviewStatistics', '客户视图-客户一览-统计', 0, 730, NULL, 'POST', '/dashboard/client/overview/statistics', 2),
	('dashboardtodolist', '统一视图-待办', 0, 730, NULL, 'POST', '/dashboard/todo/list', 1),
	('dashboardtodomyprocessapply', '统一视图-我发起的', 0, 730, NULL, 'POST', '/dashboard/todo/myprocess/apply', 2),
	('dashboardtodomyprocessdoing', '统一视图-在办', 0, 730, NULL, 'POST', '/dashboard/todo/myprocess/doing', 2),
	('dashboardtodomyprocessfinish', '统一视图-已办', 0, 730, NULL, 'POST', '/dashboard/todo/myprocess/finish', 2),
	('dashboardtodocount', '统一视图-数量统计', 0, 730, NULL, 'GET', '/dashboard/todo/count', 2);