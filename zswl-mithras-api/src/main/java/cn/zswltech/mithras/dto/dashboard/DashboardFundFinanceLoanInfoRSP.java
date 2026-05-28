package cn.zswltech.mithras.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@Data
public class DashboardFundFinanceLoanInfoRSP {

    @ApiModelProperty("idKey")
    private String idKey;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("融资类别code")
    private String financingTypeCode;

    @ApiModelProperty("融资类别display")
    private String financingTypeDisplay;

    @ApiModelProperty("银行/产品名称")
    private String orgName;

    @ApiModelProperty("融资金额（元）")
    private String loanAmount;

    @ApiModelProperty("融资余额（元）")
    private String balanceAmount;

    @JsonIgnore
    private Long planPrincipalAmount;

    @JsonIgnore
    private Long planInterestAmount;

    @JsonIgnore
    private Long actualPrincipalAmount;

    @JsonIgnore
    private LocalDate actualLoanDate;

    @JsonIgnore
    private LocalDate actualExpireDate;

    @ApiModelProperty("起息日")
    private String actualLoanDateStr;

    @ApiModelProperty("到期日")
    private String actualExpireDateStr;

    @ApiModelProperty("利率（%）")
    private String interestRate;

    @ApiModelProperty("综合资金成本（%）")
    private String comprehensiveFinancingCost;

    @ApiModelProperty("期限（年）")
    private String duration;

    @ApiModelProperty("还款方式")
    private String repayWay;

    @ApiModelProperty("质押资产合同编号")
    private List<String> relatedContractCodeList;
}
