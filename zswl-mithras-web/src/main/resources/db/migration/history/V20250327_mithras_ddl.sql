-- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> FTP调整
alter table proj_pricing_base_info add column ftp_industry_category varchar(50) default null comment 'FTP行业分类';
alter table proj_pricing_base_info_lib add column ftp_industry_category varchar(50) default null comment 'FTP行业分类';

alter table proj_pricing_base_info add column project_manage_level varchar(50) default null comment '项目管理层级';
alter table proj_pricing_base_info_lib add column project_manage_level varchar(50) default null comment '项目管理层级';

alter table proj_pricing_base_info add column is_AAA tinyint(4) default null comment '是否AAA评级';
alter table proj_pricing_base_info_lib add column is_AAA tinyint(4) default null comment '是否AAA评级';

alter table proj_review_base_info add column ftp_industry_category varchar(50) default null comment 'FTP行业分类';
alter table proj_review_base_info_lib add column ftp_industry_category varchar(50) default null comment 'FTP行业分类';

alter table ftp_assessment_info add column hangyong_special_adjustment bigint(20) default 0 comment '杭甬特殊调整';

alter table ftp_assessment_info add column ftp_interest_change_apply_record_id bigint(20) not null default 0 comment 'FTP计息变更申请表id';
alter table ftp_assessment_info add column effect_date date not null default '1970-01-01' comment 'FTP生效日期';
alter table ftp_assessment_info add column ftp_interest_diff_date date default null comment 'FTP计息变更差额调整日期';
alter table ftp_assessment_info add column is_effect tinyint(1) default 0 comment 'FTP价格是否生效，0-否，1-是';
alter table ftp_assessment_info add column receipt_id bigint(20) not null default 0 comment '借据id';

alter table new_ftp_base_info add column ftp_business_version varchar(10) not null default 'V2' comment '业务版本';
alter table new_ftp_monthly_guidance_template_config add column ftp_business_version varchar(10) not null default 'V2' comment '业务版本';
alter table new_ftp_monthly_guidance_template_draft add column ftp_business_version varchar(10) not null default 'V2' comment '业务版本';
alter table new_ftp_quarterly_base_pricing_template_config add column ftp_business_version varchar(10) not null default 'V2' comment '业务版本';
alter table new_ftp_quarterly_base_pricing_template_draft add column ftp_business_version varchar(10) not null default 'V2' comment '业务版本';
alter table new_ftp_monthly_deduction_draft add column customer_listed_state_owned int(11) default null comment '其他产业类-客户主体计价-上市公司/国有企业';
alter table new_ftp_monthly_deduction_draft add column customer_other_listed int(11) default null comment '其他产业类-客户主体计价-其他上市公司';
alter table new_ftp_monthly_deduction_lib add column customer_listed_state_owned int(11) default null comment '其他产业类-客户主体计价-上市公司/国有企业';
alter table new_ftp_monthly_deduction_lib add column customer_other_listed int(11) default null comment '其他产业类-客户主体计价-其他上市公司';
alter table new_ftp_monthly_deduction_draft add column civil_consumption_region_zhejiang int(11) default null comment '民生消费类-地区分类计价-浙江地区';
alter table new_ftp_monthly_deduction_draft add column civil_consumption_region_encourage int(11) default null comment '民生消费类-地区分类计价-鼓励支持类地区';
alter table new_ftp_monthly_deduction_draft add column civil_consumption_region_other int(11) default null comment '民生消费类-地区分类计价-其他地区';
alter table new_ftp_monthly_deduction_lib add column civil_consumption_region_zhejiang int(11) default null comment '民生消费类-地区分类计价-浙江地区';
alter table new_ftp_monthly_deduction_lib add column civil_consumption_region_encourage int(11) default null comment '民生消费类-地区分类计价-鼓励支持类地区';
alter table new_ftp_monthly_deduction_lib add column civil_consumption_region_other int(11) default null comment '民生消费类-地区分类计价-其他地区';
alter table new_ftp_monthly_deduction_draft add column state_owned_industry_region_zhejiang int(11) default null comment '国有产业类-地区分类计价-浙江地区';
alter table new_ftp_monthly_deduction_draft add column state_owned_industry_region_encourage int(11) default null comment '国有产业类-地区分类计价-鼓励支持类地区';
alter table new_ftp_monthly_deduction_draft add column state_owned_industry_region_other int(11) default null comment '国有产业类-地区分类计价-其他地区';
alter table new_ftp_monthly_deduction_lib add column state_owned_industry_region_zhejiang int(11) default null comment '国有产业类-地区分类计价-浙江地区';
alter table new_ftp_monthly_deduction_lib add column state_owned_industry_region_encourage int(11) default null comment '国有产业类-地区分类计价-鼓励支持类地区';
alter table new_ftp_monthly_deduction_lib add column state_owned_industry_region_other int(11) default null comment '国有产业类-地区分类计价-其他地区';

alter table ftp_interest_detail_record add column ftp_overdue_adjust int(10) not null default 0 comment 'FTP价格逾期调整';
alter table ftp_interest_detail_record add column remark text default null comment '备注说明';

CREATE TABLE `new_ftp_change_apply_record` (
                                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                                               `apply_user_id` bigint(20) NOT NULL COMMENT '申请人id',
                                               `approval_status` varchar(20) NOT NULL DEFAULT '' COMMENT '审批状态',
                                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
                                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
                                               `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除，0-未删除，1-已删除',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='FTP计息变更申请表';

alter table ftp_assessment_info add key idx_ftp_interest_change_apply_id (ftp_interest_change_apply_record_id);
alter table ftp_assessment_info add key idx_receipt_id (receipt_id);
-- >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> FTP调整
