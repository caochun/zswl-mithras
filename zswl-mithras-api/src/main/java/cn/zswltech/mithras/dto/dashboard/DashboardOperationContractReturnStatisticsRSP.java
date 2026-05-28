package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardOperationContractReturnStatisticsRSP {

    @ApiModelProperty(value = "合同审批通过数量")
    private Integer contractApprovedNumber;

    @ApiModelProperty(value = "存在流程退回数量")
    private Integer contractReturnNumber;

    @ApiModelProperty(value = "退回率 %")
    private BigDecimal returnRate;

    @ApiModelProperty(value = "总退回次数")
    private Integer returnCount;

    @ApiModelProperty(value = "平均退回次数")
    private BigDecimal returnAverage;

}
