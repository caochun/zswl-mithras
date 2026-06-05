drop table risk_control_related_transaction_submission;

-- auto-generated definition
create table risk_control_gljy_report
(
    id                         bigint auto_increment comment '主键id'
        primary key,
    amount                     bigint                             null comment '关联交易金额（万）',
    description                varchar(500)                       null comment '描述',
    important_reason           varchar(500)                       null,
    level                      varchar(20)                        null,
    opinion                    varchar(200)                       null comment '意见',
    purpose                    varchar(500)                       null comment '交易目的',
    risk                       varchar(200)                       null comment '风险、影响',
    trade_category_parent_name varchar(50)                        null comment '一级分类',
    trade_category_name        varchar(20)                        null comment '二级分类',
    trade_date                 date                               null comment '交易日期',
    trade_party_assets         bigint                             null comment '交易对手上一年度末审计净资产',
    trade_party_name           varchar(50)                        null comment '交易对手名称',
    subject_party_name         varchar(200)                       null comment '主体机构名称',
    report_status              varchar(20)                        null comment '报送状态',
    create_time                datetime default CURRENT_TIMESTAMP null,
    update_time                datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    comment '关联交易报送';

-- 菜单调整
delete from bifrost_role_menu_function where function_id IN(select id from bifrost_function where name like '关联交易报送%');
delete from bifrost_org_menu_function where function_id IN (select id from bifrost_function where name like '关联交易报送%');
delete from bifrost_function where name like '关联交易报送%';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select  'riskcontrolgljyreportadd', '关联交易记录-手动新增', 0, a.id, 'POST', '/risk/control/gljy/report/add', 2, b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '金控系统报送'
  and b.group_describe = '金控系统报送-编辑';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'riskcontrolgljyreportremove', '关联交易记录-删除', 0, a.id, 'POST', '/risk/control/gljy/report/remove', 2, b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '金控系统报送'
  and b.group_describe = '金控系统报送-编辑';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'riskcontrolgljyreportmodify', '关联交易记录-修改', 0, a.id, 'POST', '/risk/control/gljy/report/modify', 2, b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '金控系统报送'
  and b.group_describe = '金控系统报送-编辑';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'riskcontrolgljyreportlist', '关联交易记录-列表', 0, a.id, 'POST', '/risk/control/gljy/report/list', 1, b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '金控系统报送'
  and b.group_describe = '金控系统报送-查看';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'riskcontrolgljyreportsubmit', '关联交易记录-报送', 0, a.id, 'POST', '/risk/control/gljy/report/submit', 2, b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '金控系统报送'
  and b.group_describe = '金控系统报送-编辑';

insert into bifrost_function (code, name, sort_no, menu_id, method, path, type, group_id)
select 'riskcontrolgljyreportrelatedclients', '关联交易记录-关联方查询', 0, a.id, 'POST', '/risk/control/gljy/report/related/clients', 1, b.id
from bifrost_menu a,
     gruul_function_group b
where a.name = '金控系统报送'
  and b.group_describe = '金控系统报送-查看';



-- 定时任务，同步名单