package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class AssociationReportCollectionSnapshot {

    Long receiptId;

    Long paymentId;

    String cashFlowItem;

    LocalDate planCollectionDate;

    Long planCollectionAmount;

    Long collectionAmount;

    Long collectionPrincipal;
}
