delete from bifrost_function where code = 'paymentFinanceProjectDistribution';
INSERT INTO bifrost_function ( code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('paymentFinanceProjectDistribution', '项目利润流程校验', 0, (select id from bifrost_menu where code= 'QX0113'), NULL, NULL, 'paymentFinanceProjectDistribution', 'POST', '/payment/check/finance/project/distribution', 1, (select id from gruul_function_group where code = 'QX0113-Read'));

delete from bifrost_role_menu_function where function_id = (select id from  bifrost_function where code = 'paymentFinanceProjectDistribution');
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('项目经理')
  and function.code = 'paymentFinanceProjectDistribution'
  and menu.name = '付款申请'
  and tree.name = '收付款管理';

DELETE FROM bifrost_role_menu_function WHERE function_id IN (select id from bifrost_function
WHERE code IN (
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
    'filingSynchronizationButtonFlag'));

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('项目经理')
  and function.code in ('filingFileBatchDownload','filingGetOperationsDirDict','filingGetCustomerReferenceMaterials','filingGetTab','filingSynchronizationButtonFlag','filingGetNonCustomerReferenceMaterials')
  and menu.name = '资料归档'
  and tree.name = '档案管理';

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.id in (select br.id from gruul_user_org_role guor
inner join gruul_user_org_job  a on a.user_id = guor.user_id and a.org_id = guor.org_id
inner join bifrost_org bo on bo.id = guor.org_id and bo.`type` ='2'
inner join bifrost_role br on br.id = guor.role_id where a.job_code in ('yunYingGuanLi','yunYingGuanLiReview'))
  and function.code like 'filing%'
  and menu.name = '资料归档'
  and tree.name = '档案管理';