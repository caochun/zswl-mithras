package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@Data
public class DashboardFundFinanceBalanceRSP {

    @ApiModelProperty("融资idKey")
    private String idKey;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("机构名称")
    private String orgName;

    @ApiModelProperty("融资类别Code")
    private String financingTypeCode;

    @ApiModelProperty("融资类别Display")
    private String financingTypeDisplay;

    @ApiModelProperty(value = "融资方式")
    private String businessType;

    @ApiModelProperty("融资金额(元)")
    private String loanAmount;

    @ApiModelProperty("期限(年)")
    private String duration;

    @ApiModelProperty("综合利率")
    private String comprehensiveInterestRate;

    @ApiModelProperty("手续费(元)")
    private String commission;

    @ApiModelProperty("借款年利率")
    private String interestRate;

    @ApiModelProperty("利率方式")
    private String interestRateWay;

    @ApiModelProperty("剩余本金(元)")
    private String remainingAmount;

    @ApiModelProperty("剩余利息(元)")
    private String remainingInterestAmount;

    @ApiModelProperty("剩余期限(年)")
    private String remainingDuration;

    @ApiModelProperty("质押资产合同编号")
    private List<String> relatedContractCodeList;
}
