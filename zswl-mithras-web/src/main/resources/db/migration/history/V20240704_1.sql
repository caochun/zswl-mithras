-- 利润测算
CREATE TABLE `contract_assess_dept_detail` (
       `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
       `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',
       `assess_dept_id` bigint(20) DEFAULT NULL COMMENT '部门id',
       `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
       `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
       `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
       `deleted` int(11) DEFAULT '0' COMMENT '0：未删除，1：已删除，默认0',
       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COMMENT='考核部门设置表';
-- 设置特殊合同对应部门
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (991, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (992, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (993, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (994, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (958, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (957, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (954, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (948, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (947, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (989, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (990, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (1015, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (1068, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (982, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (981, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (987, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (907, 6);
INSERT INTO `contract_assess_dept_detail` (`contract_id`, `assess_dept_id`) VALUES (1144, 6);
-- 费用比例
UPDATE `kpi_parameter_config` SET `config_code` = 'EXPENSE_RADIO', `config_desc` = '费用计提比例', `config_value` = '[{\"expenseRadio\":\"23\",\"assessDept\":\"6\"},\n{\"expenseRadio\":\"20\",\"assessDept\":\"9\"},\n{\"expenseRadio\":\"44\",\"assessDept\":\"12\"},\n{\"expenseRadio\":\"50\",\"assessDept\":\"11\"},\n{\"expenseRadio\":\"20\",\"assessDept\":\"27\"},\n{\"expenseRadio\":\"633\",\"assessDept\":\"31\"}]' WHERE `id` = 248;
-- 新增接口
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type` ) VALUES ( 'financeprojectprofitprofitcalculation', '利润测算', 0, 487, 'POST', '/finance/projectprofit/profit/calculation', 2 );
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`) VALUES ('kpiparameterconfigcontractAssessDeptget', '参数设置-考核部门维护-详情', 0, 308,'POST', '/kpi/parameterconfig/contractAssessDept/get', 2);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`) VALUES ('kpiparameterconfigcontractAssessDeptdelete', '参数设置-考核部门维护-删除', 0, 308,'POST', '/kpi/parameterconfig/contractAssessDept/delete', 2);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`,  `method`, `path`, `type`) VALUES ('kpiparameterconfigcontractAssessDeptsave', '参数设置-考核部门维护-保存', 0, 308, 'POST', '/kpi/parameterconfig/contractAssessDept/save', 2);

-- 抵质押模块新增字段
ALTER TABLE contract_mortgage ADD COLUMN contract_mortgage_type VARCHAR(50) COMMENT '抵押合同类型';
ALTER TABLE contract_pledge ADD COLUMN contract_pledge_type VARCHAR(50) COMMENT '质押合同类型';
ALTER TABLE contract_mortgage_lib ADD COLUMN contract_mortgage_type VARCHAR(50) COMMENT '抵押合同类型';
ALTER TABLE contract_pledge_lib ADD COLUMN contract_pledge_type VARCHAR(50) COMMENT '质押合同类型';


-- 大熊的SQL START =============================================================================================================
CREATE TABLE `performance_base_info`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `year`                 bigint(20) NOT NULL COMMENT '年份',
    `belong_dept_id`       bigint(20)          DEFAULT NULL COMMENT '所属部门ID',
    `belong_dept_name`     varchar(20)         DEFAULT NULL COMMENT '部门名称，特殊key使用',
    `business_user_id`     bigint(20)          DEFAULT NULL COMMENT '业务人员ID',
    `business_type`        varchar(32)         DEFAULT NULL COMMENT '业务类型(枚举)',
    `belong_type`          varchar(32)         DEFAULT NULL COMMENT '所属类型(枚举)，标识种类',
    `asset_balance_target` bigint(20)          DEFAULT NULL COMMENT '资产余额目标',
    `revenue_target`       bigint(20)          DEFAULT NULL COMMENT '营业收入目标',
    `profit_target`        bigint(20)          DEFAULT NULL COMMENT '利润目标',
    `deleted`              tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by`            bigint(20)          DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`          datetime            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            bigint(20)          DEFAULT NULL COMMENT '最后更新人id',
    `update_time`          datetime            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `advertising_amount`   bigint(20)          DEFAULT NULL COMMENT '投放金额',
    `main_id`              bigint(20)          DEFAULT NULL COMMENT '主表ID',
    PRIMARY KEY (`id`),
    KEY `idx_main_id_deleted` (`main_id`, `deleted`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='业绩基本信息表';

CREATE TABLE `performance_main_info`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `year`        bigint(20) NOT NULL COMMENT '年度',
    `status`      tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态，0:未启用， 1:启用',
    `deleted`     tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by`   bigint(20)          DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   bigint(20)          DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='业绩信息主表';

CREATE TABLE `performance_record_info`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `performance_id` bigint(20) NOT NULL COMMENT '业绩信息基本表ID',
    `month`          int(11)    NOT NULL COMMENT '月份',
    `target_amount`  bigint(20)          DEFAULT NULL COMMENT '目标金额',
    `deleted`        tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by`      bigint(20)          DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`    datetime            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      bigint(20)          DEFAULT NULL COMMENT '最后更新人id',
    `update_time`    datetime            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_performance_id_deleted` (`performance_id`, `deleted`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='业绩信息详情表';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by,
                              en_name, method, path, type, group_id)
VALUES ('kpiParameterConfigFileDownloadTemplate', '文件模版下载',
        0, 731, null, null, null, 'GET', '/file/download/template', 1, null),
       ('dashboardPerformanceDeptInSort', '业绩排名-部门内排名', 0,
        730, null, null, null, 'POST', '/dashboard/performance/dept/in/sort', 1, null),
       ('dashboardPerformanceDeptShipSort', '业绩排名-部门间排名',
        0, 730, null, null, null, 'POST', '/dashboard/performance/dept/ship/sort', 1, null),
       ('dashboardPerformancePersonal', '我的业绩-个人业绩', 0,
        730, null, null, null, 'POST', '/dashboard/performance/personal', 1, null),
       ('dashboardPerformanceDept', '我的业绩-部门业绩', 0, 730,
        null, null, null, 'POST', '/dashboard/performance/dept', 1, null),
       ('kpiPerformanceMangeModify', '业绩目标年度状态修改', 0,
        731, null, null, null, 'POST', '/kpi/performance/manage/modify', 2, null),
       ('kpiPerformanceMangeImport', '业绩目标导入', 0, 731, null,
        null, null, 'POST', '/kpi/performance/manage/import', 2, null),
       ('kpiPerformanceMangeList', '业绩目标子列表', 0, 731, null,
        null, null, 'POST', '/kpi/performance/manage/list', 1, null),
       ('kpiPerformanceMangeExport', '业绩目标导出', 0, 731, null,
        null, null, 'POST', '/kpi/performance/manage/export', 1, null),
       ('kpiPerformanceMangeAdd', '业绩年度新增', 0, 731, null,
        null, null, 'POST', '/kpi/performance/manage/add', 2, null),
       ('kpiPerformanceMangeMainDetail', '业绩目标主数据详情', 0,
        731, null, null, null, 'POST', '/kpi/performance/manage/main/detail', 1, null),
       ('kpiperformancemanagemainlist', '业绩目标主数据列表', 0,
        731, null, null, null, 'POST', '/kpi/performance/manage/main/list', 1, null);

INSERT INTO bifrost_menu (code, level, sort_no, path, parent_id, icon, name, id)
VALUES ('kpiBusinessGoal', 3, 0, '/kpi/businessGoal', null, null, '业务目标管理', 731);


INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
VALUES (118, 731, 0);
-- 大熊的SQL END ===============================================================================================================

INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`)
VALUES
	('job', '岗位类型', 'chairmanauthorization', '董事长授权岗', 10);

-- 工作台 项目质押/监管情况和资金成本
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-07-04 17:18:12', '2024-07-04 17:18:12', null, 'dashboardFinanceFundsList', '业务工作台-融资视图-资金成本', 0, (select id from bifrost_menu where `code` = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/finance/funds/list', 1, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-07-04 17:18:50', '2024-07-04 17:18:50', null, 'dashboardProjectInfoPledgeList', '业务工作台-项目视图-项目情况-质押/监管情况', 0, (select id from bifrost_menu where `code` = 'dashboard'), NULL, NULL, NULL, 'POST', '/dashboard/project/info/pledge/list', 1, NULL);

-- 收据打印
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('collectionFlowCenterBusinessExport', '导出收款业务流水', 0, 495, NULL, NULL, NULL, 'POST', '/collection/flow/center/business/export', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('collectionFlowCenterBusinessExportList', '收款业务流水列表详情', 0, 495, NULL, NULL, NULL, 'POST', '/collection/flow/center/business/export/list', 2, NULL);

-- 应付利息
INSERT INTO `bifrost_custom_tree_menu_ref` (`custom_tree_id`, `menu_id`, `sort_no`) VALUES (139, 732, 0);

INSERT INTO `bifrost_menu` (`code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`, `id`, `en_name`, `target`, `create_by`, `update_by`) VALUES ('financialpayableInterest', 3, 0, NULL, '/financial/payableInterest', NULL, NULL, '应付利息', 732, NULL, NULL, NULL, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('interestPayList', '列表', 0, 732, NULL, NULL, NULL, 'POST', '/interestPay/list', 2, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('interestPayCalculate', '计提利息计算', 0, 732, NULL, NULL, NULL, 'POST', '/interestPay/calculate', 2, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('interestPayBasicDetail', '基本详情', 0, 732, NULL, NULL, NULL, 'POST', '/interestPay/basic/detail', 2, NULL);

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('interestPayCalDetail', '应付计提利息详情', 0, 732, NULL, NULL, NULL, 'POST', '/interestPay/cal/detail', 2, NULL);