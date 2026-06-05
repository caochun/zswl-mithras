CREATE TABLE `proc_attention_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id ',
    `process_instance_id` varchar(64) NOT NULL COMMENT '流程id',
    `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
    `attention_type` tinyint(4) COMMENT '0不关注，1关注',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_process_instance_id_user_id` (`process_instance_id`, `user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='流程关注记录';
