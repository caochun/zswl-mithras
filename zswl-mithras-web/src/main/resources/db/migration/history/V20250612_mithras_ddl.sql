alter table contract_pledge add column `relat_contracts` json DEFAULT NULL COMMENT '关联合同ID' after `pledge_contract_code`;
alter table contract_pledge_lib add column `relat_contracts` json DEFAULT NULL COMMENT '关联合同ID' after `pledge_contract_code`;
alter table proj_review_meet_minute_base_info modify special_contract_terms json default null comment '特殊合同条款';
alter table proj_review_meet_minute_base_info modify conditions_before_disbursement json default null comment '放款前须落实条件';

alter table proj_review_meet_minute_base_info add column approval_conditions_project_change text default null comment '项目变更批复条件';
