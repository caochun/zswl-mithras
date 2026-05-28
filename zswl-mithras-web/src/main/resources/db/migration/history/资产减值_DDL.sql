CREATE TABLE `ecl_business_config`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT,
    `config_module`      varchar(50)          DEFAULT NULL COMMENT '配置模块',
    `config_code`          varchar(50)         DEFAULT NULL COMMENT '配置code',
    `config_name`          varchar(50)         DEFAULT NULL COMMENT '配置名称',
    `config_value`          text         DEFAULT NULL COMMENT '配置详情',
    `config_enum`          text         DEFAULT NULL COMMENT '配置枚举',
    `order_flag`      int(11)          DEFAULT NULL COMMENT '排序',
    `version_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '版本时间',
    `config_version`          varchar(50)         DEFAULT NULL COMMENT '配置版本号',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`               bigint(20)          DEFAULT NULL COMMENT '创建人id',
    `update_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`               bigint(20)          DEFAULT NULL COMMENT '修改人id',
    PRIMARY KEY (`id`),
    unique index (`config_code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='ecl_业务配置表';


CREATE TABLE `ecl_business_config_lib`
(
    `id`               bigint(20)  NOT NULL AUTO_INCREMENT,
    `config_module`      varchar(50)          DEFAULT NULL COMMENT '配置模块',
    `config_code`      varchar(50)          DEFAULT NULL COMMENT '配置code',
    `config_name`      varchar(50)          DEFAULT NULL COMMENT '配置名称',
    `config_value`     text                 DEFAULT NULL COMMENT '配置详情',
    `config_enum`          text         DEFAULT NULL COMMENT '配置枚举',
    `order_flag`      int(11)          DEFAULT NULL COMMENT '排序',
    `version_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '版本时间',
    `config_version`   varchar(50)          DEFAULT NULL COMMENT '配置版本号',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`        bigint(20)           DEFAULT NULL COMMENT '创建人id',
    `update_time`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`        bigint(20)           DEFAULT NULL COMMENT '修改人id',
    `data_create_time` datetime             DEFAULT NULL COMMENT '数据创建时间',
    `data_create_by`   bigint(20)           DEFAULT NULL COMMENT '数据创建人id',
    `data_update_time` datetime             DEFAULT NULL COMMENT '数据更新时间',
    `data_update_by`   bigint(20)           DEFAULT NULL COMMENT '数据修改人id',
    `origin_id`        bigint(20)  NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `version`          varchar(50)          DEFAULT NULL COMMENT '版本号',
    `version_type`     tinyint(2)          DEFAULT NULL COMMENT '版本类型',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='ecl_业务配置表版本表';



CREATE TABLE `ecl_execute_record`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT,
    `model_record_key` varchar(50) DEFAULT NULL COMMENT '调用记录编号',
    `kpi_provision_detail_id`              bigint(20)           DEFAULT NULL COMMENT '拨备计提详情id',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
    `client_name` varchar(50) DEFAULT NULL COMMENT '客户名称',
    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同ID',
    `contract_code` varchar(50) DEFAULT NULL COMMENT '合同编号',
    `inner_md_level` varchar(50) DEFAULT NULL COMMENT '内评评级',
    `ecl_pd` varchar(50) DEFAULT NULL COMMENT 'ECL违约概率',
    `outer_level` varchar(50) DEFAULT NULL COMMENT '外评级别',
    `ecl_outer_pd` varchar(50) DEFAULT NULL COMMENT 'ECL外评违约概率',
    `group` varchar(50) DEFAULT NULL COMMENT '所属分组',
    `classify` varchar(50) DEFAULT NULL COMMENT '五级分类',
    `late_day` int(11) DEFAULT NULL COMMENT '逾期天数',
    `late_date` date DEFAULT NULL COMMENT '计划时间',
    `lease_type` varchar(50) DEFAULT NULL COMMENT '租赁物类型',
    `remain_principal` varchar(50) DEFAULT NULL COMMENT '剩余本金',
    `accrued_interest` varchar(50) DEFAULT NULL COMMENT '应计利息',
    `deposit` varchar(50) DEFAULT NULL COMMENT '保证金',
    `next_rent` varchar(50) DEFAULT NULL COMMENT '下一期租金',
    `risk_exposure` varchar(50) DEFAULT NULL COMMENT '风险敞口',
    `ead` varchar(50) DEFAULT NULL COMMENT 'ead计算值,融资租赁：max（剩余本金+应计利息-剩余保证金，0）经营性租赁：max（拨备计提月份下一期租金，0）',
    `contract_expiration_date` date DEFAULT NULL COMMENT '合同到期日',
    `ecl_step` varchar(50) DEFAULT NULL COMMENT '债项阶段',
    `ecl_factor_t` varchar(50) DEFAULT NULL COMMENT '期限调整系数T',
    `ecl_param_z` varchar(200) DEFAULT NULL COMMENT 'ECL基准/乐观/悲观调整因子Z',
    `ecl_param_weight` varchar(200) DEFAULT NULL COMMENT 'ECL基准/乐观/悲观情景权重',
    `lgd` varchar(50) DEFAULT NULL COMMENT '违约损失率(LGD)',
    `rzy_ecl_down_level` int(11) DEFAULT NULL COMMENT '下迁等级',
    `base_pd_forward` varchar(50) DEFAULT NULL COMMENT '基准PDforward',
    `opt_pd_forward` varchar(50) DEFAULT NULL COMMENT '乐观PDforward',
    `glo_pd_forward` varchar(50) DEFAULT NULL COMMENT '悲观PDforward',
    `ecl_base_ifrs9` varchar(50) DEFAULT NULL COMMENT '基准PDIFRS9',
    `ecl_opt_ifrs9` varchar(50) DEFAULT NULL COMMENT '乐观PDIFRS9',
    `ecl_glo_ifrs9` varchar(50) DEFAULT NULL COMMENT '悲观PDIFRS9',
    `base_ecl` varchar(50) DEFAULT NULL COMMENT '基准ECL',
    `opt_ecl` varchar(50) DEFAULT NULL COMMENT '乐观ECL',
    `glo_ecl` varchar(50) DEFAULT NULL COMMENT '悲观ECL',
    `ecl` varchar(50) DEFAULT NULL COMMENT 'ECL',
    `remark` varchar(50) DEFAULT NULL COMMENT '备注',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间-测算时间',
    `create_by`               bigint(20)          DEFAULT NULL COMMENT '创建人id',
    `update_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`               bigint(20)          DEFAULT NULL COMMENT '修改人id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='资产减值记录表';



CREATE TABLE `ecl_execute_record_lib`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT,
    `model_record_key` varchar(50) DEFAULT NULL COMMENT '调用记录编号',
    `kpi_provision_detail_id`              bigint(20)           DEFAULT NULL COMMENT '拨备计提详情id',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
    `client_name` varchar(50) DEFAULT NULL COMMENT '客户名称',
    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同ID',
    `contract_code` varchar(50) DEFAULT NULL COMMENT '合同编号',
    `inner_md_level` varchar(50) DEFAULT NULL COMMENT '内评评级',
    `ecl_pd` varchar(50) DEFAULT NULL COMMENT 'ECL违约概率',
    `outer_level` varchar(50) DEFAULT NULL COMMENT '外评级别',
    `ecl_outer_pd` varchar(50) DEFAULT NULL COMMENT 'ECL外评违约概率',
    `group` varchar(50) DEFAULT NULL COMMENT '所属分组',
    `classify` varchar(50) DEFAULT NULL COMMENT '五级分类',
    `late_day` int(11) DEFAULT NULL COMMENT '逾期天数',
    `late_date` date DEFAULT NULL COMMENT '计划时间',
    `lease_type` varchar(50) DEFAULT NULL COMMENT '租赁物类型',
    `remain_principal` varchar(50) DEFAULT NULL COMMENT '剩余本金',
    `accrued_interest` varchar(50) DEFAULT NULL COMMENT '应计利息',
    `deposit` varchar(50) DEFAULT NULL COMMENT '保证金',
    `next_rent` varchar(50) DEFAULT NULL COMMENT '下一期租金',
    `risk_exposure` varchar(50) DEFAULT NULL COMMENT '风险敞口',
    `ead` varchar(50) DEFAULT NULL COMMENT 'ead计算值,融资租赁：max（剩余本金+应计利息-剩余保证金，0）经营性租赁：max（拨备计提月份下一期租金，0）',
    `contract_expiration_date` date DEFAULT NULL COMMENT '合同到期日',
    `ecl_step` varchar(50) DEFAULT NULL COMMENT '债项阶段',
    `ecl_factor_t` varchar(50) DEFAULT NULL COMMENT '期限调整系数T',
    `ecl_param_z` varchar(200) DEFAULT NULL COMMENT 'ECL基准/乐观/悲观调整因子Z',
    `ecl_param_weight` varchar(200) DEFAULT NULL COMMENT 'ECL基准/乐观/悲观情景权重',
    `lgd` varchar(50) DEFAULT NULL COMMENT '违约损失率(LGD)',
    `rzy_ecl_down_level` int(11) DEFAULT NULL COMMENT '下迁等级',
    `base_pd_forward` varchar(50) DEFAULT NULL COMMENT '基准PDforward',
    `opt_pd_forward` varchar(50) DEFAULT NULL COMMENT '乐观PDforward',
    `glo_pd_forward` varchar(50) DEFAULT NULL COMMENT '悲观PDforward',
    `ecl_base_ifrs9` varchar(50) DEFAULT NULL COMMENT '基准PDIFRS9',
    `ecl_opt_ifrs9` varchar(50) DEFAULT NULL COMMENT '乐观PDIFRS9',
    `ecl_glo_ifrs9` varchar(50) DEFAULT NULL COMMENT '悲观PDIFRS9',
    `base_ecl` varchar(50) DEFAULT NULL COMMENT '基准ECL',
    `opt_ecl` varchar(50) DEFAULT NULL COMMENT '乐观ECL',
    `glo_ecl` varchar(50) DEFAULT NULL COMMENT '悲观ECL',
    `ecl` varchar(50) DEFAULT NULL COMMENT 'ECL',
    `remark` varchar(50) DEFAULT NULL COMMENT '备注',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间-测算时间',
    `create_by`               bigint(20)          DEFAULT NULL COMMENT '创建人id',
    `update_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`               bigint(20)          DEFAULT NULL COMMENT '修改人id',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `version_type` tinyint(3) NOT NULL default 0 COMMENT '版本类型',
    `data_create_time` datetime             DEFAULT NULL COMMENT '数据创建时间',
    `data_create_by`   bigint(20)           DEFAULT NULL COMMENT '数据创建人id',
    `data_update_time` datetime             DEFAULT NULL COMMENT '数据更新时间',
    `data_update_by`   bigint(20)           DEFAULT NULL COMMENT '数据修改人id',
    `origin_id`        bigint(20)  NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='资产减值记录表版本表';

alter table ecl_execute_record add column `source_type` tinyint(2) DEFAULT 0 COMMENT '操作类型 0 系统 1 手工';
alter table ecl_execute_record_lib add column `source_type` tinyint(2) DEFAULT 0 COMMENT '操作类型 0 系统 1 手工';


alter table kpi_provision_detail add column `accrued_interest` bigint(20) DEFAULT NULL COMMENT '应计利息';
alter table kpi_provision_detail add column `next_rent`  bigint(20) DEFAULT NULL COMMENT '下期租金';
alter table kpi_provision_detail add column `remark` varchar(50) DEFAULT NULL COMMENT '备注';

alter table contract_base_info add column `lease_item_types` varchar(128) DEFAULT NULL COMMENT '租赁物类型';
alter table contract_base_info_lib add column `lease_item_types` varchar(128) DEFAULT NULL COMMENT '租赁物类型';




alter table kpi_provision_detail add column source_type tinyint(2) NOT NULL default 0 COMMENT '来源 0 系统, 1 添加',

alter table ecl_execute_record add column receipt_id bigint(20) default null COMMENT '借据id';
alter table ecl_execute_record_lib add column receipt_id bigint(20) default null COMMENT '借据id';

alter table ecl_execute_record add column receipt_code varchar(50) default null COMMENT '借据编号';
alter table ecl_execute_record_lib add column receipt_code varchar(50) default null COMMENT '借据编号';

alter table kpi_provision_detail add column receipt_code varchar (50) default null COMMENT '借据编号';

-- 资产减值应用在预算
alter table budget_plan_profit_detail add column end_of_last_period_risk_fund_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '上年末/上月末风险准备金余额（理想值）' after end_of_last_period_risk_fund;
alter table budget_plan_profit_detail add column end_of_this_period_risk_fund_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '本年末/本月末风险准备金余额（理想值）' after end_of_this_period_risk_fund;
alter table budget_plan_profit_detail add column risk_fund_diff_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '累计风险准备金计提/转回（理想值）' after risk_fund_diff;
alter table budget_plan_profit_detail add column assessment_profit_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '考核利润（理想值）' after assessment_profit;
alter table budget_plan_profit_detail add column assessment_profit_without_expense_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '考核利润（扣费后）（理想值）' after assessment_profit_without_expense;
alter table budget_plan_profit_detail add column assessment_profit_original_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '考核利润（实际值）（理想值）' after assessment_profit_original;
alter table budget_plan_profit_detail add column assessment_profit_without_expense_original_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '考核利润（扣费后）（实际值）（理想值）' after assessment_profit_without_expense_original;
alter table budget_plan_profit_detail add column expense_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '费用（理想值）' after expense;


-- 预算
CREATE TABLE `ecl_execute_predict_base_info`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT,
    `budget_plan_id` bigint(20) default null comment '预算计划id',
    `budget_plan_name` varchar(128) DEFAULT NULL COMMENT '拨备预测计划名称',
    `predict_data_began` date default null comment '拨备预测开始日期',
    `predict_data_end` date default null comment '拨备预测结束日期',
    `source` tinyint(2) DEFAULT 0 COMMENT '拨备预测来源 0自动创建 1 手工添加',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间-测算时间',
    `create_by`               bigint(20)          DEFAULT NULL COMMENT '创建人id',
    `update_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`               bigint(20)          DEFAULT NULL COMMENT '修改人id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='资产减值预测表';


CREATE TABLE `ecl_predict_business_config`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT,
    `execute_predict_id` bigint(20) default null comment '预测计划id',
    `config_module`      varchar(50)          DEFAULT NULL COMMENT '配置模块',
    `config_code`          varchar(50)         DEFAULT NULL COMMENT '配置code',
    `config_name`          varchar(50)         DEFAULT NULL COMMENT '配置名称',
    `config_value`          text         DEFAULT NULL COMMENT '配置详情',
    `config_enum`          text         DEFAULT NULL COMMENT '配置枚举',
    `order_flag`      int(11)          DEFAULT NULL COMMENT '排序',
    `version_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '版本时间',
    `config_version`          varchar(50)         DEFAULT NULL COMMENT '配置版本号',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`               bigint(20)          DEFAULT NULL COMMENT '创建人id',
    `update_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`               bigint(20)          DEFAULT NULL COMMENT '修改人id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='ecl_预测业务配置表';


CREATE TABLE `ecl_execute_predict_record`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT,
    `execute_predict_id` bigint(20) default null comment '预测计划id',
    `budget_plan_profit_detail_id` bigint(20) DEFAULT NULL COMMENT '拨备预测记录表id',
    `kpi_provision_detail_id`              bigint(20)           DEFAULT NULL COMMENT '拨备计提详情id',
    `model_record_key` varchar(50) DEFAULT NULL COMMENT '合同编号',
    `calculation_date` date default null comment '计算日期',
    `overdue_flag`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否逾期计算 0 非逾期计算 ,1逾期计算',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
    `client_name` varchar(50) DEFAULT NULL COMMENT '客户名称',
    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同ID',
    `contract_code` varchar(50) DEFAULT NULL COMMENT '合同编号',
    `receipt_id` bigint(20) default null COMMENT '借据id',
    `receipt_code` varchar(50) default null COMMENT '借据编号',
    `inner_md_level` varchar(50) DEFAULT NULL COMMENT '内评评级',
    `ecl_pd` varchar(50) DEFAULT NULL COMMENT 'ECL违约概率',
    `outer_level` varchar(50) DEFAULT NULL COMMENT '外评级别',
    `ecl_outer_pd` varchar(50) DEFAULT NULL COMMENT 'ECL外评违约概率',
    `group` varchar(50) DEFAULT NULL COMMENT '所属分组',
    `classify` varchar(50) DEFAULT NULL COMMENT '五级分类',
    `late_day` int(11) DEFAULT NULL COMMENT '逾期天数',
    `late_date` date DEFAULT NULL COMMENT '逾期时间',
    `lease_type` varchar(50) DEFAULT NULL COMMENT '租赁物类型',
    `remain_principal` varchar(50) DEFAULT NULL COMMENT '剩余本金',
    `accrued_interest` varchar(50) DEFAULT NULL COMMENT '应计利息',
    `deposit` varchar(50) DEFAULT NULL COMMENT '保证金',
    `next_rent` varchar(50) DEFAULT NULL COMMENT '下一期租金',
    `risk_exposure` varchar(50) DEFAULT NULL COMMENT '风险敞口',
    `ead` varchar(50) DEFAULT NULL COMMENT 'ead计算值,融资租赁：max（剩余本金+应计利息-剩余保证金，0）经营性租赁：max（拨备计提月份下一期租金，0）',
    `contract_expiration_date` date DEFAULT NULL COMMENT '合同到期日',
    `ecl_step` varchar(50) DEFAULT NULL COMMENT '债项阶段',
    `ecl_factor_t` varchar(50) DEFAULT NULL COMMENT '期限调整系数T',
    `ecl_param_z` varchar(200) DEFAULT NULL COMMENT 'ECL基准/乐观/悲观调整因子Z',
    `ecl_param_weight` varchar(200) DEFAULT NULL COMMENT 'ECL基准/乐观/悲观情景权重',
    `lgd` varchar(50) DEFAULT NULL COMMENT '违约损失率(LGD)',
    `rzy_ecl_down_level` int(11) DEFAULT NULL COMMENT '下迁等级',
    `base_pd_forward` varchar(50) DEFAULT NULL COMMENT '基准PDforward',
    `opt_pd_forward` varchar(50) DEFAULT NULL COMMENT '乐观PDforward',
    `glo_pd_forward` varchar(50) DEFAULT NULL COMMENT '悲观PDforward',
    `ecl_base_ifrs9` varchar(50) DEFAULT NULL COMMENT '基准PDIFRS9',
    `ecl_opt_ifrs9` varchar(50) DEFAULT NULL COMMENT '乐观PDIFRS9',
    `ecl_glo_ifrs9` varchar(50) DEFAULT NULL COMMENT '悲观PDIFRS9',
    `base_ecl` varchar(50) DEFAULT NULL COMMENT '基准ECL',
    `opt_ecl` varchar(50) DEFAULT NULL COMMENT '乐观ECL',
    `glo_ecl` varchar(50) DEFAULT NULL COMMENT '悲观ECL',
    `ecl` varchar(50) DEFAULT NULL COMMENT 'ECL',
    `remark` varchar(50) DEFAULT NULL COMMENT '备注',
    `calculation_type` varchar(50) DEFAULT NULL COMMENT '测算类型',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间-测算时间',
    `create_by`               bigint(20)          DEFAULT NULL COMMENT '创建人id',
    `update_time`             datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`               bigint(20)          DEFAULT NULL COMMENT '修改人id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='资产减值预测详情记录表';

alter table ecl_execute_predict_record add column  `source_type`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '来源类型 0自动，1手工添加';
alter table ecl_execute_predict_record add column  `calculation_model_time`                        date DEFAULT null COMMENT '模型调用时间';







