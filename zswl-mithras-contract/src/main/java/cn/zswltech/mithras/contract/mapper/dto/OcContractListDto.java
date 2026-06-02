package cn.zswltech.mithras.contract.mapper.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/24 10:15
 */
@Data
@ApiModel("逾期催收详情-合同信息返回")
public class OcContractListDto {
    @ApiModelProperty("合同id")
    private Long id;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "业务类型")
    private String bizType;

    @ApiModelProperty(value = "合同状态")
    private String contractStatus;

    @ApiModelProperty(value = "合同金额")
    private Long contractAmount;

    @ApiModelProperty(value = "当前逾期天数")
    private Integer overdueDays;

    @ApiModelProperty(value = "逾期金额")
    private Long overdueRent;

    @ApiModelProperty(value = "逾期罚息")
    private Long lateCharge;

    @ApiModelProperty(value = "剩余本金")
    private Long remainPrincipal;

    @ApiModelProperty(value = "剩余保证金")
    private Long remainDeposit;

    @ApiModelProperty(value = "风险敞口")
    private Long riskExposure;

    @ApiModelProperty(value = "项目主办")
    private String projSponsorName;

    @ApiModelProperty(value = "业务部门")
    private String bizDeptName;

}
