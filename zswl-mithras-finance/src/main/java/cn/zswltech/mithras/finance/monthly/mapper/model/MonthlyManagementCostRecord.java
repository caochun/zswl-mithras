package cn.zswltech.mithras.finance.monthly.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 成本计提-记录表
 * @author yangxiong
 * @TableName monthly_management_cost_record
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value ="monthly_management_cost_record")
public class MonthlyManagementCostRecord extends MonthlyManageBaseModel implements Serializable {

    /**
     * 融资交易的ID
     */
    @TableField(value = "financing_id")
    private Long financingId;

    /**
     * 融资交易的唯一编号
     */
    @TableField(value = "financing_code")
    private String financingCode;

    /**
     * 融资渠道或机构名称
     */
    @TableField(value = "organization_name")
    private String organizationName;

    /**
     * 融资总金额（单位：元）
     */
    @TableField(value = "financing_amount")
    private Long financingAmount;

    /**
     * 融资剩余金额（单位：元）
     */
    @TableField(value = "remaining_amount")
    private Long remainingAmount;

    /**
     * 业务类型
     */
    @TableField(value = "business_type")
    private String businessType;

    /**
     * 融资年利率
     */
    @TableField(value = "financing_rate")
    private Integer financingRate;

    /**
     * 日利率
     */
    @TableField(value = "daily_rate")
    private Integer dailyRate;

    /**
     * 累计计提的资金成本
     */
    @TableField(value = "total_capital_cost")
    private Long totalCapitalCost;

    /**
     * 累计计提的资金成本（税后）
     */
    @TableField(value = "total_capital_cost_after_tax")
    private Long totalCapitalCostAfterTax;

    /**
     * 当期应付利息
     */
    @TableField(value = "term_capital_cost")
    private Long termCapitalCost;

    /**
     * 当期应付利息（税后）
     */
    @TableField(value = "term_capital_cost_after_tax")
    private Long termCapitalCostAfterTax;

    /**
     * 质押资产类型
     */
    @TableField(value = "property_type")
    private String propertyType;

    /**
     * 当日应付利息
     */
    @TableField(value = "financing_cost")
    private Long financingCost;

    /**
     * 当日应付利息钆差金额
     */
    @TableField(value = "financing_cost_diff")
    private Long financingCostDiff;

    /**
     * 起息日
     */
    @TableField(value = "value_date")
    private LocalDate valueDate;

    /**
     * 质押资产类型的展示名称
     */
    @TableField(value = "property_type_display")
    private String propertyTypeDisplay;

    /**
     * 借款性质
     */
    @TableField(value = "loan_property")
    private String loanProperty;

    /**
     * 期初应付利息余额
     */
    @TableField(value = "begin_of_period_interest_balance")
    private Long beginOfPeriodInterestBalance;

    /**
     * 期末应付利息余额
     */
    @TableField(value = "end_of_period_interest_balance")
    private Long endOfPeriodInterestBalance;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}