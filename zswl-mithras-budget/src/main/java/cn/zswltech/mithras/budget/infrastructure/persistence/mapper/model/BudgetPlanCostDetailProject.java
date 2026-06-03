package cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model;

import cn.zswltech.mithras.budget.domain.enums.BudgetPlanDataCategoryEnum;
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
@TableName("budget_plan_cost_detail_project")
public class BudgetPlanCostDetailProject extends BaseModelWithLogicDelete {
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
     * 投放计划详情id
     */
    @TableField("budget_plan_pay_detail_id")
    private Long budgetPlanPayDetailId;

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
     * 合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 租金
     */
    @TableField("rent")
    private Long rent;

    /**
     * 本金
     */
    @TableField("principal")
    private Long principal;

    /**
     * 利息
     */
    @TableField("interest")
    private Long interest;

    /**
     * 保证金
     */
    @TableField("deposit")
    private Long deposit;

    /**
     * 服务费/咨询费
     */
    @TableField("consulting_fee")
    private Long consultingFee;

    /**
     * 投放
     */
    @TableField("pay_amount")
    private Long payAmount;
}
