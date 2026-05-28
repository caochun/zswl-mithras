CREATE TABLE `user_custom_config` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                      `user_id` bigint(20) NOT NULL COMMENT '用户id',
                                      `config_key` varchar(100) NOT NULL COMMENT '配置key',
                                      `config_value` text COMMENT '配置value',
                                      `metadata_type` varchar(100) DEFAULT NULL COMMENT '元数据类型',
                                      `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_userid_configkey` (`user_id`,`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户自定义配置表';

CREATE TABLE `fund_financial_system_call_record`
(
    `id`                bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'Id',
    `date`              datetime     NOT NULL COMMENT '数据时点',
    `batch_number`      varchar(255) NOT NULL COMMENT '批次号',
    `status`            varchar(255) NOT NULL COMMENT '状态',
    `type`              varchar(255) NOT NULL COMMENT '推送类型',
    `count`             int(10)      NOT NULL COMMENT '推送数量',
    `query`             json                  DEFAULT NULL COMMENT '请求参数',
    `result`            json                  DEFAULT NULL COMMENT '返回体',
    `system_error_info` json                  DEFAULT NULL COMMENT '系统内校验的错误信息',
    `create_by`         bigint(20)            DEFAULT NULL COMMENT '创建人、发起人',
    `create_time`       datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`         bigint(20)            DEFAULT NULL COMMENT '最后更新人id',
    `update_time`       datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           tinyint(4)   NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT='保融推送记录表';

create table mithras_uat.kpi_project_distribution_dept_weight
(
    id                      bigint auto_increment comment '主键ID'
        primary key,
    project_distribution_id bigint                               not null comment '项目分配表ID',
    weight_type             varchar(50)                          null comment '分配比重类型',
    weight_target           varchar(255)                         null comment '分配比重归属目标',
    wight_value             int                                  null comment '分配比重数值',
    create_by               bigint                               null comment '创建人',
    update_by               bigint                               null comment '更新人',
    create_time             datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time             datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted                 tinyint(1) default 0                 not null comment '逻辑删除标记(0:正常 1:删除)'
)
    comment '绩效考核-部门-项目分配比重表' collate = utf8mb4_unicode_ci;

create index idx_project_distribution
    on mithras_uat.kpi_project_distribution_dept_weight (project_distribution_id);

create table mithras_uat.kpi_project_distribution_dept_weight_lib
(
    id                      bigint auto_increment comment '主键ID'
        primary key,
    project_distribution_id bigint                               not null comment '项目分配表ID',
    weight_type             varchar(50)                          null comment '分配比重类型',
    weight_target           varchar(255)                         null comment '分配比重归属目标',
    wight_value             int                                  null comment '分配比重数值',
    create_by               bigint                               null comment '创建人',
    update_by               bigint                               null comment '更新人',
    create_time             datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time             datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted                 tinyint(1) default 0                 not null comment '逻辑删除标记(0:正常 1:删除)',
    version                 varchar(40)                          not null comment '版本号',
    origin_id               bigint                               not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time        datetime                             null comment '原始数据创建时间',
    data_create_by          bigint                               null comment '原始数据创建人id',
    data_update_time        datetime                             null comment '原始数据更新时间',
    data_update_by          bigint                               null comment '原始数据更新人id',
    version_type            tinyint    default 1                 null comment '版本标志，0无效，1有效...业务自扩展'
)
    comment '绩效考核-部门-项目分配比重版本表' collate = utf8mb4_unicode_ci;

create index idx_project_distribution
    on mithras_uat.kpi_project_distribution_dept_weight_lib (project_distribution_id);

