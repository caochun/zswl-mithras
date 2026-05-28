/*
CREATE TABLE `flow_query_extra`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `flow_key`      varchar(50) DEFAULT NULL,
    `instance_id`   varchar(20) DEFAULT NULL,
    `biz_id`        bigint(20) DEFAULT NULL,
    `client_name`   varchar(50) DEFAULT NULL,
    `proj_name`     varchar(50) DEFAULT NULL,
    `proj_code`     varchar(20) DEFAULT NULL,
    `contract_code` varchar(50) DEFAULT NULL,
    `create_time`   datetime    DEFAULT CURRENT_TIMESTAMP,
    `update_time`   datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`     bigint(20) DEFAULT NULL,
    `update_by`     bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批流查询附加表';

ALTER TABLE new_after_lease_check_plan_client
    ADD check_time DATE NULL COMMENT '检查时间';
ALTER TABLE new_after_lease_check_plan_client_lib
    ADD check_time DATE NULL COMMENT '检查时间';

ALTER TABLE new_after_lease_check_plan_client
    ADD is_notify bit(1) DEFAULT b'0' COMMENT '是否已通知';
ALTER TABLE new_after_lease_check_plan_client_lib
    ADD is_notify bit(1) DEFAULT b'0' COMMENT '是否已通知';

alter table new_after_lease_check_report_base drop column check_date;
alter table new_after_lease_check_report_base_lib drop column check_date;

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
VALUES ('afterleasecheckplanquarterprojectmodify', '季度客户计划变更', 0, 38, 'POST',
        '/afterlease/checkplan/quarter/project/modify', 2);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
VALUES ('funddirectfinancingbaseinfosum', '直接融资-详情信息合计行', 0, 490, 'POST',
        '/fund/direct/financing/base/info/sum', 1);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
VALUES ('riskcontrolopinionmonitorview', '风控舆情-查看处理', 0, 398, 'POST', '/risk/control/opinion/monitor/view', 1);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
VALUES ('newftpmonthlydeductionrefresh', '刷新月度计价指导', 0, 484, 'POST', '/new/ftp/monthly/deduction/refresh', 2);
*/