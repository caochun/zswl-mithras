alter table `payment_base_info` add column `cash_ftp` int(10) DEFAULT NULL COMMENT '现金FTP成本';
alter table `payment_base_info` add column `cash_ftp_final` int(10) DEFAULT NULL COMMENT '现金FTP成本（最终值）';
alter table `payment_base_info` add column `bill_ftp` int(10) DEFAULT NULL COMMENT '票据FTP成本';
alter table `payment_base_info` add column `bill_ftp_final` int(10) DEFAULT NULL COMMENT '票据FTP成本（最终值）';

alter table `payment_base_info_lib` add column `cash_ftp` int(10) DEFAULT NULL COMMENT '现金FTP成本';
alter table `payment_base_info_lib` add column `cash_ftp_final` int(10) DEFAULT NULL COMMENT '现金FTP成本（最终值）';
alter table `payment_base_info_lib` add column `bill_ftp` int(10) DEFAULT NULL COMMENT '票据FTP成本';
alter table `payment_base_info_lib` add column `bill_ftp_final` int(10) DEFAULT NULL COMMENT '票据FTP成本（最终值）';

CREATE TABLE `ftp_interest_base_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) NOT NULL COMMENT '合同id',
  `contract_code` varchar(50) NOT NULL COMMENT '合同编号',
  `receipt_id` bigint(20) NOT NULL COMMENT '借据id',
  `receipt_code` varchar(50) NOT NULL DEFAULT '' COMMENT '借据编号',
  `proj_review_id` bigint(20) NOT NULL COMMENT '项目评审id',
  `proj_name` varchar(200) NOT NULL COMMENT '项目名称',
  `client_id` bigint(20) NOT NULL COMMENT '客户id',
  `client_name` varchar(50) NOT NULL COMMENT '客户名称',
  `biz_dept_id` bigint(20) NOT NULL COMMENT '业务部门id',
  `sponsor_user_id` bigint(20) NOT NULL COMMENT '项目主办id',
  `last_update_date` date NOT NULL COMMENT '最近一次更新日期',
  `total_interest_amount` bigint(20) DEFAULT NULL COMMENT '累计计息',
  `finish` tinyint(4) NOT NULL DEFAULT '0' COMMENT 'FTP计息是否结束',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FTP计息-基本信息';

CREATE TABLE `ftp_interest_detail_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ftp_interest_id` bigint(20) NOT NULL COMMENT 'FTP计息id',
  `item_text` varchar(20) NOT NULL COMMENT 'FTP计息表条目展示文本（通常是日期，每年的1.1会使用“期初余额”替代日期）',
  `interest_date` date NOT NULL COMMENT '计息日期',
  `cash_out` bigint(20) DEFAULT NULL COMMENT '现金支出',
  `cash_in` bigint(20) DEFAULT NULL COMMENT '现金收入',
  `cash_occupy` bigint(20) DEFAULT NULL COMMENT '资金占用',
  `cash_ftp` int(10) DEFAULT NULL COMMENT '现金FTP',
  `cash_interest` bigint(20) DEFAULT NULL COMMENT '资金计息',
  `bill_out` bigint(20) DEFAULT NULL COMMENT '票据支出',
  `bill_in` bigint(20) DEFAULT NULL COMMENT '票据收入',
  `bill_occupy` bigint(20) DEFAULT NULL COMMENT '票据占用',
  `bill_ftp` int(10) DEFAULT NULL COMMENT '票据FTP',
  `bill_interest` bigint(20) DEFAULT NULL COMMENT '票据计息',
  `is_overdue` tinyint(4) DEFAULT NULL COMMENT '是否逾期',
  `total_interest_this_year` bigint(20) DEFAULT NULL COMMENT '当年累计计息',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `ftp_id_date` (`ftp_interest_id`,`interest_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FTP计息-计息详情记录';

CREATE TABLE `bill_management`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT '票据id	',
    `main_id`          bigint(20) DEFAULT NULL COMMENT '管理收付款主表id-收款/付款明细ID',
    `bill_type`        varchar(100) DEFAULT NULL COMMENT '票据类型 收款/付款',
    `bill_code`        varchar(100) DEFAULT NULL COMMENT '票据code',
    `bill_amount`      bigint(20) DEFAULT NULL COMMENT '票据金额',
    `bill_expire_date` datetime     DEFAULT NULL COMMENT '票据到期日期',
    `bill_buy_rate`      bigint(20) DEFAULT NULL COMMENT '票据买入价',
    `bill_buy_rate_type`      bigint(20) DEFAULT NULL COMMENT '票据买入价类型,0其他，1，同项目FTP',
    `create_by`        bigint(20) DEFAULT NULL,
    `create_time`      datetime     DEFAULT CURRENT_TIMESTAMP,
    `update_by`        bigint(20) DEFAULT NULL,
    `update_time`      datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='票据管理表';

alter table client add column auth_type varchar(50) DEFAULT NULL COMMENT '权限类型';
alter table `collection_overdue_history` add column `batch_number` BIGINT(20) DEFAULT NULL COMMENT '批次号，依次递增';




-- ftp收益率

alter table fund_direct_financing_base_info add column ftp_yield_rate int(11) DEFAULT NULL COMMENT 'FTP收益率';
alter table fund_financing_plan add column ftp_yield_rate int(11) DEFAULT NULL COMMENT 'FTP收益率';
alter table fund_financing_plan_lib add column ftp_yield_rate int(11) DEFAULT NULL COMMENT 'FTP收益率';
alter table fund_direct_financing_product_detail add column ftp_yield_rate int(11) DEFAULT NULL COMMENT 'FTP收益率';



CREATE TABLE `ftp_income_base_info`
(
    `id`                             bigint(20)          NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `fund_financing_id`              bigint(20)                   DEFAULT NULL COMMENT '融资id',
    `financing_type`                 varchar(50)                  DEFAULT NULL COMMENT '融资类型 直融 or 间融 FinancingTypeEnum',
    `financing_code`                 varchar(50)               NOT NULL COMMENT '融资编号',
    `financing_product_id`                bigint(20)                   DEFAULT NULL COMMENT '直融保存产品信息',
    `abbreviation`              varchar(50)                  DEFAULT NULL COMMENT '证券简称',
    `direct_financing_type`              varchar(50)                  DEFAULT NULL COMMENT '直融产品名称',
    `financing_amount`             bigint(20)                   DEFAULT NULL COMMENT '融资金额',
    `ftp_yield_rate`                   int(11)                 DEFAULT NULL COMMENT 'FTP收益率',
    `product_ftp_yield_rate`                   int(11)                 DEFAULT NULL COMMENT '产品FTP收益率',
    `fund_manager_id`                bigint(20)                  NOT NULL COMMENT '资金经理id',
    `create_time`                    datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`                      bigint(20)                   DEFAULT NULL COMMENT '创建人id',
    `update_time`                    datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`                      bigint(20)                   DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_fund_financing_id` (`fund_financing_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='资金管理-融资管理-ftp收益表';

CREATE TABLE `ftp_income_detail_record`
(
    `id`                             bigint(20)          NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `ftp_income_id`              bigint(20)                   DEFAULT NULL COMMENT 'ftp收益表id',
    `interest_date`            date        NOT NULL COMMENT '计息日期',
    `remaining_principal`                 bigint(20)           DEFAULT NULL COMMENT '剩余本金',
    `ftp_yield_rate`                   int(11)                 DEFAULT NULL COMMENT 'FTP收益率',
    `ftp_yield_rate_day`                   decimal(19, 4)                 DEFAULT NULL COMMENT 'FTP日收益率',
    `ftp_income`                   bigint(20)                 DEFAULT NULL COMMENT 'FTP收益',
    `ftp_income_current_year`                   bigint(20)                 DEFAULT NULL COMMENT 'FTP收益',
    `create_time`                    datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_by`                      bigint(20)                   DEFAULT NULL COMMENT '创建人id',
    `update_time`                    datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `update_by`                      bigint(20)                   DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_ftp_income_id` (`ftp_income_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='资金管理-融资管理-ftp收益记录表';


INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name)
VALUES ('ftpIncome', 3, 0, null, '/budget/pricing/ftpYield', null, null, 'FTP收益率');

INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
VALUES (88, (select id from bifrost_menu where code = 'ftpIncome'), 0);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('ftpIncomeBaseInfoList', '资金管理-融资管理-ftp收益表列表', 0, (select id from bifrost_menu where code = 'ftpIncome'), 'POST', '/ftp/income/base/info/list', 2),
        ('ftpIncomeBaseInfoDetail', '资金管理-融资管理-ftp收益表详情', 0, (select id from bifrost_menu where code = 'ftpIncome'), 'POST',
         '/ftp/income/base/info/detail', 2),
        ('ftpIncomeDetailRecordList', '资金管理-融资管理-ftp收益记录表列表', 0, (select id from bifrost_menu where code = 'ftpIncome'), 'POST',
         '/ftp/income/detail/record/list', 2),
        ('ftpIncomeOrganizationList', '资金管理-融资管理-ftp收益记录获取融资机构', 0, (select id from bifrost_menu where code = 'ftpIncome'), 'POST',
         '/ftp/income/organization/list', 2);

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('ftpIncomeBaseInfoCount', '资金管理-融资管理-ftp收益表统计', 0, (select id from bifrost_menu where code = 'ftpIncome'), 'POST',
         '/ftp/income/base/info/count', 2);



-- 应付利息

alter table funds_daily_cost add column`financing_product_id`                bigint(20)                   DEFAULT NULL COMMENT
    '直融保存产品信息';
alter table funds_daily_cost add column    `abbreviation`              varchar
    (50)                  DEFAULT NULL COMMENT '证券简称';





