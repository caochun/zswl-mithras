package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssociationReportMainBusinessPaymentSnapshot {

    Long id;

    Long receiptId;
}
