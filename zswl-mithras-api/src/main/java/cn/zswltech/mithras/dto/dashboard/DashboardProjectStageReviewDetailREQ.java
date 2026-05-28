package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@Data
public class DashboardProjectStageReviewDetailREQ {
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("评审状态")
    private String projReviewStatusCode;
    @ApiModelProperty("评审审批状态")
    private String projReviewProcessStatusCode;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty("可见范围 workbenchProjectDataRange")
    @NotNull
    private String permissionType;
}
