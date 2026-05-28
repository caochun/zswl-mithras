
update bifrost_function set menu_id = (select id from bifrost_menu bm where bm.code ='QX0108' and `path` ='/process/receive'),
group_id = (select id from gruul_function_group where code = 'QX0108-Write')
where code IN ('filingBusinessClientFileDownload','filingBusinessClientFilingUpload','filingBusinessCollateralizationFileDownload','filingBusinessCollateralizationFilingUpload','filingBusinessInnerOperationFileDownload','filingBusinessInnerOperationFilingUpload','filingBusinessLeaseholdFileDownload','filingBusinessLeaseholdFilingUpload','filingBusinessPaymentFileDownload','filingBusinessPaymentFilingUpload');

-- 新增基础清单下载模板及删除功能
delete from bifrost_function where code in ('filingRemove','filingTemplateDownload');
insert into bifrost_function ( code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('filingRemove', '基础资料清单删除', 0, (select id from bifrost_menu bm where bm.code ='QX0108' and `path` ='/process/receive'), NULL, NULL, 'filingRemove', 'POST', '/filingMaterial/file/remove', 1, (select id from gruul_function_group where code = 'QX0108-Write'));
insert into bifrost_function ( code, name, sort_no, menu_id, create_by, update_by, en_name, `method`, `path`, `type`, group_id) VALUES('filingTemplateDownload', '基础资料模板下载', 0, (select id from bifrost_menu bm where bm.code ='QX0108' and `path` ='/process/receive'), NULL, NULL, 'filingTemplateDownload', 'GET', '/filingMaterial/template/download', 1, (select id from gruul_function_group where code = 'QX0108-Write'));

delete from bifrost_org_menu_function where function_id in (select id from bifrost_function where code in ('filingRemove','filingTemplateDownload'));
delete from bifrost_role_menu_function where function_id in (select id from bifrost_function where code in ('filingRemove','filingTemplateDownload'));

insert into bifrost_org_menu_function (org_id, menu_id, function_id,custom_tree_id)
select org.id, menu.id, function.id, tree.id
from bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where (org.type ='1' or org.code ='YYGLB')
  and function.code in ('filingRemove','filingTemplateDownload')
  and menu.name = '我收到的'
  and tree.name = '我的流程';


insert into bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
select role.id, menu.id, function.id, tree.id
from bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where (role.id in (select b.id  from gruul_user_org_job a,bifrost_role b  where a.org_id  = b.org_id and a.job_code in ('operationManagement','yunYingGuanLi','yunYingGuanLiReview','headofyyglb'))
  and function.code in ('filingRemove','filingTemplateDownload') or (role.name = '项目经理'))
  and menu.name = '我收到的'
  and tree.name = '我的流程';

