package cn.zswltech.mithras.afterlease.application;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RentCollectionRecordSnapshot {
    Long id;
    String collectionType;
    LocalDate collectionDate;
    Long principal;
    Long interest;
    Long penaltyInterest;
}
