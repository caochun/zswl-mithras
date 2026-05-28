package cn.zswltech.mithras.dto.dashboard;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yangxiong
 * @date 2024/6/19/09:36
 * @description
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DashboardClientAfterLeaseCheckREQ extends PageReq {

    @ApiModelProperty(value = "客户id")
    private String clientId;

    @ApiModelProperty("检查计划名称")
    private String checkPlanName;

    @ApiModelProperty("检查计划类型Code")
    private String planType;

    @ApiModelProperty("检查方式code")
    private String checkWayCode;

    @ApiModelProperty("本次租后检查截止时间开始")
    private String checkDateFrom;

    @ApiModelProperty("本次租后检查截止时间结束")
    private String checkDateTo;

    @ApiModelProperty("检查计划状态code")
    private String checkPlanStatus;

    @ApiModelProperty("检查报告审批状态code")
    private String reportProcessStatusCode;

    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
}
