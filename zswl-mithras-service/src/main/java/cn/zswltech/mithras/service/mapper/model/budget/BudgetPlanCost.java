package cn.zswltech.mithras.service.mapper.model.budget;
import cn.zswltech.mithras.service.enums.budget.BudgetPlanCalculateStatusEnum;
import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-预算计划-成本预算
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetPlanCost extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

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
    * 计划名称
    */
    @TableField("budget_plan_name")
    private String budgetPlanName;

    /**
    * 预算计划开始日期
    */
    @TableField("budget_date_from")
    private LocalDate budgetDateFrom;

    /**
    * 预算计划结束日期
    */
    @TableField("budget_date_to")
    private LocalDate budgetDateTo;

    /**
    * 计划填报开始日期
    */
    @TableField("write_date_from")
    private LocalDate writeDateFrom;

    /**
    * 计划填报结束日期
    */
    @TableField("write_date_to")
    private LocalDate writeDateTo;

    /**
    * 预算类型
    */
    @TableField("budget_type")
    private String budgetType;

    /**
    * 状态 budgetStatusEnum
    */
    @TableField("budget_status")
    private String budgetStatus;

    /**
    * 收集截止日期
    */
    @TableField("collect_date_to")
    private LocalDate collectDateTo;

    /**
     * 成本计算状态 {@link BudgetPlanCalculateStatusEnum#name()}
     */
    @TableField("calculate_status")
    private String calculateStatus;

}
