-- 消息建表
CREATE TABLE `zhfk_notice_data_scope`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `notice_id`  bigint(20)   DEFAULT NULL,
    `scope_type` varchar(20)  DEFAULT NULL COMMENT '数据范围类型：角色、部门、用户',
    `content`    varchar(255) DEFAULT NULL COMMENT '具体范围名称：角色名称、部门名称或userId',
    PRIMARY KEY (`id`),
    KEY `idx_content` (`content`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4;