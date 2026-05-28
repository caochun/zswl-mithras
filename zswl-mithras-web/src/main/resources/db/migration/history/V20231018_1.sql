delete
from flow_task_duration;
drop table flow_node_time;
CREATE TABLE `flow_proj_node_time`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `establish_id`   bigint(20) DEFAULT NULL,
    `establish_type` int(11) DEFAULT NULL,
    `review_id`      bigint(20) DEFAULT NULL COMMENT '项目评审id',
    `time_json`      json     DEFAULT NULL,
    `create_time`    datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time`    datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`      bigint(20) DEFAULT NULL,
    `update_by`      bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY              `flow_proj_node_time_review_id_index` (`review_id`),
    KEY              `flow_proj_node_time_establish_id_establish_type_index` (`establish_id`,`establish_type`)
) ENGINE=InnoDB AUTO_INCREMENT=292 DEFAULT CHARSET=utf8mb4 COMMENT='流程节点审批时间记录表';
CREATE TABLE `flow_contract_node_time`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `review_id`   bigint(20) DEFAULT NULL COMMENT '项目评审id',
    `contract_id` bigint(20) DEFAULT NULL,
    `time_json`   json     DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`   bigint(20) DEFAULT NULL,
    `update_by`   bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY           `flow_contract_node_time_contract_id_index` (`contract_id`)
) ENGINE=InnoDB AUTO_INCREMENT=380 DEFAULT CHARSET=utf8mb4 COMMENT='流程节点审批时间记录表';