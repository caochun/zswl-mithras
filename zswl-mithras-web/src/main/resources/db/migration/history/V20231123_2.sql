/*update mithras.bifrost_role set name='租赁物管理员' where id = 102;

-- 租赁物审核
CREATE TABLE `lease_item_info` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                   `proj_review_id` bigint(20) unsigned NOT NULL COMMENT '项目评审id',
                                   `proj_name` varchar(200) DEFAULT NULL COMMENT '项目名称',
                                   `client_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
                                   `proj_sponsor_user_id` bigint(20) DEFAULT NULL COMMENT '项目主办用户id',
                                   `proj_cosponsor_user_ids` varchar(100) DEFAULT NULL COMMENT '项目协办方用户id列表',
                                   `flow_id` varchar(50) DEFAULT NULL COMMENT '流程id',
                                   `relevance_flow_id` varchar(50) DEFAULT NULL COMMENT '关联流程id',
                                   `duplicate_checking_date` date DEFAULT NULL COMMENT '中登网查重时间',
                                   `lease_item_types` varchar(150) DEFAULT NULL COMMENT '租赁物类型',
                                   `ownership_file_types` varchar(150) DEFAULT NULL COMMENT '权属文件类型',
                                   `value_identification_files` varchar(150) DEFAULT NULL COMMENT '价值认定文件',
                                   `total_amount_of_lease_item` bigint(20) unsigned DEFAULT NULL COMMENT '租赁物总额',
                                   `approval_status` varchar(20) DEFAULT NULL COMMENT '流程状态',
                                   `item_list_header` text COMMENT '租赁物清单表头',
                                   `contract_ids` json DEFAULT NULL COMMENT '占有合同ID列表',
                                   `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='租赁物管理信息';

CREATE TABLE `lease_item_list_row_data` (
                                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                            `lease_item_info_id` bigint(20) unsigned NOT NULL COMMENT '租赁物审核管理id',
                                            `row_data` text COMMENT '租赁物清单行数据',
                                            `create_by` bigint(20) DEFAULT NULL COMMENT '创建人、发起人',
                                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `update_by` bigint(20) DEFAULT NULL COMMENT '最后更新人id',
                                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `idx_lease_item_info_id` (`lease_item_info_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='租赁物清单数据';

INSERT INTO `bifrost_custom_tree` (`code`, `name`, `flag`, `sort_no`, `parent_id`, `create_by`, `update_by`, `en_name`, `icon`, `path`, `lang_env`)
VALUES
    ('leaseMaintain', '租赁物管理', 1, 0, NULL, NULL, NULL, NULL, 'icon-zulinwuguanli', NULL, NULL);

INSERT INTO `bifrost_menu` (`code`, `level`, `sort_no`, `type`, `path`, `parent_id`, `icon`, `name`, `en_name`, `target`, `create_by`, `update_by`)
VALUES
    ('leaseMaintain', 3, 0, NULL, '/lease/maintain', NULL, 'icon-zulinwuguanli', '审核管理台账', NULL, NULL, NULL, NULL);

alter table `contract_lease_item` add column `row_data` text COMMENT '行数据' after `storage_place`;
alter table `contract_lease_item_lib` add column `row_data` text COMMENT '行数据' after `storage_place`;
alter table `contract_lease_item` add column `lease_item_row_data_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '引用的租赁物审核管理的行数据id' after `storage_place`;
alter table `contract_lease_item_lib` add column `lease_item_row_data_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '引用的租赁物审核管理的行数据id' after `storage_place`;

INSERT INTO `general_dictionary` (`dict_key`, `dict_desc`, `code`, `display`, `sort`)
VALUES
	('FILE_TEMPLATE_TYPE', '文件模板类型', '租赁物模版-中登网查重', '租赁物模版-中登网查重', 10),
	('FILE_TEMPLATE_TYPE', '文件模板类型', '租赁物管理-租赁物清单', '租赁物管理-租赁物清单', 13);

alter table `contract_base_info` add column `item_list_header` text COMMENT '租赁物清单表头' after `contract_status`;
alter table `contract_base_info_lib` add column `item_list_header` text COMMENT '租赁物清单表头' after `contract_status`;
alter table `contract_base_info` add column `item_total_amount` bigint(20) DEFAULT NULL COMMENT '租赁物总额' after `contract_status`;
alter table `contract_base_info_lib` add column `item_total_amount` bigint(20) DEFAULT NULL COMMENT '租赁物总额' after `contract_status`;

alter table contract_base_info add column lease_item_info_id bigint(20) not null default -1 comment '合同引用的租赁物审核id' after `contract_status`;
alter table contract_base_info_lib add column lease_item_info_id bigint(20) not null default -1 comment '合同引用的租赁物审核id' after `contract_status`;*/