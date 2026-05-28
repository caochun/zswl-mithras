INSERT INTO bifrost_custom_tree (code, name, flag, sort_no, parent_id, create_by, update_by, en_name, icon)
VALUES ('overduemanagement', '逾期管理', 1, 51, null, null, null, null, 'icon-cuishou');

INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('overduelitigationDoc', 3, 3, null, '/overdue/litigationDoc', null, null, '诉讼文书用印');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('overduelitigationRegistration', 3, 2, null, '/overdue/litigationRegistration', null, null, '诉讼登记');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('overduecollection', 3, 1, null, '/overdue/collection', null, null, '逾期催收');

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '逾期管理') a
         join (select id as menu_id from bifrost_menu where code = 'overduecollection') b;
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '逾期管理') a
         join (select id as menu_id from bifrost_menu where code = 'overduelitigationRegistration') b;
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '逾期管理') a
         join (select id as menu_id from bifrost_menu where code = 'overduelitigationDoc') b;

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileDownload', '文件下载', 0, id, 'GET', '/file/download', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileListGroup', '文件列表分组', 0, id, 'POST', '/file/list/group', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileList', '文件列表', 0, id, 'POST', '/file/list', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileBatchDownload', '文件批量下载', 0, id, 'GET', '/file/batch/download', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileBatchRemove', '批量删除', 0, id, 'post', '/file/batch/remove', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileRemove', '删除文件', 0, id, 'post', '/file/remove', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileUpload', '上传文件', 0, id, 'post', '/file/upload', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingIndexDownload', '首页列表下载', 0, id, 'POST', '/index/download', 2
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'printingSave', '文书用印编辑', 0, id, 'POST', '/printing/save', 2
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'printingAdd', '文书用印新增', 0, id, 'POST', '/printing/add', 2
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'printingSubmit', '文书用印提交审批', 0, id, 'POST', '/printing/submit', 2
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'printingDetail', '文书用印详情', 0, id, 'POST', '/printing/detail', 2
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'printingPageList', '文书用印列表', 0, id, 'POST', '/printing/pageList', 2
from bifrost_menu
where code = 'overduelitigationDoc';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileDownload', '文件下载', 0, id, 'GET', '/file/download', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileListGroup', '文件列表分组', 0, id, 'POST', '/file/list/group', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileList', '文件列表', 0, id, 'POST', '/file/list', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileBatchDownload', '文件批量下载', 0, id, 'GET', '/file/batch/download', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileBatchRemove', '批量删除', 0, id, 'post', '/file/batch/remove', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileRemove', '删除文件', 0, id, 'post', '/file/remove', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileUpload', '上传文件', 0, id, 'post', '/file/upload', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationIndexDownload', '首页列表下载', 0, id, 'POST', '/index/download', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationSave', '诉讼登记保存', 0, id, 'POST', '/litigation/save', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationAdd', '新增诉讼登记', 0, id, 'POST', '/litigation/add', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationDetail', '诉讼登记详情', 0, id, 'GET', '/litigation/detail', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationPageList', '诉讼登记列表', 0, id, 'POST', '/litigation/pageList', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationClientOverdueinfo', '客户逾期信息展示', 0, id, 'GET', '/litigation/client/overdueinfo', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationProgressAdd', '诉讼登记新增进展', 0, id, 'POST', '/litigation/progress/add', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationDefendantRemove', '诉讼登记删除被告', 0, id, 'POST', '/litigation/defendant/remove', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationDefendantAdd', '诉讼登记新增被告', 0, id, 'POST', '/litigation/defendant/add', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationContractClient', '诉讼登记-合同相关全量客户', 0, id, 'POST', '/litigation/contract/client', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationContractPulldown', '诉讼登记-客户相关合同下拉', 0, id, 'GET', '/litigation/contract/pulldown', 2
from bifrost_menu
where code = 'overduelitigationRegistration';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileDownload', '文件下载', 0, id, 'GET', '/file/download', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileListGroup', '文件列表分组', 0, id, 'POST', '/file/list/group', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileList', '文件列表', 0, id, 'POST', '/file/list', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileBatchDownload', '文件批量下载', 0, id, 'GET', '/file/batch/download', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileBatchRemove', '批量删除', 0, id, 'post', '/file/batch/remove', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileRemove', '删除文件', 0, id, 'post', '/file/remove', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileUpload', '上传文件', 0, id, 'post', '/file/upload', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionIndexDownload', '首页列表下载', 0, id, 'POST', '/index/download', 2
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactiondetail', '催收动作详情', 0, id, 'POST', '/overduecollection/action/detail', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactionsubmit', '催收提交审批', 0, id, 'POST', '/overduecollection/action/submit', 2
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionlettergenerate', '生成函件', 0, id, 'POST', '/overduecollection/letter/generate', 2
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactionupdate', '修改催收信息', 0, id, 'POST', '/overduecollection/action/update', 2
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactionadd', '新增催收动作', 0, id, 'POST', '/overduecollection/action/add', 2
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactiondownload', '催收动作列表导出', 0, id, 'POST', '/overduecollection/action/download', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectioncontractpulldown', '合同下拉列表', 0, id, 'GET', '/overduecollection/contract/pulldown', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectioncontractexport', '合同列表导出', 0, id, 'POST', '/overduecollection/contract/export', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectioncontractlist', '合同列表', 0, id, 'GET', '/overduecollection/contract/list', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectiondetail', '催收详情', 0, id, 'POST', '/overduecollection/detail', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionlist', '催收列表', 0, id, 'POST', '/overduecollection/list', 1
from bifrost_menu
where code = 'overduecollection';


INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (1, 2024, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (2, 2025, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (3, 2026, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (3, 2027, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (3, 2028, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (3, 2029, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (3, 2030, 1);


INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactiondelete', '删除催收信息', 0, id, 'POST', '/overduecollection/action/delete', 2
from bifrost_menu
where code = 'overduecollection';
