package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/5/21
 * @description
 */
@Data
public class BudgetPlanProfitDetailRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("部门id")
    private Long belongDeptId;

    @ApiModelProperty("部门名称")
    private String belongDeptName;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty("项目主办名称")
    private String sponsorUserName;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("借据编号")
    private String receiptCode;

    @ApiModelProperty("业务类型（租赁类型）")
    private String leaseType;

    @ApiModelProperty("业务类型（租赁类型）-展示")
    private String leaseTypeDisplay;

    @ApiModelProperty("起租日")
    private LocalDate payDate;

    @ApiModelProperty("FTP行业分类")
    private String ftpIndustryCategory;

    @ApiModelProperty("FTP行业分类-展示")
    private String ftpIndustryCategoryDisplay;

    @ApiModelProperty("上年末/上月末业务余额")
    private Long endOfLastPeriodBalance = 0L;

    @ApiModelProperty("本年末/本月末业务余额")
    private Long endOfThisPeriodBalance = 0L;

    @ApiModelProperty("收入")
    private Long income = 0L;

    @ApiModelProperty("不含税收入")
    private Long incomeWithoutTax = 0L;

    @ApiModelProperty("成本")
    private Long cost = 0L;

    @ApiModelProperty("不含税成本")
    private Long costWithoutTax = 0L;

    @ApiModelProperty("毛利")
    private Long grossProfit = 0L;

    @ApiModelProperty("上年末/上月末风险准备金余额")
    private Long endOfLastPeriodRiskFund = 0L;

    @ApiModelProperty("本年末/本月末风险准备金余额")
    private Long endOfThisPeriodRiskFund = 0L;

    @ApiModelProperty("累计风险准备金计提/转回")
    private Long riskFundDiff = 0L;

    @ApiModelProperty("累计价差")
    private Long diff = 0L;

    @ApiModelProperty("附加税")
    private Long additionalTax = 0L;

    @ApiModelProperty("印花税")
    private Long stampTax = 0L;

    @ApiModelProperty("累计利润")
    private Long assessmentProfit = 0L;

    @ApiModelProperty("累计利润（扣费后）")
    private Long assessmentProfitWithoutExpense = 0L;

    @ApiModelProperty("累计利润（原始值）")
    private Long assessmentProfitOriginal = 0L;

    @ApiModelProperty("累计利润（扣费后）（原始值）")
    private Long assessmentProfitWithoutExpenseOriginal = 0L;

    @ApiModelProperty("项目利润调整项")
    private Long profitAdjust = 0L;

    @ApiModelProperty("备注说明")
    private String remark;
}
