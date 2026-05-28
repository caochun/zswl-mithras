package cn.zswltech.mithras.dto.workbench;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-10-21
 **/

@Data
public class NewReviewListRSP {
    @ApiModelProperty("项目评审id")
    private Long projectId;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("客户Id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户类型")
    private String clientType;

    @ApiModelProperty("境内or境外")
    private String domesticOrAbroad;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty("项目主办名")
    private String projSponsorUserName;

    @ApiModelProperty("业务部门")
    private String bizDeptName;

    @ApiModelProperty("申请授信金额")
    private Long applyCreditAmount;

    @ApiModelProperty("存量风险敞口")
    private Long stockRiskExposure;
}
