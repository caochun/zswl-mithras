package cn.zswltech.mithras.dto.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@Data
@ApiModel("财务管理-项目利润-详情-返回参数")
public class FinanceProjectProfitDetailRSP {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("项目利润记录id")
    private Long projectProfitId;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("主办id")
    private Long sponsorUserId;

    @ApiModelProperty("主办名称")
    private String sponsorUserName;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("业务大类")
    private String bizType;

    @ApiModelProperty("业务小类")
    private String bizSubType;

    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassify;

    @ApiModelProperty("年份")
    private Integer year;

    @ApiModelProperty("月份")
    private Integer month;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("投放日")
    private String contractStartDate;

    @ApiModelProperty("当年累计收入")
    private Long totalIncomeThisYear;

    @ApiModelProperty("当年累计资金成本")
    private Long totalCostThisYear;

    @ApiModelProperty("当年累计风险金")
    private Long totalRiskThisYear;

    @ApiModelProperty("当年累计附加税")
    private Long totalAdditionalTaxThisYear;

    @ApiModelProperty("当年累计印花税")
    private Long totalStampTaxThisYear;

    @ApiModelProperty("当年累计利润总额(扣费后)")
    private Long totalProfitThisYear;

    @ApiModelProperty("当月收入")
    private Long incomeThisMonth;

    @ApiModelProperty("当月资金成本")
    private Long costThisMonth;

    @ApiModelProperty("当月风险金")
    private Long riskThisMonth;

    @ApiModelProperty("当月利润")
    private Long profitThisMonth;

    @ApiModelProperty("当年累计毛利")
    private Long totalGrossProfitThisYear;

    @ApiModelProperty("当月毛利")
    private Long grossProfitThisMonth;

    @ApiModelProperty(value = "本月收入")
    private Long revenueThisMonth;

    @ApiModelProperty(value = "费用计提比例")
    private Integer expenseRadio;

    @ApiModelProperty("考核部门id")
    private Long assessDeptId;

    @ApiModelProperty("考核部门名称")
    private String assessDeptName;

    @ApiModelProperty("本年累计利润总额(扣费前)")
    private Long totalProfitThisYearBefore;

    @ApiModelProperty("年初风险金余额")
    private Long riskBalanceBeginYear;

    @ApiModelProperty("累计风险金计提/冲抵")
    private Long totalRiskBalanceThisYear;
}
