

-- ============= 来自文件：20260206_linlili_001_ddl.sql ============= 

CREATE TABLE `fund_lpr_adjust_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `financing_id` bigint(20) NOT NULL COMMENT '融资申请id',
  `before_lpr_rate_percent` int(10) NULL COMMENT '调整前lpr利率',
  `after_lpr_rate_percent` int(10) NULL COMMENT '调整后lpr利率',
  `lpr_date` date NULL COMMENT 'lpr日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='资金-间融浮动利率调整记录表';


-- ============= 来自文件：20260206_liushaokang_001_ddl.sql ============= 

-- 资产五级分类风险因子表新增字段
ALTER TABLE asset_classify_client_risk_factor_template
ADD COLUMN risk_factor_type VARCHAR(20) DEFAULT 'HISTORY' COMMENT '风险因素项目类别: OPERATION_LEASE：经营租赁项目  NON_SHIPPING：非航运项目  HISTORY：历史数据';



-- ============= 来自文件：20260206_hengtx_001_ddl.sql ============= 

alter table ftp_assessment_info add column  is_special_matter tinyint(1) default 0 null comment '是否特殊事项，0-否，1-是';


-- ============= 来自文件：20260206_hengtx_001_ddl.sql ============= 

create table proj_review_material
(
    id                  bigint auto_increment
        primary key,
    client_id           bigint                             null comment '客户id',
    client_type         varchar(20)                        null comment '客户类型',
    name                varchar(50)                        null comment '客户名称',
    business_type       varchar(200)                       null comment '业务类型',
    client_type_name    varchar(200)                       null comment '展示类型名',
    proj_review_id      bigint                             null comment '项目评审id',
    proj_code           varchar(20)                        null comment '项目编号',
    create_by           bigint                             null comment '创建人、发起人',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                             null comment '最后更新人id',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    remark              varchar(500)                       null,
    review_comments     varchar(50)                        null comment '审核意见：已审核-AUDITED、补充基础资料-SUPPLEMENT_INFO',
    review_instructions varchar(1024)                      null comment '审核说明',
    record_id           text                               null comment '文件Id,使用,分割记录',
    last_version_flag   int      default 0                 null comment '最后版本标记'
)
    comment '立项资料清单审核表';

create index idx_proj_review_id
    on proj_review_material (proj_review_id);


-- ============= 来自文件：20260206_gxy_001_ddl.sql ============= 

-- 为表新增insurance_purchase_time字段、insurance_purchase_time_value字段
ALTER TABLE proj_review_meet_minute_base_info ADD COLUMN insurance_purchase_time VARCHAR(50) COMMENT '保险购买时间 InsurancePurchaseTimeEnum#name' AFTER policy_require_value;
ALTER TABLE proj_review_meet_minute_base_info ADD COLUMN insurance_purchase_time_value bigint(20) COMMENT '保险购买时间值' AFTER insurance_purchase_time;

-- 为表新增limit_requirement字段（文本类型）
ALTER TABLE proj_review_meet_minute_base_info ADD COLUMN limit_requirement VARCHAR(200) COMMENT '管理要求-限额要求' AFTER management_requirement;


-- ============= 来自文件：20260206_hengtx_001_ddl.sql ============= 

alter table new_after_lease_check_plan_client add column  next_check_flag    tinyint(2)  null comment '是否已发起下次检查';
alter table new_after_lease_check_plan_client_lib add column  next_check_flag    tinyint(2)  null comment '是否已发起下次检查';


-- ============= 来自文件：20260206_huliangdong_001_ddl.sql ============= 

ALTER TABLE payment_collection_info ADD warranty_pay_way int(11) NULL COMMENT '质保金标识，0 内扣，1 不内扣';
ALTER TABLE payment_collection_info ADD warranty_return_date datetime NULL COMMENT '厂商质保金退还日期';



CREATE TABLE `warranty_base_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '质保金id	',
  `warranty_code` varchar(100) DEFAULT NULL COMMENT '编号',
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `contract_code` varchar(100) DEFAULT NULL COMMENT '合同编号',
  `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
  `collection_date` datetime DEFAULT NULL COMMENT '收款日期',
  `collection_amount` bigint(20) DEFAULT NULL COMMENT '质保金余额',
  `back_amount` bigint(20) DEFAULT NULL COMMENT '已退金额',
  `deduct_amount` bigint(20) DEFAULT NULL COMMENT '已抵扣金额',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `plan_warranty_amount` bigint(20) DEFAULT NULL COMMENT '计划收款金额',
  `plan_warranty_date` datetime DEFAULT NULL COMMENT '计划收款日期',
  `contract_is_settle` int(11) DEFAULT '0' COMMENT '合同是否结清，0 否，1 是',
  `total_receivable_amount` bigint(20) DEFAULT NULL COMMENT '累加应收金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1084 DEFAULT CHARSET=utf8mb4 COMMENT='质保金明细表';


CREATE TABLE `warranty_record_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '质保金核销记录明细id	',
  `warranty_id` bigint(20) DEFAULT NULL COMMENT '质保金明细id',
  `data_source` varchar(100) DEFAULT NULL COMMENT '信息来源',
  `collection_type` varchar(20) DEFAULT NULL COMMENT '收款类型',
  `record_type` varchar(20) DEFAULT NULL COMMENT '记录类型：退款 or 收款',
  `collection_date` datetime DEFAULT NULL COMMENT '实收or付日期',
  `collection_amount` bigint(20) DEFAULT NULL COMMENT '实收or付金额',
  `deduct_principal` bigint(20) DEFAULT NULL COMMENT '抵扣本金',
  `deduct_interest` bigint(20) DEFAULT NULL COMMENT '抵扣利息',
  `deduct_penalty_interest` bigint(20) DEFAULT NULL COMMENT '抵扣罚息',
  `deduct_rent` bigint(20) DEFAULT NULL COMMENT '抵扣租金',
  `deduct_term` int(11) DEFAULT NULL COMMENT '抵扣期项',
  `collection_id` bigint(20) DEFAULT NULL COMMENT '收款核销id',
  `postscript` varchar(100) DEFAULT NULL COMMENT '附言',
  `enclosure_id` bigint(20) DEFAULT NULL COMMENT '附件id',
  `enclosure_name` varchar(100) DEFAULT NULL COMMENT '附件名',
  `write_off_status` varchar(100) DEFAULT NULL COMMENT '核销状态',
  `our_account_id` bigint(20) DEFAULT NULL COMMENT '我方账户id',
  `sort_id` int(11) DEFAULT NULL COMMENT '序号',
  `write_off` varchar(100) DEFAULT NULL COMMENT '核销',
  `review` varchar(100) DEFAULT NULL COMMENT '复核',
  `create_by` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `source_flag` int(11) DEFAULT NULL COMMENT '来源标志',
  `write_off_user` bigint(20) DEFAULT NULL COMMENT '核销人',
  `review_user` bigint(20) DEFAULT NULL COMMENT '复核人',
  `our_account_name` varchar(100) DEFAULT NULL COMMENT '我方账户名',
  `our_account_number` varchar(100) DEFAULT NULL COMMENT '我方银行账号',
  `our_account_bank` varchar(100) DEFAULT NULL COMMENT '我方开户行',
  `other_account_name` varchar(100) DEFAULT NULL COMMENT '对方账户名',
  `other_account_number` varchar(100) DEFAULT NULL COMMENT '对方银行账号',
  `other_account_bank` varchar(100) DEFAULT NULL COMMENT '对方开户行',
  `flow_id` varchar(40) DEFAULT NULL COMMENT '流水id',
  `invoice_flag` tinyint(4) DEFAULT NULL COMMENT '是否开票，0不开 1开',
  `rent_collection_code` varchar(50) DEFAULT NULL COMMENT '租金现金流编号',
  `finance_flow_id` bigint(20) DEFAULT NULL COMMENT '银行流水ID',
  `bank_detail_no` varchar(32) DEFAULT NULL COMMENT '银行流水编号',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除，0：未删除，1：已删除，默认0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1520 DEFAULT CHARSET=utf8mb4 COMMENT='质保金核销记录明细表';


-- ============= 来自文件：20260206_huliangdong_001_ddl.sql ============= 


ALTER TABLE collection_base_info ADD retreat_lock varchar(1) NULL COMMENT '保证金退抵锁';


CREATE TABLE contract_retreat_info (
                             id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
							 contract_id bigint(20) NULL COMMENT '合同id',
							 contract_code varchar(50) NULL COMMENT '合同编号',
							 client_name varchar(50) NULL COMMENT '客户名称',
							 process_status varchar(20) NULL COMMENT '流程状态',
                             deduction_amount bigint(20) NULL COMMENT '保证金内扣金额',
							 returned_amount bigint(20) NULL COMMENT '保证金退还金额',
							 recycling_flag varchar(1) NULL COMMENT '是否回收保证金',
							 sended_flag varchar(1) NULL COMMENT '已推送通知标记',
                             deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：1=已删除，0=未删除',
                             create_by bigint(20) NOT NULL COMMENT '创建人ID（关联sys_user.id）',
                             create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             update_by bigint(20) NOT NULL COMMENT '更新人ID（关联sys_user.id）',
                             update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='合同退抵信息';



CREATE TABLE contract_deduct_rent_info (
                             id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
							 retreat_info_id bigint(20) NULL COMMENT '退抵信息id',
							 code varchar(20) NULL COMMENT '现金流编号',
							 plan_collection_date datetime DEFAULT NULL COMMENT '日期',
							 phase int(11) DEFAULT NULL COMMENT '期项',
							 plan_collection_amount bigint(20) DEFAULT NULL COMMENT '租金',
							 principal bigint(20) DEFAULT NULL COMMENT '本金',
							 interest bigint(20) DEFAULT NULL COMMENT '利息',
							 collection_amount bigint(20) DEFAULT NULL COMMENT '已收租金',
							 rest_amount bigint(20) DEFAULT NULL COMMENT '未收租金',
                             deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：1=已删除，0=未删除',
                             create_by bigint(20) NOT NULL COMMENT '创建人ID（关联sys_user.id）',
                             create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             update_by bigint(20) NOT NULL COMMENT '更新人ID（关联sys_user.id）',
                             update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='合同抵扣租金信息';



-- ============= 来自文件：20260206_linlili_002_ddl.sql ============= 

ALTER TABLE filing_materials ADD object_id bigint(20) NULL COMMENT '关联id' AFTER id;
ALTER TABLE filing_materials ADD object_type varchar(50) NULL COMMENT '关联类型' AFTER object_id;
ALTER TABLE filing_first_level_config ADD require_flag int(11) NULL COMMENT '是否必传,0非必传，1必传';
ALTER TABLE filing_materials MODIFY COLUMN proj_code varchar(20) NULL COMMENT '关联编号（项目/融资编号）';


-- ============= 来自文件：20260206_linlili_003_ddl.sql ============= 

ALTER TABLE filing_materials MODIFY COLUMN first_commit_date datetime NULL COMMENT '首岗提交时间';
ALTER TABLE filing_materials MODIFY COLUMN start_date datetime NULL COMMENT '待办推送时间';

ALTER TABLE filing_materials ADD COLUMN user_id int(11) COMMENT '发起人' after update_by;
ALTER TABLE filing_materials ADD COLUMN dept_id int(11) COMMENT '发起人所属部门' after user_id;
ALTER TABLE filing_materials ADD COLUMN materials_desc varchar(200) NULL COMMENT '资料类型';
ALTER TABLE filing_materials ADD COLUMN filing_overdue int(11) NULL DEFAULT 0 COMMENT '是否归档超期';
ALTER TABLE filing_materials ADD COLUMN supplement_filing_overdue int(11) NULL DEFAULT 0  COMMENT '是否补充材料超期';

ALTER TABLE filing_materials ADD COLUMN archive_pre_review_last_submit_time datetime NULL COMMENT '档案管理初审最后提交时间';
ALTER TABLE filing_materials ADD COLUMN archive_review_last_submit_time datetime NULL COMMENT '档案管理复核最后提交时间';
ALTER TABLE filing_materials ADD COLUMN archive_pre_review_reject_count int(11) NULL COMMENT '档案管理初审退回次数';
ALTER TABLE filing_materials ADD COLUMN archive_review_reject_count int(11) NULL COMMENT '档案管理复核退回次数';
ALTER TABLE filing_materials ADD COLUMN archive_pre_review_reject_reason_sum text NULL COMMENT '档案管理初审退回原因汇总';
ALTER TABLE filing_materials ADD COLUMN archive_review_reject_reason_sum text NULL COMMENT '档案管理复核退回原因汇总';
ALTER TABLE filing_materials ADD duration bigint(20) NULL COMMENT '全流程耗时（工作日）' AFTER filing_overdue;
ALTER TABLE filing_materials ADD materials_manager_review_duration bigint(20) NULL COMMENT '档案管理初审耗时（工作日）' AFTER duration;
ALTER TABLE filing_materials ADD materials_manager_re_review_duration bigint(20) NULL COMMENT '档案管理复核耗时（工作日）' AFTER materials_manager_review_duration;




-- ============= 来自文件：20260206_gxy_001_ddl.sql ============= 

CREATE TABLE `archived_materials_download_record`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT,
    `filename`        varchar(255) DEFAULT NULL COMMENT '文件名',
    `download_status` varchar(20)  DEFAULT NULL COMMENT '下载状态',
    `file_path`       varchar(200) DEFAULT NULL COMMENT '文件url',
    `create_time`     datetime     DEFAULT CURRENT_TIMESTAMP,
    `create_by`       bigint(20) DEFAULT NULL,
    `update_time`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`       bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY               `idx_create_time` (`create_time`),
    KEY               `idx_update_time` (`update_time`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='归档资料下载记录';


-- ============= 来自文件：20260206_liushaokang_001_ddl.sql ============= 

-- 资产五级分类主表添加字段
ALTER TABLE asset_classify
ADD COLUMN init_type VARCHAR(20) DEFAULT 'QUARTER_END' COMMENT '初分类型: QUARTER_END 季末初分, QUARTER_MID 季中初分',
ADD INDEX idx_init_type (init_type);

-- 资产五级分类版本表添加字段
ALTER TABLE asset_classify_lib
ADD COLUMN init_type VARCHAR(20) DEFAULT 'QUARTER_END' COMMENT '初分类型: QUARTER_END 季末初分, QUARTER_MID 季中初分';


-- ============= 来自文件：20260206_luyujie_001_ddl.sql ============= 

create table stamp_duty_detail
(
    id                  bigint  NOT NULL auto_increment primary key,
    belong_id           bigint                             null comment '关联id（合同id、融资id）',
    name                varchar(50)                        null comment '申报税目名称',
    belong_org_id       bigint(20)                         null comment '业务部门id',
    belong_org_name     varchar(100)                       null comment '业务部门名称',
    client_id           bigint(20)                         null comment '客户id、机构id',
    client_name         varchar(200)                       null comment '客户名称/融资机构',
    belong_code         varchar(50)                        null comment '合同编号/融资编号',
    receipt_id          bigint(20)                         null comment '借据id',
    receipt_code  		varchar(20)                        null comment '借据编号',
    start_date          date                               null comment '实际起租日',
    rent                bigint(20)                         null comment '不含税租金',
    commission          bigint(20)                         null comment '不含税手续费',
    consulting_fee      bigint(20)                         null comment '不含税咨询费',
    amount              bigint(20)                         null comment '金额',
    tax_rate            varchar(20)                        null comment '印花税率',
    stamp_duty          varchar(20)                        null comment '印花税',
    scoure              varchar(20)                        null comment '来源类型',
    is_delete           varchar(20)                        null comment '是否删除(1是、0否)',
    create_by           bigint                             null comment '创建人、发起人',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                             null comment '最后更新人id',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    remark              varchar(500)                       null
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 comment '印花税缴纳明细表';


-- ============= 来自文件：20260206_luyujie_002_ddl.sql ============= 

-- 印花税明细表合同编号/融资编号、借据编号字段加长
ALTER TABLE stamp_duty_detail
MODIFY COLUMN receipt_code VARCHAR(200),
MODIFY COLUMN belong_code VARCHAR(200);
