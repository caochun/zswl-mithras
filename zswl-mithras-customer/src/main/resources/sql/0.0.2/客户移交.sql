-- 客户增加两个字段
alter table client
    add belong_dept_id bigint null comment '所属部门id';
alter table client
    add belong_sponsor_id bigint null comment '所属主办id';
-- 历史数据订正
update client set belong_dept_id = create_by_dept, belong_sponsor_id = create_by ;


-- 客户移交表
create table client_transfer
(
    id                bigint auto_increment
        primary key,
    batch_no          varchar(50)                        not null comment '批次编号',
    to_dept_id        bigint                             null comment '移至部门id',
    to_sponsor_id     bigint                             null comment '移至用户id',
    client_id         bigint                             null comment '客户id',
    client_name       varchar(100)                       null comment '客户名称',
    client_code       varchar(50)                        null comment '客户编码',
    client_type       varchar(20)                        null comment '客户类型',
    belong_dept_id    bigint                             null comment '所属部门id',
    belong_sponsor_id bigint                             null comment '所属经理id',
    transfer_status   varchar(20)                        null comment '转交状态',
    transfer_date     date                               null comment '正式移交日期',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null
);







