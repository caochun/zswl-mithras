alter table contract_base_info
    add overdue_collection_flag tinyint(1) DEFAULT '1' COMMENT '逾期催收状态0可催收，1不可催收';

alter table contract_base_info_lib
    add overdue_collection_flag tinyint(1) DEFAULT NULL COMMENT '逾期催收状态0可催收，1不可催收';
