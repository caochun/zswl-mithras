ALTER TABLE new_after_lease_check_plan_client ADD COLUMN `deadline` date DEFAULT NULL COMMENT '截止日期标签';
ALTER TABLE new_after_lease_check_plan_client_lib ADD COLUMN `deadline` date DEFAULT NULL COMMENT '截止日期标签';

ALTER TABLE new_after_lease_check_plan_client ADD COLUMN `deadline_label` varchar(128) DEFAULT NULL COMMENT '截止日期标签';
ALTER TABLE new_after_lease_check_plan_client_lib ADD COLUMN `deadline_label` varchar(128) DEFAULT NULL COMMENT '截止日期标签';

ALTER TABLE new_after_lease_check_plan_base ADD COLUMN `deadline_label` varchar(128) DEFAULT NULL COMMENT '截止日期标签' AFTER dead_line;
ALTER TABLE new_after_lease_check_plan_base_lib ADD COLUMN `deadline_label` varchar(128) DEFAULT NULL COMMENT '截止日期标签' AFTER dead_line;

ALTER TABLE new_after_lease_check_plan_client ADD COLUMN next_deadline DATE NULL  COMMENT '截止日期标签';
ALTER TABLE new_after_lease_check_plan_client_lib ADD COLUMN next_deadline DATE NULL  COMMENT '截止日期标签';

ALTER TABLE new_after_lease_check_plan_client
    ADD COLUMN tmp_next_deadline VARCHAR(32) NULL  COMMENT '临时截止日期（审批完成之后copy）' AFTER deadline;
ALTER TABLE new_after_lease_check_plan_client_lib
    ADD COLUMN tmp_next_deadline VARCHAR(32) NULL  COMMENT '临时截止日期（审批完成之后copy）' AFTER deadline;

ALTER TABLE new_after_lease_check_plan_client
    ADD COLUMN next_check_way VARCHAR(32) NULL  COMMENT '下次租后检查形式' AFTER deadline_label;
ALTER TABLE new_after_lease_check_plan_client_lib
    ADD COLUMN next_check_way VARCHAR(32) NULL  COMMENT '下次租后检查形式' AFTER deadline_label;

ALTER TABLE new_after_lease_check_plan_client
    ADD COLUMN tmp_next_check_way VARCHAR(32) NULL  COMMENT '临时-下次租后检查形式' AFTER next_check_way;
ALTER TABLE new_after_lease_check_plan_client_lib
    ADD COLUMN tmp_next_check_way VARCHAR(32) NULL  COMMENT '临时-下次租后检查形式' AFTER next_check_way;

ALTER TABLE new_after_lease_check_plan_client
    ADD COLUMN remaining_principal BIGINT NULL COMMENT '剩余本金' AFTER tmp_next_deadline;
ALTER TABLE new_after_lease_check_plan_client_lib
    ADD COLUMN remaining_principal BIGINT NULL COMMENT '剩余本金' AFTER tmp_next_deadline;

ALTER TABLE new_after_lease_check_report_base
    ADD COLUMN remaining_principal BIGINT NULL COMMENT '剩余本金';
ALTER TABLE new_after_lease_check_report_base_lib
    ADD COLUMN remaining_principal BIGINT NULL COMMENT '剩余本金';

-- 历史数据需要做初始化
ALTER TABLE new_after_lease_check_plan_client ADD COLUMN guarantee_ids varchar(256) NULL COMMENT '担保人ids';
ALTER TABLE new_after_lease_check_plan_client ADD COLUMN guarantee_names varchar(512) NULL COMMENT '担保人名称集合';

ALTER TABLE new_after_lease_check_plan_client_lib ADD COLUMN guarantee_ids varchar(256) NULL COMMENT '担保人ids';
ALTER TABLE new_after_lease_check_plan_client_lib ADD COLUMN guarantee_names varchar(512) NULL COMMENT '担保人名称集合';

ALTER TABLE common_process_prepare ADD COLUMN first_check_flag tinyint(2) NULL COMMENT '租后检查待办-是否首次投放';
ALTER TABLE new_after_lease_check_plan_base ADD COLUMN first_check_flag tinyint(2) NULL COMMENT '是否首次投放';
ALTER TABLE new_after_lease_check_plan_base_lib ADD COLUMN first_check_flag tinyint(2) NULL COMMENT '是否首次投放';

ALTER TABLE new_after_lease_check_plan_client ADD COLUMN first_check_flag tinyint(2) NULL COMMENT '是否首次投放';
ALTER TABLE new_after_lease_check_plan_client_lib ADD COLUMN first_check_flag tinyint(2) NULL COMMENT '是否首次投放';