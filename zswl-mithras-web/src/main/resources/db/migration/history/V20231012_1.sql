-- 反核销标识
alter table collection_record_info
    add column `cancel_write_off_id` bigint(20) DEFAULT NULL COMMENT '反核销对应ID' AFTER source_flag;
alter table collection_record_info
    add column `cancel_write_off_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '反核销标识 0正常，1反核销' AFTER source_flag;
alter table payment_actual_detail
    add column `cancel_write_off_id` bigint(20) DEFAULT NULL COMMENT '反核销对应ID' AFTER write_off_status;
alter table payment_actual_detail
    add column `cancel_write_off_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '反核销标识 0正常，1反核销' AFTER write_off_status;

-- 索引
alter table `collection_record_info`
    add index `idx_collection_id` (`collection_id`);


CREATE TABLE `flow_node_time`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `review_id`   bigint(20) DEFAULT NULL COMMENT '项目评审id',
    `time_json`   json     DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`   bigint(20) DEFAULT NULL,
    `update_by`   bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY           `flow_node_time_review_id_index` (`review_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='流程节点审批时间记录表';