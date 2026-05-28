alter table visit_record add column contract_id bigint(20) default null comment '合同id' after proj_code;
alter table visit_record add column contract_code varchar(50) default null comment '合同编号' after contract_id;

CREATE TABLE `visit_download_task_record` (
                                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                              `user_id` bigint(20) NOT NULL COMMENT '用户id',
                                              `req_params` text COMMENT '请求参数',
                                              `task_status` varchar(20) NOT NULL COMMENT '任务状态',
                                              `finish_time` datetime DEFAULT NULL COMMENT '任务结束时间',
                                              `file_path` varchar(200) NOT NULL COMMENT '文件路径',
                                              `file_name` varchar(50) NOT NULL COMMENT '文件名称',
                                              `remark` text COMMENT '备注',
                                              `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='拜访文件下载任务记录';