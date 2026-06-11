package cn.zswltech.mithras.budget.mapper.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-预算计划-投放计划（非月度）-明细-现金流计划
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetPlanPayDetailCashFlow extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 逻辑删除，0-未删除
    */
    @TableField("deleted")
    private Integer deleted;

    /**
    * 预算计划id
    */
    @TableField("budget_plan_id")
    private Long budgetPlanId;

    /**
    * 投放计划id
    */
    @TableField("budget_plan_pay_id")
    private Long budgetPlanPayId;

    /**
    * 投放计划-明细id
    */
    @TableField("budget_plan_pay_detail_id")
    private Long budgetPlanPayDetailId;

    /**
    * 日期
    */
    @TableField("cash_flow_date")
    private LocalDate cashFlowDate;

    /**
    * 期项
    */
    @TableField("cash_flow_phase")
    private Integer cashFlowPhase;

    /**
    * 现金流金额
    */
    @TableField("cash_flow_amount")
    private Long cashFlowAmount;

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
    * 剩余本金
    */
    @TableField("remaining_principal")
    private Long remainingPrincipal;

    @Override
    public void reset() {
        super.reset();
        this.id = null;
    }

}
