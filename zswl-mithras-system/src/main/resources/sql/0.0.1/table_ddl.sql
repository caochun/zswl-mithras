create table if not exists bifrost_custom_tree
(
    gmt_create   timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    id           bigint auto_increment comment '主键'
    primary key,
    code         varchar(128)                        not null comment '自定义分组标志',
    name         varchar(128)                        null comment '名称',
    flag         int       default 0                 null comment '开关,0:关;1:开',
    sort_no      int                                 null comment '分组排序，ASC',
    parent_id    bigint                              null comment '上级id',
    create_by    varchar(30)                         null comment '创建用户账号',
    update_by    varchar(30)                         null comment '修改用户账号',
    en_name      varchar(64)                         null comment '英文名称',
    icon         varchar(512)                        null comment '图标',
    path         varchar(255)                        null comment '跳转路径',
    lang_env     varchar(128)                        null comment '语言环境，多种语言切换用“,”隔开：cn,en	',
    constraint uk_code
    unique (code)
    )
    comment '解决方案自定义分组' charset = utf8;

create index idx_sort_no
    on bifrost_custom_tree (sort_no);

create table if not exists bifrost_custom_tree_menu_ref
(
    id             bigint unsigned auto_increment comment 'id'
    primary key,
    gmt_create     timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified   timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    create_by      varchar(30)                         null comment '创建用户账号',
    update_by      varchar(30)                         null comment '修改用户账号',
    custom_tree_id bigint                              not null comment 'custom_tree_uuid',
    menu_id        bigint                              not null comment 'menu_uuid',
    sort_no        int                                 null comment '菜单排序，ASC',
    constraint uk_custom_tree_uuid_menu_uuid
    unique (custom_tree_id, menu_id)
    )
    comment '自定义分组菜单关系表' charset = utf8;

create index idx_sort_no
    on bifrost_custom_tree_menu_ref (sort_no);

create table if not exists bifrost_function
(
    gmt_create   timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    id           bigint auto_increment comment '主键 唯一'
    primary key,
    code         varchar(128)                        null comment '功能标志',
    name         varchar(30)                         null comment '功能名称',
    sort_no      int       default 0                 null comment '排序',
    menu_id      bigint                              not null comment '菜单标志',
    create_by    varchar(30)                         null comment '创建用户账号',
    update_by    varchar(30)                         null comment '修改用户账号',
    en_name      varchar(64)                         null comment '英文名称',
    method       varchar(16)                         null comment '功能method',
    path         varchar(255)                        null comment '功能路径',
    constraint uk_un_code
    unique (code)
    )
    comment '功能' charset = utf8;

create index idx_uuid
    on bifrost_function (menu_id);

create table if not exists bifrost_menu
(
    gmt_create   timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    code         varchar(128)                          not null comment '菜单标志 唯一 预留license',
    level        int         default 1                 not null comment '级别',
    sort_no      int         default 0                 null comment '排序',
    type         varchar(2)  default '0'               null comment '类型，枚举：0:未知、1:project、2:group、3:menu',
    path         varchar(255)                          null comment '请求路径',
    parent_id    bigint                                null comment '上级菜单标志',
    icon         varchar(255)                          null comment '图标码',
    name         varchar(40)                           null comment '名称',
    id           bigint auto_increment comment '主键'
    primary key,
    en_name      varchar(64)                           null comment '英文名称',
    target       varchar(16) default '_self'           null comment '菜单链接打开方式：_blank、_self、_parent、_top	',
    create_by    varchar(30)                           null comment '创建人',
    update_by    varchar(30)                           null comment '修改人',
    constraint uk_code
    unique (code)
    )
    comment '菜单' charset = utf8;

create table if not exists bifrost_org
(
    gmt_create   timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    id           bigint auto_increment comment 'id'
    primary key,
    code         varchar(128)                        not null comment '机构号唯一',
    level        int       default 1                 not null comment '机构级别：1、2、3',
    parent_id    bigint                              null comment '上级机构id 可为null',
    name         varchar(80)                         null comment '机构名称',
    create_by    varchar(30)                         null comment '创建用户账号',
    update_by    varchar(30)                         null comment '修改用户账号',
    en_name      varchar(80)                         null comment '机构英文名称',
    type         int                                 null comment '部门类型：0 公司， 1 业务部门，2 领导层',
    constraint uk_code
    unique (code)
    )
    comment '机构' charset = utf8;

create table if not exists bifrost_org_menu_function
(
    gmt_create     timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified   timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    org_id         bigint                              not null comment '机构号',
    menu_id        bigint                              not null comment '可用菜单id',
    function_id    bigint                              null comment '可用功能id',
    create_by      varchar(30)                         null comment '创建用户账号',
    update_by      varchar(30)                         null comment '修改用户账号',
    custom_tree_id bigint                              not null comment 'custom tree id',
    id             bigint auto_increment comment '主键'
    primary key
    )
    comment '菜单功能关系表' charset = utf8;

create index idx_custom_tree_uuid
    on bifrost_org_menu_function (custom_tree_id);

create table if not exists bifrost_role
(
    gmt_create   timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    id           bigint auto_increment comment '主键'
    primary key,
    code         varchar(128)                          not null comment '角色标志 唯一',
    name         varchar(40)                           not null comment '角色名称',
    type         varchar(10) default 'default'         not null comment '角色类型 同机构下层次',
    org_id       bigint                                not null comment '所属机构',
    create_by    varchar(30)                           null comment '创建用户账号',
    update_by    varchar(30)                           null comment '修改用户账号',
    en_name      varchar(80)                           null comment '英文机构名称'
    )
    comment '角色表' charset = utf8;

create table if not exists bifrost_role_menu_function
(
    gmt_create     timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified   timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    id             bigint auto_increment comment 'Ora主键'
    primary key,
    role_id        bigint                              not null comment '角色',
    menu_id        bigint                              null comment '可用菜单id',
    function_id    bigint                              null comment '可用功能id',
    create_by      varchar(30)                         null comment '创建用户账号',
    update_by      varchar(30)                         null comment '修改用户账号',
    custom_tree_id bigint                              not null comment 'custom tree id'
    )
    comment '角色和菜单、功能关系表' charset = utf8;

create index idx_role_uuid
    on bifrost_role_menu_function (role_id);

create table if not exists bifrost_salt
(
    id         bigint auto_increment comment '主键'
    primary key,
    salt       varchar(32)                        not null comment '盐值',
    expiration datetime                           null comment '过期时间',
    gmt_create datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
    )
    comment 'bifrost登陆code表' charset = utf8;

create index idx_salt
    on bifrost_salt (salt);

create table if not exists bifrost_system_config
(
    id           bigint auto_increment comment 'id'
    primary key,
    gmt_create   timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    config_key   varchar(64)                           null comment '配置项',
    config_value text                                  null comment '配置值',
    created_by   varchar(32)                           not null comment '创建人',
    updated_by   varchar(32)                           not null comment '修改人',
    description  varchar(512)                          null comment '描述信息',
    status       int         default 0                 not null comment '状态，0：无效；1：生效',
    type         varchar(16) default 'String'          not null comment 'String、Boolean、Json',
    constraint uk_config_key
    unique (config_key)
    )
    comment '系统参数配置' charset = utf8;

create table if not exists bifrost_user
(
    gmt_create      timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified    timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    id              bigint auto_increment comment '主键'
    primary key,
    account         varchar(64) default ''                not null comment '账号',
    user_name       varchar(64)                           null comment '用户名称',
    pwd             varchar(32)                           not null comment '密码',
    jobs            varchar(100)                          null comment '岗位',
    avatar          mediumtext                            null comment '头像',
    expiration      datetime                              null comment '过期时间',
    gender          int                                   null comment '性别 0:未知;1:男;2:女',
    status          int         default 0                 not null comment '用户状态 0:正常',
    create_by       varchar(30)                           null comment '创建用户账号',
    update_by       varchar(30)                           null comment '修改用户账号',
    lang            varchar(16)                           null comment '语言',
    theme           varchar(32)                           null comment '主题',
    layout          varchar(32)                           null comment '布局',
    simplified      int                                   null comment '是否简体：1是0否',
    phone           varchar(20)                           null comment '手机号码',
    sign            varchar(1024)                         null comment '个性签名',
    salt            varchar(32) default ''                not null comment '盐值',
    tokenMD5        varchar(32)                           null comment '用户登录tokenMD5',
    en_user_name    varchar(40)                           null comment '英文用户名称',
    try_time        int         default 0                 null comment '密码错误次数',
    try_date        timestamp                             null comment '密码重试时间',
    update_pwd_time timestamp                             null comment '密码修改时间',
    first_login     varchar(10) default '0'               null comment '第一次登陆标志位，1、已登录，0或者null，初次登录',
    email           varchar(128)                          null comment '邮箱',
    parent_id       bigint                                null,
    main_code       varchar(100)                          null,
    constraint uk_account
    unique (account)
    )
    comment '用户表' charset = utf8;

create table if not exists gruul_user_org_role
(
    id           bigint auto_increment
    primary key,
    user_id      bigint                              not null,
    org_id       bigint                              not null,
    role_id      bigint                              not null,
    gmt_create   timestamp default CURRENT_TIMESTAMP not null,
    gmt_modified timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    create_by    varchar(100)                        null,
    update_by    varchar(100)                        null,
    constraint gruul_user_org_role_un
    unique (user_id, org_id, role_id)
    );

