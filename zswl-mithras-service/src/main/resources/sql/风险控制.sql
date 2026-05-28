INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('clientUnifiedViewList', '获取的客户列表', (select id from bifrost_menu where code = 'customerView'), 'POST', '/client/unified/view/list', '2'),
       ('clientUnifiedViewDetail', '获取的客户详情', (select id from bifrost_menu where code = 'customerView'), 'POST', '/client/unified/view/detail', '2'),
       ('clientUnifiedViewApplyCredit', '获取客户授信信息', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/apply/credit', '2'),
       ('clientUnifiedViewApplyCreditHistory', '获取客户授信历史', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/apply/credit/history', '2'),
       ('clientUnifiedViewProjList', '获取客户项目列表', (select id from bifrost_menu where code = 'customerView'), 'POST', '/client/unified/view/proj/list',
        '2'),
       ('clientUnifiedViewContractList', '获取客户合同列表', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/contract/list', '2'),
       ('clientUnifiedViewCustomerTrends', '获取客户统一折线图', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/customer/trends', '2'),
       ('clientUnifiedViewRatingHistory', '获取客户历史评级', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/rating/history', '2');

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('riskWarnMonitorQuantityChange', '监控预警-风险数量变化', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/risk/warn/monitor/quantity/change', '2'),
       ('riskWarnMonitorOpinionList', '监控预警-舆情列表', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/risk/warn/monitor/opinion/list', '2'),
       ('riskWarnMonitorStatistics', '监控预警-统计', (select id from bifrost_menu where code = 'monitorEarly'), 'POST', '/risk/warn/monitor/statistics',
        '2'),
       ('riskWarnMonitorTypeChange', '监控预警-风险类型占比', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/risk/warn/monitor/type/change', '2'),
       ('riskControlBlackGrayBaseInfoLibrary', '监控预警-查询客户黑灰名单', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/black/gray/base/info/library', '2'),
       ('opinionWarnOrgSelect', '监控预警-查询部门', (select id from bifrost_menu where code = 'monitorEarly'), 'GET',
        '/select/orgs', '2'),
       ('riskWarnMonitorWarnList', '监控预警-预警列表', (select id from bifrost_menu where code = 'monitorEarly'), 'POST', '/risk/warn/monitor/warn/list',
        '2');

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('riskWarnMonitorWarnDetail', '监控预警-预警列表', (select id from bifrost_menu where code = 'riskpublicMonitor'), 'POST', '/risk/warn/monitor/warn/detail','2');


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('dashboardprojectstagestatisticsUnified', '项目视图-项目阶段-统计', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/dashboard/project/stage/statistics', '2'),
       ('RiskControlSelectOrgs', '监控预警-查询部门1', (select id from bifrost_menu where code = 'monitorEarly'), 'GET','/select/orgs', '2');


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('riskWarnFileList', '监控预警-文件', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/file/list', '2'),
       ('riskWarnFileUpload', '监控预警-文件上传', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/file/upload', '2'),
       ('riskWarnFileBatchRemove', '监控预警-文件删除', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/file/batch/remove', '2'),
       ('riskWarnFileDownload', '监控预警-文件下载', (select id from bifrost_menu where code = 'monitorEarly'), 'GET',
        '/file/download', '2');

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('clientUnifiedViewOverdueRent', '获取客户逾期租金', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/overdue/rent', '2');


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('riskWarnMonitorModify', '监控预警-预警信息修改', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/risk/warn/monitor/modify', '2');

ALTER TABLE risk_control_opinion_monitor
    ADD handle_result tinyint(1) DEFAULT NULL COMMENT '处置方式 0 处理， 1 关闭';
ALTER TABLE risk_control_warn_monitor
    ADD handle_result tinyint(1) DEFAULT NULL COMMENT '处置方式 0 处理， 1 关闭';


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('clientMonitorOpinionList', '客户监控舆情列表', (select id from bifrost_menu where code = 'customerMonitoring'), 'POST',
        '/clientMonitor/opinion/list', '2'),
       ('clientMonitorWarnList', '客户监控预警列表', (select id from bifrost_menu where code = 'customerMonitoring'), 'POST',
        '/clientMonitor/warn/list', '2');

