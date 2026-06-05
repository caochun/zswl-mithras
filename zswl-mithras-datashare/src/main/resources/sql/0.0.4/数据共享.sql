CREATE TABLE `data_share_code_dict`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `phone`  varchar(30) DEFAULT NULL COMMENT '手机号',
    `user_id`   varchar(30) DEFAULT NULL COMMENT '主数据编号',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='数据分享-主数据编码映射';