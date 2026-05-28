package cn.zswltech.mithras.service.mapper.model.budget;

import cn.zswltech.mithras.service.enums.budget.BudgetPlanDataCategoryEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/6/25
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("budget_plan_cost_detail_fund")
public class BudgetPlanCostDetailFund extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 预算计划id
     */
    @TableField("budget_plan_id")
    private Long budgetPlanId;

    /**
     * 成本预算id
     */
    @TableField("budget_plan_cost_id")
    private Long budgetPlanCostId;

    /**
     * 数据类别 {@link BudgetPlanDataCategoryEnum#name()}
     */
    @TableField("data_category")
    private String dataCategory;

    /**
     * 年份
     */
    @TableField("year")
    private Integer year;

    /**
     * 月份
     */
    @TableField("month")
    private Integer month;

    /**
     * 融资类别
     */
    @TableField("financing_type")
    private String financingType;

    /**
     * 融资id
     */
    @TableField("financing_id")
    private Long financingId;

    /**
     * 归还金额
     */
    @TableField("repay_amount")
    private Long repayAmount;

    /**
     * 归还本金
     */
    @TableField("principal")
    private Long principal;

    /**
     * 归还利息
     */
    @TableField("interest")
    private Long interest;
}
