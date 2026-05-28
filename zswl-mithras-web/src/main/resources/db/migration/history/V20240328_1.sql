-- 增加删除数据留档
CREATE TABLE `system_database_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `operation` varchar(20) DEFAULT '' COMMENT '操作方式',
  `operator_id` bigint(20) DEFAULT '-1' COMMENT '操作人id',
  `batch_sequence` varchar(100) NOT NULL DEFAULT '' COMMENT '操作批次号',
  `origin_sql` longtext COMMENT '原始sql',
  `origin_parameter` longtext COMMENT '原始参数',
  `table_name` varchar(100) NOT NULL DEFAULT '' COMMENT '表名',
  `row_data` longtext COMMENT '原始sql查询得到的行数据',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_table_batch` (`table_name`,`batch_sequence`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库记录表';

INSERT INTO `system_switch` (`code`, `description`, `value`)
VALUES
	('STORE_DELETE_RECORD', '持久化物理删除记录', 1);

-- 付款增加校验
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('paymentprojreviewtimeoutcheck', '检查付款申请对应的项目评审是否超时', 0, 15, NULL, NULL, NULL, 'POST', '/payment/projreview/timeout/check', 1, NULL),
	('riskcontrolopinionmonitorcount', '查询指定客户的需处理舆情数量', 0, 398, NULL, NULL, NULL, 'POST', '/risk/control/opinion/monitor/count', 1, NULL);
-- 付款核销查询部门
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'paymentWriteOffSelectorgs', '付款核销-业务部门列表', 0, id, 'GET', '/select/orgs', 1
from bifrost_menu where `code` = 'QX0114';
-- 消息添加读字段 不上线 先注释
-- ALTER TABLE zhfk_notice
--     ADD COLUMN `read_status` TINYINT(1) NULL DEFAULT 2 COMMENT '消息读状态 1 已读，2未读' AFTER `status`;

CREATE TABLE file_authentication_config
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    file_name   VARCHAR(64) NULL COMMENT '文件名称',
    file_type   VARCHAR(64) NOT NULL COMMENT '文件类型',
    owner_type  TINYINT(1)  NOT NULL COMMENT '岗位/人',
    owner_post  VARCHAR(64) NULL COMMENT '关联用户岗位',
    owner_id    BIGINT      NULL COMMENT '关联用户ID',
    create_by   BIGINT      NULL COMMENT '创建人ID',
    update_by   BIGINT      NULL COMMENT '更新人ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '文件模版权限配置表';

INSERT INTO file_authentication_config SET file_type = 'ftp月度指导报价', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '保单管理-保单模版', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-保理合同', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-保证合同', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-合同清单', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-咨询合同', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-抵押合同', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-直租合同', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-租赁合同', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-质押合同', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '租后-租后外部信息查询报告', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '租后-租后检查报告', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '租赁物模版-中登网查重', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '租赁物管理-租赁物清单', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '资产五级分类', owner_post = 'admin', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-保理合同', owner_post = 'legalmanager', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-保证合同', owner_post = 'legalmanager', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-合同清单', owner_post = 'legalmanager', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-咨询合同', owner_post = 'legalmanager', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-抵押合同', owner_post = 'legalmanager', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-直租合同', owner_post = 'legalmanager', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-租赁合同', owner_post = 'legalmanager', owner_type = 1, update_by = 125, create_by = 125;
INSERT INTO file_authentication_config SET file_type = '合同-质押合同', owner_post = 'legalmanager', owner_type = 1, update_by = 125, create_by = 125;

-- 付款交易结构
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
    ('paymenttransactionStructureInfo', '付款申请-交易结构信息', 0, 15, NULL, NULL, NULL, 'POST', '/payment/transactionStructureInfo', 1,NULL);

-- 项目及合同阶段增加评估主体字段
ALTER TABLE proj_establish_base_info
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `funds_purpose`;
ALTER TABLE proj_establish_base_info_lib
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `funds_purpose`;
ALTER TABLE proj_review_base_info
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `proj_source`;
ALTER TABLE proj_review_base_info_lib
    ADD COLUMN `evaluation_subject_id` BIGINT(20) NULL DEFAULT null COMMENT '评估主体ID' AFTER `proj_source`;

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'projEstablishGetClientAddress', '获取法人客户地址信息', 0, id, 'POST', '/proj/establish/get/client/address', 2
from bifrost_menu where `code` = 'QX0106';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'projReviewGetClientAddress', '获取法人客户地址信息', 0, id, 'POST', '/proj/establish/get/client/address', 2
from bifrost_menu where `code` = 'QX0110';

-- 融资的质押与监管
ALTER TABLE `fund_direct_financing_pledge_info`
ADD COLUMN `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL   COMMENT '账户名称' AFTER `remaining_unpaid_principal`,
ADD COLUMN `account_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '账号' AFTER `account_name`,
ADD COLUMN `account_bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支行名称' AFTER `account_number`,
ADD COLUMN `is_pledge` tinyint(1)  DEFAULT NULL  COMMENT '是否质押' AFTER `account_bank`,
ADD COLUMN `is_supervise` tinyint(1)  DEFAULT NULL  COMMENT '是否监管' AFTER `is_pledge`;

ALTER TABLE `fund_financing_pledge_info`
ADD COLUMN `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL   COMMENT '账户名称' AFTER `remaining_unpaid_principal`,
ADD COLUMN `account_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '账号' AFTER `account_name`,
ADD COLUMN `account_bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支行名称' AFTER `account_number`,
ADD COLUMN `is_pledge` tinyint(1)  DEFAULT NULL  COMMENT '是否质押' AFTER `account_bank`,
ADD COLUMN `is_supervise` tinyint(1)  DEFAULT NULL  COMMENT '是否监管' AFTER `is_pledge`;

ALTER TABLE `fund_financing_pledge_info_lib`
ADD COLUMN `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL  COMMENT '账户名称' AFTER `remaining_unpaid_principal`,
ADD COLUMN `account_number` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '账号' AFTER `account_name`,
ADD COLUMN `account_bank` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支行名称' AFTER `account_number`,
ADD COLUMN `is_pledge` tinyint(1)  DEFAULT NULL  COMMENT '是否质押' AFTER `account_bank`,
ADD COLUMN `is_supervise` tinyint(1)  DEFAULT NULL  COMMENT '是否监管' AFTER `is_pledge`;

-- ftp菜单权限
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpFinancingCostDraftCompare', 'ftp定价比对-融资成本列表', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/financing/cost/draft/compare', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpGuaranteeCostPricingCompare', 'ftp定价比对-担保成本列表', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/guarantee/cost/pricing/compare', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpLprPricingCompare', 'ftp定价比对-LPR列表', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/lpr/pricing/compare', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpShiborInterestRateCompare', 'ftp定价比对-1年期SHIBOR利率列表', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/shibor/interest/rate/compare', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpTreasuryBondYieldCompare', 'ftp定价比对-十年期国债收益率', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/treasury/bond/yield/compare', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpFinancingCostPricingDraftFlash', '融资成本定价刷新编辑区', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/financing/cost/pricing/draft/flash', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpFinancingCostDraftDetail', '融资成本编辑区查询接口', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/financing/cost/draft/detail', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpGuaranteeCostPricingDraftFlash', '编辑-修改担保成本刷新', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/guarantee/cost/pricing/draft/flash', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpGuaranteeCostPricingDraftList', '编辑-担保成本定价列表', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/guarantee/cost/pricing/draft/list', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpGuaranteeCostPricingDraftModify', '编辑-修改担保成本定价', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/guarantee/cost/pricing/draft/modify', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpDetailLprPricing', '详情页-LPR列表', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/detail/lpr/pricing', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpDetailShiborInterestRate', '详情页-1年期SHIBOR利率列表', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/detail/shibor/interest/rate', 2, NULL);
INSERT INTO `bifrost_function` ( `code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`) VALUES ('newFtpDetailTreasuryBondYield', '详情页-十年期国债收益率', 0, 484, NULL, NULL, NULL, 'POST', '/new/ftp/detail/treasury/bond/yield', 2, NULL);


