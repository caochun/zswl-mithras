ALTER TABLE payment_actual_detail ADD COLUMN write_off_type varchar(20) DEFAULT 'AUTO_RECORD' COMMENT '核销方式' AFTER write_off_status;
