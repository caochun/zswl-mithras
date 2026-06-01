package cn.zswltech.mithras.service.mapper.model.dashboard;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectInfoSettleInThreeMonthResult extends DashboardProjectBasicResult {
    private Long projReviewId;
    private Long contractId;
    private String contractCode;
    private Long earnestBalance;
    private Long totalPay;
    private Long totalFirstRent;
    private Long totalPlanPrincipal;
    private Long totalPlanInterest;
    private Long totalCollectionPrincipal;
    private Long totalCollectionInterest;
    private LocalDate lastRentPlanDate;
}
