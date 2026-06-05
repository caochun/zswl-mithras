create table if not exists collection_base_info
(
    id                          bigint auto_increment comment '收款明细id	'
    primary key,
    contract_id                 bigint                                not null comment '合同id',
    contract_code               varchar(100)                          null comment '合同编号',
    client_id                   bigint                                null comment '客户id',
    code                        varchar(100)                          null comment '收款编号',
    write_off_status            varchar(20)                           null comment '核销状态',
    collection_date             datetime                              null comment '实收日期',
    collection_amount           bigint                                null comment '实收金额',
    create_by                   bigint                                null,
    create_time                 datetime    default CURRENT_TIMESTAMP null,
    update_by                   bigint                                null,
    update_time                 datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    penalty_interest            bigint                                null comment '罚息',
    payment_id                  bigint                                null comment '付款id',
    payment_code                varchar(100)                          null comment '付款code',
    phase                       int                                   null comment '期项',
    plan_collection_amount      varchar(100)                          null comment '计划收款金额',
    plan_collection_date        datetime                              null comment '计划收款日期',
    principal                   bigint                                null comment '本金',
    interest                    bigint                                null comment '利息',
    cash_flow_amount            bigint                                null comment '现金流金额',
    collection_principal        bigint                                null comment '实收本金',
    collection_interest         bigint                                null comment '实收利息',
    collection_penalty_interest bigint                                null comment '实收罚息',
    cash_flow_item              varchar(100)                          null comment '现金流项目',
    rent_actual_id              bigint                                null comment '实际租金表id',
    write_off_user_ids          varchar(100)                          null comment 'json数据 核销人员ids',
    comment                     varchar(100)                          null comment '罚息修改备注',
    penalty_interest_amount     bigint                                null comment '罚息金额',
    penalty_interest_update     varchar(10) default '0'               null comment '0 无修改，1修改过',
    all_record_sort             int         default 0                 null comment '核销记录序号最大值'
    )
    comment '收款明细表';

create table if not exists collection_overdue_record_info
(
    id                    bigint auto_increment comment '罚息记录明细id	'
    primary key,
    collection_id         bigint                             null comment '收款id',
    overdue_amount        bigint                             null comment '逾期金额',
    day_penalty_interest  bigint                             null comment '单日产生罚息',
    last_penalty_interest bigint                             null comment '罚息余额',
    create_by             bigint                             null,
    create_time           datetime default CURRENT_TIMESTAMP null,
    update_by             bigint                             null,
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    record_date           datetime                           null comment '记录日期'
)
    comment '逾期表';

create table if not exists collection_record_info
(
    id                bigint auto_increment comment '收款记录明细id	'
    primary key,
    collection_id     bigint                             null comment '收款核销明细id',
    data_source       varchar(100)                       null comment '信息来源',
    collection_type   varchar(20)                        null comment '收款类型',
    collection_date   datetime                           null comment '实收日期',
    collection_amount bigint                             null comment '实收金额',
    principal         bigint                             null comment '本金',
    interest          bigint                             null comment '利息',
    penalty_interest  bigint                             null comment '罚息',
    postscript        varchar(100)                       null comment '附言',
    enclosure_id      bigint                             null comment '附件id',
    enclosure_name    varchar(100)                       null comment '附件名',
    write_off_status  varchar(100)                       null comment '核销状态',
    client_id         bigint                             null comment '客户id',
    bank_account_id   bigint                             null comment '我方账户id',
    sort_id           int                                null comment '序号',
    source_flag       int      default 0                 null comment '来源标记',
    create_by         bigint                             null,
    create_time       datetime default CURRENT_TIMESTAMP null,
    update_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '收款记录明细表';

create table if not exists collection_write_off_record
(
    id             bigint auto_increment comment '核销记录id'
    primary key,
    collection_id  bigint                             null comment '归属id',
    record_id      bigint                             null comment '收款记录id',
    operate        varchar(20)                        not null comment '操作',
    receipt_status varchar(20)                        null comment '单据状态',
    create_by      bigint                             null,
    create_time    datetime default CURRENT_TIMESTAMP null,
    update_by      bigint                             null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    operate_info   varchar(100)                       null comment '被操作明细'
    )
    comment '核销记录表';

