CREATE INDEX materials_list_belong_id_IDX USING BTREE ON mithras.materials_list (belong_id,business_type,materials_type,materials_sub_type);

ALTER TABLE mithras.bifrost_function ADD group_id BIGINT NULL COMMENT '功能分组id';

CREATE TABLE mithras.gruul_function_group (
                                              id BIGINT auto_increment NOT NULL COMMENT '主键',
                                              gmt_create TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
                                              gmt_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP NOT NULL COMMENT '修改时间',
                                              create_by varchar(100) NULL COMMENT '创建人',
                                              update_by varchar(100) NULL COMMENT '修改人',
                                              sort_no INT DEFAULT 0 NULL COMMENT '排序',
                                              code varchar(128) NULL COMMENT '标志code',
                                              name varchar(100) NULL COMMENT '分组名称',
                                              group_describe varchar(300) NULL COMMENT '分组描述',
                                              menu_id BIGINT NOT NULL COMMENT '菜单id',
                                              CONSTRAINT gruul_function_group_pk PRIMARY KEY (id),
                                              CONSTRAINT gruul_function_group_un UNIQUE KEY (code)
)
    ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_general_ci;
