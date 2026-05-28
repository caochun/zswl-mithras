-- 预算管理-预算计划-投放计划-明细
alter table budget_plan_pay_detail add column project_classify varchar(50)  null comment '项目分类，可选项：鼓励类，适度支持类，谨慎类，工程机械类（厂商担保模式），集团内协同业务';
alter table budget_plan_pay_detail add column a_rate  varchar(20)  null comment '是否3A评级:是、否';
alter table budget_plan_pay_detail add column manage_level  varchar(50)  null comment '管理层级:市级、区县级、镇级';
alter table budget_plan_pay_detail add column fund_plan_pay_amount  bigint  default 0  not null comment '资金拟投放金额';
