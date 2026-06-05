create table if not exists payment_actual_detail
(
    id                      bigint auto_increment comment '主键id'
    primary key,
    payment_id              bigint                             null comment '所属支付申请id',
    seq_code                varchar(20)                        null comment '序号',
    contract_id             bigint                             null comment '所属合同id',
    info_source             varchar(50)                        null comment '同步还是录入，显示‘财务系统’或者录入者名字',
    payment_method          varchar(50)                        null comment '付款方式',
    paid_in_date            datetime                           null comment '实付日期',
    paid_in_amount          bigint                             null comment '实付金额',
    postscript              varchar(1024)                      null comment '附言',
    our_account_id          bigint                             null comment '我方账户',
    our_account_number      varchar(255)                       null comment '我方账号',
    our_account_name        varchar(255)                       null comment '我方账号名',
    our_account_bank        varchar(255)                       null comment '我方账号开户行',
    opposite_account_id     bigint                             null comment '对方账户',
    opposite_account_number varchar(255)                       null comment '对方账号',
    opposite_account_name   varchar(255)                       null comment '对方账号名',
    opposite_account_bank   varchar(255)                       null comment '对方账号开户行',
    write_off_status        varchar(20)                        null comment '核销状态',
    create_by               bigint                             null comment '创建人、发起人',
    create_time             datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by               bigint                             null comment '最后更新人id',
    update_time             datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    )
    comment '实际付款记录表';

create table if not exists payment_base_info
(
    id                          bigint auto_increment comment '主键id'
    primary key,
    payment_code                varchar(50)                        null comment '付款申请编号',
    contract_id                 bigint                             null comment '对应合同id',
    contract_code               varchar(50)                        null comment '合同编号',
    client_id                   bigint(50)                         null comment '客户id',
    payables                    varchar(255)                       null comment '应付款项',
    apply_payment_date          datetime                           null comment '申请付款日期',
    apply_payment_amount        bigint                             null comment '申请付款金额',
    earnest_money               bigint                             null comment '保证金',
    down_payment                bigint                             null comment '首付款',
    consulting_fee              bigint                             null comment '服务费/咨询费',
    nominal_price               bigint                             null comment '名义货价',
    payment_process_status      varchar(30)                        null comment '审批状态',
    payment_status              varchar(30)                        null comment '申请状态',
    write_off_status            varchar(255)                       null comment '付款申请核销状态',
    write_off_user_ids          varchar(255)                       null comment '核销人员id，json',
    paid_in_date                datetime                           null comment '最新实付日期',
    actual_detail_count         int                                null comment '付款记录个数，用于生成记录序号',
    create_by                   bigint                             null comment '创建人、发起人',
    create_time                 datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                   bigint                             null comment '最后更新人id',
    update_time                 datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    con_project_type            varchar(50)                        null comment '项目类型',
    con_apply_credit_amount     bigint                             null comment '合同带入申请授信金额',
    con_biz_dept_id             bigint                             null comment '合同带入业务部门id',
    con_biz_dept_leader_id      bigint                             null comment '合同带入业务部门领导id',
    con_biz_division_leader_id  bigint                             null comment '合同带入业务分管领导id',
    con_risk_control_manager_id bigint                             null comment '合同带入风控经理id'
    );

create table if not exists payment_base_info_lib
(
    id                          bigint auto_increment comment '主键id'
    primary key,
    payment_code                varchar(50)                        null comment '付款申请编号',
    contract_id                 bigint                             null comment '对应合同id',
    contract_code               varchar(50)                        null comment '合同编号',
    client_id                   bigint(50)                         null comment '客户id',
    payables                    varchar(255)                       null comment '应付款项',
    apply_payment_date          datetime                           null comment '申请付款日期',
    apply_payment_amount        bigint                             null comment '申请付款金额',
    earnest_money               bigint                             null comment '保证金',
    down_payment                bigint                             null comment '首付款',
    consulting_fee              bigint                             null comment '服务费/咨询费',
    nominal_price               bigint                             null comment '名义货价',
    payment_process_status      varchar(30)                        null comment '审批状态',
    payment_status              varchar(30)                        null comment '申请状态',
    write_off_status            varchar(255)                       null comment '付款申请核销状态',
    write_off_user_ids          varchar(255)                       null comment '核销人员id，json',
    paid_in_date                datetime                           null comment '最新实付日期',
    actual_detail_count         int                                null comment '付款记录个数，用于生成记录序号',
    create_by                   bigint                             null comment '创建人、发起人',
    create_time                 datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                   bigint                             null comment '最后更新人id',
    update_time                 datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    con_project_type            varchar(50)                        null comment '项目类型',
    con_apply_credit_amount     bigint                             null comment '合同带入申请授信金额',
    con_biz_dept_id             bigint                             null comment '合同带入业务部门id',
    con_biz_dept_leader_id      bigint                             null comment '合同带入业务部门领导id',
    con_biz_division_leader_id  bigint                             null comment '合同带入业务分管领导id',
    con_risk_control_manager_id bigint                             null comment '合同带入风控经理id',
    version                     varchar(40)                        not null comment '版本号',
    origin_id                   bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time            datetime                           null,
    data_create_by              bigint                             null,
    data_update_time            datetime                           null,
    data_update_by              bigint                             null
    );

create table if not exists payment_planed_detail
(
    id                    bigint auto_increment comment '主键id'
    primary key,
    payment_id            bigint                             null comment '所属支付id',
    payee_client_id       bigint                             null comment '收款方客户ID',
    payee_client_name     varchar(255)                       null comment '收款方客户名称',
    opposite_account      varchar(50)                        null comment '对方账号',
    opposite_account_name varchar(50)                        null comment '对方账号名',
    opposite_account_bank varchar(255)                       null comment '对方账号开户行',
    payment_method        varchar(50)                        null comment '支付方式',
    payment_amount        bigint                             null comment '支付金额',
    postscript            varchar(1024)                      null comment '附言',
    create_by             bigint                             null comment '创建人、发起人id',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by             bigint                             null comment '最后更新人id',
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    )
    comment '计划付款明细表（付款申请 1:n付款明细）';

create table if not exists payment_planed_detail_lib
(
    id                    bigint auto_increment comment '主键id'
    primary key,
    payment_id            bigint                             null comment '所属支付id',
    payee_client_id       bigint                             null comment '收款方客户ID',
    payee_client_name     varchar(255)                       null comment '收款方客户名称',
    opposite_account      varchar(50)                        null comment '对方账号',
    opposite_account_name varchar(50)                        null comment '对方账号名',
    opposite_account_bank varchar(255)                       null comment '对方账号开户行',
    payment_method        varchar(50)                        null comment '支付方式',
    payment_amount        bigint                             null comment '支付金额',
    postscript            varchar(1024)                      null comment '附言',
    create_by             bigint                             null comment '创建人、发起人id',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by             bigint                             null comment '最后更新人id',
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    version               varchar(40)                        not null comment '版本号',
    origin_id             bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time      datetime                           null,
    data_create_by        bigint                             null,
    data_update_time      datetime                           null,
    data_update_by        bigint                             null
    )
    comment '计划付款明细表（付款申请 1:n付款明细）';

create table if not exists payment_policy_info
(
    id                   bigint auto_increment comment '主键id'
    primary key,
    payment_id           bigint                             null,
    policy_code          varchar(50)                        null comment '保单编号',
    policy_amount        bigint                             null comment '保单金额',
    insurance_start_date datetime                           null comment '保险起始日',
    insurance_end_date   datetime                           null comment '保险到期日',
    insurance_company    varchar(255)                       null comment '保险公司名称',
    create_by            bigint                             null comment '创建人、发起人',
    create_time          datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by            bigint                             null comment '最后更新人id',
    update_time          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    );

create table if not exists payment_questionnaire
(
    id            bigint                             not null comment '主键id'
    primary key,
    seq_code      varchar(20)                        null comment '序号',
    question_type varchar(255)                       null comment '问题分类',
    question      varchar(512)                       null comment '问题',
    create_by     bigint                             null,
    create_time   datetime default CURRENT_TIMESTAMP null,
    update_by     bigint                             null,
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version       varchar(255)                       null comment '版本'
    );

create table if not exists payment_questionnaire_answer
(
    id              bigint auto_increment comment '主键'
    primary key,
    payment_id      bigint                             null comment '所属付款id',
    question_id     bigint                             null comment '问题id',
    question_answer varchar(255)                       null comment '问题答案',
    remarks         varchar(1024)                      null comment '备注',
    create_by       bigint                             null,
    create_time     datetime default CURRENT_TIMESTAMP null,
    update_by       bigint                             null,
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    );

create table if not exists payment_questionnaire_answer_lib
(
    id               bigint auto_increment comment '主键'
    primary key,
    payment_id       bigint                             null comment '所属付款id',
    question_id      bigint                             null comment '问题id',
    question_answer  varchar(255)                       null comment '问题答案',
    remarks          varchar(1024)                      null comment '备注',
    create_by        bigint                             null,
    create_time      datetime default CURRENT_TIMESTAMP null,
    update_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version          varchar(40)                        not null comment '版本号',
    origin_id        bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    );

create table if not exists payment_write_off_history
(
    id                  bigint auto_increment comment '主键'
    primary key,
    paymentId           bigint                             null comment '所属付款申请id',
    operate_time        datetime                           null comment '操作时间',
    operate_person_id   bigint                             null comment '操作人id',
    operate_person_name varchar(255)                       null comment '操作人姓名',
    operate_data_type   varchar(20)                        null comment '操作数据类型 （记录还是申请）',
    operate_data_code   varchar(20)                        null comment '被操作数据序号（记录是递增的数字，申请是paymentcode）',
    operation           varchar(20)                        null comment '操作',
    data_status         varchar(20)                        null comment '单据状态',
    create_by           bigint                             null comment '创建人、发起人',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                             null comment '最后更新人id',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    );

