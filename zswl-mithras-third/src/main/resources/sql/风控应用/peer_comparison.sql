create table peer_comparison_ready
(
    busi_date varchar(32) null comment '财年',
    id        bigint auto_increment
        primary key,
    constraint peer_comparison_ready_busi_date_uindex
        unique (busi_date)
);

create table peer_comparison_index
(
    id              bigint auto_increment
        primary key,
    enterprise_code varchar(32) null comment '企业code',
    enterprise_name varchar(32) null comment '企业名称',
    busi_date       varchar(32) null comment '财年',
    type            varchar(32) null comment '类型 HB HBTZ',
    roa             varchar(32) null comment '资产收益率',
    roe             varchar(32) null comment '净资产收益率',
    total_assets    varchar(32) null comment '总资产',
    net_assets      varchar(32) null comment '净资产',
    net_profit      varchar(32) null comment '净利润',
    leverage_ratio  varchar(32) null comment '杠杆率',
    average_roa     varchar(32) null comment '行业平均值',
    total_liab      varchar(32) null comment '总负债'
)
    comment '租赁同业分析指标';
