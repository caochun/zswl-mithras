package cn.zswltech.mithras.service.mapper.model.budget;
import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-投放计划-项目周报
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetPlanPayWeeklyReport extends BaseModelWithLogicDelete implements Serializable {

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
    * 预算计划名称
    */
    @TableField("budget_plan_name")
    private String budgetPlanName;

    /**
    * 投放计划id
    */
    @TableField("budget_plan_pay_id")
    private Long budgetPlanPayId;

    /**
    * 周报区间-起
    */
    @TableField("date_from")
    private LocalDate dateFrom;

    /**
    * 周报区间-止
    */
    @TableField("date_to")
    private LocalDate dateTo;

    /**
    * 状态
    */
    @TableField("plan_status")
    private String planStatus;

}
