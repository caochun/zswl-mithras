package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
public class DashboardProjectStageEstablishDetailREQ {
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("立项状态")
    private String projEstablishStatusCode;
    @ApiModelProperty("立项审批状态")
    private String projEstablishProcessStatusCode;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty("可见范围 workbenchProjectDataRange")
    @NotNull
    private String permissionType;
}
