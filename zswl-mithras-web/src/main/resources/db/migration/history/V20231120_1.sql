INSERT INTO bifrost_function (code, name, sort_no, menu_id, create_by, update_by,
                              en_name, method, path, type, group_id)
VALUES ('corpsubjectitemremove', '删除财报', 0, 6, null, null,
        null, 'POST', '/corp/subject/item/remove', 2, 69);

-- 租后检查报告模板优化
CREATE TABLE `new_after_lease_check_report_field_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_type` varchar(50) DEFAULT NULL,
  `field_name` varchar(20) NOT NULL COMMENT '字段名称',
  `field_type` varchar(20) DEFAULT '' COMMENT '字段类型',
  `field_option` text COMMENT '字段枚举',
  `field_remark` varchar(200) NOT NULL COMMENT '字段说明',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='（新）租后检查报告字段映射表';

CREATE TABLE `new_after_lease_check_report_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_plan_client_id` bigint(20) NOT NULL COMMENT '检查计划-客户信息表主键id',
  `report_content` longtext COMMENT '检查报告内容',
  `report_summary` longtext COMMENT '检查报告总结',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_plan_client_id` (`check_plan_client_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='（新）租后检查报告-报告详情';

CREATE TABLE `new_after_lease_check_report_detail_lib` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_plan_client_id` bigint(20) NOT NULL COMMENT '检查计划-客户信息表主键id',
  `report_content` longtext COMMENT '检查报告内容',
  `report_summary` longtext COMMENT '检查报告总结',
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
  `version_type` tinyint(4) DEFAULT '1' COMMENT '版本标志，0无效，1有效...业务自扩展',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_plan_client_id` (`check_plan_client_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='（新）租后检查报告-报告详情版本表';

alter table `new_after_lease_check_report_meta` add column `report_template_version` varchar(10) NOT NULL DEFAULT 'V1' COMMENT '检查报告模板版本' after `report_type`;
alter table `new_after_lease_check_report_meta_lib` add column `report_template_version` varchar(10) NOT NULL DEFAULT 'V1' COMMENT '检查报告模板版本' after `report_type`;

