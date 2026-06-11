package cn.zswltech.mithras.dashboard.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStageRepaymentResult extends DashboardProjectBasicResult {
    private String contractCode;
    private LocalDate actualLeaseDate;
    private LocalDate earliestPayDate;
    private Long totalPay;
    private Long totalPlanRent;
    private Long totalCollectionRent;
    private Long totalPlanPrincipal;
    private Long totalCollectionPrincipal;
    private Long totalPlanInterest;
    private Long totalCollectionInterest;
    private Long earnestBalance;
    private Long planCollectionFirstRent;
    private Long collectionFirstRent;
    private Integer leaseDuration;
    private Integer estimateIrr;
}
