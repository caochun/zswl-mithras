package cn.zswltech.mithras.factory.model.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author zswl
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AreaModelResult {

    /**
     * 一般公共预算收入(亿元)
     */
    private BigDecimal gpBudgetRevenue;

    /**
     * GDP
     */
    private BigDecimal gdp;

    /**
     * GDP：第二产业占比(%)
     */
    private BigDecimal secondaryIndustryRt;

    /**
     * 城镇居民人均可支配收入（单位：元）
     */
    private BigDecimal townPcdi;

    /**
     * 人口同比变化(%)
     */
    private BigDecimal residentPopYoyRatio;

    /**
     * 房屋平均单价(元/㎡) （年末，二手房）
     */
    private BigDecimal houseAvgPriceSc;

    /**
     * 政府性基金收入
     */
    private BigDecimal governmentFundIncome;

    /**
     * 税收收入占比
     */
    private BigDecimal taxIncomeRatio;

    /**
     * 财政平衡性
     */
    private BigDecimal budgetBalance;

    /**
     * 负债率
     */
    private BigDecimal debtRatio;

    /**
     * 债务率
     */
    private BigDecimal debtRate;

    /**
     * 广义城投债务倍数
     */
    private BigDecimal debtLargeRt;

    /**
     * 政府透明度
     */
    private BigDecimal governmentTransparency;

//    /**
//     * 所属地级市评分
//     */
//    private BigDecimal cityScore;


}
