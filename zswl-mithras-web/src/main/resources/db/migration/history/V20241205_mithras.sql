-- 风险策略调整开始 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
update risk_control_strategy set metric_type = 'CAPITAL', early_warning_value_one = 70000 where metric_code = 'A10000396_ZL001';
update risk_control_strategy set metric_type = 'CREDIT_RISK', early_warning_value_one = 13000 where metric_code = 'A10000396_JC017';
update risk_control_strategy set metric_name = '单一客户集中度（集团协同业务）', computational_logic = 'R=按单个客户维度（风控行业分类为「集团协同业务」）的剩余本金之和/资产负债表「所有者权益（或股东权益）合计@期末余额」' where metric_code = 'A10000396_ZL002';
update risk_control_strategy set metric_name = '单一集团客户集中度（集团协同业务）', computational_logic = 'R=按集团维度（风控行业分类为「集团协同业务」）的剩余本金之和/资产负债表「所有者权益（或股东权益）合计@期末余额」' where metric_code = 'A10000396_ZL003';
update risk_control_strategy set computational_logic = 'R=按单个客户维度（客户注册地址不为「浙江省」和风控行业分类不为「集团协同业务」）的剩余本金之和', current_value_one = null, limit_value_one = null, early_warning_value_one = null, comparison_method_one = null, value_unit_one = null where metric_code = 'A10000396_ZL004';
update risk_control_strategy set computational_logic = 'R=按集团维度（客户注册地址不为「浙江省」和风控行业分类不为「集团协同业务」）的剩余本金之和', current_value_one = null, limit_value_one = null, early_warning_value_one = null, comparison_method_one = null, value_unit_one = null, limit_value_two = 50000 where metric_code = 'A10000396_ZL005';
update risk_control_strategy set metric_type = 'CAPITAL' where metric_code = 'A10000396_ZL006';
update risk_control_strategy set metric_name = '户均授信余额（不含公用事业类、民生消费类、集团协同业务）', computational_logic = '风控行业分类不为（公用事业类、民生消费类、集团协同业务），R=满足以上条件的「剩余本金之和」/满足以上条件「客户数量」', current_value_one = null, limit_value_one = 5500, early_warning_value_one = 5200 where metric_code = 'J10000396_JC47548';
update risk_control_strategy set metric_name = '民生消费类（含供水供热供电供气、污水处理、旅游行业等）', computational_logic = 'R=风控行业分类为「民生消费类（含供水供热供电供气、污水处理、旅游行业等）」的「剩余本金之和 - 保证金余额」', limit_value_one = 200000 where metric_code = 'A10000396_JC034';
update risk_control_strategy set metric_name = '交通运输物流行业（含冷链仓储物流、汽车经销商、普通物流、公共交通等）', computational_logic = 'R=风控行业分类为「交通运输物流行业（含冷链仓储物流、汽车经销商、普通物流、公共交通等）」的「剩余本金之和 - 保证金余额」', limit_value_one = 200000 where metric_code = 'A10000396_JC036';
update risk_control_strategy set metric_name = '造纸、精细化工、汽车零部件等传统制造行业', computational_logic = 'R=风控行业分类为「造纸、精细化工、汽车零部件等传统制造行业」的「剩余本金之和 - 保证金余额」', limit_value_one = 200000 where metric_code = 'A10000396_JC039';
update risk_control_strategy set metric_name = '建筑工程行业（含建筑材料）', computational_logic = 'R=风控行业分类为「建筑工程行业（含建筑材料）」的「剩余本金之和 - 保证金余额」', limit_value_one = 150000 where metric_code = 'A10000396_JC040';
update risk_control_strategy set metric_name = '信息产业（5G、IDC、通信服务等新基建行业）', computational_logic = 'R=风控行业分类为「5G、IDC、通信服务等」的「剩余本金之和 - 保证金余额」', limit_value_one = 100000 where metric_code = 'J10000396_JC47578';
update risk_control_strategy set computational_logic = 'R=风控行业分类为「集团内协同业务（授信主体为集团合并范围内企业）」的「剩余本金之和 - 保证金余额」/资产负债表「所有者权益（或股东权益）合计@期末余额」', current_value_one = null, limit_value_one = 5000, early_warning_value_one = null, value_unit_one = '%' where metric_code = 'A10000396_ZL007';
update risk_control_strategy set metric_name = '新能源、新材料、新科技等智能制造、先进装备制造行业', computational_logic = 'R=风控行业分类为「新能源、新材料、新科技等智能制造、先进装备制造行业」的「剩余本金之和 - 保证金余额」', limit_value_one = 150000 where metric_code = 'J10000396_JC47598';
INSERT INTO risk_control_strategy (metric_code, metric_type, metric_category, metric_name, computational_logic, current_value_one, limit_value_one, early_warning_value_one, comparison_method_one, value_unit_one, limit_value_two, early_warning_value_two, comparison_method_two, value_unit_two, current_value_two, remaining_principal, early_warning_state, quick_context, remark, null_reason)
VALUES
    ('A10000396_MD001', 'CAPITAL', 'CONTROL', '租赁资产占比', 'R=存量租赁资产剩余本金之和/资产负债表（总资产科目余额）', NULL, 8000, NULL, '>=', '%', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL),
    ('A10000396_MD002', 'MARKET_RISK', 'CONTROL', '单一客户集中度（浙江省客户）', 'R=按单个客户维度（承租人注册地址为「浙江省」）的剩余本金之和', NULL, 50000, NULL, '<=', '亿元', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL),
    ('A10000396_MD003', 'MARKET_RISK', 'CONTROL', '单一集团客户集中度（浙江省客户）', 'R=按集团维度（承租人注册地址为「浙江省」）的剩余本金之和', NULL, 100000, NULL, '<=', '亿元', NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL);
delete from risk_control_strategy where metric_code = 'J10000396_JC47558';
delete from risk_control_strategy where metric_code = 'A10000396_JC038';
delete from risk_control_strategy where metric_code = 'A10000396_ZL039';
delete from risk_control_strategy where metric_code = 'J10000396_JC47588';
-- 风险策略调整结束 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


-- 逾期催收ddl <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
CREATE TABLE `oc_letter_index`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `year`  int(11) DEFAULT NULL COMMENT '年份',
    `index` int(11) DEFAULT NULL COMMENT '最大顺序号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='函件顺序号';


CREATE TABLE `oc_doc_printing`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
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
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
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
    `data_create_time` datetime DEFAULT NULL COMMENT 'data_create_time',
    `data_create_by`   bigint(20) unsigned DEFAULT NULL COMMENT 'data_create_by',
    `data_update_time` datetime DEFAULT NULL COMMENT 'data_update_time',
    `data_update_by`   bigint(20) DEFAULT NULL COMMENT 'data_update_by',
    `repay_rate`       varchar(20) DEFAULT NULL COMMENT '还款频率',
    `version_type`     tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    `lock_version`     bigint(20) DEFAULT NULL COMMENT '乐观锁',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书用印';

CREATE TABLE `oc_litigation_case_progress`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
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
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
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
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
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
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
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
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
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
    `overdue`              tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否逾期',
    `create_by`            bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`          datetime        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`          datetime        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `lock_version`         bigint(20) NOT NULL DEFAULT '1' COMMENT '乐观锁',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='逾期催收主表';

CREATE TABLE `oc_overdue_collection_action`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
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
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
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
    `data_create_time` datetime DEFAULT NULL COMMENT 'data_create_time',
    `data_create_by`   bigint(20) unsigned DEFAULT NULL COMMENT 'data_create_by',
    `data_update_time` datetime DEFAULT NULL COMMENT 'data_update_time',
    `data_update_by`   bigint(20) DEFAULT NULL COMMENT 'data_update_by',
    `repay_rate`       varchar(20)  DEFAULT NULL COMMENT '还款频率',
    `version_type`     tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='逾期催收动作';
-- 逾期催收ddl 结束 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


-- 逾期催收dml <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
INSERT INTO bifrost_custom_tree (code, name, flag, sort_no, parent_id, create_by, update_by, en_name, icon)
VALUES ('overduemanagement', '逾期管理', 1, 51, null, null, null, null, 'icon-cuishou');

INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('overduelitigationDoc', 3, 3, null, '/overdue/litigationDoc', null, null, '诉讼文书用印');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('overduelitigationRegistration', 3, 2, null, '/overdue/litigationRegistration', null, null, '诉讼登记');
INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('overduecollection', 3, 1, null, '/overdue/collection', null, null, '逾期催收');

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '逾期管理') a
         join (select id as menu_id from bifrost_menu where code = 'overduecollection') b;
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '逾期管理') a
         join (select id as menu_id from bifrost_menu where code = 'overduelitigationRegistration') b;
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '逾期管理') a
         join (select id as menu_id from bifrost_menu where code = 'overduelitigationDoc') b;

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileDownload', '文件下载', 0, id, 'GET', '/file/download', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileListGroup', '文件列表分组', 0, id, 'POST', '/file/list/group', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileList', '文件列表', 0, id, 'POST', '/file/list', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileBatchDownload', '文件批量下载', 0, id, 'GET', '/file/batch/download', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileBatchRemove', '批量删除', 0, id, 'post', '/file/batch/remove', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileRemove', '删除文件', 0, id, 'post', '/file/remove', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingFileUpload', '上传文件', 0, id, 'post', '/file/upload', null
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'docPrintingIndexDownload', '首页列表下载', 0, id, 'POST', '/index/download', 2
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'printingSave', '文书用印编辑', 0, id, 'POST', '/printing/save', 2
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'printingAdd', '文书用印新增', 0, id, 'POST', '/printing/add', 2
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'printingSubmit', '文书用印提交审批', 0, id, 'POST', '/printing/submit', 2
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'printingDetail', '文书用印详情', 0, id, 'POST', '/printing/detail', 2
from bifrost_menu
where code = 'overduelitigationDoc';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'printingPageList', '文书用印列表', 0, id, 'POST', '/printing/pageList', 2
from bifrost_menu
where code = 'overduelitigationDoc';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileDownload', '文件下载', 0, id, 'GET', '/file/download', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileListGroup', '文件列表分组', 0, id, 'POST', '/file/list/group', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileList', '文件列表', 0, id, 'POST', '/file/list', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileBatchDownload', '文件批量下载', 0, id, 'GET', '/file/batch/download', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileBatchRemove', '批量删除', 0, id, 'post', '/file/batch/remove', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileRemove', '删除文件', 0, id, 'post', '/file/remove', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationFileUpload', '上传文件', 0, id, 'post', '/file/upload', null
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationRegistrationIndexDownload', '首页列表下载', 0, id, 'POST', '/index/download', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationSave', '诉讼登记保存', 0, id, 'POST', '/litigation/save', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationAdd', '新增诉讼登记', 0, id, 'POST', '/litigation/add', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationDetail', '诉讼登记详情', 0, id, 'GET', '/litigation/detail', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationPageList', '诉讼登记列表', 0, id, 'POST', '/litigation/pageList', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationClientOverdueinfo', '客户逾期信息展示', 0, id, 'GET', '/litigation/client/overdueinfo', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationProgressAdd', '诉讼登记新增进展', 0, id, 'POST', '/litigation/progress/add', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationDefendantRemove', '诉讼登记删除被告', 0, id, 'POST', '/litigation/defendant/remove', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationDefendantAdd', '诉讼登记新增被告', 0, id, 'POST', '/litigation/defendant/add', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationContractClient', '诉讼登记-合同相关全量客户', 0, id, 'POST', '/litigation/contract/client', 2
from bifrost_menu
where code = 'overduelitigationRegistration';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'litigationContractPulldown', '诉讼登记-客户相关合同下拉', 0, id, 'GET', '/litigation/contract/pulldown', 2
from bifrost_menu
where code = 'overduelitigationRegistration';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileDownload', '文件下载', 0, id, 'GET', '/file/download', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileListGroup', '文件列表分组', 0, id, 'POST', '/file/list/group', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileList', '文件列表', 0, id, 'POST', '/file/list', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileBatchDownload', '文件批量下载', 0, id, 'GET', '/file/batch/download', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileBatchRemove', '批量删除', 0, id, 'post', '/file/batch/remove', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileRemove', '删除文件', 0, id, 'post', '/file/remove', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionActionFileUpload', '上传文件', 0, id, 'post', '/file/upload', null
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overdueCollectionIndexDownload', '首页列表下载', 0, id, 'POST', '/index/download', 2
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactiondetail', '催收动作详情', 0, id, 'POST', '/overduecollection/action/detail', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactionsubmit', '催收提交审批', 0, id, 'POST', '/overduecollection/action/submit', 2
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionlettergenerate', '生成函件', 0, id, 'POST', '/overduecollection/letter/generate', 2
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactionupdate', '修改催收信息', 0, id, 'POST', '/overduecollection/action/update', 2
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactionadd', '新增催收动作', 0, id, 'POST', '/overduecollection/action/add', 2
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionactiondownload', '催收动作列表导出', 0, id, 'POST', '/overduecollection/action/download', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectioncontractpulldown', '合同下拉列表', 0, id, 'GET', '/overduecollection/contract/pulldown', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectioncontractexport', '合同列表导出', 0, id, 'POST', '/overduecollection/contract/export', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectioncontractlist', '合同列表', 0, id, 'GET', '/overduecollection/contract/list', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectiondetail', '催收详情', 0, id, 'POST', '/overduecollection/detail', 1
from bifrost_menu
where code = 'overduecollection';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'overduecollectionlist', '催收列表', 0, id, 'POST', '/overduecollection/list', 1
from bifrost_menu
where code = 'overduecollection';

update bifrost_menu
set name = '租金计划'
where name = '租金催收';


INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (1, 2024, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (2, 2025, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (3, 2026, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (4, 2027, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (5, 2028, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (6, 2029, 1);
INSERT INTO `oc_letter_index` (`id`, `year`, `index`)
VALUES (7, 2030, 1);
-- 逾期催收dml 结束 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
