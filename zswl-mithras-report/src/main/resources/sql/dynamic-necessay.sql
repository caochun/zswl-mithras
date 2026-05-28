-- 指标动态必填api
alter table risk_metric
drop
column need_report;

alter table risk_metric_value
    add need_report tinyint(1) default 0 null after status;

-- auto-generated definition
create table risk_metric_factor_file
(
    id          bigint auto_increment
        primary key,
    sheet_name  varchar(50) null comment 'excel sheet名称',
    sheet_date  date null comment 'sheet数据的时间节点',
    file_id     bigint null comment 'materials表id',
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint risk_metric_factor_sheet__index
        unique (sheet_name, sheet_date)
);
-- alter table risk_metric_factor
--     add factor_json json null after factor_value;



-- create table guanyuan_col_assist
-- (
--     id          bigint auto_increment,
--     col_name    varchar(200) null,
--     sort        int          null,
--     month       varchar(10)  null,
--     report_name varchar(50)  null,
--     constraint guanyuan_col_assist_pk
--         primary key (id)
-- );
-- create unique index guanyuan_col_assist_id_uindex
--     on guanyuan_col_assist (id);



