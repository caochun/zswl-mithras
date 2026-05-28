package cn.zswltech.mithras.dto.liquidityrisk;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2023-05-15
 **/

@Data
public class CapitalFlowSettingRsp {

    @ApiModelProperty("期初现金流余额")
    private Long beginCashflowAmount;

    @ApiModelProperty("其他收入")
    private Long otherIncome;

    @ApiModelProperty("其他支出")
    private Long otherExpenses;

    @ApiModelProperty("近【】天租金/利息回笼")
    private Long rentInterestReturn;

    @ApiModelProperty("保证金/手续费等收入")
    private Long earnestMoneyRevenue;

    @ApiModelProperty("融资总额")
    private Long financingSum;

    @ApiModelProperty("近【】天归还融资本金")
    private Long returnFinancingPrincipal;

    @ApiModelProperty("归还融资利息")
    private Long returnFinancingInterest;

    @ApiModelProperty("项目保证金")
    private Long projEarnestMoney;

    @ApiModelProperty("项目投放总额")
    private Long projOutSum;

    @ApiModelProperty("时间区间-从")
    private LocalDate timeFrom;

    @ApiModelProperty("时间区间-到")
    private LocalDate timeTo;

    @ApiModelProperty("总计资金流入量")
    private Long sumAmountIn;

    @ApiModelProperty("总计资金流出量")
    private Long sumAmountOut;

    @ApiModelProperty("总资金盈缺")
    private Long totalFundingSurplus;

    @ApiModelProperty("期间现金余额")
    private Long periodCashBalance;

    @ApiModelProperty("融资明细")
    private List<Detail> inDetail;

    @ApiModelProperty("项目投放明细")
    private List<Detail> outDetail;

    @Data
    public static class Detail{
        @ApiModelProperty("金额")
        private Long amount;

        @ApiModelProperty("日期")
        private LocalDate date;

        @ApiModelProperty("备注")
        private String remark;
    }

}
