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
    `config_enum`          varchar(200)         DEFAULT NULL COMMENT '配置枚举',
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
ALTER TABLE ecl_execute_predict_record
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `client_name`;
ALTER TABLE ecl_execute_predict_record
    ADD COLUMN `evaluation_subject_name` varchar(50) NULL DEFAULT null COMMENT '评估主体名称' AFTER `client_name`;


INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
VALUES ((select id from bifrost_custom_tree where code = 'budgetPlanManage'), (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 0);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('eclExecutePredictBaseInfoAdd', '新增资产减值预测表', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/base/info/add', 2),
        ('eclExecutePredictBaseInfoList', '资产减值预测表列表', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/base/info/list', 2),
        ('eclExecutePredictBaseInfoRemove', '删除资产减值预测表', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/base/info/remove', 2),
        ('eclExecutePredictCalculation', '删除资产减值预测详情记录表', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/calculation', 2);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('eclExecutePredictRecordAdd', '新增资产减值预测详情记录表', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/record/add', 2),
        ('eclExecutePredictRecordAddCheck', '新增资产减值预测检查', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/record/addCheck', 2),
        ('eclExecutePredictRecordImport', '新增资产减值预测导入', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/record/import', 2),
        ('eclExecutePredictRecordCheck', '新增资产减值预测导入检查', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/record/check', 2),
        ('eclExecutePredictRecordModify', '修改资产减值预测详情记录表', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/record/modify', 2),
        ('eclExecutePredictRecordList', '资产减值预测详情记录表列表', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/record/list', 2),
        ('eclExecutePredictRecordRemove', '删除资产减值预测详情记录表', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'), 'POST',
         '/ecl/execute/predict/record/remove', 2);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values ('eclExecutePredictBusinessConfigModify', '修改ecl_预测业务配置表', 0, (select id
                                                                      from bifrost_menu
                                                                      where code =
                                                                            'budgetManagementProvisionForecast'), 'POST',
        '/ecl/predict/business/config/modify', 2),
       ('eclExecutePredictBusinessConfigList', 'ecl_业务配置表列表', 0, (select id from bifrost_menu where code = 'budgetManagementProvisionForecast'),
        'POST',
        '/ecl/predict/business/config/list', 2),
       ('eclExecutePredictBusinessConfigDetail', '修改ecl_预测业务配置表详情', 0, (select id
                                                                        from bifrost_menu
                                                                        where code =
                                                                              'budgetManagementProvisionForecast'), 'POST',
        '/ecl/predict/business/config/detail', 2);
