INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('collectionFlowCenterRecycleMarginPlan', '获取保证金回收计划', (select id from bifrost_menu where code = 'budgetFlowCenter'), 'POST',
        '/collection/flow/center/recycle/margin/plan', '2');
