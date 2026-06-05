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

