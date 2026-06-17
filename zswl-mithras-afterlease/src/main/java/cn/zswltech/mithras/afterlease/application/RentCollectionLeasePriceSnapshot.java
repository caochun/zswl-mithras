package cn.zswltech.mithras.afterlease.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RentCollectionLeasePriceSnapshot {
    Long contractId;
    Integer repayTimesTotal;
}
