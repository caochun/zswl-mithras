package cn.zswltech.mithras.finance.bo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/6/17
 * @description
 */
@Data
public class FinanceProjectProfitDetailBO {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 项目利润记录id
     */
    private Long projectProfitId;

    /**
     * 业务部门id
     */
    private Long bizDeptId;

    /**
     * 业务部门名称
     */
    private String bizDeptName;

    /**
     * 主办id
     */
    private Long sponsorUserId;

    /**
     * 主办名称
     */
    private String sponsorUserName;

    /**
     * 项目名称
     */
    private String projName;

    /**
     * 业务大类
     */
    private String bizType;

    /**
     * 租赁业务子类型
     */
    private String leaseType;

    /**
     * 保理业务子类型
     */
    private String factoringType;

    /**
     * 客户id
     */
    private Long clientId;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 合同id
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 投放日
     */
    private LocalDate contractStartDate;

    /**
     * 当年累计利息收入（分）
     */
    private Long totalIncomeThisYear;

    /**
     * 当年累计资金成本（分）
     */
    private Long totalCostThisYear;

    /**
     * 当年累计风险金（分）
     */
    private Long totalRiskThisYear;

    /**
     * 当年累计附加税（分）
     */
    private Long totalAdditionalTaxThisYear;

    /**
     * 当年累计印花税（分）
     */
    private Long totalStampTaxThisYear;

    /**
     * 当年累计利润总额（分）
     */
    private Long totalProfitThisYear;

    /**
     * 当月利息收入
     */
    private Long incomeThisMonth;

    /**
     * 当月资金成本
     */
    private Long costThisMonth;

    /**
     * 当月风险金
     */
    private Long riskThisMonth;

    /**
     * 当月利润
     */
    private Long profitThisMonth;

    /**
     * 考核部门id
     */
    private Long assessDeptId;

    /**
     * 本年累计毛利
     */
    private Long totalGrossProfitThisYear;

    /**
     * 本月毛利
     */
    private Long grossProfitThisMonth;

    /**
     * 本月收入
     */
    private Long  revenueThisMonth;

    /**
     * 本年累计利润总额(扣费前)
     */
    private Long totalProfitThisYearBefore;

    /**
     * 累计风险金计提/冲抵
     */
    private Long totalRiskBalanceThisYear;

    /**
     * 费用比例
     */
    private Integer expenseRadio;

    /**
     * 年初风险金余额
     */
    private Long riskBalanceBeginYear;
}
