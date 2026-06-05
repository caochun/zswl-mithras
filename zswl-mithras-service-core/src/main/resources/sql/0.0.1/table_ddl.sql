create table if not exists common_version
(
    id          bigint auto_increment comment '主键'
    primary key,
    main_id     bigint                             not null comment '立项id',
    version     varchar(32)                        not null comment '版本',
    type        tinyint                            not null comment '版本类型（1直接生效，2审批通过生效）',
    module      varchar(50)                        null,
    create_time datetime default CURRENT_TIMESTAMP null,
    create_by   bigint                             null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by   bigint                             null
    )
    comment '立项信息版本表';

create index idx_proj_establish_id
    on common_version (main_id);

