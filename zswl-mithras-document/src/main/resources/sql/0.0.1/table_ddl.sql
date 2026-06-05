create table if not exists materials_list
(
    id                 bigint auto_increment
    primary key,
    belong_id          bigint                             null comment '客户id',
    materials_type     varchar(50)                        null comment '资料类型',
    materials_sub_type varchar(50)                        null comment '资料子类型',
    business_type      varchar(50)                        null comment '业务类型',
    oss_filename       varchar(100)                       null comment 'oss文件名',
    suffix             varchar(100)                       null comment '文件后缀',
    filename           varchar(100)                       null comment '附件名',
    file_path          varchar(200)                       null comment '文件url',
    create_time        datetime default CURRENT_TIMESTAMP null,
    create_by          bigint                             null,
    update_time        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by          bigint                             null
    )
    comment '资料清单';

create table if not exists onlyoffice_key_store
(
    id                bigint auto_increment comment '主键'
    primary key,
    file_oss_path     varchar(256)                       not null comment '文件oss路径',
    file_oss_path_md5 varchar(32)                        not null comment '文件oss路径 md5值',
    file_key          varchar(32)                        not null comment '随机生成的key，文件未改动前不变',
    create_time       datetime default CURRENT_TIMESTAMP null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment 'onlyoffice文件key存档';

