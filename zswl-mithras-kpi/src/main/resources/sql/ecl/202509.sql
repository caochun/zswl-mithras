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

INSERT INTO ecl_business_config (id, config_module, config_code, config_name, config_value, order_flag, version_time, config_version,
                                 deleted, create_time, create_by, update_time, update_by, config_enum) VALUES (1, 'ECL', 'RATING_MAPPING', '国内评级与穆迪评级映射', '{"data":[{"innerLevel":"","outerLevel":"Aaa"},{"innerLevel":"","outerLevel":"Aa1"},{"innerLevel":"","outerLevel":"Aa2"},{"innerLevel":"","outerLevel":"Aa3"},{"innerLevel":"","outerLevel":"A1"},{"innerLevel":"","outerLevel":"A2"},{"innerLevel":"","outerLevel":"A3"},{"innerLevel":"AAA","outerLevel":"Baa1"},{"innerLevel":"AA+","outerLevel":"Baa2"},{"innerLevel":"AA","outerLevel":"Baa3"},{"innerLevel":"AA-","outerLevel":"Ba1"},{"innerLevel":"A+","outerLevel":"Ba2"},{"innerLevel":"A","outerLevel":"Ba3"},{"innerLevel":"A-","outerLevel":"B1"},{"innerLevel":"BBB+","outerLevel":"B2"},{"innerLevel":"BBB","outerLevel":"B3"},{"innerLevel":"BBB-","outerLevel":"Caa1"},{"innerLevel":"BB+","outerLevel":"Caa2"},{"innerLevel":"BB","outerLevel":"Caa3"},{"innerLevel":"BB-","outerLevel":"Caa3"},{"innerLevel":"B","outerLevel":"Ca-C"},{"innerLevel":"C","outerLevel":"Ca-C"},{"innerLevel":"D","outerLevel":"D"}],"enums":{"innerLevelEnum":["AAA","AA+","AA","AA-","A+","A","A-","BBB+","BBB","BBB-","BB+","BB","BB-","B","C","D"],"outerLevelEnum":["Aaa","Aa1","Aa2","Aa3","A1","A2","A3","Baa1","Baa2","Baa3","Ba1","Ba2","Ba3","B1","B2","B3","Caa1","Caa2","Caa3","Ca-C","D"]}}', 10, '2025-11-06 09:25:07', '1', 0, '2025-09-30 09:05:03', null, '2025-11-06 09:25:07', null, '{"enums":{"innerLevelEnum":["AAA","AA+","AA","AA-","A+","A","A-","BBB+","BBB","BBB-","BB+","BB","BB-","B","C","D"],"outerLevelEnum":["Aaa","Aa1","Aa2","Aa3","A1","A2","A3","Baa1","Baa2","Baa3","Ba1","Ba2","Ba3","B1","B2","B3","Caa1","Caa2","Caa3","Ca-C","D"]}}');
INSERT INTO ecl_business_config (id, config_module, config_code, config_name, config_value, order_flag, version_time, config_version,
                                 deleted, create_time, create_by, update_time, update_by, config_enum) VALUES (2, 'ECL', 'BREACH_MAPPING', '穆迪评级和违约概率映射', '{"data":[{"outerLevel":"Aaa","outerPd":0.0005},{"outerLevel":"Aa1","outerPd":0.0005},{"outerLevel":"Aa2","outerPd":0.0005},{"outerLevel":"Aa3","outerPd":0.0005},{"outerLevel":"A1","outerPd":0.0005},{"outerLevel":"A2","outerPd":0.0005923576304433001},{"outerLevel":"A3","outerPd":0.0008926642746635},{"outerLevel":"Baa1","outerPd":0.0013452169201641},{"outerLevel":"Baa2","outerPd":0.0020271994899513},{"outerLevel":"Baa3","outerPd":0.0030549257227284},{"outerLevel":"Ba1","outerPd":0.0046036767558639},{"outerLevel":"Ba2","outerPd":0.0069375957375333},{"outerLevel":"Ba3","outerPd":0.010454738064772899},{"outerLevel":"B1","outerPd":0.0157549606719911},{"outerLevel":"B2","outerPd":0.023742229048507403},{"outerLevel":"B3","outerPd":0.0357787906886949},{"outerLevel":"Caa1","outerPd":0.053917509620939205},{"outerLevel":"Caa2","outerPd":0.0812519872183},{"outerLevel":"Caa3","outerPd":0.12244418322241801},{"outerLevel":"Ca-C","outerPd":0.18451952399298902},{"outerLevel":"D","outerPd":1}],"enums":{"innerLevelEnum":["AAA","AA+","AA","AA-","A+","A","A-","BBB+","BBB","BBB-","BB+","BB","BB-","B","C","D"],"outerLevelEnum":["Aaa","Aa1","Aa2","Aa3","A1","A2","A3","Baa1","Baa2","Baa3","Ba1","Ba2","Ba3","B1","B2","B3","Caa1","Caa2","Caa3","Ca-C","D"]}}', 20, '2025-11-06 09:24:13', '1983711528058695680', 0, '2025-09-30 09:05:49', null, '2025-11-06 09:24:13', 126, '{"enums":{"innerLevelEnum":["AAA","AA+","AA","AA-","A+","A","A-","BBB+","BBB","BBB-","BB+","BB","BB-","B","C","D"],"outerLevelEnum":["Aaa","Aa1","Aa2","Aa3","A1","A2","A3","Baa1","Baa2","Baa3","Ba1","Ba2","Ba3","B1","B2","B3","Caa1","Caa2","Caa3","Ca-C","D"]}}');
INSERT INTO ecl_business_config (id, config_module, config_code, config_name, config_value, order_flag, version_time, config_version,
                                 deleted, create_time, create_by, update_time, update_by, config_enum) VALUES (3, 'ECL', 'FORWARD_Z', '前瞻调整因子Z', '{"data":[{"factorBaseZ":-1.031384843,"factorGloZ":-1.236121174,"factorOptZ":-0.754934099,"group":"政信"},{"factorBaseZ":-0.866586426,"factorGloZ":-1.044325582,"factorOptZ":-0.708171266,"group":"制造业"},{"factorBaseZ":-0.688623943,"factorGloZ":-0.871855758,"factorOptZ":-0.260303457,"group":"非制造业"},{"factorBaseZ":-0.688623943,"factorGloZ":-0.871855758,"factorOptZ":-0.260303457,"group":"新能源"}],"enums":{"leaseTypeEnum":["factorBaseZ","factorGloZ","factorOptZ","group"]}}', 30, '2025-11-06 09:25:59', '1', 0, '2025-09-30 09:05:49', null, '2025-11-06 09:25:59', null, '{"enums":{"leaseTypeEnum":["factorBaseZ","factorGloZ","factorOptZ","group"]}}');
INSERT INTO ecl_business_config (id, config_module, config_code, config_name, config_value, order_flag, version_time, config_version,
                                 deleted, create_time, create_by, update_time, update_by, config_enum) VALUES (4, 'ECL', 'LOSS_LGD', '违约损失率LGD', '{"data":[{"leaseType":"不含船","lgd":0.45},{"leaseType":"含船","lgd":0.32770000000000005}],"enums":{"leaseTypeEnum":["不含船","含船"]}}', 40, '2025-11-06 09:26:33', '1983419460414746624', 0, '2025-09-30 09:05:49', null, '2025-11-06 09:26:33', 126, '{"enums":{"leaseTypeEnum":["不含船","含船"]}}');
INSERT INTO ecl_business_config (id, config_module, config_code, config_name, config_value, order_flag, version_time, config_version,
                                 deleted, create_time, create_by, update_time, update_by, config_enum) VALUES (5, 'ECL', 'SCENARIO_WEIGHT', '情景权重', '{"data":[{"scene":"基准情景","sceneWeight":0.8},{"scene":"乐观情景","sceneWeight":0.1},{"scene":"悲观情景","sceneWeight":0.1}],"enums":{"sceneEnum":["基准情景","乐观情景","悲观情景"]}}', 50, '2025-11-06 09:27:19', '1', 0, '2025-09-30 09:06:32', null, '2025-11-06 09:27:19', null, '{"enums":{"sceneEnum":["基准情景","乐观情景","悲观情景"]}}');
INSERT INTO ecl_business_config (id, config_module, config_code, config_name, config_value, order_flag, version_time, config_version,
                                             deleted, create_time, create_by, update_time, update_by, config_enum) VALUES (6, 'ECL', 'INNER_BREACH_MAPPING', '内部评级和违约概率映射', '{"data":[{"innerLevel":"AAA","innerPdUpper":"0.003","innerPdLower":"1e-5","innerPd":"1.7320508075688773e-4"},{"innerLevel":"AA+","innerPdUpper":"0.00487","innerPdLower":"0.003","innerPd":"0.0038223029707232786"},{"innerLevel":"AA","innerPdUpper":"0.0079","innerPdLower":"0.00487","innerPd":"0.00620266071940099"},{"innerLevel":"AA-","innerPdUpper":"0.01282","innerPdLower":"0.0079","innerPd":"0.010063697133757554"},{"innerLevel":"A+","innerPdUpper":"0.0208","innerPdLower":"0.01282","innerPd":"0.0163296050166561"},{"innerLevel":"A","innerPdUpper":"0.03375","innerPdLower":"0.0208","innerPd":"0.02649528259898354"},{"innerLevel":"A-","innerPdUpper":"0.05477","innerPdLower":"0.03375","innerPd":"0.04299404028467201"},{"innerLevel":"BBB","innerPdUpper":"0.08888","innerPdLower":"0.05477","innerPd":"0.06977075031845364"},{"innerLevel":"BB","innerPdUpper":"0.14422","innerPdLower":"0.08888","innerPd":"0.11321781485261054"},{"innerLevel":"B","innerPdUpper":"0.23403","innerPdLower":"0.14422","innerPd":"0.18371664758535083"},{"innerLevel":"CCC","innerPdUpper":"0.37977","innerPdLower":"234.03","innerPd":"9.427490286391176"},{"innerLevel":"CC","innerPdUpper":"0.61625","innerPdLower":"0.37977","innerPd":"0.4837698445542053"},{"innerLevel":"C","innerPdUpper":"1","innerPdLower":"0.61625","innerPd":"0.7850159234053791"},{"innerLevel":"D","innerPdUpper":"1","innerPdLower":"1","innerPd":"1"}]}', 60, '2025-11-07 10:31:29', '1986622503731343360', 0, '2025-11-06 09:21:18', null, '2025-11-07 10:31:29', 125, '{"enums":{"innerLevelEnum":["AAA","AA+","AA","AA-","A+","A","A-","BBB","BB","B","CCC","CC","C","D"]}}');
--! 权限相关
update bifrost_menu set sort_no = 10, path = '/budget/provisioning/impairment' where id = 488;

INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('budgetProvisionDataSearch', 3, 30, null, '/budget/provisioning/dataSearch', 488, null, '数据校对');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('budgetProvisionParamConfig', 3, 20, null, '/budget/provisioning/paramsConfig', 488, null, '参数配置');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('budgetManagementProvisionForecast', 3, 40, null, '/budgetManagement/provisionForecast', null, 488, '拨备预测');

INSERT INTO bifrost_custom_tree (code, name, flag, sort_no, parent_id, en_name, icon, path, lang_env)
VALUES ('overdueLossImpairment', '逾期信用损失减值', 1, 80, 139, null, null, null, null);

update bifrost_custom_tree_menu_ref set custom_tree_id = (select id from bifrost_custom_tree where code = 'overdueLossImpairment') where id = 480;
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
VALUES ((select id from bifrost_custom_tree where code = 'overdueLossImpairment'), (select id from bifrost_menu where code = 'budgetProvisionParamConfig'), 0);
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
VALUES ((select id from bifrost_custom_tree where code = 'overdueLossImpairment'), (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 0);


update bifrost_org_menu_function set custom_tree_id = (select id from bifrost_custom_tree where code = 'overdueLossImpairment') where menu_id = 488;
update bifrost_role_menu_function set custom_tree_id = (select id from bifrost_custom_tree where code = 'overdueLossImpairment') where menu_id = 488



    insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('eclBusinessConfigList', 'ecl_业务配置表列表', 0, (select id from bifrost_menu where code = 'budgetProvisionParamConfig'), 'POST',
    '/ecl/business/config/list', 2),
    ('eclBusinessConfigVersionList', 'ecl_业务配置表版本列表', 0, (select id from bifrost_menu where code = 'budgetProvisionParamConfig'), 'POST',
    '/ecl/business/config/version/list', 2),
    ('eclBusinessConfigModify', '修改ecl_业务配置表', 0, (select id from bifrost_menu where code = 'budgetProvisionParamConfig'), 'POST',
    '/ecl/business/config/modify', 2),
    ('eclBusinessConfigDetail', 'ecl_业务配置表详情', 0, (select id from bifrost_menu where code = 'budgetProvisionParamConfig'), 'POST',
    '/ecl/business/config/detail', 2),
    ('eclBusinessConfigVersionDetail', 'ecl_业务配置表版本详情', 0, (select id from bifrost_menu where code = 'budgetProvisionParamConfig'), 'POST',
    '/ecl/business/config/version/detail', 2);

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('eclExecuteRecordAdd', '新增资产减值记录表', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
         '/ecl/execute/record/add', 2),
        ('eclExecuteRecordModify', '修改资产减值记录表', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
         '/ecl/execute/record/modify', 2),
        ('eclExecuteRecordListCompare', '资产减值记录表比对列表', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
         '/ecl/execute/record/list/compare', 2),
        ('eclExecuteRecordList', '资产减值记录表列表', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
         '/ecl/execute/record/list', 2),
        ('eclExecuteRecordRemove', '删除资产减值记录表', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
         '/ecl/execute/record/remove', 2),
        ('eclExecuteRecordImport', '新增资产减值记录表导入', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
         '/ecl/execute/record/import', 2),
        ('eclExecuteRecordAddCheck', '新增资产减值记录表检查', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
         '/ecl/execute/record/addCheck', 2),
        ('eclExecuteRecordAllRemove', '删除资产减值全量记录表', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
         '/ecl/execute/record/all/remove', 2);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values
('eclReceiptlistByContract', '资产减值获取借据编号', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
 '/receipt/list/bycontract', 2),
('selectcontract-provisioning', '资产减值获取合同编号', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
 '/contract/base/info/list', 2),
('eclContractBaseInfoModifyLeaseItem', '修改合同基本信息表-租赁物类型', 0, (select id from bifrost_menu where code = 'QX0118'), 'POST',
 '/contract/base/info/modify/leaseItem', 2);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values
('eclFileDownloadTemplate', '资产减值记录导入模版下载', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'GET',
 '/file/download/template', 2);

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values
('eclExecuteRecordImportCheck', '资产减值记录导入检查', 0, (select id from bifrost_menu where code = 'budgetProvisionDataSearch'), 'POST',
 '/ecl/execute/record/import/check', 2);


alter table kpi_provision_detail add column source_type tinyint(2) NOT NULL default 0 COMMENT '来源 0 系统, 1 添加',

alter table ecl_execute_record add column receipt_id bigint(20) default null COMMENT '借据id';
alter table ecl_execute_record_lib add column receipt_id bigint(20) default null COMMENT '借据id';

alter table ecl_execute_record add column receipt_code varchar(50) default null COMMENT '借据编号';
alter table ecl_execute_record_lib add column receipt_code varchar(50) default null COMMENT '借据编号';

alter table kpi_provision_detail add column receipt_code varchar (50) default null COMMENT '借据编号';

