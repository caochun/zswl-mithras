package cn.zswltech.mithras.budget.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author vico
 * @description 预算管理-预算考核-投放计划执行情况表
 * @date 2025-04-11
 */
@Data
public class BudgetExaminePayPlanExecute extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 预算考核id
     */
    @TableField("budget_examine_id")
    private Long budgetExamineId;

    /**
     * 业务部门id
     */
    @TableField("belong_dept_id")
    private Long belongDeptId;

    /**
     * 资金计划偏离度
     */
    @TableField("deviation_degree_plan")
    private Integer deviationDegreePlan;

    /**
     * 项目准确度
     */
    @TableField("project_accuracy")
    private Integer projectAccuracy;

    /**
     * 未及时提报次数-资金计划
     */
    @TableField("failed_report_funding_plan")
    private Integer failedReportFundingPlan;

    /**
     * 未及时提报次数-周报
     */
    @TableField("failed_report_week")
    private Integer failedReportWeek;


    /**
     * 延迟天数-资金计划
     */
    @TableField("delay_days_funding_plan")
    private Integer delayDaysFundingPlan;

    /**
     * 延迟天数-周报
     */
    @TableField("delay_days_week")
    private Integer delayDaysWeek;


}
