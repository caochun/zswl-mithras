package cn.zswltech.mithras.dashboard.mapper.model;

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
public class DashboardProjectInfoOverdueResult extends DashboardProjectBasicResult {
    private Long projReviewId;
    private Long contractId;
    private String contractCode;
    private String cashFlowCode;
    private Integer phase;
    private LocalDate planCollectionDate;
    private Long planCollectionAmount;
    private Long collectionAmount;
    private Integer defaultInterestRate;
    private Long penaltyInterestAmount;
    private Long collectionPenaltyInterest;
    private Long penaltyInterestDeductionAmount;
}
