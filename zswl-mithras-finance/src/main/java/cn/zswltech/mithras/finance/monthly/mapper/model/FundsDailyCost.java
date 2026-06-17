package cn.zswltech.mithras.finance.monthly.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2023/5/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("funds_daily_cost")
public class                                                                                                                                                                                                                                                                                                                    FundsDailyCost extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 主表id
     */
    @TableField("main_id")
    private Long mainId;

    /**
     * 融资id
     */
    @TableField("financing_id")
    private Long financingId;


    /**
     * 直融保存产品信息
     */
    @TableField("financing_product_id")
    private Long financingProductId;

    /**
     * 证券简称
     */
    @TableField("abbreviation")
    private String abbreviation;

    /**
     * 展示文本（通常是日期，初始化会使用“期初余额”替代日期）
     */
    @TableField("item_text")
    private String itemText;

    /**
     * 计息日期
     */
    @TableField("interest_date")
    private LocalDate interestDate;


    /**
     * 融资金额
     */
    @TableField("financing_amount")
    private Long financingAmount;

    /**
     * 还款本金
     */
    @TableField("principle_amount")
    private Long principleAmount;

    /**
     * 还款利息
     */
    @TableField("interest_amount")
    private Long interestAmount;

    /**
     * 融资利率
     */
    @TableField("financing_rate")
    private Integer financingRate;

    /**
     * 日利率
     */
    @TableField("daily_rate")
    private Integer dailyRate;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal taxRate;

    /**
     * 当日应付利息
     */
    @TableField("financing_cost")
    private Long financingCost;

    /**
     * 钆差金额
     */
    @TableField("financing_cost_diff")
    private Long financingCostDiff;

    /**
     * 当日应付利息（税后）
     */
    @TableField("financing_cost_after_tax")
    private Long financingCostAfterTax;

    /**
     * 累计计提资金成本
     */
    @TableField("total_capital_cost")
    private Long totalCapitalCost;

    /**
     * 累计计提资金成本税后
     */
    @TableField("total_capital_cost_after_tax")
    private Long totalCapitalCostAfterTax;

    /**
     * 融资类型
     */
    @TableField("type")
    private String type;

    /**
     * 质押资产类型
     */
    @TableField("property_type")
    private String propertyType;

    /**
     * 收入是否确认
     */
    @TableField("is_confirmed")
    private Integer isConfirmed;

    @TableField("confirm_time")
    private LocalDateTime confirmTime;

    @TableField("confirm_batch")
    private String confirmBatch;

    /**
     * 期初应付利息余额
     */
    @TableField("begin_of_period_interest_balance")
    private Long beginOfPeriodInterestBalance;

    /**
     * 期末应付利息余额
     */
    @TableField("end_of_period_interest_balance")
    private Long endOfPeriodInterestBalance;
}
