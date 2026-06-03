package cn.zswltech.mithras.finance.mapper.model.finance;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description 财务管理-项目利润
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("finance_project_profit")
public class FinanceProjectProfit extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 年份
     */
    @TableField(value = "year")
    private Integer year;

    /**
     * 月份
     */
    @TableField(value = "month")
    private Integer month;

    /**
     * 当年累计收入
     */
    @TableField(value = "total_income_this_year")
    private Long totalIncomeThisYear;

    /**
     * 当年累计资金成本
     */
    @TableField(value = "total_cost_this_year")
    private Long totalCostThisYear;

    /**
     * 当年累计风险金
     */
    @TableField(value = "total_risk_this_year")
    private Long totalRiskThisYear;

    /**
     * 当年累计税金及附加
     * @deprecated 拆分成印花税和附加税两个字段
     */
    @Deprecated
    @TableField(value = "total_tax_this_year")
    private Long totalTaxThisYear;

    /**
     * 当年累计利润总额
     */
    @TableField(value = "total_profit_this_year")
    private Long totalProfitThisYear;

    /**
     * 当年累计附加税
     */
    @TableField(value = "total_additional_tax_this_year")
    private Long totalAdditionalTaxThisYear;

    /**
     * 当年累计印花税
     */
    @TableField(value = "total_stamp_tax_this_year")
    private Long totalStampTaxThisYear;

    /**
     * 当月收入
     */
    @TableField(value = "income_this_month")
    private Long incomeThisMonth;

    /**
     * 当月资金成本
     */
    @TableField(value = "cost_this_month")
    private Long costThisMonth;

    /**
     * 当月风险金
     */
    @TableField(value = "risk_this_month")
    private Long riskThisMonth;

    /**
     * 当月利润
     */
    @TableField(value = "profit_this_month")
    private Long profitThisMonth;

    /**
     * 是否已确认
     */
    @TableField(value = "is_confirmed")
    private Integer isConfirmed;

}
