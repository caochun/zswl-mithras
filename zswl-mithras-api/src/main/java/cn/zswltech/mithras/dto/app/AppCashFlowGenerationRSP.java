package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author yibin
 */
@Data
public class AppCashFlowGenerationRSP {

    @ApiModelProperty("租金总额(元)")
    private Long totalRent;

    @ApiModelProperty("利息总额(元)")
    private Long totalInterest;

    @ApiModelProperty("irr值；直接返回字符串显示")
    private String irr;

    @ApiModelProperty("现金流详情")
    private List<CashFlowBasicInfoRSP> basicInfoRSPList;

    @Data
    public static class CashFlowBasicInfoRSP {
        @ApiModelProperty("日期")
        private LocalDate cashFlowDate;
        @ApiModelProperty("期项")
        private Integer cashFlowPhase;
        @ApiModelProperty("租金")
        private Long rent;
        @ApiModelProperty("本金")
        private Long principal;
        @ApiModelProperty("利息")
        private Long interest;
        @ApiModelProperty("剩余本金")
        private Long remainingPrincipal;
        @ApiModelProperty("现金流金额")
        private Long cashFlowAmount;
    }
}
