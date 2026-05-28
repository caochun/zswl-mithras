package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
public class DashboardAfterLeaseCheckRSP {
    @ApiModelProperty("唯一key,checkPlanId_clientId")
    private Long idKey;
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("检查计划ID")
    private Long checkPlanId;
    @ApiModelProperty("检查计划名称")
    private String checkPlanName;
    @ApiModelProperty("检查计划类型Code")
    private String planType;
    @ApiModelProperty("检查计划类型描述")
    private String checkPlanTypeDisplay;
    @ApiModelProperty("检查方式code")
    private String checkWayCode;
    @ApiModelProperty("检查方式display")
    private String checkWayDisplay;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("业务部门名称")
    private String bizDeptName;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty("项目主办名称")
    private String projSponsorUserName;
    @ApiModelProperty("本次租后检查截止时间")
    private LocalDate checkDate;
    @ApiModelProperty("检查报告审批状态code ")
    private String reportProcessStatusCode;
    @ApiModelProperty("检查报告审批状态display")
    private String reportProcessStatusDisplay;
    @ApiModelProperty("检查计划状态code DashboardAfterLeaseCheckStatueEnum")
    private String checkPlanStatus;
    @ApiModelProperty("检查计划状态描述")
    private String checkPlanStatusDisplay;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
