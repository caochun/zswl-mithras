package cn.zswltech.mithras.dto.projlifecycle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/23
 * @description
 */
@Data
@ApiModel("项目全周期-租后检查卡片-返回体")
public class ProjectLifecycleAfterLeaseCheckCardRSP {
    @ApiModelProperty("检查计划列表")
    private List<CheckPlanInfo> checkPlanList;

    @ApiModelProperty("外部查询列表")
    private List<ExternalQueryInfo> externalQueryList;

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CheckPlanInfo extends AbstractProcessInfoRSP {
        @ApiModelProperty("检查计划id")
        private Long checkPlanId;

        @ApiModelProperty("检查计划中的客户记录id")
        private Long checkPlanClientId;

        @ApiModelProperty("检查计划名称")

        private String planName;

        @ApiModelProperty("检查时间")
        private String planTime;

        @ApiModelProperty("检查计划状态")
        private String planStatus;

        @ApiModelProperty("检查项目审批状态")
        private String checkProjectApprovalStatus;

        @ApiModelProperty("是否可跳转详情页")
        private Boolean canJump;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ExternalQueryInfo extends AbstractProcessInfoRSP {
        @ApiModelProperty("外部查询记录id")
        private Long queryId;

        @ApiModelProperty("查询月份")
        private String queryMonth;
    }
}
