create table if not exists address_dictionary
(
    id        bigint auto_increment
    primary key,
    code      varchar(20)     null comment '编号',
    display   varchar(50)     null comment '显示名称',
    parent_id bigint          null comment '父级id',
    sort      int default 100 null,
    level     int default 1   null
    )
    comment '地址字典表';

create table if not exists base_data_bank_account
(
    id             bigint auto_increment
    primary key,
    account_type   varchar(50)                           not null comment '账户类型，BASE-基本户，NORMAL-一般户',
    account_name   varchar(100)                          not null comment '账户名称',
    account_number varchar(30) default ''                not null comment '账号',
    account_bank   varchar(100)                          not null comment '支行名称',
    create_time    datetime    default CURRENT_TIMESTAMP not null,
    create_by      bigint                                null comment '创建人id',
    update_time    datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    update_by      bigint                                null
    )
    comment '基础数据-我方账户';

create table if not exists base_data_lpr
(
    id          bigint auto_increment
    primary key,
    lpr_date    date                               not null comment 'LPR报价日',
    one_year    varchar(10)                        not null comment '1年期，单位：百分比',
    five_year   varchar(10)                        not null comment '5年期，单位：百分比',
    create_time datetime default CURRENT_TIMESTAMP not null,
    create_by   bigint                             null comment '创建人id',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    update_by   bigint                             null
    )
    comment '基础数据-lpr';

create table if not exists general_dictionary
(
    id        bigint auto_increment
    primary key,
    dict_key  varchar(20)    null comment '字典标识',
    dict_desc varchar(20)    null comment '字典说明',
    code      varchar(20)    null comment '标识编码',
    display   varchar(200)   null comment '显示名称',
    sort      int default 10 null
    )
    comment '通用字典表';

CREATE TABLE if not exists `base_data_special_date` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `special_type` varchar(20) NOT NULL DEFAULT '' COMMENT '特殊类型，HOLIDAY-节假日，WORKDAY-工作日',
  `year` int(10) NOT NULL COMMENT '年份',
  `month` int(10) NOT NULL COMMENT '月份',
  `special_date` date NOT NULL COMMENT '特殊日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基础数据-特殊日期';

