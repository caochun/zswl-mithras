CREATE TABLE `risk_control_strategy`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `metric_code`             varchar(50)        DEFAULT NULL COMMENT '指标编号',
    `metric_type`             varchar(30)        DEFAULT NULL COMMENT '指标类型',
    `metric_category`         varchar(20)        DEFAULT NULL COMMENT '指标类别',
    `metric_name`             varchar(50)        DEFAULT NULL COMMENT '指标名称',
    `computational_logic`     varchar(500)       DEFAULT NULL COMMENT '计算逻辑',
    `early_warning_value_one` int(11) DEFAULT NULL COMMENT '预警值',
    `comparison_method_one`   varchar(20)        DEFAULT NULL COMMENT '比较方式1',
    `value_unit_one`          varchar(20)        DEFAULT NULL COMMENT '单位',
    `current_value_one`       bigint(20) DEFAULT NULL COMMENT '当前值',
    `early_warning_value_two` int(11) DEFAULT NULL COMMENT '预警值',
    `comparison_method_two`   varchar(20)        DEFAULT NULL COMMENT '比较方式1',
    `value_unit_two`          varchar(255)       DEFAULT NULL COMMENT '单位',
    `current_value_two`       bigint(20) DEFAULT NULL COMMENT '当前值',
    `remaining_principal`     bigint(20) DEFAULT NULL COMMENT '指标计算中的剩余本金',
    `quick_context`           varchar(2000)      DEFAULT NULL COMMENT '速算上下文',
    `remark`                  varchar(255)       DEFAULT NULL COMMENT '通用字段',
    `null_reason`             varchar(255)       DEFAULT NULL COMMENT '当前值为空的原因',
    `create_time`             timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time`             timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`               bigint(20) DEFAULT NULL COMMENT '创建人',
    `update_by`               bigint(20) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO bifrost_system_config (config_key, config_value, created_by, updated_by,
                                   description, status, type)
VALUES ('riskManagerIdsOrderByDeptId',
        '{"9":[97,100],"37":[97,100],"27":[99,102],"31":[253,98],"34":[61,253],"35":[101,138],"6":[98,102],"11":[138,99],"12":[101,61]}',
        'admin', 'admin', '业务部门对应风控经理', 1, 'Json');
