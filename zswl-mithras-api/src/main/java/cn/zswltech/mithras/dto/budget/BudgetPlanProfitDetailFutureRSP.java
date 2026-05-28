package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/5/15
 * @description
 */
@Data
public class BudgetPlanProfitDetailFutureRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("部门id")
    private Long belongDeptId;

    @ApiModelProperty("部门名称")
    private String belongDeptName;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("FTP行业分类")
    private String ftpIndustryCategory;

    @ApiModelProperty("FTP行业分类-展示")
    private String ftpIndustryCategoryDisplay;

    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassify;

    @ApiModelProperty("风控行业分类-展示")
    private String riskControlIndustryClassifyDisplay;

    @ApiModelProperty("租赁类型")
    private String leaseType;

    @ApiModelProperty("租赁类型-展示")
    private String leaseTypeDisplay;

    @ApiModelProperty("预计投放日")
    private LocalDate planPayDate;

    @ApiModelProperty("租赁期限（月）")
    private Integer termMonth;

    @ApiModelProperty("还款周期")
    private String repayFrequency;

    @ApiModelProperty("还款周期-展示")
    private String repayFrequencyDisplay;

    @ApiModelProperty("投放额")
    private Long payAmount = 0L;

    @ApiModelProperty("保证金比例")
    private Integer depositRate = 0;

    @ApiModelProperty("合同利率")
    private Integer contractInterestRate = 0;

    @ApiModelProperty("IRR")
    private Integer irr = 0;

    @ApiModelProperty("XIRR")
    private Double xirr = 0.0;

    @ApiModelProperty("咨询费率")
    private Integer consultingFeeRate = 0;

    @ApiModelProperty("年化咨询费率")
    private Integer consultingFeeRateYear = 0;

    @ApiModelProperty("FTP")
    private Integer ftp = 0;

    @ApiModelProperty("利息收入（含税）")
    private Long interestIncome = 0L;

    @ApiModelProperty("利息收入（不含税）")
    private Long interestIncomeWithoutTax = 0L;

    @ApiModelProperty("咨询服务费收入（含税）")
    private Long consultingFeeIncome = 0L;

    @ApiModelProperty("咨询服务费收入（不含税）")
    private Long consultingFeeIncomeWithoutTax = 0L;

    @ApiModelProperty("营业收入（含税）")
    private Long income = 0L;

    @ApiModelProperty("营业收入（不含税）")
    private Long incomeWithoutTax = 0L;

    @ApiModelProperty("营业成本（不含税）")
    private Long costWithoutTax = 0L;

    @ApiModelProperty("增值税")
    private Long valueAddedTax = 0L;

    @ApiModelProperty("印花税")
    private Long stampTax = 0L;

    @ApiModelProperty("附加税")
    private Long additionalTax = 0L;

    @ApiModelProperty("当月/当年平均资金占用")
    private Long fundOccupyAverage = 0L;

    @ApiModelProperty("风险准备金")
    private Long riskFundDiff = 0L;

    @ApiModelProperty("考核利润")
    private Long assessmentProfit = 0L;

    @ApiModelProperty("本月末/本年末资产余额")
    private Long endOfThisPeriodBalance = 0L;
}
