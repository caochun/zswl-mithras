-- XMX-82直融/间融新增投放资产明细模块  新加‘资金来源’、‘融资编号’
ALTER TABLE payment_actual_detail ADD capital_source varchar(50) COMMENT '资金来源';
ALTER TABLE payment_actual_detail ADD financing_code varchar(50) COMMENT '融资编号';
ALTER TABLE payment_actual_detail_unconfirmed ADD capital_source varchar(50) COMMENT '资金来源';
ALTER TABLE payment_actual_detail_unconfirmed ADD financing_code varchar(50) COMMENT '融资编号';