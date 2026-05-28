INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by,
                          update_by)
VALUES ('budgetAccountReceivable', 3, 90, null, '/budget/accountsReceivable', null, null, '应收逾期报送', null, '_self',
        null, null);

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
VALUES (139, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 0);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('financeOverdueSettlementList', '资金管理-逾期报送-应收逾期结算表列表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/list', 2),
        ('financeOverdueSettlementRemove', '资金管理-逾期报送-删除应收逾期结算表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/remove', 2),
        ('financeOverdueSettlementModify', '资金管理-逾期报送-修改应收逾期结算表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/modify', 2),
        ('financeOverdueSettlementAdd', '资金管理-逾期报送-新增应收逾期结算表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/add', 2),
        ('financeOverdueSettlementContractRelation', '资金管理-逾期报送-逾期查询客户下合同信息', 0, (select id from bifrost_menu where code =                                                                        'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/contract/relation', 2),
        ('financeOverdueSettlementPush', '资金管理-逾期报送-推送应收逾期结算', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/settlement/push', 2);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('financeOverdueIntegrationList', '资金管理-逾期报送-应收逾期集成表列表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/integration/list', 2),
        ('financeOverdueIntegrationRemove', '资金管理-逾期报送-删除应收逾期集成表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/integration/remove', 2),
        ('financeOverdueIntegrationModify', '资金管理-逾期报送-修改应收逾期集成表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/integration/modify', 2),
        ('financeOverdueIntegrationPush', '资金管理-逾期报送-推送应收逾期集成表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/integration/push', 2);

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('financeOverdueReportClose', '资金管理-逾期报送-关闭逾期报送计划表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/report/base/close', 2),
        ('financeOverdueReportAdd', '资金管理-逾期报送-新增逾期报送计划表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/report/base/add', 2),
        ('financeOverdueReportList', '资金管理-逾期报送-逾期报送计划表列表', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/report/base/list', 2),
        ('financeOverdueSubmit', '资金管理-逾期报送-提交应收逾期集成单', 0, (select id from bifrost_menu where code = 'budgetAccountReceivable'), 'POST',
         '/finance/overdue/submit', 2);
