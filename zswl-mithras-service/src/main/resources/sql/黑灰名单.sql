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


-- 注册sql
INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('blackGrayApprovalWarehouseSubmit', '黑灰名单库入库审批', (select id from bifrost_menu where code = 'blackListManagewarehousemainTask'), 'POST',
        '/black/gray/approval/warehouse/submit', '2'),
       ('blackGrayApprovalWarehouseAuditList', '黑灰名单入库-审批查询', (select id from bifrost_menu where code = 'blackListManagewarehousesearch'), 'GET',
        '/black/gray/approval/warehouse/auditList', '2'),
       ('blackGrayApprovalBreakBusiness', '黑灰名单库突破审批', (select id from bifrost_menu where code = 'blackListManageWarehouse'), 'POST',
        '/black/gray/approval/break/business', '2'),
       ('blackGrayApprovalBreakBusinessAuditList', '黑灰名单突破-审批查询', (select id from bifrost_menu where code = 'blackListManageWarehouse'), 'GET',
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
VALUES ('publicBlackQueryVagueEnterprise', '黑灰名单模糊查询企业信息', (select id from bifrost_menu where code = 'blackListManageWarehouse'), 'POST',
        '/public/black/query/vague/enterprise', '2'),
       ('publicBlackBatchQueryAssociatedEnterprise', '批量填充所属企业及下属企业', (select id from bifrost_menu where code = 'blackListManageWarehouse'), 'POST',
        '/public/black/batch/query/associated/enterprise', '2'),
       ('publicBlackQueryAssociatedEnterprise', '查询下属企业', (select id from bifrost_menu where code = 'blackListManageWarehouse'), 'POST',
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
       ('blackGrayWarehouseTaskDetail', '黑灰名单任务表详情', (select id from bifrost_menu where code = 'blackListManagewarehousesubTask'), 'POST',
        '/black/gray/warehouse/task/detail', '2'),
       ('blackGrayWarehouseTaskModify', '修改黑灰名单任务表', (select id from bifrost_menu where code = 'blackListManagewarehousesubTask'), 'POST',
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


