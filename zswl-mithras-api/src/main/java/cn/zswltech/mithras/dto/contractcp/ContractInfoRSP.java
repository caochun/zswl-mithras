package cn.zswltech.mithras.dto.contractcp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-16
 **/
@Data
@ApiModel("合同信息-返回体")
public class ContractInfoRSP {

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目主办")
    private String projSponsorUser;

    @ApiModelProperty("业务部门")
    private String bizDept;

    @ApiModelProperty("合同状态")
    private String contractStatus;

    @ApiModelProperty("已收本金")
    private Long receivedPrincipal;

    @ApiModelProperty("已收利息")
    private Long receivedInterest;

    @ApiModelProperty("剩余本金")
    private Long lastPrincipal;

    @ApiModelProperty("剩余利息")
    private Long lastInterest;

    @ApiModelProperty("名义贷价")
    private Long nominalLoanPrice;

    @ApiModelProperty("逾期未还金额")
    private Long overdueAmount;

    @ApiModelProperty("逾期利息")
    private Long overdueInterest;

    @ApiModelProperty("已收逾期利息")
    private Long receivedOverdueInterest;

    @ApiModelProperty("未收逾期利息")
    private Long uncollectedOverdueInterest;

    @ApiModelProperty("剩余保证金")
    private Long lastMargin;

    @ApiModelProperty("已收资费")
    private Long otherSum;
}
