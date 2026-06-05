create table if not exists client
(
    id               bigint auto_increment
    primary key,
    client_code      varchar(50)                        null comment '客户编号',
    client_name      varchar(50)                        null comment '客户名称',
    client_type      varchar(20)                        null comment '客户分类，法人/自然人',
    client_status    varchar(20)                        null comment '客户状态',
    process_status   varchar(30)                        null comment '流程状态',
    usc_code         varchar(50)                        null comment '统一社会信用代码。Unified Social credit code',
    cert_type        varchar(20)                        null comment '证件类型',
    cert_number      varchar(50)                        null comment '证件号码',
    create_time      datetime default CURRENT_TIMESTAMP not null,
    create_by        bigint                             null comment '创建人id',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    create_by_dept   bigint                             null comment '创建部门',
    hand_import_flag tinyint  default 0                 null comment '汉得导入数据标志（1为导入）',
    newest_version   varchar(32)                        null comment '最新的版本',
    tyc_name         varchar(50)                        null comment '天眼查名称',
    constraint client_cert_number_uindex
    unique (cert_number),
    constraint usc_code_unique
    unique (usc_code)
    )
    comment '客户信息';

create table if not exists client_materials_list
(
    id             bigint auto_increment
    primary key,
    client_id      bigint                             null comment '客户id',
    materials_type varchar(50)                        null comment '资料类型',
    filename       varchar(100)                       null comment '附件名',
    file_path      varchar(200)                       null comment '文件url',
    create_time    datetime default CURRENT_TIMESTAMP null,
    create_by      bigint                             null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by      bigint                             null
    )
    comment '客户资料清单';

create table if not exists client_version
(
    id          bigint auto_increment comment '主键'
    primary key,
    client_id   bigint                                not null comment '客户id',
    version     varchar(32)                           not null comment '版本',
    type        tinyint                               not null comment '版本类型（1直接生效，2审批通过生效）',
    create_time datetime    default CURRENT_TIMESTAMP null,
    create_by   bigint                                null,
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by   bigint                                null,
    module      varchar(32) default 'CLIENT'          not null comment '业务模块枚举'
    )
    comment '客户信息版本表';

create index idx_client_id
    on client_version (client_id);

create table if not exists corp_address_info
(
    id           bigint auto_increment
    primary key,
    client_id    bigint                             not null comment '客户id',
    address_type varchar(20)                        null comment '地址类型',
    country      varchar(20)                        null comment '国家',
    province     varchar(20)                        null comment '省份',
    city         varchar(20)                        null comment '城市',
    district     varchar(20)                        null comment '区、县',
    detail       varchar(200)                       null comment '详细地址',
    region_code  varchar(50)                        null comment '区域代码',
    create_time  datetime default CURRENT_TIMESTAMP null,
    create_by    bigint                             null,
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by    bigint                             null
    )
    comment '法人地址信息';

create table if not exists corp_address_info_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             not null comment '客户id',
    address_type     varchar(20)                        null comment '地址类型',
    country          varchar(20)                        null comment '国家',
    province         varchar(20)                        null comment '省份',
    city             varchar(20)                        null comment '城市',
    district         varchar(20)                        null comment '区、县',
    detail           varchar(200)                       null comment '详细地址',
    region_code      varchar(50)                        null comment '区域代码',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '法人地址信息';

create table if not exists corp_bank_account
(
    id             bigint auto_increment
    primary key,
    client_id      bigint                             null comment '客户id',
    main_account   tinyint(1)                         null comment '是否主账号',
    account_name   varchar(50)                        null comment '账号名称',
    account_number varchar(50)                        null comment '银行账号',
    account_bank   varchar(50)                        null comment '开户行',
    create_time    datetime default CURRENT_TIMESTAMP null,
    create_by      bigint                             null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by      bigint                             null
    )
    comment '法人银行账户';

create table if not exists corp_bank_account_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             null comment '客户id',
    main_account     tinyint(1)                         null comment '是否主账号',
    account_name     varchar(50)                        null comment '账号名称',
    account_number   varchar(50)                        null comment '银行账号',
    account_bank     varchar(50)                        null comment '开户行',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '法人银行账户';

create table if not exists corp_bond_info
(
    id              bigint auto_increment
    primary key,
    client_id       bigint                             null comment '客户id',
    rate_date       date                               null comment '评级日期',
    rate_company    varchar(50)                        null comment '评级公司',
    rate            varchar(20)                        null comment '评级',
    rate_future     varchar(100)                       null comment '评级展望',
    issue_total     bigint                             null comment '发行总额，单位亿元',
    issue_amount    bigint                             null comment '发行只数',
    stock_scale     bigint                             null comment '存量规模，单位：亿元',
    stock_amount    bigint                             null comment '存量只数',
    maturity_scale  bigint                             null comment '到期规模，单位：亿元',
    maturity_amount bigint                             null comment '到期只数',
    create_time     datetime default CURRENT_TIMESTAMP null,
    create_by       bigint                             null,
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by       bigint                             null
    )
    comment '法人股票信息';

create table if not exists corp_bond_info_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             null comment '客户id',
    rate_date        date                               null comment '评级日期',
    rate_company     varchar(50)                        null comment '评级公司',
    rate             varchar(20)                        null comment '评级',
    rate_future      varchar(100)                       null comment '评级展望',
    issue_total      bigint                             null comment '发行总额，单位亿元',
    issue_amount     bigint                             null comment '发行只数',
    stock_scale      bigint                             null comment '存量规模，单位：亿元',
    stock_amount     bigint                             null comment '存量只数',
    maturity_scale   bigint                             null comment '到期规模，单位：亿元',
    maturity_amount  bigint                             null comment '到期只数',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '法人股票信息';

create table if not exists corp_commerce_info
(
    id                     bigint auto_increment
    primary key,
    client_id              bigint                             not null comment '客户id',
    triple_cert_in_one     tinyint(1)                         null comment '三证合一',
    zhong_zheng_code       varchar(50)                        null comment '中征码',
    org_code               varchar(50)                        null comment '组织机构代码',
    biz_license_code       varchar(50)                        null comment '营业执照号',
    continuous_status      varchar(20)                        null comment '存续状态',
    establish_date         date                               null comment '成立日期',
    approval_date          date                               null comment '核准日期',
    biz_licence_long_term  tinyint(1)                         null comment '营业许可证是否为长期',
    biz_license_end_date   date                               null comment '营业许可证到期日(如果许可证是非长期类型)',
    biz_scope              text                               null comment '业务范围',
    industry_type          varchar(50)                        null comment '行业分类',
    economy_type           varchar(50)                        null comment '经济类型',
    org_type               varchar(20)                        null comment '组织机构类型',
    org_scale              varchar(20)                        null comment '企业规模',
    register_currency_type varchar(20)                        null comment '注册币种',
    register_capital       bigint                             null comment '注册资本',
    real_currency_type     varchar(20)                        null comment '实收币种',
    real_capital           bigint                             null comment '实收资本',
    register_capital_rate  bigint                             null comment '注册资本到位率',
    corp_represent         varchar(20)                        null comment '法人代表',
    corp_gender            varchar(20)                        null comment '法人性别',
    corp_cert_type         varchar(20)                        null comment '法人证件类型',
    corp_cert_code         varchar(50)                        null comment '法人证件号码',
    listed_company         tinyint(1)                         null comment '是否上市公司',
    create_time            datetime default CURRENT_TIMESTAMP null,
    create_by              bigint                             null comment '创建人',
    update_time            datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by              bigint                             null,
    client_code            varchar(50)                        null comment '客户编号（冗余）',
    constraint client_id
    unique (client_id),
    constraint org_code
    unique (org_code),
    constraint zhong_zheng_code
    unique (zhong_zheng_code)
    )
    comment '法人工商信息表';

create table if not exists corp_commerce_info_lib
(
    id                     bigint auto_increment
    primary key,
    client_id              bigint                             not null comment '客户id',
    triple_cert_in_one     tinyint(1)                         null comment '三证合一',
    zhong_zheng_code       varchar(50)                        null comment '中征码',
    org_code               varchar(50)                        null comment '组织机构代码',
    biz_license_code       varchar(50)                        null comment '营业执照号',
    continuous_status      varchar(20)                        null comment '存续状态',
    establish_date         date                               null comment '成立日期',
    approval_date          date                               null comment '核准日期',
    biz_licence_long_term  tinyint(1)                         null comment '营业许可证是否为长期',
    biz_license_end_date   date                               null comment '营业许可证到期日(如果许可证是非长期类型)',
    biz_scope              text                               null comment '业务范围',
    industry_type          varchar(50)                        null comment '行业分类',
    economy_type           varchar(50)                        null comment '经济类型',
    org_type               varchar(20)                        null comment '组织机构类型',
    org_scale              varchar(20)                        null comment '企业规模',
    register_currency_type varchar(20)                        null comment '注册币种',
    register_capital       varchar(50)                        null comment '注册资本',
    real_currency_type     varchar(20)                        null comment '实收币种',
    real_capital           varchar(50)                        null comment '实收资本',
    register_capital_rate  varchar(20)                        null comment '注册资本到位率',
    corp_represent         varchar(20)                        null comment '法人代表',
    corp_gender            varchar(20)                        null comment '法人性别',
    corp_cert_type         varchar(20)                        null comment '法人证件类型',
    corp_cert_code         varchar(50)                        null comment '法人证件号码',
    listed_company         tinyint(1)                         null comment '是否上市公司',
    create_time            datetime default CURRENT_TIMESTAMP null,
    create_by              bigint                             null comment '创建人',
    update_time            datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by              bigint                             null,
    origin_id              bigint                             not null comment '草稿表id',
    version                varchar(32)                        not null comment '版本',
    data_create_time       datetime                           null,
    data_create_by         bigint                             null,
    data_update_time       datetime                           null,
    data_update_by         bigint                             null,
    client_code            varchar(50)                        null comment '客户编号（冗余）'
    )
    comment '法人工商信息表';

create table if not exists corp_contact_info
(
    id          bigint auto_increment
    primary key,
    client_id   bigint                             not null comment '客户id',
    main        tinyint(1)                         null comment '是否主联系人',
    position    varchar(20)                        null comment '职务',
    gender      varchar(20)                        null comment '性别',
    name        varchar(50)                        null comment '姓名',
    telephone   varchar(20)                        null comment '电话',
    mail        varchar(50)                        null comment '邮箱',
    cert_type   varchar(20)                        null comment '证件类型',
    cert_number varchar(50)                        null comment '证件号码',
    create_time datetime default CURRENT_TIMESTAMP null,
    create_by   bigint                             null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by   bigint                             null
    )
    comment '法人联系人';

create table if not exists corp_contact_info_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             not null comment '客户id',
    main             tinyint(1)                         null comment '是否主联系人',
    position         varchar(20)                        null comment '职务',
    gender           varchar(20)                        null comment '性别',
    name             varchar(50)                        null comment '姓名',
    telephone        varchar(20)                        null comment '电话',
    mail             varchar(50)                        null comment '邮箱',
    cert_type        varchar(20)                        null comment '证件类型',
    cert_number      varchar(50)                        null comment '证件号码',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '法人联系人';

create table if not exists corp_related_enterprise
(
    id                 bigint auto_increment
    primary key,
    client_id          bigint                             null comment '客户id',
    enterprise_name    varchar(100)                       null comment '关联企业名称',
    relationship       varchar(100)                       null comment '关联关系',
    register_capital   bigint                             null comment '注册资本',
    shareholding_ratio bigint                             null comment '持股比例',
    invest_amount      bigint                             null comment '投资金额（万元）',
    continuous_status  varchar(20)                        null comment '存续状态',
    establish_date     date                               null comment '成立日期',
    industry_type      varchar(20)                        null comment '行业',
    create_time        datetime default CURRENT_TIMESTAMP null,
    create_by          bigint                             null,
    update_time        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by          bigint                             null,
    constraint corp_related_enterprise_enterprise_name_client_id_uindex
    unique (enterprise_name, client_id)
    )
    comment '法人关联企业';

create table if not exists corp_related_enterprise_lib
(
    id                 bigint auto_increment
    primary key,
    client_id          bigint                             null comment '客户id',
    enterprise_name    varchar(100)                       null comment '关联企业名称',
    relationship       varchar(100)                       null comment '关联关系',
    register_capital   bigint                             null comment '注册资本',
    shareholding_ratio bigint                             null comment '持股比例',
    invest_amount      bigint                             null comment '投资金额（万元）',
    create_time        datetime default CURRENT_TIMESTAMP null,
    create_by          bigint                             null,
    update_time        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by          bigint                             null,
    origin_id          bigint                             not null comment '草稿表id',
    version            varchar(32)                        not null comment '版本',
    industry_type      varchar(20)                        null comment '行业',
    establish_date     date                               null comment '成立日期',
    data_create_time   datetime                           null,
    data_create_by     bigint                             null,
    data_update_time   datetime                           null,
    data_update_by     bigint                             null,
    continuous_status  varchar(20)                        null comment '存续状态'
    )
    comment '法人关联企业';

create table if not exists corp_shareholder_info
(
    id                bigint auto_increment
    primary key,
    client_id         bigint                             null comment '客户id',
    shareholder_type  varchar(20)                        null comment '股东类型',
    shareholder_name  varchar(50)                        null comment '股东姓名',
    paid_total        bigint                             null comment '认缴金额（万）',
    actual_paid_total bigint                             null comment '实缴金额',
    capital_way       varchar(20)                        null comment '出资方式',
    capital_percent   bigint                             null comment '出资占比',
    real_controller   tinyint(1)                         null comment '是否实际控制人',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null,
    constraint shareholder_name
    unique (client_id, shareholder_name)
    )
    comment '股东信息';

create table if not exists corp_shareholder_info_lib
(
    id                bigint auto_increment
    primary key,
    client_id         bigint                             null comment '客户id',
    shareholder_type  varchar(20)                        null comment '股东类型',
    shareholder_name  varchar(50)                        null comment '股东姓名',
    paid_total        bigint                             null comment '认缴金额（万）',
    actual_paid_total bigint                             null comment '实缴金额',
    capital_way       varchar(20)                        null comment '出资方式',
    capital_percent   bigint                             null comment '出资占比',
    real_controller   tinyint(1)                         null comment '是否实际控制人',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null,
    origin_id         bigint                             not null comment '草稿表id',
    version           varchar(32)                        not null comment '版本',
    data_create_time  datetime                           null,
    data_create_by    bigint                             null,
    data_update_time  datetime                           null,
    data_update_by    bigint                             null
    )
    comment '股东信息';

create table if not exists corp_subject_item
(
    id            bigint auto_increment
    primary key,
    client_id     bigint                             null comment '客户id',
    report_type   varchar(20)                        null comment '报表类型',
    subject_type  varchar(20)                        null comment '科目类型',
    subject_code  varchar(20)                        null comment '科目代码',
    subject_name  varchar(50)                        null comment '科目名称',
    year          int                                null comment '年度',
    quarter       int                                null comment '季度',
    subject_value bigint                             null comment '值文本',
    create_time   datetime default CURRENT_TIMESTAMP null,
    create_by     bigint                             null,
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by     bigint                             null
    )
    comment '法人公司科目指标表';

create table if not exists environment_penalty
(
    id              bigint auto_increment comment '主键'
    primary key,
    client_id       bigint                             not null comment '客户id',
    penalty_time    datetime                           null comment '处罚日期',
    punish_number   varchar(256)                       null comment '决定文书号',
    reason          varchar(500)                       null comment '处罚事由',
    result          varchar(128)                       null comment '处罚结果',
    amount          bigint                             null comment '处罚金额（元）',
    department_name varchar(128)                       null comment '处罚单位',
    source          varchar(64)                        null comment '数据来源',
    info            varchar(500)                       null comment '执行情况',
    detail_url      varchar(256)                       null comment '详情url',
    create_time     datetime default CURRENT_TIMESTAMP null,
    create_by       bigint                             null,
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by       bigint                             null
    )
    comment '环保处罚';

create index idx_client_id
    on environment_penalty (client_id);

create table if not exists industry_type
(
    id        bigint auto_increment
    primary key,
    code      varchar(20)      null comment '行业编号',
    display   varchar(200)     null comment '行业名称',
    parent_id bigint default 0 null comment '父级行业',
    level     int    default 1 null
    )
    comment '行业类型字典表';

create table if not exists info_history
(
    id               bigint auto_increment
    primary key,
    history_code     varchar(50)                        null comment '变更编号',
    module_code      varchar(20)                        null comment '模块',
    module_record_id bigint                             null comment '模块中对应表记录的id',
    client_id        bigint                             null comment '客户id',
    operation_type   varchar(20)                        null comment '操作类型',
    original_data    text                               null comment '变更前数据',
    current_data     text                               null comment '变更后数据',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    constraint info_history_history_code_uindex
    unique (history_code)
    );

create table if not exists normal_bank_account
(
    id             bigint auto_increment
    primary key,
    account_name   varchar(50)                        null comment '账户名称',
    account_number varchar(50)                        null comment '账号',
    account_bank   varchar(50)                        null comment '开户行',
    main_account   tinyint(1)                         null comment '是否主账号',
    create_time    datetime default CURRENT_TIMESTAMP null,
    create_by      bigint                             null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    client_id      bigint                             null comment '客户id',
    update_by      bigint                             null
    )
    comment '自然人银行账户';

create table if not exists normal_bank_account_lib
(
    id               bigint auto_increment
    primary key,
    account_name     varchar(50)                        null comment '账户名称',
    account_number   varchar(50)                        null comment '账号',
    account_bank     varchar(50)                        null comment '开户行',
    main_account     tinyint(1)                         null comment '是否主账号',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    client_id        bigint                             null comment '客户id',
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '自然人银行账户';

create table if not exists normal_base_info
(
    id            bigint auto_increment
    primary key,
    client_id     bigint                             null comment '客户id',
    cert_type     varchar(20)                        null comment '证件类型',
    cert_number   varchar(50)                        null comment '证件号码',
    gender        varchar(20)                        null comment '性别',
    marriage_type varchar(20)                        null comment '婚姻情况',
    country       varchar(20)                        null comment '国家',
    age           int                                null comment '年龄',
    mobile_number varchar(20)                        null comment '手机号',
    home_address  varchar(100)                       null comment '家庭地址',
    mail          varchar(50)                        null comment '邮箱',
    create_time   datetime default CURRENT_TIMESTAMP null,
    create_by     int                                null,
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by     bigint                             null,
    client_code   varchar(50)                        null comment '客户编号（冗余）'
    )
    comment '自然人基本信息';

create table if not exists normal_base_info_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             null comment '客户id',
    cert_type        varchar(20)                        null comment '证件类型',
    cert_number      varchar(50)                        null comment '证件号码',
    gender           varchar(20)                        null comment '性别',
    marriage_type    varchar(20)                        null comment '婚姻情况',
    country          varchar(20)                        null comment '国家',
    age              int                                null comment '年龄',
    mobile_number    varchar(20)                        null comment '手机号',
    home_address     varchar(100)                       null comment '家庭地址',
    mail             varchar(50)                        null comment '邮箱',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        int                                null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null,
    client_code      varchar(50)                        null comment '客户编号（冗余）'
    )
    comment '自然人基本信息';

create table if not exists normal_spouse
(
    id          bigint auto_increment
    primary key,
    client_id   bigint                             null comment '客户id',
    spouse_name varchar(50)                        null comment '配偶姓名',
    cert_type   varchar(20)                        null comment '证件类型',
    cert_number varchar(50)                        null comment '证件号码',
    create_time datetime default CURRENT_TIMESTAMP null,
    create_by   bigint                             null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by   bigint                             null
    )
    comment '自然人配偶';

create table if not exists normal_spouse_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             null comment '客户id',
    spouse_name      varchar(50)                        null comment '配偶姓名',
    cert_type        varchar(20)                        null comment '证件类型',
    cert_number      varchar(50)                        null comment '证件号码',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '自然人配偶';

create table if not exists tmp_client_project
(
    id          bigint auto_increment comment '主键'
    primary key,
    client_id   bigint                             not null comment '客户id',
    status      tinyint  default 1                 not null comment '为1生效，为0不生效（审批拒绝）',
    create_time datetime default CURRENT_TIMESTAMP null,
    create_by   bigint                             null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by   bigint                             null
)
    comment '用于模拟客户立项校验';

create index idx_client_id
    on tmp_client_project (client_id);

create table if not exists tyc_abnormal
(
    id                bigint auto_increment comment '主键'
    primary key,
    client_id         bigint                             not null comment '客户id',
    remove_date       varchar(30)                        null comment '移出日期',
    put_reason        varchar(4091)                      null comment '列入异常名录原因',
    put_department    varchar(200)                       null comment '决定列⼊异常名录部⻔(作出决定机关)',
    remove_department varchar(200)                       null comment '移出部⻔',
    remove_reason     varchar(4091)                      null comment '移除异常名录原因',
    put_date          varchar(30)                        null comment '列入日期',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null
    )
    comment '天眼查_经营异常';

create index idx_client_id
    on tyc_abnormal (client_id);

create table if not exists tyc_consumption_restriction
(
    id               bigint auto_increment comment '主键'
    primary key,
    client_id        bigint                             not null comment '客户id',
    case_code        varchar(50)                        null comment '案号',
    file_path        varchar(150)                       null comment 'pdf文件地址',
    publish_date     datetime                           null comment '发布日期',
    xname            varchar(60)                        null comment '限制消费者名称',
    hcgid            varchar(50)                        null comment '限制消费者id',
    applicant        varchar(255)                       null comment '申请人信息',
    applicant_cid    varchar(255)                       null comment '申请人id',
    qyinfo_alias     varchar(100)                       null comment '企业信息',
    case_create_time datetime                           null comment '立案时间',
    alias            varchar(100)                       null comment '别名',
    tyc_id           bigint                             null comment '天眼查id',
    cid              bigint                             null comment '企业id',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null
    )
    comment '天眼查_限制消费令';

create index idx_client_id
    on tyc_consumption_restriction (client_id);

create table if not exists tyc_dishonest
(
    id                bigint auto_increment comment '主键'
    primary key,
    client_id         bigint                             not null comment '客户id',
    business_entity   varchar(60)                        null comment '法人、负责人姓名',
    area_name         varchar(30)                        null comment '省份地区',
    court_name        varchar(50)                        null comment '法院',
    unperform_part    longtext                           null comment '未履行部分',
    staff_json        varchar(512)                       null comment '法定负责人/主要负责人信息',
    type              varchar(2)                         null comment '失信⼈类型，0代表⼈，1代表公司',
    performed_part    longtext                           null comment '已履行部分',
    iname             varchar(60)                        null comment '失信人名称',
    disrupt_type_name varchar(2000)                      null comment '失信被执行人行为具体情形',
    case_code         varchar(50)                        null comment '案号',
    card_num          varchar(30)                        null comment '身份证号码/组织机构代码',
    performance       varchar(60)                        null comment '履行情况',
    reg_date          datetime                           null comment '立案时间',
    publish_date      datetime                           null comment '发布时间',
    gist_unit         varchar(60)                        null comment '做出执行的依据单位',
    duty              longtext                           null comment '生效法律文书确定的义务',
    gist_id           varchar(100)                       null comment '执行依据文号',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null
    )
    comment '天眼查_失信人';

create index idx_client_id
    on tyc_dishonest (client_id);

create table if not exists tyc_equity_info
(
    id                  bigint auto_increment comment '主键'
    primary key,
    client_id           bigint                             not null comment '客户id',
    pledgee_json        longtext                           null comment '质权人列表',
    reg_date            datetime                           null comment '股权出质设立登记日期',
    pledgor             varchar(255)                       null comment '出质人',
    certif_number_r     varchar(20)                        null comment '质权人证照/证件号码',
    pledgee             varchar(255)                       null comment '质权人',
    reg_number          varchar(50)                        null comment '登记编号',
    certif_number       varchar(20)                        null comment '出质人证照/证件号码',
    company_json        longtext                           null comment '公司列表',
    target_company_json varchar(350)                       null comment '出质股权标的企业',
    pledgor_json        longtext                           null comment '出质人列表',
    equity_amount       varchar(20)                        null comment '出质股权数额',
    tyc_id              bigint                             null comment '天眼查id',
    state               varchar(31)                        null comment '状态',
    put_date            datetime                           null comment '股权出质设立发布日期',
    create_time         datetime default CURRENT_TIMESTAMP null,
    create_by           bigint                             null,
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by           bigint                             null
    )
    comment '天眼查_股权出质';

create index idx_client_id
    on tyc_equity_info (client_id);

create table if not exists tyc_judicial
(
    id                     bigint auto_increment comment '主键'
    primary key,
    client_id              bigint                             not null comment '客户id',
    execute_notice_num     varchar(200)                       null comment '执行通知书文号',
    executed_person_cid    bigint                             null comment '执行人公司id',
    publicity_date         varchar(30)                        null comment '公示日期',
    stock_executed_company varchar(255)                       null comment '股权被执行的企业',
    executed_person_hid    bigint                             null comment '被执行人hgid',
    stock_executed_cid     bigint                             null comment '股权被执行的企业id',
    executed_person        varchar(50)                        null comment '被执行人',
    ass_id                 varchar(100)                       null comment '司法协助基本信息id',
    equity_amount          varchar(50)                        null comment '股权数额',
    tyc_id                 bigint                             null comment '天眼查id',
    type_state             varchar(50)                        null comment '类型',
    executed_person_type   varchar(50)                        null comment '执⾏⼈类型，2-⼈，1-公司',
    executive_court        varchar(100)                       null comment '执行法院',
    create_time            datetime default CURRENT_TIMESTAMP null,
    create_by              bigint                             null,
    update_time            datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by              bigint                             null
    )
    comment '天眼查_司法协助';

create index idx_client_id
    on tyc_judicial (client_id);

create table if not exists tyc_law_suit
(
    id                bigint auto_increment comment '主键'
    primary key,
    client_id         bigint                             not null comment '客户id',
    doc_type          varchar(1000)                      null comment '文书类型',
    lawsuit_url       varchar(150)                       null comment '天眼查url（Web）',
    lawsuit_h5_url    varchar(150)                       null comment '天眼查url（H5）',
    title             varchar(2000)                      null comment '案件名称',
    court             varchar(100)                       null comment '审理法院',
    judge_time        varchar(30)                        null comment '裁判日期',
    uuid              varchar(50)                        null comment 'uuid',
    case_no           varchar(1000)                      null comment '案号',
    case_type         varchar(50)                        null comment '案件类型',
    case_reason       varchar(500)                       null comment '案由',
    case_persons_json longtext                           null comment '涉案方',
    case_money        varchar(100)                       null comment '案件金额',
    submit_time       datetime                           null comment '发布日期',
    tyc_id            bigint                             null comment '天眼查id',
    detail_json       longtext                           null comment '详情',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null
    )
    comment '天眼查_法律诉讼';

create index idx_client_id
    on tyc_law_suit (client_id);

create table if not exists tyc_mortgage_info
(
    id               bigint auto_increment comment '主键'
    primary key,
    client_id        bigint                             not null comment '客户id',
    amount           varchar(50)                        null comment '被担保债权数额',
    cancel_date      datetime                           null comment '注销日期',
    publish_date     datetime                           null comment '公示日期',
    reg_date         varchar(30)                        null comment '登记日期',
    remark           varchar(1000)                      null comment '备注',
    type             varchar(50)                        null comment '被担保债权种类',
    reg_department   varchar(255)                       null comment '登记机关',
    reg_num          varchar(100)                       null comment '登记编号',
    scope            varchar(1000)                      null comment '担保范围',
    term             varchar(1000)                      null comment '债务人履行债务的期限',
    tyc_id           bigint                             null comment '天眼查表id',
    cancel_reason    varchar(500)                       null comment '注销原因',
    status           varchar(100)                       null comment '状态',
    base             varchar(4)                         null comment '省份',
    people_info_json longtext                           null comment '抵押权人信息',
    pawn_info_json   longtext                           null comment '抵押物信息',
    change_info_json longtext                           null comment '变更信息',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null
    )
    comment '天眼查_动产抵押';

create index idx_client_id
    on tyc_mortgage_info (client_id);

create table if not exists tyc_punishment_info
(
    id                bigint auto_increment comment '主键'
    primary key,
    client_id         bigint                             not null comment '客户id',
    department_name   varchar(512)                       null comment '处罚单位',
    reason            varchar(512)                       null comment '处罚事由/违法行为类型',
    evidence          varchar(1000)                      null comment '处罚依据（source=信⽤中国时返回数据）',
    punish_status     varchar(10)                        null comment '处罚状态（source=信⽤中国时返回数据）',
    remark            varchar(100)                       null comment '备注（source=国家市场监督管理总局时返回数据）',
    source            varchar(30)                        null comment '数据来源',
    type              varchar(100)                       null comment '处罚类别1（source=信⽤中国时返回数据）',
    content           text                               null comment '处罚结果/内容',
    decision_date     varchar(30)                        null comment '日期',
    legal_person_name varchar(120)                       null comment '法定代表⼈（source=国家市场监督管理总局时返回数据）',
    punish_name       varchar(500)                       null comment '处罚名称（source=信⽤中国时返回数据）',
    punish_number     varchar(256)                       null comment '决定⽂书号',
    type_second       varchar(100)                       null comment '处罚类别2（source=信⽤中国时返回数据）',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null
    )
    comment '天眼查_行政处罚';

create index idx_client_id
    on tyc_punishment_info (client_id);

create table if not exists tyc_zhixing_info
(
    id               bigint auto_increment comment '主键'
    primary key,
    client_id        bigint                             not null comment '客户id',
    case_code        varchar(50)                        null comment '案号',
    party_card_num   varchar(30)                        null comment '身份证号/组织机构代码',
    pname            varchar(60)                        null comment '被执行人名称',
    exec_court_name  varchar(50)                        null comment '执行法院',
    case_create_time datetime                           null comment '创建时间',
    exec_money       varchar(20)                        null comment '执行标的（元）',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null
    )
    comment '天眼查_被执行人';

create index idx_client_id
    on tyc_zhixing_info (client_id);

create table if not exists zhongdeng_info
(
    id                  bigint auto_increment comment '主键'
    primary key,
    client_id           bigint                             not null comment '客户id',
    trade_business_type varchar(32)                        null comment '交易业务类型',
    credit_org          varchar(128)                       null comment '授信机构',
    amount              bigint                             null comment '金额',
    reg_date            datetime                           null comment '登记日期',
    reg_expire_date     datetime                           null comment '登记到期日',
    term                int                                null comment '期限（年）',
    create_time         datetime default CURRENT_TIMESTAMP null,
    create_by           bigint                             null,
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by           bigint                             null
    )
    comment '中登网';

create index idx_client_id
    on zhongdeng_info (client_id);

