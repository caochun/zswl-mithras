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

