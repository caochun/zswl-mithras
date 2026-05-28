-- 新增逾期天数和提交审批时间字段
alter table new_after_lease_check_plan_client add column overdue_days int null comment '逾期天数';
alter table new_after_lease_check_plan_client add column commit_time datetime null comment '提交审批时间';

alter table new_after_lease_check_plan_client_lib add column overdue_days int null comment '逾期天数';
alter table new_after_lease_check_plan_client_lib add column commit_time datetime null comment '提交审批时间';
