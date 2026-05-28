-- 集中度报送-表创建

-- auto-generated definition
create table risk_control_jzd_report
(
    id                   bigint auto_increment
        primary key,
    biz_type             varchar(50)                        null,
    data_month           date                               null,
    target_subject       varchar(50)                        null,
    biz_amount_total     bigint                             null,
    biz_amount_left      bigint                             null,
    client_name          varchar(50)                        null,
    client_same_trade    varchar(10)                        null,
    economic_composition varchar(50)                        null,
    sponsor_org_name     varchar(50)                        null,
    biz_start_date       date                               null,
    biz_end_date         date                               null,
    ensure_value         bigint                             null,
    guarantee_name       varchar(50)                        null,
    yjtjz_value          bigint                             null,
    overdue_days         int                                null,
    overdue_value        bigint                             null,
    assets_category      varchar(50)                        null,
    create_type          varchar(50)                        null,
    report_status        varchar(20)                        null,
    proj_review_id       bigint                             null,
    create_time          datetime default CURRENT_TIMESTAMP null,
    update_time          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);






-- 集中度特殊配置
INSERT INTO mithras.bifrost_system_config (gmt_create, gmt_modified, config_key, config_value, created_by, updated_by, description, status, type)
 VALUES (DEFAULT, DEFAULT, 'jzd_report_special_config', '{"suspiciousClientName":["河南骏化发展股份有限公司"]}', 'admin', 'admin', '集中度报送特殊配置', 1, 'Json')


-- 定时任务，季度报送
