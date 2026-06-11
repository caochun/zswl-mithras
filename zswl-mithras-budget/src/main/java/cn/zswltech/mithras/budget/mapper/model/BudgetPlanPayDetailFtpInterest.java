package cn.zswltech.mithras.budget.mapper.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-预算计划-投放计划（非月度）-明细-资金成本（ftp计息）
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetPlanPayDetailFtpInterest extends BaseModel implements Serializable {

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
    * 计息日期
    */
    @TableField("interest_date")
    private LocalDate interestDate;

    /**
    * 计息年份
    */
    @TableField("interest_year")
    private Integer interestYear;

    /**
    * 计息月份
    */
    @TableField("interest_month")
    private Integer interestMonth;

    /**
    * 现金支出
    */
    @TableField("cash_out")
    private Long cashOut;

    /**
    * 现金收入
    */
    @TableField("cash_in")
    private Long cashIn;

    /**
    * 资金占用
    */
    @TableField("cash_occupy")
    private Long cashOccupy;

    /**
    * 现金ftp
    */
    @TableField("cash_ftp")
    private Integer cashFtp;

    /**
    * 资金计息
    */
    @TableField("cash_interest")
    private Long cashInterest;

    /**
    * 备注说明
    */
    @TableField("remark")
    private String remark;

    @Override
    public void reset() {
        super.reset();
        this.id = null;
    }

}
