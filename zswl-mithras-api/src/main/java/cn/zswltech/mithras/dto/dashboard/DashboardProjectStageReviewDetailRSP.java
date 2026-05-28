package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/6/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStageReviewDetailRSP extends DashboardProjectBasicRSP {
    @ApiModelProperty("评审状态code")
    private String projReviewStatusCode;
    @ApiModelProperty("评审状态display")
    private String projReviewStatusDisplay;
    @ApiModelProperty("评审审批状态code")
    private String projReviewProcessStatusCode;
    @ApiModelProperty("评审审批状态display")
    private String projReviewProcessStatusDisplay;
    @ApiModelProperty("尽调报告上传时间")
    private LocalDateTime dueDiligenceReportUploadTime;
}
