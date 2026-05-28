-- 合同关联租赁物名称拼接
CREATE TABLE contract_lease_item_name (
     id bigint(20) NOT NULL COMMENT '合同ID',
     name text COMMENT '租赁物名称',
     deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：1=已删除，0=未删除',
     create_by bigint(20)  COMMENT '创建人ID（关联sys_user.id）',
     create_time datetime  DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     update_by bigint(20)  COMMENT '更新人ID（关联sys_user.id）',
     update_time datetime  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='合同关联租赁物名称表';


