-- 插入
delete from bifrost_org_menu_function where function_id = (SELECT c.id FROM bifrost_function c WHERE c.code = 'businessFlowFinanceListExport');
delete from bifrost_role_menu_function where function_id = (SELECT c.id FROM bifrost_function c WHERE c.code = 'businessFlowFinanceListExport');
delete from bifrost_function where code ='businessFlowFinanceListExport';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ('businessFlowFinanceListExport', '业务流水资金端列表导出', 0, (SELECT c.menu_id FROM bifrost_function c WHERE c.code = 'businessFlowFinanceList'), 'POST', '/business/flow/finance/list/export', 2);

-- 复制列表查询权限
INSERT INTO bifrost_org_menu_function (
    org_id,
    menu_id,
    function_id,
    custom_tree_id
)
SELECT
    a.org_id,
    a.menu_id,
    (SELECT c.id FROM bifrost_function c WHERE c.code = 'businessFlowFinanceListExport'),
    a.custom_tree_id
FROM bifrost_org_menu_function a
INNER JOIN bifrost_function b ON a.function_id = b.id
WHERE b.code = 'businessFlowFinanceList'
AND NOT EXISTS (
    SELECT 1
    FROM bifrost_org_menu_function t
    WHERE t.org_id = a.org_id
      AND t.menu_id = a.menu_id
      AND t.function_id = (SELECT c.id FROM bifrost_function c WHERE c.code = 'businessFlowFinanceListExport')
      AND t.custom_tree_id = a.custom_tree_id
);


-- 复制列表查询权限
INSERT INTO bifrost_role_menu_function (
    role_id ,
    menu_id,
    function_id,
    custom_tree_id
)
SELECT
    a.role_id,
    a.menu_id,
    (SELECT c.id FROM bifrost_function c WHERE c.code = 'businessFlowFinanceListExport'),
    a.custom_tree_id
FROM bifrost_role_menu_function a
INNER JOIN bifrost_function b ON a.function_id = b.id
WHERE b.code = 'businessFlowFinanceList'
AND NOT EXISTS (
    SELECT 1
    FROM bifrost_role_menu_function t
    WHERE t.role_id = a.role_id
      AND t.menu_id = a.menu_id
      AND t.function_id = (SELECT c.id FROM bifrost_function c WHERE c.code = 'businessFlowFinanceListExport')
      AND t.custom_tree_id = a.custom_tree_id
);


-- end