package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class AssociationReportMainBusinessPaymentActualSnapshot {

    Long paymentId;

    LocalDate paidInDate;

    Long paidInAmount;
}
