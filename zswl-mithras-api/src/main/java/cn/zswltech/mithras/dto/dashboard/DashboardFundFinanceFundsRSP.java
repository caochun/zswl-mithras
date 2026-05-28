package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DashboardFundFinanceFundsRSP {


    @ApiModelProperty("融资id")
    private Long financingId;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("机构名称")
    private String orgName;

    @ApiModelProperty("融资类别Code FinancingTypeEnum")
    private String financingTypeCode;

    @ApiModelProperty("融资类别display FinancingTypeEnum")
    private String financingTypeDisplay;

    @ApiModelProperty("还款期限(月)")
    private String financingMonth;

    @ApiModelProperty("还款方式")
    private String repayWay;

    @ApiModelProperty("融资金额(元)")
    private ValueUnitDTO loanAmount;

    @ApiModelProperty("剩余金额(元)")
    private ValueUnitDTO remainingPrincipleAmount;

    @ApiModelProperty("综合资金成本")
    private ValueUnitDTO comprehensiveInterestRate;

    @ApiModelProperty("质押资产合同编号")
    private List<String> relatedContractCodeList;

    @ApiModelProperty("起息日")
    private String actualLoanDateStr;

    @ApiModelProperty("到期日")
    private String actualExpireDateStr;
}
