delete from bifrost_menu where code ='financeProjectDistribution';
insert into bifrost_menu (code, `level`, sort_no, `type`, `path`, parent_id, icon, name) VALUES( 'financeProjectDistribution', 3, 40, NULL, '/budgetManagement/financeProjectDistribution', NULL, NULL, '项目利润分配');

delete from gruul_function_group where code in ('financeProjectDistribution-Read','financeProjectDistribution-Edit');
insert into gruul_function_group ( sort_no, code, name, group_describe,menu_id) values( 0, 'financeProjectDistribution-Read', '项目利润分配-查看', '项目利润分配-查看',(select id from bifrost_menu bm where code = 'financeProjectDistribution'));
insert into gruul_function_group ( sort_no, code, name, group_describe,menu_id) values( 0, 'financeProjectDistribution-Edit', '项目利润分配-编辑', '项目利润分配-编辑',(select id from bifrost_menu bm where code = 'financeProjectDistribution'));

delete from bifrost_function where code in ('financeProjectDistributionSubmit','financeProjectDistributionDeptSave','financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail');
insert into bifrost_function ( code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('financeProjectDistributionSubmit', '提交审批', 0, (select id from bifrost_menu where code= 'financeProjectDistribution'), NULL, NULL, 'financeProjectDistributionSubmit', 'POST', '/finance/projectdistribution/submit', 1, (select id from gruul_function_group where code = 'financeProjectDistribution-Edit'));
insert into bifrost_function ( code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('financeProjectDistributionDeptSave', '保存', 0, (select id from bifrost_menu where code= 'financeProjectDistribution'), NULL, NULL, 'financeProjectDistributionDeptSave', 'POST', '/finance/project/distribution/dept/weight/save', 1, (select id from gruul_function_group where code = 'financeProjectDistribution-Edit'));
insert into bifrost_function ( code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('financeProjectDistributionBaseDetail', '基本信息详情', 0, (select id from bifrost_menu where code= 'financeProjectDistribution'), NULL, NULL, 'financeProjectDistributionBaseDetail', 'POST', '/finance/projectdistribution/baseinfo/detail', 1, (select id from gruul_function_group where code = 'financeProjectDistribution-Read'));
insert into bifrost_function ( code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('financeProjectDistributionDeptDetail', '项目利润部门分配信息', 0, (select id from bifrost_menu where code= 'financeProjectDistribution'), NULL, NULL, 'financeProjectDistributionDeptDetail', 'POST', '/finance/project/distribution/dept/weight/detail', 1, (select id from gruul_function_group where code = 'financeProjectDistribution-Read'));

delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in ('financeProjectDistributionSubmit','financeProjectDistributionDeptSave','financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail'));

insert into bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
select role.id, menu.id, function.id, tree.id
from bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where role.name in ('项目经理')
  and function.code in ('financeProjectDistributionSubmit','financeProjectDistributionDeptSave','financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail')
  and menu.name = '项目利润分配'
  and tree.name = '预算管理';

insert into bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
select role.id, menu.id, function.id, tree.id
from bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where role.id in (select br.id from gruul_user_org_role guor
inner join gruul_user_org_job  a on a.user_id = guor.user_id and a.org_id = guor.org_id
inner join bifrost_org bo on bo.id = guor.org_id and bo.`type` ='1'
inner join bifrost_role br on br.id = guor.role_id where a.job_code in ('businesshead','leaderincharge'))
  and function.code in ('financeProjectDistributionDeptSave','financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail')
  and menu.name = '项目利润分配'
  and tree.name = '预算管理';
