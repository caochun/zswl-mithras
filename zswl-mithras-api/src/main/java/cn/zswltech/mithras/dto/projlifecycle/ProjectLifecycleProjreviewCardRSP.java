package cn.zswltech.mithras.dto.projlifecycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-10-24
 **/

@Data
public class ProjectLifecycleProjreviewCardRSP {

    @ApiModelProperty("项目评审卡片")
    private CardRSP projreview;

    @ApiModelProperty("项目定价卡片")
    private CardRSP projReviewPricing;

    @Data
    public static class CardRSP {
        @ApiModelProperty("项目评审id")
        private Long projreviewId;

        @ApiModelProperty("审批状态")
        private String processStatus;

        @ApiModelProperty("创建时间")
        private LocalDate createTime;

        @ApiModelProperty("审批通过时间")
        private LocalDate approveTime;

        @ApiModelProperty("流程类型")
        private String processType;

        @ApiModelProperty("当前节点")
        private String currentNode;

        @ApiModelProperty("申请授信金额")
        private Long applyCreditAmount;

        @ApiModelProperty("是否有条件同意")
        private Boolean conditional;

        //
        @ApiModelProperty("利率类型")
        private String rateType;
        @ApiModelProperty("固定利率")
        private Integer ratePercent;
        @ApiModelProperty("租赁期限")
        private Integer leaseMonthCount;
        @ApiModelProperty("计划起租")
        private LocalDate planStart;
        @ApiModelProperty("计划截止")
        private LocalDate planEnd;
    }
}
