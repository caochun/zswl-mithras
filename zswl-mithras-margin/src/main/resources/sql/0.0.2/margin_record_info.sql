alter table margin_record_info add flow_id VARCHAR(40) DEFAULT NULL COMMENT  '流水id';

alter table margin_record_info add invoice_flag TINYINT DEFAULT NULL COMMENT  '是否开票，0不开 1开';
