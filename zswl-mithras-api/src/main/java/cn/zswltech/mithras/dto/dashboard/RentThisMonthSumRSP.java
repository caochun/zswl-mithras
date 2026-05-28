package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lizhao
 * @date 2024/7/02
 * @description
 */
@Data
public class RentThisMonthSumRSP {
    @ApiModelProperty("本月应收列表")
    private List<DashboardProjectInfoRentThisMonthRSP> list;

    @ApiModelProperty("合计")
    private RentThisMonthSumRSP.SumData sumData;

    @Data
    public static class SumData{
        @ApiModelProperty("已收租金金额合计")
        private BigDecimal collectionAmount;
        @ApiModelProperty("剩余租金金额合计")
        private BigDecimal remainingAmount;
        @ApiModelProperty("保证金金额合计")
        private BigDecimal earnestBalanceAmount;
        @ApiModelProperty("已收本金合计")
        private BigDecimal collectionPrincipalAmount;
        @ApiModelProperty("已收利息合计")
        private BigDecimal collectionInterestAmount;
        @ApiModelProperty("剩余本金合计")
        private BigDecimal principalBalanceAmount;
        @ApiModelProperty("剩余利息合计")
        private BigDecimal interestBalanceAmount;

        @ApiModelProperty("逾期金额")
        private BigDecimal overdueAmount;
        @ApiModelProperty("罚息金额")
        private BigDecimal interestPenaltyAmount;
        @ApiModelProperty("罚息减免金额")
        private BigDecimal interestPenaltyReduceAmount;

        @ApiModelProperty("投放金额")
        private BigDecimal principalAmount;
        @ApiModelProperty("剩余租金金额")
        private BigDecimal balanceAmount;
        @ApiModelProperty("本期本金")
        private BigDecimal currentPrincipal;
        @ApiModelProperty("本期利息")
        private BigDecimal interestAmount;

    }
}
