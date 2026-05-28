CREATE TABLE `penalty_reduce_base_info`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收款明细id	',
    `notes` varchar(500) DEFAULT NULL COMMENT '备注',
    `penalty_reduce_status` varchar(20) DEFAULT 'UN_SUBMIT' COMMENT '流程状态',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL  COMMENT '修改人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='罚息减免基本表';

CREATE TABLE `penalty_reduce_detail_record`
(
    `id`                                bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收款明细id	',
    `reduce_base_id`                    bigint(20)   DEFAULT NULL COMMENT '罚息减免id',
    `client_id`                         bigint(20)   DEFAULT NULL COMMENT '客户id',
    `contract_id`                       bigint(20) NOT NULL COMMENT '合同id',
    `collection_id`                     bigint(20) NOT NULL COMMENT '收款id',
    `contract_code`                     varchar(100) DEFAULT NULL COMMENT '合同编号',
    `receipt_id`                        bigint(20)   DEFAULT NULL COMMENT '借据id',
    `receipt_code`                      varchar(20)  DEFAULT NULL COMMENT '借据编号',
    `phase`                             int(11)      DEFAULT NULL COMMENT '期项',
    `apply_credit_amount`               bigint(20) NOT NULL COMMENT '合同金额',
    `plan_collection_amount`            bigint(20)   DEFAULT NULL COMMENT '计划收款金额',
    `plan_collection_date`              datetime     DEFAULT NULL COMMENT '计划收款日期',
    `collection_amount`                 bigint(20)   DEFAULT NULL COMMENT '实收金额',
    `penalty_close_date`                date      DEFAULT NULL COMMENT '罚息截止日',
    `penalty_interest`                  bigint(20)   DEFAULT NULL COMMENT '应收罚息',
    `reduce_penalty_interest`           bigint(20)   DEFAULT NULL COMMENT '申请减免罚息',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL  COMMENT '修改人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX idx_penalty_reduce_detail_record_reduce_base_id (`reduce_base_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  ROW_FORMAT = DYNAMIC COMMENT ='罚息减免明细表';

ALTER TABLE `collection_base_info`
    ADD COLUMN `penalty_interest_calculate_flag` TINYINT(1) NULL DEFAULT 0 COMMENT '罚息计算状态 0 计算，1 不再计算';


insert into bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
values  ('rentCollectionPenaltyEffect', '租金催收罚息减免-提交审批', 0, 58, null, null, null, 'POST', '/rent/collection/penalty/effect', 2, null),
        ('rentCollectionPenaltyReductionList', '租金催收罚息减免-列表', 0, 58, null, null, null, 'POST', '/rent/collection/penalty/reduction/list', 2, null),
        ('rentCollectionPenaltyModify', '租金催收罚息减免-变更', 0, 58, null, null, null, 'POST', '/rent/collection/penalty/modify', 2, null);


-- --------------------------------------------------大熊的SQL BEGIN----------------------------------------------------
-- 多借据的实际起租日
-- 为了迎合借据的起租日绑定在借据上，所以此次变更的表为借据的相关主表
ALTER TABLE contract_receipt ADD COLUMN receipt_start_date DATE NULL comment '借据起租日期';
ALTER TABLE contract_receipt_lib ADD COLUMN receipt_start_date DATE NULL comment '借据起租日期';

-- 为了方便区分是不是第一个借据，加一个是否是第一个借据的标识，默认是0，在合同创建的时候手动设置为1
ALTER TABLE contract_receipt ADD COLUMN is_first_receipt TINYINT(4) DEFAULT '0' comment '是否是第一个借据';
ALTER TABLE contract_receipt_lib ADD COLUMN is_first_receipt TINYINT(4) DEFAULT '0' comment '是否是第一个借据';

-- 接口注册
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ('contractReceiptProcessUpdateActualStartDate', '借据-流程中变更实际起租日',
        0, 20, 'POST',
        '/contract/receipt/process/updateActualStartDate', 2),
       ('contractReceiptEditUpdateActualStartDate', '借据-编辑区变更实际起租日',
        0, 20, 'POST',
        '/contract/receipt/edit/updateActualStartDate', 2);

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ( 'riskControlOpinionMonitorClose', '待发起舆情关闭(发起流程)', 0, 398,
         'POST', '/risk/control/opinion/monitor/close', 2);

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ('newAfterLeaseCheckPlanIndexDownload', '租后-检查计划列表导出', 0,
        38, 'POST', '/index/download', 1);;


-- ----------------------------------------------------大熊的SQL END----------------------------------------------------


