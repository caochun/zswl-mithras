CREATE TABLE `base_amount_setting`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `begin_cashflow_amount` bigint(20) DEFAULT NULL COMMENT '期初现金流余额',
    `other_income`          bigint(20) DEFAULT NULL COMMENT '其他收入',
    `other_expenses`        bigint(20) DEFAULT NULL COMMENT '其他支出',
    `create_by`             bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`           datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`             bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time`           datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `financing_deliver_detail_setting`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `amount`      bigint(20) DEFAULT NULL COMMENT '融资/投放金额',
    `date`        datetime    DEFAULT NULL COMMENT '融资/投放日期',
    `type`        INT(11) DEFAULT NULL COMMENT '0 融资/ 1 投放',
    `remark`      VARCHAR(25) DEFAULT NULL COMMENT '备注',
    `create_by`   bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   bigint(20) DEFAULT NULL COMMENT '最后更新人id',
    `update_time` datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;