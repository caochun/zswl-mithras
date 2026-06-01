package cn.zswltech.mithras.service.service.bo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/6/25
 * @description
 */
@Data
public class ProfitCalculateResultBO {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 测算日期（T+1）
     */
    private LocalDate calculateDate;

    /**
     * 客户id
     */
    private Long clientId;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 客户风控行业分类
     */
    private String riskControlIndustryClassify;

    /**
     * 合同id
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 业务部门id
     */
    private Long bizDeptId;

    /**
     * 业务部门名称
     */
    private String bizDeptName;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 租赁类型
     */
    private String leaseType;

    /**
     * 保理类型
     */
    private String factoringType;

    /**
     * 转让类型
     */
    private String zrType;

    /**
     * 投放时间
     */
    private LocalDate contractStartDate;

    /**
     * 当年已确认收入（税后）
     */
    private Long confirmIncomeThisYear;

    /**
     * 当年测算利息收入
     */
    private Long calculateInterestThisYear;

    /**
     * 营业收入
     */
    private Long operatingIncome;

    /**
     * FTP成本
     */
    private Long ftpInterest;

    /**
     * 上期末风险金余额
     */
    private Long riskBalanceEndOfLastYear;

    /**
     * 本期末风险金余额
     */
    private Long riskBalanceEndOfThisYear;

    /**
     * 附加税
     */
    private Long additionalTax;

    /**
     * 利润总额
     */
    private Long profit;

    /**
     * 利润总额（扣除费用）
     */
    private Long profitExcludeFee;

    /**
     * 本年末剩余本金
     */
    private Long remainingPrincipleEndOfThisYear;

    /**
     * 本年末保证金余额
     */
    private Long remainingEarnestEndOfThisYear;
}
