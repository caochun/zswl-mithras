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


insert into bifrost_function (code, name, sort_no, menu_id, method, path, type)
values ('financeAccountAgeBaseInfoCount', '帐龄月份-统计', 0, (select id from bifrost_menu where code = 'budgetBusinessAgingTable'), 'POST', '/finance/account/age/base/info/count', 2);

update finance_account_age_item set accountancy_organization_name = '浙江浙商融资租赁有限公司' where accountancy_organization_name is null;


