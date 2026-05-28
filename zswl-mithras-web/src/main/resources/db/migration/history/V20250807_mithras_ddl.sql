-- 营业收入拆分出利息收入和咨询服务费收入
alter table `budget_plan_profit_detail` add column `interest_income` bigint(20) not null default 0 comment '利息收入（含税）' after `fund_occupy_average`;
alter table `budget_plan_profit_detail` add column `consulting_fee_income` bigint(20) not null default 0 comment '咨询服务费收入（含税）' after `interest_income`;

-- 增加字段
alter table budget_plan_profit_detail add asset_classify_result varchar(20) default null comment '最近一次五级分类结果';


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




