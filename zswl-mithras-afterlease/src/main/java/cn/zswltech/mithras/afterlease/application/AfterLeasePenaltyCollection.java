package cn.zswltech.mithras.afterlease.application;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AfterLeasePenaltyCollection {
    private Long id;
    private String code;
    private String paymentCode;
    private Long penaltyInterest;
    private Long penaltyInterestDeductionAmount;
    private Long collectionPenaltyInterest;
    private LocalDate planPenaltyInterestDate;
    private Long overdueCollectionCount;
}
