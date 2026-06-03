package cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model;
import cn.zswltech.mithras.budget.domain.enums.BudgetExamineBudgetExecuteEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 预算管理-预算考核-预算执行情况表
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetExamineBudgetExecute extends BaseModelWithLogicDelete implements Serializable {

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
    * 字段名称 {@link BudgetExamineBudgetExecuteEnum#name()}
    */
    @TableField("field_name")
    private String fieldName;

    /**
    * 本月数
    */
    @TableField("current_month")
    private Long currentMonth;

    /**
     * 本年累计
     */
    @TableField("total_year")
    private Long totalYear;

    /**
     * 上年同期
     */
    @TableField("last_year_period")
    private Long lastYearPeriod;

    /**
     * 同比
     */
    @TableField("on_year")
    private Long onYear;

    /**
    * 全年预算目标
    */
    @TableField("annual_budget_target")
    private Long annualBudgetTarget;

    /**
     * 进度预算目标
     */
    @TableField("progress_budget_target")
    private Long progressBudgetTarget;

    /**
     * 进度预算完成率
     */
    @TableField("progress_budget_completion_rate")
    private Long progressBudgetCompletionRate;

    /**
     * 全年预算完成率
     */
    @TableField("annual_budget_completion_rate")
    private Long annualBudgetCompletionRate;

}
