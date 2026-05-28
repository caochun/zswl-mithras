delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in ('financeProjectDistributionDeptSave','financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail'))
and role_id <> (select id from bifrost_role where name = '项目经理');

insert into bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
select role.id, menu.id, function.id, tree.id
from bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where role.name <> '项目经理'
  and function.code in ('financeProjectDistributionDeptSave','financeProjectDistributionBaseDetail','financeProjectDistributionDeptDetail')
  and menu.name = '项目利润分配'
  and tree.name = '预算管理';

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
    'filingSynchronizationButtonFlag'
))
and role_id <> (select id from bifrost_role where name = '项目经理');


insert into bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
select role.id, menu.id, function.id, tree.id
from bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where role.name <> '项目经理'
  and function.code IN (
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
    'filingSynchronizationButtonFlag'
) and menu.name = '资料归档'
  and tree.name = '档案管理';

delete from bifrost_role_menu_function where function_id = (select id from  bifrost_function where code = 'paymentFinanceProjectDistribution')
and role_id <> (select id from bifrost_role where name = '项目经理');

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name <> '项目经理'
  and function.code = 'paymentFinanceProjectDistribution'
  and menu.name = '付款申请'
  and tree.name = '收付款管理';


