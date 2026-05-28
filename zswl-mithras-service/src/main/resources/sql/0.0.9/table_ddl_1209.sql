CREATE TABLE `biz_process_data` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id	',
    `process_instance_id` varchar(64) NOT NULL COMMENT '流程id',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_process_instance_id` (`process_instance_id`),
    KEY `idx_client_id` (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='流程中的业务数据';

# 邮件发送优化
alter table rent_collection_email_html_store add default_flag tinyint(4) comment '是否默认记录（无界面手填内容）';
alter table rent_collection_email_html_store add version int(11) comment '版本';

alter table exception_info add retry_cnt BIGINT DEFAULT NULL COMMENT '已经重试次数';
alter table exception_info add gmt_update datetime DEFAULT NULL COMMENT '修改时间（重试时间）';
alter table exception_info add retry_send_status TINYINT DEFAULT NULL COMMENT '重试发送状态';
alter table exception_info add notice_id BIGINT DEFAULT NULL COMMENT '消息id';
alter table exception_info add notice_record_id VARCHAR ( 50 ) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '消息redisOffset';
