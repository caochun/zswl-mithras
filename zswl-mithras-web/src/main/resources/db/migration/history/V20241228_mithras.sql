-- 运营管报开始 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
alter table management_report add column report_source varchar(50) default null comment '报表来源';
alter table management_report add column report_key varchar(50) default null comment '报表key（用于自研页面管报的前端识别）';
-- 历史数据补充source，理论上都是guanyuan配置的
update management_report set report_source = 'GUAN_YUAN' where report_source is null;
-- 历史数据补充key（历史的不关注该字段，能唯一就行，采用固定前缀+id生成好了）
update management_report set report_key = concat('REPORT_', id) where report_key is null;
-- 运营的报表使用新的树形结构，需要指定report_type_name
update management_report set report_type_name = '运营报表' where report_type = 'yunying';
-- 本次需求新增的运营报表
INSERT INTO `management_report` (`report_name`, `report_url`, `report_type`, `sort_num`, `report_type_name`, `report_key`, `report_source`)
VALUES
    ('业务运行分析表', '', 'yunying', 0, '运营报表', 'YE_WU_YUN_XING_FEN_XI', 'MITHRAS'),
    ('运营待办管理表', '', 'yunying', 0, '运营报表', 'YUN_YING_DAI_BAN', 'MITHRAS'),
    ('合同退回分析表', 'http://10.158.11.178/page/e19dda864cc7540898d57de1', 'yunying', 0, '运营报表', 'HE_TONG_TUI_HUI_FEN_XI', 'GUAN_YUAN');
-- 调整菜单名称和路径
update `bifrost_menu` set `name` = '管理报表-历史', `path` = '/report/internalHistory' where `name` = '管理报表' and `code` = 'reportinternal';
update `bifrost_menu` set `name` = '管理报表', `path` = '/report/management' where `name` = '财务管报' and `code` = 'reportFinancial';
-- 运营管报结束 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

#===================数据风控应用===================
create table bill_overdue
(
    id                 bigint auto_increment comment 'id'
    primary key,
    seq_no             varchar(32)                        null comment '序号',
    org_code           varchar(64)                        null comment '机构编号',
    org_name           varchar(128)                       null comment '机构名称',
    org_type           varchar(32)                        null comment '机构类型 企业/金融机构',
    overdue_start_date varchar(32)                        null comment '持续逾期开始日期 yyyy-mm-dd',
    busi_date          varchar(32)                        null comment '业务日期  yyyy-MM-dd',
    create_by          varchar(32)                        null comment '创建人',
    update_by          varchar(32)                        null comment '更新人',
    create_time        datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        datetime default CURRENT_TIMESTAMP null comment '更新时间'
) comment '票据逾期表';

create table if not exists risk_control_warn_monitor
(
    id            bigint auto_increment comment 'id'
    primary key,
    warn_code     varchar(30)                        null comment '预警编码',
    chi_name      varchar(200)                       null comment '客户名称',
    title         varchar(500)                       null comment '标题',
    credit_code   varchar(50)                        null comment '统一社会信用代码',
    data_time     datetime                           null comment '预警日期',
    link_address  varchar(200)                       null comment '链接地址',
    warn_level    int                                null comment '预警信号：1：绿灯；2：黄灯；3：红灯',
    handle_status varchar(20)                        null comment '处理状态',
    advisement    longtext                           null comment '处置意见',
    noticed       tinyint(1)                         null comment '是否通知',
    notice_time   datetime                           null comment '最近通知时间',
    rule_code     varchar(30)                        null comment '去重字段',
    risk_type     varchar(50)                        null comment '风险类型',
    risk_class    varchar(30)                        null comment '风险分类',
    create_by     bigint                             null comment '创建人id、发起人id	',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by     bigint                             null comment '最后更新人id	',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	'
    ) comment '风控预警监测';


-- 票据逾期
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('overdueListSearch', 1, 25, '0', '/overdueListSearch', null, null, '上海票交所逾期名单');
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueList', '上海票交所逾期名单', 0, id, 'post', '/overdueList/list', null
from bifrost_menu
where code = 'overdueListSearch';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueListImport', '上海票交所逾期名单', 0, id, 'post', '/overdueList/import', null
from bifrost_menu
where code = 'overdueListSearch';

-- 视图 && 预警
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('monitorEarly', 1, 3, '0', '/monitorEarly', null, null, '预警监测');
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '风控管理') a
         join (select id as menu_id from bifrost_menu where code = 'monitorEarly') b;

INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('customerView', 1, 5, '0', '/customerView', null, null, '统一视图');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManagequeryallQuery', 1, 15, '0', '/blackListManage/query/allQuery', null, null, '灰黑名单全量查询');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('blackListManagequeryrecognize', 1, 20, '0', '/blackListManage/query/recognize', null, null, '灰黑名单企业识别');

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '客户管理') a
         join (select id as menu_id from bifrost_menu where code = 'customerView') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '客户管理') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManagequeryallQuery') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '客户管理') a
         join (select id as menu_id from bifrost_menu where code = 'blackListManagequeryrecognize') b;

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '客户管理') a
         join (select id as menu_id from bifrost_menu where code = 'overdueListSearch') b;


update bifrost_menu set sort_no = 6 where code = 'customerCustomerRat';


-- 流动性管理接口注册
INSERT INTO bifrost_function (code, name, menu_id, method, path, type) VALUES
                                                                           ('liquidityAccountBalanceList','账户余额明细列表','481','POST','/liquidity/accountBalance/list','2'),
                                                                           ('liquidityAccountBalanceModify','账户余额明细编辑','481','POST','/liquidity/accountBalance/modify','2'),
                                                                           ('liquidityAccountBalanceImport','账户余额明细导入','481','POST','/liquidity/accountBalance/import','2'),
                                                                           ('liquidityAccountSettingList','回款账户配置列表','481','POST','/liquidity/accountSetting/list','2'),
                                                                           ('liquidityAccountSettingModify','回款账户配置编辑','481','POST','/liquidity/accountSetting/modify','2'),
                                                                           ('liquidityAccountSettingRestore','回款账户配置账户还原','481','POST','/liquidity/accountSetting/restore','2'),
                                                                           ('liquiditySettingParameterBaseDetail','基础参数配置详情','481','POST','/liquidity/setting/parameterBase/detail','2'),
                                                                           ('liquiditySettingParameterBaseModify','基础参数配置编辑','481','POST','/liquidity/setting/parameterBase/modify','2'),
                                                                           ('liquiditySettingParameterIndexDetail','流动性指标配置详情','481','POST','/liquidity/setting/parameterIndex/detail','2'),
                                                                           ('liquiditySettingParameterIndexModify','流动性指标配置编辑','481','POST','/liquidity/setting/parameterIndex/modify','2'),
                                                                           ('liquidityManageMismatch','错配明细','481','POST','/liquidity/manage/mismatch','2'),
                                                                           ('liquidityManageBoard','流动性看板','481','POST','/liquidity/manage/board','2'),
                                                                           ('liquidityManageIndex','流动性指标','481','POST','/liquidity/manage/index','2'),
                                                                           ('accountBalanceIndexDownload','账户余额表导出','481','POST','/index/download','2'),
                                                                           ('liquidityBoardIndexDownload','流动性看板导出','481','POST','/index/download','2'),
                                                                           ('liquidityMismatchIndexDownload','错配明细导出','481','POST','/index/download','2'),
                                                                           ('accountSettingIndexDownload','回款账户导出','481','POST','/index/download','2'),
                                                                           ('fundDayReportRepayPrincipalInterest', '还本付息', '481', 'POST', '/fundDayReport/repay/principalInterest','2'),
                                                                           ('fundDayReportIndicatorList', '日结指标', '481', 'POST', '/fundDayReport/indicator/list', '2'),
                                                                           ('fundDayReportAccountBalance', '账户余额', '481', 'POST', '/fundDayReport/account/balance', '2'),
                                                                           ('fundDayReportRentIncome', '租金流入', '481', 'POST', '/fundDayReport/rent/income', '2');

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES
    ('2024-07-04 10:13:12', '2024-12-26 14:12:38', NULL, 'liquidityAccountBalanceFileDownloadTemplate', '账户余额表模版下载', 0, 481, NULL, NULL, NULL, 'GET', '/file/download/template', 1, NULL);


update bifrost_menu set path = '/financial/liquidity' where id = 481;


CREATE TABLE `account_balance_base_info` (
                                             `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
                                             `date` datetime NOT NULL COMMENT '数据时点',
                                             `account_id` bigint(20) NOT NULL COMMENT '账户基本表id',
                                             `account_bank` varchar(255) DEFAULT NULL COMMENT '开户银行',
                                             `account_number` varchar(255) DEFAULT NULL COMMENT '银行账号',
                                             `account_type` varchar(255) DEFAULT NULL COMMENT '账户性质',
                                             `drawings_amount` bigint(20) DEFAULT NULL COMMENT '提款(编辑字段)',
                                             `rent_reflow_amount` bigint(20) DEFAULT NULL COMMENT '租金回流',
                                             `other_flow_amount` bigint(20) DEFAULT NULL COMMENT '其他流入(编辑字段)',
                                             `payment_amount` bigint(20) DEFAULT NULL COMMENT '投放(编辑字段)',
                                             `repay_amount` bigint(20) DEFAULT NULL COMMENT '还本付息',
                                             `repay_edit_amount` bigint(20) DEFAULT NULL COMMENT '还本付息-调整(编辑字段)',
                                             `repay_abs_amount` bigint(20) DEFAULT NULL COMMENT '还本付息-abs',
                                             `repay_no_abs_amount` bigint(20) DEFAULT NULL COMMENT '还本付息-非abs',
                                             `must_expense_amount` bigint(20) DEFAULT NULL COMMENT '刚性支出(编辑字段)',
                                             `other_expense_amount` bigint(20) DEFAULT NULL COMMENT '其他支出(编辑字段)',
                                             `estimate_balance_amount` bigint(20) DEFAULT NULL COMMENT '结余-预估',
                                             `estimate_balance_limit_amount` bigint(20) DEFAULT NULL COMMENT '结余受限-预估(系统取值)',
                                             `estimate_balance_limit_edit_amount` bigint(20) DEFAULT NULL COMMENT '结余受限-预估(编辑字段)',
                                             `actual_balance_amount` bigint(20) DEFAULT NULL COMMENT '结余-实际(编辑字段)',
                                             `diff_amount` bigint(20) DEFAULT NULL COMMENT '差额',
                                             `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                             `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                             PRIMARY KEY (`id`),
                                             KEY `index_account_id` (`account_id`) COMMENT '账户id索引'
) ENGINE=InnoDB AUTO_INCREMENT=121739 DEFAULT CHARSET=utf8mb4 COMMENT='账户余额表';

CREATE TABLE `fund_financing_account_setting` (
                                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
                                                  `financing_id` bigint(20) NOT NULL COMMENT '融资id',
                                                  `financing_type` varchar(255) DEFAULT NULL COMMENT '融资类型',
                                                  `account_id` bigint(20) NOT NULL COMMENT '账户基本表id',
                                                  `account_category` varchar(255) DEFAULT NULL COMMENT '账户类别',
                                                  `account_bank` varchar(255) DEFAULT NULL COMMENT '开户银行',
                                                  `account_number` varchar(255) DEFAULT NULL COMMENT '银行账号',
                                                  `account_type` varchar(255) DEFAULT NULL COMMENT '账户性质',
                                                  `account_use` varchar(255) DEFAULT NULL COMMENT '账户用途',
                                                  `simulate_settle` tinyint(4) DEFAULT NULL COMMENT '是否模拟结清',
                                                  `settle_time` datetime DEFAULT NULL COMMENT '模拟结清日期',
                                                  `settle_amount` bigint(20) DEFAULT NULL COMMENT '模拟结清金额',
                                                  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                                  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                                  `account_origin_id` bigint(20) NOT NULL COMMENT '原数据账号id',
                                                  PRIMARY KEY (`id`),
                                                  KEY `index_financing_id` (`financing_id`) USING BTREE COMMENT '融资id索引',
                                                  KEY `index_account_id` (`account_id`) USING BTREE COMMENT '账户id索引'
) ENGINE=InnoDB AUTO_INCREMENT=604 DEFAULT CHARSET=utf8mb4 COMMENT='回款账户配置表';


CREATE TABLE `fund_parameter_config` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `config_code` varchar(50) NOT NULL DEFAULT '' COMMENT '参数code',
                                         `config_desc` varchar(50) NOT NULL DEFAULT '' COMMENT '参数描述',
                                         `config_value` text NOT NULL COMMENT '参数值',
                                         `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='资金基础参数配置';


INSERT INTO `fund_parameter_config` (`id`, `config_code`, `config_desc`, `config_value`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES (1, 'liquidityBase', '基础参数', '{\"flexibleCredit\":200000000000,\"saveStock\":1000000000000}', NULL, '2024-12-20 11:15:05', NULL, '2024-12-26 14:05:26', 0);
INSERT INTO `fund_parameter_config` (`id`, `config_code`, `config_desc`, `config_value`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES (3, 'accountBalanceCalculateTime', '账户余额表计算月数', '24', NULL, '2024-12-22 11:38:03', NULL, '2024-12-27 18:19:05', 0);
INSERT INTO `fund_parameter_config` (`id`, `config_code`, `config_desc`, `config_value`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES (4, 'liquidityIndex', '流动性参数', '[{\"indexDisplay\":\"资产负债久期比\",\"indexName\":\"assetLiabilityDurationRatio\",\"redSign\":\">=\",\"redValue\":0.69,\"yellowSign\":\">=\",\"yellowValue\":0.65},{\"indexDisplay\":\"流动性覆盖率\",\"indexName\":\"liquidityCoverageRatio\",\"redSign\":\"<=\",\"redValue\":0.7,\"yellowSign\":\"<=\",\"yellowValue\":1},{\"indexDisplay\":\"流动性缺口率\",\"indexName\":\"liquidityGapRate\",\"redSign\":\">=\",\"redValue\":0.1,\"yellowSign\":\">=\",\"yellowValue\":0.1},{\"indexDisplay\":\"可用授信比\",\"indexName\":\"availableCreditRatio\",\"redSign\":\"<\",\"redValue\":0.35,\"yellowSign\":\"<\",\"yellowValue\":0.35}]', NULL, '2024-12-20 11:15:21', NULL, '2024-12-26 19:10:39', 0);
INSERT INTO `fund_parameter_config` (`id`, `config_code`, `config_desc`, `config_value`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES (5, 'accountBalanceStartTime', '账户余额表计算开始日', '2024-12-01', NULL, '2024-12-22 11:38:03', NULL, '2024-12-27 18:19:16', 0);


-- 舆情监控开始 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('clientUnifiedViewList', '获取的客户列表', (select id from bifrost_menu where code = 'customerView'), 'POST', '/client/unified/view/list', '2'),
       ('clientUnifiedViewDetail', '获取的客户详情', (select id from bifrost_menu where code = 'customerView'), 'POST', '/client/unified/view/detail', '2'),
       ('clientUnifiedViewApplyCredit', '获取客户授信信息', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/apply/credit', '2'),
       ('clientUnifiedViewApplyCreditHistory', '获取客户授信历史', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/apply/credit/history', '2'),
       ('clientUnifiedViewProjList', '获取客户项目列表', (select id from bifrost_menu where code = 'customerView'), 'POST', '/client/unified/view/proj/list',
        '2'),
       ('clientUnifiedViewContractList', '获取客户合同列表', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/contract/list', '2'),
       ('clientUnifiedViewCustomerTrends', '获取客户统一折线图', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/customer/trends', '2'),
       ('clientUnifiedViewRatingHistory', '获取客户历史评级', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/rating/history', '2');

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('riskWarnMonitorQuantityChange', '监控预警-风险数量变化', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/risk/warn/monitor/quantity/change', '2'),
       ('riskWarnMonitorOpinionList', '监控预警-舆情列表', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/risk/warn/monitor/opinion/list', '2'),
       ('riskWarnMonitorStatistics', '监控预警-统计', (select id from bifrost_menu where code = 'monitorEarly'), 'POST', '/risk/warn/monitor/statistics',
        '2'),
       ('riskWarnMonitorTypeChange', '监控预警-风险类型占比', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/risk/warn/monitor/type/change', '2'),
       ('riskControlBlackGrayBaseInfoLibrary', '监控预警-查询客户黑灰名单', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/black/gray/base/info/library', '2'),
       ('opinionWarnOrgSelect', '监控预警-查询部门', (select id from bifrost_menu where code = 'monitorEarly'), 'GET',
        '/select/orgs', '2'),
       ('riskWarnMonitorWarnList', '监控预警-预警列表', (select id from bifrost_menu where code = 'monitorEarly'), 'POST', '/risk/warn/monitor/warn/list',
        '2');

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('riskWarnMonitorWarnDetail', '监控预警-预警列表', (select id from bifrost_menu where code = 'riskpublicMonitor'), 'POST', '/risk/warn/monitor/warn/detail','2');


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('dashboardprojectstagestatisticsUnified', '项目视图-项目阶段-统计', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/dashboard/project/stage/statistics', '2'),
       ('RiskControlSelectOrgs', '监控预警-查询部门1', (select id from bifrost_menu where code = 'monitorEarly'), 'GET','/select/orgs', '2');


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('riskWarnFileList', '监控预警-文件', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/file/list', '2'),
       ('riskWarnFileUpload', '监控预警-文件上传', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/file/upload', '2'),
       ('riskWarnFileBatchRemove', '监控预警-文件删除', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/file/batch/remove', '2'),
       ('riskWarnFileDownload', '监控预警-文件下载', (select id from bifrost_menu where code = 'monitorEarly'), 'GET',
        '/file/download', '2');

INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('clientUnifiedViewOverdueRent', '获取客户逾期租金', (select id from bifrost_menu where code = 'customerView'), 'POST',
        '/client/unified/view/overdue/rent', '2');


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('riskWarnMonitorModify', '监控预警-预警信息修改', (select id from bifrost_menu where code = 'monitorEarly'), 'POST',
        '/risk/warn/monitor/modify', '2');

ALTER TABLE risk_control_opinion_monitor
    ADD handle_result tinyint(1) DEFAULT NULL COMMENT '处置方式 0 处理， 1 关闭';
ALTER TABLE risk_control_warn_monitor
    ADD handle_result tinyint(1) DEFAULT NULL COMMENT '处置方式 0 处理， 1 关闭';

-- 舆情监控end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


-- 黑灰名单 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

CREATE TABLE `black_gray_break_business`
(
    `id`                         bigint(20) NOT NULL AUTO_INCREMENT,
    `black_gray_id`              bigint(20) DEFAULT NULL COMMENT '黑灰名单ID',
    `enterprise_name`            varchar(100) NOT NULL COMMENT '企业名称',
    `unified_social_credit_code` varchar(50)  NOT NULL COMMENT '统一社会信用代码',
    `black_gray_type`            varchar(20)  NOT NULL COMMENT '黑灰标识',
    `business_type`              varchar(20)    DEFAULT NULL COMMENT '业务类型',
    `proposed_business_type`     varchar(50)    DEFAULT NULL COMMENT '拟开展业务类型',
    `plan_outbound_time`         date           DEFAULT NULL COMMENT '原计划出库时间',
    `propose_business_scale`     decimal(20, 5) DEFAULT NULL COMMENT '拟开展业务规模（万元）',
    `apply_dept`                 varchar(20)    DEFAULT NULL COMMENT '申请部门',
    `apply_organization`         varchar(20)    DEFAULT NULL COMMENT '申请机构',
    `apply_reason`               varchar(1000)  DEFAULT NULL COMMENT '申请原因',
    `apply_file_keys`            text COMMENT '申请文件keys',
    `audit_status`               int(11) DEFAULT NULL COMMENT '突破流程状态',
    `create_by`                  bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                  bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                          `black_gray_break_business_black_gary_id` (`black_gray_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `black_gray_business_dict`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `org_id`        bigint(20) DEFAULT NULL COMMENT '机构ID',
    `org_name`      varchar(50) DEFAULT NULL COMMENT '机构名称',
    `business_name` varchar(50) DEFAULT NULL COMMENT '业务类型名称',
    `business_desc` varchar(50) DEFAULT NULL COMMENT '业务类型描述',
    `level`         int(11) NOT NULL DEFAULT '0' COMMENT '层级 0金控定义',
    `parent_id`     bigint(20) DEFAULT NULL COMMENT '父ID',
    `main_id`       bigint(20) DEFAULT NULL COMMENT '所属金控类型主ID，即一级ID',
    `create_time`   datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='黑灰名单库';

CREATE TABLE `black_gray_library`
(
    `id`                         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `enterprise_name`            varchar(100) NOT NULL COMMENT '企业名称',
    `unified_social_credit_code` varchar(50)  NOT NULL COMMENT '统一社会信用代码',
    `business_type`              varchar(20)  NOT NULL COMMENT '业务类型',
    `data_digest`                varchar(255)   DEFAULT NULL COMMENT '黑灰名单数据摘要（企业名称+统一社会信用代码+业务类型）加密后获得',
    `period_under_observation`   varchar(20)    DEFAULT NULL COMMENT '观察期',
    `risk_scale`                 decimal(20, 5) DEFAULT NULL COMMENT '风险规模（万元）',
    `membership_group`           varchar(100)   DEFAULT NULL COMMENT '所属集团',
    `blacklist_status`           tinyint(1) DEFAULT NULL COMMENT '集团是否纳入黑名单',
    `apply_reason_type`          varchar(200)   DEFAULT NULL COMMENT '申请原因类型',
    `apply_reason`               varchar(1000)  DEFAULT NULL COMMENT '申请原因',
    `black_gray_type`            varchar(20)    DEFAULT NULL COMMENT '黑灰标识',
    `group_black_gray_type`      varchar(20)    DEFAULT NULL COMMENT '集团黑灰标识',
    `apply_time`                 date           DEFAULT NULL COMMENT '申请时间',
    `apply_organization`         varchar(50)    DEFAULT NULL COMMENT '申请机构',
    `apply_dept`                 varchar(50)    DEFAULT NULL COMMENT '申请部门',
    `warehouse_time`             date           DEFAULT NULL COMMENT '入库时间',
    `plan_outbound_time`         date           DEFAULT NULL COMMENT '出库时间',
    `warehouse_reason`           varchar(255)   DEFAULT NULL COMMENT '入库原因',
    `warehouse_file_keys`        text COMMENT '入库文件key',
    `rectify_file_keys`          text COMMENT '整改文件key',
    `share_type`                 tinyint(1) DEFAULT '0' COMMENT '共享类型 0金融企业黑名单， 1金控黑名单',
    `record_id`                  bigint(20) DEFAULT NULL COMMENT '黑灰记录id',
    `stock_status`               tinyint(1) DEFAULT '0' COMMENT '0在库，1出库',
    `source`                     varchar(20)    DEFAULT NULL COMMENT '黑名单来源',
    `create_by`                  bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                  bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                          `black_gray_library_enterprise` (`enterprise_name`,`unified_social_credit_code`,`business_type`) USING BTREE,
    KEY                          `black_gray_library_apply_organization` (`apply_organization`) USING BTREE,
    KEY                          `black_gray_library_apply_business_type` (`business_type`) USING BTREE,
    KEY                          `black_gray_library_unified_social_credit_code` (`unified_social_credit_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=516 DEFAULT CHARSET=utf8mb4 COMMENT='黑灰名单库';

CREATE TABLE `black_gray_manual_outbound`
(
    `id`                         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `black_gray_id`              bigint(20) DEFAULT NULL COMMENT '黑灰名单ID',
    `enterprise_name`            varchar(100) NOT NULL COMMENT '企业名称',
    `unified_social_credit_code` varchar(50)  NOT NULL COMMENT '统一社会信用代码',
    `business_type`              varchar(20)  NOT NULL COMMENT '业务类型',
    `black_gray_type`            varchar(20)   DEFAULT NULL COMMENT '黑灰标识',
    `source`                     varchar(30)   DEFAULT NULL COMMENT '来源',
    `warehouse_time`             date          DEFAULT NULL COMMENT '入库时间',
    `plan_outbound_time`         date          DEFAULT NULL COMMENT '出库时间',
    `apply_organization`         varchar(50)   DEFAULT NULL COMMENT '申请机构',
    `warehouse_organization`     varchar(50)   DEFAULT NULL COMMENT '入库机构',
    `apply_dept`                 varchar(50)   DEFAULT NULL COMMENT '申请部门',
    `apply_reason`               varchar(2000) DEFAULT NULL COMMENT '申请原因',
    `apply_file_keys`            text COMMENT '申请文件keys',
    `audit_status`               int(11) DEFAULT NULL COMMENT '记录状态',
    `create_by`                  bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                  bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                          `black_gray_manual_outbound_enterpris` (`enterprise_name`,`unified_social_credit_code`,`business_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='黑灰名单人工出库表';

CREATE TABLE `black_gray_warehouse_record`
(
    `id`                         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `enterprise_name`            varchar(100) NOT NULL COMMENT '企业名称',
    `unified_social_credit_code` varchar(50)  NOT NULL COMMENT '统一社会信用代码',
    `business_type`              varchar(20)  NOT NULL COMMENT '业务类型',
    `customise_business_type`    varchar(50)    DEFAULT NULL COMMENT '金融企业业务类型业务类型',
    `period_under_observation`   varchar(20)    DEFAULT NULL COMMENT '观察期',
    `risk_scale`                 decimal(20, 5) DEFAULT NULL COMMENT '风险规模（万元）',
    `membership_group`           varchar(100)   DEFAULT NULL COMMENT '所属集团',
    `blacklist_status`           tinyint(1) DEFAULT '0' COMMENT '集团是否纳入黑名单 0否 1纳入黑名单',
    `apply_reason_type`          varchar(200)   DEFAULT NULL COMMENT '申请原因类型',
    `apply_reason`               varchar(1000)  DEFAULT NULL COMMENT '申请原因',
    `black_gray_type`            varchar(20)    DEFAULT NULL COMMENT '黑灰标识',
    `group_black_gray_type`      varchar(20)    DEFAULT NULL COMMENT '集团黑灰标识',
    `apply_time`                 date           DEFAULT NULL COMMENT '申请时间',
    `apply_organization`         varchar(50)    DEFAULT NULL COMMENT '申请机构',
    `apply_dept`                 varchar(50)    DEFAULT NULL COMMENT '申请部门',
    `warehouse_time`             date           DEFAULT NULL COMMENT '入库时间',
    `plan_outbound_time`         date           DEFAULT NULL COMMENT '出库时间',
    `warehouse_reason`           varchar(255)   DEFAULT NULL COMMENT '入库原因',
    `warehouse_file_keys`        text COMMENT '入库文件key',
    `rectify_file_keys`          text COMMENT '整改文件key',
    `audit_status`               int(11) DEFAULT NULL COMMENT '记录状态',
    `source`                     varchar(20)    DEFAULT NULL COMMENT '黑名单来源',
    `create_by`                  bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                  bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                          `black_warehouse_record_ apply_organization_dept` (`apply_organization`) USING BTREE,
    KEY                          `black_warehouse_record_ apply_audit_status` (`audit_status`) USING BTREE,
    KEY                          `black_warehouse_record_ apply_business_type` (`business_type`) USING BTREE,
    KEY                          `black_warehouse_record_unified_social_credit_code` (`unified_social_credit_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=738 DEFAULT CHARSET=utf8mb4 COMMENT='黑灰名单记录表';

CREATE TABLE `black_gray_warehouse_task`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
    `task_num`           varchar(164) DEFAULT NULL COMMENT '任务编号，唯一索引，保存时生成，机构缩写+8位年月日+至少3位自增数,如GDRF20230329001，理论位数应该是15位，考虑到后三位自增数极端情况下不一定够用，所以预留32位',
    `task_num_sequence`  varchar(150) DEFAULT NULL COMMENT '任务编号序列',
    `parent_task_id`     bigint(20) DEFAULT NULL COMMENT '上层任务id',
    `org_code`           varchar(32) NOT NULL COMMENT '机构code',
    `business_source`    varchar(20)  DEFAULT NULL COMMENT '业务类型',
    `sub_task_dept_code` varchar(255) DEFAULT NULL COMMENT '子任务-派发到的部门code',
    `time_point`         varchar(32) NOT NULL COMMENT '数据时点，即用户提交任务时所在的月份',
    `deadline`           datetime     DEFAULT NULL COMMENT '定期任务报送截止时间',
    `overtime_flag`      tinyint(1) DEFAULT '0' COMMENT '定期任务是否超时，0 不超时，1超时',
    `upload_file`        longtext COMMENT '上传导入文件',
    `task_type`          varchar(32) NOT NULL COMMENT '关联交易任务类型 ',
    `audit_status`       tinyint(4) NOT NULL DEFAULT '0' COMMENT '审批状态 0=wait, 1=ing, 2=withdraw, 3=reject, 4=finish',
    `retract_suggest`    text COMMENT '子任务-退回说明',
    `is_closed`          tinyint(1) DEFAULT '0' COMMENT '子任务-关闭标记',
    `is_retract`         tinyint(1) DEFAULT '0' COMMENT '派发人退回标记',
    `audit_task_id`      bigint(20) DEFAULT NULL COMMENT '审批流任务id，冗余，便于查找audit_task记录',
    `current_operator`   varchar(255) DEFAULT NULL COMMENT '审批流当前操作人，冗余，方便进行用户数据权限过滤',
    `pre_operator`       varchar(255) DEFAULT NULL COMMENT '上一操作人，冗余，方便判断能否撤回',
    `assigner`           varchar(255) DEFAULT NULL COMMENT '子任务-派发人',
    `assign_submit_role` varchar(255) DEFAULT NULL COMMENT '子任务-指定接受角色',
    `assign_submit_user` varchar(255) DEFAULT NULL COMMENT '子任务-指定接收处理人',
    `submit_time`        datetime     DEFAULT NULL COMMENT '最后提交时间',
    `assign_time`        datetime     DEFAULT NULL COMMENT '子任务-派发时间',
    `audit_time`         datetime     DEFAULT NULL COMMENT '最后审批时间',
    `source`             tinyint(4) NOT NULL COMMENT '1=页面录入，2=openApi对接',
    `created_by`         varchar(255) DEFAULT NULL COMMENT '创建人',
    `gmt_create`         timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`         varchar(255) DEFAULT NULL COMMENT '更新人',
    `gmt_update`         timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `unique_task_num` (`task_num`) USING BTREE,
    KEY                  `black_gray_warehouse_task_time_point` (`time_point`) USING BTREE,
    KEY                  `black_gray_warehouse_task_org_id` (`org_code`) USING BTREE,
    KEY                  `black_gray_warehouse_task_audit_status` (`audit_status`) USING BTREE,
    KEY                  `black_gray_warehouse_task_current_operator` (`current_operator`) USING BTREE,
    KEY                  `black_gray_warehouse_task_task_type` (`task_type`) USING BTREE,
    KEY                  `black_gray_warehouse_task_parent_task_id` (`parent_task_id`) USING BTREE,
    KEY                  `black_gray_warehouse_task_deadline` (`deadline`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8mb4 COMMENT='黑灰名单任务表';

CREATE TABLE `black_gray_warehouse_rule_config`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `org_code`        varchar(50)  DEFAULT NULL COMMENT '维护机构',
    `rule_number`     varchar(50)  DEFAULT NULL COMMENT '规则编号',
    `rule_sequence`   int(11) DEFAULT NULL COMMENT '递增序列',
    `rule_name`       varchar(100) DEFAULT NULL COMMENT '规则名称',
    `level`           int(11) NOT NULL DEFAULT '0' COMMENT '层级 0金控定义',
    `parent_id`       bigint(20) DEFAULT NULL COMMENT '父ID',
    `main_id`         bigint(20) DEFAULT NULL COMMENT '所属金控类型主ID，即一级ID',
    `status`          tinyint(1) DEFAULT '0' COMMENT '状态 1启用，0禁用',
    `black_gray_type` varchar(20)  DEFAULT NULL COMMENT '黑灰标识',
    `source`          varchar(20)  DEFAULT NULL COMMENT '来源,内部外部',
    `suit_business`   json         DEFAULT NULL COMMENT '适用类型',
    `suit_org`        json         DEFAULT NULL COMMENT '适用机构',
    `create_time`     datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`       bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `update_by`       bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `black_gray_warehouse_rule_config_rule_number` (`rule_number`) USING BTREE,
    KEY               `black_gray_warehouse_rule_config_black_gray_type` (`black_gray_type`) USING BTREE,
    KEY               `black_gray_warehouse_rule_config_rule_name` (`rule_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COMMENT='黑灰名单库-入库原因参数配置';

CREATE TABLE `business_task_config`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `module_key`           varchar(50)  DEFAULT NULL COMMENT '模块标识',
    `task_type`            varchar(20)  DEFAULT NULL COMMENT '任务类型',
    `frequency`            varchar(255) DEFAULT NULL COMMENT '生成频率 年月周',
    `generation_day`       int(11) DEFAULT NULL COMMENT '第xx天',
    `generation_hour`      int(11) DEFAULT NULL COMMENT '第xx小时',
    `deadline_day`         int(11) DEFAULT NULL COMMENT '任务截止天数',
    `deadline_hour`        int(11) DEFAULT NULL COMMENT '任务截止小时',
    `org_code`             varchar(50)  DEFAULT NULL COMMENT '维护机构',
    `ext`                  json         DEFAULT NULL COMMENT '拓展参数',
    `task_start_time`      date         DEFAULT NULL COMMENT '任务开始时间',
    `task_end_time`        date         DEFAULT NULL COMMENT '任务结束时间',
    `last_generation_time` date         DEFAULT NULL COMMENT '最近一次生成时间',
    `status`               tinyint(1) DEFAULT '1' COMMENT '状态 0 禁用 1 启用',
    `create_time`          datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`          datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`            bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `update_by`            bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    PRIMARY KEY (`id`),
    KEY                    `business_task_config_all_key` (`module_key`,`task_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='任务配置表';

ALTER TABLE black_gray_library add COLUMN black_gray_sort int(11) COMMENT '黑灰排序' after black_gray_type;

ALTER TABLE black_gray_warehouse_record add COLUMN task_num varchar(100) COMMENT '任务编号' after id;

ALTER TABLE black_gray_library add COLUMN task_num varchar(100) COMMENT '任务编号' after id;

ALTER TABLE black_gray_manual_outbound add COLUMN actual_outbound_time date COMMENT '实际出库时间' after plan_outbound_time;

ALTER TABLE black_gray_manual_outbound add COLUMN message varchar(2000) COMMENT '出库说明' after audit_status;

ALTER TABLE black_gray_business_dict add COLUMN business_type varchar(100) COMMENT '字典类型';
ALTER TABLE `black_gray_warehouse_record`
    ADD COLUMN `group_credit_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属集团主企业统一社会信用代码' AFTER `membership_group`,
    ADD COLUMN `group_leader_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否集团主企业' AFTER `group_credit_code`;
ALTER TABLE `black_gray_library`
    ADD COLUMN `black_gray_type_num` int NULL COMMENT '黑灰标识数字，BLACK=2, GRAY=1，用于聚合、排序，方便查询' AFTER `black_gray_type`,
    ADD COLUMN `group_credit_code` varchar(50) NULL COMMENT '所属集团主企业统一社会信用代码' AFTER `membership_group`,
    ADD COLUMN `group_leader_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否集团主企业' AFTER `group_credit_code`,
    ADD INDEX `idx_black_gray_type`(`black_gray_type`) USING BTREE,
    ADD INDEX `idx_enterprise_name`(`enterprise_name`) USING BTREE,
    ADD INDEX `idx_black_gray_type_num`(`black_gray_type_num`) USING BTREE,
    ADD INDEX `idx_stock_status`(`stock_status`) USING BTREE,
    ADD INDEX `idx_membership_group`(`membership_group`) USING BTREE,
    ADD INDEX `idx_group_credit_code`(`group_credit_code`) USING BTREE,
    ADD INDEX `idx_group_leader_flag`(`group_leader_flag`) USING BTREE,
    ADD INDEX `idx_update_time`(`update_time`) USING BTREE;

ALTER TABLE `black_gray_library`
    MODIFY COLUMN `black_gray_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '黑灰标识' AFTER `apply_reason`,
    MODIFY COLUMN `black_gray_type_num` int(11) NOT NULL COMMENT '黑灰标识数字，BLACK=2, GRAY=1，用于聚合、排序，方便查询' AFTER `black_gray_type`;

ALTER TABLE black_gray_manual_outbound add COLUMN report_flag tinyint(1) default 0 COMMENT '是否报送 0 不报送， 1报送';
ALTER TABLE black_gray_break_business add COLUMN report_flag tinyint(1) default 0 COMMENT '是否报送 0 不报送， 1报送';
ALTER TABLE black_gray_library add COLUMN report_flag tinyint(1) default 0 COMMENT '是否报送 0 不报送， 1报送';
ALTER TABLE black_gray_warehouse_record add COLUMN report_flag tinyint(1) default 0 COMMENT '是否报送 0 不报送， 1报送';

INSERT INTO `black_gray_business_dict` (`id`, `org_id`, `org_name`, `business_name`, `business_desc`, `level`, `parent_id`, `main_id`, `create_time`, `update_time`) VALUES (8, 101, '浙商金控', 'INFORMATION_RELATED', '涉信类', 0, NULL, 8, '2023-12-05 15:45:37', '2023-12-05 16:40:24');
INSERT INTO `black_gray_business_dict` (`id`, `org_id`, `org_name`, `business_name`, `business_desc`, `level`, `parent_id`, `main_id`, `create_time`, `update_time`) VALUES (9, 101, '浙商金控', 'INSURANCE_CATEGORY', '保险类', 0, NULL, 9, '2023-12-05 15:45:37', '2023-12-05 16:40:45');
INSERT INTO `black_gray_business_dict` (`id`, `org_id`, `org_name`, `business_name`, `business_desc`, `level`, `parent_id`, `main_id`, `create_time`, `update_time`) VALUES (10, 103, '浙商保险', 'OTHER', '其他', 0, NULL, 9, '2023-12-05 15:45:37', '2023-12-05 16:40:50');

alter table black_gray_warehouse_task add column `is_supply_group_info` tinyint(1) DEFAULT NULL COMMENT '是否填充了所属集团信息';

-- 黑灰规则
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org) VALUES ('10000079', 'IB20241224000011', 11, '金融企业日常管理中风险管理部和业务部门沟通发现并确认的', 0, null, null, 1, 'BLACK_LIST', 'INTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000396"]');
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org) VALUES ('10000079', 'IB20241224000010', 10, '金融企业内外部检查中要求列为黑名单，或要求禁止准入，或要求到期退出的客户', 0, null, null, 1, 'BLACK_LIST', 'INTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000396"]');
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org) VALUES ('10000079', 'IB20241224000009', 9, '公司内部判定实质违约（如判断为恶意拖欠利息90天等）', 0, null, null, 1, 'BLACK_LIST', 'INTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000396"]');
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org) VALUES ('10000079', 'IB20241224000008', 8, '非标、固收出现逾期或展期超过180天（按照合同约定）', 0, null, null, 1, 'BLACK_LIST', 'INTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000396"]');
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org) VALUES ('10000079', 'IG20241224000030', 30, '金融企业内外部检查中要求进行规模压降的客户', 0, null, null, 1, 'GRAY_LIST', 'INTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000396"]');
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org) VALUES ('10000079', 'IG20241224000029', 29, '金融企业内部评级准入级别以下或评审会否决的信贷客户', 0, null, null, 1, 'GRAY_LIST', 'INTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000396"]');
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org) VALUES ('10000079', 'IG20241224000028', 28, '存在逾期90天及以上的借款人及其担保人', 0, null, null, 1, 'GRAY_LIST', 'INTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000396"]');

INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EG20240131000008', 8, '近三年企业出现破产重整', 0, null, null, 0, 'GRAY_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["ZSJK", "ZSBX", "ZSZL"]', '2024-01-31 10:29:02', '2024-12-24 10:04:31', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EG20240131000007', 7, '近两年发行债券违约', 0, null, null, 1, 'GRAY_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:28:50', '2024-01-31 20:50:56', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EG20240131000006', 6, '存在财务造假', 0, null, null, 1, 'GRAY_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:28:41', '2024-01-31 20:50:46', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000028', 28, '集团被认定为黑名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:28:25', '2024-01-31 20:50:37', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000027', 27, '被出具审计非标准意见企业', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:28:18', '2024-01-31 20:50:37', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000026', 26, '拖欠农民工工资黑名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:28:10', '2024-01-31 20:50:37', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000025', 25, '重大税收违法失信主体', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:28:02', '2024-01-31 20:50:37', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000024', 24, '失信被执行人', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:27:55', '2024-01-31 20:50:37', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000023', 23, '能源行业重点关注名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:27:49', '2024-01-31 20:50:37', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000022', 22, '涉金融领域黑名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:27:42', '2024-01-31 20:50:37', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000021', 21, '严重违法失信', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:27:36', '2024-01-31 20:50:37', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000020', 20, '反洗钱高风险等级中禁入类客户', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INSURANCE_CATEGORY"]', '["10000079"]', '2024-01-31 10:27:15', '2024-01-31 20:50:37', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EG20240131000005', 5, '近三年企业出现破产重整', 0, null, null, 1, 'GRAY_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:22:32', '2024-01-31 20:48:31', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EG20240131000004', 4, '存在财务造假', 0, null, null, 1, 'GRAY_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:22:21', '2024-01-31 20:48:19', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000019', 19, '集团被认定为黑名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:21:56', '2024-01-31 20:48:00', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000018', 18, '近两年发行债券违约', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:21:43', '2024-01-31 20:47:41', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000017', 17, '被出具审计非标准意见企业', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:21:34', '2024-01-31 20:47:41', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000016', 16, '拖欠农民工工资黑名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:21:26', '2024-01-31 20:47:41', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000015', 15, '重大税收违法失信主体', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:21:19', '2024-01-31 20:47:41', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000014', 14, '失信被执行人', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:21:12', '2024-01-31 20:47:41', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000013', 13, '能源行业重点关注名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:21:04', '2024-01-31 20:47:41', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000012', 12, '涉金融领域黑名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:20:58', '2024-01-31 20:47:41', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000011', 11, '严重违法失信', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:20:51', '2024-01-31 20:47:41', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000010', 10, '反洗钱高风险等级中禁入类客户', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000079"]', '2024-01-31 10:20:26', '2024-01-31 20:47:41', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EG20240131000003', 3, '近三年企业出现破产重整', 0, null, null, 1, 'GRAY_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:17:00', '2024-01-31 20:45:39', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EG20240131000002', 2, '近两年发行债券违约', 0, null, null, 1, 'GRAY_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:16:46', '2024-01-31 20:45:39', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EG20240131000001', 1, '存在财务造假', 0, null, null, 1, 'GRAY_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:16:31', '2024-01-31 20:45:10', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000009', 9, '集团被认定为黑名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:16:06', '2024-01-31 20:44:56', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000008', 8, '被出具审计非标准意见企业', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:15:56', '2024-01-31 20:44:56', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000007', 7, '拖欠农民工工资黑名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:15:50', '2024-01-31 20:44:56', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000006', 6, '重大税收违法失信主体', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:15:43', '2024-01-31 20:44:56', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000005', 5, '失信被执行人', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:15:36', '2024-01-31 20:44:56', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000004', 4, '能源行业重点关注名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:15:29', '2024-01-31 20:44:56', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000003', 3, '涉金融领域黑名单', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:15:22', '2024-01-31 20:44:56', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000002', 2, '严重违法失信', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:15:16', '2024-01-31 20:44:56', 3, 3);
INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type, source, suit_business, suit_org, create_time, update_time, create_by, update_by) VALUES ('10000079', 'EB20240131000001', 1, '反洗钱高风险等级中禁入类客户', 0, null, null, 1, 'BLACK_LIST', 'EXTERNAL_APPROVAL', '["OTHER"]', '["10000079"]', '2024-01-31 10:14:47', '2024-01-31 20:44:38', 3, 3);

INSERT INTO black_gray_warehouse_rule_config (org_code, rule_number, rule_sequence, rule_name, level, parent_id, main_id, status, black_gray_type,
                                              source, suit_business, suit_org) VALUES ('10000079', 'IG20250114000032', 31, '存在票据持续逾期', 0, null, null, 1, 'GRAY_LIST', 'INTERNAL_APPROVAL', '["INFORMATION_RELATED"]', '["10000396"]');

-- 黑灰名单end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



