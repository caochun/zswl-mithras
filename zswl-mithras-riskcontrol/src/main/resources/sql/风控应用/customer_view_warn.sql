INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('riskWarnMonitorWarnListCustomerView', '监控预警-预警列表-统一客户视图', 0, (select id from bifrost_menu where code = 'customerView'), null, null, null, 'POST',
        '/risk/warn/monitor/warn/list', 2, null);
