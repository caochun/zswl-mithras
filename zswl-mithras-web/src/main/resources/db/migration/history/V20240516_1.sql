-- 剩余本金法计算收益&实际利率法增加变更场景
alter table `contract_income_sharing` add column `batch_sequence` varchar(50) NOT NULL DEFAULT '' COMMENT '批次号' after `receipt_id`;
update `contract_income_sharing` set `batch_sequence` = '20240515125308' where deleted = 0;
alter table `contract_receipt` add column `change_date` date default null comment '借据变更日期';
alter table `contract_receipt_lib` add column `change_date` date default null comment '借据变更日期';
alter table `contract_receipt` add column `income_sharing_flag` tinyint(4) DEFAULT NULL COMMENT '借据收入确认明细是否已重算，0-未重算，1-已重算';
alter table `contract_receipt_lib` add column `income_sharing_flag` tinyint(4) DEFAULT NULL COMMENT '借据收入确认明细是否已重算，0-未重算，1-已重算';

-- 租金支付通知流程变更配置
INSERT INTO bifrost_system_config (config_key, config_value, created_by, updated_by, description, status, type)
VALUES ('rentPayNoticeProcess',
        '[{"userIds":[107],"deptCode":"JCSSYWB"},{"userIds":[126],"deptCode":"GGSY"},{"userIds":[126],"deptCode":"XNYYWB"},{"userIds":[184],"deptCode":"JTYSYWB"},{"userIds":[184],"deptCode":"HYYWB"},{"userIds":[108],"deptCode":"SYCYWB"},{"userIds":[108],"deptCode":"JSSYB"},{"userIds":[108],"deptCode":"XJZZHXJJTD"}]',
        'admin', 'admin', '租金支付通知流程-部门人员配置关系', 1, 'Json');

-- 岗位新增
INSERT INTO general_dictionary (dict_key, dict_desc, code, display, sort)
VALUES ('job', '岗位类型', 'comprehensiveDept', '综合部经办', 10);