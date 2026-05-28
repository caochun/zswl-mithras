create table if not exists act_evt_log
(
    LOG_NR_       bigint auto_increment
    primary key,
    TYPE_         varchar(64)                               null,
    PROC_DEF_ID_  varchar(64)                               null,
    PROC_INST_ID_ varchar(64)                               null,
    EXECUTION_ID_ varchar(64)                               null,
    TASK_ID_      varchar(64)                               null,
    TIME_STAMP_   timestamp(3) default CURRENT_TIMESTAMP(3) not null,
    USER_ID_      varchar(255)                              null,
    DATA_         longblob                                  null,
    LOCK_OWNER_   varchar(255)                              null,
    LOCK_TIME_    timestamp(3)                              null,
    IS_PROCESSED_ tinyint      default 0                    null
    )
    collate = utf8_bin;

create table if not exists act_ge_property
(
    NAME_  varchar(64)  not null
    primary key,
    VALUE_ varchar(300) null,
    REV_   int          null
    )
    collate = utf8_bin;

create table if not exists act_hi_actinst
(
    ID_                varchar(64)             not null
    primary key,
    REV_               int          default 1  null,
    PROC_DEF_ID_       varchar(64)             not null,
    PROC_INST_ID_      varchar(64)             not null,
    EXECUTION_ID_      varchar(64)             not null,
    ACT_ID_            varchar(255)            not null,
    TASK_ID_           varchar(64)             null,
    CALL_PROC_INST_ID_ varchar(64)             null,
    ACT_NAME_          varchar(255)            null,
    ACT_TYPE_          varchar(255)            not null,
    ASSIGNEE_          varchar(255)            null,
    START_TIME_        datetime(3)             not null,
    END_TIME_          datetime(3)             null,
    TRANSACTION_ORDER_ int                     null,
    DURATION_          bigint                  null,
    DELETE_REASON_     varchar(4000)           null,
    TENANT_ID_         varchar(255) default '' null
    )
    collate = utf8_bin;

create index ACT_IDX_HI_ACT_INST_END
    on act_hi_actinst (END_TIME_);

create index ACT_IDX_HI_ACT_INST_EXEC
    on act_hi_actinst (EXECUTION_ID_, ACT_ID_);

create index ACT_IDX_HI_ACT_INST_PROCINST
    on act_hi_actinst (PROC_INST_ID_, ACT_ID_);

create index ACT_IDX_HI_ACT_INST_START
    on act_hi_actinst (START_TIME_);

create table if not exists act_hi_attachment
(
    ID_           varchar(64)   not null
    primary key,
    REV_          int           null,
    USER_ID_      varchar(255)  null,
    NAME_         varchar(255)  null,
    DESCRIPTION_  varchar(4000) null,
    TYPE_         varchar(255)  null,
    TASK_ID_      varchar(64)   null,
    PROC_INST_ID_ varchar(64)   null,
    URL_          varchar(4000) null,
    CONTENT_ID_   varchar(64)   null,
    TIME_         datetime(3)   null
    )
    collate = utf8_bin;

create table if not exists act_hi_comment
(
    ID_           varchar(64)   not null
    primary key,
    TYPE_         varchar(255)  null,
    TIME_         datetime(3)   not null,
    USER_ID_      varchar(255)  null,
    TASK_ID_      varchar(64)   null,
    PROC_INST_ID_ varchar(64)   null,
    ACTION_       varchar(255)  null,
    MESSAGE_      varchar(4000) null,
    FULL_MSG_     longblob      null
    )
    collate = utf8_bin;

create table if not exists act_hi_detail
(
    ID_           varchar(64)   not null
    primary key,
    TYPE_         varchar(255)  not null,
    PROC_INST_ID_ varchar(64)   null,
    EXECUTION_ID_ varchar(64)   null,
    TASK_ID_      varchar(64)   null,
    ACT_INST_ID_  varchar(64)   null,
    NAME_         varchar(255)  not null,
    VAR_TYPE_     varchar(255)  null,
    REV_          int           null,
    TIME_         datetime(3)   not null,
    BYTEARRAY_ID_ varchar(64)   null,
    DOUBLE_       double        null,
    LONG_         bigint        null,
    TEXT_         varchar(4000) null,
    TEXT2_        varchar(4000) null
    )
    collate = utf8_bin;

create index ACT_IDX_HI_DETAIL_ACT_INST
    on act_hi_detail (ACT_INST_ID_);

create index ACT_IDX_HI_DETAIL_NAME
    on act_hi_detail (NAME_);

create index ACT_IDX_HI_DETAIL_PROC_INST
    on act_hi_detail (PROC_INST_ID_);

create index ACT_IDX_HI_DETAIL_TASK_ID
    on act_hi_detail (TASK_ID_);

create index ACT_IDX_HI_DETAIL_TIME
    on act_hi_detail (TIME_);

create table if not exists act_hi_entitylink
(
    ID_                      varchar(64)  not null
    primary key,
    LINK_TYPE_               varchar(255) null,
    CREATE_TIME_             datetime(3)  null,
    SCOPE_ID_                varchar(255) null,
    SUB_SCOPE_ID_            varchar(255) null,
    SCOPE_TYPE_              varchar(255) null,
    SCOPE_DEFINITION_ID_     varchar(255) null,
    PARENT_ELEMENT_ID_       varchar(255) null,
    REF_SCOPE_ID_            varchar(255) null,
    REF_SCOPE_TYPE_          varchar(255) null,
    REF_SCOPE_DEFINITION_ID_ varchar(255) null,
    ROOT_SCOPE_ID_           varchar(255) null,
    ROOT_SCOPE_TYPE_         varchar(255) null,
    HIERARCHY_TYPE_          varchar(255) null
    )
    collate = utf8_bin;

create index ACT_IDX_HI_ENT_LNK_REF_SCOPE
    on act_hi_entitylink (REF_SCOPE_ID_, REF_SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_HI_ENT_LNK_ROOT_SCOPE
    on act_hi_entitylink (ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_HI_ENT_LNK_SCOPE
    on act_hi_entitylink (SCOPE_ID_, SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_HI_ENT_LNK_SCOPE_DEF
    on act_hi_entitylink (SCOPE_DEFINITION_ID_, SCOPE_TYPE_, LINK_TYPE_);

create table if not exists act_hi_identitylink
(
    ID_                  varchar(64)  not null
    primary key,
    GROUP_ID_            varchar(255) null,
    TYPE_                varchar(255) null,
    USER_ID_             varchar(255) null,
    TASK_ID_             varchar(64)  null,
    CREATE_TIME_         datetime(3)  null,
    PROC_INST_ID_        varchar(64)  null,
    SCOPE_ID_            varchar(255) null,
    SUB_SCOPE_ID_        varchar(255) null,
    SCOPE_TYPE_          varchar(255) null,
    SCOPE_DEFINITION_ID_ varchar(255) null
    )
    collate = utf8_bin;

create index ACT_IDX_HI_IDENT_LNK_PROCINST
    on act_hi_identitylink (PROC_INST_ID_);

create index ACT_IDX_HI_IDENT_LNK_SCOPE
    on act_hi_identitylink (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_IDENT_LNK_SCOPE_DEF
    on act_hi_identitylink (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_IDENT_LNK_SUB_SCOPE
    on act_hi_identitylink (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_IDENT_LNK_TASK
    on act_hi_identitylink (TASK_ID_);

create index ACT_IDX_HI_IDENT_LNK_USER
    on act_hi_identitylink (USER_ID_);

create table if not exists act_hi_procinst
(
    ID_                        varchar(64)             not null
    primary key,
    REV_                       int          default 1  null,
    PROC_INST_ID_              varchar(64)             not null,
    BUSINESS_KEY_              varchar(255)            null,
    PROC_DEF_ID_               varchar(64)             not null,
    START_TIME_                datetime(3)             not null,
    END_TIME_                  datetime(3)             null,
    DURATION_                  bigint                  null,
    START_USER_ID_             varchar(255)            null,
    START_ACT_ID_              varchar(255)            null,
    END_ACT_ID_                varchar(255)            null,
    SUPER_PROCESS_INSTANCE_ID_ varchar(64)             null,
    DELETE_REASON_             varchar(4000)           null,
    TENANT_ID_                 varchar(255) default '' null,
    NAME_                      varchar(255)            null,
    CALLBACK_ID_               varchar(255)            null,
    CALLBACK_TYPE_             varchar(255)            null,
    REFERENCE_ID_              varchar(255)            null,
    REFERENCE_TYPE_            varchar(255)            null,
    PROPAGATED_STAGE_INST_ID_  varchar(255)            null,
    BUSINESS_STATUS_           varchar(255)            null,
    constraint PROC_INST_ID_
    unique (PROC_INST_ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_HI_PRO_INST_END
    on act_hi_procinst (END_TIME_);

create index ACT_IDX_HI_PRO_I_BUSKEY
    on act_hi_procinst (BUSINESS_KEY_);

create table if not exists act_hi_taskinst
(
    ID_                       varchar(64)             not null
    primary key,
    REV_                      int          default 1  null,
    PROC_DEF_ID_              varchar(64)             null,
    TASK_DEF_ID_              varchar(64)             null,
    TASK_DEF_KEY_             varchar(255)            null,
    PROC_INST_ID_             varchar(64)             null,
    EXECUTION_ID_             varchar(64)             null,
    SCOPE_ID_                 varchar(255)            null,
    SUB_SCOPE_ID_             varchar(255)            null,
    SCOPE_TYPE_               varchar(255)            null,
    SCOPE_DEFINITION_ID_      varchar(255)            null,
    PROPAGATED_STAGE_INST_ID_ varchar(255)            null,
    NAME_                     varchar(255)            null,
    PARENT_TASK_ID_           varchar(64)             null,
    DESCRIPTION_              varchar(4000)           null,
    OWNER_                    varchar(255)            null,
    ASSIGNEE_                 varchar(255)            null,
    START_TIME_               datetime(3)             not null,
    CLAIM_TIME_               datetime(3)             null,
    END_TIME_                 datetime(3)             null,
    DURATION_                 bigint                  null,
    DELETE_REASON_            varchar(4000)           null,
    PRIORITY_                 int                     null,
    DUE_DATE_                 datetime(3)             null,
    FORM_KEY_                 varchar(255)            null,
    CATEGORY_                 varchar(255)            null,
    TENANT_ID_                varchar(255) default '' null,
    LAST_UPDATED_TIME_        datetime(3)             null
    )
    collate = utf8_bin;

create index ACT_IDX_HI_TASK_INST_PROCINST
    on act_hi_taskinst (PROC_INST_ID_);

create index ACT_IDX_HI_TASK_SCOPE
    on act_hi_taskinst (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_TASK_SCOPE_DEF
    on act_hi_taskinst (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_TASK_SUB_SCOPE
    on act_hi_taskinst (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table if not exists act_hi_tsk_log
(
    ID_                  bigint auto_increment
    primary key,
    TYPE_                varchar(64)                               null,
    TASK_ID_             varchar(64)                               not null,
    TIME_STAMP_          timestamp(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3),
    USER_ID_             varchar(255)                              null,
    DATA_                varchar(4000)                             null,
    EXECUTION_ID_        varchar(64)                               null,
    PROC_INST_ID_        varchar(64)                               null,
    PROC_DEF_ID_         varchar(64)                               null,
    SCOPE_ID_            varchar(255)                              null,
    SCOPE_DEFINITION_ID_ varchar(255)                              null,
    SUB_SCOPE_ID_        varchar(255)                              null,
    SCOPE_TYPE_          varchar(255)                              null,
    TENANT_ID_           varchar(255) default ''                   null
    )
    collate = utf8_bin;

create table if not exists act_hi_varinst
(
    ID_                varchar(64)   not null
    primary key,
    REV_               int default 1 null,
    PROC_INST_ID_      varchar(64)   null,
    EXECUTION_ID_      varchar(64)   null,
    TASK_ID_           varchar(64)   null,
    NAME_              varchar(255)  not null,
    VAR_TYPE_          varchar(100)  null,
    SCOPE_ID_          varchar(255)  null,
    SUB_SCOPE_ID_      varchar(255)  null,
    SCOPE_TYPE_        varchar(255)  null,
    BYTEARRAY_ID_      varchar(64)   null,
    DOUBLE_            double        null,
    LONG_              bigint        null,
    TEXT_              varchar(4000) null,
    TEXT2_             varchar(4000) null,
    CREATE_TIME_       datetime(3)   null,
    LAST_UPDATED_TIME_ datetime(3)   null
    )
    collate = utf8_bin;

create index ACT_IDX_HI_PROCVAR_EXE
    on act_hi_varinst (EXECUTION_ID_);

create index ACT_IDX_HI_PROCVAR_NAME_TYPE
    on act_hi_varinst (NAME_, VAR_TYPE_);

create index ACT_IDX_HI_PROCVAR_PROC_INST
    on act_hi_varinst (PROC_INST_ID_);

create index ACT_IDX_HI_PROCVAR_TASK_ID
    on act_hi_varinst (TASK_ID_);

create index ACT_IDX_HI_VAR_SCOPE_ID_TYPE
    on act_hi_varinst (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_HI_VAR_SUB_ID_TYPE
    on act_hi_varinst (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table if not exists act_id_bytearray
(
    ID_    varchar(64)  not null
    primary key,
    REV_   int          null,
    NAME_  varchar(255) null,
    BYTES_ longblob     null
    )
    collate = utf8_bin;

create table if not exists act_id_group
(
    ID_   varchar(64)  not null
    primary key,
    REV_  int          null,
    NAME_ varchar(255) null,
    TYPE_ varchar(255) null
    )
    collate = utf8_bin;

create table if not exists act_id_info
(
    ID_        varchar(64)  not null
    primary key,
    REV_       int          null,
    USER_ID_   varchar(64)  null,
    TYPE_      varchar(64)  null,
    KEY_       varchar(255) null,
    VALUE_     varchar(255) null,
    PASSWORD_  longblob     null,
    PARENT_ID_ varchar(255) null
    )
    collate = utf8_bin;

create table if not exists act_id_priv
(
    ID_   varchar(64)  not null
    primary key,
    NAME_ varchar(255) not null,
    constraint ACT_UNIQ_PRIV_NAME
    unique (NAME_)
    )
    collate = utf8_bin;

create table if not exists act_id_priv_mapping
(
    ID_       varchar(64)  not null
    primary key,
    PRIV_ID_  varchar(64)  not null,
    USER_ID_  varchar(255) null,
    GROUP_ID_ varchar(255) null,
    constraint ACT_FK_PRIV_MAPPING
    foreign key (PRIV_ID_) references act_id_priv (ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_PRIV_GROUP
    on act_id_priv_mapping (GROUP_ID_);

create index ACT_IDX_PRIV_USER
    on act_id_priv_mapping (USER_ID_);

create table if not exists act_id_property
(
    NAME_  varchar(64)  not null
    primary key,
    VALUE_ varchar(300) null,
    REV_   int          null
    )
    collate = utf8_bin;

create table if not exists act_id_token
(
    ID_          varchar(64)                               not null
    primary key,
    REV_         int                                       null,
    TOKEN_VALUE_ varchar(255)                              null,
    TOKEN_DATE_  timestamp(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3),
    IP_ADDRESS_  varchar(255)                              null,
    USER_AGENT_  varchar(255)                              null,
    USER_ID_     varchar(255)                              null,
    TOKEN_DATA_  varchar(2000)                             null
    )
    collate = utf8_bin;

create table if not exists act_id_user
(
    ID_           varchar(64)             not null
    primary key,
    REV_          int                     null,
    FIRST_        varchar(255)            null,
    LAST_         varchar(255)            null,
    DISPLAY_NAME_ varchar(255)            null,
    EMAIL_        varchar(255)            null,
    PWD_          varchar(255)            null,
    PICTURE_ID_   varchar(64)             null,
    TENANT_ID_    varchar(255) default '' null
    )
    collate = utf8_bin;

create table if not exists act_id_membership
(
    USER_ID_  varchar(64) not null,
    GROUP_ID_ varchar(64) not null,
    primary key (USER_ID_, GROUP_ID_),
    constraint ACT_FK_MEMB_GROUP
    foreign key (GROUP_ID_) references act_id_group (ID_),
    constraint ACT_FK_MEMB_USER
    foreign key (USER_ID_) references act_id_user (ID_)
    )
    collate = utf8_bin;

create table if not exists act_re_deployment
(
    ID_                   varchar(64)             not null
    primary key,
    NAME_                 varchar(255)            null,
    CATEGORY_             varchar(255)            null,
    KEY_                  varchar(255)            null,
    TENANT_ID_            varchar(255) default '' null,
    DEPLOY_TIME_          timestamp(3)            null,
    DERIVED_FROM_         varchar(64)             null,
    DERIVED_FROM_ROOT_    varchar(64)             null,
    PARENT_DEPLOYMENT_ID_ varchar(255)            null,
    ENGINE_VERSION_       varchar(255)            null
    )
    collate = utf8_bin;

create table if not exists act_ge_bytearray
(
    ID_            varchar(64)  not null
    primary key,
    REV_           int          null,
    NAME_          varchar(255) null,
    DEPLOYMENT_ID_ varchar(64)  null,
    BYTES_         longblob     null,
    GENERATED_     tinyint      null,
    constraint ACT_FK_BYTEARR_DEPL
    foreign key (DEPLOYMENT_ID_) references act_re_deployment (ID_)
    )
    collate = utf8_bin;

create table if not exists act_re_model
(
    ID_                           varchar(64)             not null
    primary key,
    REV_                          int                     null,
    NAME_                         varchar(255)            null,
    KEY_                          varchar(255)            null,
    CATEGORY_                     varchar(255)            null,
    CREATE_TIME_                  timestamp(3)            null,
    LAST_UPDATE_TIME_             timestamp(3)            null,
    VERSION_                      int                     null,
    META_INFO_                    varchar(4000)           null,
    DEPLOYMENT_ID_                varchar(64)             null,
    EDITOR_SOURCE_VALUE_ID_       varchar(64)             null,
    EDITOR_SOURCE_EXTRA_VALUE_ID_ varchar(64)             null,
    TENANT_ID_                    varchar(255) default '' null,
    constraint ACT_FK_MODEL_DEPLOYMENT
    foreign key (DEPLOYMENT_ID_) references act_re_deployment (ID_),
    constraint ACT_FK_MODEL_SOURCE
    foreign key (EDITOR_SOURCE_VALUE_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_MODEL_SOURCE_EXTRA
    foreign key (EDITOR_SOURCE_EXTRA_VALUE_ID_) references act_ge_bytearray (ID_)
    )
    collate = utf8_bin;

create table if not exists act_re_procdef
(
    ID_                     varchar(64)             not null
    primary key,
    REV_                    int                     null,
    CATEGORY_               varchar(255)            null,
    NAME_                   varchar(255)            null,
    KEY_                    varchar(255)            not null,
    VERSION_                int                     not null,
    DEPLOYMENT_ID_          varchar(64)             null,
    RESOURCE_NAME_          varchar(4000)           null,
    DGRM_RESOURCE_NAME_     varchar(4000)           null,
    DESCRIPTION_            varchar(4000)           null,
    HAS_START_FORM_KEY_     tinyint                 null,
    HAS_GRAPHICAL_NOTATION_ tinyint                 null,
    SUSPENSION_STATE_       int                     null,
    TENANT_ID_              varchar(255) default '' null,
    ENGINE_VERSION_         varchar(255)            null,
    DERIVED_FROM_           varchar(64)             null,
    DERIVED_FROM_ROOT_      varchar(64)             null,
    DERIVED_VERSION_        int          default 0  not null,
    constraint ACT_UNIQ_PROCDEF
    unique (KEY_, VERSION_, DERIVED_VERSION_, TENANT_ID_)
    )
    collate = utf8_bin;

create table if not exists act_procdef_info
(
    ID_           varchar(64) not null
    primary key,
    PROC_DEF_ID_  varchar(64) not null,
    REV_          int         null,
    INFO_JSON_ID_ varchar(64) null,
    constraint ACT_UNIQ_INFO_PROCDEF
    unique (PROC_DEF_ID_),
    constraint ACT_FK_INFO_JSON_BA
    foreign key (INFO_JSON_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_INFO_PROCDEF
    foreign key (PROC_DEF_ID_) references act_re_procdef (ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_INFO_PROCDEF
    on act_procdef_info (PROC_DEF_ID_);

create table if not exists act_ru_actinst
(
    ID_                varchar(64)             not null
    primary key,
    REV_               int          default 1  null,
    PROC_DEF_ID_       varchar(64)             not null,
    PROC_INST_ID_      varchar(64)             not null,
    EXECUTION_ID_      varchar(64)             not null,
    ACT_ID_            varchar(255)            not null,
    TASK_ID_           varchar(64)             null,
    CALL_PROC_INST_ID_ varchar(64)             null,
    ACT_NAME_          varchar(255)            null,
    ACT_TYPE_          varchar(255)            not null,
    ASSIGNEE_          varchar(255)            null,
    START_TIME_        datetime(3)             not null,
    END_TIME_          datetime(3)             null,
    DURATION_          bigint                  null,
    TRANSACTION_ORDER_ int                     null,
    DELETE_REASON_     varchar(4000)           null,
    TENANT_ID_         varchar(255) default '' null
    )
    collate = utf8_bin;

create index ACT_IDX_RU_ACTI_END
    on act_ru_actinst (END_TIME_);

create index ACT_IDX_RU_ACTI_EXEC
    on act_ru_actinst (EXECUTION_ID_);

create index ACT_IDX_RU_ACTI_EXEC_ACT
    on act_ru_actinst (EXECUTION_ID_, ACT_ID_);

create index ACT_IDX_RU_ACTI_PROC
    on act_ru_actinst (PROC_INST_ID_);

create index ACT_IDX_RU_ACTI_PROC_ACT
    on act_ru_actinst (PROC_INST_ID_, ACT_ID_);

create index ACT_IDX_RU_ACTI_START
    on act_ru_actinst (START_TIME_);

create index ACT_IDX_RU_ACTI_TASK
    on act_ru_actinst (TASK_ID_);

create table if not exists act_ru_entitylink
(
    ID_                      varchar(64)  not null
    primary key,
    REV_                     int          null,
    CREATE_TIME_             datetime(3)  null,
    LINK_TYPE_               varchar(255) null,
    SCOPE_ID_                varchar(255) null,
    SUB_SCOPE_ID_            varchar(255) null,
    SCOPE_TYPE_              varchar(255) null,
    SCOPE_DEFINITION_ID_     varchar(255) null,
    PARENT_ELEMENT_ID_       varchar(255) null,
    REF_SCOPE_ID_            varchar(255) null,
    REF_SCOPE_TYPE_          varchar(255) null,
    REF_SCOPE_DEFINITION_ID_ varchar(255) null,
    ROOT_SCOPE_ID_           varchar(255) null,
    ROOT_SCOPE_TYPE_         varchar(255) null,
    HIERARCHY_TYPE_          varchar(255) null
    )
    collate = utf8_bin;

create index ACT_IDX_ENT_LNK_REF_SCOPE
    on act_ru_entitylink (REF_SCOPE_ID_, REF_SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_ENT_LNK_ROOT_SCOPE
    on act_ru_entitylink (ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_ENT_LNK_SCOPE
    on act_ru_entitylink (SCOPE_ID_, SCOPE_TYPE_, LINK_TYPE_);

create index ACT_IDX_ENT_LNK_SCOPE_DEF
    on act_ru_entitylink (SCOPE_DEFINITION_ID_, SCOPE_TYPE_, LINK_TYPE_);

create table if not exists act_ru_execution
(
    ID_                        varchar(64)             not null
    primary key,
    REV_                       int                     null,
    PROC_INST_ID_              varchar(64)             null,
    BUSINESS_KEY_              varchar(255)            null,
    PARENT_ID_                 varchar(64)             null,
    PROC_DEF_ID_               varchar(64)             null,
    SUPER_EXEC_                varchar(64)             null,
    ROOT_PROC_INST_ID_         varchar(64)             null,
    ACT_ID_                    varchar(255)            null,
    IS_ACTIVE_                 tinyint                 null,
    IS_CONCURRENT_             tinyint                 null,
    IS_SCOPE_                  tinyint                 null,
    IS_EVENT_SCOPE_            tinyint                 null,
    IS_MI_ROOT_                tinyint                 null,
    SUSPENSION_STATE_          int                     null,
    CACHED_ENT_STATE_          int                     null,
    TENANT_ID_                 varchar(255) default '' null,
    NAME_                      varchar(255)            null,
    START_ACT_ID_              varchar(255)            null,
    START_TIME_                datetime(3)             null,
    START_USER_ID_             varchar(255)            null,
    LOCK_TIME_                 timestamp(3)            null,
    LOCK_OWNER_                varchar(255)            null,
    IS_COUNT_ENABLED_          tinyint                 null,
    EVT_SUBSCR_COUNT_          int                     null,
    TASK_COUNT_                int                     null,
    JOB_COUNT_                 int                     null,
    TIMER_JOB_COUNT_           int                     null,
    SUSP_JOB_COUNT_            int                     null,
    DEADLETTER_JOB_COUNT_      int                     null,
    EXTERNAL_WORKER_JOB_COUNT_ int                     null,
    VAR_COUNT_                 int                     null,
    ID_LINK_COUNT_             int                     null,
    CALLBACK_ID_               varchar(255)            null,
    CALLBACK_TYPE_             varchar(255)            null,
    REFERENCE_ID_              varchar(255)            null,
    REFERENCE_TYPE_            varchar(255)            null,
    PROPAGATED_STAGE_INST_ID_  varchar(255)            null,
    BUSINESS_STATUS_           varchar(255)            null,
    constraint ACT_FK_EXE_PARENT
    foreign key (PARENT_ID_) references act_ru_execution (ID_)
    on delete cascade,
    constraint ACT_FK_EXE_PROCDEF
    foreign key (PROC_DEF_ID_) references act_re_procdef (ID_),
    constraint ACT_FK_EXE_PROCINST
    foreign key (PROC_INST_ID_) references act_ru_execution (ID_)
    on update cascade on delete cascade,
    constraint ACT_FK_EXE_SUPER
    foreign key (SUPER_EXEC_) references act_ru_execution (ID_)
    on delete cascade
    )
    collate = utf8_bin;

create table if not exists act_ru_deadletter_job
(
    ID_                  varchar(64)             not null
    primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_DEADLETTER_JOB_CUSTOM_VALUES
    foreign key (CUSTOM_VALUES_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_DEADLETTER_JOB_EXCEPTION
    foreign key (EXCEPTION_STACK_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_DEADLETTER_JOB_EXECUTION
    foreign key (EXECUTION_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE
    foreign key (PROCESS_INSTANCE_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_DEADLETTER_JOB_PROC_DEF
    foreign key (PROC_DEF_ID_) references act_re_procdef (ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_DEADLETTER_JOB_CORRELATION_ID
    on act_ru_deadletter_job (CORRELATION_ID_);

create index ACT_IDX_DEADLETTER_JOB_CUSTOM_VALUES_ID
    on act_ru_deadletter_job (CUSTOM_VALUES_ID_);

create index ACT_IDX_DEADLETTER_JOB_EXCEPTION_STACK_ID
    on act_ru_deadletter_job (EXCEPTION_STACK_ID_);

create index ACT_IDX_DJOB_SCOPE
    on act_ru_deadletter_job (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_DJOB_SCOPE_DEF
    on act_ru_deadletter_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_DJOB_SUB_SCOPE
    on act_ru_deadletter_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table if not exists act_ru_event_subscr
(
    ID_                  varchar(64)                               not null
    primary key,
    REV_                 int                                       null,
    EVENT_TYPE_          varchar(255)                              not null,
    EVENT_NAME_          varchar(255)                              null,
    EXECUTION_ID_        varchar(64)                               null,
    PROC_INST_ID_        varchar(64)                               null,
    ACTIVITY_ID_         varchar(64)                               null,
    CONFIGURATION_       varchar(255)                              null,
    CREATED_             timestamp(3) default CURRENT_TIMESTAMP(3) not null,
    PROC_DEF_ID_         varchar(64)                               null,
    SUB_SCOPE_ID_        varchar(64)                               null,
    SCOPE_ID_            varchar(64)                               null,
    SCOPE_DEFINITION_ID_ varchar(64)                               null,
    SCOPE_TYPE_          varchar(64)                               null,
    TENANT_ID_           varchar(255) default ''                   null,
    constraint ACT_FK_EVENT_EXEC
    foreign key (EXECUTION_ID_) references act_ru_execution (ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_EVENT_SUBSCR_CONFIG_
    on act_ru_event_subscr (CONFIGURATION_);

create index ACT_IDC_EXEC_ROOT
    on act_ru_execution (ROOT_PROC_INST_ID_);

create index ACT_IDX_EXEC_BUSKEY
    on act_ru_execution (BUSINESS_KEY_);

create index ACT_IDX_EXEC_REF_ID_
    on act_ru_execution (REFERENCE_ID_);

create table if not exists act_ru_external_job
(
    ID_                  varchar(64)             not null
    primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    LOCK_EXP_TIME_       timestamp(3)            null,
    LOCK_OWNER_          varchar(255)            null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    RETRIES_             int                     null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_EXTERNAL_JOB_CUSTOM_VALUES
    foreign key (CUSTOM_VALUES_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_EXTERNAL_JOB_EXCEPTION
    foreign key (EXCEPTION_STACK_ID_) references act_ge_bytearray (ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_EJOB_SCOPE
    on act_ru_external_job (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_EJOB_SCOPE_DEF
    on act_ru_external_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_EJOB_SUB_SCOPE
    on act_ru_external_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_EXTERNAL_JOB_CORRELATION_ID
    on act_ru_external_job (CORRELATION_ID_);

create index ACT_IDX_EXTERNAL_JOB_CUSTOM_VALUES_ID
    on act_ru_external_job (CUSTOM_VALUES_ID_);

create index ACT_IDX_EXTERNAL_JOB_EXCEPTION_STACK_ID
    on act_ru_external_job (EXCEPTION_STACK_ID_);

create table if not exists act_ru_history_job
(
    ID_                 varchar(64)             not null
    primary key,
    REV_                int                     null,
    LOCK_EXP_TIME_      timestamp(3)            null,
    LOCK_OWNER_         varchar(255)            null,
    RETRIES_            int                     null,
    EXCEPTION_STACK_ID_ varchar(64)             null,
    EXCEPTION_MSG_      varchar(4000)           null,
    HANDLER_TYPE_       varchar(255)            null,
    HANDLER_CFG_        varchar(4000)           null,
    CUSTOM_VALUES_ID_   varchar(64)             null,
    ADV_HANDLER_CFG_ID_ varchar(64)             null,
    CREATE_TIME_        timestamp(3)            null,
    SCOPE_TYPE_         varchar(255)            null,
    TENANT_ID_          varchar(255) default '' null
    )
    collate = utf8_bin;

create table if not exists act_ru_job
(
    ID_                  varchar(64)             not null
    primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    LOCK_EXP_TIME_       timestamp(3)            null,
    LOCK_OWNER_          varchar(255)            null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    RETRIES_             int                     null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_JOB_CUSTOM_VALUES
    foreign key (CUSTOM_VALUES_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_JOB_EXCEPTION
    foreign key (EXCEPTION_STACK_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_JOB_EXECUTION
    foreign key (EXECUTION_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_JOB_PROCESS_INSTANCE
    foreign key (PROCESS_INSTANCE_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_JOB_PROC_DEF
    foreign key (PROC_DEF_ID_) references act_re_procdef (ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_JOB_CORRELATION_ID
    on act_ru_job (CORRELATION_ID_);

create index ACT_IDX_JOB_CUSTOM_VALUES_ID
    on act_ru_job (CUSTOM_VALUES_ID_);

create index ACT_IDX_JOB_EXCEPTION_STACK_ID
    on act_ru_job (EXCEPTION_STACK_ID_);

create index ACT_IDX_JOB_SCOPE
    on act_ru_job (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_JOB_SCOPE_DEF
    on act_ru_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_JOB_SUB_SCOPE
    on act_ru_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table if not exists act_ru_suspended_job
(
    ID_                  varchar(64)             not null
    primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    RETRIES_             int                     null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_SUSPENDED_JOB_CUSTOM_VALUES
    foreign key (CUSTOM_VALUES_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_SUSPENDED_JOB_EXCEPTION
    foreign key (EXCEPTION_STACK_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_SUSPENDED_JOB_EXECUTION
    foreign key (EXECUTION_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE
    foreign key (PROCESS_INSTANCE_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_SUSPENDED_JOB_PROC_DEF
    foreign key (PROC_DEF_ID_) references act_re_procdef (ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_SJOB_SCOPE
    on act_ru_suspended_job (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_SJOB_SCOPE_DEF
    on act_ru_suspended_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_SJOB_SUB_SCOPE
    on act_ru_suspended_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_SUSPENDED_JOB_CORRELATION_ID
    on act_ru_suspended_job (CORRELATION_ID_);

create index ACT_IDX_SUSPENDED_JOB_CUSTOM_VALUES_ID
    on act_ru_suspended_job (CUSTOM_VALUES_ID_);

create index ACT_IDX_SUSPENDED_JOB_EXCEPTION_STACK_ID
    on act_ru_suspended_job (EXCEPTION_STACK_ID_);

create table if not exists act_ru_task
(
    ID_                       varchar(64)             not null
    primary key,
    REV_                      int                     null,
    EXECUTION_ID_             varchar(64)             null,
    PROC_INST_ID_             varchar(64)             null,
    PROC_DEF_ID_              varchar(64)             null,
    TASK_DEF_ID_              varchar(64)             null,
    SCOPE_ID_                 varchar(255)            null,
    SUB_SCOPE_ID_             varchar(255)            null,
    SCOPE_TYPE_               varchar(255)            null,
    SCOPE_DEFINITION_ID_      varchar(255)            null,
    PROPAGATED_STAGE_INST_ID_ varchar(255)            null,
    NAME_                     varchar(255)            null,
    PARENT_TASK_ID_           varchar(64)             null,
    DESCRIPTION_              varchar(4000)           null,
    TASK_DEF_KEY_             varchar(255)            null,
    OWNER_                    varchar(255)            null,
    ASSIGNEE_                 varchar(255)            null,
    DELEGATION_               varchar(64)             null,
    PRIORITY_                 int                     null,
    CREATE_TIME_              timestamp(3)            null,
    DUE_DATE_                 datetime(3)             null,
    CATEGORY_                 varchar(255)            null,
    SUSPENSION_STATE_         int                     null,
    TENANT_ID_                varchar(255) default '' null,
    FORM_KEY_                 varchar(255)            null,
    CLAIM_TIME_               datetime(3)             null,
    IS_COUNT_ENABLED_         tinyint                 null,
    VAR_COUNT_                int                     null,
    ID_LINK_COUNT_            int                     null,
    SUB_TASK_COUNT_           int                     null,
    constraint ACT_FK_TASK_EXE
    foreign key (EXECUTION_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_TASK_PROCDEF
    foreign key (PROC_DEF_ID_) references act_re_procdef (ID_),
    constraint ACT_FK_TASK_PROCINST
    foreign key (PROC_INST_ID_) references act_ru_execution (ID_)
    )
    collate = utf8_bin;

create table if not exists act_ru_identitylink
(
    ID_                  varchar(64)  not null
    primary key,
    REV_                 int          null,
    GROUP_ID_            varchar(255) null,
    TYPE_                varchar(255) null,
    USER_ID_             varchar(255) null,
    TASK_ID_             varchar(64)  null,
    PROC_INST_ID_        varchar(64)  null,
    PROC_DEF_ID_         varchar(64)  null,
    SCOPE_ID_            varchar(255) null,
    SUB_SCOPE_ID_        varchar(255) null,
    SCOPE_TYPE_          varchar(255) null,
    SCOPE_DEFINITION_ID_ varchar(255) null,
    constraint ACT_FK_ATHRZ_PROCEDEF
    foreign key (PROC_DEF_ID_) references act_re_procdef (ID_),
    constraint ACT_FK_IDL_PROCINST
    foreign key (PROC_INST_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_TSKASS_TASK
    foreign key (TASK_ID_) references act_ru_task (ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_ATHRZ_PROCEDEF
    on act_ru_identitylink (PROC_DEF_ID_);

create index ACT_IDX_IDENT_LNK_GROUP
    on act_ru_identitylink (GROUP_ID_);

create index ACT_IDX_IDENT_LNK_SCOPE
    on act_ru_identitylink (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_IDENT_LNK_SCOPE_DEF
    on act_ru_identitylink (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_IDENT_LNK_SUB_SCOPE
    on act_ru_identitylink (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_IDENT_LNK_USER
    on act_ru_identitylink (USER_ID_);

create index ACT_IDX_TASK_CREATE
    on act_ru_task (CREATE_TIME_);

create index ACT_IDX_TASK_SCOPE
    on act_ru_task (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_TASK_SCOPE_DEF
    on act_ru_task (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_TASK_SUB_SCOPE
    on act_ru_task (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table if not exists act_ru_timer_job
(
    ID_                  varchar(64)             not null
    primary key,
    REV_                 int                     null,
    CATEGORY_            varchar(255)            null,
    TYPE_                varchar(255)            not null,
    LOCK_EXP_TIME_       timestamp(3)            null,
    LOCK_OWNER_          varchar(255)            null,
    EXCLUSIVE_           tinyint(1)              null,
    EXECUTION_ID_        varchar(64)             null,
    PROCESS_INSTANCE_ID_ varchar(64)             null,
    PROC_DEF_ID_         varchar(64)             null,
    ELEMENT_ID_          varchar(255)            null,
    ELEMENT_NAME_        varchar(255)            null,
    SCOPE_ID_            varchar(255)            null,
    SUB_SCOPE_ID_        varchar(255)            null,
    SCOPE_TYPE_          varchar(255)            null,
    SCOPE_DEFINITION_ID_ varchar(255)            null,
    CORRELATION_ID_      varchar(255)            null,
    RETRIES_             int                     null,
    EXCEPTION_STACK_ID_  varchar(64)             null,
    EXCEPTION_MSG_       varchar(4000)           null,
    DUEDATE_             timestamp(3)            null,
    REPEAT_              varchar(255)            null,
    HANDLER_TYPE_        varchar(255)            null,
    HANDLER_CFG_         varchar(4000)           null,
    CUSTOM_VALUES_ID_    varchar(64)             null,
    CREATE_TIME_         timestamp(3)            null,
    TENANT_ID_           varchar(255) default '' null,
    constraint ACT_FK_TIMER_JOB_CUSTOM_VALUES
    foreign key (CUSTOM_VALUES_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_TIMER_JOB_EXCEPTION
    foreign key (EXCEPTION_STACK_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_TIMER_JOB_EXECUTION
    foreign key (EXECUTION_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_TIMER_JOB_PROCESS_INSTANCE
    foreign key (PROCESS_INSTANCE_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_TIMER_JOB_PROC_DEF
    foreign key (PROC_DEF_ID_) references act_re_procdef (ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_TIMER_JOB_CORRELATION_ID
    on act_ru_timer_job (CORRELATION_ID_);

create index ACT_IDX_TIMER_JOB_CUSTOM_VALUES_ID
    on act_ru_timer_job (CUSTOM_VALUES_ID_);

create index ACT_IDX_TIMER_JOB_DUEDATE
    on act_ru_timer_job (DUEDATE_);

create index ACT_IDX_TIMER_JOB_EXCEPTION_STACK_ID
    on act_ru_timer_job (EXCEPTION_STACK_ID_);

create index ACT_IDX_TJOB_SCOPE
    on act_ru_timer_job (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_TJOB_SCOPE_DEF
    on act_ru_timer_job (SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_TJOB_SUB_SCOPE
    on act_ru_timer_job (SUB_SCOPE_ID_, SCOPE_TYPE_);

create table if not exists act_ru_variable
(
    ID_           varchar(64)   not null
    primary key,
    REV_          int           null,
    TYPE_         varchar(255)  not null,
    NAME_         varchar(255)  not null,
    EXECUTION_ID_ varchar(64)   null,
    PROC_INST_ID_ varchar(64)   null,
    TASK_ID_      varchar(64)   null,
    SCOPE_ID_     varchar(255)  null,
    SUB_SCOPE_ID_ varchar(255)  null,
    SCOPE_TYPE_   varchar(255)  null,
    BYTEARRAY_ID_ varchar(64)   null,
    DOUBLE_       double        null,
    LONG_         bigint        null,
    TEXT_         varchar(4000) null,
    TEXT2_        varchar(4000) null,
    constraint ACT_FK_VAR_BYTEARRAY
    foreign key (BYTEARRAY_ID_) references act_ge_bytearray (ID_),
    constraint ACT_FK_VAR_EXE
    foreign key (EXECUTION_ID_) references act_ru_execution (ID_),
    constraint ACT_FK_VAR_PROCINST
    foreign key (PROC_INST_ID_) references act_ru_execution (ID_)
    )
    collate = utf8_bin;

create index ACT_IDX_RU_VAR_SCOPE_ID_TYPE
    on act_ru_variable (SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_RU_VAR_SUB_ID_TYPE
    on act_ru_variable (SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_VARIABLE_TASK_ID
    on act_ru_variable (TASK_ID_);

create table if not exists add_sign_record
(
    id         bigint auto_increment comment '主键'
    primary key,
    type       tinyint                                       not null comment '加签类型：1前加签，2后加签',
    task_id    varchar(64)                                   not null comment '任务id',
    p_task_id  varchar(64)                                   not null comment '父任务id',
    pass_flag  tinyint             default 0                 null comment '审批通过标志，如果前加签的某个父任务的子任务都通过了，要还原父任务',
    is_delete  tinyint(4) unsigned default 0                 not null comment '是否删除',
    gmt_create datetime            default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify datetime            default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间'
    )
    comment '加签记录表';

create index idx_p_task_id
    on add_sign_record (p_task_id);

create index idx_task_id
    on add_sign_record (task_id);

create table if not exists address_dictionary
(
    id        bigint auto_increment
    primary key,
    code      varchar(20)     null comment '编号',
    display   varchar(50)     null comment '显示名称',
    parent_id bigint          null comment '父级id',
    sort      int default 100 null,
    level     int default 1   null
    )
    comment '地址字典表';

create table if not exists admin_dictionary
(
    id           bigint unsigned auto_increment comment 'id'
    primary key,
    uuid         char(32)                              not null comment 'uuid',
    mykey        varchar(64)                           null comment '字典key',
    myvalue      text                                  null comment '字典值',
    `groups`     varchar(64) default '["system"]'      null comment '字典分组编号：system系统；auditForm，审批表单',
    created_by   varchar(36)                           not null comment '创建人',
    updated_by   varchar(36)                           not null comment '修改人',
    gmt_create   timestamp   default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modified timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    description  varchar(500)                          null comment '描述信息',
    default_flag tinyint(1)                            null comment '是否默认，true, false',
    constraint uk_mykey
    unique (mykey),
    constraint uk_uuid
    unique (uuid)
    )
    comment '字典配置' charset = utf8;

create table if not exists base_data_bank_account
(
    id             bigint auto_increment
    primary key,
    account_type   varchar(50)                           not null comment '账户类型，BASE-基本户，NORMAL-一般户',
    account_name   varchar(100)                          not null comment '账户名称',
    account_number varchar(30) default ''                not null comment '账号',
    account_bank   varchar(100)                          not null comment '支行名称',
    create_time    datetime    default CURRENT_TIMESTAMP not null,
    create_by      bigint                                null comment '创建人id',
    update_time    datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    update_by      bigint                                null
    )
    comment '基础数据-我方账户';

create table if not exists base_data_lpr
(
    id          bigint auto_increment
    primary key,
    lpr_date    date                               not null comment 'LPR报价日',
    one_year    varchar(10)                        not null comment '1年期，单位：百分比',
    five_year   varchar(10)                        not null comment '5年期，单位：百分比',
    create_time datetime default CURRENT_TIMESTAMP not null,
    create_by   bigint                             null comment '创建人id',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    update_by   bigint                             null
    )
    comment '基础数据-lpr';

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

create table if not exists business_status
(
    id            bigint auto_increment comment '主键'
    primary key,
    source_id     varchar(64)                                   not null comment '来源表id',
    source_type   int                                           not null comment '业务数据类型',
    source_status int                                           not null comment '业务数据枚举',
    biz_type      varchar(32)                                   null,
    is_delete     tinyint(4) unsigned default 0                 not null comment '是否删除',
    gmt_create    datetime            default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify    datetime            default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint uniq_source_id_type
    unique (source_id, source_type)
    )
    comment '业务状态表';

create table if not exists client
(
    id               bigint auto_increment
    primary key,
    client_code      varchar(50)                        null comment '客户编号',
    client_name      varchar(50)                        null comment '客户名称',
    client_type      varchar(20)                        null comment '客户分类，法人/自然人',
    client_status    varchar(20)                        null comment '客户状态',
    process_status   varchar(30)                        null comment '流程状态',
    usc_code         varchar(50)                        null comment '统一社会信用代码。Unified Social credit code',
    cert_type        varchar(20)                        null comment '证件类型',
    cert_number      varchar(50)                        null comment '证件号码',
    create_time      datetime default CURRENT_TIMESTAMP not null,
    create_by        bigint                             null comment '创建人id',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    create_by_dept   bigint                             null comment '创建部门',
    hand_import_flag tinyint  default 0                 null comment '汉得导入数据标志（1为导入）',
    newest_version   varchar(32)                        null comment '最新的版本',
    tyc_name         varchar(50)                        null comment '天眼查名称',
    constraint client_cert_number_uindex
    unique (cert_number),
    constraint usc_code_unique
    unique (usc_code)
    )
    comment '客户信息';

create table if not exists client_materials_list
(
    id             bigint auto_increment
    primary key,
    client_id      bigint                             null comment '客户id',
    materials_type varchar(50)                        null comment '资料类型',
    filename       varchar(100)                       null comment '附件名',
    file_path      varchar(200)                       null comment '文件url',
    create_time    datetime default CURRENT_TIMESTAMP null,
    create_by      bigint                             null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by      bigint                             null
    )
    comment '客户资料清单';

create table if not exists client_version
(
    id          bigint auto_increment comment '主键'
    primary key,
    client_id   bigint                                not null comment '客户id',
    version     varchar(32)                           not null comment '版本',
    type        tinyint                               not null comment '版本类型（1直接生效，2审批通过生效）',
    create_time datetime    default CURRENT_TIMESTAMP null,
    create_by   bigint                                null,
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by   bigint                                null,
    module      varchar(32) default 'CLIENT'          not null comment '业务模块枚举'
    )
    comment '客户信息版本表';

create index idx_client_id
    on client_version (client_id);

create table if not exists collection_base_info
(
    id                          bigint auto_increment comment '收款明细id	'
    primary key,
    contract_id                 bigint                                not null comment '合同id',
    contract_code               varchar(100)                          null comment '合同编号',
    client_id                   bigint                                null comment '客户id',
    code                        varchar(100)                          null comment '收款编号',
    write_off_status            varchar(20)                           null comment '核销状态',
    collection_date             datetime                              null comment '实收日期',
    collection_amount           bigint                                null comment '实收金额',
    create_by                   bigint                                null,
    create_time                 datetime    default CURRENT_TIMESTAMP null,
    update_by                   bigint                                null,
    update_time                 datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    penalty_interest            bigint                                null comment '罚息',
    payment_id                  bigint                                null comment '付款id',
    payment_code                varchar(100)                          null comment '付款code',
    phase                       int                                   null comment '期项',
    plan_collection_amount      varchar(100)                          null comment '计划收款金额',
    plan_collection_date        datetime                              null comment '计划收款日期',
    principal                   bigint                                null comment '本金',
    interest                    bigint                                null comment '利息',
    cash_flow_amount            bigint                                null comment '现金流金额',
    collection_principal        bigint                                null comment '实收本金',
    collection_interest         bigint                                null comment '实收利息',
    collection_penalty_interest bigint                                null comment '实收罚息',
    cash_flow_item              varchar(100)                          null comment '现金流项目',
    rent_actual_id              bigint                                null comment '实际租金表id',
    write_off_user_ids          varchar(100)                          null comment 'json数据 核销人员ids',
    comment                     varchar(100)                          null comment '罚息修改备注',
    penalty_interest_amount     bigint                                null comment '罚息金额',
    penalty_interest_update     varchar(10) default '0'               null comment '0 无修改，1修改过',
    all_record_sort             int         default 0                 null comment '核销记录序号最大值'
    )
    comment '收款明细表';

create table if not exists collection_overdue_record_info
(
    id                    bigint auto_increment comment '罚息记录明细id	'
    primary key,
    collection_id         bigint                             null comment '收款id',
    overdue_amount        bigint                             null comment '逾期金额',
    day_penalty_interest  bigint                             null comment '单日产生罚息',
    last_penalty_interest bigint                             null comment '罚息余额',
    create_by             bigint                             null,
    create_time           datetime default CURRENT_TIMESTAMP null,
    update_by             bigint                             null,
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    record_date           datetime                           null comment '记录日期'
)
    comment '逾期表';

create table if not exists collection_record_info
(
    id                bigint auto_increment comment '收款记录明细id	'
    primary key,
    collection_id     bigint                             null comment '收款核销明细id',
    data_source       varchar(100)                       null comment '信息来源',
    collection_type   varchar(20)                        null comment '收款类型',
    collection_date   datetime                           null comment '实收日期',
    collection_amount bigint                             null comment '实收金额',
    principal         bigint                             null comment '本金',
    interest          bigint                             null comment '利息',
    penalty_interest  bigint                             null comment '罚息',
    postscript        varchar(100)                       null comment '附言',
    enclosure_id      bigint                             null comment '附件id',
    enclosure_name    varchar(100)                       null comment '附件名',
    write_off_status  varchar(100)                       null comment '核销状态',
    client_id         bigint                             null comment '客户id',
    bank_account_id   bigint                             null comment '我方账户id',
    sort_id           int                                null comment '序号',
    source_flag       int      default 0                 null comment '来源标记',
    create_by         bigint                             null,
    create_time       datetime default CURRENT_TIMESTAMP null,
    update_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '收款记录明细表';

create table if not exists collection_write_off_record
(
    id             bigint auto_increment comment '核销记录id'
    primary key,
    collection_id  bigint                             null comment '归属id',
    record_id      bigint                             null comment '收款记录id',
    operate        varchar(20)                        not null comment '操作',
    receipt_status varchar(20)                        null comment '单据状态',
    create_by      bigint                             null,
    create_time    datetime default CURRENT_TIMESTAMP null,
    update_by      bigint                             null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    operate_info   varchar(100)                       null comment '被操作明细'
    )
    comment '核销记录表';

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

create table if not exists contract_account
(
    id              bigint auto_increment comment '方案id'
    primary key,
    contract_id     bigint                             not null comment '所属合同ID',
    client_id       bigint                             null comment '预留-客户id',
    client_name     varchar(200)                       null comment '客户名称',
    account_name    varchar(200)                       null comment '账户名称',
    account_num     varchar(30)                        null comment '银行账号',
    account_address varchar(200)                       null comment '开户行',
    create_by       bigint                             null comment '创建人id、发起人id',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by       bigint                             null comment '最后更新人id',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间'
    )
    comment '合同-收款账户表';

create table if not exists contract_account_lib
(
    id               bigint auto_increment comment '方案id'
    primary key,
    contract_id      bigint                             not null comment '所属合同ID',
    client_id        bigint                             null comment '预留-客户id',
    client_name      varchar(200)                       null comment '客户名称',
    account_name     varchar(200)                       null comment '账户名称',
    account_num      varchar(30)                        null comment '银行账号',
    account_address  varchar(200)                       null comment '开户行',
    create_by        bigint                             null comment '创建人id、发起人id',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by        bigint                             null comment '最后更新人id',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间',
    version          varchar(40)                        not null comment '版本号',
    origin_id        bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '合同-收款账户表';

create table if not exists contract_aoc_price
(
    id                       bigint auto_increment comment 'id	'
    primary key,
    contract_id              bigint                             not null comment '所属合同ID',
    apply_credit_amount      bigint                             null comment '申报授信金额',
    proj_credit_amount       bigint                             null comment '项目金额',
    lease_month_count        int                                null comment '租赁期限月数',
    repay_rate               varchar(20)                        null comment '还款频率。按月，按季，按年，不规则',
    repay_type               varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    repay_times_total        int                                null comment '还款期数',
    lease_rate_percent       bigint                             null comment '项目租赁利率值。百分之多少',
    lpr_type                 varchar(20)                        null comment 'lpr品种 一年期，五年期',
    lpr_percent              int                                null comment 'lpr',
    lpr_add_percent          int                                null comment '加点',
    before_lpr_percent       int                                null comment '租前lpr',
    before_rate_type         varchar(10)                        null comment '租前利率类型。固定利率：fixed、浮动利率：float	',
    before_lpr_type          varchar(20)                        null comment '租前lpr品种 一年期，五年期',
    proj_irr_percent         bigint                             null comment '项目内部收益率',
    before_lpr_add_percent   int                                null comment '租前加点',
    aoc_credit_term          int                                null comment '转让额度有效期（月',
    rate_type                varchar(10)                        null comment '转让费率类型。固定利率：fixed、浮动利率：float	',
    credit_amount_loop       tinyint(1)                         null comment '额度是否可循环',
    earnest_money            bigint                             null comment '保证金',
    proj_earnest_money       bigint                             null comment '项目保证金',
    confirmatory_party       text                               null comment '确权方',
    consulting_fee           bigint                             null comment '服务费/咨询费',
    aoc_financing_proportion int                                null comment '转让融资比例',
    summary                  text                               null comment '拟转应收账款概述',
    aoc_rate_percent         int                                null comment '转让费率值。百分之多少',
    irr_percent              int                                null comment '内部收益率。百分之多少',
    planned_starting_date    date                               null comment '计划起租日',
    rental_calc_type         varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                bigint                             null,
    create_time              datetime default CURRENT_TIMESTAMP null,
    update_by                bigint                             null,
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同-债权转让报价方案表';

create table if not exists contract_base_info
(
    id                             bigint auto_increment
    primary key,
    client_id                      bigint                             null comment '客户ID',
    contract_code                  varchar(50)                        null comment '合同编号',
    apply_credit_amount            bigint                             null comment '项目金额',
    remain_available_quota         bigint                             null comment '剩余可用额度(元)',
    proj_name                      varchar(200)                       null comment '项目名称',
    proj_code                      varchar(20)                        null comment '项目编号',
    biz_type                       varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    lease_type                     varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    project_type                   varchar(30)                        null comment '项目类型：公共事业类、省内国（央）企、其他',
    risk_level                     varchar(20)                        null comment '风险等级',
    proj_source                    varchar(20)                        null comment '项目来源：存量翻单、渠道介绍、自主开发',
    funds_purpose                  varchar(200)                       null comment '资金用途',
    proj_background                text                               null comment '项目背景',
    proj_sponsor_user_id           bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids        json                               null comment '项目协办方用户id列表',
    biz_dept_id                    bigint                             null comment '业务部门id',
    biz_dept_leader_id             bigint                             null comment '业务部门负责人id',
    biz_division_leader_id         bigint                             null comment '业务分管领导id',
    proj_review_id                 bigint                             null comment '关联的评审ID',
    contract_process_status        varchar(20)                        null comment '流程状态',
    contract_status                varchar(20)                        null comment '合同状态',
    create_by                      bigint                             null comment '创建人、发起人',
    create_time                    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                      bigint                             null comment '最后更新人id',
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    estimated_lease_date           datetime                           null comment '概算起租日',
    actual_lease_date              datetime                           null comment '实际起租日',
    actual_finish_date             datetime                           null comment '实际结束日',
    payment_plan_date              datetime                           null comment '计划付款日期',
    payment_plan_amount            bigint                             null comment '计划付款金额-合同金额',
    payment_count                  int(11) unsigned zerofill          null comment '支付申请次数',
    proj_item                      varchar(30)                        null comment '项目类型',
    risk_control_manager_id        bigint                             null comment '风控经理id',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    contract_process_change_status varchar(50)                        null comment '流程变更子类型状态'
    )
    comment '合同基本信息表';

create table if not exists contract_base_info_lib
(
    id                             bigint auto_increment
    primary key,
    client_id                      bigint                             null comment '客户ID',
    contract_code                  varchar(50)                        null comment '合同编号',
    apply_credit_amount            bigint                             null comment '合同金额',
    remain_available_quota         bigint                             null comment '剩余可用额度(元)',
    proj_name                      varchar(200)                       null comment '项目名称',
    proj_code                      varchar(20)                        null comment '项目编号',
    biz_type                       varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    lease_type                     varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    project_type                   varchar(30)                        null comment '项目类型：公共事业类、省内国（央）企、其他',
    risk_level                     varchar(20)                        null comment '风险等级',
    proj_source                    varchar(20)                        null comment '项目来源：存量翻单、渠道介绍、自主开发',
    funds_purpose                  varchar(200)                       null comment '资金用途',
    proj_background                text                               null comment '项目背景',
    proj_sponsor_user_id           bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids        json                               null comment '项目协办方用户id列表',
    biz_dept_id                    bigint                             null comment '业务部门id',
    biz_dept_leader_id             bigint                             null comment '业务部门负责人id',
    biz_division_leader_id         bigint                             null comment '业务分管领导id',
    proj_review_id                 bigint                             null comment '关联的评审ID',
    contract_process_status        varchar(20)                        null comment '流程状态',
    contract_status                varchar(20)                        null comment '合同状态',
    create_by                      bigint                             null comment '创建人、发起人',
    create_time                    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                      bigint                             null comment '最后更新人id',
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    estimated_lease_date           datetime                           null comment '概算起租日',
    actual_lease_date              datetime                           null comment '实际起租日',
    actual_finish_date             datetime                           null comment '实际结束日',
    payment_plan_date              datetime                           null comment '计划付款日期',
    payment_plan_amount            bigint                             null comment '计划付款金额-合同金额',
    payment_count                  int(11) unsigned zerofill          null comment '支付申请次数',
    proj_item                      varchar(30)                        null comment '项目类型',
    risk_control_manager_id        bigint                             null comment '风控经理id',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    contract_process_change_status varchar(50)                        null comment '流程变更子类型状态',
    version                        varchar(40)                        not null comment '版本号',
    origin_id                      bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time               datetime                           null,
    data_create_by                 bigint                             null,
    data_update_time               datetime                           null,
    data_update_by                 bigint                             null
    )
    comment '合同基本信息表';

create table if not exists contract_factoring_price
(
    id                             bigint auto_increment comment 'id	'
    primary key,
    contract_id                    bigint                             not null comment '所属合同ID',
    apply_credit_amount            bigint                             null comment '申报授信金额',
    factoring_credit_term          int                                null comment '保理额度有效期（月',
    repay_type                     varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    earnest_money                  bigint                             null comment '保证金',
    confirmatory_party             text                               null comment '确权方',
    factoring_financing_proportion int                                null comment '保理融资比例',
    consulting_fee                 bigint                             null comment '服务费/咨询费',
    summary                        text                               null comment '拟转应收账款概述',
    rate_type                      varchar(10)                        null comment '保理费率类型。固定利率：fixed、浮动利率：float	',
    factoring_rate_percent         int                                null comment '保理费率值。百分之多少',
    irr_percent                    int                                null comment '内部收益率。百分之多少',
    existing_lease_credit          bit      default b'0'              null comment '是否有存续租赁授信',
    new_lease_credit               bit      default b'0'              null comment '是否新增租赁授信',
    planned_starting_date          date                               null comment '计划起租日',
    rental_calc_type               varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    proj_credit_amount             bigint                             null comment '项目金额',
    lease_month_count              int                                null comment '租赁期限月数',
    repay_rate                     varchar(20)                        null comment '还款频率。按月，按季，按年，不规则',
    repay_times_total              int                                null comment '还款期数',
    lease_rate_percent             bigint                             null comment '项目租赁利率值',
    lpr_type                       varchar(20)                        null comment 'lpr品种 一年期，五年期',
    lpr_percent                    int                                null comment 'lpr',
    lpr_add_percent                int                                null comment '加点',
    before_lpr_percent             int                                null comment '租前lpr',
    before_lpr_add_percent         int                                null comment '租前加点',
    before_rate_type               varchar(10)                        null comment '租前利率类型。固定利率：fixed、浮动利率：float	',
    before_lpr_type                varchar(20)                        null comment '租前lpr品种 一年期，五年期',
    proj_irr_percent               bigint                             null comment '项目irr',
    proj_earnest_money             bigint                             null comment '项目保证金',
    create_by                      bigint                             null,
    create_time                    datetime default CURRENT_TIMESTAMP null,
    update_by                      bigint                             null,
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同-保理报价方案表';

create table if not exists contract_guarantor
(
    id               bigint auto_increment comment 'id'
    primary key,
    contract_id      bigint                             not null comment '所属合同id',
    relat_contracts  json                               null comment '关联合同ID',
    guarantor_type   varchar(20)                        null comment '担保人类型：自然人、法人',
    guarantor_ids    json                               null comment '担保人id',
    guarantee_method varchar(50)                        null comment '担保方式-连带责任担保、一般担保',
    is_report        tinyint(1)                         null comment '是否上报征信 0不上报，1上报',
    create_by        bigint                             null comment '创建人id、发起人id',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by        bigint                             null comment '最后更新人id',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间'
    )
    comment '合同-担保措施';

create table if not exists contract_guarantor_lib
(
    id               bigint auto_increment comment 'id'
    primary key,
    contract_id      bigint                             not null comment '所属合同id',
    relat_contracts  json                               null comment '关联合同ID',
    guarantor_type   varchar(20)                        null comment '担保人类型：自然人、法人',
    guarantor_ids    json                               null comment '担保人id',
    guarantee_method varchar(50)                        null comment '担保方式-连带责任担保、一般担保',
    is_report        tinyint(1)                         null comment '是否上报征信 0不上报，1上报',
    create_by        bigint                             null comment '创建人id、发起人id',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by        bigint                             null comment '最后更新人id',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间',
    version          varchar(40)                        not null comment '版本号',
    origin_id        bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '合同-担保措施';

create table if not exists contract_lease_item
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                        not null comment '合同id',
    sequence            int(10)                                null comment '序号',
    name                varchar(100) default ''                null comment '设备名称',
    type                varchar(50)  default ''                null comment '设备类型',
    model               varchar(50)  default ''                null comment '规格型号',
    supplier            varchar(100) default ''                null comment '供应商',
    quantity            varchar(20)  default ''                null comment '数量',
    unit                varchar(10)  default ''                null comment '计量单位',
    purchase_date       varchar(50)  default ''                null comment '购置日期',
    original_book_value bigint unsigned                        null comment '账面原值',
    assessed_value      bigint unsigned                        null comment '评估价值',
    original_book_net_value      bigint unsigned                        null comment '评估价值',
    invoice_code        varchar(1000)  default ''                null comment '发票号',
    car_vin_code        varchar(1000)  default ''                null comment '车架号',
    fixed_assets_code        varchar(1000)  default ''                null comment '固定资产编号',
    storage_place       varchar(50)  default ''                null comment '存放地点',
    create_by           bigint                                 null comment '创建人、发起人',
    create_time         datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                                 null comment '最后更新人id',
    update_time         datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同明细-租赁物清单';

create table if not exists contract_lease_item_lib
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                        not null comment '合同id',
    sequence            varchar(10)                            null comment '序号',
    name                varchar(100) default ''                null comment '设备名称',
    type                varchar(50)  default ''                null comment '设备类型',
    model               varchar(50)  default ''                null comment '规格型号',
    supplier            varchar(100) default ''                null comment '供应商',
    quantity            varchar(20)  default ''                null comment '数量',
    unit                varchar(10)  default ''                null comment '计量单位',
    purchase_date       varchar(50)  default ''                null comment '购置日期',
    original_book_value bigint unsigned                        null comment '账面原值',
    assessed_value      bigint unsigned                        null comment '评估价值',
    original_book_net_value      bigint unsigned                        null comment '评估价值',
    invoice_code        varchar(1000)  default ''                null comment '发票号',
    car_vin_code        varchar(1000)  default ''                null comment '车架号',
    fixed_assets_code        varchar(1000)  default ''                null comment '固定资产编号',
    storage_place       varchar(50)  default ''                null comment '存放地点',
    create_by           bigint                                 null comment '创建人、发起人',
    create_time         datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                                 null comment '最后更新人id',
    update_time         datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version             varchar(40)                            not null comment '版本号',
    origin_id           bigint                                 not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time    datetime                               null,
    data_create_by      bigint                                 null,
    data_update_time    datetime                               null,
    data_update_by      bigint                                 null
    )
    comment '合同明细-租赁物清单';

create table if not exists contract_lease_price
(
    id                     bigint auto_increment comment '租赁报价方案id	'
    primary key,
    contract_id            bigint                             not null comment '所属合同ID',
    apply_credit_amount    bigint                             null comment '申报授信金额-合同金额',
    lease_month_count      int                                null comment '租赁期限月数',
    proj_lease_month_count int                                null comment '项目租赁期限月数',
    repay_rate             varchar(20)                        null comment '还款频率。按月，按季，按年，不规则',
    repay_times_total      int                                null comment '还款期数',
    pay_type               varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type       varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    earnest_money          bigint                             null comment '保证金',
    proj_down_payment      bigint                             null comment '项目首期租金',
    down_payment           bigint                             null comment '首付款',
    consulting_fee         bigint                             null comment '服务费/咨询费',
    proj_consulting_fee    bigint                             null comment '项目服务费/咨询费',
    nominal_price          bigint                             null comment '名义货价',
    rate_type              varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lpr_type               varchar(20)                        null comment 'lpr品种 一年期，五年期',
    lpr_percent            int                                null comment 'lpr',
    lpr_add_percent        int                                null comment '加点',
    before_lpr_percent     int                                null comment '租前lpr',
    before_rate_type       varchar(10)                        null comment '租前利率类型。固定利率：fixed、浮动利率：float	',
    before_lpr_type        varchar(20)                        null comment '租前lpr品种 一年期，五年期',
    before_lpr_add_percent int                                null comment '租前加点',
    default_interest_rate  int                                null comment '罚息日利率',
    irr_percent            int                                null comment '内部收益率。百分之多少',
    lease_rate_percent     bigint                             null comment '项目租赁利率值',
    proj_earnest_money     bigint                             null comment '项目保证金',
    proj_credit_amount     bigint                             null comment '项目金额',
    proj_irr_percent       bigint                             null comment '项目irr',
    credit_amount_loop     tinyint(1)                         null comment '额度是否可循环',
    create_by              bigint                             null comment '创建人id、发起人id	',
    create_time            datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by              bigint                             null comment '最后更新人id	',
    update_time            datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    constraint uniq_contract_id
    unique (contract_id) comment '每个合同只能有一份报价'
    )
    comment '合同-租赁报价方案表';

create table if not exists contract_lease_price_lib
(
    id                     bigint auto_increment comment '租赁报价方案id	'
    primary key,
    contract_id            bigint                             not null comment '所属合同ID',
    apply_credit_amount    bigint                             null comment '申报授信金额-合同金额',
    lease_month_count      int                                null comment '租赁期限月数',
    proj_lease_month_count int                                null comment '项目租赁期限月数',
    repay_rate             varchar(20)                        null comment '还款频率。按月，按季，按年，不规则',
    repay_times_total      int                                null comment '还款期数',
    pay_type               varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type       varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    earnest_money          bigint                             null comment '保证金',
    proj_down_payment      bigint                             null comment '项目首期租金',
    down_payment           bigint                             null comment '首付款',
    consulting_fee         bigint                             null comment '服务费/咨询费',
    proj_consulting_fee    bigint                             null comment '项目服务费/咨询费',
    nominal_price          bigint                             null comment '名义货价',
    rate_type              varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lpr_type               varchar(20)                        null comment 'lpr品种 一年期，五年期',
    lpr_percent            int                                null comment 'lpr',
    lpr_add_percent        int                                null comment '加点',
    before_lpr_percent     int                                null comment '租前lpr',
    before_rate_type       varchar(10)                        null comment '租前利率类型。固定利率：fixed、浮动利率：float	',
    before_lpr_type        varchar(20)                        null comment '租前lpr品种 一年期，五年期',
    before_lpr_add_percent int                                null comment '租前加点',
    default_interest_rate  int                                null comment '罚息日利率',
    irr_percent            int                                null comment '内部收益率。百分之多少',
    lease_rate_percent     bigint                             null comment '项目租赁利率值',
    proj_earnest_money     bigint                             null comment '项目保证金',
    proj_credit_amount     bigint                             null comment '项目金额',
    proj_irr_percent       bigint                             null comment '项目irr',
    credit_amount_loop     tinyint(1)                         null comment '额度是否可循环',
    create_by              bigint                             null comment '创建人id、发起人id	',
    create_time            datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by              bigint                             null comment '最后更新人id	',
    update_time            datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    version                varchar(40)                        not null comment '版本号',
    origin_id              bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time       datetime                           null,
    data_create_by         bigint                             null,
    data_update_time       datetime                           null,
    data_update_by         bigint                             null
    )
    comment '合同-租赁报价方案表';

create table if not exists contract_mortgage
(
    id                bigint auto_increment comment 'id'
    primary key,
    contract_id       bigint                             not null comment '所属合同id',
    file_id           bigint                             null comment '抵押物清单id',
    relat_contracts   json                               null comment '关联合同编号',
    mortgage_type     varchar(30)                        null comment '质押人类型',
    mortgage_ids      json                               null comment '抵押人id,姓名，类型',
    mortgage_describe varchar(200)                       null comment '抵押物描述',
    appraisal_company varchar(200)                       null comment '评估公司',
    appraisal_code    varchar(200)                       null comment '评估编号',
    highest           tinyint                            null comment '是否最高额标识，0-否，1-是',
    assess            tinyint                            null comment '是否评估，0-否，1-是',
    assess_date       date                               null comment '评估日期',
    create_by         bigint                             null comment '创建人id、发起人id',
    create_time       datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by         bigint                             null comment '最后更新人id',
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间'
    )
    comment '合同-抵押措施';

CREATE TABLE if not exists `contract_mortgage_item` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) DEFAULT NULL COMMENT '合同id',
  `mortgage_id` bigint(20) unsigned NOT NULL COMMENT '抵押措施id',
  `sequence` varchar(10) DEFAULT NULL COMMENT '序号',
  `category` varchar(100) DEFAULT NULL COMMENT '种类',
  `unique_identify_code` varchar(1000) DEFAULT NULL COMMENT '唯一识别号',
  `unique_identify_code_type` varchar(100) DEFAULT NULL COMMENT '唯一识别号类型',
  `name` varchar(100) DEFAULT '' COMMENT '设备名称',
  `supplier` varchar(100) DEFAULT '' COMMENT '供应商',
  `quantity` varchar(20) DEFAULT '' COMMENT '数量',
  `unit` varchar(10) DEFAULT '' COMMENT '计量单位',
  `purchase_date` varchar(50) DEFAULT '' COMMENT '购置日期',
  `original_book_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面原值',
  `original_book_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面净值',
  `assessed_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估原值',
  `assessed_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估净值',
  `invoice_code` varchar(1000) DEFAULT '' COMMENT '发票号',
  `storage_place` varchar(50) DEFAULT '' COMMENT '存放地点',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抵押措施-抵押物清单';

CREATE TABLE if not exists `contract_mortgage_item_lib` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) DEFAULT NULL,
  `mortgage_id` bigint(20) unsigned NOT NULL COMMENT '抵押措施id',
  `sequence` varchar(10) DEFAULT NULL COMMENT '序号',
  `category` varchar(100) DEFAULT NULL COMMENT '种类',
  `unique_identify_code` varchar(1000) DEFAULT NULL COMMENT '唯一识别号',
  `unique_identify_code_type` varchar(100) DEFAULT NULL COMMENT '唯一识别号类型',
  `name` varchar(100) DEFAULT '' COMMENT '设备名称',
  `supplier` varchar(100) DEFAULT '' COMMENT '供应商',
  `quantity` varchar(20) DEFAULT '' COMMENT '数量',
  `unit` varchar(10) DEFAULT '' COMMENT '计量单位',
  `purchase_date` varchar(50) DEFAULT '' COMMENT '购置日期',
  `original_book_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面原值',
  `original_book_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面净值',
  `assessed_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估价值',
  `assessed_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估净值',
  `invoice_code` varchar(1000) DEFAULT '' COMMENT '发票号',
  `storage_place` varchar(50) DEFAULT '' COMMENT '存放地点',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抵押措施-抵押物清单';

create table if not exists contract_mortgage_lib
(
    id                bigint auto_increment comment 'id'
    primary key,
    contract_id       bigint                             not null comment '所属合同id',
    file_id           bigint                             null comment '抵押物清单id',
    relat_contracts   json                               null comment '关联合同编号',
    mortgage_type     varchar(30)                        null comment '质押人类型',
    mortgage_ids      json                               null comment '抵押人id,姓名，类型',
    mortgage_describe varchar(200)                       null comment '抵押物描述',
    appraisal_company varchar(200)                       null comment '评估公司',
    appraisal_code    varchar(200)                       null comment '评估编号',
    highest           tinyint                            null comment '是否最高额标识，0-否，1-是',
    assess            tinyint                            null comment '是否评估，0-否，1-是',
    assess_date       date                               null comment '评估日期',
    create_by         bigint                             null comment '创建人id、发起人id',
    create_time       datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by         bigint                             null comment '最后更新人id',
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间',
    version           varchar(40)                        not null comment '版本号',
    origin_id         bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time  datetime                           null,
    data_create_by    bigint                             null,
    data_update_time  datetime                           null,
    data_update_by    bigint                             null
    )
    comment '合同-抵押措施';

create table if not exists contract_pledge
(
    id              bigint auto_increment comment 'id	'
    primary key,
    contract_id     bigint                             not null comment '所属合同id',
    pledge_Type     varchar(20)                        null comment '质押人类型',
    pledge_ids      json                               null comment '质押人id',
    pledge_describe varchar(200)                       null comment '质押物描述',
    create_by       bigint                             null comment '创建人id、发起人id	',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by       bigint                             null comment '最后更新人id	',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	'
    )
    comment '合同-质押措施';

create table if not exists contract_pledge_lib
(
    id               bigint auto_increment comment 'id	'
    primary key,
    contract_id      bigint                             not null comment '所属合同id',
    pledge_Type      varchar(20)                        null comment '质押人类型',
    pledge_ids       json                               null comment '质押人id',
    pledge_describe  varchar(200)                       null comment '质押物描述',
    create_by        bigint                             null comment '创建人id、发起人id	',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by        bigint                             null comment '最后更新人id	',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    version          varchar(40)                        not null comment '版本号',
    origin_id        bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '合同-质押措施';

create table if not exists contract_prepayment
(
    id                       bigint auto_increment comment 'id	'
    primary key,
    contract_id              bigint                             not null comment '所属合同ID',
    applay_repayment_date    datetime                           null comment '申请还款日期',
    unpaid_rent_due          bigint                             not null comment '到期未付租金',
    penalty                  bigint                             null comment '违约金',
    early_repayment          bigint                             not null comment '提前归还本金',
    early_repayment_interest bigint                             null comment '提前归还利息',
    loss                     bigint                             null comment '提前终止补偿金',
    unpaid_rent              bigint                             null comment '到期未付租金',
    create_by                bigint                             null comment '创建人id、发起人id	',
    create_time              datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by                bigint                             null comment '最后更新人id	',
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    constraint uniq_contract_id
    unique (contract_id) comment '每个合同只能有一份报价'
    )
    comment '合同-提前还款表';

create table if not exists contract_receipt
(
    id                 bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id        bigint unsigned                       not null comment '合同id',
    payment_apply_code varchar(20) default ''                null comment '付款申请编号',
    create_by          bigint                                null comment '创建人、发起人',
    create_time        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by          bigint                                null comment '最后更新人id',
    update_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同明细-借据';

create table if not exists contract_receipt_lib
(
    id                 bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id        bigint unsigned                       not null comment '合同id',
    payment_apply_code varchar(20) default ''                null comment '付款申请编号',
    create_by          bigint                                null comment '创建人、发起人',
    create_time        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by          bigint                                null comment '最后更新人id',
    update_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version            varchar(40)                           not null comment '版本号',
    origin_id          bigint                                not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time   datetime                              null,
    data_create_by     bigint                                null,
    data_update_time   datetime                              null,
    data_update_by     bigint                                null
    )
    comment '合同明细-借据';

create table if not exists contract_remind_record
(
    id                   bigint auto_increment
    primary key,
    contract_id          bigint                                not null comment '合同id',
    contract_code        varchar(50) default ''                not null comment '合同编号',
    payment_id           bigint                                not null comment '付款id',
    payment_code         varchar(50) default ''                not null comment '付款编号',
    contract_creator_id  bigint                                not null comment '合同创建人id',
    contract_create_time datetime                              not null comment '合同创建时间',
    create_by            bigint                                null comment '创建人、发起人',
    create_time          datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by            bigint                                null comment '最后更新人id',
    update_time          datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    )
    comment '合同起租提醒记录表';

create index idx_contract_creator
    on contract_remind_record (contract_creator_id);

create index idx_contract_id
    on contract_remind_record (contract_id);

create table if not exists contract_rent_actual
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                       not null comment '合同id',
    receipt_id          bigint unsigned                       not null comment '借据id',
    cash_flow_code      varchar(30) default ''                null comment '现金流编号',
    cash_flow_date      date                                  null comment '日期',
    cash_flow_phase     tinyint(4) unsigned                   null comment '期项',
    rent                bigint                                null comment '租金',
    principal           bigint                                null comment '本金',
    interest            bigint                                null comment '利息',
    remaining_principal bigint                                null comment '剩余本金',
    create_by           bigint                                null comment '创建人、发起人',
    create_time         datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                                null comment '最后更新人id',
    update_time         datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同明细-实际租金';

create table if not exists contract_rent_actual_lib
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                       not null comment '合同id',
    receipt_id          bigint unsigned                       not null comment '借据id',
    cash_flow_code      varchar(30) default ''                null comment '现金流编号',
    cash_flow_date      date                                  null comment '日期',
    cash_flow_phase     tinyint(4) unsigned                   null comment '期项',
    rent                bigint                                null comment '租金',
    principal           bigint                                null comment '本金',
    interest            bigint                                null comment '利息',
    remaining_principal bigint                                null comment '剩余本金',
    create_by           bigint                                null comment '创建人、发起人',
    create_time         datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                                null comment '最后更新人id',
    update_time         datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version             varchar(40)                           not null comment '版本号',
    origin_id           bigint                                not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time    datetime                              null,
    data_create_by      bigint                                null,
    data_update_time    datetime                              null,
    data_update_by      bigint                                null
    )
    comment '合同明细-实际租金';

create table if not exists contract_rent_estimate
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                    not null comment '合同id',
    cash_flow_date      date                               null comment '日期',
    cash_flow_phase     tinyint(4) unsigned                null comment '期项',
    rent                bigint                             null comment '租金',
    principal           bigint                             null comment '本金',
    interest            bigint                             null comment '利息',
    remaining_principal bigint                             null comment '剩余本金',
    create_by           bigint                             null comment '创建人、发起人',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                             null comment '最后更新人id',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同明细-概算租金';

create table if not exists contract_rent_estimate_lib
(
    id                  bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id         bigint unsigned                    not null comment '合同id',
    cash_flow_date      date                               null comment '日期',
    cash_flow_phase     tinyint(4) unsigned                null comment '期项',
    rent                bigint                             null comment '租金',
    principal           bigint                             null comment '本金',
    interest            bigint                             null comment '利息',
    remaining_principal bigint                             null comment '剩余本金',
    create_by           bigint                             null comment '创建人、发起人',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                             null comment '最后更新人id',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version             varchar(40)                        not null comment '版本号',
    origin_id           bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time    datetime                           null,
    data_create_by      bigint                             null,
    data_update_time    datetime                           null,
    data_update_by      bigint                             null
    )
    comment '合同明细-概算租金';

create table if not exists contract_settle_plan
(
    id                        bigint unsigned auto_increment comment '主键id'
    primary key,
    contract_id               bigint unsigned                    not null comment '合同id',
    settle_type               varchar(20)                        not null comment '结清类型',
    outstanding_rent          bigint unsigned                    not null comment '到期未付租金',
    before_maturity_principal bigint unsigned                    not null comment '未到期本金',
    loss                      bigint unsigned                    null comment '损失金',
    liquidated_damages        bigint unsigned                    null comment '违约金',
    earnest_balance           bigint unsigned                    null comment '保证金余额',
    is_earnest_deduction      tinyint                            not null comment '保证金是否内扣',
    nominal_price             bigint unsigned                    null comment '名义货价',
    apply_derate_amount       bigint unsigned                    null comment '申请减免金额',
    original_deadline         date                               not null comment '原到期日',
    total_amount              bigint unsigned                    null comment '合计金额',
    apply_settle_date         date                               null comment '申请结清日期',
    create_by                 bigint                             null comment '创建人、发起人',
    create_time               datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                 bigint                             null comment '最后更新人id',
    update_time               datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '合同明细-结清方案';

create table if not exists contract_tenantry
(
    id                  bigint auto_increment comment 'id	'
    primary key,
    contract_id         bigint                             not null comment '所属合同ID',
    lessee_id           bigint                             null comment '承租人id',
    lessee_type         varchar(20)                        null comment '承租人类型 联合承租人,主承租人',
    lessee_name         varchar(200)                       null comment '承租人名称',
    stock_risk_exposure bigint                             null comment '存量风险敞口',
    contact_id          bigint                             null comment '联系人id',
    contact_info        json                               null comment '指定联系人',
    is_report           tinyint(1)                         null comment '是否上报征信 0不上报，1上报',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	'
    )
    comment '合同-承租人表';

create table if not exists contract_tenantry_lib
(
    id                  bigint auto_increment comment 'id	'
    primary key,
    contract_id         bigint                             not null comment '所属合同ID',
    lessee_id           bigint                             null comment '承租人id',
    lessee_type         varchar(20)                        null comment '承租人类型 联合承租人,主承租人',
    lessee_name         varchar(200)                       null comment '承租人名称',
    stock_risk_exposure bigint                             null comment '存量风险敞口',
    contact_id          bigint                             null comment '联系人id',
    contact_info        json                               null comment '指定联系人',
    is_report           tinyint(1)                         null comment '是否上报征信 0不上报，1上报',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    version             varchar(40)                        not null comment '版本号',
    origin_id           bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time    datetime                           null,
    data_create_by      bigint                             null,
    data_update_time    datetime                           null,
    data_update_by      bigint                             null
    )
    comment '合同-承租人表';

create table if not exists corp_address_info
(
    id           bigint auto_increment
    primary key,
    client_id    bigint                             not null comment '客户id',
    address_type varchar(20)                        null comment '地址类型',
    country      varchar(20)                        null comment '国家',
    province     varchar(20)                        null comment '省份',
    city         varchar(20)                        null comment '城市',
    district     varchar(20)                        null comment '区、县',
    detail       varchar(200)                       null comment '详细地址',
    region_code  varchar(50)                        null comment '区域代码',
    create_time  datetime default CURRENT_TIMESTAMP null,
    create_by    bigint                             null,
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by    bigint                             null
    )
    comment '法人地址信息';

create table if not exists corp_address_info_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             not null comment '客户id',
    address_type     varchar(20)                        null comment '地址类型',
    country          varchar(20)                        null comment '国家',
    province         varchar(20)                        null comment '省份',
    city             varchar(20)                        null comment '城市',
    district         varchar(20)                        null comment '区、县',
    detail           varchar(200)                       null comment '详细地址',
    region_code      varchar(50)                        null comment '区域代码',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '法人地址信息';

create table if not exists corp_bank_account
(
    id             bigint auto_increment
    primary key,
    client_id      bigint                             null comment '客户id',
    main_account   tinyint(1)                         null comment '是否主账号',
    account_name   varchar(50)                        null comment '账号名称',
    account_number varchar(50)                        null comment '银行账号',
    account_bank   varchar(50)                        null comment '开户行',
    create_time    datetime default CURRENT_TIMESTAMP null,
    create_by      bigint                             null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by      bigint                             null
    )
    comment '法人银行账户';

create table if not exists corp_bank_account_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             null comment '客户id',
    main_account     tinyint(1)                         null comment '是否主账号',
    account_name     varchar(50)                        null comment '账号名称',
    account_number   varchar(50)                        null comment '银行账号',
    account_bank     varchar(50)                        null comment '开户行',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '法人银行账户';

create table if not exists corp_bond_info
(
    id              bigint auto_increment
    primary key,
    client_id       bigint                             null comment '客户id',
    rate_date       date                               null comment '评级日期',
    rate_company    varchar(50)                        null comment '评级公司',
    rate            varchar(20)                        null comment '评级',
    rate_future     varchar(100)                       null comment '评级展望',
    issue_total     bigint                             null comment '发行总额，单位亿元',
    issue_amount    bigint                             null comment '发行只数',
    stock_scale     bigint                             null comment '存量规模，单位：亿元',
    stock_amount    bigint                             null comment '存量只数',
    maturity_scale  bigint                             null comment '到期规模，单位：亿元',
    maturity_amount bigint                             null comment '到期只数',
    create_time     datetime default CURRENT_TIMESTAMP null,
    create_by       bigint                             null,
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by       bigint                             null
    )
    comment '法人股票信息';

create table if not exists corp_bond_info_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             null comment '客户id',
    rate_date        date                               null comment '评级日期',
    rate_company     varchar(50)                        null comment '评级公司',
    rate             varchar(20)                        null comment '评级',
    rate_future      varchar(100)                       null comment '评级展望',
    issue_total      bigint                             null comment '发行总额，单位亿元',
    issue_amount     bigint                             null comment '发行只数',
    stock_scale      bigint                             null comment '存量规模，单位：亿元',
    stock_amount     bigint                             null comment '存量只数',
    maturity_scale   bigint                             null comment '到期规模，单位：亿元',
    maturity_amount  bigint                             null comment '到期只数',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '法人股票信息';

create table if not exists corp_commerce_info
(
    id                     bigint auto_increment
    primary key,
    client_id              bigint                             not null comment '客户id',
    triple_cert_in_one     tinyint(1)                         null comment '三证合一',
    zhong_zheng_code       varchar(50)                        null comment '中征码',
    org_code               varchar(50)                        null comment '组织机构代码',
    biz_license_code       varchar(50)                        null comment '营业执照号',
    continuous_status      varchar(20)                        null comment '存续状态',
    establish_date         date                               null comment '成立日期',
    approval_date          date                               null comment '核准日期',
    biz_licence_long_term  tinyint(1)                         null comment '营业许可证是否为长期',
    biz_license_end_date   date                               null comment '营业许可证到期日(如果许可证是非长期类型)',
    biz_scope              text                               null comment '业务范围',
    industry_type          varchar(50)                        null comment '行业分类',
    economy_type           varchar(50)                        null comment '经济类型',
    org_type               varchar(20)                        null comment '组织机构类型',
    org_scale              varchar(20)                        null comment '企业规模',
    register_currency_type varchar(20)                        null comment '注册币种',
    register_capital       bigint                             null comment '注册资本',
    real_currency_type     varchar(20)                        null comment '实收币种',
    real_capital           bigint                             null comment '实收资本',
    register_capital_rate  bigint                             null comment '注册资本到位率',
    corp_represent         varchar(20)                        null comment '法人代表',
    corp_gender            varchar(20)                        null comment '法人性别',
    corp_cert_type         varchar(20)                        null comment '法人证件类型',
    corp_cert_code         varchar(50)                        null comment '法人证件号码',
    listed_company         tinyint(1)                         null comment '是否上市公司',
    create_time            datetime default CURRENT_TIMESTAMP null,
    create_by              bigint                             null comment '创建人',
    update_time            datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by              bigint                             null,
    client_code            varchar(50)                        null comment '客户编号（冗余）',
    constraint client_id
    unique (client_id),
    constraint org_code
    unique (org_code),
    constraint zhong_zheng_code
    unique (zhong_zheng_code)
    )
    comment '法人工商信息表';

create table if not exists corp_commerce_info_lib
(
    id                     bigint auto_increment
    primary key,
    client_id              bigint                             not null comment '客户id',
    triple_cert_in_one     tinyint(1)                         null comment '三证合一',
    zhong_zheng_code       varchar(50)                        null comment '中征码',
    org_code               varchar(50)                        null comment '组织机构代码',
    biz_license_code       varchar(50)                        null comment '营业执照号',
    continuous_status      varchar(20)                        null comment '存续状态',
    establish_date         date                               null comment '成立日期',
    approval_date          date                               null comment '核准日期',
    biz_licence_long_term  tinyint(1)                         null comment '营业许可证是否为长期',
    biz_license_end_date   date                               null comment '营业许可证到期日(如果许可证是非长期类型)',
    biz_scope              text                               null comment '业务范围',
    industry_type          varchar(50)                        null comment '行业分类',
    economy_type           varchar(50)                        null comment '经济类型',
    org_type               varchar(20)                        null comment '组织机构类型',
    org_scale              varchar(20)                        null comment '企业规模',
    register_currency_type varchar(20)                        null comment '注册币种',
    register_capital       varchar(50)                        null comment '注册资本',
    real_currency_type     varchar(20)                        null comment '实收币种',
    real_capital           varchar(50)                        null comment '实收资本',
    register_capital_rate  varchar(20)                        null comment '注册资本到位率',
    corp_represent         varchar(20)                        null comment '法人代表',
    corp_gender            varchar(20)                        null comment '法人性别',
    corp_cert_type         varchar(20)                        null comment '法人证件类型',
    corp_cert_code         varchar(50)                        null comment '法人证件号码',
    listed_company         tinyint(1)                         null comment '是否上市公司',
    create_time            datetime default CURRENT_TIMESTAMP null,
    create_by              bigint                             null comment '创建人',
    update_time            datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by              bigint                             null,
    origin_id              bigint                             not null comment '草稿表id',
    version                varchar(32)                        not null comment '版本',
    data_create_time       datetime                           null,
    data_create_by         bigint                             null,
    data_update_time       datetime                           null,
    data_update_by         bigint                             null,
    client_code            varchar(50)                        null comment '客户编号（冗余）'
    )
    comment '法人工商信息表';

create table if not exists corp_contact_info
(
    id          bigint auto_increment
    primary key,
    client_id   bigint                             not null comment '客户id',
    main        tinyint(1)                         null comment '是否主联系人',
    position    varchar(20)                        null comment '职务',
    gender      varchar(20)                        null comment '性别',
    name        varchar(50)                        null comment '姓名',
    telephone   varchar(20)                        null comment '电话',
    mail        varchar(50)                        null comment '邮箱',
    cert_type   varchar(20)                        null comment '证件类型',
    cert_number varchar(50)                        null comment '证件号码',
    create_time datetime default CURRENT_TIMESTAMP null,
    create_by   bigint                             null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by   bigint                             null
    )
    comment '法人联系人';

create table if not exists corp_contact_info_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             not null comment '客户id',
    main             tinyint(1)                         null comment '是否主联系人',
    position         varchar(20)                        null comment '职务',
    gender           varchar(20)                        null comment '性别',
    name             varchar(50)                        null comment '姓名',
    telephone        varchar(20)                        null comment '电话',
    mail             varchar(50)                        null comment '邮箱',
    cert_type        varchar(20)                        null comment '证件类型',
    cert_number      varchar(50)                        null comment '证件号码',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '法人联系人';

create table if not exists corp_related_enterprise
(
    id                 bigint auto_increment
    primary key,
    client_id          bigint                             null comment '客户id',
    enterprise_name    varchar(100)                       null comment '关联企业名称',
    relationship       varchar(100)                       null comment '关联关系',
    register_capital   bigint                             null comment '注册资本',
    shareholding_ratio bigint                             null comment '持股比例',
    invest_amount      bigint                             null comment '投资金额（万元）',
    continuous_status  varchar(20)                        null comment '存续状态',
    establish_date     date                               null comment '成立日期',
    industry_type      varchar(20)                        null comment '行业',
    create_time        datetime default CURRENT_TIMESTAMP null,
    create_by          bigint                             null,
    update_time        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by          bigint                             null,
    constraint corp_related_enterprise_enterprise_name_client_id_uindex
    unique (enterprise_name, client_id)
    )
    comment '法人关联企业';

create table if not exists corp_related_enterprise_lib
(
    id                 bigint auto_increment
    primary key,
    client_id          bigint                             null comment '客户id',
    enterprise_name    varchar(100)                       null comment '关联企业名称',
    relationship       varchar(100)                       null comment '关联关系',
    register_capital   bigint                             null comment '注册资本',
    shareholding_ratio bigint                             null comment '持股比例',
    invest_amount      bigint                             null comment '投资金额（万元）',
    create_time        datetime default CURRENT_TIMESTAMP null,
    create_by          bigint                             null,
    update_time        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by          bigint                             null,
    origin_id          bigint                             not null comment '草稿表id',
    version            varchar(32)                        not null comment '版本',
    industry_type      varchar(20)                        null comment '行业',
    establish_date     date                               null comment '成立日期',
    data_create_time   datetime                           null,
    data_create_by     bigint                             null,
    data_update_time   datetime                           null,
    data_update_by     bigint                             null,
    continuous_status  varchar(20)                        null comment '存续状态'
    )
    comment '法人关联企业';

create table if not exists corp_shareholder_info
(
    id                bigint auto_increment
    primary key,
    client_id         bigint                             null comment '客户id',
    shareholder_type  varchar(20)                        null comment '股东类型',
    shareholder_name  varchar(50)                        null comment '股东姓名',
    paid_total        bigint                             null comment '认缴金额（万）',
    actual_paid_total bigint                             null comment '实缴金额',
    capital_way       varchar(20)                        null comment '出资方式',
    capital_percent   bigint                             null comment '出资占比',
    real_controller   tinyint(1)                         null comment '是否实际控制人',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null,
    constraint shareholder_name
    unique (client_id, shareholder_name)
    )
    comment '股东信息';

create table if not exists corp_shareholder_info_lib
(
    id                bigint auto_increment
    primary key,
    client_id         bigint                             null comment '客户id',
    shareholder_type  varchar(20)                        null comment '股东类型',
    shareholder_name  varchar(50)                        null comment '股东姓名',
    paid_total        bigint                             null comment '认缴金额（万）',
    actual_paid_total bigint                             null comment '实缴金额',
    capital_way       varchar(20)                        null comment '出资方式',
    capital_percent   bigint                             null comment '出资占比',
    real_controller   tinyint(1)                         null comment '是否实际控制人',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null,
    origin_id         bigint                             not null comment '草稿表id',
    version           varchar(32)                        not null comment '版本',
    data_create_time  datetime                           null,
    data_create_by    bigint                             null,
    data_update_time  datetime                           null,
    data_update_by    bigint                             null
    )
    comment '股东信息';

create table if not exists corp_subject_item
(
    id            bigint auto_increment
    primary key,
    client_id     bigint                             null comment '客户id',
    report_type   varchar(20)                        null comment '报表类型',
    subject_type  varchar(20)                        null comment '科目类型',
    subject_code  varchar(20)                        null comment '科目代码',
    subject_name  varchar(50)                        null comment '科目名称',
    year          int                                null comment '年度',
    quarter       int                                null comment '季度',
    subject_value bigint                             null comment '值文本',
    create_time   datetime default CURRENT_TIMESTAMP null,
    create_by     bigint                             null,
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by     bigint                             null
    )
    comment '法人公司科目指标表';

create table if not exists data_share_manager
(
    id          bigint auto_increment
    primary key,
    model_name  varchar(30)                        null comment '模块',
    start_time  datetime                           null comment '开始时间',
    end_time    datetime                           null comment '结束时间',
    page_size   int(20)                            null comment '页大小',
    page_num    int(20)                            null comment '页码数',
    data_total  int(20)                            null comment '数据总量',
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null
    )
    comment '数据分享-进度管理表';

create table if not exists data_share_merchants
(
    client_id             bigint       not null comment '客商ID'
    primary key,
    name                  varchar(50)  null comment '客商名称',
    short_name            varchar(50)  null comment '客商简称',
    english_name          varchar(500) null comment '英文名称',
    english_short_name    varchar(100) null comment '英文简称',
    is_internal_unit      tinyint(1)   null comment '是否为内部单位 0否，1是',
    customer_type         int(3)       null comment '客户类型 01 企业 02 个体工商户 03 农民专业合作社 04 政府机关 05 事业单位 06 社会团体 07 民办非企业单位 08 司法行政 09 外国企业 10 军队 11 个人 12 临时客商',
    credit_code           varchar(20)  null comment '统一社会信用代码',
    national_org_code     varchar(32)  null comment '全国组织机构代码',
    tax_no                varchar(32)  null comment '税务登记证号',
    business_registration varchar(32)  null comment '工商登记号',
    dunbar_code           varchar(50)  null comment '邓白氏编码',
    identification_number varchar(20)  null comment '身份证号',
    region                varchar(50)  null comment '国家',
    province              varchar(50)  null comment '省份',
    city                  varchar(50)  null comment '城市',
    legal_name            varchar(50)  null comment '法人姓名',
    registered_address    varchar(1000) null comment '注册地址',
    registered_capital    varchar(20)  null comment '注册资本',
    contact_name          varchar(50)  null comment '联系人姓名',
    contact_tel           varchar(20)  null comment '联系人电话',
    enclosure             varchar(100) null comment '附件',
    is_customer           tinyint(1)   null comment '是否客户 0否，1是',
    is_supplier           tinyint(1)   null comment '是否供应商 0否，1是',
    capital_currency      varchar(20)  null comment '注册资本币种',
    natural_key           varchar(50)  null comment '业务主键',
    source                varchar(50)  null comment '来源系统',
    internal_unit         varchar(30)  null comment '内部单位',
    status                tinyint(1)   null comment '状态 0 失效 1 有效',
    mdm_create_number     varchar(50)  null comment '主数据创建人名称',
    mdm_create_name       varchar(50)  null comment '主数据创建人账号',
    mdm_create_org        varchar(50)  null comment '主数据创建人组织机构',
    mdm_create_org_name   varchar(50)  null comment '主数据创建人组织机构名称',
    mdm_create_time       datetime     null comment '主数据创建时间',
    operation_time        datetime     null comment '操作时间',
    create_time           datetime     null comment '创建时间',
    update_time           datetime     null comment '修改时间'
    )
    comment '数据分享-客商信息维护';

create index normal_contact_credit_code
    on data_share_merchants (credit_code);

create table if not exists environment_penalty
(
    id              bigint auto_increment comment '主键'
    primary key,
    client_id       bigint                             not null comment '客户id',
    penalty_time    datetime                           null comment '处罚日期',
    punish_number   varchar(256)                       null comment '决定文书号',
    reason          varchar(500)                       null comment '处罚事由',
    result          varchar(128)                       null comment '处罚结果',
    amount          bigint                             null comment '处罚金额（元）',
    department_name varchar(128)                       null comment '处罚单位',
    source          varchar(64)                        null comment '数据来源',
    info            varchar(500)                       null comment '执行情况',
    detail_url      varchar(256)                       null comment '详情url',
    create_time     datetime default CURRENT_TIMESTAMP null,
    create_by       bigint                             null,
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by       bigint                             null
    )
    comment '环保处罚';

create index idx_client_id
    on environment_penalty (client_id);

create table if not exists exception_info
(
    id         bigint auto_increment
    primary key,
    biz_info   text         null,
    msg        varchar(255) null comment '错误信息',
    gmt_create datetime     null comment '发生时间'
    );

create table if not exists flw_channel_definition
(
    ID_             varchar(255) not null
    primary key,
    NAME_           varchar(255) null,
    VERSION_        int          null,
    KEY_            varchar(255) null,
    CATEGORY_       varchar(255) null,
    DEPLOYMENT_ID_  varchar(255) null,
    CREATE_TIME_    datetime(3)  null,
    TENANT_ID_      varchar(255) null,
    RESOURCE_NAME_  varchar(255) null,
    DESCRIPTION_    varchar(255) null,
    TYPE_           varchar(255) null,
    IMPLEMENTATION_ varchar(255) null,
    constraint ACT_IDX_CHANNEL_DEF_UNIQ
    unique (KEY_, VERSION_, TENANT_ID_)
    );

create table if not exists flw_ev_databasechangelog
(
    ID            varchar(255) not null,
    AUTHOR        varchar(255) not null,
    FILENAME      varchar(255) not null,
    DATEEXECUTED  datetime     not null,
    ORDEREXECUTED int          not null,
    EXECTYPE      varchar(10)  not null,
    MD5SUM        varchar(35)  null,
    DESCRIPTION   varchar(255) null,
    COMMENTS      varchar(255) null,
    TAG           varchar(255) null,
    LIQUIBASE     varchar(20)  null,
    CONTEXTS      varchar(255) null,
    LABELS        varchar(255) null,
    DEPLOYMENT_ID varchar(10)  null
    );

create table if not exists flw_ev_databasechangeloglock
(
    ID          int          not null
    primary key,
    LOCKED      bit          not null,
    LOCKGRANTED datetime     null,
    LOCKEDBY    varchar(255) null
    );

create table if not exists flw_event_definition
(
    ID_            varchar(255) not null
    primary key,
    NAME_          varchar(255) null,
    VERSION_       int          null,
    KEY_           varchar(255) null,
    CATEGORY_      varchar(255) null,
    DEPLOYMENT_ID_ varchar(255) null,
    TENANT_ID_     varchar(255) null,
    RESOURCE_NAME_ varchar(255) null,
    DESCRIPTION_   varchar(255) null,
    constraint ACT_IDX_EVENT_DEF_UNIQ
    unique (KEY_, VERSION_, TENANT_ID_)
    );

create table if not exists flw_event_deployment
(
    ID_                   varchar(255) not null
    primary key,
    NAME_                 varchar(255) null,
    CATEGORY_             varchar(255) null,
    DEPLOY_TIME_          datetime(3)  null,
    TENANT_ID_            varchar(255) null,
    PARENT_DEPLOYMENT_ID_ varchar(255) null
    );

create table if not exists flw_event_resource
(
    ID_             varchar(255) not null
    primary key,
    NAME_           varchar(255) null,
    DEPLOYMENT_ID_  varchar(255) null,
    RESOURCE_BYTES_ longblob     null
    );

create table if not exists flw_ru_batch
(
    ID_            varchar(64)             not null
    primary key,
    REV_           int                     null,
    TYPE_          varchar(64)             not null,
    SEARCH_KEY_    varchar(255)            null,
    SEARCH_KEY2_   varchar(255)            null,
    CREATE_TIME_   datetime(3)             not null,
    COMPLETE_TIME_ datetime(3)             null,
    STATUS_        varchar(255)            null,
    BATCH_DOC_ID_  varchar(64)             null,
    TENANT_ID_     varchar(255) default '' null
    )
    collate = utf8_bin;

create table if not exists flw_ru_batch_part
(
    ID_            varchar(64)             not null
    primary key,
    REV_           int                     null,
    BATCH_ID_      varchar(64)             null,
    TYPE_          varchar(64)             not null,
    SCOPE_ID_      varchar(64)             null,
    SUB_SCOPE_ID_  varchar(64)             null,
    SCOPE_TYPE_    varchar(64)             null,
    SEARCH_KEY_    varchar(255)            null,
    SEARCH_KEY2_   varchar(255)            null,
    CREATE_TIME_   datetime(3)             not null,
    COMPLETE_TIME_ datetime(3)             null,
    STATUS_        varchar(255)            null,
    RESULT_DOC_ID_ varchar(64)             null,
    TENANT_ID_     varchar(255) default '' null,
    constraint FLW_FK_BATCH_PART_PARENT
    foreign key (BATCH_ID_) references flw_ru_batch (ID_)
    )
    collate = utf8_bin;

create index FLW_IDX_BATCH_PART
    on flw_ru_batch_part (BATCH_ID_);

create table if not exists future_log
(
    id         bigint auto_increment
    primary key,
    content    varchar(1000)                       null,
    model      varchar(60)                         null,
    oper_type  varchar(60)                         null,
    create_by  varchar(255)                        not null,
    gmt_create timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
    );

create table if not exists general_dictionary
(
    id        bigint auto_increment
    primary key,
    dict_key  varchar(20)    null comment '字典标识',
    dict_desc varchar(20)    null comment '字典说明',
    code      varchar(20)    null comment '标识编码',
    display   varchar(200)   null comment '显示名称',
    sort      int default 10 null
    )
    comment '通用字典表';

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

create table if not exists hand_process_operate_record
(
    id                    bigint auto_increment
    primary key,
    process_instance_id   varchar(64)                        null comment '流程id',
    start_time            datetime                           null comment '流程发起时间',
    end_time              datetime                           null comment '流程结束时间',
    process_instance_name varchar(128)                       null comment '流程实例名称',
    start_user_name       varchar(32)                        null comment '发起人名称',
    start_user_dept_id    bigint                             null comment '发起人部门id',
    start_user_id         bigint                             null comment '发起人id',
    model_name            varchar(32)                        null comment '模型名称',
    operate_info          text                               null comment '操作详情数组',
    create_time           datetime default CURRENT_TIMESTAMP not null,
    create_by             bigint                             null comment '创建人id',
    update_time           datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    update_by             bigint                             null
    )
    comment '汉得流程操作记录表';

create table if not exists industry_type
(
    id        bigint auto_increment
    primary key,
    code      varchar(20)      null comment '行业编号',
    display   varchar(200)     null comment '行业名称',
    parent_id bigint default 0 null comment '父级行业',
    level     int    default 1 null
    )
    comment '行业类型字典表';

create table if not exists info_history
(
    id               bigint auto_increment
    primary key,
    history_code     varchar(50)                        null comment '变更编号',
    module_code      varchar(20)                        null comment '模块',
    module_record_id bigint                             null comment '模块中对应表记录的id',
    client_id        bigint                             null comment '客户id',
    operation_type   varchar(20)                        null comment '操作类型',
    original_data    text                               null comment '变更前数据',
    current_data     text                               null comment '变更后数据',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    constraint info_history_history_code_uindex
    unique (history_code)
    );

create table if not exists margin_base_info
(
    id                 bigint auto_increment comment '保证金id	'
    primary key,
    margin_code        varchar(100)                       null comment '编号',
    contract_id        bigint                             not null comment '合同id',
    contract_code      varchar(100)                       null comment '合同编号',
    client_id          bigint                             null comment '客户id',
    collection_date    datetime                           null comment '收款日期',
    collection_amount  bigint                             null comment '保证金余额',
    back_amount        bigint                             null comment '已退金额',
    deduct_amount      bigint                             null comment '已抵扣金额',
    create_by          bigint                             null,
    create_time        datetime default CURRENT_TIMESTAMP null,
    update_by          bigint                             null,
    update_time        datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    plan_margin_amount bigint                             null comment '计划收款金额',
    plan_margin_date   datetime                           null comment '计划收款日期',
    contract_is_settle int      default 0                 null comment '合同是否结清，0 否，1 是'
    )
    comment '保证金明细表';

create table if not exists margin_record_info
(
    id                      bigint auto_increment comment '保证金核销记录明细id	'
    primary key,
    margin_id               bigint                             null comment '保证金明细id',
    data_source             varchar(100)                       null comment '信息来源',
    collection_type         varchar(20)                        null comment '收款类型',
    record_type             varchar(20)                        null comment '记录类型：退款 or 收款',
    collection_date         datetime                           null comment '实收or付日期',
    collection_amount       bigint                             null comment '实收or付金额',
    deduct_principal        bigint                             null comment '抵扣本金',
    deduct_interest         bigint                             null comment '抵扣利息',
    deduct_penalty_interest bigint                             null comment '抵扣罚息',
    deduct_rent             bigint                             null comment '抵扣租金',
    deduct_term             int                                null comment '抵扣期项',
    collection_id           bigint                             null comment '收款核销id',
    postscript              varchar(100)                       null comment '附言',
    enclosure_id            bigint                             null comment '附件id',
    enclosure_name          varchar(100)                       null comment '附件名',
    write_off_status        varchar(100)                       null comment '核销状态',
    bank_account_id         bigint                             null comment '我方账户id',
    client_id               bigint                             null comment '客户id',
    other_bank_account_id   bigint                             null comment '对方账户id',
    other_client_id         bigint                             null comment '对方客户id',
    sort_id                 int                                null comment '序号',
    write_off               varchar(100)                       null comment '核销',
    review                  varchar(100)                       null comment '复核',
    create_by               bigint                             null,
    create_time             datetime default CURRENT_TIMESTAMP null,
    update_by               bigint                             null,
    update_time             datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    source_flag             int                                null comment '来源标志',
    write_off_user          bigint                             null comment '核销人',
    review_user             bigint                             null comment '复核人'
    )
    comment '保证金核销记录明细表';

create table if not exists margin_write_off_record
(
    id            bigint auto_increment comment '核销记录id'
    primary key,
    margin_id     bigint                             null comment '归属id',
    record_id     bigint                             null comment '收款记录id',
    operate       varchar(20)                        not null comment '操作',
    operate_info  varchar(100)                       null comment '被操作明细',
    margin_amount varchar(20)                        null comment '保证金金额',
    create_by     bigint                             null,
    create_time   datetime default CURRENT_TIMESTAMP null,
    update_by     bigint                             null,
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '核销记录表';

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

create table if not exists message_base_info
(
    id           bigint auto_increment
    primary key,
    client_id    varchar(32)   not null comment '客户ID',
    is_read      tinyint(1)    null comment '是否已读 0未读，1已读',
    message_time datetime      null comment '消息时间',
    message_body varchar(1000) null comment '消息体',
    message_type varchar(50)   null comment '消息类型',
    create_by    bigint        null,
    create_time  datetime      null,
    update_by    bigint        null,
    update_time  datetime      null
    )
    comment '系统消息基础表';

create index client_id_type_key
    on message_base_info (client_id, message_type);

create table if not exists model_ext
(
    id         bigint auto_increment comment '主键'
    primary key,
    model_id   varchar(64)                                   not null comment 'act_re_model表id',
    ext_json   longtext                                      null comment '模型额外信息json',
    note       varchar(128)        default ''                not null comment '模型描述',
    is_delete  tinyint(4) unsigned default 0                 not null comment '是否删除',
    gmt_create datetime            default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify datetime            default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint uniq_model_id
    unique (model_id)
    )
    comment '模型扩展数据表';

create table if not exists node_back_record
(
    id                      bigint auto_increment comment '主键'
    primary key,
    process_instance_id     varchar(64)                        not null comment '流程实例id',
    source_task_activity_id varchar(64)                        not null comment '操作时原节点任务节点id',
    sink_task_activity_id   varchar(64)                        not null comment '回退到的节点id',
    process_start_user_id   varchar(64)                        not null comment '流程发起人id，索引协助减小查询范围',
    type                    tinyint                            not null comment '回退类型，1撤回，2驳回',
    handler_id              varchar(64)                        not null comment '操作人id',
    status                  tinyint  default 1                 not null comment '是否还未处理此次回退，1未处理，0已处理',
    jump_to_source_flag     tinyint  default 0                 not null comment 'sink节点正常处理后是否需要跳回source节点',
    gmt_create              datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify              datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间'
    )
    comment '节点回退记录表';

create index idx_pi_sink_ta_id
    on node_back_record (process_instance_id, sink_task_activity_id);

create table if not exists normal_bank_account
(
    id             bigint auto_increment
    primary key,
    account_name   varchar(50)                        null comment '账户名称',
    account_number varchar(50)                        null comment '账号',
    account_bank   varchar(50)                        null comment '开户行',
    main_account   tinyint(1)                         null comment '是否主账号',
    create_time    datetime default CURRENT_TIMESTAMP null,
    create_by      bigint                             null,
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    client_id      bigint                             null comment '客户id',
    update_by      bigint                             null
    )
    comment '自然人银行账户';

create table if not exists normal_bank_account_lib
(
    id               bigint auto_increment
    primary key,
    account_name     varchar(50)                        null comment '账户名称',
    account_number   varchar(50)                        null comment '账号',
    account_bank     varchar(50)                        null comment '开户行',
    main_account     tinyint(1)                         null comment '是否主账号',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    client_id        bigint                             null comment '客户id',
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '自然人银行账户';

create table if not exists normal_base_info
(
    id            bigint auto_increment
    primary key,
    client_id     bigint                             null comment '客户id',
    cert_type     varchar(20)                        null comment '证件类型',
    cert_number   varchar(50)                        null comment '证件号码',
    gender        varchar(20)                        null comment '性别',
    marriage_type varchar(20)                        null comment '婚姻情况',
    country       varchar(20)                        null comment '国家',
    age           int                                null comment '年龄',
    mobile_number varchar(20)                        null comment '手机号',
    home_address  varchar(100)                       null comment '家庭地址',
    mail          varchar(50)                        null comment '邮箱',
    create_time   datetime default CURRENT_TIMESTAMP null,
    create_by     int                                null,
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by     bigint                             null,
    client_code   varchar(50)                        null comment '客户编号（冗余）'
    )
    comment '自然人基本信息';

create table if not exists normal_base_info_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             null comment '客户id',
    cert_type        varchar(20)                        null comment '证件类型',
    cert_number      varchar(50)                        null comment '证件号码',
    gender           varchar(20)                        null comment '性别',
    marriage_type    varchar(20)                        null comment '婚姻情况',
    country          varchar(20)                        null comment '国家',
    age              int                                null comment '年龄',
    mobile_number    varchar(20)                        null comment '手机号',
    home_address     varchar(100)                       null comment '家庭地址',
    mail             varchar(50)                        null comment '邮箱',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        int                                null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null,
    client_code      varchar(50)                        null comment '客户编号（冗余）'
    )
    comment '自然人基本信息';

create table if not exists normal_spouse
(
    id          bigint auto_increment
    primary key,
    client_id   bigint                             null comment '客户id',
    spouse_name varchar(50)                        null comment '配偶姓名',
    cert_type   varchar(20)                        null comment '证件类型',
    cert_number varchar(50)                        null comment '证件号码',
    create_time datetime default CURRENT_TIMESTAMP null,
    create_by   bigint                             null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by   bigint                             null
    )
    comment '自然人配偶';

create table if not exists normal_spouse_lib
(
    id               bigint auto_increment
    primary key,
    client_id        bigint                             null comment '客户id',
    spouse_name      varchar(50)                        null comment '配偶姓名',
    cert_type        varchar(20)                        null comment '证件类型',
    cert_number      varchar(50)                        null comment '证件号码',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null,
    origin_id        bigint                             not null comment '草稿表id',
    version          varchar(32)                        not null comment '版本',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    )
    comment '自然人配偶';

create table if not exists notify_record
(
    id                  bigint auto_increment comment '主键'
    primary key,
    type                int                                           not null comment '通知类型；1通知；2抄送；3催办',
    process_instance_id varchar(64)                                   not null comment '流程实例id',
    task_id             varchar(64)         default ''                not null comment '任务id，可以为空，比如流程启动时抄送',
    sender_id           varchar(64)                                   not null comment '发送人id',
    receiver_id         varchar(64)                                   not null comment '接收者id',
    message             varchar(255)                                  not null comment '通知信息',
    read_flag           tinyint             default 0                 not null comment '已读标识',
    is_delete           tinyint(4) unsigned default 0                 not null comment '是否删除',
    gmt_create          datetime            default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify          datetime            default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间'
    )
    comment '通知记录表';

create index idx_receiver_id
    on notify_record (receiver_id);

create index idx_sender_id
    on notify_record (sender_id);

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

create table if not exists operate_record
(
    id                    bigint auto_increment comment '主键'
    primary key,
    type                  varchar(32)                        not null comment '操作类型',
    biz_type              varchar(32)                        null comment '操作-业务自扩展类型',
    process_instance_id   varchar(64)                        not null comment '流程实例id',
    process_definition_id varchar(64)                        not null comment '流程定义id',
    task_id               varchar(64)                        null comment '任务id，可以为空，比如操作流程撤回',
    task_activity_id      varchar(64)                        null comment '任务节点id，可以为空',
    note                  varchar(1024)                      null comment '操作备注',
    handler_id            varchar(64)                        null comment '操作人',
    gmt_create            datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify            datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间'
    )
    comment '操作记录表';

create index idx_handler_id
    on operate_record (handler_id);

create index idx_process_instance_id
    on operate_record (process_instance_id);

create table if not exists payment_actual_detail
(
    id                      bigint auto_increment comment '主键id'
    primary key,
    payment_id              bigint                             null comment '所属支付申请id',
    seq_code                varchar(20)                        null comment '序号',
    contract_id             bigint                             null comment '所属合同id',
    info_source             varchar(50)                        null comment '同步还是录入，显示‘财务系统’或者录入者名字',
    payment_method          varchar(50)                        null comment '付款方式',
    paid_in_date            datetime                           null comment '实付日期',
    paid_in_amount          bigint                             null comment '实付金额',
    postscript              varchar(1024)                      null comment '附言',
    our_account_id          bigint                             null comment '我方账户',
    our_account_number      varchar(255)                       null comment '我方账号',
    our_account_name        varchar(255)                       null comment '我方账号名',
    our_account_bank        varchar(255)                       null comment '我方账号开户行',
    opposite_account_id     bigint                             null comment '对方账户',
    opposite_account_number varchar(255)                       null comment '对方账号',
    opposite_account_name   varchar(255)                       null comment '对方账号名',
    opposite_account_bank   varchar(255)                       null comment '对方账号开户行',
    write_off_status        varchar(20)                        null comment '核销状态',
    create_by               bigint                             null comment '创建人、发起人',
    create_time             datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by               bigint                             null comment '最后更新人id',
    update_time             datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    )
    comment '实际付款记录表';

create table if not exists payment_base_info
(
    id                          bigint auto_increment comment '主键id'
    primary key,
    payment_code                varchar(50)                        null comment '付款申请编号',
    contract_id                 bigint                             null comment '对应合同id',
    contract_code               varchar(50)                        null comment '合同编号',
    client_id                   bigint(50)                         null comment '客户id',
    payables                    varchar(255)                       null comment '应付款项',
    apply_payment_date          datetime                           null comment '申请付款日期',
    apply_payment_amount        bigint                             null comment '申请付款金额',
    earnest_money               bigint                             null comment '保证金',
    down_payment                bigint                             null comment '首付款',
    consulting_fee              bigint                             null comment '服务费/咨询费',
    nominal_price               bigint                             null comment '名义货价',
    payment_process_status      varchar(30)                        null comment '审批状态',
    payment_status              varchar(30)                        null comment '申请状态',
    write_off_status            varchar(255)                       null comment '付款申请核销状态',
    write_off_user_ids          varchar(255)                       null comment '核销人员id，json',
    paid_in_date                datetime                           null comment '最新实付日期',
    actual_detail_count         int                                null comment '付款记录个数，用于生成记录序号',
    create_by                   bigint                             null comment '创建人、发起人',
    create_time                 datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                   bigint                             null comment '最后更新人id',
    update_time                 datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    con_project_type            varchar(50)                        null comment '项目类型',
    con_apply_credit_amount     bigint                             null comment '合同带入申请授信金额',
    con_biz_dept_id             bigint                             null comment '合同带入业务部门id',
    con_biz_dept_leader_id      bigint                             null comment '合同带入业务部门领导id',
    con_biz_division_leader_id  bigint                             null comment '合同带入业务分管领导id',
    con_risk_control_manager_id bigint                             null comment '合同带入风控经理id'
    );

create table if not exists payment_base_info_lib
(
    id                          bigint auto_increment comment '主键id'
    primary key,
    payment_code                varchar(50)                        null comment '付款申请编号',
    contract_id                 bigint                             null comment '对应合同id',
    contract_code               varchar(50)                        null comment '合同编号',
    client_id                   bigint(50)                         null comment '客户id',
    payables                    varchar(255)                       null comment '应付款项',
    apply_payment_date          datetime                           null comment '申请付款日期',
    apply_payment_amount        bigint                             null comment '申请付款金额',
    earnest_money               bigint                             null comment '保证金',
    down_payment                bigint                             null comment '首付款',
    consulting_fee              bigint                             null comment '服务费/咨询费',
    nominal_price               bigint                             null comment '名义货价',
    payment_process_status      varchar(30)                        null comment '审批状态',
    payment_status              varchar(30)                        null comment '申请状态',
    write_off_status            varchar(255)                       null comment '付款申请核销状态',
    write_off_user_ids          varchar(255)                       null comment '核销人员id，json',
    paid_in_date                datetime                           null comment '最新实付日期',
    actual_detail_count         int                                null comment '付款记录个数，用于生成记录序号',
    create_by                   bigint                             null comment '创建人、发起人',
    create_time                 datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                   bigint                             null comment '最后更新人id',
    update_time                 datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    con_project_type            varchar(50)                        null comment '项目类型',
    con_apply_credit_amount     bigint                             null comment '合同带入申请授信金额',
    con_biz_dept_id             bigint                             null comment '合同带入业务部门id',
    con_biz_dept_leader_id      bigint                             null comment '合同带入业务部门领导id',
    con_biz_division_leader_id  bigint                             null comment '合同带入业务分管领导id',
    con_risk_control_manager_id bigint                             null comment '合同带入风控经理id',
    version                     varchar(40)                        not null comment '版本号',
    origin_id                   bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time            datetime                           null,
    data_create_by              bigint                             null,
    data_update_time            datetime                           null,
    data_update_by              bigint                             null
    );

create table if not exists payment_planed_detail
(
    id                    bigint auto_increment comment '主键id'
    primary key,
    payment_id            bigint                             null comment '所属支付id',
    payee_client_id       bigint                             null comment '收款方客户ID',
    payee_client_name     varchar(255)                       null comment '收款方客户名称',
    opposite_account      varchar(50)                        null comment '对方账号',
    opposite_account_name varchar(50)                        null comment '对方账号名',
    opposite_account_bank varchar(255)                       null comment '对方账号开户行',
    payment_method        varchar(50)                        null comment '支付方式',
    payment_amount        bigint                             null comment '支付金额',
    postscript            varchar(1024)                      null comment '附言',
    create_by             bigint                             null comment '创建人、发起人id',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by             bigint                             null comment '最后更新人id',
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    )
    comment '计划付款明细表（付款申请 1:n付款明细）';

create table if not exists payment_planed_detail_lib
(
    id                    bigint auto_increment comment '主键id'
    primary key,
    payment_id            bigint                             null comment '所属支付id',
    payee_client_id       bigint                             null comment '收款方客户ID',
    payee_client_name     varchar(255)                       null comment '收款方客户名称',
    opposite_account      varchar(50)                        null comment '对方账号',
    opposite_account_name varchar(50)                        null comment '对方账号名',
    opposite_account_bank varchar(255)                       null comment '对方账号开户行',
    payment_method        varchar(50)                        null comment '支付方式',
    payment_amount        bigint                             null comment '支付金额',
    postscript            varchar(1024)                      null comment '附言',
    create_by             bigint                             null comment '创建人、发起人id',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by             bigint                             null comment '最后更新人id',
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    version               varchar(40)                        not null comment '版本号',
    origin_id             bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time      datetime                           null,
    data_create_by        bigint                             null,
    data_update_time      datetime                           null,
    data_update_by        bigint                             null
    )
    comment '计划付款明细表（付款申请 1:n付款明细）';

create table if not exists payment_policy_info
(
    id                   bigint auto_increment comment '主键id'
    primary key,
    payment_id           bigint                             null,
    policy_code          varchar(50)                        null comment '保单编号',
    policy_amount        bigint                             null comment '保单金额',
    insurance_start_date datetime                           null comment '保险起始日',
    insurance_end_date   datetime                           null comment '保险到期日',
    insurance_company    varchar(255)                       null comment '保险公司名称',
    create_by            bigint                             null comment '创建人、发起人',
    create_time          datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by            bigint                             null comment '最后更新人id',
    update_time          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    );

create table if not exists payment_questionnaire
(
    id            bigint                             not null comment '主键id'
    primary key,
    seq_code      varchar(20)                        null comment '序号',
    question_type varchar(255)                       null comment '问题分类',
    question      varchar(512)                       null comment '问题',
    create_by     bigint                             null,
    create_time   datetime default CURRENT_TIMESTAMP null,
    update_by     bigint                             null,
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version       varchar(255)                       null comment '版本'
    );

create table if not exists payment_questionnaire_answer
(
    id              bigint auto_increment comment '主键'
    primary key,
    payment_id      bigint                             null comment '所属付款id',
    question_id     bigint                             null comment '问题id',
    question_answer varchar(255)                       null comment '问题答案',
    remarks         varchar(1024)                      null comment '备注',
    create_by       bigint                             null,
    create_time     datetime default CURRENT_TIMESTAMP null,
    update_by       bigint                             null,
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    );

create table if not exists payment_questionnaire_answer_lib
(
    id               bigint auto_increment comment '主键'
    primary key,
    payment_id       bigint                             null comment '所属付款id',
    question_id      bigint                             null comment '问题id',
    question_answer  varchar(255)                       null comment '问题答案',
    remarks          varchar(1024)                      null comment '备注',
    create_by        bigint                             null,
    create_time      datetime default CURRENT_TIMESTAMP null,
    update_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version          varchar(40)                        not null comment '版本号',
    origin_id        bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time datetime                           null,
    data_create_by   bigint                             null,
    data_update_time datetime                           null,
    data_update_by   bigint                             null
    );

create table if not exists payment_write_off_history
(
    id                  bigint auto_increment comment '主键'
    primary key,
    paymentId           bigint                             null comment '所属付款申请id',
    operate_time        datetime                           null comment '操作时间',
    operate_person_id   bigint                             null comment '操作人id',
    operate_person_name varchar(255)                       null comment '操作人姓名',
    operate_data_type   varchar(20)                        null comment '操作数据类型 （记录还是申请）',
    operate_data_code   varchar(20)                        null comment '被操作数据序号（记录是递增的数字，申请是paymentcode）',
    operation           varchar(20)                        null comment '操作',
    data_status         varchar(20)                        null comment '单据状态',
    create_by           bigint                             null comment '创建人、发起人',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by           bigint                             null comment '最后更新人id',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
    );

create table if not exists process_auth
(
    id                  bigint auto_increment comment '主键'
    primary key,
    process_instance_id varchar(64)                        not null comment '流程实例id',
    task_activity_id    varchar(64)                        null comment '任务节点id',
    source_type         int                                not null comment '权限来源，1发起流程，2流程审批，3抄送',
    user_id             varchar(64)                        not null comment '被授权人id',
    status              tinyint  default 1                 not null comment '是否有效，1有效，0无效',
    gmt_create          datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify          datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint uniq_u_pi_ta_id
    unique (user_id, process_instance_id, task_activity_id)
    )
    comment '流程权限表';

create index idx_process_instance_id
    on process_auth (process_instance_id);

create table if not exists process_instance_ext
(
    id                  bigint auto_increment comment '主键'
    primary key,
    process_instance_id varchar(64)                        not null comment '流程实例id',
    sub_module          varchar(32)                        null comment '二级模块枚举',
    start_user_dept_id  varchar(64)                        null comment '被抄送人id',
    last_operator_id    varchar(64)                        null comment '最后操作人id',
    last_operation_type varchar(32)                        null comment '最后操作类型',
    gmt_create          datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify          datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间'
    )
    comment '流程实例额外信息表';

create index idx_process_instance_id
    on process_instance_ext (process_instance_id);

create table if not exists proj_establish_aoc_price
(
    id                       bigint auto_increment comment '立项基本信息表id	'
    primary key,
    proj_establish_id        bigint                             not null comment '所属立项ID',
    apply_credit_amount      bigint                             null comment '申报授信金额',
    aoc_credit_term          int                                null comment '转让额度有效期（月',
    repay_type               varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop       tinyint(1)                         null comment '额度是否可循环',
    earnest_money            bigint                             null comment '保证金',
    confirmatory_party       text                               null comment '确权方',
    consulting_fee           bigint                             null comment '服务费/咨询费',
    aoc_financing_proportion int                                null comment '转让融资比例',
    summary                  text                               null comment '拟转应收账款概述',
    rate_type                varchar(10)                        null comment '转让费率类型。固定利率：fixed、浮动利率：float	',
    aoc_rate_percent         int                                null comment '转让费率值。百分之多少',
    irr_percent              int                                null comment '内部收益率。百分之多少',
    rental_calc_type         varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay
其他：other',
    create_by                bigint                             null,
    create_time              datetime default CURRENT_TIMESTAMP null,
    update_by                bigint                             null,
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uniq_proj_establish_id
    unique (proj_establish_id) comment '每个立项只能有一份报价'
    )
    comment '债权转让报价方案表';

create table if not exists proj_establish_aoc_price_lib
(
    id                       bigint auto_increment comment '立项基本信息表id	'
    primary key,
    proj_establish_id        bigint                             not null comment '所属立项ID',
    apply_credit_amount      bigint                             null comment '申报授信金额',
    aoc_credit_term          int                                null comment '转让额度有效期（月',
    repay_type               varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop       tinyint(1)                         null comment '额度是否可循环',
    earnest_money            bigint                             null comment '保证金',
    confirmatory_party       text                               null comment '确权方',
    consulting_fee           bigint                             null comment '服务费/咨询费',
    aoc_financing_proportion int                                null comment '转让融资比例',
    summary                  text                               null comment '拟转应收账款概述',
    rate_type                varchar(10)                        null comment '转让费率类型。固定利率：fixed、浮动利率：float	',
    aoc_rate_percent         int                                null comment '转让费率值。百分之多少',
    irr_percent              int                                null comment '内部收益率。百分之多少',
    rental_calc_type         varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                bigint                             null,
    create_time              datetime default CURRENT_TIMESTAMP null,
    update_by                bigint                             null,
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version                  varchar(40)                        not null comment '版本号',
    origin_id                bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time         datetime                           null,
    data_create_by           bigint                             null,
    data_update_time         datetime                           null,
    data_update_by           bigint                             null
    )
    comment '债权转让报价方案表';

create table if not exists proj_establish_base_info
(
    id                            bigint auto_increment
    primary key,
    client_id                     bigint                             null comment '客户id',
    biz_type                      varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    proj_name                     varchar(200)                       null comment '项目名称',
    approval_type                 varchar(20)                        null comment '审批类型',
    proj_code                     varchar(20)                        null comment '项目编号',
    lease_types                   varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    factoring_types               varchar(100)                       null comment '保理类型。有追明保理、无追明保理、有追暗保理',
    proj_source                   varchar(20)                        null comment '存量翻单、渠道介绍、自主开发',
    funds_purpose                 varchar(200)                       null comment '资金用途',
    proj_background               text                               null comment '项目背景',
    transferor_client_id          bigint                             null comment '转让方。（项目类型为租赁时）',
    lessee_info                   json                               null comment '承租人列表',
    creditor_client_id            bigint                             null comment '债权人id',
    creditor_stock_risk_exposure  bigint                             null comment '债权人存量风险敞口',
    debtor_info                   json                               null comment '债务人信息。（项目类型为保理时）',
    guarantee_info                json                               null comment '担保人信息',
    pledgor_info                  json                               null comment '质押人信息',
    mortgagor_info                json                               null comment '抵押人信息',
    proj_sponsor_user_id          bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids       json                               null comment '项目协办方用户id列表',
    create_by                     bigint                             null comment '创建人、发起人',
    biz_dept_id                   bigint                             null comment '业务部门id',
    biz_dept_leader_id            bigint                             null comment '业务部门负责人id',
    biz_division_leader_id        bigint                             null comment '业务分管领导id',
    risk_control_manager_id       bigint                             null comment '风控经理id',
    proj_establish_status         varchar(50)                        null comment '立项状态',
    proj_establish_process_status varchar(50)                        null comment '流程状态',
    create_time                   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                     bigint                             null comment '最后更新人id',
    update_time                   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    type_seq_id                   bigint                             null comment '不同类型，不同的自增序列id',
    constraint proj_establish_base_info_proj_code_uindex
    unique (proj_code),
    constraint seqid_unique
    unique (biz_type, type_seq_id)
    )
    comment '立项基本信息表';

create table if not exists proj_establish_base_info_lib
(
    id                            bigint auto_increment
    primary key,
    client_id                     bigint                             null comment '客户id',
    biz_type                      varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    proj_name                     varchar(200)                       null comment '项目名称',
    approval_type                 varchar(20)                        null comment '审批类型',
    proj_code                     varchar(20)                        null comment '项目编号',
    lease_types                   varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    factoring_types               varchar(100)                       null comment '保理类型。有追明保理、无追明保理、有追暗保理',
    proj_source                   varchar(20)                        null comment '存量翻单、渠道介绍、自主开发',
    funds_purpose                 varchar(200)                       null comment '资金用途',
    proj_background               text                               null comment '项目背景',
    transferor_client_id          bigint                             null comment '转让方。（项目类型为租赁时）',
    lessee_info                   json                               null comment '承租人列表',
    creditor_client_id            bigint                             null comment '债权人id',
    creditor_stock_risk_exposure  bigint                             null comment '债权人存量风险敞口',
    debtor_info                   json                               null comment '债务人信息。（项目类型为保理时）',
    guarantee_info                json                               null comment '担保人信息',
    pledgor_info                  json                               null comment '质押人信息',
    mortgagor_info                json                               null comment '抵押人信息',
    proj_sponsor_user_id          bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids       varchar(200)                       null comment '项目协办方用户id列表',
    create_by                     bigint                             null comment '创建人、发起人',
    biz_dept_id                   bigint                             null comment '业务部门id',
    biz_dept_leader_id            bigint                             null comment '业务部门负责人id',
    biz_division_leader_id        bigint                             null comment '业务分管领导id',
    risk_control_manager_id       bigint                             null comment '风控经理id',
    proj_establish_status         varchar(50)                        null comment '立项状态',
    proj_establish_process_status varchar(50)                        null comment '流程状态',
    create_time                   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                     bigint                             null comment '最后更新人id',
    update_time                   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    origin_id                     bigint                             not null comment '草稿表id',
    version                       varchar(32)                        not null comment '版本',
    data_create_time              datetime                           null,
    data_create_by                bigint                             null,
    data_update_time              datetime                           null,
    data_update_by                bigint                             null,
    type_seq_id                   bigint                             null
    )
    comment '立项基本信息表';

create table if not exists proj_establish_factoring_price
(
    id                             bigint auto_increment comment '立项基本信息表id	'
    primary key,
    proj_establish_id              bigint                             not null comment '所属立项ID',
    apply_credit_amount            bigint                             null comment '申报授信金额',
    factoring_credit_term          int                                null comment '保理额度有效期（月',
    repay_type                     varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    earnest_money                  bigint                             null comment '保证金',
    confirmatory_party             text                               null comment '确权方',
    factoring_financing_proportion int                                null comment '保理融资比例',
    consulting_fee                 bigint                             null comment '服务费/咨询费',
    summary                        text                               null comment '拟转应收账款概述',
    rate_type                      varchar(10)                        null comment '保理费率类型。固定利率：fixed、浮动利率：float	',
    factoring_rate_percent         int                                null comment '保理费率值。百分之多少',
    irr_percent                    int                                null comment '内部收益率。百分之多少',
    rental_calc_type               varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    existing_lease_credit          bit      default b'0'              null comment '是否有存续租赁授信',
    new_lease_credit               bit      default b'0'              null comment '是否新增租赁授信',
    create_by                      bigint                             null,
    create_time                    datetime default CURRENT_TIMESTAMP null,
    update_by                      bigint                             null,
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uniq_proj_establish_id
    unique (proj_establish_id) comment '每个立项只能有一份报价'
    )
    comment '保理报价方案表';

create table if not exists proj_establish_factoring_price_lib
(
    id                             bigint auto_increment comment '立项基本信息表id	'
    primary key,
    proj_establish_id              bigint                             not null comment '所属立项ID',
    apply_credit_amount            bigint                             null comment '申报授信金额',
    factoring_credit_term          int                                null comment '保理额度有效期（月',
    repay_type                     varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    earnest_money                  bigint                             null comment '保证金',
    confirmatory_party             text                               null comment '确权方',
    consulting_fee                 bigint                             null comment '服务费/咨询费',
    factoring_financing_proportion int                                null comment '保理融资比例',
    summary                        text                               null comment '拟转应收账款概述',
    rate_type                      varchar(10)                        null comment '保理费率类型。固定利率：fixed、浮动利率：float	',
    factoring_rate_percent         int                                null comment '保理费率值。百分之多少',
    irr_percent                    int                                null comment '内部收益率。百分之多少',
    rental_calc_type               varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    existing_lease_credit          bit      default b'0'              null comment '是否有存续租赁授信',
    new_lease_credit               bit      default b'0'              null comment '是否新增租赁授信',
    create_by                      bigint                             null,
    create_time                    datetime default CURRENT_TIMESTAMP null,
    update_by                      bigint                             null,
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    origin_id                      bigint                             not null comment '草稿表id',
    version                        varchar(32)                        not null comment '版本',
    data_create_time               datetime                           null,
    data_create_by                 bigint                             null,
    data_update_time               datetime                           null,
    data_update_by                 bigint                             null
    )
    comment '保理报价方案表';

create table if not exists proj_establish_lease_price
(
    id                  bigint auto_increment comment '立项基本信息表id'
    primary key,
    proj_establish_id   bigint                             not null comment '所属立项ID',
    apply_credit_amount bigint                             null comment '申报授信金额',
    lease_month_count   int                                null comment '租赁期限月数',
    repay_times_yearly  int                                null comment '每年还款次数',
    repay_times_total   int                                null comment '还款期数',
    repay_rate          varchar(20)                        null comment '还款频率',
    pay_type            varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type    varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    credit_amount_loop  tinyint(1)                         null comment '额度是否可循环',
    earnest_money       bigint                             null comment '保证金',
    down_payment        bigint                             null comment '首付款',
    consulting_fee      bigint                             null comment '服务费/咨询费',
    nominal_price       bigint                             null comment '名义货价',
    rate_type           varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lease_rate_percent  int                                null comment '租赁利率值。百分之多少',
    irr_percent         int                                null comment '内部收益率。百分之多少',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    constraint uniq_proj_establish_id
    unique (proj_establish_id) comment '每个立项只能有一份报价'
    )
    comment '租赁报价方案表';

create table if not exists proj_establish_lease_price_lib
(
    id                  bigint auto_increment comment '立项基本信息表id'
    primary key,
    proj_establish_id   bigint                             not null comment '所属立项ID',
    apply_credit_amount bigint                             null comment '申报授信金额',
    lease_month_count   int                                null comment '租赁期限月数',
    repay_times_yearly  int                                null comment '每年还款次数',
    repay_times_total   int                                null comment '还款期数',
    repay_rate          varchar(20)                        null comment '还款频率',
    pay_type            varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type    varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    credit_amount_loop  tinyint(1)                         null comment '额度是否可循环',
    earnest_money       bigint                             null comment '保证金',
    down_payment        bigint                             null comment '首付款',
    consulting_fee      bigint                             null comment '服务费/咨询费',
    nominal_price       bigint                             null comment '名义货价',
    rate_type           varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lease_rate_percent  int                                null comment '租赁利率值。百分之多少',
    irr_percent         int                                null comment '内部收益率。百分之多少',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    origin_id           bigint                             not null comment '草稿表id',
    version             varchar(32)                        not null comment '版本',
    data_create_time    datetime                           null,
    data_create_by      bigint                             null,
    data_update_time    datetime                           null,
    data_update_by      bigint                             null
    )
    comment '租赁报价方案表';

create table if not exists proj_review_aoc_price
(
    id                       bigint auto_increment comment '立项基本信息表id	'
    primary key,
    project_id               bigint                             not null comment '所属项目ID',
    apply_credit_amount      bigint                             null comment '申报授信金额',
    aoc_credit_term          int                                null comment '转让额度有效期（月',
    repay_type               varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop       tinyint(1)                         null comment '额度是否可循环',
    earnest_money            bigint                             null comment '保证金',
    confirmatory_party       text                               null comment '确权方',
    consulting_fee           bigint                             null comment '服务费/咨询费',
    aoc_financing_proportion int                                null comment '转让融资比例',
    summary                  text                               null comment '拟转应收账款概述',
    rate_type                varchar(10)                        null comment '转让费率类型。固定利率：fixed、浮动利率：float	',
    aoc_rate_percent         int                                null comment '转让费率值。百分之多少',
    irr_percent              int                                null comment '内部收益率。百分之多少',
    planned_starting_date    date                               null comment '计划起租日',
    rental_calc_type         varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                bigint                             null,
    create_time              datetime default CURRENT_TIMESTAMP null,
    update_by                bigint                             null,
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uniq_project_id
    unique (project_id) comment '每个项目只能有一份报价'
    )
    comment '债权转让报价方案表';

create table if not exists proj_review_aoc_price_lib
(
    id                       bigint auto_increment comment '立项基本信息表id	'
    primary key,
    project_id               bigint                             not null comment '所属项目ID',
    apply_credit_amount      bigint                             null comment '申报授信金额',
    aoc_credit_term          int                                null comment '转让额度有效期（月',
    repay_type               varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop       tinyint(1)                         null comment '额度是否可循环',
    earnest_money            bigint                             null comment '保证金',
    confirmatory_party       text                               null comment '确权方',
    consulting_fee           bigint                             null comment '服务费/咨询费',
    aoc_financing_proportion int                                null comment '转让融资比例',
    summary                  text                               null comment '拟转应收账款概述',
    rate_type                varchar(10)                        null comment '转让费率类型。固定利率：fixed、浮动利率：float	',
    aoc_rate_percent         int                                null comment '转让费率值。百分之多少',
    irr_percent              int                                null comment '内部收益率。百分之多少',
    planned_starting_date    date                               null comment '计划起租日',
    rental_calc_type         varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                bigint                             null,
    create_time              datetime default CURRENT_TIMESTAMP null,
    update_by                bigint                             null,
    update_time              datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version                  varchar(40)                        not null comment '版本号',
    origin_id                bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time         datetime                           null,
    data_create_by           bigint                             null,
    data_update_time         datetime                           null,
    data_update_by           bigint                             null
    )
    comment '债权转让报价方案lib表';

create index uniq_project_id
    on proj_review_aoc_price_lib (project_id)
    comment '每个项目只能有一份报价';

create table if not exists proj_review_base_info
(
    id                           bigint auto_increment
    primary key,
    client_id                    bigint                             null comment '客户id',
    biz_type                     varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    proj_name                    varchar(200)                       null comment '项目名称',
    project_type                 varchar(30)                        null comment '下拉框选项：公共事业类、省内国（央）企、其他。
内容决定后续审批流审批权限。
',
    project_classify             varchar(50)                        null comment '项目分类，可选项：鼓励类，适度支持类，谨慎类，工程机械类（厂商担保模式），集团内协同业务',
    approval_type                varchar(20)                        null comment '审批类型',
    proj_code                    varchar(20)                        null comment '项目编号',
    lease_types                  varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    factoring_types              varchar(100)                       null comment '保理类型。有追明保理、无追明保理、有追暗保理',
    proj_source                  varchar(20)                        null comment '存量翻单、渠道介绍、自主开发',
    funds_purpose                varchar(200)                       null comment '资金用途',
    proj_background              text                               null comment '项目背景',
    transferor_client_id         bigint                             null comment '转让方。（项目类型为租赁时）',
    lessee_info                  json                               null comment '承租人列表',
    creditor_client_id           bigint                             null comment '债权人id',
    creditor_stock_risk_exposure bigint                             null comment '债权人存量风险敞口',
    debtor_info                  json                               null comment '债务人信息。（项目类型为保理时）',
    guarantee_info               json                               null comment '担保人信息',
    pledgor_info                 json                               null comment '质押人信息',
    mortgagor_info               json                               null comment '抵押人信息',
    proj_sponsor_user_id         bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids      json                               null comment '项目协办方用户id列表',
    biz_dept_id                  bigint                             null comment '业务部门id',
    biz_dept_leader_id           bigint                             null comment '业务部门负责人id',
    biz_division_leader_id       bigint                             null comment '业务分管领导id',
    risk_control_manager_id      bigint                             null comment '风控经理id',
    legal_manager_user_id        bigint                             null,
    proj_review_status           varchar(50)                        null comment '立项状态',
    proj_review_process_status   varchar(50)                        null comment '流程状态',
    declared_amount              bigint                             null comment '冗余报价方案字段',
    proj_establish_id            bigint                             null comment '关联的立项ID',
    create_by                    bigint                             null comment '创建人、发起人',
    create_time                  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                    bigint                             null comment '最后更新人id',
    update_time                  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
    )
    comment '立项基本信息表';

create table if not exists proj_review_base_info_lib
(
    id                           bigint auto_increment
    primary key,
    client_id                    bigint                             null comment '客户id',
    biz_type                     varchar(20)                        null comment '业务类型。租赁、保理、转租赁',
    proj_name                    varchar(200)                       null comment '项目名称',
    project_type                 varchar(30)                        null comment '下拉框选项：公共事业类、省内国（央）企、其他。
内容决定后续审批流审批权限。
',
    project_classify             varchar(50)                        null comment '项目分类，可选项：鼓励类，适度支持类，谨慎类，工程机械类（厂商担保模式），集团内协同业务',
    approval_type                varchar(20)                        null comment '审批类型',
    proj_code                    varchar(20)                        null comment '项目编号',
    lease_types                  varchar(100)                       null comment '租赁类型。直租、回租、经营性租赁',
    factoring_types              varchar(100)                       null comment '保理类型。有追明保理、无追明保理、有追暗保理',
    proj_source                  varchar(20)                        null comment '存量翻单、渠道介绍、自主开发',
    funds_purpose                varchar(200)                       null comment '资金用途',
    proj_background              text                               null comment '项目背景',
    transferor_client_id         bigint                             null comment '转让方。（项目类型为租赁时）',
    lessee_info                  json                               null comment '承租人列表',
    creditor_client_id           bigint                             null comment '债权人id',
    creditor_stock_risk_exposure bigint                             null comment '债权人存量风险敞口',
    debtor_info                  json                               null comment '债务人信息。（项目类型为保理时）',
    guarantee_info               json                               null comment '担保人信息',
    pledgor_info                 json                               null comment '质押人信息',
    mortgagor_info               json                               null comment '抵押人信息',
    proj_sponsor_user_id         bigint                             null comment '项目主办用户id',
    proj_cosponsor_user_ids      json                               null comment '项目协办方用户id列表',
    create_by                    bigint                             null comment '创建人、发起人',
    biz_dept_id                  bigint                             null comment '业务部门id',
    biz_dept_leader_id           bigint                             null comment '业务部门负责人id',
    biz_division_leader_id       bigint                             null comment '业务分管领导id',
    risk_control_manager_id      bigint                             null comment '风控经理id',
    legal_manager_user_id        bigint                             null,
    proj_review_status           varchar(50)                        null comment '立项状态',
    proj_review_process_status   varchar(50)                        null comment '流程状态',
    declared_amount              bigint                             null comment '冗余报价方案字段',
    proj_establish_id            bigint                             null comment '关联的立项ID',
    create_time                  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by                    bigint                             null comment '最后更新人id',
    update_time                  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version                      varchar(40)                        not null comment '版本号',
    origin_id                    bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time             datetime                           null,
    data_create_by               bigint                             null,
    data_update_time             datetime                           null,
    data_update_by               bigint                             null
    )
    comment '评审基本信息表';

create table if not exists proj_review_cash_flow_plan
(
    id                  bigint unsigned auto_increment comment '现金流量明细表id'
    primary key,
    project_id          bigint                             not null comment '所属项目评审记录ID',
    cash_flow_date      date                               null comment '日期',
    cash_flow_phase     int(10)                            null comment '期项',
    cash_flow_amount    bigint                             null comment '现金流金额',
    rent                bigint                             null comment '租金',
    principal           bigint                             null comment '本金',
    interest            bigint                             null comment '利息',
    remaining_principal bigint                             null comment '剩余本金',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	'
    )
    comment '项目评审-现金流计划表';

create table if not exists proj_review_cash_flow_plan_lib
(
    id                  bigint auto_increment comment '现金流量明细表id'
    primary key,
    project_id          bigint                             not null comment '所属项目评审记录ID',
    cash_flow_date      date                               null comment '日期',
    cash_flow_phase     int(10)                            null comment '期项',
    cash_flow_amount    bigint                             null comment '现金流金额',
    rent                bigint                             null comment '租金',
    principal           bigint                             null comment '本金',
    interest            bigint                             null comment '利息',
    remaining_principal bigint                             null comment '剩余本金',
    create_by           bigint                             null comment '创建人id、发起人id	',
    create_time         datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by           bigint                             null comment '最后更新人id	',
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    version             varchar(40)                        not null comment '版本号',
    origin_id           bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time    datetime                           null,
    data_create_by      bigint                             null,
    data_update_time    datetime                           null,
    data_update_by      bigint                             null
    )
    comment '项目评审-现金流计划版本表';

create table if not exists proj_review_factoring_price
(
    id                             bigint auto_increment comment '立项基本信息表id	'
    primary key,
    project_id                     bigint                             not null comment '所属项目ID',
    apply_credit_amount            bigint                             null comment '申报授信金额',
    factoring_credit_term          int                                null comment '保理额度有效期（月',
    repay_type                     varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    earnest_money                  bigint                             null comment '保证金',
    confirmatory_party             text                               null comment '确权方',
    factoring_financing_proportion int                                null comment '保理融资比例',
    consulting_fee                 bigint                             null comment '服务费/咨询费',
    summary                        text                               null comment '拟转应收账款概述',
    rate_type                      varchar(10)                        null comment '保理费率类型。固定利率：fixed、浮动利率：float	',
    factoring_rate_percent         int                                null comment '保理费率值。百分之多少',
    irr_percent                    int                                null comment '内部收益率。百分之多少',
    existing_lease_credit          bit      default b'0'              null comment '是否有存续租赁授信',
    new_lease_credit               bit      default b'0'              null comment '是否新增租赁授信',
    planned_starting_date          date                               null comment '计划起租日',
    rental_calc_type               varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                      bigint                             null,
    create_time                    datetime default CURRENT_TIMESTAMP null,
    update_by                      bigint                             null,
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uniq_project_id
    unique (project_id) comment '每个立项只能有一份报价'
    )
    comment '保理报价方案表';

create table if not exists proj_review_factoring_price_lib
(
    id                             bigint auto_increment comment '立项基本信息表id	'
    primary key,
    project_id                     bigint                             not null comment '所属项目ID',
    apply_credit_amount            bigint                             null comment '申报授信金额',
    factoring_credit_term          int                                null comment '保理额度有效期（月',
    repay_type                     varchar(20)                        null comment '还款方式。间接还款：indirect、直接还款：direct',
    credit_amount_loop             tinyint(1)                         null comment '额度是否可循环',
    earnest_money                  bigint                             null comment '保证金',
    confirmatory_party             text                               null comment '确权方',
    factoring_financing_proportion int                                null comment '保理融资比例',
    consulting_fee                 bigint                             null comment '服务费/咨询费',
    summary                        text                               null comment '拟转应收账款概述',
    rate_type                      varchar(10)                        null comment '保理费率类型。固定利率：fixed、浮动利率：float	',
    factoring_rate_percent         int                                null comment '保理费率值。百分之多少',
    irr_percent                    int                                null comment '内部收益率。百分之多少',
    existing_lease_credit          bit      default b'0'              null comment '是否有存续租赁授信',
    new_lease_credit               bit      default b'0'              null comment '是否新增租赁授信',
    planned_starting_date          date                               null comment '计划起租日',
    rental_calc_type               varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    create_by                      bigint                             null,
    create_time                    datetime default CURRENT_TIMESTAMP null,
    update_by                      bigint                             null,
    update_time                    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    version                        varchar(40)                        not null comment '版本号',
    origin_id                      bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time               datetime                           null,
    data_create_by                 bigint                             null,
    data_update_time               datetime                           null,
    data_update_by                 bigint                             null
    )
    comment '保理报价方案表';

create index uniq_project_id
    on proj_review_factoring_price_lib (project_id)
    comment '每个立项只能有一份报价';

create table if not exists proj_review_lease_price
(
    id                    bigint auto_increment comment '立项基本信息表id'
    primary key,
    project_id            bigint                             not null comment '所属项目ID',
    apply_credit_amount   bigint                             null comment '申报授信金额',
    lease_month_count     int                                null comment '租赁期限月数',
    repay_times_yearly    int                                null comment '每年还款次数',
    repay_times_total     int                                null comment '还款期数',
    repay_rate            varchar(20)                        null comment '还款频率',
    pay_type              varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type      varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    credit_amount_loop    tinyint(1)                         null comment '额度是否可循环',
    earnest_money         bigint                             null comment '保证金',
    down_payment          bigint                             null comment '首付款',
    consulting_fee        bigint                             null comment '服务费/咨询费',
    nominal_price         bigint                             null comment '名义货价',
    rate_type             varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lease_rate_percent    int                                null comment '租赁利率值。百分之多少',
    irr_percent           int                                null comment '内部收益率。百分之多少',
    planned_starting_date date                               null comment '计划起租日',
    create_by             bigint                             null comment '创建人id、发起人id	',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by             bigint                             null comment '最后更新人id	',
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    constraint uniq_project_id
    unique (project_id) comment '每个立项只能有一份报价'
    )
    comment '租赁报价方案表';

create table if not exists proj_review_lease_price_lib
(
    id                    bigint auto_increment comment '立项基本信息表id'
    primary key,
    project_id            bigint                             not null comment '所属项目ID',
    apply_credit_amount   bigint                             null comment '申报授信金额',
    lease_month_count     int                                null comment '租赁期限月数',
    repay_times_yearly    int                                null comment '每年还款次数',
    repay_times_total     int                                null comment '还款期数',
    repay_rate            varchar(20)                        null comment '还款频率',
    pay_type              varchar(20)                        null comment '支付方式。先付：advanced、后付：afterward	',
    rental_calc_type      varchar(40)                        null comment '租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay

其他：other',
    credit_amount_loop    tinyint(1)                         null comment '额度是否可循环',
    earnest_money         bigint                             null comment '保证金',
    down_payment          bigint                             null comment '首付款',
    consulting_fee        bigint                             null comment '服务费/咨询费',
    nominal_price         bigint                             null comment '名义货价',
    rate_type             varchar(10)                        null comment '租赁利率类型。固定利率：fixed、浮动利率：float	',
    lease_rate_percent    int                                null comment '租赁利率值。百分之多少',
    irr_percent           int                                null comment '内部收益率。百分之多少',
    planned_starting_date date                               null comment '计划起租日',
    create_by             bigint                             null comment '创建人id、发起人id	',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间。默认当前时间',
    update_by             bigint                             null comment '最后更新人id	',
    update_time           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间；每次记录变化，自动更新为当前时间	',
    version               varchar(40)                        not null comment '版本号',
    origin_id             bigint                             not null comment '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
    data_create_time      datetime                           null,
    data_create_by        bigint                             null,
    data_update_time      datetime                           null,
    data_update_by        bigint                             null
    )
    comment '租赁报价方案表';

create index uniq_project_id
    on proj_review_lease_price_lib (project_id)
    comment '每个立项只能有一份报价';

create table if not exists task_delegate_rule
(
    id          bigint auto_increment comment '主键'
    primary key,
    employer_id varchar(64)                                   not null comment '转交人id',
    employee_id varchar(64)                                   not null comment '被转交人id',
    start_time  datetime                                      not null comment '规则生效时间',
    end_time    datetime                                      not null comment '规则结束时间',
    is_delete   tinyint(4) unsigned default 0                 not null comment '是否删除',
    gmt_create  datetime            default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify  datetime            default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间'
    )
    comment '任务转交规则表';

create index idx_employee_id
    on task_delegate_rule (employee_id);

create index idx_employer_id
    on task_delegate_rule (employer_id);

create table if not exists tmp_client_project
(
    id          bigint auto_increment comment '主键'
    primary key,
    client_id   bigint                             not null comment '客户id',
    status      tinyint  default 1                 not null comment '为1生效，为0不生效（审批拒绝）',
    create_time datetime default CURRENT_TIMESTAMP null,
    create_by   bigint                             null,
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by   bigint                             null
)
    comment '用于模拟客户立项校验';

create index idx_client_id
    on tmp_client_project (client_id);

create table if not exists tyc_abnormal
(
    id                bigint auto_increment comment '主键'
    primary key,
    client_id         bigint                             not null comment '客户id',
    remove_date       varchar(30)                        null comment '移出日期',
    put_reason        varchar(4091)                      null comment '列入异常名录原因',
    put_department    varchar(200)                       null comment '决定列⼊异常名录部⻔(作出决定机关)',
    remove_department varchar(200)                       null comment '移出部⻔',
    remove_reason     varchar(4091)                      null comment '移除异常名录原因',
    put_date          varchar(30)                        null comment '列入日期',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null
    )
    comment '天眼查_经营异常';

create index idx_client_id
    on tyc_abnormal (client_id);

create table if not exists tyc_consumption_restriction
(
    id               bigint auto_increment comment '主键'
    primary key,
    client_id        bigint                             not null comment '客户id',
    case_code        varchar(50)                        null comment '案号',
    file_path        varchar(150)                       null comment 'pdf文件地址',
    publish_date     datetime                           null comment '发布日期',
    xname            varchar(60)                        null comment '限制消费者名称',
    hcgid            varchar(50)                        null comment '限制消费者id',
    applicant        varchar(255)                       null comment '申请人信息',
    applicant_cid    varchar(255)                       null comment '申请人id',
    qyinfo_alias     varchar(100)                       null comment '企业信息',
    case_create_time datetime                           null comment '立案时间',
    alias            varchar(100)                       null comment '别名',
    tyc_id           bigint                             null comment '天眼查id',
    cid              bigint                             null comment '企业id',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null
    )
    comment '天眼查_限制消费令';

create index idx_client_id
    on tyc_consumption_restriction (client_id);

create table if not exists tyc_dishonest
(
    id                bigint auto_increment comment '主键'
    primary key,
    client_id         bigint                             not null comment '客户id',
    business_entity   varchar(60)                        null comment '法人、负责人姓名',
    area_name         varchar(30)                        null comment '省份地区',
    court_name        varchar(50)                        null comment '法院',
    unperform_part    longtext                           null comment '未履行部分',
    staff_json        varchar(512)                       null comment '法定负责人/主要负责人信息',
    type              varchar(2)                         null comment '失信⼈类型，0代表⼈，1代表公司',
    performed_part    longtext                           null comment '已履行部分',
    iname             varchar(60)                        null comment '失信人名称',
    disrupt_type_name varchar(2000)                      null comment '失信被执行人行为具体情形',
    case_code         varchar(50)                        null comment '案号',
    card_num          varchar(30)                        null comment '身份证号码/组织机构代码',
    performance       varchar(60)                        null comment '履行情况',
    reg_date          datetime                           null comment '立案时间',
    publish_date      datetime                           null comment '发布时间',
    gist_unit         varchar(60)                        null comment '做出执行的依据单位',
    duty              longtext                           null comment '生效法律文书确定的义务',
    gist_id           varchar(100)                       null comment '执行依据文号',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null
    )
    comment '天眼查_失信人';

create index idx_client_id
    on tyc_dishonest (client_id);

create table if not exists tyc_equity_info
(
    id                  bigint auto_increment comment '主键'
    primary key,
    client_id           bigint                             not null comment '客户id',
    pledgee_json        longtext                           null comment '质权人列表',
    reg_date            datetime                           null comment '股权出质设立登记日期',
    pledgor             varchar(255)                       null comment '出质人',
    certif_number_r     varchar(20)                        null comment '质权人证照/证件号码',
    pledgee             varchar(255)                       null comment '质权人',
    reg_number          varchar(50)                        null comment '登记编号',
    certif_number       varchar(20)                        null comment '出质人证照/证件号码',
    company_json        longtext                           null comment '公司列表',
    target_company_json varchar(350)                       null comment '出质股权标的企业',
    pledgor_json        longtext                           null comment '出质人列表',
    equity_amount       varchar(20)                        null comment '出质股权数额',
    tyc_id              bigint                             null comment '天眼查id',
    state               varchar(31)                        null comment '状态',
    put_date            datetime                           null comment '股权出质设立发布日期',
    create_time         datetime default CURRENT_TIMESTAMP null,
    create_by           bigint                             null,
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by           bigint                             null
    )
    comment '天眼查_股权出质';

create index idx_client_id
    on tyc_equity_info (client_id);

create table if not exists tyc_judicial
(
    id                     bigint auto_increment comment '主键'
    primary key,
    client_id              bigint                             not null comment '客户id',
    execute_notice_num     varchar(200)                       null comment '执行通知书文号',
    executed_person_cid    bigint                             null comment '执行人公司id',
    publicity_date         varchar(30)                        null comment '公示日期',
    stock_executed_company varchar(255)                       null comment '股权被执行的企业',
    executed_person_hid    bigint                             null comment '被执行人hgid',
    stock_executed_cid     bigint                             null comment '股权被执行的企业id',
    executed_person        varchar(50)                        null comment '被执行人',
    ass_id                 varchar(100)                       null comment '司法协助基本信息id',
    equity_amount          varchar(50)                        null comment '股权数额',
    tyc_id                 bigint                             null comment '天眼查id',
    type_state             varchar(50)                        null comment '类型',
    executed_person_type   varchar(50)                        null comment '执⾏⼈类型，2-⼈，1-公司',
    executive_court        varchar(100)                       null comment '执行法院',
    create_time            datetime default CURRENT_TIMESTAMP null,
    create_by              bigint                             null,
    update_time            datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by              bigint                             null
    )
    comment '天眼查_司法协助';

create index idx_client_id
    on tyc_judicial (client_id);

create table if not exists tyc_law_suit
(
    id                bigint auto_increment comment '主键'
    primary key,
    client_id         bigint                             not null comment '客户id',
    doc_type          varchar(1000)                      null comment '文书类型',
    lawsuit_url       varchar(150)                       null comment '天眼查url（Web）',
    lawsuit_h5_url    varchar(150)                       null comment '天眼查url（H5）',
    title             varchar(2000)                      null comment '案件名称',
    court             varchar(100)                       null comment '审理法院',
    judge_time        varchar(30)                        null comment '裁判日期',
    uuid              varchar(50)                        null comment 'uuid',
    case_no           varchar(1000)                      null comment '案号',
    case_type         varchar(50)                        null comment '案件类型',
    case_reason       varchar(500)                       null comment '案由',
    case_persons_json longtext                           null comment '涉案方',
    case_money        varchar(100)                       null comment '案件金额',
    submit_time       datetime                           null comment '发布日期',
    tyc_id            bigint                             null comment '天眼查id',
    detail_json       longtext                           null comment '详情',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null
    )
    comment '天眼查_法律诉讼';

create index idx_client_id
    on tyc_law_suit (client_id);

create table if not exists tyc_mortgage_info
(
    id               bigint auto_increment comment '主键'
    primary key,
    client_id        bigint                             not null comment '客户id',
    amount           varchar(50)                        null comment '被担保债权数额',
    cancel_date      datetime                           null comment '注销日期',
    publish_date     datetime                           null comment '公示日期',
    reg_date         varchar(30)                        null comment '登记日期',
    remark           varchar(1000)                      null comment '备注',
    type             varchar(50)                        null comment '被担保债权种类',
    reg_department   varchar(255)                       null comment '登记机关',
    reg_num          varchar(100)                       null comment '登记编号',
    scope            varchar(1000)                      null comment '担保范围',
    term             varchar(1000)                      null comment '债务人履行债务的期限',
    tyc_id           bigint                             null comment '天眼查表id',
    cancel_reason    varchar(500)                       null comment '注销原因',
    status           varchar(100)                       null comment '状态',
    base             varchar(4)                         null comment '省份',
    people_info_json longtext                           null comment '抵押权人信息',
    pawn_info_json   longtext                           null comment '抵押物信息',
    change_info_json longtext                           null comment '变更信息',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null
    )
    comment '天眼查_动产抵押';

create index idx_client_id
    on tyc_mortgage_info (client_id);

create table if not exists tyc_punishment_info
(
    id                bigint auto_increment comment '主键'
    primary key,
    client_id         bigint                             not null comment '客户id',
    department_name   varchar(512)                       null comment '处罚单位',
    reason            varchar(512)                       null comment '处罚事由/违法行为类型',
    evidence          varchar(1000)                      null comment '处罚依据（source=信⽤中国时返回数据）',
    punish_status     varchar(10)                        null comment '处罚状态（source=信⽤中国时返回数据）',
    remark            varchar(100)                       null comment '备注（source=国家市场监督管理总局时返回数据）',
    source            varchar(30)                        null comment '数据来源',
    type              varchar(100)                       null comment '处罚类别1（source=信⽤中国时返回数据）',
    content           text                               null comment '处罚结果/内容',
    decision_date     varchar(30)                        null comment '日期',
    legal_person_name varchar(120)                       null comment '法定代表⼈（source=国家市场监督管理总局时返回数据）',
    punish_name       varchar(500)                       null comment '处罚名称（source=信⽤中国时返回数据）',
    punish_number     varchar(256)                       null comment '决定⽂书号',
    type_second       varchar(100)                       null comment '处罚类别2（source=信⽤中国时返回数据）',
    create_time       datetime default CURRENT_TIMESTAMP null,
    create_by         bigint                             null,
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by         bigint                             null
    )
    comment '天眼查_行政处罚';

create index idx_client_id
    on tyc_punishment_info (client_id);

create table if not exists tyc_zhixing_info
(
    id               bigint auto_increment comment '主键'
    primary key,
    client_id        bigint                             not null comment '客户id',
    case_code        varchar(50)                        null comment '案号',
    party_card_num   varchar(30)                        null comment '身份证号/组织机构代码',
    pname            varchar(60)                        null comment '被执行人名称',
    exec_court_name  varchar(50)                        null comment '执行法院',
    case_create_time datetime                           null comment '创建时间',
    exec_money       varchar(20)                        null comment '执行标的（元）',
    create_time      datetime default CURRENT_TIMESTAMP null,
    create_by        bigint                             null,
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by        bigint                             null
    )
    comment '天眼查_被执行人';

create index idx_client_id
    on tyc_zhixing_info (client_id);

create table if not exists vote_record
(
    id                  bigint auto_increment comment '主键'
    primary key,
    process_instance_id varchar(64)                        not null comment '流程实例id',
    voter_id            varchar(64)                        not null comment '投票人id',
    task_activity_id    varchar(64)                        not null comment '投票节点id',
    task_id             varchar(64)                        not null comment '任务id',
    vote_key            varchar(64)                        not null comment '标记多次投票属于一次投票',
    type                varchar(32)                        not null comment '投票结果枚举',
    note                varchar(1024)                      null comment '投票备注',
    gmt_create          datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    gmt_modify          datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间'
    )
    comment '投票记录表（任务通过）';

create index idx_process_instance_id
    on vote_record (process_instance_id);

create index idx_voter_id
    on vote_record (voter_id);

create table if not exists zhfk_notice
(
    id         bigint auto_increment
    primary key,
    title      varchar(32)  null comment '标题',
    content    varchar(200) null comment '内容',
    type       smallint     not null comment '1通知，2公告',
    status     smallint     not null comment '0正常，1关闭',
    remark     varchar(200) null comment '备注',
    data_scope json         null comment '数据范围（deptId,roleId,userID)',
    create_by  varchar(32)  null comment '创建用户，发送者',
    gmt_create timestamp    null on update CURRENT_TIMESTAMP comment '创建时间',
    update_by  varchar(32)  null comment '修改用户',
    gmt_update timestamp    null on update CURRENT_TIMESTAMP comment '修改时间',
    deal_user  varchar(32)  null comment '处理用户列表，ALL则为所有用户',
    biz_info   json         null comment '业务模块信息',
    record_id  varchar(20)  null comment 'Redis_stream_offset'
    )
    collate = utf8mb4_bin;

create index type_status
    on zhfk_notice (type, status);

create table if not exists zhfk_notice_relation
(
    id          bigint auto_increment
    primary key,
    message_id  bigint                             null comment '消息ID',
    create_time datetime default CURRENT_TIMESTAMP null,
    update_time datetime default CURRENT_TIMESTAMP null
)
    comment '消息通知-消息关联表';

create table if not exists zhfk_task_operation
(
    id         bigint auto_increment
    primary key,
    user_id    varchar(60) not null comment '操作用户',
    notice_id  bigint      not null comment '处理待办id',
    gmt_create timestamp   null on update CURRENT_TIMESTAMP comment '创建时间',
    biz_info   json        null comment '业务信息'
    )
    collate = utf8mb4_bin;

create index user_id
    on zhfk_task_operation (user_id);

create table if not exists zhongdeng_info
(
    id                  bigint auto_increment comment '主键'
    primary key,
    client_id           bigint                             not null comment '客户id',
    trade_business_type varchar(32)                        null comment '交易业务类型',
    credit_org          varchar(128)                       null comment '授信机构',
    amount              bigint                             null comment '金额',
    reg_date            datetime                           null comment '登记日期',
    reg_expire_date     datetime                           null comment '登记到期日',
    term                int                                null comment '期限（年）',
    create_time         datetime default CURRENT_TIMESTAMP null,
    create_by           bigint                             null,
    update_time         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    update_by           bigint                             null
    )
    comment '中登网';

create index idx_client_id
    on zhongdeng_info (client_id);

alter table zhfk_notice_relation add mithras_id VARCHAR(40) DEFAULT NULL COMMENT  '租赁系统id';

CREATE TABLE if not exists `base_data_special_date` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `special_type` varchar(20) NOT NULL DEFAULT '' COMMENT '特殊类型，HOLIDAY-节假日，WORKDAY-工作日',
  `year` int(10) NOT NULL COMMENT '年份',
  `month` int(10) NOT NULL COMMENT '月份',
  `special_date` date NOT NULL COMMENT '特殊日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基础数据-特殊日期';

CREATE TABLE if not exists `contract_pledge_item` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) unsigned NOT NULL COMMENT '合同id',
  `pledge_id` bigint(20) unsigned NOT NULL COMMENT '质押措施id',
  `sequence` int(10) DEFAULT NULL COMMENT '序号',
  `category` varchar(100) DEFAULT NULL COMMENT '种类',
  `unique_identify_code` varchar(1000) DEFAULT NULL COMMENT '唯一识别号',
  `unique_identify_code_type` varchar(100) DEFAULT NULL COMMENT '识别号类型',
  `name` varchar(100) DEFAULT '' COMMENT '设备名称',
  `supplier` varchar(100) DEFAULT '' COMMENT '供应商',
  `quantity` varchar(20) DEFAULT '' COMMENT '数量',
  `unit` varchar(10) DEFAULT '' COMMENT '计量单位',
  `purchase_date` varchar(50) DEFAULT '' COMMENT '购置日期',
  `original_book_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面原值',
  `original_book_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面净值',
  `assessed_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估原值',
  `assessed_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估净值',
  `invoice_code` varchar(1000) DEFAULT '' COMMENT '发票号',
  `storage_place` varchar(100) DEFAULT '' COMMENT '存放地点',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同明细-质押物清单';

CREATE TABLE if not exists `contract_pledge_item_lib` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `contract_id` bigint(20) unsigned NOT NULL COMMENT '合同id',
  `pledge_id` bigint(20) unsigned NOT NULL COMMENT '质押措施id',
  `sequence` int(10) DEFAULT NULL COMMENT '序号',
  `category` varchar(100) DEFAULT NULL COMMENT '种类',
  `unique_identify_code` varchar(1000) DEFAULT NULL COMMENT '唯一识别号',
  `unique_identify_code_type` varchar(100) DEFAULT NULL COMMENT '识别号类型',
  `name` varchar(100) DEFAULT '' COMMENT '设备名称',
  `supplier` varchar(100) DEFAULT '' COMMENT '供应商',
  `quantity` varchar(20) DEFAULT '' COMMENT '数量',
  `unit` varchar(10) DEFAULT '' COMMENT '计量单位',
  `purchase_date` varchar(50) DEFAULT '' COMMENT '购置日期',
  `original_book_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面原值',
  `original_book_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '账面净值',
  `assessed_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估原值',
  `assessed_net_value` bigint(20) unsigned DEFAULT NULL COMMENT '评估净值',
  `invoice_code` varchar(1000) DEFAULT '' COMMENT '发票号',
  `storage_place` varchar(100) DEFAULT '' COMMENT '存放地点',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` varchar(40) NOT NULL COMMENT '版本号',
  `origin_id` bigint(20) NOT NULL COMMENT '临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写',
  `data_create_time` datetime DEFAULT NULL,
  `data_create_by` bigint(20) DEFAULT NULL,
  `data_update_time` datetime DEFAULT NULL,
  `data_update_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同明细-质押物清单版本';

