create table if not exists exception_info
(
    id         bigint auto_increment
    primary key,
    biz_info   text         null,
    msg        varchar(255) null comment '错误信息',
    gmt_create datetime     null comment '发生时间'
    );

create table if not exists message_base_info
(
    id           bigint auto_increment
    primary key,
    client_id    varchar(32)   not null comment '客户ID',
    is_read      tinyint(1)    null comment '是否已读 0未读，1已读',
    message_time datetime      null comment '消息时间',
    message_body varchar(1000) null comment '消息体',
    message_type varchar(50)   null comment '消息类型',
    create_by    bigint        null,
    create_time  datetime      null,
    update_by    bigint        null,
    update_time  datetime      null
    )
    comment '系统消息基础表';

create index client_id_type_key
    on message_base_info (client_id, message_type);

create table if not exists zhfk_notice
(
    id         bigint auto_increment
    primary key,
    title      varchar(32)  null comment '标题',
    content    varchar(200) null comment '内容',
    type       smallint     not null comment '1通知，2公告',
    status     smallint     not null comment '0正常，1关闭',
    remark     varchar(200) null comment '备注',
    data_scope json         null comment '数据范围（deptId,roleId,userID)',
    create_by  varchar(32)  null comment '创建用户，发送者',
    gmt_create timestamp    null on update CURRENT_TIMESTAMP comment '创建时间',
    update_by  varchar(32)  null comment '修改用户',
    gmt_update timestamp    null on update CURRENT_TIMESTAMP comment '修改时间',
    deal_user  varchar(32)  null comment '处理用户列表，ALL则为所有用户',
    biz_info   json         null comment '业务模块信息',
    record_id  varchar(20)  null comment 'Redis_stream_offset'
    )
    collate = utf8mb4_bin;

create index type_status
    on zhfk_notice (type, status);

create table if not exists zhfk_notice_relation
(
    id          bigint auto_increment
    primary key,
    message_id  bigint                             null comment '消息ID',
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null
)
    comment '消息通知-消息关联表';

create table if not exists zhfk_task_operation
(
    id         bigint auto_increment
    primary key,
    user_id    varchar(60) not null comment '操作用户',
    notice_id  bigint      not null comment '处理待办id',
    gmt_create timestamp   null on update CURRENT_TIMESTAMP comment '创建时间',
    biz_info   json        null comment '业务信息'
    )
    collate = utf8mb4_bin;

create index user_id
    on zhfk_task_operation (user_id);

alter table zhfk_notice_relation add mithras_id VARCHAR(40) DEFAULT NULL COMMENT  '租赁系统id';

