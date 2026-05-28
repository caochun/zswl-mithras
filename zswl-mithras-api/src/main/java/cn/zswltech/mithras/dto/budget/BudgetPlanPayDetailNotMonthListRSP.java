package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@Data
public class BudgetPlanPayDetailNotMonthListRSP {
    @ApiModelProperty("投放计划明细id")
    private Long id;

    @ApiModelProperty("最后一次操作人id")
    private Long lastOperateUserId;

    @ApiModelProperty("预算计划id")
    private Long budgetPlanId;

    @ApiModelProperty("投放计划id")
    private Long budgetPlanPayId;

    @ApiModelProperty("部门id")
    private Long belongDeptId;

    @ApiModelProperty("部门名称")
    private String belongDeptName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("FTP行业分类")
    private String ftpIndustryCategory;

    @ApiModelProperty("业务类型（租赁类型）")
    private String leaseType;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty(value = "地区信息")
    private String areaName;

    @ApiModelProperty("项目主办名称")
    private String sponsorUserName;

    @ApiModelProperty("项目金额")
    private Long projectAmount;

    @ApiModelProperty("首期租金率")
    private Long firstRentRate;

    @ApiModelProperty("租赁期限")
    private Integer termMonth;

    @ApiModelProperty("保证金率")
    private Integer depositRate;

    @ApiModelProperty("还款频率")
    private String repayFrequency;

    @ApiModelProperty("咨询费率")
    private Integer consultingFeeRate;

    @ApiModelProperty("还款期数")
    private Integer repayTimesTotal;

    @ApiModelProperty("手续费率")
    private Integer commissionRate;

    @ApiModelProperty("支付方式")
    private String payType;

    @ApiModelProperty("名义价款")
    private Long nominalPrice;

    @ApiModelProperty("利息计算方式")
    private String interestCalculateWay;

    @ApiModelProperty("合同利率类型")
    private String contractInterestRateType;

    @ApiModelProperty("合同利率")
    private Integer contractInterestRate;

    @ApiModelProperty("投放日")
    private LocalDate payDate;

    @ApiModelProperty("irr")
    private Integer irr;

    @ApiModelProperty("ftp")
    private Integer ftp;

    @ApiModelProperty("营业收入（不含税）")
    private Long incomeWithoutTax;

    @ApiModelProperty("营业成本")
    private Long costWithoutTax;

    @ApiModelProperty("税金及附加")
    private Long taxAndOther;

    @ApiModelProperty("风险准备金")
    private Long riskReserve;

    @ApiModelProperty("考核利润")
    private Long profit;

    @ApiModelProperty("费用")
    private Long expense;

    @ApiModelProperty("考核利润（扣费后）")
    private Long profitWithoutExpense;
}
