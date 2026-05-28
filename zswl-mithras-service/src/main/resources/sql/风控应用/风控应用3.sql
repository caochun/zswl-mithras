
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


INSERT INTO dashboard_config (dashboard_key, dashboard_display, order_num, deleted, create_by, create_time, update_by, update_time) VALUES ('IndustryIndexComparison ', '行业指标对比', 600, 0, null, '2024-05-15 19:33:38', null, '2024-05-18 23:33:57');

INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by, en_name, method, path, type, group_id)
VALUES ('riskWarnMonitorWarnListCustomerView', '监控预警-预警列表-统一客户视图', 0, (select id from bifrost_menu where code = 'customerView'), null, null, null, 'POST',
        '/risk/warn/monitor/warn/list', 2, null);




