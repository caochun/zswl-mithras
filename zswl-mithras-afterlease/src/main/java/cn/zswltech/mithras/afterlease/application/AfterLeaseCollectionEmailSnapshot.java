package cn.zswltech.mithras.afterlease.application;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class AfterLeaseCollectionEmailSnapshot {
    Long id;
    Long contractId;
    String cashFlowItem;
    LocalDate planCollectionDate;
    String writeOffStatus;
}
