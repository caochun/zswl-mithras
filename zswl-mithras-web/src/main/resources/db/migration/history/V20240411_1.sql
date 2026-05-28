-- PC端审批流样式
INSERT INTO `bifrost_system_config` (`config_key`, `config_value`, `created_by`, `updated_by`, `description`, `status`, `type`)
VALUES
	('flow.ContractCreateFlow.userTask_chairman.ui.version', '1', '', '', '合同创建-董事长节点特殊审批样式', 1, 'String'),
	('flow.ContractModifyFlow.userTask_chairman.ui.version', '1', '', '', '合同其他变更-董事长节点特殊审批样式', 1, 'String'),
	('flow.ContractEarlySettleFlow.userTask_chairman.ui.version', '1', '', '', '合同提前结清-董事长节点特殊审批样式', 1, 'String'),
	('flow.ContractChangeRepayPlanFlow.userTask_chairman.ui.version', '1', '', '', '合同调整还款计划-董事长节点特殊审批样式', 1, 'String'),
	('flow.ContractEarlyRepayFlow.userTask_chairman.ui.version', '1', '', '', '合同部分还款-董事长节点特殊审批样式', 1, 'String'),
	('flow.ContractExtensionFlow.userTask_chairman.ui.version', '1', '', '', '合同展期-董事长节点特殊审批样式', 1, 'String'),
	('flow.PaymentCreateFlow.userTask_chairman.ui.version', '1', '', '', '付款申请-董事长节点特殊审批样式', 1, 'String'),
	('flow.RentCollectionExemptionFlow.userTask_chairman.ui.version', '1', '', '', '罚息减免-董事长节点特殊审批样式', 1, 'String');

-- 工作台数据导出
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('workbenchchartlistlaunchexport', '大可视化新增投放表格-导出', 0, 478, NULL, NULL, NULL, 'POST', '/workbench/chart/list/launch/export', 1, NULL),
	('workbenchchartlistreviewexport', '大可视化新增评审表格-导出', 0, 478, NULL, NULL, NULL, 'POST', '/workbench/chart/list/review/export', 1, NULL),
	('workbenchchartlistoverdueprojexport', '逾期项目列表-导出', 0, 478, NULL, NULL, NULL, 'POST', '/workbench/chart/list/overdue/proj/export', 1, NULL),
	('workbenchchartlistundesirableprojexport', '不良项目列表-导出', 0, 478, NULL, NULL, NULL, 'POST', '/workbench/chart/list/undesirable/proj/export', 1, NULL),
	('workbenchchartlistyearclientexport', '本年新增投放客户列表-导出', 0, 478, NULL, NULL, NULL, 'POST', '/workbench/chart/list/year/client/export', 1, NULL),
	('workbenchchartlistyearreviewexport', '本年新增项目评审列表-导出', 0, 478, NULL, NULL, NULL, 'POST', '/workbench/chart/list/year/review/export', 1, NULL),
	('workbenchchartlistyearpaymentexport', '本年/本月累计投放金额列表-导出', 0, 478, NULL, NULL, NULL, 'POST', '/workbench/chart/list/year/payment/export', 1, NULL),
	('workbenchchartlistyearprojexport', '本年/本月新增立项列表-导出', 0, 478, NULL, NULL, NULL, 'POST', '/workbench/chart/list/year/proj/export', 1, NULL);

-- 合同提前结清、正常结清、提前还款流程调整
INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`)
VALUES
	('job', '岗位类型', 'headofyyglb', '运营管理部负责人', 10);

-- 新增决议文件字段
ALTER TABLE `contract_guarantor` ADD COLUMN `resolution_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '决议类型' AFTER `guarantor_type`;
ALTER TABLE `contract_guarantor_lib` ADD COLUMN `resolution_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '决议类型' AFTER `guarantor_type`;
ALTER TABLE contract_tenantry ADD COLUMN resolution_type VARCHAR ( 100 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '决议类型' AFTER lessee_name;
ALTER TABLE contract_tenantry_lib ADD COLUMN resolution_type VARCHAR ( 100 ) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '决议类型' AFTER lessee_name;
-- 新增`租赁物文件类型`字段
ALTER TABLE `contract_tenantry` ADD COLUMN `lease_item_file_type` VARCHAR(50) COMMENT '租赁物文件类型' AFTER `lessee_name`;
ALTER TABLE `contract_tenantry_lib` ADD COLUMN `lease_item_file_type` VARCHAR(50) COMMENT '租赁物文件类型' AFTER `lessee_name`;
-- 新增 决议文件、租赁物文件、基础资料 文件模板类型
INSERT INTO `general_dictionary` (`id`,`dict_key`,`dict_desc`,`code`,`display`,`sort`) VALUES (NULL,'FILE_TEMPLATE_TYPE','文件模板类型','租赁物文件','租赁物文件',18);
INSERT INTO `general_dictionary` (`id`, `dict_key`, `dict_desc`, `code`, `display`, `sort`) VALUES (NULL, 'FILE_TEMPLATE_TYPE', '文件模板类型', '合同-决议文件', '合同-决议文件', 17);
INSERT INTO `general_dictionary` (`id`,`dict_key`,`dict_desc`,`code`,`display`,`sort`) VALUES (NULL,'FILE_TEMPLATE_TYPE','文件模板类型','基础资料','基础资料',19);
-- 配置表新增
INSERT INTO `file_authentication_config` (`id`,`file_name`,`file_type`,`owner_type`,`owner_post`,`owner_id`,`create_by`,`update_by`,`create_time`,`update_time`) VALUES (NULL,NULL,'租赁物文件',1,'admin',NULL,3,3,'2024-03-20 10:56:46','2024-03-20 10:56:46');
INSERT INTO `file_authentication_config` (`id`,`file_name`,`file_type`,`owner_type`,`owner_post`,`owner_id`,`create_by`,`update_by`,`create_time`,`update_time`) VALUES (NULL,NULL,'合同-决议文件',1,'admin',NULL,3,3,'2024-03-20 10:56:46','2024-03-20 10:56:46');
INSERT INTO `file_authentication_config` (`id`,`file_name`,`file_type`,`owner_type`,`owner_post`,`owner_id`,`create_by`,`update_by`,`create_time`,`update_time`) VALUES (NULL,NULL,'基础资料',1,'admin',NULL,3,3,'2024-03-20 10:56:46','2024-03-20 10:56:46');
-- 税率维护
UPDATE `kpi_parameter_config` SET `config_desc`='税率维护',`config_value`='[{\"bizType\":\"ZL_ZZ\",\"configValueType\":\"VALUE\",\"taxRate\":\"1.6\",\"taxType\":\"ZZS\"},{\"bizType\":\"ZL_HZ\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"ZZS\"},{\"bizType\":\"ZL_JYX\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"ZZS\"},{\"bizType\":\"BL\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"ZZS\"},{\"bizType\":\"ZR\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"ZZS\"},{\"bizType\":\"\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"CJS\"},{\"bizType\":\"\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"JYFJS\"},{\"bizType\":\"\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"DFJYFJS\"},{\"bizType\":\"RZZLHT\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"YHS\"},{\"bizType\":\"ZXHT\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"YHS\"},{\"bizType\":\"CGJXSHT\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"YHS\"},{\"bizType\":\"JYZLHT\",\"configValueType\":\"VALUE\",\"taxRate\":\"5\",\"taxType\":\"YHS\"},{\"bizType\":\"ZL_ZZ\",\"configValueType\":\"VALUE\",\"taxRate\":\"13\",\"taxType\":\"XMS\"},{\"bizType\":\"ZL_HZ\",\"configValueType\":\"VALUE\",\"taxRate\":\"6\",\"taxType\":\"XMS\"},{\"bizType\":\"ZL_JYX\",\"configValueType\":\"VALUE\",\"taxRate\":\"6\",\"taxType\":\"ZXS\"}]',`create_time`='2023-02-17 10:58:57',`create_by`=NULL,`update_time`='2023-11-22 11:29:10',`update_by`=3 WHERE `config_code`='TAX_RATE';

-- 放款底稿
INSERT INTO `file_authentication_config` (`file_name`, `file_type`, `owner_type`, `owner_post`, `owner_id`, `create_by`, `update_by`, `create_time`, `update_time`)
VALUES (NULL, '付款-放款底稿', 1, 'admin', NULL, 3, 3, '2024-03-20 10:56:46', '2024-03-20 10:56:46');

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('paymentloanReviewFile', '放款审核文件生成', 0, 15, NULL, NULL, NULL, 'POST', '/payment/loanReviewFile', 2, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ('paymentloanReviewFilelist', '付款底稿查询', 0, 15, NULL, NULL, NULL, 'POST', '/payment/loanReviewFile/list', 1,NULL );

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ( 'paymentloanReviewFiledownloadtemplate', '放款审核模板下载', 0, 15, NULL, NULL, NULL, 'POST', '/payment/loanReviewFile/download/template', 2, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES ( 'fileuploadloanReviewFile', '付款底稿上传', 0, 15, NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL);

-- 管报类型增加排序
alter table management_report add column sort_num int(10) not null default 0 comment '排序字段';

-- 自动起租
ALTER TABLE contract_lease_price add column structured_interest json COMMENT '结构化利息' after first_installment_interest;
ALTER TABLE contract_lease_price_lib add column structured_interest json COMMENT '结构化利息' after first_installment_interest;

ALTER TABLE contract_aoc_price add column structured_interest json COMMENT '结构化利息' after consulting_fee;
ALTER TABLE contract_aoc_price_lib add column structured_interest json COMMENT '结构化利息' after consulting_fee;

ALTER TABLE contract_factoring_price add column structured_interest json COMMENT '结构化利息' after consulting_fee;
ALTER TABLE contract_factoring_price_lib add column structured_interest json COMMENT '结构化利息' after consulting_fee;

ALTER TABLE payment_base_info add column lowest_irr INT(11) COMMENT '最低irr' after nominal_price;
ALTER TABLE payment_base_info_lib add column lowest_irr INT(11) COMMENT '最低irr' after nominal_price;

ALTER TABLE payment_base_info add column default_collection_day INT(11) COMMENT '默认收款日' after lowest_irr;
ALTER TABLE payment_base_info_lib add column default_collection_day INT(11) COMMENT '默认收款日' after lowest_irr;

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES
('paymentCollectionDay', '获取付款对应收款日', 0, 16, NULL, NULL, NULL, 'POST', '/payment/collection/day', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES
('paymentCollectionDayModify', '修改付款对应收款日', 0, 16, NULL, NULL, NULL, 'POST', '/payment/collection/day/modify', 2, NULL);


-- 待发起相关
-- 流程配置、定时任务
-- auto-generated definition
create table common_process_prepare
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    process_type     varchar(255)                       null comment '流程类型',
    business_id      varchar(50)                        DEFAULT NULL COMMENT '业务主键',
    business_data    varchar(50)                        null comment '业务数据',
    form_name        varchar(255)                       null comment '表单名称',
    proj_name        varchar(255)                       null comment '项目名称',
    proj_code        varchar(255)                       null comment '项目编号',
    client_name      varchar(255)                       null comment '客户名称',
    current_node     varchar(255)                       null comment '当前节点',
    current_assignee json                               null comment '当前审批人',
    apply_time       datetime                           null comment '申请时间',
    status           varchar(20)                        null comment '状态',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    create_by        bigint                             null comment '创建人',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by        bigint                             null comment '更新人'
)
    charset = utf8mb4;

-- auto-generated definition
create table rent_collection_month_detail
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    collection_id       bigint                             null comment '收款collectionBaseInfo 的id',
    pledge_id           bigint                             null comment '质押关联id',
    financing_type      varchar(20)                        null comment '融资类型',
    dept_id             bigint                             null comment '部门id',
    month               int                                null comment '月份',
    prepare_id          bigint                             null comment '预备表id',
    client_id           bigint                             null comment '客户id',
    sponsor_id          bigint                             null comment '主办id',
    contract_code       varchar(255)                       null comment '合同编号',
    phase               int                                null comment '期项',
    repay_date          date                               null comment '租金支付日',
    rent                bigint                             null comment '租金',
    principal           bigint                             null comment '本金',
    interest            bigint                             null comment '利息',
    bank_account_name   varchar(255)                       null comment '银行开户账户名',
    bank_account_number varchar(255)                       null comment '银行开户账户号',
    bank_name           varchar(255)                       null comment '开户行名称',
    initial_data        json                               null comment '最初的样子',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    create_by           bigint                             null comment '创建人',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by           bigint                             null comment '更新人'
);

-- 待发起-查看类功能
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'process_prepare_list','待发起流程列表',0,b.id,'POST','/process/prepare/list',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
--
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'process_prepare_detail','待发起流程详情',0,b.id,'POST','/process/prepare/detail',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
-- --
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'rentnotify_process_prepare_detail_list','租金支付通知书-待发起-详情-列表',0,b.id,'POST','/process/prepare/detail/list',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
-- --
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'rentnotify_process_prepare_detail_list_download','租金支付通知书-待发起-下载全量清单',0,b.id,'POST','/process/prepare/detail/list/download',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
-- --
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'rentnotify_process_prepare_detail_single_preview','租金支付通知书-待发起-单个预览',0,b.id,'POST','/process/prepare/detail/single/preview',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
--
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'rentnotify_process_prepare_detail_single_download','租金支付通知书-待发起-单个下载',0,b.id,'POST','/process/prepare/detail/single/download',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';
--
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'rentnotify_process_prepare_detail_batch_download','租金支付通知书-待发起-批量下载通知单',0,b.id,'POST','/process/prepare/detail/batch/download',1,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-查看';


-- 待发起-编辑类功能
--
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'process_prepare_commit','待发起-确认',0,b.id,'POST','/process/prepare/commit',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
--
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'process_prepare_discard','待发起-取消',0,b.id,'POST','/process/prepare/discard',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';
--
INSERT INTO bifrost_function(code,name,sort_no,menu_id,method,path,type,group_id)
SELECT 'rentnotify_process_prepare_refresh','租金支付通知书-待发起-刷新租金信息',0,b.id,'POST','/process/prepare/refresh',2,c.id
FROM bifrost_menu b, gruul_function_group c where b.name = '我收到的' and c.group_describe='我收到的-编辑';



-- 待发起-权限配置
-- 领导层>财务总监、首席风险官、总经理、综合管理
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '领导层'
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('财务总监', '首席风险官', '总经理', '综合管理')
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
-- 化工建材业务部>化工建材业务部主管
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '化工建材业务部'
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('化工建材业务部主管')
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
-- 冷链物流团队>冷链物流团队主管
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '冷链物流团队'
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('冷链物流团队主管')
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
-- 智能制造业务部>智能制造业务部主管
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '智能制造业务部'
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('智能制造业务部主管')
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
-- 浙江业务部>浙江业务部主管
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '浙江业务部'
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('浙江业务部主管')
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
-- 交通物流业务部>交通物流业务部主管
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '交通物流业务部'
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('交通物流业务部主管')
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
-- 计划财务部>财务主管
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '计划财务部'
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('财务主管')
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';

-- 资金管理部>财务资金管理，资金管理
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '资金管理部'
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('财务资金管理','资金管理')
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';

-- 浙江浙商融资租赁有限公司>团队长
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '浙江浙商融资租赁有限公司'
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('团队长')
  and function.path like '/process/prepare%' and function.path != '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
-- 刷新按钮只有财务和资金能够点击
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '计划财务部'
  and function.path = '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('财务主管')
  and function.path = '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
--
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, function.id, tree.id
FROM bifrost_org org,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '资金管理部'
  and function.path = '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, function.id, tree.id
FROM bifrost_role role,
     bifrost_function function,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.code IN ('ZJ-CWZJGL')
  and function.path = '/process/prepare/refresh'
  and menu.name = '我收到的'
  and tree.name = '我的流程';


-- 让财务主管，能看到【我发起的】
INSERT INTO bifrost_org_menu_function(org_id, menu_id, function_id, custom_tree_id)
SELECT org.id, menu.id, null, tree.id
FROM bifrost_org org,
     bifrost_menu menu,
     bifrost_custom_tree tree
where org.name = '计划财务部'
  and menu.name = '我发起的'
  and tree.name = '我的流程';
INSERT INTO bifrost_role_menu_function(role_id, menu_id, function_id, custom_tree_id)
SELECT role.id, menu.id, null, tree.id
FROM bifrost_role role,
     bifrost_menu menu,
     bifrost_custom_tree tree
WHERE role.name IN ('财务主管')
  and menu.name = '我发起的'
  and tree.name = '我的流程';


