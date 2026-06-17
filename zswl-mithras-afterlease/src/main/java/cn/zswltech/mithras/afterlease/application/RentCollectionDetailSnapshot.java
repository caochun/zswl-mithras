package cn.zswltech.mithras.afterlease.application;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RentCollectionDetailSnapshot {
    Long id;
    Long contractId;
    Long receiptId;
    String receiptCode;
    String code;
    Integer phase;
    String writeOffStatus;
    LocalDate collectionDate;
    Long collectionAmount;
    Long principal;
    Long interest;
    Long penaltyInterest;
    Long penaltyInterestDeductionAmount;
    LocalDate planPenaltyInterestDate;
    Long planCollectionAmount;
    LocalDate planCollectionDate;
    Long collectionPenaltyInterest;
    Long overdueCollectionCount;
    Integer emailNoticeCount;
}
