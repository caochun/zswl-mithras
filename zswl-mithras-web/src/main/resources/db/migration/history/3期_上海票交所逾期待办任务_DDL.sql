CREATE TABLE `bill_overdue_draft` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                      `seq_no` varchar(32) DEFAULT NULL COMMENT '序号',
                                      `org_code` varchar(64) DEFAULT NULL COMMENT '机构编号',
                                      `org_name` varchar(128) DEFAULT NULL COMMENT '机构名称',
                                      `org_type` varchar(32) DEFAULT NULL COMMENT '机构类型 企业/金融机构',
                                      `overdue_start_date` varchar(32) DEFAULT NULL COMMENT '持续逾期开始日期 yyyy-mm-dd',
                                      `busi_date` varchar(32) DEFAULT NULL COMMENT '业务日期  yyyy-MM-dd',
                                      `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
                                      `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='票据逾期表-草稿';