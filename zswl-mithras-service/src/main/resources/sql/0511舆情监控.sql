alter table risk_control_opinion_monitor
    add warn_star int null comment '预警星级；1：一星；2：二星；3：三星' after major_org_code;

alter table risk_control_opinion_monitor
    add warn_level int null comment '预警信号：1：绿灯；2：黄灯；3：红灯' after warn_star;

alter table risk_control_opinion_monitor
    modify emotion varchar(50) null comment '情感方向' after warn_level;

alter table risk_control_opinion_monitor
    modify importance varchar(50) null comment '情感重要度' after emotion;

alter table risk_control_opinion_monitor
    modify major_org_code varchar(255) null comment '主体机构名称' after link_address;

alter table risk_control_opinion_monitor
    modify credit_code varchar(50) null comment '统一社会信用代码' after title;
alter table risk_control_opinion_monitor
    add handle_status varchar(20) null comment '处理状态' after warn_level;

alter table risk_control_opinion_monitor
    add advisement longtext null comment '处置意见' after handle_status;

-- todo 1、舆情处置审批流配置；2、舆情处置审批人要有舆情列表接口的功能权限；3、同步全量关注客户信息给风控系统-定时任务





