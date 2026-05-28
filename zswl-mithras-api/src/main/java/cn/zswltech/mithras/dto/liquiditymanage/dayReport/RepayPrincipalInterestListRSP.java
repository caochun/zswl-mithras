package cn.zswltech.mithras.dto.liquiditymanage.dayReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bigbear
 * @date 2024/12/13 16:44
 * @className RepayPrincipalInterestListRSP
 * @description
 */
@Data
@ApiModel(value = "还款本金利息列表响应参数")
public class RepayPrincipalInterestListRSP {

    @ApiModelProperty(value = "融资机构名称")
    private List<String> financingOrgName;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "融资id")
    private Long financingId;

    @ApiModelProperty(value = "融资金额")
    private String financingAmount;

    @ApiModelProperty(value = "本期到期日")
    private String expireDate;

    @ApiModelProperty(value = "本期应还金额")
    private String shouldPayAmount;

    @ApiModelProperty(value = "应还本金")
    private String shouldPayPrincipal;

    @ApiModelProperty(value = "应还利息")
    private String shouldPayInterest;
}
