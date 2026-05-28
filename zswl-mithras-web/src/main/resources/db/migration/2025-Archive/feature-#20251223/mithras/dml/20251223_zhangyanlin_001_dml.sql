-- 新增投放资产页面
-- select * from bifrost_menu where code = 'financialproperty' and path = '/financial/property';
delete from bifrost_menu where code = 'financialproperty' and path = '/financial/property';
INSERT INTO bifrost_menu
(gmt_create, gmt_modified, code, `level`, sort_no, `type`, `path`, parent_id, icon, name, en_name, target, create_by, update_by)
VALUES('2025-12-15 11:28:32', '2025-12-15 11:28:32', 'financialproperty', 3, 5, '0', '/financial/property', NULL, NULL, '投放资产',  NULL, '_self', NULL, NULL);



-- 资金管理下加投放资产菜单
-- select * from bifrost_custom_tree_menu_ref where menu_id in (select id from bifrost_menu where code = 'financialproperty' and path = '/financial/property');
delete from bifrost_custom_tree_menu_ref where menu_id in (select id from bifrost_menu where code = 'financialproperty' and path = '/financial/property');
INSERT INTO bifrost_custom_tree_menu_ref
(gmt_create, gmt_modified, create_by, update_by, custom_tree_id, menu_id, sort_no)
VALUES('2025-12-15 19:15:32', '2025-12-15 19:15:32', NULL, NULL, 38, (select id from bifrost_menu where code = 'financialproperty' and path = '/financial/property'), 0);



-- 新加接口 合同收付款，发送支付通知书按钮接口权限、直接融资详情-投放资产列表、间接融资详情-投放资产列表、资金管理模块下-投放资产列表、资金管理模块下-投放资产列表-导出
-- select * from bifrost_function where path in ('/contractcp/list/pushRentNotify','/direct/financing/property/list','/fund/financing/property/list','/financing/property/list','/financing/property/list/download');
delete from bifrost_function where path in ('/contractcp/list/pushRentNotify','/direct/financing/property/list','/fund/financing/property/list','/financing/property/list','/financing/property/list/download');
INSERT INTO bifrost_function
(gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id)
VALUES('2025-12-08 20:18:30', '2025-12-11 20:02:22', 'contractcplistpushRentNotify', '合同收付款-发送租金支付通知书', 0, 19, NULL, NULL, NULL, 'POST', '/contractcp/list/pushRentNotify', 1, 16);
INSERT INTO bifrost_function
(gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id)
VALUES('2025-12-16 10:48:09', '2025-12-16 10:49:52', 'directfinancingpropertylist', '直接融资-投放资产列表', 0, 490, NULL, NULL, NULL, 'POST', '/direct/financing/property/list', 1, 150);
INSERT INTO bifrost_function
(gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id)
VALUES('2025-12-16 20:09:37', '2025-12-16 10:49:43', 'fundfinancingpropertylist', '间接融资-投放资产列表', 0, 358, NULL, NULL, NULL, 'POST', '/fund/financing/property/list', 1, 45);
INSERT INTO bifrost_function
(gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id)
VALUES('2025-12-16 20:09:37', '2025-12-16 20:09:37', 'financingpropertylist', '投放资产列表', 0, (select id from bifrost_menu where code = 'financialproperty' and path = '/financial/property'), NULL, NULL, NULL, 'POST', '/financing/property/list', 1, NULL);
INSERT INTO bifrost_function
(gmt_create, gmt_modified, code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id)
VALUES('2025-12-16 20:09:37', '2025-12-16 20:09:37', 'financingpropertylistdownload', '投放资产列表-导出', 0, (select id from bifrost_menu where code = 'financialproperty' and path = '/financial/property'), NULL, NULL, NULL, 'POST', '/financing/property/list/download', 1, NULL);

