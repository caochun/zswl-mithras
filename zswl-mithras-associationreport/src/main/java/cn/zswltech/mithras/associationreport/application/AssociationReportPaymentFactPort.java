package cn.zswltech.mithras.associationreport.application;

import java.time.LocalDate;

public interface AssociationReportPaymentFactPort {

    int countPaidClient(LocalDate fromDate, LocalDate toDate);
}
