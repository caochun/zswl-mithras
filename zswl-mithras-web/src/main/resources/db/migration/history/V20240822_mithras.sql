-- 评估机构接口
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES
      ('2024-08-15 17:26:17', '2024-08-15 17:26:17', null, 'ledgerAppraisalLeaseItemList', '租赁物内评估机构列表', 0, 494, NULL, NULL, NULL, 'POST', '/ledger/appraisal/leaseItem/list', 1, NULL),
      ('2024-08-15 17:26:44', '2024-08-15 17:26:44', null, 'ledgerAppraisalCompanyList', '获取系统中所有评估机构', 0, 494, NULL, NULL, NULL, 'POST', '/ledger/appraisal/company/list', 1, NULL),
      ('2024-08-15 17:27:03', '2024-08-15 17:27:03', null, 'ledgerAppraisalAdd', '新增评估机构', 0, 494, NULL, NULL, NULL, 'POST', '/ledger/appraisal/add', 2, NULL),
      ('2024-08-15 17:27:21', '2024-08-15 17:27:21', null, 'ledgerAppraisalQueryCompany', '模糊搜索评估机构', 0, 494, NULL, NULL, NULL, 'POST', '/ledger/appraisal/queryCompany', 1, NULL),
      ('2024-08-15 18:15:01', '2024-08-15 18:15:01', null, 'ledgerAppraisalDetail', '评估机构信息详情', 0, 494, NULL, NULL, NULL, 'POST', '/ledger/appraisal/detail', 1, NULL),
      ('2024-08-15 18:15:20', '2024-08-15 18:15:20', null, 'ledgerAppraisalLasted', '更新评估机构最新信息', 0, 494, NULL, NULL, NULL, 'POST', '/ledger/appraisal/lasted', 2, NULL),
      ('2024-08-15 18:15:37', '2024-08-15 18:15:37', null, 'ledgerAppraisalRelation', '关联评估机构信息', 0, 494, NULL, NULL, NULL, 'POST', '/ledger/appraisal/relation', 1, NULL);

CREATE TABLE `tyc_appraisal_company_base_info` (
     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
     `company_name` varchar(255) DEFAULT NULL COMMENT '机构名称',
     `credit_code` varchar(255) DEFAULT NULL COMMENT '统一社会信用代码',
     `biz_license_end_date` datetime DEFAULT NULL COMMENT '营业许可证到期日',
     `biz_licence_long_term` tinyint(1) DEFAULT NULL COMMENT '营业许可证是否为长期',
     `biz_scope` text COMMENT '业务范围',
     `establish_date` datetime DEFAULT NULL COMMENT '成立日期',
     `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
     `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COMMENT='天眼查-评估机构主表';


CREATE TABLE `lease_item_appraisal_relation` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `company_id` bigint(20) DEFAULT NULL COMMENT '评估机构id',
    `lease_item_id` bigint(20) DEFAULT NULL COMMENT '租赁物id',
    `purpose` varchar(255) DEFAULT NULL COMMENT '用途',
    `select_type` varchar(255) DEFAULT 'NOT_SELECTED' COMMENT '是否被选中',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4 COMMENT='评估机构与租赁物关联表';


-- 评估机构文件管理接口
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-04-23 14:31:19', '2024-05-09 21:03:16', null, 'leaseAppraisalDataListFileUpload', '评估机构文件上传', 0, 494, NULL, NULL, NULL, 'POST', '/file/upload', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-04-23 14:31:49', '2024-05-09 21:03:33', null, 'leaseAppraisalDataListDownload', '评估机构文件下载', 0, 494, NULL, NULL, NULL, 'GET', '/file/download', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-04-23 14:32:36', '2024-05-09 21:03:33', null, 'leaseAppraisalDataListFileList', '评估机构文件列表', 0, 494, NULL, NULL, NULL, 'POST', '/file/list', 2, NULL);
INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('2024-04-23 14:33:37', '2024-05-09 21:03:16', null, 'leaseAppraisalDataListFileBatchRemove', '评估机构文件删除', 0, 494, NULL, NULL, NULL, 'POST', '/file/batch/remove', 2, NULL);

-- 项目利润表增加字段
alter table risk_metric_factor add column factor_code varchar(20) DEFAULT NULL COMMENT '科目编码';

-- ==================================================================大熊的SQL BEGIN =========================================================
-- 创建FTP考核信息表
CREATE TABLE ftp_assessment_info (
                                     id                  BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     payment_id          BIGINT(20)   NOT NULL COMMENT '付款ID',
                                     base_price          BIGINT(20) DEFAULT 0 COMMENT '基础价格',
                                     mountain_adjustment BIGINT(20) DEFAULT 0 COMMENT '山区调整',
                                     grade_adjustment    BIGINT(20) DEFAULT 0 COMMENT '评级调整',
                                     guide_price         BIGINT(20) DEFAULT 0 COMMENT '指引价格',
                                     pledge_price        BIGINT(20) DEFAULT 0 COMMENT '是否质押',
                                     hand_adjustment     BIGINT(20) DEFAULT 0 COMMENT '手工调整',
                                     assessment_price    BIGINT(20) DEFAULT 0 COMMENT '考核价格',
                                     ticket_price        BIGINT(20) DEFAULT 0 COMMENT '票据价格',
                                     remark              VARCHAR(1024) NULL COMMENT '备注',
                                     create_by           BIGINT(20)   NULL COMMENT '创建人',
                                     update_by           BIGINT(20)   NULL COMMENT '更新人',
                                     deleted             TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除',
                                     create_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     update_time         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (id),
                                     KEY idx_ftp_assessment_info_payment_id (payment_id, deleted)
) ENGINE = InnoDB, CHARSET = utf8mb4, COMMENT = 'FTP考核信息表';

-- 创建FTP考核信息表
CREATE TABLE ftp_price_info
(
    id                BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '主键',
    payment_id        BIGINT(20)    NOT NULL COMMENT '付款ID',
    record_date       DATE          NOT NULL COMMENT '记录日期',
    guide_price       BIGINT(20)             DEFAULT 0 COMMENT '指引价格',
    pledge_price      BIGINT(20)             DEFAULT 0 COMMENT '是否质押',
    overdue_price     BIGINT(20)             DEFAULT 0 COMMENT '是否逾期',
    hand_adjustment   BIGINT(20)             DEFAULT 0 COMMENT '手工调整',
    assessment_price  BIGINT(20)             DEFAULT 0 COMMENT '考核价格',
    cost_is_confirmed TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '成本是否已确认',
    remark            VARCHAR(1024) NULL COMMENT '备注',
    create_by         BIGINT(20)    NULL COMMENT '创建人',
    update_by         BIGINT(20)    NULL COMMENT '更新人',
    deleted           TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_ftp_assessment_info_payment_id (payment_id, deleted)
) ENGINE = InnoDB,
  CHARSET = utf8mb4, COMMENT = 'FTP价格表';

CREATE TABLE `async_task_record` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `task_id` varchar(64) NULL COMMENT '任务ID',
                                     `task_name` varchar(255) NULL COMMENT '任务名称',
                                     `task_type` varchar(255) NULL COMMENT '任务类型',
                                     `task_status` varchar(32) NULL COMMENT '任务状态',
                                     `retry_count` int(10) NULL DEFAULT '0' COMMENT '重试次数',
                                     `create_by` bigint(20)  NULL COMMENT '创建人',
                                     `update_by` bigint(20) NULL COMMENT '更新人',
                                     `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `args` text COMMENT '任务参数',
                                     `description` varchar(1024) COMMENT '任务描述',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_task_id` (`task_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异步任务记录表';


-- 这里是否需要做数据初始化
ALTER TABLE finance_project_profit ADD COLUMN is_confirmed TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已确认';
UPDATE finance_project_profit SET is_confirmed = 1 WHERE year <= 2024 AND month <= 7;

ALTER TABLE corp_commerce_info ADD ownership_type varchar(32) NULL COMMENT '是否是上市公司控股' AFTER enterprise_nature;
ALTER TABLE corp_commerce_info_lib ADD ownership_type varchar(32) NULL COMMENT '是否是上市公司控股' AFTER enterprise_nature;

ALTER TABLE ftp_interest_detail_record ADD COLUMN rolling_difference_mark TINYINT(2) NOT NULL DEFAULT 0 COMMENT '轧差标志';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('profitFtpInterestLatestMonth', '项目利润-最新数据月份', 0, 487, null, null, null, 'POST',
        '/ftp/interest/latest/month', 2, null),
       ('ftpInterestLatestMonth', 'FTP计息-最新数据月份', 0, 482, null, null, null, 'POST',
        '/ftp/interest/latest/month', 2, null),
       ('ftpInterestRecalculate', 'FTP-计息任务重算', 0, 482, null, null, null, 'POST', '/ftp/interest/recalculate', 2,
        null),
       ('financeProjectProfitConfirm', '确认项目利润', 0, 482, null, null, null, 'POST',
        '/finance/projectprofit/confirm', 2, null),
       ('ftpPriceCheck', 'FTP-根据日期修改数据前置校验', 0, 482, null, null, null, 'POST', '/ftp/price/check', 2, null),
       ('ftpPriceUpdate', 'FTP-修改FTP价格表记录', 0, 482, null, null, null, 'POST', '/ftp/price/update', 2, null),
       ('ftpPriceList', 'FTP-价格表列表', 0, 482, null, null, null, 'POST', '/ftp/price/list', 2, null);
-- ==================================================================大熊的SQL END ===========================================================