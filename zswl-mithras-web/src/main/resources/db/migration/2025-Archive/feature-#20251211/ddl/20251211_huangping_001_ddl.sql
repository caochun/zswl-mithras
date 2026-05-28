-- 流程抄送配置表
CREATE TABLE flow_node_cc_config (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    flow_key VARCHAR(255) NOT NULL COMMENT '流程标识',
    node_key VARCHAR(255) NOT NULL COMMENT '流程节点标识（对应流程阶段唯一key）',
    object_type VARCHAR(12) NOT NULL COMMENT '抄送对象类型：job=角色，user=用户',
    object_ids VARCHAR(512) NOT NULL COMMENT '抄送对象账号集合（多个以英文逗号,拼接，如：a,b,c）',
    deleted TINYINT(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：1=已删除，0=未删除',
    create_by BIGINT(20) NOT NULL COMMENT '创建人ID（关联sys_user.id）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by BIGINT(20) NOT NULL COMMENT '更新人ID（关联sys_user.id）',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_flow_node (flow_key, node_key) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='流程节点抄送配置表';