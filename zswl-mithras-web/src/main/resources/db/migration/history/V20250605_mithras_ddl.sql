-- 还本付息批量还款计划
alter table fund_receipt_repay_plan add column repay_year int(11) default null comment '还款年份' after batch_id;
alter table fund_receipt_repay_plan add column repay_month int(11) default null comment '还款月份' after repay_year;