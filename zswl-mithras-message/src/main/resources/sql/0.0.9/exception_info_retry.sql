alter table exception_info add retry_cnt BIGINT DEFAULT NULL COMMENT '已经重试次数';
alter table exception_info add gmt_update datetime DEFAULT NULL COMMENT '修改时间（重试时间）';
alter table exception_info add retry_send_status TINYINT DEFAULT NULL COMMENT '重试发送状态';
alter table exception_info add notice_id BIGINT DEFAULT NULL COMMENT '消息id';
alter table exception_info add notice_record_id VARCHAR ( 50 ) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '消息redisOffset';
