create table bill_overdue
(
    id                 bigint auto_increment
        primary key,
    seq_no             varchar(32)                        null comment '序号',
    org_code           varchar(64)                        null comment '机构编号',
    org_name           varchar(128)                       null comment '机构名称',
    org_type           varchar(32)                        null comment '机构类型 企业/金融机构',
    overdue_start_date varchar(32)                        null comment '持续逾期开始日期 yyyy-mm-dd',
    busi_date          varchar(32)                        null comment '业务日期  yyyy-MM-dd',
    create_by          varchar(32)                        null,
    update_by          varchar(32)                        null,
    create_time        datetime default CURRENT_TIMESTAMP null,
    update_time        datetime default CURRENT_TIMESTAMP null
)
    comment '票据逾期表';