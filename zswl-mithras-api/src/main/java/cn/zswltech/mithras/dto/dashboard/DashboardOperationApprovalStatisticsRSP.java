package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardOperationApprovalStatisticsRSP {

    @ApiModelProperty(value = "合同审批通过数量")
    private Integer contractApprovedNumber;

    @ApiModelProperty(value = "运营经办平均时效（工作日）")
    private BigDecimal operationHandAverage;

    @ApiModelProperty(value = "运营复核平均时效（工作日）")
    private BigDecimal operationReviewAverage;

    @ApiModelProperty(value = "运营部平均时效（工作日）")
    private BigDecimal operationAverage;

    @ApiModelProperty(value = "全流程平均时效（工作日）")
    private BigDecimal processAverage;

}
