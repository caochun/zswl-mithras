CREATE TABLE `biz_process_data` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id	',
    `process_instance_id` varchar(64) NOT NULL COMMENT '流程id',
    `client_id` bigint(20) DEFAULT NULL COMMENT '客户id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_process_instance_id` (`process_instance_id`),
    KEY `idx_client_id` (`client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='流程中的业务数据';
