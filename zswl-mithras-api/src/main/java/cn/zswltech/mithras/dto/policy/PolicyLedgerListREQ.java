package cn.zswltech.mithras.dto.policy;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("保单台账-列表-请求体")
public class PolicyLedgerListREQ extends PageReq {

    @ApiModelProperty(value = "保险公司名称")
    private String insuranceCompany;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty(value = "保单编号")
    private String policyCode;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "保险起始日")
    private LocalDate insuranceStartDateFrom;

    @ApiModelProperty(value = "保险起始日")
    private LocalDate insuranceStartDateTo;

    @ApiModelProperty(value = "保险到期日")
    private LocalDate insuranceEndDateFrom;

    @ApiModelProperty(value = "保险到期日")
    private LocalDate insuranceEndDateTo;

    @ApiModelProperty(value = "是否续保 PolicyRenewInsuranceEnum")
    private String renewInsuranceFlag;

    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty("项目协办")
    private Long projCosponsorUserId;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    /**
     * PolicyStatusEnum
     **/
    @ApiModelProperty("保单状态")
    private String policyStatus;

    @ApiModelProperty("15日内到期保单")
    private Boolean expires;

}
