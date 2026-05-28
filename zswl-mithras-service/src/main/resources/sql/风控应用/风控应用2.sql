create table if not exists public_outer_info_record
(
    id                   bigint auto_increment comment '主键ID'
    primary key,
    public_info_query_id bigint                               not null comment '公开信息ID',
    config_key           varchar(64)                          not null comment '关联配置表的KEY',
    `index`              int                                  null comment '序号',
    query_result         mediumtext                           null comment '查询结果',
    create_by            bigint                               null comment '创建人',
    update_by            bigint                               null comment '更新人',
    create_time          datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time          datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted              tinyint(1) default 0                 null comment '逻辑删除',
    version              int                                  null comment '版本号'
    )
    comment '外部公开信息查询报告表';


INSERT INTO bifrost_menu (code, level, sort_no, type, path, parent_id, icon, name) VALUES ('customerMonitoring', 1, 3, '0', '/customerMonitoring', null, null, '客户监控');
INSERT INTO bifrost_custom_tree_menu_ref (custom_tree_id, menu_id, sort_no)
select a.tree_id, b.menu_id, 1
from (select id as tree_id from bifrost_custom_tree where name = '风控管理') a
         join (select id as menu_id from bifrost_menu where code = 'customerMonitoring') b;

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitorlist', '客户监控列表', 0, id, 'POST', '/clientMonitor/list', null
from bifrost_menu
where code = 'customerMonitoring';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitorstatistic', '客户监控统计', 0, id, 'POST', '/clientMonitor/statistic', null
from bifrost_menu
where code = 'customerMonitoring';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitorrisklinechart', '风险客户数量折线图', 0, id, 'POST', '/clientMonitor/risk/linechart', null
from bifrost_menu
where code = 'customerMonitoring';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitorriskpiechart', '客户监控饼图', 0, id, 'POST', '/clientMonitor/risk/piechart', null
from bifrost_menu
where code = 'customerMonitoring';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitordetailwarn', '客户监控预警详情', 0, id, 'POST', '/clientMonitor/detail/warn', null
from bifrost_menu
where code = 'customerMonitoring';
INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT 'clientMonitordetailopinion', '客户监控舆情详情', 0, id, 'POST', '/clientMonitor/detail/opinion', null
from bifrost_menu
where code = 'customerMonitoring';


INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT '/outer/public/query', '外部公开信息查询', 0, id, 'POST', '/outer/public/query', null
from bifrost_menu
where code = 'QX0113';


UPDATE public_info_config SET description = '确认是否存在：
1、<span style=\'padding:2px;background:#e5ecfb;color:#2c5ee0; border:1px solid #2c5ee0;\>系统取数</span>重大行政处罚（含重大安全事故，罚金＞500万或对经营生产产生重大影响）
2、重大环保处罚（罚金＞500万或对经营生产产生重大影响）
3、<span style=\padding:2px;background:#e5ecfb;color:#2c5ee0; border:1px solid #2c5ee0;\>系统取数</span>重大经营异常
4、<span style=\padding:2px;background:#e5ecfb;color:#2c5ee0; border:1px solid #2c5ee0;\>系统取数</span>任何欠税公告
5、注销备案
6、任何拖欠员工工资（劳动仲裁）
7、上市公司涉及重大监管处罚
8、其他' WHERE id = 3;
UPDATE public_info_config SET description = '确认是否存在：
1、<span style=\'padding:2px;background:#e5ecfb;color:#2c5ee0; border:1px solid #2c5ee0;\'>系统取数</span>股权冻结
2、<span style=\'padding:2px;background:#e5ecfb;color:#2c5ee0; border:1px solid #2c5ee0;\'>系统取数</span>重大诉讼案件（原告为金融机构或非金融机构但涉案金额单一或累计在3000万以上或涉及刑事诉讼）
3、<span style=\'padding:2px;background:#e5ecfb;color:#2c5ee0; border:1px solid #2c5ee0;\'>系统取数</span>重大被执行案件（原告为金融机构或非金融机构但涉案金额单一或累计在3000万以上或涉及刑事诉讼）
4、<span style=\'padding:2px;background:#e5ecfb;color:#2c5ee0; border:1px solid #2c5ee0;\'>系统取数</span>失信被执行
5、<span style=\'padding:2px;background:#e5ecfb;color:#2c5ee0; border:1px solid #2c5ee0;\'>系统取数</span>限高、限制出境
6、<span style=\'padding:2px;background:#e5ecfb;color:#2c5ee0; border:1px solid #2c5ee0;\'>系统取数</span>司法拍卖
7、上市公司实际控制人或财务总监涉及刑事案件
8、上市公司被监管机构或交易所处罚
9、生产型企业涉及批量起诉
10、其他' WHERE id = 4;