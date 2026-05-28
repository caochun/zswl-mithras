-- 项目利润分配、项目资料归档菜单挂我收到下
update bifrost_function set menu_id = (select id from bifrost_menu bm where bm.code ='QX0108' and `path` ='/process/receive'),
group_id = (select id from gruul_function_group where code = 'QX0108-Read')
where code in ('filingGetCustomerReferenceMaterials','filingGetNonCustomerReferenceMaterials','filingGetOperationsDirDict','filingGetTab','filingSynchronizationButtonFlag', 'financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail');

update bifrost_function set menu_id = (select id from bifrost_menu bm where bm.code ='QX0108' and `path` ='/process/receive'),
group_id = (select id from gruul_function_group where code = 'QX0108-Write')
where code in ('filingBusinessClientFileBatchRemove',
    'filingBusinessClientFileUpload',
    'filingBusinessCollateralizationFileBatchRemove',
    'filingBusinessCollateralizationFileUpload',
    'filingBusinessInnerOperationFileBatchRemove',
    'filingBusinessInnerOperationFileUpload',
    'filingBusinessLeaseholdFileBatchRemove',
    'filingBusinessLeaseholdFileUpload',
    'filingBusinessPaymentFileBatchRemove',
    'filingBusinessPaymentFileUpload',
    'filingFileBatchDownload',
    'filingFileDownload',
    'filingMaterialsImport',
    'filingMaterialsSynchronization',
	'financeProjectDistributionDeptSave',
	'financeProjectDistributionSubmit');

delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in (
    'filingBusinessClientFileBatchRemove',
    'filingBusinessClientFileUpload',
    'filingBusinessCollateralizationFileBatchRemove',
    'filingBusinessCollateralizationFileUpload',
    'filingBusinessInnerOperationFileBatchRemove',
    'filingBusinessInnerOperationFileUpload',
    'filingBusinessLeaseholdFileBatchRemove',
    'filingBusinessLeaseholdFileUpload',
    'filingBusinessPaymentFileBatchRemove',
    'filingBusinessPaymentFileUpload',
    'filingFileBatchDownload',
    'filingFileDownload',
    'filingGetCustomerReferenceMaterials',
    'filingGetNonCustomerReferenceMaterials',
    'filingGetOperationsDirDict',
    'filingGetTab',
    'filingMaterialsImport',
    'filingMaterialsSynchronization',
    'filingSynchronizationButtonFlag',
    'financeProjectDistributionDeptSave',
    'financeProjectDistributionBaseDetail',
    'financeProjectDistributionDeptDetail',
    'financeProjectDistributionSubmit'
));

delete from bifrost_org_menu_function where function_id in (select id from bifrost_function where code in (
    'filingBusinessClientFileBatchRemove',
    'filingBusinessClientFileUpload',
    'filingBusinessCollateralizationFileBatchRemove',
    'filingBusinessCollateralizationFileUpload',
    'filingBusinessInnerOperationFileBatchRemove',
    'filingBusinessInnerOperationFileUpload',
    'filingBusinessLeaseholdFileBatchRemove',
    'filingBusinessLeaseholdFileUpload',
    'filingBusinessPaymentFileBatchRemove',
    'filingBusinessPaymentFileUpload',
    'filingFileBatchDownload',
    'filingFileDownload',
    'filingGetCustomerReferenceMaterials',
    'filingGetNonCustomerReferenceMaterials',
    'filingGetOperationsDirDict',
    'filingGetTab',
    'filingMaterialsImport',
    'filingMaterialsSynchronization',
    'filingSynchronizationButtonFlag',
    'financeProjectDistributionDeptSave',
    'financeProjectDistributionBaseDetail',
    'financeProjectDistributionDeptDetail',
    'financeProjectDistributionSubmit'
));

-- 项目利润分配中后台数据
insert into bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
select role.id, menu.id, function.id, tree.id
from bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where role.id in (select b.id  from gruul_user_org_job a,bifrost_role b  where a.org_id  = b.org_id and a.job_code  in ('businesshead','leaderincharge','financialmanager','financialmanager'))
  and function.code in ('financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

insert into bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
select role.id, menu.id, function.id, tree.id
from bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where role.name = '项目经理'
  and function.code in ('financeProjectDistributionDeptSave','financeProjectDistributionSubmit','financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

-- 项目资料归档角色权限
insert into bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
select role.id, menu.id, function.id, tree.id
from bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where role.name = '项目经理'
  and function.code in ('filingFileBatchDownload','filingGetOperationsDirDict','filingGetCustomerReferenceMaterials','filingGetTab','filingSynchronizationButtonFlag','filingGetNonCustomerReferenceMaterials')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

insert into bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
select role.id, menu.id, function.id, tree.id
from bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where role.id in (select b.id  from gruul_user_org_job a,bifrost_role b  where a.org_id  = b.org_id and a.job_code in ('operationManagement','yunYingGuanLi','yunYingGuanLiReview','headofyyglb'))
  and function.code in ('filingBusinessClientFileBatchRemove',
    'filingBusinessClientFileUpload',
    'filingBusinessCollateralizationFileBatchRemove',
    'filingBusinessCollateralizationFileUpload',
    'filingBusinessInnerOperationFileBatchRemove',
    'filingBusinessInnerOperationFileUpload',
    'filingBusinessLeaseholdFileBatchRemove',
    'filingBusinessLeaseholdFileUpload',
    'filingBusinessPaymentFileBatchRemove',
    'filingBusinessPaymentFileUpload',
    'filingFileBatchDownload',
    'filingFileDownload',
    'filingGetCustomerReferenceMaterials',
    'filingGetNonCustomerReferenceMaterials',
    'filingGetOperationsDirDict',
    'filingGetTab',
    'filingMaterialsImport',
    'filingMaterialsSynchronization',
    'filingSynchronizationButtonFlag')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

insert into bifrost_org_menu_function (org_id, menu_id, function_id,custom_tree_id)
select org.id, menu.id, function.id, tree.id
from bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where (org.type ='1' or org.code ='JHCWB')
  and function.code in ('financeProjectDistributionDeptSave','financeProjectDistributionSubmit','financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

insert into bifrost_org_menu_function (org_id, menu_id, function_id,custom_tree_id)
select org.id, menu.id, function.id, tree.id
from bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where (org.type ='1' or org.code ='YYGLB')
  and function.code in ('filingBusinessClientFileBatchRemove',
    'filingBusinessClientFileUpload',
    'filingBusinessCollateralizationFileBatchRemove',
    'filingBusinessCollateralizationFileUpload',
    'filingBusinessInnerOperationFileBatchRemove',
    'filingBusinessInnerOperationFileUpload',
    'filingBusinessLeaseholdFileBatchRemove',
    'filingBusinessLeaseholdFileUpload',
    'filingBusinessPaymentFileBatchRemove',
    'filingBusinessPaymentFileUpload',
    'filingFileBatchDownload',
    'filingFileDownload',
    'filingGetCustomerReferenceMaterials',
    'filingGetNonCustomerReferenceMaterials',
    'filingGetOperationsDirDict',
    'filingGetTab',
    'filingMaterialsImport',
    'filingMaterialsSynchronization',
    'filingSynchronizationButtonFlag')
  and menu.name = '我收到的'
  and tree.name = '我的流程';

