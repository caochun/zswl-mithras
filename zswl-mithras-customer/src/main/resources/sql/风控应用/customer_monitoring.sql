INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('customerMonitoring', 1, 3, '0', '/customerMonitoring', null, null, '客户监控');
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '风控管理') a
         join (select id as menu_id from bifrost_menu where code = 'customerMonitoring') b;

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitorlist', '客户监控列表', 0, id, 'POST', '/clientMonitor/list', null
from bifrost_menu
where code = 'customerMonitoring';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitorstatistic', '客户监控统计', 0, id, 'POST', '/clientMonitor/statistic', null
from bifrost_menu
where code = 'customerMonitoring';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitorrisklinechart', '风险客户数量折线图', 0, id, 'POST', '/clientMonitor/risk/linechart', null
from bifrost_menu
where code = 'customerMonitoring';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitorriskpiechart', '客户监控饼图', 0, id, 'POST', '/clientMonitor/risk/piechart', null
from bifrost_menu
where code = 'customerMonitoring';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitordetailwarn', '客户监控预警详情', 0, id, 'POST', '/clientMonitor/detail/warn', null
from bifrost_menu
where code = 'customerMonitoring';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitordetailopinion', '客户监控舆情详情', 0, id, 'POST', '/clientMonitor/detail/opinion', null
from bifrost_menu
where code = 'customerMonitoring';
