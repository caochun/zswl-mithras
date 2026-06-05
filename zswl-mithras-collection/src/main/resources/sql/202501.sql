ALTER TABLE collection_record_info
    ADD COLUMN deduction_margin_base_id bigint(20) DEFAULT null COMMENT '抵扣保证金id' AFTER write_off_type;
ALTER TABLE collection_record_info
    ADD COLUMN deduction_margin_base_code varchar (50) DEFAULT null COMMENT '抵扣保证金编号' AFTER deduction_margin_base_id;


INSERT INTO bifrost_function (code, name, menu_id, method, path, type)
VALUES ('collectionFlowCenterClientContractMargin', '查询客户下可用合同保证金', (select id from bifrost_menu where code = 'budgetFlowCenter'), 'POST',
       '/collection/flow/center/client/contract/margin', '2');



