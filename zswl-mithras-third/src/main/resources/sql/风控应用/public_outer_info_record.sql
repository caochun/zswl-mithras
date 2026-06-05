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

INSERT INTO bifrost_function (code, name, sort_no, menu_id, method, path, type)
SELECT '/outer/public/query', '外部公开信息查询', 0, id, 'POST', '/outer/public/query', null
from bifrost_menu
where code = 'QX0113';
