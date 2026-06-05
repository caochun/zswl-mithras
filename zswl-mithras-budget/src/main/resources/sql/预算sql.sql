
-- 预算sql
insert into budget_parameter_config (config_key, config_name, config_value) values
('FTP_PRICE', 'FTP定价', '[{"ftpIndustryClassification":"FTP_PUBLIC_UTILITIES","oneYearTerm":"10000","oneToThreeYearTerm":"20000","moreThanThreeYears":"440000","rowId":"1"},{"ftpIndustryClassification":"FTP_CIVIL_CONSUMPTION","oneYearTerm":"30000","oneToThreeYearTerm":"30000","moreThanThreeYears":"10000","rowId":"2"},{"ftpIndustryClassification":"FTP_STATE_OWNED_INDUSTRY","oneYearTerm":"10000","oneToThreeYearTerm":"20000","moreThanThreeYears":"30000","rowId":"3"},{"ftpIndustryClassification":"FTP_OTHER_INDUSTRY","oneYearTerm":"30000","oneToThreeYearTerm":"20000","moreThanThreeYears":"10000","rowId":"4"}]'),
('RISK_RATIO', '风险准备金计提比例', '[{"riskReserve":"10000"}]'),
('EXPENSE_RATIO', '费用比例', '[{"deptId":9,"deptName":"浙江业务部","expenseRatio":"11100","rowId":"20"},{"deptId":7,"deptName":"化工建材业务部","expenseRatio":"30000","rowId":"21"},{"deptId":11,"deptName":"智能制造业务部","expenseRatio":"0","rowId":"22"},{"deptId":12,"deptName":"交通运输业务部","expenseRatio":"50000","rowId":"23"}]');

drop table finance_bcm_balance_mf;
CREATE TABLE `finance_bcm_balance_mf`
(
    `id`             BIGINT UNSIGNED   AUTO_INCREMENT COMMENT '主键id（自增）',
    `org_code`       VARCHAR(64)       DEFAULT '' COMMENT '组织编码（业务标识，建议索引）',
    `org_name`       VARCHAR(255)      DEFAULT '' COMMENT '组织名称',
    `f_year`         INT UNSIGNED      COMMENT '财年（如2024）',
    `f_period`       INT UNSIGNED      COMMENT '期间（1-12月）',
    `year_period`    VARCHAR(20)       DEFAULT '' COMMENT '期间-年月（格式：YYYYMM，如202405）',
    `acct_no`        VARCHAR(64)       DEFAULT '' COMMENT '科目编码（业务关键标识，建议索引）',
    `acct_name`      VARCHAR(255)      DEFAULT '' COMMENT '科目名称',
    `risk_name`      VARCHAR(255)      DEFAULT '' COMMENT '科目编码转译@本年累计/上年同期@贷方金额/借方金额',
    `risk_value`        DECIMAL(19, 4)    DEFAULT 0.0000 COMMENT '金额',
    `fassgrpid`      VARCHAR(255)      DEFAULT '' COMMENT '辅助核算id（关联辅助核算表）',
    `insert_time`    VARCHAR(20)       DEFAULT '' COMMENT '插入时间（格式：yyyy-MM-dd HH:mm:ss）',
    `delete_flag`    TINYINT UNSIGNED  DEFAULT 0 COMMENT '逻辑删除标记（0-未删除，1-已删除，继承自BaseModelWithLogicDelete）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '更新人id',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除',
    PRIMARY KEY (`id`),
    KEY `idx_org_period_acct` (`org_code`, `year_period`, `acct_no`) COMMENT '业务常用查询索引（组织+期间+科目组合查询）'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
    COMMENT ='科目余额表';

alter table performance_base_info add column `consulting_fee_income` bigint(20) DEFAULT NULL COMMENT '咨询服务费收入';
alter table performance_base_info add column  `interest_income` bigint(20) DEFAULT NULL COMMENT '利息收入';
alter table performance_base_info add column  `biz_fee` bigint(20) DEFAULT NULL COMMENT '经营费用';
alter table performance_base_info add column  `business_trip_fee` bigint(20) DEFAULT NULL COMMENT '差旅费';
alter table performance_base_info add column  `business_serve_fee` bigint(20) DEFAULT NULL COMMENT '业务招待费';
alter table performance_base_info add column  `before_profit_target` bigint(20) DEFAULT NULL COMMENT '拨备前利润目标';

alter table budget_examine_budget_execute add column current_month bigint(20) default null comment '本月数';
alter table budget_examine_budget_execute add column total_year bigint(20) default null comment '本年累计';
alter table budget_examine_budget_execute add column Last_year_period bigint(20) default null comment '上年同期';
alter table budget_examine_budget_execute add column on_year bigint(20) default null comment '同比';
alter table budget_examine_budget_execute add column annual_budget_target bigint(20) default null comment '全年预算目标';
alter table budget_examine_budget_execute add column progress_budget_target bigint(20) default null comment '进度预算目标';
alter table budget_examine_budget_execute add column progress_budget_completion_rate bigint(20) default null comment '进度预算完成率';
alter table budget_examine_budget_execute add column annual_budget_completion_rate bigint(20) default null comment '全年预算完成率';


alter table budget_examine_pay_plan_execute add column deviation_degree_plan int(11) default null comment '资金计划偏离度';
alter table budget_examine_pay_plan_execute add column project_accuracy int(11) default null comment '项目准确度';
alter table budget_examine_pay_plan_execute add column failed_report_funding_plan int(11) default null comment '未及时提报次数-资金计划';
alter table budget_examine_pay_plan_execute add column failed_report_week int(11) default null comment '未及时提报次数-周报';
alter table budget_examine_pay_plan_execute add column delay_days_funding_plan int(11) default null comment '延迟天数-资金计划';
alter table budget_examine_pay_plan_execute add column delay_days_week int(11) default null comment '延迟天数-周报';
alter table budget_plan_pay_weekly_report_detail add column is_businesshead_confirm tinyint(1) default 0 comment '业务负责人是否确认';

alter table budget_plan_pay_weekly_report_detail add column commission bigint(20) default null comment '项目手续费';
alter table budget_plan_pay_weekly_report_detail add column irr_ftp_diff int(11) default null comment 'IRR-FTP差值';
alter table budget_plan_pay_weekly_report_detail add column yunying_feedback varchar(200) default null comment '运营进度反馈';
alter table budget_plan_pay_weekly_report_detail add column last_operate_user_id bigint(20) default null comment '最后一次操作人id';
alter table budget_plan_pay_weekly_report_detail add column proj_review_id bigint(20) default null comment '项目评审id';

alter table budget_plan_pay_weekly_report_detail add column businesshead_confirm_date date default null comment '业务负责人确认时间';
alter table budget_plan_pay_detail add column businesshead_confirm_date date default null comment '业务负责人确认时间';


-- 权限
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select'budgetParameterConfigModify','修改预算管理-参数设置',0,a.id,'POST','/budget/parameter/config/modify',2 from bifrost_menu a where a.code='budgetPlanParameterConfig';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select'budgetParameterConfigList','预算管理-参数设置列表',0,a.id,'POST','/budget/parameter/config/list',2 from bifrost_menu a where a.code='budgetPlanParameterConfig';


INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name, en_name, target)
VALUES ('budgetManagementPlanCost', 3, 0, null, '/budgetManagement/plan/cost', null, null, '成本预算',
        null, null);

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no) VALUES ((select id from bifrost_custom_tree where code = 'budgetPlan'),
                                                                                    (select id from bifrost_menu where code = 'budgetManagementPlanCost'), 5);

-- 成本
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanCostList','预算管理-预算计划-成本预算列表',0,a.id,'POST','/budget/plan/cost/list',2 from bifrost_menu a where a.code='budgetManagementPlanCost';
insert  into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanCostDetailModify','修改预算管理-预算计划-成本预算-明细',0,a.id,'POST','/budget/plan/cost/detail/modify',2 from bifrost_menu a where a
                                                                                                                                          .code='budgetManagementPlanCost';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanCostDetailList','预算管理-预算计划-成本预算-明细列表',0,a.id,'POST','/budget/plan/cost/detail/list',2 from bifrost_menu a where a
                                                                                                                                      .code='budgetManagementPlanCost';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanCostDetailRemove','删除预算管理-预算计划-成本预算-明细',0,a.id,'POST','/budget/plan/cost/detail/remove',2 from bifrost_menu a where a
                                                                                                                                          .code='budgetManagementPlanCost';

-- 周报
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanPayWeeklyReportList','预算管理-投放计划-项目周报列表',0,a.id,'POST','/budget/plan/pay/weekly/report/list',2 from bifrost_menu a where a
                                                                                                                                              .code='budgetPlanPay';
insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanPayWeeklyReportInfo','预算管理-预算计划-投放计划-信息',0,a.id,'POST','/budget/plan/pay/weekly/report/info',2 from bifrost_menu a where a
                                                                                                                                               .code='budgetPlanPay';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanWeeklyReportDetailStatistics','预算管理-投放计划（月度）-项目周报-明细-统计',0,a.id,'POST','/budget/plan/weekly/report/detail/statistics',2 from
    bifrost_menu a where a.code='budgetPlanPay';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanWeeklyReportPageList','预算管理-投放计划（月度）-项目周报-明细-列表',0,a.id,'POST','/budget/plan/weekly/report/pageList',2 from bifrost_menu a where a
                                                                                                                                                       .code='budgetPlanPay';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanWeeklyReportDetailAdd', '预算管理-投放计划（月度）-项目周报-明细-新增', 0, a.id, 'POST', '/budget/plan/weekly/report/detail/add', 2
from bifrost_menu a
where a.code = 'budgetPlanPay';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanWeeklyReportDetailModify', '预算管理-投放计划（月度）-项目周报-明细-编辑', 0, a.id, 'POST', '/budget/plan/weekly/report/detail/modify', 2
from bifrost_menu a
where a.code = 'budgetPlanPay';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanWeeklyDetailPageListContract',
       '预算管理-投放计划（月度）-项目周报-明细-列表-合同信息',
       0,
       a.id,
       'POST',
       '/budget/plan/weekly/detail/pageList/contract',
       2
from bifrost_menu a
where a.code = 'budgetPlanPay';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanWeeklyDetailDeptConfirmList',
       '预算管理-投放计划（月度）-项目周报-明细-部门确认情况',
       0,
       a.id,
       'POST',
       '/budget/plan/weekly/detail/deptConfirm/list',
       2
from bifrost_menu a
where a.code = 'budgetPlanPay';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetPlanPayWeeklyReportDetailBatchDelete',
       '预算管理-投放计划（月度）-项目周报-明细-删除',
       0,
       a.id,
       'POST',
       '/budget/plan/pay/weekly/report/detail/batchDelete',
       2
from bifrost_menu a
where a.code = 'budgetPlanPay';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetExamineAdd','预算管理-预算考核-新增预算管理',0,a.id,'POST','/budget/examine/add',2
from bifrost_menu a where a.code = 'budgetPlanExamine';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetExamineList','预算管理-预算考核-预算考核列表',0,a.id,'POST','/budget/examine/list',2
from bifrost_menu a where a.code = 'budgetPlanExamine';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetExamineRemove','预算管理-预算考核-删除预算管理',0,a.id,'POST','/budget/examine/remove',2
from bifrost_menu a where a.code = 'budgetPlanExamine';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetExamineSubmit','预算管理-预算考核-提交预算管理',0,a.id,'POST','/budget/examine/submit',2
from bifrost_menu a where a.code = 'budgetPlanExamine';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetExamineDetail','预算管理-预算考核-预算执行情况表列表',0,a.id,'POST','/budget/examine/detail',2
from bifrost_menu a where a.code = 'budgetPlanExamine';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetExamineBenefitModify','修改预算管理-预算考核-效益考核表',0,a.id,'POST','/budget/examine/benefit/modify',2
from bifrost_menu a where a.code = 'budgetPlanExamine';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetExamineBenefitList','预算管理-预算考核-效益考核表列表',0,a.id,'POST','/budget/examine/benefit/list',2
from bifrost_menu a where a.code = 'budgetPlanExamine';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetExamineBudgetExecuteModify','修改预算管理-预算考核-预算执行情况表',0,a.id,'POST','/budget/examine/budget/execute/modify',2
from bifrost_menu a where a.code = 'budgetPlanExamine';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetExamineBudgetExecuteList','预算管理-预算考核-预算执行情况表列表',0,a.id,'POST','/budget/examine/budget/execute/list',2
from bifrost_menu a where a.code = 'budgetPlanExamine';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
select 'budgetExaminePayPlanExecuteList','预算管理-预算考核-投放计划执行情况表列表',0,a.id,'POST','/budget/examine/pay/plan/execute/list',2
from bifrost_menu a where a.code = 'budgetPlanExamine';






