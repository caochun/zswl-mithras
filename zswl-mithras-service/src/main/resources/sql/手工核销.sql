ALTER TABLE collection_record_info ADD COLUMN write_off_type varchar(20) DEFAULT 'AUTO_RECORD' COMMENT '核销方式' AFTER write_off_status;
ALTER TABLE payment_actual_detail ADD COLUMN write_off_type varchar(20) DEFAULT 'AUTO_RECORD' COMMENT '核销方式' AFTER write_off_status;

INSERT INTO `bifrost_menu` ( `code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`, ) VALUES
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

INSERT INTO `mithras`.`bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'collectionFlowCenterBusinessCollectionManualRecord', '收款手工核销', 0, id, 'POST', '/collection/flow/center/business/collection/manual/record', 2
from bifrost_menu where `code` = 'budgetFlowCenter';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type` )
select 'collectionFlowCenterBusinessCollectionSettleDetail', '收款业务流水结算明细', 0, id, 'POST', '/collection/flow/center/business/collection/settle/detail', 2
from bifrost_menu where `code` = 'budgetFlowCenter';

INSERT INTO `bifrost_function` (`code`, `name`, `sort_no`, `menu_id`, `method`, `path`, `type`)
select 'collectionFlowCenterCount', '流水中心统计', 0, id, 'POST', '/collection/flow/center/count', 2
from bifrost_menu where `code` = 'budgetFlowCenter';