-- 合同正常结清
delete from bifrost_function where code ='businessFlowFinanceListExport';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ('businessFlowFinanceListExport', '业务流水资金端列表导出', 0, 495, 'POST', '/business/flow/finance/list/export', 2);




-- end