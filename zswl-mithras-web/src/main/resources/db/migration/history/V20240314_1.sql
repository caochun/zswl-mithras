-- 1> 服务费/咨询费拆分新增服务费，首期利息字段
-- (立项)租赁报价方案&版本表
ALTER TABLE `proj_establish_lease_price`
    ADD COLUMN `commission` bigint(20) NULL DEFAULT 0 COMMENT '手续费' AFTER `consulting_fee`,
ADD COLUMN `first_installment_interest` bigint(20) NULL DEFAULT 0 COMMENT '首期利息' AFTER `commission`;
ALTER TABLE `proj_establish_lease_price_lib`
    ADD COLUMN `commission` bigint(20) NULL DEFAULT 0 COMMENT '手续费' AFTER `consulting_fee`,
ADD COLUMN `first_installment_interest` bigint(20) NULL DEFAULT 0 COMMENT '首期利息' AFTER `commission`;
-- (评审)租赁报价方案&版本表
ALTER TABLE `proj_review_lease_price`
    ADD COLUMN `commission` bigint(20) NULL DEFAULT 0 COMMENT '手续费' AFTER `consulting_fee`,
ADD COLUMN `first_installment_interest` bigint(20) NULL DEFAULT 0 COMMENT '首期利息' AFTER `commission`;
ALTER TABLE `proj_review_lease_price_lib`
    ADD COLUMN `commission` bigint(20) NULL DEFAULT 0 COMMENT '手续费' AFTER `consulting_fee`,
ADD COLUMN `first_installment_interest` bigint(20) NULL DEFAULT 0 COMMENT '首期利息' AFTER `commission`;
-- (合同)租赁报价方案&版本表
ALTER TABLE `contract_lease_price`
    ADD COLUMN `commission` bigint(20) NULL DEFAULT 0 COMMENT '手续费' AFTER `consulting_fee`,
ADD COLUMN `first_installment_interest` bigint(20) NULL DEFAULT 0 COMMENT '首期利息' AFTER `commission`;
ALTER TABLE `contract_lease_price_lib`
    ADD COLUMN `commission` bigint(20) NULL DEFAULT 0 COMMENT '手续费' AFTER `consulting_fee`,
ADD COLUMN `first_installment_interest` bigint(20) NULL DEFAULT 0 COMMENT '首期利息' AFTER `commission`;
-- 付款申请表&付款申请生效表
ALTER TABLE `payment_base_info`
    ADD COLUMN `commission` bigint(20) NULL DEFAULT 0 COMMENT '手续费' AFTER `consulting_fee`,
ADD COLUMN `first_installment_interest` bigint(20) NULL DEFAULT 0 COMMENT '首期利息' AFTER `commission`;
ALTER TABLE `payment_base_info_lib`
    ADD COLUMN `commission` bigint(20) NULL DEFAULT 0 COMMENT '手续费' AFTER `consulting_fee`,
ADD COLUMN `first_installment_interest` bigint(20) NULL DEFAULT 0 COMMENT '首期利息' AFTER `commission`;

-- 手工核销
ALTER TABLE collection_record_info ADD COLUMN write_off_type varchar(20) DEFAULT 'AUTO_RECORD' COMMENT '核销方式' AFTER write_off_status;
ALTER TABLE payment_actual_detail ADD COLUMN write_off_type varchar(20) DEFAULT 'AUTO_RECORD' COMMENT '核销方式' AFTER write_off_status;

INSERT INTO `bifrost_menu` ( `code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`) VALUES
('budgetFlowCenter', 3, 0, NULL, '/budget/flowCenter', NULL, NULL, '流水中心');

INSERT INTO `bifrost_custom_tree_menu_ref` (`custom_tree_id`, `menu_id`, `sort_no`)
SELECT a.id, b.id, 0
from bifrost_custom_tree a, bifrost_menu b
where a.`code` = 'budget' and b.`code` = 'budgetFlowCenter';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type` )
select 'collectionFlowCenterBusinessPaymentList', '付款业务流水列表', 0, id, 'POST', '/collection/flow/center/business/payment/list', 2
from bifrost_menu where `code` = 'budgetFlowCenter';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'collectionFlowCenterBusinessPaymentManualRecord', '付款手工核销', 0, id, 'POST', '/collection/flow/center/business/payment/manual/record', 2
from bifrost_menu where `code` = 'budgetFlowCenter';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'collectionFlowCenterBusinessPaymentSettleDetail', '付款业务流水结算明细', 0, id, 'POST', '/collection/flow/center/business/payment/settle/detail', 2
from bifrost_menu where `code` = 'budgetFlowCenter';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select'collectionFlowCenterBusinessCollectionList', '收款业务流水列表', 0, id, 'POST', '/collection/flow/center/business/collection/list', 2
from bifrost_menu where `code` = 'budgetFlowCenter';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'collectionFlowCenterBusinessCollectionManualRecord', '收款手工核销', 0, id, 'POST', '/collection/flow/center/business/collection/manual/record', 2
from bifrost_menu where `code` = 'budgetFlowCenter';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type` )
select 'collectionFlowCenterBusinessCollectionSettleDetail', '收款业务流水结算明细', 0, id, 'POST', '/collection/flow/center/business/collection/settle/detail', 2
from bifrost_menu where `code` = 'budgetFlowCenter';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'collectionFlowCenterCount', '流水中心统计', 0, id, 'POST', '/collection/flow/center/count', 2
from bifrost_menu where `code` = 'budgetFlowCenter';

update payment_actual_detail set write_off_type = 'MANUAL_RECORD' where info_source = '人工确认';

-- 岗位变更
INSERT INTO `general_dictionary` ( `dict_key`, `dict_desc`, `code`, `display`, `sort` )
VALUES
( 'job', '岗位类型', 'fullreviewcommittee', '专职评审委员', 10 );

UPDATE `general_dictionary` SET `display` = '常任评审委员' WHERE `code` = 'Jury';

UPDATE `general_dictionary` SET `display` = '非常任评审委员' WHERE `code` = 'expertlibrary';

-- 项目评审增加财报校验
INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `create_by`, `update_by`, `en_name`, `method`, `path`, `type`, `group_id`)
VALUES
	('projreviewclientsubjectitemcheckresultreasonsave', '保存财报不完整原因', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/client/subjectitem/checkresult/reason/save', 2, NULL),
	('projreviewclientsubjectitemcheckresultlist', '获取财报校验结果详情', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/client/subjectitem/checkresult/list', 1, NULL),
	('projreviewclientsubjectitemcheckresult', '获取财报校验结果', 0, 12, NULL, NULL, NULL, 'POST', '/proj/review/client/subjectitem/checkresult', 1, NULL);

alter table `proj_review_base_info` add column `subject_item_check_reason` text default null comment '财报不完整原因';
alter table `proj_review_base_info_lib` add column `subject_item_check_reason` text default null comment '财报不完整原因';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('myProcessProcessQueryIndexDownload', '列表导出', 0, 11, NULL, NULL, NULL, 'POST', '/index/download', 2, NULL),
       ('myProcessReceivedAuditedIndexDownload', '列表导出', 0, 10, NULL, NULL, NULL, 'POST', '/index/download', 2, NULL);


