create table if not exists data_share_manager
(
    id          bigint auto_increment
    primary key,
    model_name  varchar(30)                        null comment '模块',
    start_time  datetime                           null comment '开始时间',
    end_time    datetime                           null comment '结束时间',
    page_size   int(20)                            null comment '页大小',
    page_num    int(20)                            null comment '页码数',
    data_total  int(20)                            null comment '数据总量',
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null
    )
    comment '数据分享-进度管理表';

create table if not exists data_share_merchants
(
    client_id             bigint       not null comment '客商ID'
    primary key,
    name                  varchar(50)  null comment '客商名称',
    short_name            varchar(50)  null comment '客商简称',
    english_name          varchar(500) null comment '英文名称',
    english_short_name    varchar(100) null comment '英文简称',
    is_internal_unit      tinyint(1)   null comment '是否为内部单位 0否，1是',
    customer_type         int(3)       null comment '客户类型 01 企业 02 个体工商户 03 农民专业合作社 04 政府机关 05 事业单位 06 社会团体 07 民办非企业单位 08 司法行政 09 外国企业 10 军队 11 个人 12 临时客商',
    credit_code           varchar(20)  null comment '统一社会信用代码',
    national_org_code     varchar(32)  null comment '全国组织机构代码',
    tax_no                varchar(32)  null comment '税务登记证号',
    business_registration varchar(32)  null comment '工商登记号',
    dunbar_code           varchar(50)  null comment '邓白氏编码',
    identification_number varchar(20)  null comment '身份证号',
    region                varchar(50)  null comment '国家',
    province              varchar(50)  null comment '省份',
    city                  varchar(50)  null comment '城市',
    legal_name            varchar(50)  null comment '法人姓名',
    registered_address    varchar(1000) null comment '注册地址',
    registered_capital    varchar(20)  null comment '注册资本',
    contact_name          varchar(50)  null comment '联系人姓名',
    contact_tel           varchar(20)  null comment '联系人电话',
    enclosure             varchar(100) null comment '附件',
    is_customer           tinyint(1)   null comment '是否客户 0否，1是',
    is_supplier           tinyint(1)   null comment '是否供应商 0否，1是',
    capital_currency      varchar(20)  null comment '注册资本币种',
    natural_key           varchar(50)  null comment '业务主键',
    source                varchar(50)  null comment '来源系统',
    internal_unit         varchar(30)  null comment '内部单位',
    status                tinyint(1)   null comment '状态 0 失效 1 有效',
    mdm_create_number     varchar(50)  null comment '主数据创建人名称',
    mdm_create_name       varchar(50)  null comment '主数据创建人账号',
    mdm_create_org        varchar(50)  null comment '主数据创建人组织机构',
    mdm_create_org_name   varchar(50)  null comment '主数据创建人组织机构名称',
    mdm_create_time       datetime     null comment '主数据创建时间',
    operation_time        datetime     null comment '操作时间',
    create_time           datetime     null comment '创建时间',
    update_time           datetime     null comment '修改时间'
    )
    comment '数据分享-客商信息维护';

create index normal_contact_credit_code
    on data_share_merchants (credit_code);

