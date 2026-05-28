-- 征信解析


INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target)
VALUES ('CreditManageSearch', 3, 20, '', '/creditManage/search', null, null, '征信查询', null, '_self');

insert into bifrost_custom_tree_menu_ref(custom_tree_id, menu_id)
select a.id, b.id
from bifrost_custom_tree a,
     bifrost_menu b
where a.code = 'creditReport'
  and b.code = 'CreditManageSearch';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('creditReportBaseClientInfo', '查询有征信报告查询权限的客户列表信息', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'GET',
         '/creditreport/base/clientInfo', 2),
        ('creditReportBaseShowCreditReportByClientId', '根据客户id反显客户信息和关联项目信息', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'GET',
         '/creditreport/base/showCreditReportByClientId', 2),
        ('creditReportBaseAdd', '新增征信报告查询', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/creditreport/base/add', 2),
        ('creditReportBaseList', '征信报告查询列表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/creditreport/base/list', 2),
        ('creditReportBaseDetail', '征信报告查询详情', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'GET',
         '/creditreport/base/detail', 2),
        ('creditReportBaseSave', '征信报告查询详情保存', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/creditreport/base/save', 2),
        ('creditReportBaseSubmit', '征信报告查询提交', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/creditreport/base/submit', 2),
        ('creditReportBaseDelete', '征信查询删除', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'GET', '/creditreport/base/delete',
         2),
        ('creditReportBaseCreditSearchClientCompareBusiness', '客户比对承租人及担保人工商信息', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/creditreport/base/creditSearch/client/compare/business', 2),
        ('creditReportBaseExport', '征信查询批量导出', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/creditreport/base/export', 2)
;

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('creditReportCreditLimitTableList', '征信报告-信用额度表列表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/limit/list', 2),
        ('creditReportLimitModify', '修改征信报告-信用额度表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/limit/modify', 2),
        ('creditReportLimitRemove', '删除征信报告-信用额度表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/credit/report/limit/remove', 2)
;


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('creditReportRecordDetailsList', '征信报告-信贷记录明细表列表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/record/details/list', 2),
        ('creditReportRecordDetailsRemove', '删除征信报告-信贷记录明细表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/record/details/remove', 2),
        ('creditReportRecordDetailsModify', '修改征信报告-信贷记录明细表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/record/details/modify', 2),
        ('creditReportRecordDetailsAdd', '新增征信报告-信贷记录明细表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/credit/report/record/details/add', 2)
;


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('creditReportRepaymentResponsibilityRemove', '删除征信报告-相关还款责任信息概要表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/repayment/responsibility/remove', 2),
        ('creditReportRepaymentResponsibilityModify', '修改征信报告-相关还款责任信息概要表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/repayment/responsibility/modify', 2),
        ('creditReportRepaymentResponsibilityList', '征信报告-相关还款责任信息概要表列表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/credit/report/repayment/responsibility/list', 2)
;

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('creditReportSummaryList', '征信报告-信息概要表列表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/summary/list', 2),
        ('creditReportSummaryRemove', '删除征信报告-信息概要表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/summary/remove', 2),
        ('creditReportSummaryAdd', '新增征信报告-信息概要表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/summary/add', 2),
        ('creditReportSummaryModify', '修改征信报告-信息概要表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/summary/modify', 2),
        ('creditReportSummaryDetail', '征信报告-信息概要表详情', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/credit/report/summary/detail', 2)
;


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('creditReportUnsettledSummaryModify', '修改征信报告-未结清信贷及授信信息表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/unsettled/summary/modify', 2),
        ('creditReportUnsettledSummaryList', '征信报告-未结清信贷及授信信息表列表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/credit/report/unsettled/summary/list', 2),
        ('creditReportUnsettledSummaryRemove', '删除征信报告-未结清信贷及授信信息表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/credit/report/unsettled/summary/remove', 2)
;


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('creditReportProjectList', '征信查询列表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/creditreport/project/list', 2),
        ('creditReportProjectDelete', '征信查询删除', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'GET',
         '/creditreport/project/delete', 2),
        ('creditReportProjectShowCreditReportByProjId', '根据项目id反显项目信息和关联客户信息', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST', '/creditreport/project/showCreditReportByProjId', 2)
;


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('creditReportClientList', '征信报告查询列表', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'POST',
         '/creditreport/client/list', 2),
        ('creditReportClientDelete', '征信查询删除', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), 'GET',
         '/creditreport/client/delete', 2)
;

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('creditReportSelectFileListGroup', '征信查询-文件列表分组', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), null, null, null, 'POST','/file/list/group', null, null);

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('creditReportSelectFileUpload', '征信查询-文件上传', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), null, null, null, 'POST',
        '/file/upload', 2, null);


INSERT INTO general_dictionary (dict_key, dict_desc, code, display, sort) VALUES ('job', '岗位类型', 'creditCheckOfficer', '征信查询员', 10);

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('creditReportSelectFileBatchDownload', '征信查询-文件批量下载', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), null, null, null, 'GET',
        '/file/batch/download', null, null);

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('creditReportSelectFileList', '征信查询-文件信息', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), null, null, null, 'POST',
        '/file/list', 2, null);

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('creditReportSelectFileBatchRemove', '征信查询-删除信息', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), null, null, null,
        'POST','/file/batch/remove', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('creditReportSelectFileDownload', '征信查询-单个下载', 0, (select id from bifrost_menu where code = 'CreditManageSearch'), null, null, null,
        'GET','/file/download', 2, null);

update bifrost_custom_tree set parent_id = 224, code = 'creditReportManager'
where code = 'creditReport';

update bifrost_menu set path = '/creditManage/creditTable/wait' where code = 'creditTableWait';

update bifrost_menu set path = '/creditManage/creditTable/finish' where code = 'creditTableFinish';


insert into bifrost_custom_tree_menu_ref(custom_tree_id, menu_id)
VALUES (224, 832);






