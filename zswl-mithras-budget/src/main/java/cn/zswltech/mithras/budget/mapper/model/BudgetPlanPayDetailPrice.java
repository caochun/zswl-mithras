package cn.zswltech.mithras.budget.mapper.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-预算计划-投放计划（非月度）-明细-报价方案
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetPlanPayDetailPrice extends BaseModel implements Serializable {

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
    * 项目金额
    */
    @TableField("project_amount")
    private Long projectAmount;

    /**
    * 首期租金率
    */
    @TableField("first_rent_rate")
    private Long firstRentRate;

    /**
    * 租赁期限
    */
    @TableField("term_month")
    private Integer termMonth;

    /**
    * 保证金率
    */
    @TableField("deposit_rate")
    private Integer depositRate;

    /**
    * 还款频率
    */
    @TableField("repay_frequency")
    private String repayFrequency;

    /**
    * 咨询费率
    */
    @TableField("consulting_fee_rate")
    private Integer consultingFeeRate;

    /**
    * 还款期数
    */
    @TableField("repay_times_total")
    private Integer repayTimesTotal;

    /**
    * 手续费率
    */
    @TableField("commission_rate")
    private Integer commissionRate;

    /**
    * 支付方式
    */
    @TableField("pay_type")
    private String payType;

    /**
    * 名义价款
    */
    @TableField("nominal_price")
    private Long nominalPrice;

    /**
    * 利息计算方式
    */
    @TableField("interest_calculate_way")
    private String interestCalculateWay;

    /**
    * 合同利率类型
    */
    @TableField("contract_interest_rate_type")
    private String contractInterestRateType;

    /**
    * 合同利率
    */
    @TableField("contract_interest_rate")
    private Integer contractInterestRate;

    /**
    * 投放日
    */
    @TableField("pay_date")
    private LocalDate payDate;

    /**
    * irr
    */
    @TableField("irr")
    private Integer irr;

    /**
     * xirr
     */
    @TableField("xirr")
    private Double xirr;

    @Override
    public void reset() {
        super.reset();
        this.id = null;
    }

}
