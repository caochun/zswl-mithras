package cn.zswltech.mithras.service.mapper.model.dashboard;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/26
 * @description
 */
@Data
public class DashboardFundRepayResult {
    private Long id;
    private String idKey;
    private Integer isDirect;
    private String cashFlowWriteOffState;
    private Long financingId;
    private String financingCode;
    private String financingName;
    private Long financingAmount;
    private LocalDate repayDate;
    private Long repayAmount;
    private Long principalAmount;
    private Long interestAmount;
    private String bankAccountType;
    private String bankAccountNumber;
    private String contractCode;
    private String projName;
    private LocalDate minPayDate;
    private Long totalPayPrincipalAmount;
    private Long totalPayAmount;
    private LocalDate planCollectionRentDate;
    private Long totalPlanRentAmount;
    private Long remainingPrincipalAmount;
    private String cashFlowCode;
}
