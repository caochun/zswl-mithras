package cn.zswltech.mithras.budget.mapper.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-预算计划-投放计划（非月度）-明细-收入分摊
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetPlanPayDetailIncomeSharing extends BaseModel implements Serializable {

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
    @TableField("income_date")
    private LocalDate incomeDate;

    /**
    * 年份
    */
    @TableField("income_year")
    private Integer incomeYear;

    /**
    * 月份
    */
    @TableField("income_month")
    private Integer incomeMonth;

    /**
    * 期次
    */
    @TableField("income_phase")
    private Integer incomePhase;

    /**
    * 长期应收款期初余额
    */
    @TableField("begin_of_term_balance")
    private Long beginOfTermBalance;

    /**
    * 当天应收租金
    */
    @TableField("rent")
    private Long rent;

    /**
    * 当天确认收入
    */
    @TableField("income")
    private Long income;

    /**
    * 长期应收款余额
    */
    @TableField("end_of_term_balance")
    private Long endOfTermBalance;

    /**
    * 日折现率
    */
    @TableField("daily_discount_rate")
    private String dailyDiscountRate;

    @Override
    public void reset() {
        super.reset();
        this.id = null;
    }

}
