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

