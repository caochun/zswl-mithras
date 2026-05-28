
update bifrost_function set group_id = (select id from gruul_function_group where code = 'filing-Read')
where code in ('filingGetCustomerReferenceMaterials','filingGetNonCustomerReferenceMaterials','filingGetOperationsDirDict','filingGetTab','filingSynchronizationButtonFlag');

delete from bifrost_role_menu_function where role_id in (select br.id from gruul_user_org_role guor
inner join gruul_user_org_job  a on a.user_id = guor.user_id and a.org_id = guor.org_id
inner join bifrost_org bo on bo.id = guor.org_id and bo.`type` ='2'
inner join bifrost_role br on br.id = guor.role_id where a.job_code in ('headofyyglb'))
and function_id in (select id from bifrost_function where code in ('filingFileBatchDownload','filingGetOperationsDirDict','filingGetCustomerReferenceMaterials','filingGetTab','filingSynchronizationButtonFlag','filingGetNonCustomerReferenceMaterials'));

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.id IN (select br.id from gruul_user_org_role guor
inner join gruul_user_org_job  a on a.user_id = guor.user_id and a.org_id = guor.org_id
inner join bifrost_org bo on bo.id = guor.org_id and bo.`type` ='2'
inner join bifrost_role br on br.id = guor.role_id where a.job_code in ('headofyyglb'))
  and function.code in ('filingFileBatchDownload','filingGetOperationsDirDict','filingGetCustomerReferenceMaterials','filingGetTab','filingSynchronizationButtonFlag','filingGetNonCustomerReferenceMaterials')
  and menu.name = '资料归档'
  and tree.name = '档案管理';


INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.id IN (select br.id from gruul_user_org_role guor
inner join gruul_user_org_job  a on a.user_id = guor.user_id and a.org_id = guor.org_id
inner join bifrost_org bo on bo.id = guor.org_id and bo.`type` ='2'
inner join bifrost_role br on br.id = guor.role_id where a.job_code in ('headofyyglb'))
  and function.code in ('filingFileBatchDownload','filingGetOperationsDirDict','filingGetCustomerReferenceMaterials','filingGetTab','filingSynchronizationButtonFlag','filingGetNonCustomerReferenceMaterials')
  and menu.name = '资料归档'
  and tree.name = '档案管理';

delete from bifrost_role_menu_function where role_id in (select br.id from gruul_user_org_role guor
inner join gruul_user_org_job  a on a.user_id = guor.user_id and a.org_id = guor.org_id
inner join bifrost_org bo on bo.id = guor.org_id and bo.`type` ='2'
inner join bifrost_role br on br.id = guor.role_id where a.job_code in ('financialmanager','financialofficer'))
and function_id in (select id from bifrost_function where code in ('financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail'));


insert into bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
select role.id, menu.id, function.id, tree.id
from bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where role.id in (select br.id from gruul_user_org_role guor
inner join gruul_user_org_job  a on a.user_id = guor.user_id and a.org_id = guor.org_id
inner join bifrost_org bo on bo.id = guor.org_id and bo.`type` ='2'
inner join bifrost_role br on br.id = guor.role_id where a.job_code in ('financialmanager','financialofficer'))
  and function.code in ('financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail')
  and menu.name = '项目利润分配'
  and tree.name = '预算管理';
