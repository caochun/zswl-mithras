package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

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
public class DashboardProjectInfoRentThisMonthResult extends DashboardProjectBasicResult {
    private Long projReviewId;
    private Long contractId;
    private String projName;
    private String contractCode;
    private Long totalPay;
    private String cashFlowCode;
    private Integer phase;
    private LocalDate planCollectionDate;
    private LocalDate collectionDate;
    private Long planCollectionAmount;
    private Long principal;
    private Long interest;
    private Long collectionAmount;
    private Long collectionPrincipal;
    private Long collectionInterest;
}
