package cn.zswltech.mithras.dto.managereport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/12/13
 * @description
 */
@Data
public class YunYingDaiBanDetailRSP {
    @ApiModelProperty("流程实例ID")
    private String processInstanceId;
    @ApiModelProperty("流程模型")
    private String processModelType;
    @ApiModelProperty("业务类型")
    private String leaseTypesDisplay;
    @ApiModelProperty("项目编号")
    private String projCode;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("业务部门名称")
    private String bizDeptName;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty("项目主办名称")
    private String projSponsorUserName;
    @ApiModelProperty("流程状态")
    private Integer processStatus;
    @ApiModelProperty("流程状态-展示")
    private String processStatusDisplay;
    @ApiModelProperty("当前审批人ID")
    private String currentAssignerId;
    @ApiModelProperty("当前审批人名称")
    private String currentAssignerName;
    @ApiModelProperty("退回意见")
    private String backRemark;
}
