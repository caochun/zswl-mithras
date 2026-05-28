package cn.zswltech.mithras.dto.utils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author yibin
 */
@Data
public class CashFlowGenerationIrrREQ {
    @ApiModelProperty("租赁期限/额度有效期")
    private Integer monthCount;

    /**
     * 还款频率
     */
    @NotBlank
    @ApiModelProperty("还款频率")
    private String repayRate;

    @Valid
    @NotEmpty
    @ApiModelProperty("现金流计划")
    private List<CashFlowGenerationIrrItem> itemList;

    @Data
    public static class CashFlowGenerationIrrItem {
        @NotNull
        @ApiModelProperty("日期")
        private LocalDate cashFlowDate;
        @NotNull
        @ApiModelProperty("期项")
        private Integer cashFlowPhase;
        @ApiModelProperty("租金")
        private Long rent;
        @ApiModelProperty("本金")
        private Long principal;
        @ApiModelProperty("利息")
        private Long interest;
        @NotNull
        @ApiModelProperty("剩余本金")
        private Long remainingPrincipal;
        @NotNull
        @ApiModelProperty("现金流金额")
        private Long cashFlowAmount;
    }

}
