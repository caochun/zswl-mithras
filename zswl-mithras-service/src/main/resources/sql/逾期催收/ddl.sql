CREATE TABLE `oc_letter_index`
(
    `id`    bigint(20) NOT NULL AUTO_INCREMENT,
    `year`  int(11) DEFAULT NULL COMMENT '年份',
    `index` int(11) DEFAULT NULL COMMENT '最大顺序号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='函件顺序号';


CREATE TABLE `oc_doc_printing`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `code`           varchar(20) DEFAULT NULL COMMENT '用印编号',
    `type`           varchar(30) DEFAULT NULL COMMENT '用印类型',
    `reason`         text COMMENT '用印原因',
    `process_status` varchar(30) DEFAULT NULL COMMENT '审批流状态',
    `process_id`     varchar(20) DEFAULT NULL COMMENT '审批流ID',
    `create_by`      bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`    datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`    datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `lock_version`   bigint(20) NOT NULL DEFAULT '1' COMMENT '乐观锁',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书用印';

CREATE TABLE `oc_doc_printing_lib`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `code`             varchar(20) DEFAULT NULL COMMENT '用印编号',
    `type`             varchar(30) DEFAULT NULL COMMENT '用印类型',
    `reason`           text COMMENT '用印原因',
    `process_status`   varchar(30) DEFAULT NULL COMMENT '审批流状态',
    `process_id`       varchar(20) DEFAULT NULL COMMENT '审批流ID',
    `create_by`        bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`      datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`      datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version`          varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`        bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime    DEFAULT NULL,
    `data_create_by`   bigint(20) unsigned DEFAULT NULL,
    `data_update_time` datetime    DEFAULT NULL,
    `data_update_by`   bigint(20) DEFAULT NULL,
    `repay_rate`       varchar(20) DEFAULT NULL COMMENT '还款频率',
    `version_type`     tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    `lock_version`     bigint(20) DEFAULT NULL COMMENT '乐观锁',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书用印';

CREATE TABLE `oc_litigation_case_progress`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `lr_id`       bigint(20) DEFAULT NULL COMMENT '所属诉讼登记id',
    `stage`       varchar(20) DEFAULT NULL COMMENT '诉讼阶段',
    `status`      varchar(30) DEFAULT NULL COMMENT '诉讼状态',
    `create_by`   bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诉讼登记案件进展';

CREATE TABLE `oc_litigation_defendant`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
    `lr_id`              bigint(20) DEFAULT NULL COMMENT '所属诉讼登记Id',
    `name`               varchar(50) DEFAULT NULL COMMENT '被告名称',
    `role`               varchar(20) DEFAULT NULL COMMENT '合同角色',
    `certificate_type`   varchar(20) DEFAULT NULL COMMENT '证件类型',
    `certificate_number` varchar(30) DEFAULT NULL COMMENT '证件号码',
    `create_by`          bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`        datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`          bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`        datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诉讼等级被告信息';

CREATE TABLE `oc_litigation_registration`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `code`           varchar(20)   DEFAULT NULL COMMENT '诉讼登记编号',
    `contract_ids`   varchar(255)  DEFAULT NULL COMMENT '合同ids',
    `contract_codes` varchar(1000) DEFAULT NULL COMMENT '合同编号',
    `client_id`      bigint(20) DEFAULT NULL COMMENT '客户id',
    `client_name`    varchar(50)   DEFAULT NULL COMMENT '客户名称',
    `status`         varchar(20)   DEFAULT NULL COMMENT '诉讼状态',
    `create_by`      bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`    datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`    datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `lock_version`   bigint(20) NOT NULL DEFAULT '1' COMMENT '乐观锁',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诉讼登记';

CREATE TABLE `oc_litigation_trial_info`
(
    `id`                           bigint(20) NOT NULL AUTO_INCREMENT,
    `lr_id`                        bigint(20) DEFAULT NULL COMMENT '所属诉讼登记Id',
    `f_case_no`                    varchar(40)  DEFAULT NULL COMMENT '一审案号',
    `f_filing_date`                date         DEFAULT NULL COMMENT '一审立案时间',
    `f_accepting_court`            varchar(100) DEFAULT NULL COMMENT '一审受理法院',
    `f_hearing_date`               date         DEFAULT NULL COMMENT '一审开庭日期',
    `f_judgment_date`              date         DEFAULT NULL COMMENT '一审判决日期',
    `s_case_no`                    varchar(40)  DEFAULT NULL COMMENT '二审案号',
    `s_filing_date`                date         DEFAULT NULL COMMENT '二审立案时间',
    `s_accepting_court`            varchar(100) DEFAULT NULL COMMENT '二审受理法院',
    `s_hearing_date`               date         DEFAULT NULL COMMENT '二审开庭日期',
    `s_judgment_date`              date         DEFAULT NULL COMMENT '二审判决日期',
    `t_case_no`                    varchar(40)  DEFAULT NULL COMMENT '再审案号',
    `t_filing_date`                date         DEFAULT NULL COMMENT '再审立案时间',
    `t_accepting_court`            varchar(100) DEFAULT NULL COMMENT '再审受理法院',
    `t_hearing_date`               date         DEFAULT NULL COMMENT '再审开庭日期',
    `t_judgment_date`              date         DEFAULT NULL COMMENT '再审判决日期',
    `execution_no`                 varchar(100) DEFAULT NULL COMMENT '执行案号',
    `execution_date`               date         DEFAULT NULL COMMENT '执行时间',
    `preservation_completion_date` date         DEFAULT NULL COMMENT '保全完成日期',
    `sealing_expiration_date`      date         DEFAULT NULL COMMENT '查封到期日',
    `create_by`                    bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                    bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                  datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审判信息';

CREATE TABLE `oc_overdue_collection`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `client_id`            bigint(20) DEFAULT NULL COMMENT '客户id',
    `client_name`          varchar(100)    DEFAULT NULL COMMENT '客户名称',
    `risk_exposure`        bigint(20) DEFAULT NULL COMMENT '风险敞口',
    `overdue_rent`         bigint(20) DEFAULT NULL COMMENT '逾期租金',
    `late_charge`          bigint(20) DEFAULT NULL COMMENT '逾期罚息',
    `cur_max_overdue_days` int(11) DEFAULT NULL COMMENT '当前最大逾期天数',
    `project_sponsor_name` varchar(20)     DEFAULT NULL COMMENT '项目主办',
    `project_sponsor`      bigint(20) DEFAULT NULL COMMENT '项目主办Id',
    `biz_dept_name`        varchar(50)     DEFAULT NULL COMMENT '业务部门名称',
    `biz_dept`             bigint(20) DEFAULT NULL COMMENT '业务部门id',
    `overdue`              bit(1) NOT NULL DEFAULT b'0' COMMENT '是否逾期',
    `create_by`            bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`          datetime        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`          datetime        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `lock_version`         bigint(20) NOT NULL DEFAULT '1' COMMENT '乐观锁',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='逾期催收主表';

CREATE TABLE `oc_overdue_collection_action`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `code`           varchar(20)  DEFAULT NULL COMMENT '标号',
    `oc_id`          bigint(20) DEFAULT NULL COMMENT '逾期催收id',
    `date`           datetime     DEFAULT NULL COMMENT '催收日期',
    `type`           varchar(20)  DEFAULT NULL COMMENT '催收类型',
    `describe`       text COMMENT '催收进展/发函原因',
    `letter_type`    varchar(20)  DEFAULT NULL COMMENT '发函类型',
    `contract_ids`   varchar(100) DEFAULT NULL COMMENT '合同id',
    `contract_codes` text COMMENT '合同编号',
    `process_status` varchar(30)  DEFAULT NULL COMMENT '审批流状态',
    `process_id`     varchar(20)  DEFAULT NULL COMMENT '审批流ID',
    `create_by`      bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`    datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`    datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='逾期催收动作';

CREATE TABLE `oc_overdue_collection_action_lib`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `code`             varchar(20)  DEFAULT NULL COMMENT '标号',
    `oc_id`            bigint(20) DEFAULT NULL COMMENT '逾期催收id',
    `date`             datetime     DEFAULT NULL COMMENT '催收日期',
    `type`             varchar(20)  DEFAULT NULL COMMENT '催收类型',
    `describe`         text COMMENT '催收进展/发函原因',
    `letter_type`      varchar(20)  DEFAULT NULL COMMENT '发函类型',
    `contract_ids`     varchar(100) DEFAULT NULL COMMENT '合同id',
    `contract_codes`   text COMMENT '合同编号',
    `process_status`   varchar(30)  DEFAULT NULL COMMENT '审批流状态',
    `process_id`       varchar(20)  DEFAULT NULL COMMENT '审批流ID',
    `create_by`        bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`      datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`      datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version`          varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`        bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime     DEFAULT NULL,
    `data_create_by`   bigint(20) unsigned DEFAULT NULL,
    `data_update_time` datetime     DEFAULT NULL,
    `data_update_by`   bigint(20) DEFAULT NULL,
    `repay_rate`       varchar(20)  DEFAULT NULL COMMENT '还款频率',
    `version_type`     tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='逾期催收动作';