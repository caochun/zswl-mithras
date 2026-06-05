-- 资产减值应用在预算
alter table budget_plan_profit_detail add column end_of_last_period_risk_fund_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '上年末/上月末风险准备金余额（理想值）' after end_of_last_period_risk_fund;
alter table budget_plan_profit_detail add column end_of_this_period_risk_fund_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '本年末/本月末风险准备金余额（理想值）' after end_of_this_period_risk_fund;
alter table budget_plan_profit_detail add column risk_fund_diff_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '累计风险准备金计提/转回（理想值）' after risk_fund_diff;
alter table budget_plan_profit_detail add column assessment_profit_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '考核利润（理想值）' after assessment_profit;
alter table budget_plan_profit_detail add column assessment_profit_without_expense_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '考核利润（扣费后）（理想值）' after assessment_profit_without_expense;
alter table budget_plan_profit_detail add column assessment_profit_original_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '考核利润（实际值）（理想值）' after assessment_profit_original;
alter table budget_plan_profit_detail add column assessment_profit_without_expense_original_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '考核利润（扣费后）（实际值）（理想值）' after assessment_profit_without_expense_original;
alter table budget_plan_profit_detail add column expense_ideal bigint(20) NOT NULL DEFAULT 0 COMMENT '费用（理想值）' after expense;

update
    budget_plan_profit_detail
set
    end_of_last_period_risk_fund_ideal = end_of_last_period_risk_fund,
    end_of_this_period_risk_fund_ideal = end_of_this_period_risk_fund,
    risk_fund_diff_ideal = risk_fund_diff,
    assessment_profit_ideal = assessment_profit,
    assessment_profit_without_expense_ideal = assessment_profit_without_expense,
    assessment_profit_original_ideal = assessment_profit_original,
    assessment_profit_original_ideal = assessment_profit_original,
    assessment_profit_without_expense_original_ideal = assessment_profit_without_expense_original,
    expense_ideal = expense;
