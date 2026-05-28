package cn.zswltech.mithras.dto.contractcp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-15
 **/
@Data
@ApiModel("合同收付款列表-返回体")
public class ContractCollectionPaymentListRSP {

    @ApiModelProperty("合同id")
    private Long id;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("合同code")
    private String contractCode;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("合同状态")
    private String contractStatus;

    @ApiModelProperty("类别")
    private String bizType;

    @ApiModelProperty("项目主办")
    private String projSponsorUser;

    @ApiModelProperty("业务部门")
    private String bizDept;

    @ApiModelProperty("已收本金")
    private Long receivedPrincipal;

    @ApiModelProperty("已收利息")
    private Long receivedInterest;

    @ApiModelProperty("已收本金+已收利息 = 合计")
    private Long receivedSum;

    @ApiModelProperty("剩余本金")
    private Long lastPrincipal;

    @ApiModelProperty("剩余利息")
    private Long lastInterest;

    @ApiModelProperty("名义贷价")
    private Long nominalLoanPrice;

    @ApiModelProperty("剩余本金+剩余利息+名义贷价 = 合计")
    private Long lastSum;

    @ApiModelProperty("逾期未还金额")
    private Long overdueAmount;

    @ApiModelProperty("逾期利息")
    private Long overdueInterest;

    @ApiModelProperty("已收逾期利息")
    private Long receivedOverdueInterest;

    @ApiModelProperty("未收逾期利息")
    private Long uncollectedOverdueInterest;
}
