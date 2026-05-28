

-- 客户监控

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('clientMonitorOpinionList', '客户监控舆情列表', (select id from bifrost_menu where code = 'customerMonitoring'), 'POST',
        '/clientMonitor/opinion/list', '2'),
       ('clientMonitorWarnList', '客户监控预警列表', (select id from bifrost_menu where code = 'customerMonitoring'), 'POST',
        '/clientMonitor/warn/list', '2');


alter table margin_base_info add column total_receivable_amount BIGINT(20) default null comment '累加应收金额';

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('collectionFlowCenterRecycleMarginPlan', '获取保证金回收计划', (select id from bifrost_menu where code = 'budgetFlowCenter'), 'POST',
        '/collection/flow/center/recycle/margin/plan', '2');


