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

create table if not exists common_version
(
    id          bigint auto_increment comment '主键'
    primary key,
    main_id     bigint                             not null comment '立项id',
    version     varchar(32)                        not null comment '版本',
    type        tinyint                            not null comment '版本类型（1直接生效，2审批通过生效）',
    module      varchar(50)                        null,
    create_time datetime default CURRENT_TIMESTAMP null,
    create_by   bigint                             null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by   bigint                             null
    )
    comment '立项信息版本表';

create index idx_proj_establish_id
    on common_version (main_id);

create table if not exists contract_account
(
    id              bigint auto_increment comment '方案id'
    primary key,
    contract_id     bigint                             not null comment '所属合同ID',
    client_id       bigint                             null comment '预留-客户id',
    client_name     varchar(200)                       null comment '客户名称',
    account_name    varchar(200)                       null comment '账户名称',
    account_num     varchar(30)                        null comment '银行账号',
    account_address varchar(200)                       null comment '开户行',
    create_by       bigint                             null comment '创建人id、发起人id',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by       bigint                             null comment '最后更新人id',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间'
    )
    comment '合同-收款账户表';

create table if not exists contract_account_lib
(
    id               bigint auto_increment comment '方案id'
    primary key,
    contract_id      bigint                             not null comment '所属合同ID',
    client_id        bigint                             null comment '预留-客户id',
    client_name      varchar(200)                       null comment '客户名称',
    account_name     varchar(200)                       null comment '账户名称',
    account_num      varchar(30)                        null comment '银行账号',
    account_address  varchar(200)                       null comment '开户行',
    create_by        bigint                             null comment '创建人id、发起人id',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by        bigint                             null comment '最后更新人id',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间',
    version          varchar(40)                        not null comment '版本号',
    origin_id        bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '合同-收款账户表';

create table if not exists contract_aoc_price
(
    id                       bigint auto_increment comment 'id	'
    primary key,
    contract_id              bigint                             not null comment '所属合同ID',
    apply_credit_amount      bigint                             null comment '申报授信金额',
    proj_credit_amount       bigint                             null comment '项目金额',
    lease_month_count        int                                null comment '租赁期限月数',
    repay_rate               varchar(20)                        null comment '还款频率。按月，按季，按年，不规则',
    repay_type               varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    repay_times_total        int                                null comment '还款期数',
    lease_rate_percent       bigint                             null comment '项目租赁利率值。百分之多少',
    lpr_type                 varchar(20)                        null comment 'lpr品种 一年期，五年期',
    lpr_percent              int                                null comment 'lpr',
    lpr_add_percent          int                                null comment '加点',
    before_lpr_percent       int                                null comment '租前lpr',
    before_rate_type         varchar(10)                        null comment '租前利率类型。固定利率：fixed、浮动利率：float	',
    before_lpr_type          varchar(20)                        null comment '租前lpr品种 一年期，五年期',
    proj_irr_percent         bigint                             null comment '项目内部收益率',
    before_lpr_add_percent   int                                null comment '租前加点',
    aoc_credit_term          int                                null comment '转让额度有效期（月',
    rate_type                varchar(10)                        null comment '转让费率类型。固定利率：fixed、浮动利率：float	',
    credit_amount_loop       tinyint(1)                         null comment '额度是否可循环',
    earnest_money            bigint                             null comment '保证金',
    proj_earnest_money       bigint                             null comment '项目保证金',
    confirmatory_party       text                               null comment '确权方',
    consulting_fee           bigint                             null comment '服务费/咨询费',
    aoc_financing_proportion int                                null comment '转让融资比例',
    summary                  text                               null comment '拟转应收账款概述',
    aoc_rate_percent         int                                null comment '转让费率值。百分之多少',
    irr_percent              int                                null comment '内部收益率。百分之多少',
    planned_starting_date    date                               null comment '计划起租日',
    rental_calc_type         varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                bigint                             null,
    create_time              datetime default CURRENT_TIMESTAMP null,
    update_by                bigint                             null,
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同-债权转让报价方案表';

create table if not exists contract_base_info
(
    id                             bigint auto_increment
    primary key,
    client_id                      bigint                             null comment '客户ID',
    contract_code                  varchar(50)                        null comment '合同编号',
    apply_credit_amount            bigint                             null comment '项目金额',
    remain_available_quota         bigint                             null comment '剩余可用额度(元)',
    proj_name                      varchar(200)                       null comment '项目名称',
    proj_code                      varchar(20)                        null comment '项目编号',
    biz_type                       varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    lease_type                     varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    project_type                   varchar(30)                        null comment '项目类型：公共事业类、省内国（央）企、其他',
    risk_level                     varchar(20)                        null comment '风险等级',
    proj_source                    varchar(20)                        null comment '项目来源：存量翻单、渠道介绍、自主开发',
    funds_purpose                  varchar(200)                       null comment '资金用途',
    proj_background                text                               null comment '项目背景',
    proj_sponsor_user_id           bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids        json                               null comment '项目协办方用户id列表',
    biz_dept_id                    bigint                             null comment '业务部门id',
    biz_dept_leader_id             bigint                             null comment '业务部门负责人id',
    biz_division_leader_id         bigint                             null comment '业务分管领导id',
    proj_review_id                 bigint                             null comment '关联的评审ID',
    contract_process_status        varchar(20)                        null comment '流程状态',
    contract_status                varchar(20)                        null comment '合同状态',
    create_by                      bigint                             null comment '创建人、发起人',
    create_time                    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                      bigint                             null comment '最后更新人id',
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    estimated_lease_date           datetime                           null comment '概算起租日',
    actual_lease_date              datetime                           null comment '实际起租日',
    actual_finish_date             datetime                           null comment '实际结束日',
    payment_plan_date              datetime                           null comment '计划付款日期',
    payment_plan_amount            bigint                             null comment '计划付款金额-合同金额',
    payment_count                  int(11) unsigned zerofill          null comment '支付申请次数',
    proj_item                      varchar(30)                        null comment '项目类型',
    risk_control_manager_id        bigint                             null comment '风控经理id',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    contract_process_change_status varchar(50)                        null comment '流程变更子类型状态'
    )
    comment '合同基本信息表';

create table if not exists contract_base_info_lib
(
    id                             bigint auto_increment
    primary key,
    client_id                      bigint                             null comment '客户ID',
    contract_code                  varchar(50)                        null comment '合同编号',
    apply_credit_amount            bigint                             null comment '合同金额',
    remain_available_quota         bigint                             null comment '剩余可用额度(元)',
    proj_name                      varchar(200)                       null comment '项目名称',
    proj_code                      varchar(20)                        null comment '项目编号',
    biz_type                       varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    lease_type                     varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    project_type                   varchar(30)                        null comment '项目类型：公共事业类、省内国（央）企、其他',
    risk_level                     varchar(20)                        null comment '风险等级',
    proj_source                    varchar(20)                        null comment '项目来源：存量翻单、渠道介绍、自主开发',
    funds_purpose                  varchar(200)                       null comment '资金用途',
    proj_background                text                               null comment '项目背景',
    proj_sponsor_user_id           bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids        json                               null comment '项目协办方用户id列表',
    biz_dept_id                    bigint                             null comment '业务部门id',
    biz_dept_leader_id             bigint                             null comment '业务部门负责人id',
    biz_division_leader_id         bigint                             null comment '业务分管领导id',
    proj_review_id                 bigint                             null comment '关联的评审ID',
    contract_process_status        varchar(20)                        null comment '流程状态',
    contract_status                varchar(20)                        null comment '合同状态',
    create_by                      bigint                             null comment '创建人、发起人',
    create_time                    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                      bigint                             null comment '最后更新人id',
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    estimated_lease_date           datetime                           null comment '概算起租日',
    actual_lease_date              datetime                           null comment '实际起租日',
    actual_finish_date             datetime                           null comment '实际结束日',
    payment_plan_date              datetime                           null comment '计划付款日期',
    payment_plan_amount            bigint                             null comment '计划付款金额-合同金额',
    payment_count                  int(11) unsigned zerofill          null comment '支付申请次数',
    proj_item                      varchar(30)                        null comment '项目类型',
    risk_control_manager_id        bigint                             null comment '风控经理id',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    contract_process_change_status varchar(50)                        null comment '流程变更子类型状态',
    version                        varchar(40)                        not null comment '版本号',
    origin_id                      bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time               datetime                           null,
    data_create_by                 bigint                             null,
    data_update_time               datetime                           null,
    data_update_by                 bigint                             null
    )
    comment '合同基本信息表';

create table if not exists contract_factoring_price
(
    id                             bigint auto_increment comment 'id	'
    primary key,
    contract_id                    bigint                             not null comment '所属合同ID',
    apply_credit_amount            bigint                             null comment '申报授信金额',
    factoring_credit_term          int                                null comment '保理额度有效期（月',
    repay_type                     varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    earnest_money                  bigint                             null comment '保证金',
    confirmatory_party             text                               null comment '确权方',
    factoring_financing_proportion int                                null comment '保理融资比例',
    consulting_fee                 bigint                             null comment '服务费/咨询费',
    summary                        text                               null comment '拟转应收账款概述',
    rate_type                      varchar(10)                        null comment '保理费率类型。固定利率：fixed、浮动利率：float	',
    factoring_rate_percent         int                                null comment '保理费率值。百分之多少',
    irr_percent                    int                                null comment '内部收益率。百分之多少',
    existing_lease_credit          bit      default b'0'              null comment '是否有存续租赁授信',
    new_lease_credit               bit      default b'0'              null comment '是否新增租赁授信',
    planned_starting_date          date                               null comment '计划起租日',
    rental_calc_type               varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    proj_credit_amount             bigint                             null comment '项目金额',
    lease_month_count              int                                null comment '租赁期限月数',
    repay_rate                     varchar(20)                        null comment '还款频率。按月，按季，按年，不规则',
    repay_times_total              int                                null comment '还款期数',
    lease_rate_percent             bigint                             null comment '项目租赁利率值',
    lpr_type                       varchar(20)                        null comment 'lpr品种 一年期，五年期',
    lpr_percent                    int                                null comment 'lpr',
    lpr_add_percent                int                                null comment '加点',
    before_lpr_percent             int                                null comment '租前lpr',
    before_lpr_add_percent         int                                null comment '租前加点',
    before_rate_type               varchar(10)                        null comment '租前利率类型。固定利率：fixed、浮动利率：float	',
    before_lpr_type                varchar(20)                        null comment '租前lpr品种 一年期，五年期',
    proj_irr_percent               bigint                             null comment '项目irr',
    proj_earnest_money             bigint                             null comment '项目保证金',
    create_by                      bigint                             null,
    create_time                    datetime default CURRENT_TIMESTAMP null,
    update_by                      bigint                             null,
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同-保理报价方案表';

create table if not exists contract_guarantor
(
    id               bigint auto_increment comment 'id'
    primary key,
    contract_id      bigint                             not null comment '所属合同id',
    relat_contracts  json                               null comment '关联合同ID',
    guarantor_type   varchar(20)                        null comment '担保人类型：自然人、法人',
    guarantor_ids    json                               null comment '担保人id',
    guarantee_method varchar(50)                        null comment '担保方式-连带责任担保、一般担保',
    is_report        tinyint(1)                         null comment '是否上报征信 0不上报，1上报',
    create_by        bigint                             null comment '创建人id、发起人id',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by        bigint                             null comment '最后更新人id',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间'
    )
    comment '合同-担保措施';

create table if not exists contract_guarantor_lib
(
    id               bigint auto_increment comment 'id'
    primary key,
    contract_id      bigint                             not null comment '所属合同id',
    relat_contracts  json                               null comment '关联合同ID',
    guarantor_type   varchar(20)                        null comment '担保人类型：自然人、法人',
    guarantor_ids    json                               null comment '担保人id',
    guarantee_method varchar(50)                        null comment '担保方式-连带责任担保、一般担保',
    is_report        tinyint(1)                         null comment '是否上报征信 0不上报，1上报',
    create_by        bigint                             null comment '创建人id、发起人id',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by        bigint                             null comment '最后更新人id',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间',
    version          varchar(40)                        not null comment '版本号',
    origin_id        bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '合同-担保措施';

create table if not exists contract_lease_item
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                        not null comment '合同id',
    sequence            int(10)                                null comment '序号',
    name                varchar(100) default ''                null comment '设备名称',
    type                varchar(50)  default ''                null comment '设备类型',
    model               varchar(50)  default ''                null comment '规格型号',
    supplier            varchar(100) default ''                null comment '供应商',
    quantity            varchar(20)  default ''                null comment '数量',
    unit                varchar(10)  default ''                null comment '计量单位',
    purchase_date       varchar(50)  default ''                null comment '购置日期',
    original_book_value bigint unsigned                        null comment '账面原值',
    assessed_value      bigint unsigned                        null comment '评估价值',
    original_book_net_value      bigint unsigned                        null comment '评估价值',
    invoice_code        varchar(1000)  default ''                null comment '发票号',
    car_vin_code        varchar(1000)  default ''                null comment '车架号',
    fixed_assets_code        varchar(1000)  default ''                null comment '固定资产编号',
    storage_place       varchar(50)  default ''                null comment '存放地点',
    create_by           bigint                                 null comment '创建人、发起人',
    create_time         datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                                 null comment '最后更新人id',
    update_time         datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同明细-租赁物清单';

create table if not exists contract_lease_item_lib
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                        not null comment '合同id',
    sequence            varchar(10)                            null comment '序号',
    name                varchar(100) default ''                null comment '设备名称',
    type                varchar(50)  default ''                null comment '设备类型',
    model               varchar(50)  default ''                null comment '规格型号',
    supplier            varchar(100) default ''                null comment '供应商',
    quantity            varchar(20)  default ''                null comment '数量',
    unit                varchar(10)  default ''                null comment '计量单位',
    purchase_date       varchar(50)  default ''                null comment '购置日期',
    original_book_value bigint unsigned                        null comment '账面原值',
    assessed_value      bigint unsigned                        null comment '评估价值',
    original_book_net_value      bigint unsigned                        null comment '评估价值',
    invoice_code        varchar(1000)  default ''                null comment '发票号',
    car_vin_code        varchar(1000)  default ''                null comment '车架号',
    fixed_assets_code        varchar(1000)  default ''                null comment '固定资产编号',
    storage_place       varchar(50)  default ''                null comment '存放地点',
    create_by           bigint                                 null comment '创建人、发起人',
    create_time         datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                                 null comment '最后更新人id',
    update_time         datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version             varchar(40)                            not null comment '版本号',
    origin_id           bigint                                 not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time    datetime                               null,
    data_create_by      bigint                                 null,
    data_update_time    datetime                               null,
    data_update_by      bigint                                 null
    )
    comment '合同明细-租赁物清单';

create table if not exists contract_lease_price
(
    id                     bigint auto_increment comment '租赁报价方案id	'
    primary key,
    contract_id            bigint                             not null comment '所属合同ID',
    apply_credit_amount    bigint                             null comment '申报授信金额-合同金额',
    lease_month_count      int                                null comment '租赁期限月数',
    proj_lease_month_count int                                null comment '项目租赁期限月数',
    repay_rate             varchar(20)                        null comment '还款频率。按月，按季，按年，不规则',
    repay_times_total      int                                null comment '还款期数',
    pay_type               varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type       varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    earnest_money          bigint                             null comment '保证金',
    proj_down_payment      bigint                             null comment '项目首期租金',
    down_payment           bigint                             null comment '首付款',
    consulting_fee         bigint                             null comment '服务费/咨询费',
    proj_consulting_fee    bigint                             null comment '项目服务费/咨询费',
    nominal_price          bigint                             null comment '名义货价',
    rate_type              varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lpr_type               varchar(20)                        null comment 'lpr品种 一年期，五年期',
    lpr_percent            int                                null comment 'lpr',
    lpr_add_percent        int                                null comment '加点',
    before_lpr_percent     int                                null comment '租前lpr',
    before_rate_type       varchar(10)                        null comment '租前利率类型。固定利率：fixed、浮动利率：float	',
    before_lpr_type        varchar(20)                        null comment '租前lpr品种 一年期，五年期',
    before_lpr_add_percent int                                null comment '租前加点',
    default_interest_rate  int                                null comment '罚息日利率',
    irr_percent            int                                null comment '内部收益率。百分之多少',
    lease_rate_percent     bigint                             null comment '项目租赁利率值',
    proj_earnest_money     bigint                             null comment '项目保证金',
    proj_credit_amount     bigint                             null comment '项目金额',
    proj_irr_percent       bigint                             null comment '项目irr',
    credit_amount_loop     tinyint(1)                         null comment '额度是否可循环',
    create_by              bigint                             null comment '创建人id、发起人id	',
    create_time            datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by              bigint                             null comment '最后更新人id	',
    update_time            datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    constraint uniq_contract_id
    unique (contract_id) comment '每个合同只能有一份报价'
    )
    comment '合同-租赁报价方案表';

create table if not exists contract_lease_price_lib
(
    id                     bigint auto_increment comment '租赁报价方案id	'
    primary key,
    contract_id            bigint                             not null comment '所属合同ID',
    apply_credit_amount    bigint                             null comment '申报授信金额-合同金额',
    lease_month_count      int                                null comment '租赁期限月数',
    proj_lease_month_count int                                null comment '项目租赁期限月数',
    repay_rate             varchar(20)                        null comment '还款频率。按月，按季，按年，不规则',
    repay_times_total      int                                null comment '还款期数',
    pay_type               varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type       varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    earnest_money          bigint                             null comment '保证金',
    proj_down_payment      bigint                             null comment '项目首期租金',
    down_payment           bigint                             null comment '首付款',
    consulting_fee         bigint                             null comment '服务费/咨询费',
    proj_consulting_fee    bigint                             null comment '项目服务费/咨询费',
    nominal_price          bigint                             null comment '名义货价',
    rate_type              varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lpr_type               varchar(20)                        null comment 'lpr品种 一年期，五年期',
    lpr_percent            int                                null comment 'lpr',
    lpr_add_percent        int                                null comment '加点',
    before_lpr_percent     int                                null comment '租前lpr',
    before_rate_type       varchar(10)                        null comment '租前利率类型。固定利率：fixed、浮动利率：float	',
    before_lpr_type        varchar(20)                        null comment '租前lpr品种 一年期，五年期',
    before_lpr_add_percent int                                null comment '租前加点',
    default_interest_rate  int                                null comment '罚息日利率',
    irr_percent            int                                null comment '内部收益率。百分之多少',
    lease_rate_percent     bigint                             null comment '项目租赁利率值',
    proj_earnest_money     bigint                             null comment '项目保证金',
    proj_credit_amount     bigint                             null comment '项目金额',
    proj_irr_percent       bigint                             null comment '项目irr',
    credit_amount_loop     tinyint(1)                         null comment '额度是否可循环',
    create_by              bigint                             null comment '创建人id、发起人id	',
    create_time            datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by              bigint                             null comment '最后更新人id	',
    update_time            datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    version                varchar(40)                        not null comment '版本号',
    origin_id              bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time       datetime                           null,
    data_create_by         bigint                             null,
    data_update_time       datetime                           null,
    data_update_by         bigint                             null
    )
    comment '合同-租赁报价方案表';

create table if not exists contract_mortgage
(
    id                bigint auto_increment comment 'id'
    primary key,
    contract_id       bigint                             not null comment '所属合同id',
    file_id           bigint                             null comment '抵押物清单id',
    relat_contracts   json                               null comment '关联合同编号',
    mortgage_type     varchar(30)                        null comment '质押人类型',
    mortgage_ids      json                               null comment '抵押人id,姓名，类型',
    mortgage_describe varchar(200)                       null comment '抵押物描述',
    appraisal_company varchar(200)                       null comment '评估公司',
    appraisal_code    varchar(200)                       null comment '评估编号',
    highest           tinyint                            null comment '是否最高额标识，0-否，1-是',
    assess            tinyint                            null comment '是否评估，0-否，1-是',
    assess_date       date                               null comment '评估日期',
    create_by         bigint                             null comment '创建人id、发起人id',
    create_time       datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by         bigint                             null comment '最后更新人id',
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间'
    )
    comment '合同-抵押措施';

CREATE TABLE if not exists `contract_mortgage_item` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',
  `mortgage_id` bigint(20) unsigned NOT NULL COMMENT '抵押措施id',
  `sequence` varchar(10) DEFAULT NULL COMMENT '序号',
  `category` varchar(100) DEFAULT NULL COMMENT '种类',
  `unique_identify_code` varchar(1000) DEFAULT NULL COMMENT '唯一识别号',
  `unique_identify_code_type` varchar(100) DEFAULT NULL COMMENT '唯一识别号类型',
  `name` varchar(100) DEFAULT '' COMMENT '设备名称',
  `supplier` varchar(100) DEFAULT '' COMMENT '供应商',
  `quantity` varchar(20) DEFAULT '' COMMENT '数量',
  `unit` varchar(10) DEFAULT '' COMMENT '计量单位',
  `purchase_date` varchar(50) DEFAULT '' COMMENT '购置日期',
  `original_book_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面原值',
  `original_book_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面净值',
  `assessed_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估原值',
  `assessed_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估净值',
  `invoice_code` varchar(1000) DEFAULT '' COMMENT '发票号',
  `storage_place` varchar(50) DEFAULT '' COMMENT '存放地点',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抵押措施-抵押物清单';

CREATE TABLE if not exists `contract_mortgage_item_lib` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) DEFAULT NULL,
  `mortgage_id` bigint(20) unsigned NOT NULL COMMENT '抵押措施id',
  `sequence` varchar(10) DEFAULT NULL COMMENT '序号',
  `category` varchar(100) DEFAULT NULL COMMENT '种类',
  `unique_identify_code` varchar(1000) DEFAULT NULL COMMENT '唯一识别号',
  `unique_identify_code_type` varchar(100) DEFAULT NULL COMMENT '唯一识别号类型',
  `name` varchar(100) DEFAULT '' COMMENT '设备名称',
  `supplier` varchar(100) DEFAULT '' COMMENT '供应商',
  `quantity` varchar(20) DEFAULT '' COMMENT '数量',
  `unit` varchar(10) DEFAULT '' COMMENT '计量单位',
  `purchase_date` varchar(50) DEFAULT '' COMMENT '购置日期',
  `original_book_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面原值',
  `original_book_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面净值',
  `assessed_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估价值',
  `assessed_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估净值',
  `invoice_code` varchar(1000) DEFAULT '' COMMENT '发票号',
  `storage_place` varchar(50) DEFAULT '' COMMENT '存放地点',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抵押措施-抵押物清单';

create table if not exists contract_mortgage_lib
(
    id                bigint auto_increment comment 'id'
    primary key,
    contract_id       bigint                             not null comment '所属合同id',
    file_id           bigint                             null comment '抵押物清单id',
    relat_contracts   json                               null comment '关联合同编号',
    mortgage_type     varchar(30)                        null comment '质押人类型',
    mortgage_ids      json                               null comment '抵押人id,姓名，类型',
    mortgage_describe varchar(200)                       null comment '抵押物描述',
    appraisal_company varchar(200)                       null comment '评估公司',
    appraisal_code    varchar(200)                       null comment '评估编号',
    highest           tinyint                            null comment '是否最高额标识，0-否，1-是',
    assess            tinyint                            null comment '是否评估，0-否，1-是',
    assess_date       date                               null comment '评估日期',
    create_by         bigint                             null comment '创建人id、发起人id',
    create_time       datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by         bigint                             null comment '最后更新人id',
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间',
    version           varchar(40)                        not null comment '版本号',
    origin_id         bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time  datetime                           null,
    data_create_by    bigint                             null,
    data_update_time  datetime                           null,
    data_update_by    bigint                             null
    )
    comment '合同-抵押措施';

create table if not exists contract_pledge
(
    id              bigint auto_increment comment 'id	'
    primary key,
    contract_id     bigint                             not null comment '所属合同id',
    pledge_Type     varchar(20)                        null comment '质押人类型',
    pledge_ids      json                               null comment '质押人id',
    pledge_describe varchar(200)                       null comment '质押物描述',
    create_by       bigint                             null comment '创建人id、发起人id	',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by       bigint                             null comment '最后更新人id	',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	'
    )
    comment '合同-质押措施';

create table if not exists contract_pledge_lib
(
    id               bigint auto_increment comment 'id	'
    primary key,
    contract_id      bigint                             not null comment '所属合同id',
    pledge_Type      varchar(20)                        null comment '质押人类型',
    pledge_ids       json                               null comment '质押人id',
    pledge_describe  varchar(200)                       null comment '质押物描述',
    create_by        bigint                             null comment '创建人id、发起人id	',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by        bigint                             null comment '最后更新人id	',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    version          varchar(40)                        not null comment '版本号',
    origin_id        bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '合同-质押措施';

create table if not exists contract_prepayment
(
    id                       bigint auto_increment comment 'id	'
    primary key,
    contract_id              bigint                             not null comment '所属合同ID',
    applay_repayment_date    datetime                           null comment '申请还款日期',
    unpaid_rent_due          bigint                             not null comment '到期未付租金',
    penalty                  bigint                             null comment '违约金',
    early_repayment          bigint                             not null comment '提前归还本金',
    early_repayment_interest bigint                             null comment '提前归还利息',
    loss                     bigint                             null comment '提前终止补偿金',
    unpaid_rent              bigint                             null comment '到期未付租金',
    create_by                bigint                             null comment '创建人id、发起人id	',
    create_time              datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by                bigint                             null comment '最后更新人id	',
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    constraint uniq_contract_id
    unique (contract_id) comment '每个合同只能有一份报价'
    )
    comment '合同-提前还款表';

create table if not exists contract_receipt
(
    id                 bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id        bigint unsigned                       not null comment '合同id',
    payment_apply_code varchar(20) default ''                null comment '付款申请编号',
    create_by          bigint                                null comment '创建人、发起人',
    create_time        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by          bigint                                null comment '最后更新人id',
    update_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同明细-借据';

create table if not exists contract_receipt_lib
(
    id                 bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id        bigint unsigned                       not null comment '合同id',
    payment_apply_code varchar(20) default ''                null comment '付款申请编号',
    create_by          bigint                                null comment '创建人、发起人',
    create_time        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by          bigint                                null comment '最后更新人id',
    update_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version            varchar(40)                           not null comment '版本号',
    origin_id          bigint                                not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time   datetime                              null,
    data_create_by     bigint                                null,
    data_update_time   datetime                              null,
    data_update_by     bigint                                null
    )
    comment '合同明细-借据';

create table if not exists contract_remind_record
(
    id                   bigint auto_increment
    primary key,
    contract_id          bigint                                not null comment '合同id',
    contract_code        varchar(50) default ''                not null comment '合同编号',
    payment_id           bigint                                not null comment '付款id',
    payment_code         varchar(50) default ''                not null comment '付款编号',
    contract_creator_id  bigint                                not null comment '合同创建人id',
    contract_create_time datetime                              not null comment '合同创建时间',
    create_by            bigint                                null comment '创建人、发起人',
    create_time          datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by            bigint                                null comment '最后更新人id',
    update_time          datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    )
    comment '合同起租提醒记录表';

create index idx_contract_creator
    on contract_remind_record (contract_creator_id);

create index idx_contract_id
    on contract_remind_record (contract_id);

create table if not exists contract_rent_actual
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                       not null comment '合同id',
    receipt_id          bigint unsigned                       not null comment '借据id',
    cash_flow_code      varchar(30) default ''                null comment '现金流编号',
    cash_flow_date      date                                  null comment '日期',
    cash_flow_phase     tinyint(4) unsigned                   null comment '期项',
    rent                bigint                                null comment '租金',
    principal           bigint                                null comment '本金',
    interest            bigint                                null comment '利息',
    remaining_principal bigint                                null comment '剩余本金',
    create_by           bigint                                null comment '创建人、发起人',
    create_time         datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                                null comment '最后更新人id',
    update_time         datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同明细-实际租金';

create table if not exists contract_rent_actual_lib
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                       not null comment '合同id',
    receipt_id          bigint unsigned                       not null comment '借据id',
    cash_flow_code      varchar(30) default ''                null comment '现金流编号',
    cash_flow_date      date                                  null comment '日期',
    cash_flow_phase     tinyint(4) unsigned                   null comment '期项',
    rent                bigint                                null comment '租金',
    principal           bigint                                null comment '本金',
    interest            bigint                                null comment '利息',
    remaining_principal bigint                                null comment '剩余本金',
    create_by           bigint                                null comment '创建人、发起人',
    create_time         datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                                null comment '最后更新人id',
    update_time         datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version             varchar(40)                           not null comment '版本号',
    origin_id           bigint                                not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time    datetime                              null,
    data_create_by      bigint                                null,
    data_update_time    datetime                              null,
    data_update_by      bigint                                null
    )
    comment '合同明细-实际租金';

create table if not exists contract_rent_estimate
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                    not null comment '合同id',
    cash_flow_date      date                               null comment '日期',
    cash_flow_phase     tinyint(4) unsigned                null comment '期项',
    rent                bigint                             null comment '租金',
    principal           bigint                             null comment '本金',
    interest            bigint                             null comment '利息',
    remaining_principal bigint                             null comment '剩余本金',
    create_by           bigint                             null comment '创建人、发起人',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                             null comment '最后更新人id',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同明细-概算租金';

create table if not exists contract_rent_estimate_lib
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                    not null comment '合同id',
    cash_flow_date      date                               null comment '日期',
    cash_flow_phase     tinyint(4) unsigned                null comment '期项',
    rent                bigint                             null comment '租金',
    principal           bigint                             null comment '本金',
    interest            bigint                             null comment '利息',
    remaining_principal bigint                             null comment '剩余本金',
    create_by           bigint                             null comment '创建人、发起人',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                             null comment '最后更新人id',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version             varchar(40)                        not null comment '版本号',
    origin_id           bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time    datetime                           null,
    data_create_by      bigint                             null,
    data_update_time    datetime                           null,
    data_update_by      bigint                             null
    )
    comment '合同明细-概算租金';

create table if not exists contract_settle_plan
(
    id                        bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id               bigint unsigned                    not null comment '合同id',
    settle_type               varchar(20)                        not null comment '结清类型',
    outstanding_rent          bigint unsigned                    not null comment '到期未付租金',
    before_maturity_principal bigint unsigned                    not null comment '未到期本金',
    loss                      bigint unsigned                    null comment '损失金',
    liquidated_damages        bigint unsigned                    null comment '违约金',
    earnest_balance           bigint unsigned                    null comment '保证金余额',
    is_earnest_deduction      tinyint                            not null comment '保证金是否内扣',
    nominal_price             bigint unsigned                    null comment '名义货价',
    apply_derate_amount       bigint unsigned                    null comment '申请减免金额',
    original_deadline         date                               not null comment '原到期日',
    total_amount              bigint unsigned                    null comment '合计金额',
    apply_settle_date         date                               null comment '申请结清日期',
    create_by                 bigint                             null comment '创建人、发起人',
    create_time               datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                 bigint                             null comment '最后更新人id',
    update_time               datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同明细-结清方案';

create table if not exists contract_tenantry
(
    id                  bigint auto_increment comment 'id	'
    primary key,
    contract_id         bigint                             not null comment '所属合同ID',
    lessee_id           bigint                             null comment '承租人id',
    lessee_type         varchar(20)                        null comment '承租人类型 联合承租人,主承租人',
    lessee_name         varchar(200)                       null comment '承租人名称',
    stock_risk_exposure bigint                             null comment '存量风险敞口',
    contact_id          bigint                             null comment '联系人id',
    contact_info        json                               null comment '指定联系人',
    is_report           tinyint(1)                         null comment '是否上报征信 0不上报，1上报',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	'
    )
    comment '合同-承租人表';

create table if not exists contract_tenantry_lib
(
    id                  bigint auto_increment comment 'id	'
    primary key,
    contract_id         bigint                             not null comment '所属合同ID',
    lessee_id           bigint                             null comment '承租人id',
    lessee_type         varchar(20)                        null comment '承租人类型 联合承租人,主承租人',
    lessee_name         varchar(200)                       null comment '承租人名称',
    stock_risk_exposure bigint                             null comment '存量风险敞口',
    contact_id          bigint                             null comment '联系人id',
    contact_info        json                               null comment '指定联系人',
    is_report           tinyint(1)                         null comment '是否上报征信 0不上报，1上报',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    version             varchar(40)                        not null comment '版本号',
    origin_id           bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time    datetime                           null,
    data_create_by      bigint                             null,
    data_update_time    datetime                           null,
    data_update_by      bigint                             null
    )
    comment '合同-承租人表';

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

create table if not exists proj_establish_aoc_price
(
    id                       bigint auto_increment comment '立项基本信息表id	'
    primary key,
    proj_establish_id        bigint                             not null comment '所属立项ID',
    apply_credit_amount      bigint                             null comment '申报授信金额',
    aoc_credit_term          int                                null comment '转让额度有效期（月',
    repay_type               varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop       tinyint(1)                         null comment '额度是否可循环',
    earnest_money            bigint                             null comment '保证金',
    confirmatory_party       text                               null comment '确权方',
    consulting_fee           bigint                             null comment '服务费/咨询费',
    aoc_financing_proportion int                                null comment '转让融资比例',
    summary                  text                               null comment '拟转应收账款概述',
    rate_type                varchar(10)                        null comment '转让费率类型。固定利率：fixed、浮动利率：float	',
    aoc_rate_percent         int                                null comment '转让费率值。百分之多少',
    irr_percent              int                                null comment '内部收益率。百分之多少',
    rental_calc_type         varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay
其他：other',
    create_by                bigint                             null,
    create_time              datetime default CURRENT_TIMESTAMP null,
    update_by                bigint                             null,
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uniq_proj_establish_id
    unique (proj_establish_id) comment '每个立项只能有一份报价'
    )
    comment '债权转让报价方案表';

create table if not exists proj_establish_aoc_price_lib
(
    id                       bigint auto_increment comment '立项基本信息表id	'
    primary key,
    proj_establish_id        bigint                             not null comment '所属立项ID',
    apply_credit_amount      bigint                             null comment '申报授信金额',
    aoc_credit_term          int                                null comment '转让额度有效期（月',
    repay_type               varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop       tinyint(1)                         null comment '额度是否可循环',
    earnest_money            bigint                             null comment '保证金',
    confirmatory_party       text                               null comment '确权方',
    consulting_fee           bigint                             null comment '服务费/咨询费',
    aoc_financing_proportion int                                null comment '转让融资比例',
    summary                  text                               null comment '拟转应收账款概述',
    rate_type                varchar(10)                        null comment '转让费率类型。固定利率：fixed、浮动利率：float	',
    aoc_rate_percent         int                                null comment '转让费率值。百分之多少',
    irr_percent              int                                null comment '内部收益率。百分之多少',
    rental_calc_type         varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                bigint                             null,
    create_time              datetime default CURRENT_TIMESTAMP null,
    update_by                bigint                             null,
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version                  varchar(40)                        not null comment '版本号',
    origin_id                bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time         datetime                           null,
    data_create_by           bigint                             null,
    data_update_time         datetime                           null,
    data_update_by           bigint                             null
    )
    comment '债权转让报价方案表';

create table if not exists proj_establish_base_info
(
    id                            bigint auto_increment
    primary key,
    client_id                     bigint                             null comment '客户id',
    biz_type                      varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    proj_name                     varchar(200)                       null comment '项目名称',
    approval_type                 varchar(20)                        null comment '审批类型',
    proj_code                     varchar(20)                        null comment '项目编号',
    lease_types                   varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    factoring_types               varchar(100)                       null comment '保理类型。有追明保理、无追明保理、有追暗保理',
    proj_source                   varchar(20)                        null comment '存量翻单、渠道介绍、自主开发',
    funds_purpose                 varchar(200)                       null comment '资金用途',
    proj_background               text                               null comment '项目背景',
    transferor_client_id          bigint                             null comment '转让方。（项目类型为租赁时）',
    lessee_info                   json                               null comment '承租人列表',
    creditor_client_id            bigint                             null comment '债权人id',
    creditor_stock_risk_exposure  bigint                             null comment '债权人存量风险敞口',
    debtor_info                   json                               null comment '债务人信息。（项目类型为保理时）',
    guarantee_info                json                               null comment '担保人信息',
    pledgor_info                  json                               null comment '质押人信息',
    mortgagor_info                json                               null comment '抵押人信息',
    proj_sponsor_user_id          bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids       json                               null comment '项目协办方用户id列表',
    create_by                     bigint                             null comment '创建人、发起人',
    biz_dept_id                   bigint                             null comment '业务部门id',
    biz_dept_leader_id            bigint                             null comment '业务部门负责人id',
    biz_division_leader_id        bigint                             null comment '业务分管领导id',
    risk_control_manager_id       bigint                             null comment '风控经理id',
    proj_establish_status         varchar(50)                        null comment '立项状态',
    proj_establish_process_status varchar(50)                        null comment '流程状态',
    create_time                   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                     bigint                             null comment '最后更新人id',
    update_time                   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    type_seq_id                   bigint                             null comment '不同类型，不同的自增序列id',
    constraint proj_establish_base_info_proj_code_uindex
    unique (proj_code),
    constraint seqid_unique
    unique (biz_type, type_seq_id)
    )
    comment '立项基本信息表';

create table if not exists proj_establish_base_info_lib
(
    id                            bigint auto_increment
    primary key,
    client_id                     bigint                             null comment '客户id',
    biz_type                      varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    proj_name                     varchar(200)                       null comment '项目名称',
    approval_type                 varchar(20)                        null comment '审批类型',
    proj_code                     varchar(20)                        null comment '项目编号',
    lease_types                   varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    factoring_types               varchar(100)                       null comment '保理类型。有追明保理、无追明保理、有追暗保理',
    proj_source                   varchar(20)                        null comment '存量翻单、渠道介绍、自主开发',
    funds_purpose                 varchar(200)                       null comment '资金用途',
    proj_background               text                               null comment '项目背景',
    transferor_client_id          bigint                             null comment '转让方。（项目类型为租赁时）',
    lessee_info                   json                               null comment '承租人列表',
    creditor_client_id            bigint                             null comment '债权人id',
    creditor_stock_risk_exposure  bigint                             null comment '债权人存量风险敞口',
    debtor_info                   json                               null comment '债务人信息。（项目类型为保理时）',
    guarantee_info                json                               null comment '担保人信息',
    pledgor_info                  json                               null comment '质押人信息',
    mortgagor_info                json                               null comment '抵押人信息',
    proj_sponsor_user_id          bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids       varchar(200)                       null comment '项目协办方用户id列表',
    create_by                     bigint                             null comment '创建人、发起人',
    biz_dept_id                   bigint                             null comment '业务部门id',
    biz_dept_leader_id            bigint                             null comment '业务部门负责人id',
    biz_division_leader_id        bigint                             null comment '业务分管领导id',
    risk_control_manager_id       bigint                             null comment '风控经理id',
    proj_establish_status         varchar(50)                        null comment '立项状态',
    proj_establish_process_status varchar(50)                        null comment '流程状态',
    create_time                   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                     bigint                             null comment '最后更新人id',
    update_time                   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    origin_id                     bigint                             not null comment '草稿表id',
    version                       varchar(32)                        not null comment '版本',
    data_create_time              datetime                           null,
    data_create_by                bigint                             null,
    data_update_time              datetime                           null,
    data_update_by                bigint                             null,
    type_seq_id                   bigint                             null
    )
    comment '立项基本信息表';

create table if not exists proj_establish_factoring_price
(
    id                             bigint auto_increment comment '立项基本信息表id	'
    primary key,
    proj_establish_id              bigint                             not null comment '所属立项ID',
    apply_credit_amount            bigint                             null comment '申报授信金额',
    factoring_credit_term          int                                null comment '保理额度有效期（月',
    repay_type                     varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    earnest_money                  bigint                             null comment '保证金',
    confirmatory_party             text                               null comment '确权方',
    factoring_financing_proportion int                                null comment '保理融资比例',
    consulting_fee                 bigint                             null comment '服务费/咨询费',
    summary                        text                               null comment '拟转应收账款概述',
    rate_type                      varchar(10)                        null comment '保理费率类型。固定利率：fixed、浮动利率：float	',
    factoring_rate_percent         int                                null comment '保理费率值。百分之多少',
    irr_percent                    int                                null comment '内部收益率。百分之多少',
    rental_calc_type               varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    existing_lease_credit          bit      default b'0'              null comment '是否有存续租赁授信',
    new_lease_credit               bit      default b'0'              null comment '是否新增租赁授信',
    create_by                      bigint                             null,
    create_time                    datetime default CURRENT_TIMESTAMP null,
    update_by                      bigint                             null,
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uniq_proj_establish_id
    unique (proj_establish_id) comment '每个立项只能有一份报价'
    )
    comment '保理报价方案表';

create table if not exists proj_establish_factoring_price_lib
(
    id                             bigint auto_increment comment '立项基本信息表id	'
    primary key,
    proj_establish_id              bigint                             not null comment '所属立项ID',
    apply_credit_amount            bigint                             null comment '申报授信金额',
    factoring_credit_term          int                                null comment '保理额度有效期（月',
    repay_type                     varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    earnest_money                  bigint                             null comment '保证金',
    confirmatory_party             text                               null comment '确权方',
    consulting_fee                 bigint                             null comment '服务费/咨询费',
    factoring_financing_proportion int                                null comment '保理融资比例',
    summary                        text                               null comment '拟转应收账款概述',
    rate_type                      varchar(10)                        null comment '保理费率类型。固定利率：fixed、浮动利率：float	',
    factoring_rate_percent         int                                null comment '保理费率值。百分之多少',
    irr_percent                    int                                null comment '内部收益率。百分之多少',
    rental_calc_type               varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    existing_lease_credit          bit      default b'0'              null comment '是否有存续租赁授信',
    new_lease_credit               bit      default b'0'              null comment '是否新增租赁授信',
    create_by                      bigint                             null,
    create_time                    datetime default CURRENT_TIMESTAMP null,
    update_by                      bigint                             null,
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    origin_id                      bigint                             not null comment '草稿表id',
    version                        varchar(32)                        not null comment '版本',
    data_create_time               datetime                           null,
    data_create_by                 bigint                             null,
    data_update_time               datetime                           null,
    data_update_by                 bigint                             null
    )
    comment '保理报价方案表';

create table if not exists proj_establish_lease_price
(
    id                  bigint auto_increment comment '立项基本信息表id'
    primary key,
    proj_establish_id   bigint                             not null comment '所属立项ID',
    apply_credit_amount bigint                             null comment '申报授信金额',
    lease_month_count   int                                null comment '租赁期限月数',
    repay_times_yearly  int                                null comment '每年还款次数',
    repay_times_total   int                                null comment '还款期数',
    repay_rate          varchar(20)                        null comment '还款频率',
    pay_type            varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type    varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    credit_amount_loop  tinyint(1)                         null comment '额度是否可循环',
    earnest_money       bigint                             null comment '保证金',
    down_payment        bigint                             null comment '首付款',
    consulting_fee      bigint                             null comment '服务费/咨询费',
    nominal_price       bigint                             null comment '名义货价',
    rate_type           varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lease_rate_percent  int                                null comment '租赁利率值。百分之多少',
    irr_percent         int                                null comment '内部收益率。百分之多少',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    constraint uniq_proj_establish_id
    unique (proj_establish_id) comment '每个立项只能有一份报价'
    )
    comment '租赁报价方案表';

create table if not exists proj_establish_lease_price_lib
(
    id                  bigint auto_increment comment '立项基本信息表id'
    primary key,
    proj_establish_id   bigint                             not null comment '所属立项ID',
    apply_credit_amount bigint                             null comment '申报授信金额',
    lease_month_count   int                                null comment '租赁期限月数',
    repay_times_yearly  int                                null comment '每年还款次数',
    repay_times_total   int                                null comment '还款期数',
    repay_rate          varchar(20)                        null comment '还款频率',
    pay_type            varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type    varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    credit_amount_loop  tinyint(1)                         null comment '额度是否可循环',
    earnest_money       bigint                             null comment '保证金',
    down_payment        bigint                             null comment '首付款',
    consulting_fee      bigint                             null comment '服务费/咨询费',
    nominal_price       bigint                             null comment '名义货价',
    rate_type           varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lease_rate_percent  int                                null comment '租赁利率值。百分之多少',
    irr_percent         int                                null comment '内部收益率。百分之多少',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    origin_id           bigint                             not null comment '草稿表id',
    version             varchar(32)                        not null comment '版本',
    data_create_time    datetime                           null,
    data_create_by      bigint                             null,
    data_update_time    datetime                           null,
    data_update_by      bigint                             null
    )
    comment '租赁报价方案表';

create table if not exists proj_review_aoc_price
(
    id                       bigint auto_increment comment '立项基本信息表id	'
    primary key,
    project_id               bigint                             not null comment '所属项目ID',
    apply_credit_amount      bigint                             null comment '申报授信金额',
    aoc_credit_term          int                                null comment '转让额度有效期（月',
    repay_type               varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop       tinyint(1)                         null comment '额度是否可循环',
    earnest_money            bigint                             null comment '保证金',
    confirmatory_party       text                               null comment '确权方',
    consulting_fee           bigint                             null comment '服务费/咨询费',
    aoc_financing_proportion int                                null comment '转让融资比例',
    summary                  text                               null comment '拟转应收账款概述',
    rate_type                varchar(10)                        null comment '转让费率类型。固定利率：fixed、浮动利率：float	',
    aoc_rate_percent         int                                null comment '转让费率值。百分之多少',
    irr_percent              int                                null comment '内部收益率。百分之多少',
    planned_starting_date    date                               null comment '计划起租日',
    rental_calc_type         varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                bigint                             null,
    create_time              datetime default CURRENT_TIMESTAMP null,
    update_by                bigint                             null,
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uniq_project_id
    unique (project_id) comment '每个项目只能有一份报价'
    )
    comment '债权转让报价方案表';

create table if not exists proj_review_aoc_price_lib
(
    id                       bigint auto_increment comment '立项基本信息表id	'
    primary key,
    project_id               bigint                             not null comment '所属项目ID',
    apply_credit_amount      bigint                             null comment '申报授信金额',
    aoc_credit_term          int                                null comment '转让额度有效期（月',
    repay_type               varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop       tinyint(1)                         null comment '额度是否可循环',
    earnest_money            bigint                             null comment '保证金',
    confirmatory_party       text                               null comment '确权方',
    consulting_fee           bigint                             null comment '服务费/咨询费',
    aoc_financing_proportion int                                null comment '转让融资比例',
    summary                  text                               null comment '拟转应收账款概述',
    rate_type                varchar(10)                        null comment '转让费率类型。固定利率：fixed、浮动利率：float	',
    aoc_rate_percent         int                                null comment '转让费率值。百分之多少',
    irr_percent              int                                null comment '内部收益率。百分之多少',
    planned_starting_date    date                               null comment '计划起租日',
    rental_calc_type         varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                bigint                             null,
    create_time              datetime default CURRENT_TIMESTAMP null,
    update_by                bigint                             null,
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version                  varchar(40)                        not null comment '版本号',
    origin_id                bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time         datetime                           null,
    data_create_by           bigint                             null,
    data_update_time         datetime                           null,
    data_update_by           bigint                             null
    )
    comment '债权转让报价方案lib表';

create index uniq_project_id
    on proj_review_aoc_price_lib (project_id)
    comment '每个项目只能有一份报价';

create table if not exists proj_review_base_info
(
    id                           bigint auto_increment
    primary key,
    client_id                    bigint                             null comment '客户id',
    biz_type                     varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    proj_name                    varchar(200)                       null comment '项目名称',
    project_type                 varchar(30)                        null comment '下拉框选项：公共事业类、省内国（央）企、其他。
内容决定后续审批流审批权限。
',
    project_classify             varchar(50)                        null comment '项目分类，可选项：鼓励类，适度支持类，谨慎类，工程机械类（厂商担保模式），集团内协同业务',
    approval_type                varchar(20)                        null comment '审批类型',
    proj_code                    varchar(20)                        null comment '项目编号',
    lease_types                  varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    factoring_types              varchar(100)                       null comment '保理类型。有追明保理、无追明保理、有追暗保理',
    proj_source                  varchar(20)                        null comment '存量翻单、渠道介绍、自主开发',
    funds_purpose                varchar(200)                       null comment '资金用途',
    proj_background              text                               null comment '项目背景',
    transferor_client_id         bigint                             null comment '转让方。（项目类型为租赁时）',
    lessee_info                  json                               null comment '承租人列表',
    creditor_client_id           bigint                             null comment '债权人id',
    creditor_stock_risk_exposure bigint                             null comment '债权人存量风险敞口',
    debtor_info                  json                               null comment '债务人信息。（项目类型为保理时）',
    guarantee_info               json                               null comment '担保人信息',
    pledgor_info                 json                               null comment '质押人信息',
    mortgagor_info               json                               null comment '抵押人信息',
    proj_sponsor_user_id         bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids      json                               null comment '项目协办方用户id列表',
    biz_dept_id                  bigint                             null comment '业务部门id',
    biz_dept_leader_id           bigint                             null comment '业务部门负责人id',
    biz_division_leader_id       bigint                             null comment '业务分管领导id',
    risk_control_manager_id      bigint                             null comment '风控经理id',
    legal_manager_user_id        bigint                             null,
    proj_review_status           varchar(50)                        null comment '立项状态',
    proj_review_process_status   varchar(50)                        null comment '流程状态',
    declared_amount              bigint                             null comment '冗余报价方案字段',
    proj_establish_id            bigint                             null comment '关联的立项ID',
    create_by                    bigint                             null comment '创建人、发起人',
    create_time                  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                    bigint                             null comment '最后更新人id',
    update_time                  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '立项基本信息表';

create table if not exists proj_review_base_info_lib
(
    id                           bigint auto_increment
    primary key,
    client_id                    bigint                             null comment '客户id',
    biz_type                     varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    proj_name                    varchar(200)                       null comment '项目名称',
    project_type                 varchar(30)                        null comment '下拉框选项：公共事业类、省内国（央）企、其他。
内容决定后续审批流审批权限。
',
    project_classify             varchar(50)                        null comment '项目分类，可选项：鼓励类，适度支持类，谨慎类，工程机械类（厂商担保模式），集团内协同业务',
    approval_type                varchar(20)                        null comment '审批类型',
    proj_code                    varchar(20)                        null comment '项目编号',
    lease_types                  varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    factoring_types              varchar(100)                       null comment '保理类型。有追明保理、无追明保理、有追暗保理',
    proj_source                  varchar(20)                        null comment '存量翻单、渠道介绍、自主开发',
    funds_purpose                varchar(200)                       null comment '资金用途',
    proj_background              text                               null comment '项目背景',
    transferor_client_id         bigint                             null comment '转让方。（项目类型为租赁时）',
    lessee_info                  json                               null comment '承租人列表',
    creditor_client_id           bigint                             null comment '债权人id',
    creditor_stock_risk_exposure bigint                             null comment '债权人存量风险敞口',
    debtor_info                  json                               null comment '债务人信息。（项目类型为保理时）',
    guarantee_info               json                               null comment '担保人信息',
    pledgor_info                 json                               null comment '质押人信息',
    mortgagor_info               json                               null comment '抵押人信息',
    proj_sponsor_user_id         bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids      json                               null comment '项目协办方用户id列表',
    create_by                    bigint                             null comment '创建人、发起人',
    biz_dept_id                  bigint                             null comment '业务部门id',
    biz_dept_leader_id           bigint                             null comment '业务部门负责人id',
    biz_division_leader_id       bigint                             null comment '业务分管领导id',
    risk_control_manager_id      bigint                             null comment '风控经理id',
    legal_manager_user_id        bigint                             null,
    proj_review_status           varchar(50)                        null comment '立项状态',
    proj_review_process_status   varchar(50)                        null comment '流程状态',
    declared_amount              bigint                             null comment '冗余报价方案字段',
    proj_establish_id            bigint                             null comment '关联的立项ID',
    create_time                  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                    bigint                             null comment '最后更新人id',
    update_time                  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version                      varchar(40)                        not null comment '版本号',
    origin_id                    bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time             datetime                           null,
    data_create_by               bigint                             null,
    data_update_time             datetime                           null,
    data_update_by               bigint                             null
    )
    comment '评审基本信息表';

create table if not exists proj_review_cash_flow_plan
(
    id                  bigint unsigned auto_increment comment '现金流量明细表id'
    primary key,
    project_id          bigint                             not null comment '所属项目评审记录ID',
    cash_flow_date      date                               null comment '日期',
    cash_flow_phase     int(10)                            null comment '期项',
    cash_flow_amount    bigint                             null comment '现金流金额',
    rent                bigint                             null comment '租金',
    principal           bigint                             null comment '本金',
    interest            bigint                             null comment '利息',
    remaining_principal bigint                             null comment '剩余本金',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	'
    )
    comment '项目评审-现金流计划表';

create table if not exists proj_review_cash_flow_plan_lib
(
    id                  bigint auto_increment comment '现金流量明细表id'
    primary key,
    project_id          bigint                             not null comment '所属项目评审记录ID',
    cash_flow_date      date                               null comment '日期',
    cash_flow_phase     int(10)                            null comment '期项',
    cash_flow_amount    bigint                             null comment '现金流金额',
    rent                bigint                             null comment '租金',
    principal           bigint                             null comment '本金',
    interest            bigint                             null comment '利息',
    remaining_principal bigint                             null comment '剩余本金',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    version             varchar(40)                        not null comment '版本号',
    origin_id           bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time    datetime                           null,
    data_create_by      bigint                             null,
    data_update_time    datetime                           null,
    data_update_by      bigint                             null
    )
    comment '项目评审-现金流计划版本表';

create table if not exists proj_review_factoring_price
(
    id                             bigint auto_increment comment '立项基本信息表id	'
    primary key,
    project_id                     bigint                             not null comment '所属项目ID',
    apply_credit_amount            bigint                             null comment '申报授信金额',
    factoring_credit_term          int                                null comment '保理额度有效期（月',
    repay_type                     varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    earnest_money                  bigint                             null comment '保证金',
    confirmatory_party             text                               null comment '确权方',
    factoring_financing_proportion int                                null comment '保理融资比例',
    consulting_fee                 bigint                             null comment '服务费/咨询费',
    summary                        text                               null comment '拟转应收账款概述',
    rate_type                      varchar(10)                        null comment '保理费率类型。固定利率：fixed、浮动利率：float	',
    factoring_rate_percent         int                                null comment '保理费率值。百分之多少',
    irr_percent                    int                                null comment '内部收益率。百分之多少',
    existing_lease_credit          bit      default b'0'              null comment '是否有存续租赁授信',
    new_lease_credit               bit      default b'0'              null comment '是否新增租赁授信',
    planned_starting_date          date                               null comment '计划起租日',
    rental_calc_type               varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                      bigint                             null,
    create_time                    datetime default CURRENT_TIMESTAMP null,
    update_by                      bigint                             null,
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uniq_project_id
    unique (project_id) comment '每个立项只能有一份报价'
    )
    comment '保理报价方案表';

create table if not exists proj_review_factoring_price_lib
(
    id                             bigint auto_increment comment '立项基本信息表id	'
    primary key,
    project_id                     bigint                             not null comment '所属项目ID',
    apply_credit_amount            bigint                             null comment '申报授信金额',
    factoring_credit_term          int                                null comment '保理额度有效期（月',
    repay_type                     varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    earnest_money                  bigint                             null comment '保证金',
    confirmatory_party             text                               null comment '确权方',
    factoring_financing_proportion int                                null comment '保理融资比例',
    consulting_fee                 bigint                             null comment '服务费/咨询费',
    summary                        text                               null comment '拟转应收账款概述',
    rate_type                      varchar(10)                        null comment '保理费率类型。固定利率：fixed、浮动利率：float	',
    factoring_rate_percent         int                                null comment '保理费率值。百分之多少',
    irr_percent                    int                                null comment '内部收益率。百分之多少',
    existing_lease_credit          bit      default b'0'              null comment '是否有存续租赁授信',
    new_lease_credit               bit      default b'0'              null comment '是否新增租赁授信',
    planned_starting_date          date                               null comment '计划起租日',
    rental_calc_type               varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                      bigint                             null,
    create_time                    datetime default CURRENT_TIMESTAMP null,
    update_by                      bigint                             null,
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version                        varchar(40)                        not null comment '版本号',
    origin_id                      bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time               datetime                           null,
    data_create_by                 bigint                             null,
    data_update_time               datetime                           null,
    data_update_by                 bigint                             null
    )
    comment '保理报价方案表';

create index uniq_project_id
    on proj_review_factoring_price_lib (project_id)
    comment '每个立项只能有一份报价';

create table if not exists proj_review_lease_price
(
    id                    bigint auto_increment comment '立项基本信息表id'
    primary key,
    project_id            bigint                             not null comment '所属项目ID',
    apply_credit_amount   bigint                             null comment '申报授信金额',
    lease_month_count     int                                null comment '租赁期限月数',
    repay_times_yearly    int                                null comment '每年还款次数',
    repay_times_total     int                                null comment '还款期数',
    repay_rate            varchar(20)                        null comment '还款频率',
    pay_type              varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type      varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    credit_amount_loop    tinyint(1)                         null comment '额度是否可循环',
    earnest_money         bigint                             null comment '保证金',
    down_payment          bigint                             null comment '首付款',
    consulting_fee        bigint                             null comment '服务费/咨询费',
    nominal_price         bigint                             null comment '名义货价',
    rate_type             varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lease_rate_percent    int                                null comment '租赁利率值。百分之多少',
    irr_percent           int                                null comment '内部收益率。百分之多少',
    planned_starting_date date                               null comment '计划起租日',
    create_by             bigint                             null comment '创建人id、发起人id	',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by             bigint                             null comment '最后更新人id	',
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    constraint uniq_project_id
    unique (project_id) comment '每个立项只能有一份报价'
    )
    comment '租赁报价方案表';

create table if not exists proj_review_lease_price_lib
(
    id                    bigint auto_increment comment '立项基本信息表id'
    primary key,
    project_id            bigint                             not null comment '所属项目ID',
    apply_credit_amount   bigint                             null comment '申报授信金额',
    lease_month_count     int                                null comment '租赁期限月数',
    repay_times_yearly    int                                null comment '每年还款次数',
    repay_times_total     int                                null comment '还款期数',
    repay_rate            varchar(20)                        null comment '还款频率',
    pay_type              varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type      varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    credit_amount_loop    tinyint(1)                         null comment '额度是否可循环',
    earnest_money         bigint                             null comment '保证金',
    down_payment          bigint                             null comment '首付款',
    consulting_fee        bigint                             null comment '服务费/咨询费',
    nominal_price         bigint                             null comment '名义货价',
    rate_type             varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lease_rate_percent    int                                null comment '租赁利率值。百分之多少',
    irr_percent           int                                null comment '内部收益率。百分之多少',
    planned_starting_date date                               null comment '计划起租日',
    create_by             bigint                             null comment '创建人id、发起人id	',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by             bigint                             null comment '最后更新人id	',
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    version               varchar(40)                        not null comment '版本号',
    origin_id             bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time      datetime                           null,
    data_create_by        bigint                             null,
    data_update_time      datetime                           null,
    data_update_by        bigint                             null
    )
    comment '租赁报价方案表';

create index uniq_project_id
    on proj_review_lease_price_lib (project_id)
    comment '每个立项只能有一份报价';

CREATE TABLE if not exists `contract_pledge_item` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) unsigned NOT NULL COMMENT '合同id',
  `pledge_id` bigint(20) unsigned NOT NULL COMMENT '质押措施id',
  `sequence` int(10) DEFAULT NULL COMMENT '序号',
  `category` varchar(100) DEFAULT NULL COMMENT '种类',
  `unique_identify_code` varchar(1000) DEFAULT NULL COMMENT '唯一识别号',
  `unique_identify_code_type` varchar(100) DEFAULT NULL COMMENT '识别号类型',
  `name` varchar(100) DEFAULT '' COMMENT '设备名称',
  `supplier` varchar(100) DEFAULT '' COMMENT '供应商',
  `quantity` varchar(20) DEFAULT '' COMMENT '数量',
  `unit` varchar(10) DEFAULT '' COMMENT '计量单位',
  `purchase_date` varchar(50) DEFAULT '' COMMENT '购置日期',
  `original_book_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面原值',
  `original_book_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面净值',
  `assessed_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估原值',
  `assessed_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估净值',
  `invoice_code` varchar(1000) DEFAULT '' COMMENT '发票号',
  `storage_place` varchar(100) DEFAULT '' COMMENT '存放地点',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同明细-质押物清单';

CREATE TABLE if not exists `contract_pledge_item_lib` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) unsigned NOT NULL COMMENT '合同id',
  `pledge_id` bigint(20) unsigned NOT NULL COMMENT '质押措施id',
  `sequence` int(10) DEFAULT NULL COMMENT '序号',
  `category` varchar(100) DEFAULT NULL COMMENT '种类',
  `unique_identify_code` varchar(1000) DEFAULT NULL COMMENT '唯一识别号',
  `unique_identify_code_type` varchar(100) DEFAULT NULL COMMENT '识别号类型',
  `name` varchar(100) DEFAULT '' COMMENT '设备名称',
  `supplier` varchar(100) DEFAULT '' COMMENT '供应商',
  `quantity` varchar(20) DEFAULT '' COMMENT '数量',
  `unit` varchar(10) DEFAULT '' COMMENT '计量单位',
  `purchase_date` varchar(50) DEFAULT '' COMMENT '购置日期',
  `original_book_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面原值',
  `original_book_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面净值',
  `assessed_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估原值',
  `assessed_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估净值',
  `invoice_code` varchar(1000) DEFAULT '' COMMENT '发票号',
  `storage_place` varchar(100) DEFAULT '' COMMENT '存放地点',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同明细-质押物清单版本';

