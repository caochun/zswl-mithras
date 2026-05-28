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
public class OverDueSumRSP {
    @ApiModelProperty("逾期列表")
    private List<DashboardProjectInfoOverdueRSP> list;

    @ApiModelProperty("合计")
    private OverDueSumRSP.SumData sumData;

    @Data
    public static class SumData{

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
