package cn.zswltech.mithras.afterlease.mapper.model.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/6/19/09:36
 * @description
 */
@Data
public class DashboardClientAfterLeaseCheckQuery {
    private List<Long> authBizDeptIds;
    private Long authCurrentUserId;
    private Integer start = 0;
    private Integer limit = 20;

    public void fillLimitQuery(int page, int pageSize) {
        start = (page - 1) * pageSize;
        limit = pageSize;
    }


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

    @ApiModelProperty("审批状态")
    private String approvalStatus;
}
