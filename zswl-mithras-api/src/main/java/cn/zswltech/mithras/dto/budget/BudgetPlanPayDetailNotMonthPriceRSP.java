package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@Data
public class BudgetPlanPayDetailNotMonthPriceRSP {
    @ApiModelProperty("项目金额")
    private Long projectAmount;

    @ApiModelProperty("首期租金率")
    private Long firstRentRate;

    @ApiModelProperty("租赁期限")
    private Integer termMonth;

    @ApiModelProperty("保证金率")
    private Integer depositRate;

    @ApiModelProperty("还款频率")
    private String repayFrequency;

    @ApiModelProperty("咨询费率")
    private Integer consultingFeeRate;

    @ApiModelProperty("还款期数")
    private Integer repayTimesTotal;

    @ApiModelProperty("手续费率")
    private Integer commissionRate;

    @ApiModelProperty("支付方式")
    private String payType;

    @ApiModelProperty("名义价款")
    private Long nominalPrice;

    @ApiModelProperty("利息计算方式")
    private String interestCalculateWay;

    @ApiModelProperty("合同利率类型")
    private String contractInterestRateType;

    @ApiModelProperty("合同利率")
    private Integer contractInterestRate;

    @ApiModelProperty("投放日")
    private LocalDate payDate;

    @ApiModelProperty("irr")
    private Integer irr;
}
