alter table kpi_proj_guess_base_info add column `proj_classify` varchar (30)  DEFAULT NULL COMMENT '项目类别';
alter table kpi_proj_guess_base_info add column `proj_source` varchar (30)  DEFAULT NULL COMMENT '项目来源 新增 首次投放在本年的';
alter table kpi_proj_guess_base_info add column `payment_current`  bigint (20)  DEFAULT NULL COMMENT '投放-当期值';
alter table kpi_proj_guess_base_info add column `payment_total` bigint (20)  DEFAULT NULL COMMENT '投放-累计值';
alter table kpi_proj_guess_base_info add column `proj_payment_year_amount` bigint (20)  DEFAULT NULL COMMENT '项目本年投放金额';
alter table kpi_proj_guess_base_info add column `contract_payment_month_amount` bigint (20)  DEFAULT NULL COMMENT '合同本月投放金额';
alter table kpi_proj_guess_base_info add column `receipt_id` bigint (20)  DEFAULT NULL COMMENT '借据id';
alter table kpi_proj_guess_base_info add column `receipt_first_payment_date` date  DEFAULT NULL COMMENT '借据首次投放日期';


alter table kpi_proj_guess_divide add column `payment_current` bigint(20)  DEFAULT NULL COMMENT '投放奖金-当期值';
alter table kpi_proj_guess_divide add column `payment_total` bigint(20)  DEFAULT NULL COMMENT '投放奖金-累计值';
alter table kpi_proj_guess_divide add column `dept_id` bigint(20)  DEFAULT NULL COMMENT '部门id' after `contract_id`;
alter table kpi_proj_guess_divide add column `divide_weight` int(11)  DEFAULT NULL COMMENT '分配权重';
alter table kpi_proj_guess_divide add column `profit_adjust` bigint(20)  DEFAULT NULL COMMENT '利润-调整值';
alter table kpi_proj_guess_divide add column `bonus_adjust` bigint(20)  DEFAULT NULL COMMENT '奖金-调整值';
alter table kpi_proj_guess_divide add column `payment_award_current` bigint(20)  DEFAULT NULL COMMENT '投放提奖-当期值';
alter table kpi_proj_guess_divide add column `payment_award_total` bigint(20)  DEFAULT NULL COMMENT '投放提奖-累计值';

alter table kpi_proj_guess_divide add column `project_radio_config` varchar (50)  DEFAULT NULL COMMENT '基础提奖比例';
alter table kpi_proj_guess_divide add column `scale_radio_config` varchar (50)  DEFAULT NULL COMMENT '项目规模系数';
alter table kpi_proj_guess_divide add column `type_radio_config` varchar (50)  DEFAULT NULL COMMENT '项目类型系数';
alter table kpi_proj_guess_divide add column `payment_bonus_radio_config` varchar (50)  DEFAULT NULL COMMENT '投放奖金系数';
alter table kpi_proj_guess_divide add column `project_profit` bigint(20)  DEFAULT NULL COMMENT '项目利润当期值';


CREATE TABLE `kpi_finance_project_profit_record`
(
    `id`                             bigint(20)  NOT NULL AUTO_INCREMENT,
    `profit_detail_id`               bigint(20)           DEFAULT NULL COMMENT 'finance_project_profit_detail id',
    `batch_number`                   int(11)              DEFAULT NULL COMMENT '批次号',
    `project_profit_id`              bigint(20)           DEFAULT NULL COMMENT '项目利润记录id',
    `year`                           int(11)     NOT NULL COMMENT '年份',
    `month`                          int(11)     NOT NULL COMMENT '月份',
    `contract_id`                    bigint(20)  NOT NULL COMMENT '合同id',
    `receipt_id`                     bigint(20)  DEFAULT NULL COMMENT '借据id',
    `receipt_first_payment_date`     date        DEFAULT NULL COMMENT '借据首次投放日期',
    `biz_type`                       varchar(20) DEFAULT NULL COMMENT 'ProjectBizType 项目类型',
    `lease_type`                     varchar(20) DEFAULT NULL COMMENT 'LeaseType 租赁类型 ',
    `contract_start_date`            date                 DEFAULT NULL COMMENT '投放日',
    `income_this_month`              bigint(20)           DEFAULT NULL COMMENT '当月收入',
    `cost_this_month`                bigint(20)           DEFAULT NULL COMMENT '当月资金成本',
    `risk_this_month`                bigint(20)           DEFAULT NULL COMMENT '当月风险金',
    `tax_this_month`                 bigint(20)           DEFAULT NULL COMMENT '当月税金及附加',
    `profit_this_month`              bigint(20)           DEFAULT NULL COMMENT '当月项目利润',
    `total_income_this_year`         bigint(20)           DEFAULT NULL COMMENT '当年累计收入',
    `total_cost_this_year`           bigint(20)           DEFAULT NULL COMMENT '当年累计资金成本',
    `total_risk_this_year`           bigint(20)           DEFAULT NULL COMMENT '当年累计风险金',
    `total_tax_this_year`            bigint(20)           DEFAULT NULL COMMENT '当年累计税金及附加',
    `total_profit_this_year`         bigint(20)           DEFAULT NULL COMMENT '当年累计利润总额',
    `expense_radio`                  int(11)              DEFAULT NULL COMMENT '费用计提比例快照',
    `create_time`                    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`                      bigint(20)           DEFAULT NULL COMMENT '创建人id',
    `update_time`                    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`                      bigint(20)           DEFAULT NULL,
    `additional_tax_this_month`      bigint(20)           DEFAULT NULL COMMENT '当月附加税',
    `stamp_tax_this_month`           bigint(20)           DEFAULT NULL COMMENT '当月印花税',
    `total_additional_tax_this_year` bigint(20)           DEFAULT NULL COMMENT '当年累计附加税',
    `total_stamp_tax_this_year`      bigint(20)           DEFAULT NULL COMMENT '当年累计印花税',
    `total_gross_profit_this_year`   bigint(20)           DEFAULT NULL COMMENT '本年累计毛利',
    `gross_profit_this_month`        bigint(20)           DEFAULT NULL COMMENT '本月毛利',
    `revenue_this_month`             bigint(20)           DEFAULT NULL COMMENT '本月收入',
    `assess_dept_id`                 bigint(20)           DEFAULT NULL COMMENT '考核部门id',
    `total_profit_this_year_before`  bigint(20)           DEFAULT NULL COMMENT '本年累计利润总额扣费前',
    `risk_balance_begin_year`        bigint(20)           DEFAULT NULL COMMENT '年初风险金余额',
    `total_risk_balance_this_year`   bigint(20)           DEFAULT NULL COMMENT '累计风险金计提/冲抵',
    `apply_credit_amount`            bigint(20)           DEFAULT NULL COMMENT '项目申报授信金额',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_year_month` (`year`, `month`) USING BTREE,
    KEY `idx_contract_id` (`contract_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='绩效考核-项目利润明细-记录表';



CREATE TABLE `kpi_parameter_base`
(
    `id`               bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `effect_month`     date        NOT NULL COMMENT '生效月份',
    `source`           varchar(20) NOT NULL DEFAULT '' COMMENT '参数来源',
    `parameter_status` varchar(50) NOT NULL DEFAULT '' COMMENT '参数状态',
    `create_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`        bigint(20)           DEFAULT NULL COMMENT '创建人id',
    `update_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`        bigint(20)           DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='绩效考核-参数设置基本表';

alter table kpi_parameter_config
    add column `parameter_base_id` bigint(20) DEFAULT NULL COMMENT '参数基本表id';


CREATE TABLE `kpi_parameter_config_record`
(
    `id`                  bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `batch_number`        int(11)              DEFAULT NULL COMMENT '批次号',
    `calculate_date`      date                 DEFAULT NULL COMMENT '计算时间',
    `parameter_base_id`   bigint(20)           DEFAULT NULL COMMENT '参数基本表id',
    `parameter_config_id` bigint(20)           DEFAULT NULL COMMENT '参数配置表id',
    `kpi_proj_guess_id`   bigint(20)           DEFAULT NULL COMMENT '项目测算表id',
    `config_code`         varchar(50) NOT NULL DEFAULT '' COMMENT '参数code',
    `config_desc`         varchar(50) NOT NULL DEFAULT '' COMMENT '参数描述',
    `config_value`        text        NOT NULL COMMENT '参数值',
    `create_time`         datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`           bigint(20)           DEFAULT NULL COMMENT '创建人id',
    `update_time`         datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`           bigint(20)           DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='绩效考核-参数设置-计算记录表';


CREATE TABLE `kpi_payment_amount_record`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `effect_month`   date                DEFAULT NULL COMMENT '生效月份',
    `batch_number`   int(11)             DEFAULT NULL COMMENT '批次号',
    `contract_id`    bigint(20) NOT NULL COMMENT '合同id',
    `proj_review_id` bigint(20) NOT NULL COMMENT '项目id',
    `payment_amount` bigint(20)          DEFAULT NULL COMMENT '当月投放金额',
    `create_time`    datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`      bigint(20)          DEFAULT NULL COMMENT '创建人id',
    `update_time`    datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`      bigint(20)          DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='绩效考核-投放信息记录表';


CREATE TABLE `kpi_project_distribution_record`
(
    `id`                               bigint(20)  NOT NULL AUTO_INCREMENT,
    `batch_number`                     int(11)              DEFAULT NULL COMMENT '批次号',
    `calculate_date`                   date                 DEFAULT NULL COMMENT '计算时间',
    `kpi_project_distribution_version` varchar(30)          DEFAULT NULL COMMENT '项目分配表版本',
    `project_distribution_id`          bigint(20)  NOT NULL COMMENT '项目分配表id',
    `contract_id`                      bigint(20)           DEFAULT NULL COMMENT '合同id',
    `distribution_status`              tinyint(4)           DEFAULT NULL COMMENT '分配状态，0-未分配，1-已分配',
    `approval_status`                  varchar(20) NOT NULL DEFAULT '' COMMENT '审批状态',
    `proj_classify`                    varchar(50)          DEFAULT NULL COMMENT '项目类别',
    `proj_source`                      varchar(20)          DEFAULT NULL COMMENT '项目来源',
    `contract_start_date`              date                 DEFAULT NULL COMMENT '合同开始时间',
    `contract_end_date`                date                 DEFAULT NULL COMMENT '合同结束时间',
    `profit_belong_dept_id`            bigint(20)           DEFAULT NULL COMMENT '利润所属部门id',
    `team_leader_id`                   bigint(20)           DEFAULT NULL COMMENT '团队长用户id',
    `effect_year`                      int(11)              DEFAULT NULL COMMENT '生效年份',
    `effect_month`                     int(11)              DEFAULT NULL COMMENT '生效月份',
    `create_time`                      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`                        bigint(20)           DEFAULT NULL COMMENT '创建人id',
    `update_time`                      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`                        bigint(20)           DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_contract_id` (`contract_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='绩效考核-项目分配记录表';



CREATE TABLE `kpi_project_distribution_weight_record`
(
    `id`                             bigint(20)  NOT NULL AUTO_INCREMENT,
    `project_distribution_record_id` bigint(20)  NOT NULL COMMENT '项目分配记录表id',
    `project_distribution_id`        bigint(20)  NOT NULL COMMENT '项目分配表id',
    `weight_type`                    varchar(50) NOT NULL COMMENT '分配比重类型',
    `weight_target`                  varchar(50)          DEFAULT NULL COMMENT '分配比重归属目标',
    `weight_value`                   int(10)              DEFAULT NULL COMMENT '分配比重数值',
    `create_time`                    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`                      bigint(20)           DEFAULT NULL COMMENT '创建人id',
    `update_time`                    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`                      bigint(20)           DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_project_distribution_id` (`project_distribution_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='绩效考核-项目分配表-分配比重信息记录表';



update kpi_proj_guess_divide k set dept_id = (select biz_dept_id from contract_base_info where id = k.contract_id) where dept_id is null;


alter table kpi_project_distribution_base_info add column `supple_describe` varchar (500)  DEFAULT NULL COMMENT '说明';
alter table kpi_project_distribution_base_info_lib add column `supple_describe` varchar (500)  DEFAULT NULL COMMENT '说明';


-- 权限管理

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjectdistributionBaseinfoGetProcess', '绩效考核-项目分配-基本信息-流程信息', 0, 288, null, null, null, 'POST', '/kpi/projectdistribution/baseinfo/get/process', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjectDistributionFileUpload', '文件上传-绩效分配', 0, 288, null, null, null, 'POST', '/file/upload', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjectDistributionFileBatchRemove', '文件删除-绩效分配', 0, 288, null, null, null, 'POST', '/file/batch/remove', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjectDistributionFileDownload', '文件下载-绩效分配', 0, 288, null, null, null, 'POST', '/file/download', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjectDistributionFileList', '文件列表-绩效分配', 0, 288, null, null, null, 'POST', '/file/list', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterConfigProjectRadioTypeSave', '项目提奖比例-项目类型系数保存', 0, 308, null, null, null, 'POST', '/kpi/parameterconfig/projectradio/type/save', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterConfigProjectradioTypeGet', '项目提奖比例-项目类型系数详情', 0, 308, null, null, null, 'POST', '/kpi/parameterconfig/projectradio/type/get', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterConfigProjectradioScaleSave', '项目提奖比例-项目规模系数保存', 0, 308, null, null, null, 'POST', '/kpi/parameterconfig/projectradio/scale/save', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterConfigProjectradioScaleGet', '项目提奖比例-项目规模系数详情', 0, 308, null, null, null, 'POST', '/kpi/parameterconfig/projectradio/scale/get', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterConfigProjectradioPaymentSave', '项目提奖比例-投放比例保存', 0, 308, null, null, null, 'POST', '/kpi/parameterconfig/projectradio/payment/save', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterConfigProjectradioPaymentGet', '项目提奖比例-投放比例详情', 0, 308, null, null, null, 'POST', '/kpi/parameterconfig/projectradio/payment/get', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterBaseCopy', '复制到绩效考核-参数设置基本表', 0, 308, null, null, null, 'POST', '/kpi/parameter/base/copy', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterBaseRemove', '删除绩效考核-参数设置基本表', 0, 308, null, null, null, 'POST', '/kpi/parameter/base/remove', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterBaseList', '绩效考核-参数设置基本表列表', 0, 308, null, null, null, 'POST', '/kpi/parameter/base/list', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterBaseClose', '绩效考核-参数设置基本表-关闭', 0, 308, null, null, null, 'POST', '/kpi/parameter/base/close', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterBaseEffect', '绩效考核-参数设置基本表-生效', 0, 308, null, null, null, 'POST', '/kpi/parameter/base/effect', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiParameterBaseAdd', '新增绩效考核-参数设置基本表', 0, 308, null, null, null, 'POST', '/kpi/parameter/base/add', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjGuessDeptPoolDetail', '绩效-项目测算表-部门池详情', 0, 486, null, null, null, 'POST', '/kpi/proj/guess/dept/pool/detail', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjGuessDeptPoolList', '绩效-项目测算表-部门池列表', 0, 486, null, null, null, 'POST', '/kpi/proj/guess/dept/pool/list', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjGuessProjManagerCompletionDetail', '绩效-项目测算表-项目经理利润完成率详情', 0, 486, null, null, null, 'POST', '/kpi/proj/guess/proj/manager/completion/detail', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjGuessProjManagerCompletionList', '绩效-项目测算表-项目经理利润完成率列表', 0, 486, null, null, null, 'POST', '/kpi/proj/guess/proj/manager/completion/list', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjGuessProjManagerDetail', '绩效-项目测算表-项目经理详情', 0, 486, null, null, null, 'POST', '/kpi/proj/guess/proj/manager/detail', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjGuessProjManagerList', '绩效-项目测算表-项目经理列表', 0, 486, null, null, null, 'POST', '/kpi/proj/guess/proj/manager/list', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjGuessCalculate', '绩效-项目测算表-测算', 0, 486, null, null, null, 'POST', '/kpi/proj/guess/calculate', 2, null);
INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('kpiProjectdistributionCompare', '绩效考核-项目分配-基本信息-比对', 0, 288, null, null, null, 'POST', '/kpi/projectdistribution/compare', 2, null);

update bifrost_menu set name = '提奖比例' where id =308;

INSERT INTO kpi_parameter_base (id, effect_month, source, parameter_status, create_by, update_by)
VALUES (24, '2024-06-30', 'PROJECT_RADIO', 'TAKE_EFFECT', 3, 3);

INSERT INTO kpi_parameter_config (config_code, config_desc, config_value, parameter_base_id) VALUES ('PROJECT_RADIO', '项目提奖比例', '[{"projectRadio":"1","projectType":"INDUSTRY","configValueType":"VALUE","projectSource":"HISTORY"},{"projectRadio":"2","projectType":"INDUSTRY","configValueType":"VALUE","projectSource":"NEW"},{"projectRadio":"4","projectType":"PUBLIC","configValueType":"VALUE","projectSource":"HISTORY"},{"projectRadio":"6","projectType":"PUBLIC","configValueType":"VALUE","projectSource":"NEW"},{"projectRadio":"5","projectType":"FACTORY","configValueType":"VALUE","projectSource":"HISTORY"},{"projectRadio":"8","projectType":"FACTORY","configValueType":"VALUE","projectSource":"NEW"}]', 24);
INSERT INTO kpi_parameter_config (config_code, config_desc, config_value, parameter_base_id) VALUES ('PROJECT_RADIO_TYPE', '项目提奖-项目类型系数', '[{"projectRadio":"1","projectType":"=回租"},{"projectRadio":"2.5","projectType":"≠回租"}]',24);
INSERT INTO kpi_parameter_config (config_code, config_desc, config_value, parameter_base_id) VALUES ('PROJECT_RADIO_SCALE', '项目提奖-项目规模系数', '[{"projectRadio":"1.2","projectScale":"≤3000"},{"projectRadio":"1","projectScale":"＞3000"}]',24);
INSERT INTO kpi_parameter_config (config_code, config_desc, config_value, parameter_base_id) VALUES ('PROJECT_RADIO_PAYMENT', '项目提奖-投放奖金系数', '[{"projectRadio":"0.05","projectType":"产业类"},{"projectRadio":"0","projectType":"公用事业类"}]',24);





