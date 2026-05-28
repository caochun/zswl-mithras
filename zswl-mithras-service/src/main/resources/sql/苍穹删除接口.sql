alter table exception_request_info add column source varchar(50) null comment '来源，用于标识该次请求调用模块';
alter table exception_request_info add column business_key varchar(100) null comment '业务主建';
alter table exception_request_info add column business_title varchar(50) null comment '业务模块用于存放页面展示信息，用于帮助业务区分记录';

ALTER TABLE `collection_record_info`
    ADD COLUMN `deleted` TINYINT(1) NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除，默认0';

ALTER TABLE `payment_actual_detail`
    ADD COLUMN `deleted` TINYINT(1) NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除，默认0';

ALTER TABLE `margin_record_info`
    ADD COLUMN `deleted` TINYINT(1) NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除，默认0';

ALTER TABLE `finance_flow_write_off_detail`
    ADD COLUMN `deleted` TINYINT(1) NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除，默认0';

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id) VALUES ('thirdFinancialWithdraw', '反核销数据', 0, 495, null, null, null, 'POST', '/third/financial/withdraw', 2, null);





