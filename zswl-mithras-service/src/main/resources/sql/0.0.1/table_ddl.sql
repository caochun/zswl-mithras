create table if not exists admin_dictionary
(
    id           bigint unsigned auto_increment comment 'id'
    primary key,
    uuid         char(32)                              not null comment 'uuid',
    mykey        varchar(64)                           null comment '字典key',
    myvalue      text                                  null comment '字典值',
    `groups`     varchar(64) default '["system"]'      null comment '字典分组编号：system系统；auditForm，审批表单',
    created_by   varchar(36)                           not null comment '创建人',
    updated_by   varchar(36)                           not null comment '修改人',
    gmt_create   timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    description  varchar(500)                          null comment '描述信息',
    default_flag tinyint(1)                            null comment '是否默认，true, false',
    constraint uk_mykey
    unique (mykey),
    constraint uk_uuid
    unique (uuid)
    )
    comment '字典配置' charset = utf8;

create table if not exists future_log
(
    id         bigint auto_increment
    primary key,
    content    varchar(1000)                       null,
    model      varchar(60)                         null,
    oper_type  varchar(60)                         null,
    create_by  varchar(255)                        not null,
    gmt_create timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
    );

