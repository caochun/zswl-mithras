package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/26
 * @description
 */
@Data
public class DashboardProjectPledgeRSP {
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("ZR直融，DK间融")
    private String type;
    @ApiModelProperty("业务类型")
    private String businessType;
    @ApiModelProperty("融资状态")
    private String financingStatus;
    @ApiModelProperty("融资编号")
    private String financingCode;
    @ApiModelProperty("投放金额")
    private ValueUnitDTO totalPayAmount;
    @ApiModelProperty("剩余租金金额")
    private ValueUnitDTO residualRent;
    @ApiModelProperty("剩余本金")
    private ValueUnitDTO remainingPrincipal;
    @ApiModelProperty("质押/监管情况")
    private String pledgeStatus;
    @ApiModelProperty("户名")
    private String accountName;
    @ApiModelProperty("开户行")
    private String accountBank;
    @ApiModelProperty("账号")
    private String accountNumber;



    @ApiModelProperty("融资ID")
    private Long financingId;
    @ApiModelProperty("合同ID")
    private Long contractId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("机构名称/产品名称")
    private String orgName;
//    @ApiModelProperty("融资金额")
//    private ValueUnitDTO financingAmount;
//    @ApiModelProperty("融资有效期-起")
//    private LocalDate durationFrom;
//    @ApiModelProperty("融资有效期-止")
//    private LocalDate durationTo;
//    @ApiModelProperty("是否直融，1-是，0-否")
//    private Integer isDirect;
//    @ApiModelProperty("是否被质押，1-是，0-否")
//    private Integer isPledge;
//    @ApiModelProperty("是否被监管，1-是，0-否")
//    private Integer isSupervise;
//    @ApiModelProperty("业务部门ID")
//    private Long bizDeptId;
//    @ApiModelProperty("项目主办ID")
//    private Long projSponsorUserId;
}
