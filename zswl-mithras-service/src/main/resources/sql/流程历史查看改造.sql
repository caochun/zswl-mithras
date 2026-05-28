-- 所有lib表加字段version_type 版本标志，0无效，1有效...业务自扩展
ALTER TABLE common_version ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE after_lease_check_plan_base_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE after_lease_check_plan_project_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_account_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_aoc_price_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_base_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_factoring_price_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_guarantor_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_lease_item_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_lease_price_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_mortgage_item_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_mortgage_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_pledge_item_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_pledge_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_prepayment_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_receipt_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_rent_actual_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_rent_estimate_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE contract_tenantry_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE corp_address_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE corp_bank_account_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE corp_bond_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE corp_commerce_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE corp_contact_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE corp_related_enterprise_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE corp_shareholder_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE group_credit_establish_base_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE group_credit_review_base_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE normal_bank_account_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE normal_base_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE normal_spouse_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE payment_base_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE payment_planed_detail_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE payment_questionnaire_answer_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE proj_establish_aoc_price_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE proj_establish_base_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE proj_establish_factoring_price_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE proj_establish_lease_price_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE proj_review_aoc_price_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE proj_review_base_info_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE proj_review_cash_flow_plan_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE proj_review_factoring_price_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';
ALTER TABLE proj_review_lease_price_lib ADD version_type TINYINT(4) DEFAULT 1 COMMENT '版本标志，0无效，1有效...业务自扩展';

-- 资料清单 版本表
CREATE TABLE `materials_list_lib` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `belong_id` bigint(20) DEFAULT NULL COMMENT '客户id',
    `materials_type` varchar(50) DEFAULT NULL COMMENT '资料类型',
    `materials_sub_type` varchar(50) DEFAULT NULL COMMENT '资料子类型',
    `business_type` varchar(50) DEFAULT NULL COMMENT '业务类型',
    `oss_filename` varchar(100) DEFAULT NULL COMMENT 'oss文件名',
    `suffix` varchar(100) DEFAULT NULL COMMENT '文件后缀',
    `filename` varchar(500) DEFAULT NULL COMMENT '附件名',
    `file_path` varchar(200) DEFAULT NULL COMMENT '文件url',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `create_by` bigint(20) DEFAULT NULL,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by` bigint(20) DEFAULT NULL,
    `system_generate` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否系统生成文件，0-否，1-是',
    `version` varchar(40) NOT NULL COMMENT '版本号',
    `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time` datetime DEFAULT NULL,
    `data_create_by` bigint(20) DEFAULT NULL,
    `data_update_time` datetime DEFAULT NULL,
    `data_update_by` bigint(20) DEFAULT NULL,
    `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`),
    KEY `materials_list_belong_id_IDX` (`belong_id`,`business_type`,`materials_type`,`materials_sub_type`) USING BTREE,
    KEY `idx_origin_id` (origin_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='资料清单版本表';

--保单信息 版本表
CREATE TABLE `payment_policy_info_lib`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `payment_id`           bigint(20) DEFAULT NULL,
    `policy_code`          varchar(50)  DEFAULT NULL COMMENT '保单编号',
    `policy_amount`        bigint(20) DEFAULT NULL COMMENT '保单金额',
    `insurance_start_date` datetime     DEFAULT NULL COMMENT '保险起始日',
    `insurance_end_date`   datetime     DEFAULT NULL COMMENT '保险到期日',
    `insurance_company`    varchar(255) DEFAULT NULL COMMENT '保险公司名称',
    `create_by`            bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`          datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`          datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `version`              varchar(40) NOT NULL COMMENT '版本号',
    `origin_id`            bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    `data_create_time`     datetime     DEFAULT NULL,
    `data_create_by`       bigint(20) DEFAULT NULL,
    `data_update_time`     datetime     DEFAULT NULL,
    `data_update_by`       bigint(20) DEFAULT NULL,
    `version_type`         tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
