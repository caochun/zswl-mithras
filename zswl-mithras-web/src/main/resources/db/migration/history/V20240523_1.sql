-- 发票信息
CREATE TABLE `lease_item_vat_invoice`
(
    `id`                             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `lease_item_info_id`             bigint(20) NOT NULL COMMENT '租赁物审核id',
    `file_id`                        bigint(20) DEFAULT NULL COMMENT '文件id',
    `file_name`                      varchar(200)         DEFAULT NULL COMMENT '文件名',
    `invoice_no`                     varchar(50)          DEFAULT NULL COMMENT '发票号码',
    `invoice_daima`                  varchar(100)         DEFAULT NULL COMMENT '发票代码',
    `invoice_correct_code`           varchar(100)         DEFAULT NULL COMMENT '发票信息验证码',
    `invoice_type`                   varchar(100)         DEFAULT NULL COMMENT '发票类型',
    `invoice_tax_total`              varchar(50)          DEFAULT NULL COMMENT '不含税总金额',
    `invoice_total_cover_tax_digits` varchar(50)          DEFAULT NULL COMMENT '含税总金额',
    `invoice_issue_date`             date                 DEFAULT NULL COMMENT '开票日期',
    `invoice_payer_name`             varchar(50)          DEFAULT NULL COMMENT '购买方',
    `invoice_seller_name`            varchar(50)          DEFAULT NULL COMMENT '销售方',
    `exist_stample`                  varchar(10)          DEFAULT NULL COMMENT '是否盖章',
    `verify_result`                  varchar(200)         DEFAULT NULL COMMENT '验真结果',
    `status`                         varchar(200)         DEFAULT NULL COMMENT '发票状态',
    `note`                           varchar(200)         DEFAULT NULL COMMENT '备注',
    `operation`                      varchar(200)         DEFAULT NULL COMMENT '操作',
    `locked`                         varchar(20) NOT NULL DEFAULT '0' COMMENT '锁定内容不支持修改',
    `create_by`                      bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                    datetime             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                      bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                    datetime             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                        tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3659 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='增值税发票识别信息';

-- 发票产品信息
CREATE TABLE `lease_item_vat_invoice_product`
(
    `id`                         bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `invoice_id`                 bigint(20) NOT NULL COMMENT '发票id',
    `invoice_goods`              varchar(200) DEFAULT NULL COMMENT '货物或服务名称',
    `invoice_plate_specific`     varchar(200) DEFAULT NULL COMMENT '规格型号',
    `invoice_electrans_unit`     varchar(200) DEFAULT NULL COMMENT '单位',
    `invoice_electrans_quantity` varchar(200) DEFAULT NULL COMMENT '数量',
    `invoice_tax_rate`           varchar(200) DEFAULT NULL COMMENT '税率',
    `invoice_tax`                varchar(200) DEFAULT NULL COMMENT '税额',
    `invoice_price`              varchar(200) DEFAULT NULL COMMENT '金额（含税）（小写）',
    `create_by`                  bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`                datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`                  bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`                datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                    tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3573 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='增值税发票产品信息';

-- 发票相关接口
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseVatInvoiceUpload', '增值税发票上传', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vatInvoice/upload', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseVatInvoiceAnewUpload', '增值税发票重新上传', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vatInvoice/anewUpload', 2,
        NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseVatInvoiceList', '增值税发票分页列表', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vatInvoice/list', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseVatInvoiceDelete', '增值税发票删除', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vatInvoice/delete', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseVatInvoiceUpdate', '增值税发票修改', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vatInvoice/update', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseVatInvoiceCount', '统计各状态发票数量', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vatInvoice/count', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseVatInvoiceAmountCheckout', '增值税发票金额校验', 0, 494, NULL, NULL, NULL, 'POST',
        '/lease/vatInvoice/amountCheckout', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseVatInvoiceRetest', '增值税发票重新验真', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vatInvoice/retest', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseVatInvoiceExportExcel', '增值税发票下载', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vatInvoice/exportExcel', 2,
        NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseVatInvoiceLocked', '发票锁定，解锁', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vatInvoice/locked', 2, NULL);

-- ocr接口
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`,
                                `path`, `type`, `group_id`)
VALUES ('leaseOcrFileNameComparison', 'ocr判断是否存在相同文件名', 0, 494, NULL, NULL, NULL, 'POST',
        '/lease/ocr/fileNameComparison', 2, NULL);

-- 车辆登记证字段识别
CREATE TABLE `lease_item_vehicle_registration_certificate` (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
`lease_item_info_id` bigint(20) NOT NULL COMMENT '租赁物审核id',
`vehicle_registration_owner` varchar(50) DEFAULT NULL COMMENT '机动车所有人',
`vehicle_registration_number` varchar(50) DEFAULT NULL COMMENT '机动车登记编号',
`vehicle_vin` varchar(50) DEFAULT NULL COMMENT '车辆识别代号/车架号',
`vehicle_manufacturer` varchar(50) DEFAULT NULL COMMENT '制造厂名称',
`locked` tinyint(4) NOT NULL DEFAULT '0' COMMENT '锁定内容不支持修改',
`operation` varchar(50) DEFAULT NULL COMMENT '操作',
`create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
`update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
`status` varchar(20) DEFAULT NULL COMMENT '识别状态',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=394 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='车辆登记证字段识别';

-- 发票相关接口
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('leasevehiclelist', '车证分页列表', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vehicle/list', 1, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('leasevehicleupload', '车证上传/追加车证', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vehicle/upload', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('leasevehicledelete', '车证批量删除', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vehicle/delete', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('leasevehiclereUpload', '替换车证', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vehicle/reUpload', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('leasevehicleupdate', '车证批量修改', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vehicle/update', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('leasevehiclecount', '统计各状态车证数量', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vehicle/count', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('leasevehicleexportExcel', '车证下载', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vehicle/exportExcel', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('leasevehiclelock', '车证锁定', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vehicle/lock', 2, NULL);
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('leasevehicleunlock', '车证解锁', 0, 494, NULL, NULL, NULL, 'POST', '/lease/vehicle/unlock', 2, NULL);

-- 付款核销需求
INSERT INTO `bifrost_system_config` (`id`, `gmt_create`, `gmt_modified`, `config_key`, `config_value`, `created_by`, `updated_by`, `description`, `status`, `type`) VALUES
    (NULL, '2024-05-13 17:06:32', '2024-05-13 17:24:02', 'paymentFinancialManager', '{\n  \"JCSSYWB\":[\"hejiawei\"],\n  \"GGSY\":[\"zhengxi\"],\n  \"XNYYWB\":[\"zhengxi\"],\n  \"JTYSYWB\":[\"shenyue\"],\n  \"HYYWB\":[\"shenyue\"],\n  \"JSSYB\":[\"wangshilan\"],\n  \"XJZZHXJJTD\":[\"wangshilan\"],\n  \"HGJCYWB\":[\"wangshilan\"],\n}', 'admin', 'admin', '付款核销流程-获取财务经理', 1, 'Json');

ALTER TABLE `payment_base_info` ADD COLUMN `is_finish_put` TINYINT(1) COMMENT '是否结束投放(页面选择)';
ALTER TABLE `payment_base_info` ADD COLUMN `is_finish_put_final` TINYINT(1) COMMENT '是否结束投放(最终结果)';
ALTER TABLE `payment_base_info_lib` ADD COLUMN `is_finish_put` TINYINT(1) COMMENT '是否结束投放(页面选择)';
ALTER TABLE `payment_base_info_lib` ADD COLUMN `is_finish_put_final` TINYINT(1) COMMENT '是否结束投放(最终结果)';

CREATE TABLE `payment_collection_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
    `payment_id` bigint(20) DEFAULT NULL,
    `down_payment` bigint(20) DEFAULT NULL COMMENT '首期租金',
    `earnest_money` bigint(20) DEFAULT NULL COMMENT '保证金',
    `retention_money` bigint(20) DEFAULT NULL COMMENT '质保金',
    `consulting_fee` bigint(20) DEFAULT NULL COMMENT '服务费/咨询费',
    `commission` bigint(20) DEFAULT NULL COMMENT '手续费(元)',
    `first_installment_interest` bigint(20) DEFAULT NULL COMMENT '首期利息(元)',
    `create_by` bigint(20) DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_by` bigint(20) DEFAULT NULL,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `paymentId` (`payment_id`) USING BTREE COMMENT '唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COMMENT='付款核销流程中存储的收款信息';

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('2024-05-15 13:09:56', '2024-05-15 16:37:07', NULL, 'saveCollectionInfo', '保存收款信息', 0, (select id from bifrost_menu where code = 'QX0114'), NULL, NULL, NULL, 'POST', '/payment/collection/add', 2, NULL),
    ('2024-05-15 16:39:00', '2024-05-15 16:37:07', NULL, 'detailCollectionInfo', '查看收款信息', 0, (select id from bifrost_menu where code = 'QX0114'), NULL, NULL, NULL, 'GET', '/payment/collection/detail', 1, NULL);

-- 经营全景视图
CREATE TABLE `dashboard_authority_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `dashboard_key` varchar(50) NOT NULL COMMENT '看板key',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户可查看工作台看板配置表';
CREATE TABLE `dashboard_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dashboard_key` varchar(50) NOT NULL COMMENT '看板key',
  `dashboard_display` varchar(50) NOT NULL COMMENT '看板名称',
  `order_num` int(10) NOT NULL DEFAULT '0' COMMENT '排序权重',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='工作台看板配置表';
CREATE TABLE `guanyuan_ds_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `business_key` varchar(50) NOT NULL COMMENT '业务key',
  `guanyuan_ds_id` varchar(50) NOT NULL COMMENT '观远数据集id',
  `guanyuan_ds_remark` varchar(100) NOT NULL DEFAULT '' COMMENT '观远数据集备注',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_business_key` (`business_key`) USING BTREE,
  KEY `idx_guanyuan_ds_id` (`guanyuan_ds_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='观远数据集信息';
INSERT INTO `dashboard_config` (`dashboard_key`, `dashboard_display`, `order_num`)
VALUES
	('AssetsOverview', '资产总览', 0),
	('AssetsClientDistribution', '资产及客户分布', 100),
	('BusinessTransformFunnel', '业务转化漏斗', 200),
	('ProjectOperationEfficiency', '项目运营效率', 300),
	('AssetsFiveClassify', '资产五级分类', 400),
	('OverdueProject', '逾期项目信息', 500);
INSERT INTO `dashboard_authority_config` (`user_id`, `dashboard_key`)
VALUES
	(3, 'AssetsFiveClassify'),
	(3, 'OverdueProject'),
	(3, 'AssetsOverview'),
	(3, 'AssetsClientDistribution'),
	(3, 'ProjectOperationEfficiency'),
	(125, 'AssetsFiveClassify'),
	(125, 'OverdueProject'),
	(125, 'AssetsOverview'),
	(125, 'AssetsClientDistribution'),
	(125, 'ProjectOperationEfficiency'),
	(49, 'AssetsFiveClassify'),
	(49, 'OverdueProject'),
	(49, 'AssetsOverview'),
	(49, 'AssetsClientDistribution'),
	(49, 'ProjectOperationEfficiency');
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('dashboardoperationefficiencystatistics', '项目运营效率-统计', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/operationefficiency/statistics', 1, NULL),
	('dashboardoperationefficiencylist', '项目运营效率-按部门明细', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/operationefficiency/list', 1, NULL),
	('dashboardoverdueprojectlist', '逾期项目信息-逾期项目信息', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/overdueproject/list', 1, NULL),
	('dashboardconversionstatistics', '业务转化漏斗-各阶段转化数据', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/conversion/statistics', 1, NULL),
	('dashboardassetsdetailbyarea', '资产总览-资产投放明细-按经济圈', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/assets/detail/byarea', 1, NULL),
	('dashboardassetsbalanceoverview', '资产总览-余额总览', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/assets/balance/overview', 1, NULL),
	('dashboardassetsloanoverview', '资产总览-投放总览', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/assets/loan/overview', 1, NULL),
	('dashboardassetsdetailbyprovinceexport', '资产总览-资产投放明细-按省份-导出', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/assets/detail/byprovince/export', 1, NULL),
	('dashboardassetsdetailbyprovince', '资产总览-资产投放明细-按省份', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/assets/detail/byprovince', 1, NULL),
	('dashboardassetfiveclassifystatistics', '资产五级分类-资产五级分类统计', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/assetfiveclassify/statistics', 1, NULL),
	('dashboarddistributionclientstatistics', '资产及客户分布-资产客户统计', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/distribution/clientstatistics', 2, NULL),
	('dashboarddistributionclientdepartment', '资产及客户分布-客户部门分布', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/distribution/clientdepartment', 1, NULL),
	('dashboarddistributionassetsindustry', '资产及客户分布-资产行业分布', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/distribution/assetsindustry', 1, NULL),
	('dashboardlist', '看板列表', 0, 727, NULL, NULL, NULL, 'POST', '/dashboard/list', 1, NULL);
INSERT INTO `guanyuan_ds_info` (`business_key`, `guanyuan_ds_id`, `guanyuan_ds_remark`)
VALUES
	('AssetsFiveClassifyYearQuarter', 'jfaf5cfa2fc1a4cd3bf3b899', '资产五级分类-已完成季度汇总'),
	('AssetsFiveClassifyStatisticsByCompany', 'u0b0fdc23f52741f1929b9f6', '资产五级分类-按公司统计'),
	('OverdueProjectList', 'm0a4f96ef4e224f419e74fc8', '逾期项目信息-按公司统计'),
	('BusinessTransformFunnelAllByCompany', 'v01ad58d47a0249e48ba8538', '业务转化漏斗-历史合计-按公司统计'),
	('BusinessTransformFunnelThisYearByCompany', 'j01c45cf6cc194a418fcd5b8', '业务转化漏斗-本年合计-按公司统计'),
	('AssetsOverviewGroupByCityByCompany', 'j79c7b15126d1444cbbe5467', '资产总览-城市维度-按公司统计'),
	('AssetsOverviewByCompany', 'mcdb08379538d4911820a932', '资产总览-按公司统计'),
	('AssetsClientDistributionClientStatisticsByCompany', 'b62ffe59570ab4e968518fde', '资产及客户分布-各阶段客户-按公司统计'),
	('AssetsClientDistributionClientDeptByCompany', 'xd8a9c3b21a374f9abad61bd', '资产及客户分布-客户部门分布-按公司统计'),
	('AssetsIndustryDistributionByCompany', 'm0ba25e720d1646448c711c1', '资产及客户分布-资产行业分布-按公司统计'),
	('OperationEfficiencyStageLX', 'tf614962e6b07410aa9dccee', '项目运营效率-立项'),
	('OperationEfficiencyStagePS', 'xbf86e386592a46f08a6fc03', '项目运营效率-评审'),
	('OperationEfficiencyStagePSJSWCJHT', 'p6834f50ca96b4ce492ff336', '项目运营效率-评审结束未创建合同'),
	('OperationEfficiencyStageQY', 'nf5b6d2187c964b8fab8852b', '项目运营效率-签约'),
	('OperationEfficiencyStageFK', 'sd1a3ef02c73145efb3bc98a', '项目运营效率-付款'),
	('OperationEfficiencyStageTF', 'q6979a752edce477eb52237f', '项目运营效率-投放');

-- #################################################################################################################
-- 银行流水20240523上线SQL
CREATE TABLE `finance_flow_record`
(
    `id`                            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `billno`                        varchar(255)   DEFAULT NULL COMMENT '交易明细编号',
    `billstatus`                    varchar(255)   DEFAULT NULL COMMENT '单据状态',
    `auditdate`                     varchar(255)   DEFAULT NULL COMMENT '审核日期',
    `modifytime`                    varchar(255)   DEFAULT NULL COMMENT '最后更新时间',
    `createtime`                    varchar(255)   DEFAULT NULL COMMENT '创建时间',
    `bizdate`                       varchar(255)   DEFAULT NULL COMMENT '交易日期',
    `sourcebillid`                  bigint(20)     DEFAULT NULL COMMENT '源单id',
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
    `ismatchereceipt`               tinyint(1)     DEFAULT NULL COMMENT '跟电子回单匹配',
    `isdataimport`                  tinyint(1)     DEFAULT NULL COMMENT '是否导入',
    `detailid`                      varchar(255)   DEFAULT NULL COMMENT '明细流水号',
    `isdowntobankstate`             tinyint(1)     DEFAULT NULL COMMENT '已经下载到银行对账单',
    `isnoreceipt`                   tinyint(1)     DEFAULT NULL COMMENT '确认无回单',
    `receiptno`                     varchar(255)   DEFAULT NULL COMMENT '电子回单关联标记',
    `originalbankcheckflag`         varchar(255)   DEFAULT NULL COMMENT '对账标识码(银行返回)',
    `datasource`                    varchar(255)   DEFAULT NULL COMMENT '数据来源',
    `biztype`                       varchar(255)   DEFAULT NULL COMMENT '业务类型',
    `isrefund`                      tinyint(1)     DEFAULT NULL COMMENT '是否退票',
    `bankinterface`                 varchar(255)   DEFAULT NULL COMMENT '银行接口',
    `isreced`                       tinyint(1)     DEFAULT NULL COMMENT '是否接收',
    `biztime`                       datetime       DEFAULT NULL COMMENT '交易时间',
    `iskdretflag`                   tinyint(1)     DEFAULT NULL COMMENT '是否银企付标',
    `istransup`                     tinyint(1)     DEFAULT NULL COMMENT '银行上传',
    `istransdown`                   tinyint(1)     DEFAULT NULL COMMENT '银行下载',
    `isbankwithholding`             tinyint(1)     DEFAULT NULL COMMENT '银行代扣',
    `recedbilltype`                 varchar(255)   DEFAULT NULL COMMENT '接收单据类型',
    `receredtype`                   varchar(255)   DEFAULT NULL COMMENT '入账状态',
    `sortno`                        bigint(20)     DEFAULT NULL COMMENT '排序号',
    `rulename`                      varchar(255)   DEFAULT NULL COMMENT '适配规则',
    `businessbillnum`               varchar(255)   DEFAULT NULL COMMENT '票据号',
    `autorecorpay`                  tinyint(1)     DEFAULT NULL COMMENT '自动收付款',
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
    `ishandlink`                    tinyint(1)     DEFAULT NULL COMMENT '是否手工关联',
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
    `financing_flow_type`           varchar(32)    DEFAULT NULL COMMENT '流水处理类别，理论上不会为空{@link BankFlowCenterTypeEnum}',
    `write_off_status`              varchar(32)    DEFAULT NULL COMMENT '核销状态{@link financingFlowWriteOffStatusEnum}',
    `logic_delete_flag`             tinyint(2)     DEFAULT '0' COMMENT '苍穹是否已删除标识 默认是0 未删除， 1 已删除',
    `financing_project_type`        varchar(32)    DEFAULT NULL COMMENT '资金端业务端标识{@link FinancingProjectTypeEnum}',
    `surplus_amount`                bigint(20)     DEFAULT NULL COMMENT '剩余可核销金额',
    PRIMARY KEY (`id`),
    KEY `idx_billno` (`billno`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 405
  DEFAULT CHARSET = utf8mb4 COMMENT ='财资平台流水记录';

CREATE TABLE `fund_direct_financing_repay_record`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `financing_id`    bigint(20)          DEFAULT NULL COMMENT '资金主表ID',
    `cash_flow_item`  varchar(32)         DEFAULT NULL COMMENT '现金流项目',
    `phase`           int(11)             DEFAULT NULL COMMENT '期项',
    `pay_amount`      bigint(20)          DEFAULT NULL COMMENT '收款金额，存毫厘',
    `pay_date`        datetime            DEFAULT NULL COMMENT '收款日期',
    `finance_flow_id` bigint(20)          DEFAULT NULL COMMENT '收款对应流水ID',
    `bank_detail_no`  bigint(20)          DEFAULT NULL COMMENT '收款对应银行流水号',
    `create_by`       bigint(20)          DEFAULT NULL COMMENT '创建人',
    `update_by`       bigint(20)          DEFAULT NULL COMMENT '创建人',
    `create_time`     datetime            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`         tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`),
    KEY `finance_flow_id` (`finance_flow_id`),
    KEY `bank_detail_no` (`bank_detail_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='直融资金还款记录';

CREATE TABLE `fund_direct_financing_collect_record`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `financing_id`      bigint(20)          DEFAULT NULL COMMENT '资金主表ID',
    `cash_flow_item`    varchar(32)         DEFAULT NULL COMMENT '现金流项目',
    `collection_amount` bigint(20)          DEFAULT NULL COMMENT '收款金额，存毫厘',
    `collection_date`   datetime            DEFAULT NULL COMMENT '收款日期',
    `finance_flow_id`   bigint(20)          DEFAULT NULL COMMENT '收款对应流水ID',
    `bank_detail_no`    bigint(20)          DEFAULT NULL COMMENT '收款对应银行流水号',
    `create_by`         bigint(20)          DEFAULT NULL COMMENT '创建人',
    `update_by`         bigint(20)          DEFAULT NULL COMMENT '创建人',
    `create_time`       datetime            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`           tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`),
    KEY `finance_flow_id` (`finance_flow_id`),
    KEY `bank_detail_no` (`bank_detail_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='直融资金收款记录';

CREATE TABLE `fund_financing_collect_record`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `financing_id`      bigint(20)          DEFAULT NULL COMMENT '资金主表ID',
    `collection_amount` bigint(20)          DEFAULT NULL COMMENT '收款金额，存毫厘',
    `collection_date`   datetime            DEFAULT NULL COMMENT '收款日期',
    `finance_flow_id`   bigint(20)          DEFAULT NULL COMMENT '收款对应流水ID',
    `bank_detail_no`    bigint(20)          DEFAULT NULL COMMENT '收款对应银行流水号',
    `create_by`         bigint(20)          DEFAULT NULL COMMENT '创建人',
    `update_by`         bigint(20)          DEFAULT NULL COMMENT '创建人',
    `create_time`       datetime            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`           tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`),
    KEY `finance_flow_id` (`finance_flow_id`),
    KEY `bank_detail_no` (`bank_detail_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='间融资金收款记录';

CREATE TABLE `fund_financing_repay_record`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `financing_id`    bigint(20)          DEFAULT NULL COMMENT '资金主表ID',
    `cash_flow_item`  varchar(32)         DEFAULT NULL COMMENT '现金流项目',
    `phase`           int(11)             DEFAULT NULL COMMENT '期项',
    `pay_amount`      bigint(20)          DEFAULT NULL COMMENT '收款金额，存毫厘',
    `pay_date`        datetime            DEFAULT NULL COMMENT '收款日期',
    `finance_flow_id` bigint(20)          DEFAULT NULL COMMENT '收款对应流水ID',
    `bank_detail_no`  bigint(20)          DEFAULT NULL COMMENT '收款对应银行流水号',
    `create_by`       bigint(20)          DEFAULT NULL COMMENT '创建人',
    `update_by`       bigint(20)          DEFAULT NULL COMMENT '创建人',
    `create_time`     datetime            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted`         tinyint(2) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`),
    KEY `finance_flow_id` (`finance_flow_id`),
    KEY `bank_detail_no` (`bank_detail_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='间融资金还款记录';

-- 下面是为了知道流水的具体去向要加的字段
-- 1、项目端付款，包括投放和保证金退款
ALTER TABLE `payment_actual_detail`
    ADD `finance_flow_id` bigint(20)  NULL COMMENT '银行流水ID',
    ADD `bank_detail_no`  varchar(32) NULL COMMENT '银行流水编号';

ALTER TABLE `margin_record_info`
    ADD `finance_flow_id` bigint(20)  NULL COMMENT '银行流水ID',
    ADD `bank_detail_no`  varchar(32) NULL COMMENT '银行流水编号';

-- 2、项目端收款，包括保证金、手续费、租金、罚息等
ALTER TABLE `collection_record_info`
    ADD `finance_flow_id` bigint(20)  NULL COMMENT '银行流水ID',
    ADD `bank_detail_no`  varchar(32) NULL COMMENT '银行流水编号';

-- 3、资金端收款，分直融和间融
ALTER TABLE `fund_financing_base_info`
    ADD `write_off_status`  varchar(32) NULL COMMENT '核销状态';
ALTER TABLE `fund_financing_base_info_lib`
    ADD `write_off_status`  varchar(32) NULL COMMENT '核销状态';

-- 直接融资没有版本表
ALTER TABLE `fund_direct_financing_base_info`
    ADD `write_off_status`  varchar(32) NULL COMMENT '核销状态';

-- 4、资金端付款，分直融和间融
ALTER TABLE `fund_direct_financing_repay_actual`
    ADD `write_off_status`  varchar(32) NULL COMMENT '核销状态';

ALTER TABLE `fund_financing_repay_actual`
    ADD `write_off_status`  varchar(32) NULL COMMENT '核销状态';


INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
VALUES ('bankCenterProjectSubList', '银行流水中心项目端-根据现金流ID获取对应子列表', 0, 495, 'POST',
        '/bank/center/project/sub/list', 2),
       ('bankCenterDelete', '银行流水中心-删除流水', 0, 495, 'POST', '/bank/center/delete', 2),
       ('bankCenterNoProcessingRequire', '银行流水中心-批量转移到无需处理', 0, 495, 'POST',
        '/bank/center/no/processing/require', 2),
       ('bankCenterBatchWriteOff', '银行流水批量核销', 0, 495, 'POST', '/bank/center/batch/write/off', 2),
       ('bankCenterProjectAmountDetail', '银行流水中心-项目端根据收款ID期项现金流项目金额详情', 0, 495, 'POST',
        '/bank/center/project/amount/detail', 1),
       ('bankCenterProjectPhaseList', '银行流水中心项目端根据借据ID和现金流项目查询期项', 0, 495, 'POST',
        '/bank/center/project/phase/list', 1),
       ('bankCenterProjectReceiptList', '银行流水中心-项目端根据合同ID查询借据', 0, 495, 'POST',
        '/bank/center/project/receipt/list', 1),
       ('bankCenterList', '银行流水中心列表', 0, 495, 'POST', '/bank/center/list', 2),
       ('businessFlowFinanceList', '业务流水资金端列表', 0, 495, 'POST', '/business/flow/finance/list', 2),
       ('bankCenterManualPullFlow', '手动拉取资金流水', 0, 495, 'POST', '/bank/center/manual/pull/flow', 2);

-- ####################################################################################################################

ALTER TABLE `contract_receipt` ADD COLUMN `stamp_duty` BIGINT(20) COMMENT '印花税';
ALTER TABLE `contract_receipt_lib` ADD COLUMN `stamp_duty` BIGINT(20) COMMENT '印花税';

-- 月结管理
INSERT INTO `bifrost_menu` (`gmt_create`, `gmt_modified`, `code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`, `id`, `en_name`, `target`, `create_by`, `update_by`) VALUES ('2024-05-21 16:04:44', '2024-05-23 14:05:59', 'financialMonthlyManagement', 3, 0, NULL, '/budget/financialMonthlyManagement', NULL, NULL, '月结管理', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `bifrost_custom_tree_menu_ref` (`id`, `gmt_create`, `gmt_modified`, `create_by`, `update_by`, `custom_tree_id`, `menu_id`, `sort_no`) VALUES (NULL, '2024-02-29 14:28:22', '2024-04-19 17:15:43', NULL, NULL, (select id from bifrost_custom_tree where code = 'budget'), (select id from bifrost_menu where code = 'financialMonthlyManagement'), 0);

INSERT INTO `bifrost_function` (`gmt_create`, `gmt_modified`, `id`, `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('2024-05-21 18:02:54', '2024-05-21 18:02:54', null, 'monthlyListPage', '列表', 0, (select id from bifrost_menu where code = 'financialMonthlyManagement'), NULL, NULL, NULL, 'POST', '/monthly/list/page', 1, NULL),
    ('2024-05-23 13:56:52', '2024-05-23 13:56:52', null, 'monthlyAirPage', '收入确认-实际利率法列表', 0, (select id from bifrost_menu where code = 'financialMonthlyManagement'), NULL, NULL, NULL, 'POST', '/monthly/air/page', 1, NULL),
    ('2024-05-23 13:57:26', '2024-05-23 13:57:26', null, 'monthlyRpPage', '收入计提-剩余本金法列表', 0, (select id from bifrost_menu where code = 'financialMonthlyManagement'), NULL, NULL, NULL, 'POST', '/monthly/rp/page', 1, NULL),
    ('2024-05-23 13:57:54', '2024-05-23 13:57:54', null, 'monthlyStampDutyProjPage', '印花税计提-项目端', 0, (select id from bifrost_menu where code = 'financialMonthlyManagement'), NULL, NULL, NULL, 'POST', '/monthly/stampDuty/proj/page', 1, NULL),
    ('2024-05-23 13:58:28', '2024-05-23 13:58:28', null, 'monthlyStampDutyFinPage', '印花税计提-资金端', 0, (select id from bifrost_menu where code = 'financialMonthlyManagement'), NULL, NULL, NULL, 'POST', '/monthly/stampDuty/fin/page', 1, NULL),
    ('2024-05-23 13:58:56', '2024-05-23 13:58:56', null, 'monthlyDownload', 'Excel导出', 0, (select id from bifrost_menu where code = 'financialMonthlyManagement'), NULL, NULL, NULL, 'POST', '/monthly/download', 1, NULL),
    ('2024-05-23 13:59:19', '2024-05-23 13:59:19', null, 'monthlyCostList', '每月成本计提', 0, (select id from bifrost_menu where code = 'financialMonthlyManagement'), NULL, NULL, NULL, 'POST', '/monthly/cost/list', 1, NULL),
    ('2024-05-23 13:59:43', '2024-05-23 13:59:43', null, 'monthlyValidate', '校验', 0, (select id from bifrost_menu where code = 'financialMonthlyManagement'), NULL, NULL, NULL, 'POST', '/monthly/validate', 1, NULL),
    ('2024-05-23 14:00:02', '2024-05-23 14:00:02', null, 'monthlySubmit', '提交', 0, (select id from bifrost_menu where code = 'financialMonthlyManagement'), NULL, NULL, NULL, 'POST', '/monthly/submit', 2, NULL);

CREATE TABLE `monthly_stamp_duty` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `type` varchar(20) NOT NULL COMMENT '类型-项目端/资金端',
    `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',
    `client_name` varchar(50) DEFAULT NULL COMMENT '客户名称',
    `contract_code` varchar(50) DEFAULT '' COMMENT '合同编号',
    `financing_id` bigint(20) DEFAULT NULL COMMENT '融资id',
    `organization_name` varchar(50) DEFAULT NULL COMMENT '融资渠道',
    `financing_code` varchar(50) DEFAULT '' COMMENT '融资编号',
    `stamp_duty` bigint(20) DEFAULT '0' COMMENT '印花税',
    `date` date DEFAULT NULL COMMENT '日期',
    `is_confirmed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '收入是否确认，0-否，1-是',
    `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `monthly_user_record` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `user_id` bigint(20) NOT NULL COMMENT '用户id',
    `type` varchar(20) NOT NULL COMMENT '类型',
    `value` text DEFAULT NULL COMMENT '内容',
    `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4;


CREATE TABLE `funds_daily_cost` (
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
`financing_id` bigint(20) NOT NULL COMMENT '融资id',
`item_text` varchar(20) NOT NULL COMMENT '展示文本（通常是日期，初始化会使用“期初余额”替代日期）',
`interest_date` date NOT NULL COMMENT '计息日期',
`financing_amount` bigint(20) NOT NULL COMMENT '融资金额',
`principle_amount` bigint(20) DEFAULT NULL COMMENT '还款本金',
`interest_amount` bigint(20) DEFAULT NULL COMMENT '还款利息',
`financing_rate` int(10) DEFAULT NULL COMMENT '融资利率',
`daily_rate` int(10) DEFAULT NULL COMMENT '日利率',
`financing_cost` bigint(20) DEFAULT NULL COMMENT '融资成本',
`total_capital_cost` bigint(20) NOT NULL DEFAULT '0' COMMENT '累计计提资金成本',
`total_capital_cost_after_tax` bigint(20) NOT NULL DEFAULT '0' COMMENT '累计计提资金成本税后',
`type` varchar(20) NOT NULL COMMENT '融资类型',
`deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
`create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
`update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`is_confirmed` tinyint(4) NOT NULL DEFAULT '0' COMMENT '收入是否确认，0-否，1-是',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=879 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='资⾦每⽇成本记录';


ALTER TABLE `contract_settle_plan_lib` ADD COLUMN `before_maturity_interest` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '未到期利息';
ALTER TABLE `contract_settle_plan` ADD COLUMN `before_maturity_interest` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '未到期利息';