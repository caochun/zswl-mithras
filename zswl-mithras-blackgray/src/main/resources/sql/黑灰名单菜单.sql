
INSERT INTO bifrost_custom_tree (code, name, flag, sort_no, parent_id, create_by, update_by, en_name, icon, path, lang_env) VALUES ('blackListManage', '黑灰名单', 1, null, null, null, null, null, 'icon-fengxianguanli', null, null);
INSERT INTO bifrost_custom_tree (code, name, flag, sort_no, parent_id, create_by, update_by, en_name, icon, path, lang_env) VALUES ('warehouse', '入库管理', 1, null, (select id from bifrost_custom_tree where code = 'blackListManage'), null, null, null, null, null, null);
INSERT INTO bifrost_custom_tree (code, name, flag, sort_no, parent_id, create_by, update_by, en_name, icon, path, lang_env) VALUES ('outbound', '出库管理', 1, null, (select id from bifrost_custom_tree where code = 'blackListManage'), null, null, null, null, null, null);
INSERT INTO bifrost_custom_tree (code, name, flag, sort_no, parent_id, create_by, update_by, en_name, icon, path, lang_env) VALUES ('zhquery', '综合查询', 1, null, (select id from bifrost_custom_tree where code = 'blackListManage'), null, null, null, null, null, null);
INSERT INTO bifrost_custom_tree (code, name, flag, sort_no, parent_id, create_by, update_by, en_name, icon, path, lang_env) VALUES ('bizParam', '业务参数', 1, null, (select id from bifrost_custom_tree where code = 'blackListManage'), null, null, null, null, null, null);


INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by, update_by) VALUES ('blackListManagewarehousemainTask', 1, 0, '0', '/blackListManage/warehouse/mainTask', null, null, '任务报送', null, '_self', null, null);
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by, update_by) VALUES ('blackListManagewarehousesearch', 1, 0, '0', '/blackListManage/warehouse/search', null, null, '查询', null, '_self', null, null);
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by, update_by) VALUES ('blackListManageoutboundapplication', 1, 0, '0', '/blackListManage/outbound/application', null, null, '出库',null, '_self', null, null);
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by, update_by) VALUES ('blackListManageoutboundsearch', 1, 0, '0', '/blackListManage/outbound/search', null, null, '查询', null, '_self', null, null);
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target, create_by, update_by) VALUES ('blackListManageparameterswarehouse', 1, 0, '0', '/blackListManage/parameters/warehouse', null, null, '入库规则配置', null, '_self', null, null);

INSERT INTO mithras.bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES ((select id from bifrost_custom_tree where code = 'bizParam'), (select id from bifrost_menu where code = 'blackListManageparameterswarehouse'), 1);
INSERT INTO mithras.bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES ((select id from bifrost_custom_tree where code = 'zhquery'), (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 1);
INSERT INTO mithras.bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES ((select id from bifrost_custom_tree where code = 'zhquery'), (select id from bifrost_menu where code = 'blackListManagequeryrecognize'), 1);
INSERT INTO mithras.bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES ((select id from bifrost_custom_tree where code = 'outbound'), (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 1);
INSERT INTO mithras.bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES ((select id from bifrost_custom_tree where code = 'outbound'), (select id from bifrost_menu where code = 'blackListManageoutboundsearch'), 1);
INSERT INTO mithras.bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES ((select id from bifrost_custom_tree where code = 'warehouse'), (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 1);
INSERT INTO mithras.bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES ((select id from bifrost_custom_tree where code = 'warehouse'), (select id from bifrost_menu where code = 'blackListManagewarehousesearch'), 1);
delete from where id in (761, 762);



-- 注册sql
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('blackGrayApprovalWarehouseSubmit', '黑灰名单库入库审批', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/approval/warehouse/submit', '2'),
       ('blackGrayApprovalWarehouseAuditList', '黑灰名单入库-审批查询', (select id from bifrost_menu where code = 'blackListManagewarehousesearch'), 'GET',
        '/black/gray/approval/warehouse/auditList', '2'),
       ('blackGrayApprovalBreakBusiness', '黑灰名单库突破审批', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/approval/break/business', '2'),
       ('blackGrayApprovalBreakBusinessAuditList', '黑灰名单突破-审批查询', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'GET',
        '/black/gray/approval/break/business/auditList', '2'),
       ('blackGrayApprovalManualOutboundAuditList', '黑灰名单出库-审批查询', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'GET',
        '/black/gray/approval/manual/outbound/auditList', '2'),
       ('blackGrayApprovalManualTask', '黑灰名单主任务审批', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/approval/manual/task', '2'),
       ('blackGrayApprovalManualTaskAuditList', '黑灰名单主任务-审批查询', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'GET',
        '/black/gray/approval/manual/task/auditList', '2'),
       ('blackGrayApprovalManualOutbound', '黑灰名单库出库审批', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'POST',
        '/black/gray/approval/manual/outbound', '2');
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('publicBlackQueryVagueEnterprise', '黑灰名单模糊查询企业信息', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/public/black/query/vague/enterprise', '2'),
       ('publicBlackBatchQueryAssociatedEnterprise', '批量填充所属企业及下属企业', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/public/black/batch/query/associated/enterprise', '2'),
       ('publicBlackQueryAssociatedEnterprise', '查询下属企业', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/public/black/query/associated/enterprise', '2'),
       ('publicBlackQueryAffiliatedEnterprise', '所属企业', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/public/black/query/affiliated/enterprise', '2');
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('blackGrayManualOutboundAdd', '新增黑灰名单人工出库表', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'POST',
        '/black/gray/manual/outbound/add', '2'),
       ('blackGrayManualOutboundRemove', '黑灰名单人工出库表删除', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'POST',
        '/black/gray/manual/outbound/remove', '2'),
       ('blackGrayManualOutboundList', '黑灰名单人工出库表列表', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'POST',
        '/black/gray/manual/outbound/list', '2'),
       ('blackGrayManualOutboundRecordExport', '黑灰名单出库导出', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'GET',
        '/black/gray/manual/outbound/record/export', '2'),
       ('blackGrayManualOutboundDetail', '黑灰名单人工出库表详情', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'POST',
        '/black/gray/manual/outbound/detail', '2'),
       ('blackGrayManualOutboundModify', '修改黑灰名单人工出库表', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'POST',
        '/black/gray/manual/outbound/modify', '2');
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('blackGrayBaseInfoList', '风控系统查询黑灰名单库列表', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'POST',
        '/black/gray/base/info/list', '2'),
       ('blackGraySingleEntBusinessType', '单一企业tab中展示类别过滤', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'GET',
        '/black/gray/singleEnt/businessType', '2'),
       ('blackGrayBaseInfoLibrary', '查询黑灰名单库-客户最严重记录', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'POST',
        '/black/gray/base/info/library', '2'),
       ('blackGrayBaseInfoDistinctExport', '风控系统查询黑灰名单库按企业汇总列表导出', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'GET',
        '/black/gray/base/info/distinct/export', '2'),
       ('blackGrayCanBreakBusiness', '风控系统查询可突破黑灰名单类型', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'POST',
        '/black/gray/can/break/business', '2'),
       ('blackGrayEnterpriseRiskScale', '风控系统查询风险规模', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'POST',
        '/black/gray/enterprise/riskScale', '2'),
       ('blackGrayBatchQueryTemplateDownload', '批量查询下载模版', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'GET',
        '/black/gray/batch/query/template/download', '2'),
       ('blackGrayBatchBatchQuery', '批量查询', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'POST',
        '/black/gray/batch/batch/query', '2'),
       ('blackGrayBaseInfoDistinct', '风控系统查询黑灰名单库按企业汇总列表', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'POST',
        '/black/gray/base/info/distinct', '2'),
       ('blackGrayOrgList', '风控系统查询黑灰名单库机构下列表', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'POST',
        '/black/gray/org/list', '2'),
       ('blackGrayBaseInfoExport', '黑灰名单综合查询导出', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'GET',
        '/black/gray/base/info/export', '2'),
       ('blackGrayBaseInfoDetail', '风控系统查询黑灰名单库详情', (select id from bifrost_menu where code = 'blackListManagequeryallQuery'), 'POST',
        '/black/gray/base/info/detail', '2');
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('blackGrayWarehouseRecordAdd', '新增黑灰名单记录表', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/record/add', '2'),
       ('blackGrayWarehouseRecordDelete', '黑灰名单记录表删除', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/record/delete', '2'),
       ('blackGrayWarehouseRecordList', '黑灰名单记录表列表', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/record/list', '2'),
       ('blackGrayWarehouseRecordBatchModify', '批量补全黑灰名单集团信息', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/record/batch/modify', '2'),
       ('blackGrayBusinessTypeList', '业务类型树', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/business/type/list', '2'),
       ('blackGrayWarehouseAnalysisUpload', '解析黑灰名单上传记录表', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/analysis/upload', '2'),
       ('blackGrayWarehouseRecordUploadTemplateDownload', '黑灰名单上传模版下载', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'),
        'GET', '/black/gray/warehouse/record/upload/template/download', '2'),
       ('blackGrayWarehouseRecordExport', '黑灰名单导出', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'GET',
        '/black/gray/warehouse/record/export', '2'),
       ('blackGrayWarehouseRecordDetail', '黑灰名单记录表详情', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/record/detail', '2'),
       ('blackGrayWarehouseRecordModify', '修改黑灰名单记录表', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/record/modify', '2'),
       ('blackGrayWarehouseRecordBatchAdd', '批量新增黑灰名单记录表', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/record/batch/add', '2'),
       ('blackGrayWarehouseRecordUpload', '上传黑灰名单记录表', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/record/upload', '2');
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('blackGrayWarehouseRuleConfigAdd', '新增黑灰名单库-入库原因参数配置', (select id from bifrost_menu where code = 'blackListManageparameterswarehouse'),
        'POST', '/black/gray/warehouse/rule/config/add', '2'),
       ('blackGrayWarehouseRuleConfigRemove', '删除黑灰名单库-入库原因参数配置', (select id from bifrost_menu where code = 'blackListManageparameterswarehouse'),
        'POST', '/black/gray/warehouse/rule/config/remove', '2'),
       ('blackGrayWarehouseRuleConfigList', '黑灰名单库-入库原因参数配置列表', (select id from bifrost_menu where code = 'blackListManageparameterswarehouse'),
        'POST', '/black/gray/warehouse/rule/config/list', '2'),
       ('blackGrayWarehouseRuleConfigSwitch', '黑灰名单库-入库原因参数配置启用停用接口', (select id from bifrost_menu where code = 'blackListManageparameterswarehouse'),
        'POST', '/black/gray/warehouse/rule/config/switch', '2'),
       ('blackGrayWarehouseRuleConfigDetail', '黑灰名单库-入库原因参数配置详情', (select id from bifrost_menu where code = 'blackListManageparameterswarehouse'),
        'POST', '/black/gray/warehouse/rule/config/detail', '2'),
       ('blackGrayWarehouseRuleConfigModify', '修改黑灰名单库-入库原因参数配置', (select id from bifrost_menu where code = 'blackListManageparameterswarehouse'),
        'POST', '/black/gray/warehouse/rule/config/modify', '2');
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('blackGrayWarehouseTaskAdd', '新增黑灰名单任务表', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/task/add', '2'),
       ('blackGrayWarehouseTaskRemove', '删除黑灰名单任务表', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/task/remove', '2'),
       ('blackGrayWarehouseTaskList', '黑灰名单任务表列表', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/task/list', '2'),
       ('blackGrayWarehouseTaskDetail', '黑灰名单任务表详情', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/task/detail', '2'),
       ('blackGrayWarehouseTaskModify', '修改黑灰名单任务表', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/warehouse/task/modify', '2');


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('blackGrayFileList', '黑灰名单-文件', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/file/list', '2'),
       ('blackGrayFileUpload', '黑灰名单-文件上传', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/file/upload', '2'),
       ('blackGrayFileBatchRemove', '黑灰名单-文件删除', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/file/batch/remove', '2'),
       ('blackGrayFileDownload', '黑灰名单-文件下载', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'GET',
        '/file/download', '2');

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('blackGrayManualOutboundFileList', '黑灰名单出库-文件', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'POST',
        '/file/list', '2'),
       ('blackGrayManualOutboundFileUpload', '黑灰名单出库-文件上传', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'POST',
        '/file/upload', '2'),
       ('blackGrayManualOutboundFileBatchRemove', '黑灰名单出库-文件删除', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'POST',
        '/file/batch/remove', '2'),
       ('blackGrayManualOutboundFileDownload', '黑灰名单出库-文件下载', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'GET',
        '/file/download', '2');



INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES('queryVagueMain', '黑灰名单模糊查询企业信息-主任务', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/public/black/query/vague/enterprise', '2'),
       ('queryVagueOutbound', '黑灰名单模糊查询企业信息-出库', (select id from bifrost_menu where code = 'blackListManageoutboundapplication'), 'POST',
        '/public/black/query/vague/enterprise', '2');

UPDATE black_gray_warehouse_rule_config
SET suit_org = REPLACE(suit_org, '10000396', 'ZSZL');







