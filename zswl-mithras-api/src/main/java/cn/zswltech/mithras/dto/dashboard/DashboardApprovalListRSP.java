package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DashboardApprovalListRSP extends DashboardApprovalBaseRSP {

    //流程总耗时(工作日)
    @ApiModelProperty("流程总耗时(工作日)")
    private BigDecimal approvalTotalTime;

    @ApiModelProperty("运营经办到达时间")
    private LocalDateTime operationHandlingArrivalTime;

    @ApiModelProperty("运营经办提交时间")
    private LocalDateTime operationHandlingSubmitTime;

    @ApiModelProperty("运营经办耗时(工作日)")
    private BigDecimal operationHandlingTotalTime;

    @ApiModelProperty("运营复核到达时间")
    private LocalDateTime operationReviewArrivalTime;

    @ApiModelProperty("运营复核提交时间")
    private LocalDateTime operationReviewSubmitTime;

    @ApiModelProperty("运营复核耗时(工作日)")
    private BigDecimal operationReviewTotalTime;

    @ApiModelProperty("运营部总耗时(工作日)")
    private BigDecimal operationTotalTime;


}
