-- 新增功能接口
delete from bifrost_org_menu_function where function_id = (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount');
delete from bifrost_role_menu_function where function_id = (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount');
delete from bifrost_function where code = 'budgetplanpaymonthdetailcheckplanpayamount';
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type,group_id)
VALUES ('budgetplanpaymonthdetailcheckplanpayamount', '预算管理-投放计划-月度-明细-检查', 0, (select id from bifrost_menu where code = 'budgetPlanPay'), 'POST',
        '/budget/plan/pay/month/detail/checkPlanPayAmount', 1,(select id from gruul_function_group where code = 'YSGL-TFJH-WRITE'));

-- 部门添加接口
insert into bifrost_org_menu_function (org_id, menu_id, function_id, create_by, update_by, custom_tree_id)
values  (1, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (2, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (13, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (15, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (22, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220);

-- 角色添加接口
insert into bifrost_role_menu_function (role_id, menu_id, function_id, create_by, update_by, custom_tree_id)
values  (53, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (127, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (3, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (4, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (67, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (2, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (106, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220),
        (128, 821, (select id from bifrost_function where code ='budgetplanpaymonthdetailcheckplanpayamount'), null, null, 220);
