ALTER TABLE collection_record_info
    ADD COLUMN deduction_margin_base_id bigint(20) DEFAULT null COMMENT '抵扣保证金id' AFTER write_off_type;
ALTER TABLE collection_record_info
    ADD COLUMN deduction_margin_base_code varchar (50) DEFAULT null COMMENT '抵扣保证金编号' AFTER deduction_margin_base_id;

-- --- 标准合同判断逻辑调整需求 -------
alter table materials_list add column is_edit TINYINT(4) default 0 not null comment '是否被编辑';
alter table materials_list_lib add column is_edit TINYINT(4) default 0 not null comment '是否被编辑';

-- 付款核销优化
alter table payment_base_info add column beyond_days int(10) default -1 comment '超期天数';
alter table payment_base_info_lib add column beyond_days int(10) default -1 comment '超期天数';

-- 流动性管理监管户待转资金
CREATE TABLE `financing_repay_actual_process_detail` (
                                                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                         `financing_id` bigint(20) NOT NULL COMMENT '融资id',
                                                         `financing_code` varchar(50) NOT NULL COMMENT '融资编号',
                                                         `organization_name` varchar(50) DEFAULT NULL COMMENT '融资机构',
                                                         `financing_type` varchar(50) NOT NULL COMMENT '融资类型',
                                                         `financing_amount` bigint(20) DEFAULT NULL COMMENT '融资金额',
                                                         `repay_date` date NOT NULL COMMENT '还款日期',
                                                         `repay_amount` bigint(20) DEFAULT NULL COMMENT '应还总额',
                                                         `principle_amount` bigint(20) DEFAULT NULL COMMENT '本金',
                                                         `interest_amount` bigint(20) DEFAULT NULL COMMENT '利息',
                                                         `account_number` varchar(30) NOT NULL DEFAULT '' COMMENT '银行开户账户名',
                                                         `account_bank` varchar(100) NOT NULL COMMENT '支行名称',
                                                         `prepare_id` bigint(20) DEFAULT NULL COMMENT '预备表id',
                                                         `is_confirmed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '确认状态',
                                                         `is_paid` tinyint(4) NOT NULL DEFAULT '0' COMMENT '还款状态',
                                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                         `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                         `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                                         `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除:是否已作废',
                                                         `financing_repay_actual_id` bigint(20) NOT NULL COMMENT '还款实际表id',
                                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


-- 初始化还本付息计划确认
ALTER table fund_direct_financing_repay_actual add COLUMN `is_confirmed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '确认状态';
ALTER table fund_direct_financing_repay_actual add COLUMN `is_paid` tinyint(4) NOT NULL DEFAULT '0' COMMENT '还款状态';
ALTER table fund_financing_repay_actual add COLUMN `is_confirmed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '确认状态';
ALTER table fund_financing_repay_actual add COLUMN `is_paid` tinyint(4) NOT NULL DEFAULT '0' COMMENT '还款状态';
ALTER table fund_financing_repay_actual_lib add COLUMN `is_confirmed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '确认状态';
ALTER table fund_financing_repay_actual_lib add COLUMN `is_paid` tinyint(4) NOT NULL DEFAULT '0' COMMENT '还款状态';

-- <<<<<<<<<<<<<< BigBear SQL BEGIN >>>>>>>>>>>>>>
ALTER TABLE new_after_lease_check_report_detail
    ADD COLUMN report_content_save_status VARCHAR(32) NULL COMMENT '报告内容保存状态';
ALTER TABLE new_after_lease_check_report_detail
    ADD COLUMN report_summary_save_status VARCHAR(32) NULL COMMENT '报告总结保存状态';

-- 债项评级
-- alter table rating_amount add column is_real_estate_adjust TINYINT(1) COMMENT '是否需要房地产调整';
-- alter table rating_amount add column is_stock_rights_adjust TINYINT(1) COMMENT '是否需要股权调整';
-- alter table rating_amount_lib add column is_real_estate_adjust TINYINT(1) COMMENT '是否需要房地产调整';
-- alter table rating_amount_lib add column is_stock_rights_adjust TINYINT(1) COMMENT '是否需要股权调整';

-- 征信库增加字段
-- alter table cr_account_draft add column report_flag_init varchar(32) comment '是否报送初始值';
-- update cr_account_draft set report_flag_init = report_flag where 1 = 1;
-- <<<<<<<<<<<<<< BigBear SQL END >>>>>>>>>>>>>>>>