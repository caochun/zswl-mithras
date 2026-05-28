-- 账龄 ------
CREATE TABLE `finance_account_age_base_info`
(
    `id`                              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `deadline`                        date NOT NULL COMMENT '截止日期',
    `accountancy_organization_number` varchar(32) DEFAULT NULL COMMENT '核算组织编码 默认 10000396',
    `accountancy_organization_name`   varchar(50) DEFAULT NULL COMMENT '核算组织名称 默认 浙江浙商融资租赁有限公司',
    `status`                          varchar(32) DEFAULT NULL COMMENT '状态 ',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by`                       bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                     datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                       bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                     datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='帐龄主表';



CREATE TABLE `finance_account_age_item`
(
    `id`                              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account_age_id`                  bigint(20) NOT NULL COMMENT '帐龄id',
    `accountancy_organization_number` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '核算组织编码 默认 10000396',
    `accountancy_organization_name`   varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '核算组织名称 默认 浙江浙商融资租赁有限公司',
    `send_status`                     varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'NEW' COMMENT '推送状态',
    `original_value_initial`          decimal(20, 2)                         DEFAULT NULL COMMENT '期初款项原值',
    `original_value_increase`         decimal(20, 2)                         DEFAULT NULL COMMENT '本期增加额',
    `original_value_reduce`           decimal(20, 2)                         DEFAULT NULL COMMENT '本期减少额',
    `original_value_final`            decimal(20, 2)                         DEFAULT NULL COMMENT '期末款项原值',
    `currency`                        varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '币别',
    `account_number`                  varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '科目名称编号',
    `payment_content`                 varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '款项内容',
    `client_id`                       bigint(20) DEFAULT NULL COMMENT '客户id',
    `customer_unit_name`              varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客户单位名称, 取合同对应承租人的“客户名称”字段',
    `business_date`                   date                                   DEFAULT NULL COMMENT '业务日期',
    `aging_deadline`                  date                                   DEFAULT NULL COMMENT '账龄截止日',
    `business_age`                    int(11) DEFAULT NULL COMMENT '业务账龄（月）向下取整',
    `contract_id`                     bigint(20) DEFAULT NULL COMMENT '合同id',
    `receipt_id`                      bigint(20) DEFAULT NULL COMMENT '借据id',
    `collection_id`                   bigint(20) DEFAULT NULL COMMENT '收款id',
    `collection_code`                 varchar (70) DEFAULT NULL COMMENT '收款code',
    `contract_code`                   varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '合同编号',
    `proj_name`                       varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '项目名称',
    `plan_collection_date`            date                                   DEFAULT NULL COMMENT '租金的应收日期 合同逾期日期',
    `source`                          tinyint(2) DEFAULT '0' COMMENT '来源 0系统生成 ,1 人工创建',
    `deleted`                         tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    `create_by`                       bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                     datetime                               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                       bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                     datetime                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='帐龄-详情表';

INSERT INTO `bifrost_menu` (`gmt_create`, `gmt_modified`, `code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`, `id`, `en_name`,
                            `target`, `create_by`, `update_by`)
VALUES ('2024-09-11 16:04:44', '2024-09-11 14:05:59', 'budgetBusinessAgingTable', 3, 0, NULL, '/budget/businessAgingTable', NULL, NULL,
        '业务账龄表报送', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `bifrost_custom_tree_menu_ref` (`id`, `gmt_create`, `gmt_modified`, `create_by`, `update_by`, `custom_tree_id`, `menu_id`, `sort_no`)
VALUES (NULL, '2024-02-29 14:28:22', '2024-04-19 17:15:43', NULL, NULL, (select id from bifrost_custom_tree where code = 'budget'),
        (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 0);


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values  ('financeAccountAgeBaseInfoAdd', '新增帐龄主表', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/base/info/add', 2),
        ('financeAccountAgeBaseInfoClose', '关闭帐龄主表', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/base/info/close', 2),
        ('financeAccountAgeBaseInfoList', '帐龄主表列表', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/base/info/list', 2),
        ('financeAccountAgeBaseInfoRemove', '删除帐龄主表', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/base/info/remove', 2),
        ('financeAccountAgeBaseInfoEffect', '帐龄主表-完成', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/base/info/effect', 2),
        ('financeAccountAgeBaseInfoDetail', '帐龄主表详情', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/base/info/detail', 2),
        ('financeAccountAgeItemAdd', '新增帐龄-详情表', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/item/add', 2),
        ('financeAccountAgeItemModify', '修改帐龄-详情表', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/item/modify', 2),
        ('financeAccountAgeItemList', '帐龄-详情表列表', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/item/list', 2),
        ('financeAccountAgeItemRemove', '删除帐龄-详情表', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/item/remove', 2),
        ('financeAccountAgeFileExport', '帐龄导出文件', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'),'POST', '/file/export', 2),
        ('financeAccountAgeItemRegeneration', '重新生成-详情表', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/item/regeneration', 2),
        ('financeAccountAgeItemSend', '推送至苍穹', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/item/send', 2);

update bifrost_menu set sort_no = 0 where name = '我方账户';
update bifrost_menu set sort_no = 10 where name = 'LPR设置';
update bifrost_menu set sort_no = 20 where name = '财务报表';
update bifrost_menu set sort_no = 30 where name = '项目利润';
update bifrost_menu set sort_no = 40 where name = '拨备计提';
update bifrost_menu set sort_no = 50 where name = '流水中心';
update bifrost_menu set sort_no = 60 where name = '月结管理';
update bifrost_menu set sort_no = 70 where name = '业务账龄表报送';
update bifrost_menu set sort_no = 74 where name = '收入分摊表';
update bifrost_menu set sort_no = 77 where name = '应付利息';
update bifrost_menu set sort_no = 80 where name = 'FTP/业务定价';


alter table finance_account_age_item add column cq_number varchar (200) DEFAULT NULL COMMENT '苍穹系统编号';


-- 账龄end ------

-- =========================================== BigBear 的公开信息SQL START ===========================================
-- 公开信息建表语句
CREATE TABLE `public_info_config` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `config_key` varchar(64) NOT NULL COMMENT '配置标识',
                                      `sort_priority` int(11) NOT NULL COMMENT '排序优先级',
                                      `title` varchar(256) NOT NULL COMMENT '标题',
                                      `description` varchar(1024) DEFAULT NULL COMMENT '描述信息',
                                      `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                      `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                      `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `idx_key_deleted` (`config_key`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='公开信息配置表';

CREATE TABLE `public_info_query` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `payment_id` bigint(20) NOT NULL COMMENT '关联付款申请ID',
                                     `client_id` bigint(20) NOT NULL COMMENT '关联客户ID',
                                     `query_from` date NOT NULL COMMENT '查询开始时间',
                                     `query_to` date NOT NULL COMMENT '查询结束时间',
                                     `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                     `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                     `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `client_type` varchar(32) NOT NULL COMMENT '客户类型',
                                     `process_instance_id` varchar(16) DEFAULT NULL COMMENT '流程ID',
                                     `confirmed_by` bigint(20) DEFAULT NULL COMMENT '确认人ID',
                                     `confirmed_time` datetime DEFAULT NULL COMMENT '确认时间',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_id_payment_id_deleted` (`id`,`payment_id`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='公开信息查询结果表';

CREATE TABLE `public_info_record` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `public_info_query_id` bigint(20) NOT NULL COMMENT '公开信息ID',
                                      `config_key` varchar(64) NOT NULL COMMENT '关联配置表的KEY',
                                      `investigation_type` varchar(32) DEFAULT NULL COMMENT '调查类型',
                                      `investigation_explain` varchar(1024) DEFAULT NULL COMMENT '调查说明',
                                      `projectManager_explain` varchar(1024) DEFAULT NULL COMMENT '项目经理说明',
                                      `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                      `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                      `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `payment_id` bigint(20) DEFAULT NULL COMMENT '付款申请ID',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_id_public_info_query_id_config_key_deleted` (`id`, `public_info_query_id`, `config_key`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='公开信息查询报告表';

-- 公开信息配置信息插入
INSERT INTO public_info_config (config_key, sort_priority, title, description)
VALUES ('QCC_CHANGE_RECORD', 1, '企查查-基本信息-变更记录', '确认是否存在：\n1、重大股东变更（持股比例51%以上）'),
       ('QCC_EQUITY_PENETRATION', 2, '企查查-基本信息-股权穿透图', '确认是否存在：\n1、实控人变更'),
       ('QCC_OPERATING_RISK', 3, '企查查-经营风险', '确认是否存在：\n1、重大行政处罚（含重大安全事故，罚金＞500万或对经营生产产生重大影响）；\n2、重大环保处罚（罚金＞500万或对经营生产产生重大影响）\n3、重大经营异常\n4、任何欠税公告\n5、注销备案\n6、任何拖欠员工工资（劳动仲裁）\n7、上市公司涉及重大监管处罚\n8、其他'),
       ('QCC_LEGAL_LITIGATION', 4, '企查查-法律诉讼', '确认是否存在：\n1、股权冻结\n2、重大诉讼案件（原告为金融机构或非金融机构但涉案金额单一或累计在3000万以上或涉及刑事诉讼）\n3、重大被执行案件（原告为金融机构或非金融机构但涉案金额单一或累计在3000万以上或涉及刑事诉讼）\n4、失信被执行\n5、限高、限制出境\n6、司法拍卖\n7、上市公司实际控制人或财务总监涉及刑事案件\n8、上市公司被监管机构或交易所处罚\n9、生产型企业涉及批量起诉\n10、其他'),
       ('QCC_ASSOCIATION_RISK', 5, '企查查-关联风险', '确认是否存在：\n1、母公司、实控人或持股占比超过51%的关联公司出现上述同类工商异常、经营异常、法律诉讼异常'),
       ('QYYJT_BOND_DEFAULT', 6, '企业预警通-债券违约', '确认是否存在：\n1、任何债券违约'),
       ('ZDW_REGISTRATION_DETAILS', 7, '中登网-登记详细信息列表', '确认是否存在：\n1、中登展期登记'),
       ('CREDIT_REPORT', 8, '征信报告', '确认是否存在：\n1、征信重大异常'),
       ('QG_COURT_EXECUTIONER', 9, '全国法院被执行人或被纳入失信人查询【适用于担保自然人】', '确认是否存在：\n1、被执行人信息'),
       ('OTHER_SUPPLEMENTARY', 10, '其它补充', '确认是否存在：\n1、其他补充查询或说明');

-- 公开信息API
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ('publicInfoExport', '公开信息-导出', 0, 15, 'POST', '/public/info/export', 2),
       ('publicInfoCreateIntervalTable', '公开信息-新建', 0, 15, 'POST',
        '/public/info/create/interval/table', 2),
       ('publicInfoDeleteIntervalTable', '公开信息-删除指定区间表格', 0, 15, 'POST',
        '/public/info/delete/interval/table', 2),
       ('publicInfoClientList', '公开信息-客户列表查询', 0, 15, 'POST', '/public/info/client/list', 2),
       ('publicInfoQueryIntervalTable', '公开信息-查询指定区间表格', 0, 15, 'POST',
        '/public/info/query/interval/table', 2),
       ('publicInfoModifyTableContent', '公开信息-修改表格内容', 0, 15, 'POST',
        '/public/info/modify/table/content', 2),
       ('publicInfoSubmitCheck', '公开信息-项目经理提交前校验', 0, 15, 'POST',
        '/public/info/submit/check', 2),
       ('publicInfoFileUpload', '公开信息-文件上传', 0, 15, 'POST',
        '/file/upload', 2),
       ('publicInfoFileBatchRemove', '公开信息-文件删除', 0, 15, 'POST',
        '/file/batch/remove', 2),
       ('publicInfoFileDownload', '公开信息-文件下载', 0, 15, 'GET',
        '/file/download', 2);
-- =========================================== BigBear 的公开信息SQL END =============================================