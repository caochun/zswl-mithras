create table if not exists margin_base_info
(
    id                 bigint auto_increment comment '保证金id	'
    primary key,
    margin_code        varchar(100)                       null comment '编号',
    contract_id        bigint                             not null comment '合同id',
    contract_code      varchar(100)                       null comment '合同编号',
    client_id          bigint                             null comment '客户id',
    collection_date    datetime                           null comment '收款日期',
    collection_amount  bigint                             null comment '保证金余额',
    back_amount        bigint                             null comment '已退金额',
    deduct_amount      bigint                             null comment '已抵扣金额',
    create_by          bigint                             null,
    create_time        datetime default CURRENT_TIMESTAMP null,
    update_by          bigint                             null,
    update_time        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    plan_margin_amount bigint                             null comment '计划收款金额',
    plan_margin_date   datetime                           null comment '计划收款日期',
    contract_is_settle int      default 0                 null comment '合同是否结清，0 否，1 是'
    )
    comment '保证金明细表';

create table if not exists margin_record_info
(
    id                      bigint auto_increment comment '保证金核销记录明细id	'
    primary key,
    margin_id               bigint                             null comment '保证金明细id',
    data_source             varchar(100)                       null comment '信息来源',
    collection_type         varchar(20)                        null comment '收款类型',
    record_type             varchar(20)                        null comment '记录类型：退款 or 收款',
    collection_date         datetime                           null comment '实收or付日期',
    collection_amount       bigint                             null comment '实收or付金额',
    deduct_principal        bigint                             null comment '抵扣本金',
    deduct_interest         bigint                             null comment '抵扣利息',
    deduct_penalty_interest bigint                             null comment '抵扣罚息',
    deduct_rent             bigint                             null comment '抵扣租金',
    deduct_term             int                                null comment '抵扣期项',
    collection_id           bigint                             null comment '收款核销id',
    postscript              varchar(100)                       null comment '附言',
    enclosure_id            bigint                             null comment '附件id',
    enclosure_name          varchar(100)                       null comment '附件名',
    write_off_status        varchar(100)                       null comment '核销状态',
    bank_account_id         bigint                             null comment '我方账户id',
    client_id               bigint                             null comment '客户id',
    other_bank_account_id   bigint                             null comment '对方账户id',
    other_client_id         bigint                             null comment '对方客户id',
    sort_id                 int                                null comment '序号',
    write_off               varchar(100)                       null comment '核销',
    review                  varchar(100)                       null comment '复核',
    create_by               bigint                             null,
    create_time             datetime default CURRENT_TIMESTAMP null,
    update_by               bigint                             null,
    update_time             datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    source_flag             int                                null comment '来源标志',
    write_off_user          bigint                             null comment '核销人',
    review_user             bigint                             null comment '复核人'
    )
    comment '保证金核销记录明细表';

create table if not exists margin_write_off_record
(
    id            bigint auto_increment comment '核销记录id'
    primary key,
    margin_id     bigint                             null comment '归属id',
    record_id     bigint                             null comment '收款记录id',
    operate       varchar(20)                        not null comment '操作',
    operate_info  varchar(100)                       null comment '被操作明细',
    margin_amount varchar(20)                        null comment '保证金金额',
    create_by     bigint                             null,
    create_time   datetime default CURRENT_TIMESTAMP null,
    update_by     bigint                             null,
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '核销记录表';

