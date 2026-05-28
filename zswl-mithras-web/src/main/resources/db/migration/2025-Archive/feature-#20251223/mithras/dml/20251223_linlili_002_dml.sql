delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in ('filingBusinessInnerOperationFileDownload',
    'filingBusinessClientFileDownload',
    'filingBusinessPaymentFileDownload',
    'filingBusinessLeaseholdFileDownload',
    'filingBusinessCollateralizationFileDownload'));

delete from bifrost_function where code in (
    'filingBusinessInnerOperationFileDownload',
    'filingBusinessClientFileDownload',
    'filingBusinessPaymentFileDownload',
    'filingBusinessLeaseholdFileDownload',
    'filingBusinessCollateralizationFileDownload',
    'filingFileDownload'
);

INSERT INTO bifrost_function ( code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('filingFileDownload', '下载', 0, (select id from bifrost_menu where code= 'archivesfilingMaterials'), NULL, NULL, 'filingFileDownload', 'GET', '/filingMaterial/download', 1, (select id from gruul_function_group where group_describe = '项目资料归档-编辑'));

INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('运营管理部经办人','项目经理')
  and function.code = 'filingFileDownload'
  and menu.name = '资料归档'
  and tree.name = '档案管理';
