ALTER TABLE `lease_item_vat_invoice_product`
    ADD COLUMN `vehicle_invoice_car_vin` varchar(255) NULL COMMENT '车辆识别代号/车架号码 （车辆发票使用）';
-- 租后检查计划-客户信息表字段新增
ALTER TABLE new_after_lease_check_plan_client
    ADD check_change_status varchar(20) COMMENT '检查人变更状态';

-- 财资-【资金管理-融资管理】新增字段
ALTER TABLE fund_financing_base_info
    ADD COLUMN initial_interest_received_once tinyint(1) DEFAULT NULL COMMENT '是否期初一次性收息 0-否 1-是';
ALTER TABLE fund_financing_base_info_lib
    ADD COLUMN initial_interest_received_once tinyint(1) DEFAULT NULL COMMENT '是否期初一次性收息 0-否 1-是';

UPDATE fund_financing_base_info
SET initial_interest_received_once = 1
WHERE financing_code IN
      ("DK202304130006-09", "DK202304140011-01", "DK202304140022-19", "DK202304120003-07", "DK202304120003-08",
       "DK202304140022-22", "DK202304140022-26", "DK202307060036-02", "DK202311300045-01", "DK202311300045-02",
       "DK202304130006-29");

UPDATE fund_financing_base_info
SET initial_interest_received_once = 0
WHERE financing_code NOT IN
      ("DK202304130006-09", "DK202304140011-01", "DK202304140022-19", "DK202304120003-07", "DK202304120003-08",
       "DK202304140022-22", "DK202304140022-26", "DK202307060036-02", "DK202311300045-01", "DK202311300045-02",
       "DK202304130006-29");

UPDATE fund_financing_base_info_lib
SET initial_interest_received_once = 1
WHERE financing_code IN
      ("DK202304130006-09", "DK202304140011-01", "DK202304140022-19", "DK202304120003-07", "DK202304120003-08",
       "DK202304140022-22", "DK202304140022-26", "DK202307060036-02", "DK202311300045-01", "DK202311300045-02",
       "DK202304130006-29");

UPDATE fund_financing_base_info_lib
SET initial_interest_received_once = 0
WHERE financing_code NOT IN
      ("DK202304130006-09", "DK202304140011-01", "DK202304140022-19", "DK202304120003-07", "DK202304120003-08",
       "DK202304140022-22", "DK202304140022-26", "DK202307060036-02", "DK202311300045-01", "DK202311300045-02",
       "DK202304130006-29");

-- ocr优化需求
ALTER TABLE `lease_item_vat_invoice_product`
    ADD COLUMN `vehicle_invoice_car_vin` varchar(255) NULL COMMENT '车辆识别代号/车架号码 （车辆发票使用）' AFTER `deleted`;

-- 流水表
DROP TABLE IF EXISTS `finance_flow_record`;
CREATE TABLE `finance_flow_record`
(
    `id`                            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `billno`                        varchar(255)   DEFAULT NULL COMMENT '交易明细编号',
    `billstatus`                    varchar(255)   DEFAULT NULL COMMENT '单据状态',
    `auditdate`                     varchar(255)   DEFAULT NULL COMMENT '审核日期',
    `modifytime`                    varchar(255)   DEFAULT NULL COMMENT '最后更新时间',
    `createtime`                    varchar(255)   DEFAULT NULL COMMENT '创建时间',
    `bizdate`                       varchar(255)   DEFAULT NULL COMMENT '交易日期',
    `sourcebillid`                  bigint(20) DEFAULT NULL COMMENT '源单id',
    `amount`                        decimal(19, 4) DEFAULT NULL COMMENT '金额',
    `locamt`                        decimal(19, 4) DEFAULT NULL COMMENT '金额折本位币',
    `exchangerate`                  decimal(19, 4) DEFAULT NULL COMMENT '汇率',
    `description`                   varchar(255)   DEFAULT NULL COMMENT '描述',
    `bizrefno`                      varchar(255)   DEFAULT NULL COMMENT '业务参考号',
    `debitamount`                   decimal(19, 4) DEFAULT NULL COMMENT '付款金额',
    `creditamount`                  decimal(19, 4) DEFAULT NULL COMMENT '收款金额',
    `transbalance`                  decimal(19, 4) DEFAULT NULL COMMENT '余额',
    `oppunit`                       varchar(255)   DEFAULT NULL COMMENT '对方户名',
    `oppbank`                       varchar(255)   DEFAULT NULL COMMENT '对方开户行',
    `ismatchereceipt`               tinyint(1) DEFAULT NULL COMMENT '跟电子回单匹配',
    `isdataimport`                  tinyint(1) DEFAULT NULL COMMENT '是否导入',
    `detailid`                      varchar(255)   DEFAULT NULL COMMENT '明细流水号',
    `isdowntobankstate`             tinyint(1) DEFAULT NULL COMMENT '已经下载到银行对账单',
    `isnoreceipt`                   tinyint(1) DEFAULT NULL COMMENT '确认无回单',
    `receiptno`                     varchar(255)   DEFAULT NULL COMMENT '电子回单关联标记',
    `originalbankcheckflag`         varchar(255)   DEFAULT NULL COMMENT '对账标识码(银行返回)',
    `datasource`                    varchar(255)   DEFAULT NULL COMMENT '数据来源',
    `biztype`                       varchar(255)   DEFAULT NULL COMMENT '业务类型',
    `isrefund`                      tinyint(1) DEFAULT NULL COMMENT '是否退票',
    `bankinterface`                 varchar(255)   DEFAULT NULL COMMENT '银行接口',
    `isreced`                       tinyint(1) DEFAULT NULL COMMENT '是否接收',
    `biztime`                       datetime       DEFAULT NULL COMMENT '交易时间',
    `iskdretflag`                   tinyint(1) DEFAULT NULL COMMENT '是否银企付标',
    `istransup`                     tinyint(1) DEFAULT NULL COMMENT '银行上传',
    `istransdown`                   tinyint(1) DEFAULT NULL COMMENT '银行下载',
    `isbankwithholding`             tinyint(1) DEFAULT NULL COMMENT '银行代扣',
    `recedbilltype`                 varchar(255)   DEFAULT NULL COMMENT '接收单据类型',
    `receredtype`                   varchar(255)   DEFAULT NULL COMMENT '入账状态',
    `sortno`                        bigint(20) DEFAULT NULL COMMENT '排序号',
    `rulename`                      varchar(255)   DEFAULT NULL COMMENT '适配规则',
    `businessbillnum`               varchar(255)   DEFAULT NULL COMMENT '票据号',
    `autorecorpay`                  tinyint(1) DEFAULT NULL COMMENT '自动收付款',
    `smartmatch`                    varchar(255)   DEFAULT NULL COMMENT '智能匹配',
    `oppbanknumber`                 varchar(255)   DEFAULT NULL COMMENT '对方账号',
    `bankcheckflag`                 varchar(255)   DEFAULT NULL COMMENT '对账标识码',
    `recedbillnumber`               varchar(255)   DEFAULT NULL COMMENT '接收单据编号',
    `claimnoticebillno`             varchar(255)   DEFAULT NULL COMMENT '收款认领通知',
    `sourcebilltype`                varchar(255)   DEFAULT NULL COMMENT '源单类型',
    `transfercharge`                decimal(19, 4) DEFAULT NULL COMMENT '手续费',
    `flowserialno`                  varchar(255)   DEFAULT NULL COMMENT '流程序列号',
    `sortid`                        varchar(255)   DEFAULT NULL COMMENT '排序ID',
    `requestserialno`               varchar(255)   DEFAULT NULL COMMENT '银企请求流水号',
    `responseserailno`              varchar(255)   DEFAULT NULL COMMENT '银行响应流水号',
    `kdretflag`                     varchar(255)   DEFAULT NULL COMMENT 'KD标识',
    `agentaccno`                    varchar(255)   DEFAULT NULL COMMENT '被代理账号',
    `agentaccname`                  varchar(255)   DEFAULT NULL COMMENT '被代理户名',
    `agentaccbankname`              varchar(255)   DEFAULT NULL COMMENT '被代理户开户行',
    `bustype`                       varchar(255)   DEFAULT NULL COMMENT 'busType',
    `batchno`                       varchar(255)   DEFAULT NULL COMMENT '银企付款提交的批次号',
    `billnobillno`                  varchar(255)   DEFAULT NULL COMMENT '票号',
    `transdate`                     datetime       DEFAULT NULL COMMENT '记账日期',
    `extdata`                       varchar(255)   DEFAULT NULL COMMENT 'extData',
    `lastmodifytime`                datetime       DEFAULT NULL COMMENT '最后修改时间',
    `bankdetailno`                  varchar(255)   DEFAULT NULL COMMENT '银行流水号',
    `uniqueseq`                     varchar(255)   DEFAULT NULL COMMENT '银行主键',
    `cico_reconciliationcode`       varchar(255)   DEFAULT NULL COMMENT '保融对账码',
    `cico_activepayment`            varchar(255)   DEFAULT NULL COMMENT '付款状态',
    `cico_bruid`                    varchar(255)   DEFAULT NULL COMMENT '保融uid',
    `cico_billno`                   varchar(255)   DEFAULT NULL COMMENT '付款单号',
    `receredway`                    varchar(255)   DEFAULT NULL COMMENT '入账方式',
    `ishandlink`                    tinyint(1) DEFAULT NULL COMMENT '是否手工关联',
    `company_number`                varchar(255)   DEFAULT NULL COMMENT '资金组织.编码',
    `company_name`                  varchar(255)   DEFAULT NULL COMMENT '资金组织.名称',
    `accountbank_bankaccountnumber` varchar(255)   DEFAULT NULL COMMENT '银行账号.银行账号',
    `accountbank_acctname`          varchar(255)   DEFAULT NULL COMMENT '银行账号.银企账户名称',
    `accountbank_name`              varchar(255)   DEFAULT NULL COMMENT '银行账号.银行账户名称',
    `bank_number`                   varchar(255)   DEFAULT NULL COMMENT '开户银行.编码',
    `bank_name`                     varchar(255)   DEFAULT NULL COMMENT '开户银行.名称',
    `currency_name`                 varchar(255)   DEFAULT NULL COMMENT '币别.名称',
    `data_create_time`              datetime       DEFAULT CURRENT_TIMESTAMP,
    `data_update_time`              datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `financing_flow_type`           varchar(32)    DEFAULT 'PROCESSING_CENTER' COMMENT '流水处理类别，理论上不会为空{@link BankFlowCenterTypeEnum}',
    `write_off_status`              varchar(32)    DEFAULT 'NO_WRITE_OFF' COMMENT '核销状态{@link financingFlowWriteOffStatusEnum}',
    `logic_delete_flag`             tinyint(2) DEFAULT '0' COMMENT '苍穹是否已删除标识 默认是0 未删除， 1 已删除',
    `financing_project_type`        varchar(32)    DEFAULT NULL COMMENT '资金端业务端标识{@link FinancingProjectTypeEnum}',
    `surplus_amount`                bigint(20) DEFAULT NULL COMMENT '剩余可核销金额',
    `show_in_list`                  tinyint(2) DEFAULT '1' COMMENT '是否在列表中展示, 用于操作中的数据不允许其它人访问',
    `write_off_type`                varchar(32)    DEFAULT NULL COMMENT '核销方式',
    `send_cq_flag`                  tinyint(1) DEFAULT '0' COMMENT '是否已推送标识 0 未推送， 1 已推送',
    `sort`                          tinyint(1) DEFAULT NULL COMMENT '排序字段，关联核销状态枚举的sort',
    PRIMARY KEY (`id`),
    KEY                             `idx_billno` (`billno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财资平台流水记录';

DROP TABLE IF EXISTS `finance_flow_temp_record`;
CREATE TABLE `finance_flow_temp_record`
(
    `business_id`                   bigint(20) NOT NULL AUTO_INCREMENT COMMENT '业务id',
    `batch_id`                      varchar(20)    DEFAULT NULL COMMENT '批次号',
    `id`                            bigint(20) DEFAULT NULL COMMENT 'id',
    `billno`                        varchar(255)   DEFAULT NULL COMMENT '交易明细编号',
    `billstatus`                    varchar(255)   DEFAULT NULL COMMENT '单据状态',
    `auditdate`                     varchar(255)   DEFAULT NULL COMMENT '审核日期',
    `modifytime`                    varchar(255)   DEFAULT NULL COMMENT '最后更新时间',
    `createtime`                    varchar(255)   DEFAULT NULL COMMENT '创建时间',
    `bizdate`                       varchar(255)   DEFAULT NULL COMMENT '交易日期',
    `sourcebillid`                  bigint(20) DEFAULT NULL COMMENT '源单id',
    `amount`                        decimal(19, 4) DEFAULT NULL COMMENT '金额',
    `locamt`                        decimal(19, 4) DEFAULT NULL COMMENT '金额折本位币',
    `exchangerate`                  decimal(19, 4) DEFAULT NULL COMMENT '汇率',
    `description`                   varchar(255)   DEFAULT NULL COMMENT '描述',
    `bizrefno`                      varchar(255)   DEFAULT NULL COMMENT '业务参考号',
    `debitamount`                   decimal(19, 4) DEFAULT NULL COMMENT '付款金额',
    `creditamount`                  decimal(19, 4) DEFAULT NULL COMMENT '收款金额',
    `transbalance`                  decimal(19, 4) DEFAULT NULL COMMENT '余额',
    `oppunit`                       varchar(255)   DEFAULT NULL COMMENT '对方户名',
    `oppbank`                       varchar(255)   DEFAULT NULL COMMENT '对方开户行',
    `ismatchereceipt`               tinyint(1) DEFAULT NULL COMMENT '跟电子回单匹配',
    `isdataimport`                  tinyint(1) DEFAULT NULL COMMENT '是否导入',
    `detailid`                      varchar(255)   DEFAULT NULL COMMENT '明细流水号',
    `isdowntobankstate`             tinyint(1) DEFAULT NULL COMMENT '已经下载到银行对账单',
    `isnoreceipt`                   tinyint(1) DEFAULT NULL COMMENT '确认无回单',
    `receiptno`                     varchar(255)   DEFAULT NULL COMMENT '电子回单关联标记',
    `originalbankcheckflag`         varchar(255)   DEFAULT NULL COMMENT '对账标识码(银行返回)',
    `datasource`                    varchar(255)   DEFAULT NULL COMMENT '数据来源',
    `biztype`                       varchar(255)   DEFAULT NULL COMMENT '业务类型',
    `isrefund`                      tinyint(1) DEFAULT NULL COMMENT '是否退票',
    `bankinterface`                 varchar(255)   DEFAULT NULL COMMENT '银行接口',
    `isreced`                       tinyint(1) DEFAULT NULL COMMENT '是否接收',
    `biztime`                       datetime       DEFAULT NULL COMMENT '交易时间',
    `iskdretflag`                   tinyint(1) DEFAULT NULL COMMENT '是否银企付标',
    `istransup`                     tinyint(1) DEFAULT NULL COMMENT '银行上传',
    `istransdown`                   tinyint(1) DEFAULT NULL COMMENT '银行下载',
    `isbankwithholding`             tinyint(1) DEFAULT NULL COMMENT '银行代扣',
    `recedbilltype`                 varchar(255)   DEFAULT NULL COMMENT '接收单据类型',
    `receredtype`                   varchar(255)   DEFAULT NULL COMMENT '入账状态',
    `sortno`                        bigint(20) DEFAULT NULL COMMENT '排序号',
    `rulename`                      varchar(255)   DEFAULT NULL COMMENT '适配规则',
    `businessbillnum`               varchar(255)   DEFAULT NULL COMMENT '票据号',
    `autorecorpay`                  tinyint(1) DEFAULT NULL COMMENT '自动收付款',
    `smartmatch`                    varchar(255)   DEFAULT NULL COMMENT '智能匹配',
    `oppbanknumber`                 varchar(255)   DEFAULT NULL COMMENT '对方账号',
    `bankcheckflag`                 varchar(255)   DEFAULT NULL COMMENT '对账标识码',
    `recedbillnumber`               varchar(255)   DEFAULT NULL COMMENT '接收单据编号',
    `claimnoticebillno`             varchar(255)   DEFAULT NULL COMMENT '收款认领通知',
    `sourcebilltype`                varchar(255)   DEFAULT NULL COMMENT '源单类型',
    `transfercharge`                decimal(19, 4) DEFAULT NULL COMMENT '手续费',
    `flowserialno`                  varchar(255)   DEFAULT NULL COMMENT '流程序列号',
    `sortid`                        varchar(255)   DEFAULT NULL COMMENT '排序ID',
    `requestserialno`               varchar(255)   DEFAULT NULL COMMENT '银企请求流水号',
    `responseserailno`              varchar(255)   DEFAULT NULL COMMENT '银行响应流水号',
    `kdretflag`                     varchar(255)   DEFAULT NULL COMMENT 'KD标识',
    `agentaccno`                    varchar(255)   DEFAULT NULL COMMENT '被代理账号',
    `agentaccname`                  varchar(255)   DEFAULT NULL COMMENT '被代理户名',
    `agentaccbankname`              varchar(255)   DEFAULT NULL COMMENT '被代理户开户行',
    `bustype`                       varchar(255)   DEFAULT NULL COMMENT 'busType',
    `batchno`                       varchar(255)   DEFAULT NULL COMMENT '银企付款提交的批次号',
    `billnobillno`                  varchar(255)   DEFAULT NULL COMMENT '票号',
    `transdate`                     datetime       DEFAULT NULL COMMENT '记账日期',
    `extdata`                       varchar(255)   DEFAULT NULL COMMENT 'extData',
    `lastmodifytime`                datetime       DEFAULT NULL COMMENT '最后修改时间',
    `bankdetailno`                  varchar(255)   DEFAULT NULL COMMENT '银行流水号',
    `uniqueseq`                     varchar(255)   DEFAULT NULL COMMENT '银行主键',
    `cico_reconciliationcode`       varchar(255)   DEFAULT NULL COMMENT '保融对账码',
    `cico_activepayment`            varchar(255)   DEFAULT NULL COMMENT '付款状态',
    `cico_bruid`                    varchar(255)   DEFAULT NULL COMMENT '保融uid',
    `cico_billno`                   varchar(255)   DEFAULT NULL COMMENT '付款单号',
    `receredway`                    varchar(255)   DEFAULT NULL COMMENT '入账方式',
    `ishandlink`                    tinyint(1) DEFAULT NULL COMMENT '是否手工关联',
    `company_number`                varchar(255)   DEFAULT NULL COMMENT '资金组织.编码',
    `company_name`                  varchar(255)   DEFAULT NULL COMMENT '资金组织.名称',
    `accountbank_bankaccountnumber` varchar(255)   DEFAULT NULL COMMENT '银行账号.银行账号',
    `accountbank_acctname`          varchar(255)   DEFAULT NULL COMMENT '银行账号.银企账户名称',
    `accountbank_name`              varchar(255)   DEFAULT NULL COMMENT '银行账号.银行账户名称',
    `bank_number`                   varchar(255)   DEFAULT NULL COMMENT '开户银行.编码',
    `bank_name`                     varchar(255)   DEFAULT NULL COMMENT '开户银行.名称',
    `currency_name`                 varchar(255)   DEFAULT NULL COMMENT '币别.名称',
    `data_create_time`              datetime       DEFAULT CURRENT_TIMESTAMP,
    `data_update_time`              datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`business_id`),
    KEY                             `idx_batch_id` (`batch_id`),
    KEY                             `idx_id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财资平台流水记录-临时查询表';

CREATE TABLE `fund_receipt_flow_detail`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `data_source`      varchar(100) NOT NULL DEFAULT '手工核销',
    `receipt_repay_id` bigint(20) DEFAULT NULL COMMENT '收付款id',
    `settle_method`    varchar(30)  NOT NULL DEFAULT '' COMMENT '结算方式',
    `cash_flow_code`   varchar(50)  NOT NULL COMMENT '现金流编号',
    `cash_flow_item`   varchar(50)  NOT NULL COMMENT '现金流类型',
    `cash_flow_date`   date         NOT NULL COMMENT '核销日期',
    `total_amount`     bigint(20) NOT NULL DEFAULT '0' COMMENT '总金额',
    `principal_amount` bigint(20) DEFAULT NULL COMMENT '本金金额',
    `interest_amount`  bigint(20) DEFAULT NULL COMMENT '利息金额',
    `bank_flow_no`     varchar(50)           DEFAULT NULL COMMENT '银行流水号',
    `deleted`          tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by`        bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`      datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`        bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`      datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY                `idx_receiptrepayid_deleted` (`receipt_repay_id`,`deleted`),
    KEY                `idx_bankflowno_deleted` (`bank_flow_no`,`deleted`),
    KEY                `idx_cashflowcode_deleted` (`cash_flow_code`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金端核销明细';


INSERT INTO bifrost_system_config (config_key, config_value, created_by,
                                   updated_by, description, status, type)
VALUES ('financeFlowAutoWriteOffSign', 'NO', 'admin', 'admin',
        '银行流水自动核销', 1, 'String');

CREATE TABLE `finance_flow_write_off_detail`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `record_main_table` varchar(32) NOT NULL COMMENT '记录主表枚举FinanceFlowDetailTableEnum',
    `main_id`           bigint(20) NOT NULL COMMENT '目标记录ID',
    `bank_detail_no`    varchar(64) DEFAULT NULL COMMENT '银行流水编号,由于历史原因,不指向finance_flow_record的该字段,而是billno',
    `finance_flow_id`   bigint(20) DEFAULT NULL COMMENT '银行流水记录的ID',
    `create_by`         bigint(20) DEFAULT NULL COMMENT '创建人',
    `update_by`         bigint(20) DEFAULT NULL COMMENT '创建人',
    `create_time`       datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`           tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`),
    KEY                 `idx_main_id_record_main_table` (`record_main_table`,`main_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金流水核销详情关联记录表';

