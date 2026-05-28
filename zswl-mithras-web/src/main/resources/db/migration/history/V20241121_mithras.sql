-- 征信库的变更，不需要直接执行，走yearing
ALTER TABLE cr_repay_plan_proc_snap ADD is_show tinyint NULL COMMENT '是否展示';

alter table kpi_proj_guess_base_info add column `profit_adjust` bigint(20)  DEFAULT NULL COMMENT '利润-调整值';
