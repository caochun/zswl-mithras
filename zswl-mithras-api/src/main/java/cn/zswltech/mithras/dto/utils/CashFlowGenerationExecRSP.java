package cn.zswltech.mithras.dto.utils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class CashFlowGenerationExecRSP {

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
