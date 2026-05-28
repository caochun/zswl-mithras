package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingReportApprovalRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "审批详情")
    private RatingApprovalRSP ratingApprovalRSP;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RatingApprovalRSP {
        @ApiModelProperty(value = "指标名称")
        private String fieldName;

        @ApiModelProperty(value = "审批状态")
        private Boolean approvalStatus;

        @ApiModelProperty(value = "审批意见")
        private String approvalOpinion;
    }

}
