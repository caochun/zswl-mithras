-- 新增功能接口
delete from bifrost_org_menu_function where function_id = (select id from bifrost_function where code ='basedatabankaccountinit');
delete from bifrost_role_menu_function where function_id = (select id from bifrost_function where code ='basedatabankaccountinit');
delete from bifrost_function where code = 'basedatabankaccountinit';
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, method, path, type)
VALUES ('basedatabankaccountinit', '我方账户默认值', 0, 358, 'POST','/basedata/bankaccount/init', 2);

-- 部门添加接口
insert into bifrost_org_menu_function (org_id, menu_id, function_id, create_by, update_by, custom_tree_id)
values  (1, 358, (select id from bifrost_function where code ='basedatabankaccountinit'), null, null, 38),
        (22, 358, (select id from bifrost_function where code ='basedatabankaccountinit'), null, null, 38),
        (38, 358, (select id from bifrost_function where code ='basedatabankaccountinit'), null, null, 38);

-- 角色添加接口
insert into bifrost_role_menu_function (role_id, menu_id, function_id, create_by, update_by, custom_tree_id)
values  (55, 358, (select id from bifrost_function where code ='basedatabankaccountinit'), null, null, 38),
        (74, 358, (select id from bifrost_function where code ='basedatabankaccountinit'), null, null, 38),
        (106, 358, (select id from bifrost_function where code ='basedatabankaccountinit'), null, null, 38);