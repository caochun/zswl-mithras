

INSERT INTO bifrost_custom_tree (code, name, flag, sort_no) VALUES ('rzy', '融资租赁', 1, 0);
update bifrost_custom_tree as a,  bifrost_custom_tree as b set a.parent_id = b.id where a.code != 'rzy' and b.code = 'rzy';

INSERT INTO bifrost_custom_tree (code, name, flag, sort_no) VALUES ('riskcontrol', '数据风控', 1, 0);
INSERT INTO bifrost_custom_tree (code, name, flag, sort_no, icon) VALUES ('blackListManage', '黑灰名单', 1, null, 'icon-heihuimingdanguanli');
update bifrost_custom_tree as a,  bifrost_custom_tree as b set a.parent_id = b.id where a.code = 'blackListManage' and b.code = 'riskcontrol';

INSERT INTO bifrost_custom_tree (code, name, flag, sort_no) VALUES ('warehouse', '入库管理', 1, null);
update bifrost_custom_tree as a,  bifrost_custom_tree as b set a.parent_id = b.id where a.code = 'warehouse' and b.code = 'blackListManage';

INSERT INTO bifrost_custom_tree (code, name, flag, sort_no) VALUES ('outbound', '出库管理', 1, null);
update bifrost_custom_tree as a,  bifrost_custom_tree as b set a.parent_id = b.id where a.code = 'outbound' and b.code = 'blackListManage';

INSERT INTO bifrost_custom_tree (code, name, flag, sort_no) VALUES ('zhquery', '综合查询', 1, null);
update bifrost_custom_tree as a,  bifrost_custom_tree as b set a.parent_id = b.id where a.code = 'zhquery' and b.code = 'blackListManage';

INSERT INTO bifrost_custom_tree (code, name, flag, sort_no) VALUES ('bizParam', '业务参数', 1, null);
update bifrost_custom_tree as a,  bifrost_custom_tree as b set a.parent_id = b.id where a.code = 'bizParam' and b.code = 'blackListManage';




INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManageparameterswarehouse', 1, 0, '0', '/blackListManage/parameters/warehouse', null, null, '入库规则配置');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManagequeryallQuery', 1, 0, '0', '/blackListManage/query/allQuery', null, null, '全量名单查询');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManagequeryrecognize', 1, 0, '0', '/blackListManage/query/recognize', null, null, '名单识别');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManageoutboundsearch', 1, 0, '0', '/blackListManage/outbound/search', null, null, '查询');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManageoutboundapplication', 1, 0, '0', '/blackListManage/outbound/application', null, null, '出库');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManagewarehousesearch', 1, 0, '0', '/blackListManage/warehouse/search', null, null, '查询');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManagewarehousesubTask', 1, 0, '0', '/blackListManage/warehouse/subTask', null, null, '子任务报送');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManagewarehousemainTask', 1, 0, '0', '/blackListManage/warehouse/mainTask', null, null, '主任务报送');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('monitorEarly', 1, 0, '0', '/monitorEarly', null, 'icon-yujingguanli', '监控预警');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('customerView', 1, 10, '0', '/customerView', null, 'icon-kehufengxianzongheshitu', '客户统一视图');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManageWarehouse', 3, 0, null, '/customerView/blackListManage/warehouse', null, null, '入库管理');


INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '数据风控') a
         join (select id as menu_id from bifrost_menu where code = 'customerView') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '数据风控') a
         join (select id as menu_id from bifrost_menu where code = 'monitorEarly') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '入库管理') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManagewarehousesearch') b;
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '入库管理') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManagewarehousemainTask') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '入库管理') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManagewarehousesubTask') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '出库管理') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManageoutboundapplication') b;
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '出库管理') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManageoutboundsearch') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '综合查询') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManagequeryrecognize') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '综合查询') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManagequeryallQuery') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '业务参数') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManageparameterswarehouse') b;

CREATE DATABASE providence CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

## 逾期名单查询
INSERT INTO bifrost_menu (gmt_create, gmt_modified, code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by, update_by) VALUES ('2024-12-26 16:17:10', '2024-12-26 16:17:10', 'overdueListSearch', 1, 10, '0', ' /overdueListSearch', null, 'icon-cuishou', '上海票交所逾期名单', null, '_self', null, null);

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '数据风控') a
         join (select id as menu_id from bifrost_menu where code = 'overdueListSearch') b;

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueList', '票据逾期名单查询', 0, id, 'post', '/overdueList/list', null
from bifrost_menu
where code = 'overdueListSearch';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueListImport', '票据逾期名单导入', 0, id, 'post', '/overdueList/import', null
from bifrost_menu
where code = 'overdueListSearch';









#################### 上线sql

-- 菜单创建
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('customerView', 1, 5, '0', '/customerView', null, null, '统一视图');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManagequeryallQuery', 1, 15, '0', '/blackListManage/query/allQuery', null, null, '灰黑名单全量查询');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManagequeryrecognize', 1, 20, '0', '/blackListManage/query/recognize', null, null, '灰黑名单企业识别');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('overdueListSearch', 1, 25, '0', ' /overdueListSearch', null, null, '上海票交所逾期名单');

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '客户管理') a
         join (select id as menu_id from bifrost_menu where code = 'customerView') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '客户管理') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManagequeryallQuery') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '客户管理') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManagequeryrecognize') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '客户管理') a
         join (select id as menu_id from bifrost_menu where code = 'overdueListSearch') b;


INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('monitorEarly', 1, 5, '0', '/monitorEarly', null, null, '预警监测');
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '风控管理') a
         join (select id as menu_id from bifrost_menu where code = 'monitorEarly') b;

--
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueList', '上海票交所逾期名单', 0, id, 'post', '/overdueList/list', null
from bifrost_menu
where code = 'overdueListSearch';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueListImport', '上海票交所逾期名单', 0, id, 'post', '/overdueList/import', null
from bifrost_menu
where code = 'overdueListSearch';