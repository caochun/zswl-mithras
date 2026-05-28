-- 印花税管报
alter table contract_receipt add column stamp_duty_tax_rate_zl decimal(8,6) NOT NULL DEFAULT 0.00005 COMMENT '融资租赁合同印花税税率' after income_sharing_flag;
alter table contract_receipt add column stamp_duty_tax_rate_mm decimal(8,6) NOT NULL DEFAULT 0.0003 COMMENT '买卖合同印花税税率' after stamp_duty_tax_rate_zl;
alter table contract_receipt add column actual_pay_amount bigint(20) NOT NULL DEFAULT 0 COMMENT '已核销的投放款' after stamp_duty_tax_rate_mm;
alter table contract_receipt add column actual_service_fee_without_tax bigint(20) NOT NULL DEFAULT 0 COMMENT '已核销的手续费/咨询服务费（不含税）' after actual_pay_amount;
alter table contract_receipt add column stamp_duty_zl bigint(20) NOT NULL DEFAULT 0 COMMENT '印花税-融资租赁合同' after actual_service_fee_without_tax;
alter table contract_receipt add column stamp_duty_mm bigint(20) NOT NULL DEFAULT 0 COMMENT '印花税-买卖合同' after stamp_duty_zl;
alter table contract_receipt_lib add column stamp_duty_tax_rate_zl decimal(8,6) NOT NULL DEFAULT 0.00005 COMMENT '融资租赁合同印花税税率' after income_sharing_flag;
alter table contract_receipt_lib add column stamp_duty_tax_rate_mm decimal(8,6) NOT NULL DEFAULT 0.0003 COMMENT '买卖合同印花税税率' after stamp_duty_tax_rate_zl;
alter table contract_receipt_lib add column actual_pay_amount bigint(20) NOT NULL DEFAULT 0 COMMENT '已核销的投放款' after stamp_duty_tax_rate_mm;
alter table contract_receipt_lib add column actual_service_fee_without_tax bigint(20) NOT NULL DEFAULT 0 COMMENT '已核销的手续费/咨询服务费（不含税）' after actual_pay_amount;
alter table contract_receipt_lib add column stamp_duty_zl bigint(20) NOT NULL DEFAULT 0 COMMENT '印花税-融资租赁合同' after actual_service_fee_without_tax;
alter table contract_receipt_lib add column stamp_duty_mm bigint(20) NOT NULL DEFAULT 0 COMMENT '印花税-买卖合同' after stamp_duty_zl;
