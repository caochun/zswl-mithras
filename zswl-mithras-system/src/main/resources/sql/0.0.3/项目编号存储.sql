-- 项目编号拆表 因为项目评审、项目立项都要生成项目编号了，项目编号又需要唯一，所以需要单独拎出来存
CREATE TABLE `proj_code_store` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `proj_code` varchar(20) DEFAULT NULL COMMENT '项目编号',
    `biz_type` varchar(20) DEFAULT NULL COMMENT '业务类型。租赁、保理、转租赁',
    `type_seq_id` bigint(20) DEFAULT NULL COMMENT '不同类型，不同的自增序列id',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_proj_code` (`proj_code`),
    UNIQUE KEY `seqid_unique` (`biz_type`,`type_seq_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1208 DEFAULT CHARSET=utf8mb4 COMMENT='项目编号单存表';