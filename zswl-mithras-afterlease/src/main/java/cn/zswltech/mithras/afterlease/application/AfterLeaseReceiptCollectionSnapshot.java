package cn.zswltech.mithras.afterlease.application;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class AfterLeaseReceiptCollectionSnapshot {
    Integer phase;
    LocalDate planCollectionDate;
    Long principal;
    Long interest;
    Long penaltyInterest;
    String writeOffStatus;
}
