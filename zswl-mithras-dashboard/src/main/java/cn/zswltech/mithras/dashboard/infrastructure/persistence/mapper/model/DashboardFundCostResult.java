package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DashboardFundCostResult {

    private Long financingId;
    private String financingTypeCode;
    private String financingCode;
    private String financingStatus;
    //综合借款利率（综合资金成本）
    private Long comprehensiveInterestRate;
    // 机构名称 项目名称
    private String orgName;
    private Long loanAmount;
    // 剩余本金
    private Long remainingPrincipleAmount;

    private String financingMonth;

    private String repayWay;
    // 起息日
    private LocalDate actualLoanDate;
    // 到期日
    private LocalDate actualExpireDate;

}
