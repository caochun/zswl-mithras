-- 航运评级客户信息
-- drop table client_hymx ;
CREATE TABLE client_hymx (
                             id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             client_name varchar(64) NOT NULL COMMENT '客户名称',
                             client_code varchar(64)  COMMENT '客户编号',
                             project_manager varchar(20)  COMMENT '项目经理',
                             belong_dept_id varchar(20)  COMMENT '所属部门',
                             deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：1=已删除，0=未删除',
                             create_by bigint(20) NOT NULL COMMENT '创建人ID（关联sys_user.id）',
                             create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             update_by bigint(20) NOT NULL COMMENT '更新人ID（关联sys_user.id）',
                             update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='航运评级客户信息表';

-- 调整自增主键开始数值
ALTER TABLE client_hymx AUTO_INCREMENT = 500000000000;


